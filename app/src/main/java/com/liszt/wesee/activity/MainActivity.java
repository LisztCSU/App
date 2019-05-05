package com.liszt.wesee.activity;



import android.content.Context;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.widget.RadioGroup;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.liszt.wesee.fragment.MineFragment;
import com.liszt.wesee.fragment.MineLoginFragment;
import com.liszt.wesee.fragment.HomeFragment;
import com.liszt.wesee.R;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    SharedPreferences sharedPreferences;
    private RadioGroup radioGroup;
    private FragmentTransaction fragmentTransaction;
    Fragment homeFragment,mineFragment,mineLoginFragment;
    public static final int VIEW_HOME_INDEX = 0;
    public static final int VIEW_MINE_INDEX = 1;
    private int temp_position_index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("Cookies_Prefs",MODE_PRIVATE);
        String token=sharedPreferences.getString("token","0");
        String uid = sharedPreferences.getString("uid","0");
        if(!token.equals("0")&&!uid.equals("0")) {
              autoLogin(uid,token,MainActivity.this);
        }


        initView();
        findViewById(R.id.id_bt_home).setOnClickListener(this);
        findViewById(R.id.id_bt_mine).setOnClickListener(this);
    }
    private void initView() {
       radioGroup = (RadioGroup) findViewById(R.id.id_navcontent);
        homeFragment = HomeFragment.getNewInstance();
        mineFragment = MineFragment.getNewInstance();
        mineLoginFragment = MineLoginFragment.getNewInstance();
        //显示
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.id_fragment_content, homeFragment);
        fragmentTransaction.commit();


    }
    public void switchView(View view) {
        findViewById(R.id.home_img).setBackgroundResource(R.mipmap.ic_home);
        findViewById(R.id.min_img).setBackgroundResource(R.mipmap.ic_mine);
        ((TextView)findViewById(R.id.home_tv)).setTextColor(Color.parseColor("#707070"));
        ((TextView)findViewById(R.id.min_tv)).setTextColor(Color.parseColor("#707070"));
        switch (view.getId()) {
            case R.id.id_bt_home:
                if (temp_position_index != VIEW_HOME_INDEX) {
                    //显示
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.id_fragment_content, homeFragment);
                    fragmentTransaction.commit();
                }
                temp_position_index = VIEW_HOME_INDEX;
                findViewById(R.id.home_img).setBackgroundResource(R.mipmap.ic_home_on);
                ((TextView)findViewById(R.id.home_tv)).setTextColor(Color.parseColor("#00a628"));
                break;
            case R.id.id_bt_mine:
                if (temp_position_index != VIEW_MINE_INDEX) {
                    //显示
                    if (sharedPreferences.getString("uid","0").length()>1) {
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.id_fragment_content, mineFragment);
                        fragmentTransaction.commit();

                    }
                    else {
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.id_fragment_content, mineLoginFragment);
                        fragmentTransaction.commit();

                    }
                }
                temp_position_index = VIEW_MINE_INDEX;
                findViewById(R.id.min_img).setBackgroundResource(R.mipmap.ic_mine_on);
                ((TextView)findViewById(R.id.min_tv)).setTextColor(Color.parseColor("#00a628"));
                break;

        }
    }
public void autoLogin(String uid,String token,Context context){

        new MyThread(uid,token,context).start();
}
static class MyThread extends Thread{
        private String uid;
        private String token;
        private Context context;
       public MyThread(String uid,String token,Context context){
           this.uid = uid;
           this.token = token;
           this.context = context;
       }
       @Override
    public void run(){
           EasyHttp.get("user/autoLogin").params("uid",uid).params("token",token).execute(new SimpleCallBack<String>() {
               @Override
               public void onError(ApiException e) {
                   Toast.makeText(context, "自动登录失败", Toast.LENGTH_LONG).show();
               }

               @Override
               public void onSuccess(String result) {
                   try {
                       JSONObject obj =  new JSONObject(result);
                       int code = obj.optInt("code");
                       if (code == 1) {
                           JSONObject dataObj = obj.optJSONObject("data");
                           if (dataObj != null) {
                               String id = dataObj.optString("id", "");
                               String username = dataObj.optString("username","");
                               String token = dataObj.optString("token","");
                               SharedPreferences sharedPreferences = context.getSharedPreferences("Cookies_Prefs",MODE_PRIVATE);
                               SharedPreferences.Editor editor = sharedPreferences.edit();
                               editor.putString("uid",id);
                               editor.putString("username",username);
                               editor.putString("token",token);
                               editor.apply();
                           }

                       } else if(code==-1) {
                           Toast.makeText(context, "已在其他设备登录", Toast.LENGTH_LONG).show();
                       }
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
           });

       }
}


//    @Override
//    protected void onResume() {
//        super.onResume();
//        Intent intent = getIntent();
//        startActivityAfterLogin(intent);
//    }

//
//    public void startActivityAfterLogin(Intent intent) {
//        if (intent.getExtras() != null && getIntent().getExtras().getString("isAuth") != null) {
//            String isAuth = intent.getStringExtra("isAuth");
//            //未登录（这里用自己的登录逻辑去判断是否未登录）
//            if ("1".equals(isAuth)) {
//                Toast.makeText(MainActivity.this, "哈哈哈", Toast.LENGTH_LONG).show();
//            }
//            else{
//                ComponentName componentName = new ComponentName(this, LoginActivity.class);
//                Intent intent2 = new Intent();
//                intent2.putExtra("classNameMain", intent.getComponent().getClassName());
//                intent2.setComponent(componentName);
//                intent2.setAction("maingotologin");
//                super.startActivity(intent2);
//                super.startActivity(intent);
//            }
//        }
//        else {
//            ComponentName componentName = new ComponentName(this, LoginActivity.class);
//            Intent intent2 = new Intent();
//            intent2.putExtra("classNameMain", intent.getComponent().getClassName());
//            intent2.setComponent(componentName);
//            intent2.setAction("maingotologin");
//            super.startActivity(intent2);
//        }
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switchView(v);
    }

}



