package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.liszt.wesee.R;
import com.liszt.wesee.activity.LoginActivity;
import com.liszt.wesee.fragment.InvitedFragment;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvitedListAdapter extends SimpleAdapter {
    private Context mcontext = null;
    List<? extends Map<String, ?>> dataList;

    public InvitedListAdapter(Context context,
                              List<? extends Map<String, ?>> data, int resource,
                              String[] from, int[] to) {
        super(context, data, resource, from, to);
        // TODO Auto-generated constructor stub
        mcontext = context;
        dataList = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = super.getView(position, convertView, parent);
        HashMap<String, Object> map = (HashMap<String, Object>) getItem(position);
        Button cancel= (Button) view.findViewById(R.id.bt_operation);
        cancel.setText("取消");

        String str = map.get("operation").toString();
        cancel.setTag(str);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyThread(v.getTag().toString(),mcontext).start();
                dataList.remove(position);
                InvitedListAdapter.this.notifyDataSetChanged();
            }
        });


        return view;
    }

  static   class MyThread extends Thread {
        private String id;
        private Context mcontext;
        public MyThread(String id,Context mcontext) {
            this.id = id;
            this.mcontext = mcontext;

        }

        @Override
        public void run() {
            EasyHttp.get("appointment/cancel").params("id", id).execute(new SimpleCallBack<String>() {
                @Override
                public void onError(ApiException e) {
                    Toast.makeText(mcontext, "操作失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.optInt("code");
                        if (code == 1) {
                            Toast.makeText(mcontext, "取消成功", Toast.LENGTH_LONG).show();
                        }
                        else if(code == -1){
                            Toast.makeText(mcontext, "未登录", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(mcontext, LoginActivity.class);
                            mcontext.startActivity(intent);
                        }else {
                            Toast.makeText(mcontext, "操作失败", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        }

    }
}
