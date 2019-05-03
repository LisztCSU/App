package com.liszt.wesee.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.liszt.wesee.R;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Cookies.PersistentCookieStore;
import bean.ChatListBean;
import datahelper.DatabaseHelper;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private DatabaseHelper helper;
    private SQLiteDatabase db;
    private static final String TABLE_NAME = "record";
    private static final String from[] ={"account","time","content"};

    private  List<Map<String, Object>> dataList = new ArrayList<>();
    private List<ChatListBean> myBeanList = new ArrayList<>();
    private ListView list_chat;
    SharedPreferences sharedPreferences;
    private SimpleAdapter adapter;
    private EditText msg;
    private Button sendMsg;
    private String chatId;
    private String account;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        chatId = intent.getStringExtra("chatId");
        account = intent.getStringExtra("account");
        sharedPreferences = getSharedPreferences("Cookies_Prefs",MODE_PRIVATE);
        uid = sharedPreferences.getString("uid","0");
        helper = new DatabaseHelper(this, "chat_db", null, 1);//dbName数据库名
        db = helper.getWritableDatabase();
        msg = (EditText) findViewById(R.id.txt_chat);
        sendMsg =(Button) findViewById(R.id.bt_sendMsg);
        list_chat = (ListView) findViewById(R.id.id_list_chat);
        adapter = new SimpleAdapter(ChatActivity.this, dataList,
                R.layout.chat_list, from,
                new int[] {R.id.user_chat,R.id.time_chat,R.id.chat_content});
        list_chat.setAdapter(adapter);
        list_chat.setDivider(null);
        initDataList();
        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
                String time  = dateFormat.format( now );
                Map<String,Object> map = new HashMap<>();
                map.put(from[0],account);
                map.put(from[1],time);
                map.put(from[2],msg.getText().toString());
                dataList.add(map);
                adapter.notifyDataSetChanged();
                list_chat.setSelection(adapter.getCount()-1);
                new MyThread( uid,chatId,msg.getText().toString(),time,account,ChatActivity.this,db).start();
                msg.setText("");


            }
        });
        EventBus.getDefault().register(this);
    }
private void initDataList(){
    myBeanList.clear();
    dataList.clear();
    Cursor cursor = db.query(TABLE_NAME, new String[]{"id,account,time,content"}, "id=?", new String[]{chatId}, null, null, null,null);
    while (cursor.moveToNext()) {
        myBeanList.add(new ChatListBean(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)));
    }
    Collections.sort(myBeanList);
        for (ChatListBean bean : myBeanList) {
            Map<String, Object> map = new HashMap<>();
            map.put(from[0], bean.getAccount());
            map.put(from[1], bean.getTime());
            map.put(from[2], bean.getContent());
            dataList.add(map);
        }
        adapter.notifyDataSetChanged();
}
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onReceivePushMessage(Pair<String, String> pair) {
       String arr[] = pair.first.split("&&");
       if(arr[1].equals(chatId)) {
           Map<String, Object> map = new HashMap<>();
           map.put(from[0], arr[0]);
           map.put(from[1], arr[2]);
           map.put(from[2], pair.second);
           dataList.add(map);
           adapter.notifyDataSetChanged();
           list_chat.setSelection(adapter.getCount() - 1);
       }//判断是否同一个会话
        ContentValues values = new ContentValues();
        values.put("id",arr[1]);
        values.put("account",arr[0]);
        values.put("time",arr[2]);
        values.put("content",pair.second);
        //数据库执行插入命令
        db.insert(TABLE_NAME, null, values);

    }
    static class MyThread extends Thread{
        private String uid;
        private String id;
        private String msg;
        private String time;
        private String  account;
        private Context context;
        private SQLiteDatabase db;
        public MyThread( String uid,String id,String msg,String time,String account, Context context, SQLiteDatabase db){
            this.uid = uid;
            this.id =id;
            this.msg = msg;
            this.time = time;
            this.account = account;
            this.context = context;
            this.db = db;
        }
        public void run(){
            EasyHttp.get("msg/sendMsg").params("uid",uid).params("id",id).params("msg",msg).params("time",time).execute(new SimpleCallBack<String>() {
                @Override
                public void onError(ApiException e) {
                    Toast.makeText(context, "请求失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.optInt("code");
                        if(code == 1){
                            ContentValues values = new ContentValues();
                            values.put("id",id);
                            values.put("account",account);
                            values.put("time",time);
                            values.put("content",msg);
                            //数据库执行插入命令
                            db.insert(TABLE_NAME, null, values);

                        }
                        else if (code == -1) {
                            Toast.makeText(context, "未登录", Toast.LENGTH_LONG).show();
                            PersistentCookieStore cookieStore = new PersistentCookieStore(context.getApplicationContext());
                            cookieStore.removeAll();
                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);
                            ((Activity)context).finish();
                        } else if (code == 0) {
                            Toast.makeText(context, "发送失败", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }
}
