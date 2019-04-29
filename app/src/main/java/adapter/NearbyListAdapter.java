package adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.SimpleAdapter;
import android.widget.Toast;


import com.liszt.wesee.R;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearbyListAdapter extends SimpleAdapter {
    private Context mcontext = null;
    private SharedPreferences sharedPreferences;


    public NearbyListAdapter(Context context,
                            List<? extends Map<String, ?>> data, int resource,
                            String[] from, int[] to) {
        super(context, data, resource, from, to);
        // TODO Auto-generated constructor stub
        mcontext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
         View view =super.getView(position, convertView, parent);
        HashMap<String, Object> map = (HashMap<String, Object>) getItem(position);
        Button invite = (Button) view.findViewById(R.id.bt_invite);
        invite.setText("邀请");
        sharedPreferences = mcontext.getSharedPreferences("Cookies_Prefs",mcontext.MODE_PRIVATE);
        if (sharedPreferences.getString("uid", "0").equals("0")) {
           invite.setEnabled(false);
        }
        invite.setEnabled(true);
        final String  uid = sharedPreferences.getString("uid", "0");
        String str = map.get("invite").toString();
        invite.setTag(str);

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str[] = v.getTag().toString().split("@");
                     new MyThread(uid,str[0],str[1]).start();
            }
        });




        return view;
    }
    class MyThread extends Thread {
        private String uid;
        private String uid2;
        private String mid;

        public MyThread(String uid,String uid2,String mid) {
            this.uid = uid;
            this.uid2=uid2;
            this.mid = mid;
        }

        @Override
        public void run() {
            EasyHttp.get("appointment/invite").params("uid", uid).params("uid2",uid2).params("mid", mid).execute(new SimpleCallBack<String>() {
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
                            Toast.makeText(mcontext, "成功邀请该用户", Toast.LENGTH_LONG).show();
                        } else if (code == 0) {
                            Toast.makeText(mcontext, "你已邀请过该用户", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mcontext, "未登录", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        }
    }
}
