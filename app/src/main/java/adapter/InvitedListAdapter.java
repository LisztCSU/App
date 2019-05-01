package adapter;

import android.content.Context;
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

public class InvitedListAdapter extends SimpleAdapter {
    private Context mcontext = null;

    public InvitedListAdapter(Context context,
                              List<? extends Map<String, ?>> data, int resource,
                              String[] from, int[] to) {
        super(context, data, resource, from, to);
        // TODO Auto-generated constructor stub
        mcontext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = super.getView(position, convertView, parent);
        HashMap<String, Object> map = (HashMap<String, Object>) getItem(position);
        Button accept = (Button) view.findViewById(R.id.bt_operation);
        accept.setText("取消");

        String str = map.get("operation").toString();
        accept.setTag(str);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyThread(v.getTag().toString()).start();
            }
        });


        return view;
    }

    class MyThread extends Thread {
        private String id;

        public MyThread(String id) {
            this.id = id;
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
                        } else {
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
