package com.liszt.wesee.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.liszt.wesee.R;
import com.liszt.wesee.activity.ChatActivity;
import com.liszt.wesee.activity.LoginActivity;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import CustomizedControl.SwipeListLayout;
import bean.appointmentListBean;

public class AppointmentFragment extends Fragment {
    private View rootView;
    private static AppointmentFragment appointmentFragment;
    private ListView listview;
    private TextView empty;
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private String uid;
    private List<appointmentListBean> myBeanList= new ArrayList<>();
    private Set<SwipeListLayout> sets = new HashSet();
    private ListAdapter adapter;
    private static final String from[] ={"name","movie","time","id","intiative","name2"};
    List<Map<String, Object>> dataList = new ArrayList<>();
    public AppointmentFragment(){}
    public static AppointmentFragment getNewInstance(){
        if (appointmentFragment ==null){
            appointmentFragment =new AppointmentFragment();
        }
        return appointmentFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_appointment, container, false);
        }
        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }

        return rootView;
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // The size for this PublisherAdView is defined in the XML layout as AdSize.FLUID. It could
        // also be set here by calling publisherAdView.setAdSizes(AdSize.FLUID).
        //
        // An ad with fluid size will automatically stretch or shrink to fit the height of its
        // content, which can help layout designers cut down on excess whitespace.
        sharedPreferences = mContext.getSharedPreferences("Cookies_Prefs",mContext.MODE_PRIVATE);
        uid = sharedPreferences.getString("uid","0");
        new MyThread(uid).start();
        listview = (ListView) getView().findViewById(R.id.list_appointment);
        empty = (TextView) getView().findViewById(R.id.id_emptyList);
        adapter = new ListAdapter();
        listview.setAdapter(adapter);


    }
    @Override
    public void onResume() {
        super.onResume();
    }



    public void initDataList(){
        dataList.clear();
        for(appointmentListBean bean : myBeanList){
            Map<String,Object> map = new HashMap<>();
            map.put(from[0],bean.getObjectname().split("&&")[0]);
            map.put(from[5],bean.getObjectname().split("&&")[1]);
            map.put(from[1],bean.getMoviename());
            map.put(from[2],bean.getTime());
            map.put(from[3],bean.getId());
            map.put(from[4],bean.getInitiative());

            dataList.add(map);
        }


    }
    class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int arg0) {
            return dataList.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int arg0, View view, ViewGroup arg2) {
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(
                        R.layout.appiontment_list, null);
            }
            LinearLayout linearLayout =  view.findViewById(R.id.ll_view);
            final HashMap<String, Object> map = (HashMap<String, Object>) dataList.get(arg0);
            TextView names = (TextView) view.findViewById(R.id.txt_objectname);
            TextView moviesname = (TextView) view.findViewById(R.id.txt_moviename);

            TextView time = (TextView) view.findViewById(R.id.txt_time);
            if(map.get(from[4]).toString().equals("1")) {
                names.setText("我邀请"+map.get(from[5]).toString());
            }
            else {
                names.setText(map.get(from[0]).toString()+"邀请我");
            }
            moviesname.setText(map.get(from[1]).toString());
            time.setText(map.get(from[2]).toString());
            final SwipeListLayout sll_main = (SwipeListLayout) view
                    .findViewById(R.id.sll_main);
            TextView complete = (TextView) view.findViewById(R.id.bt_complete);
            sll_main.setOnSwipeStatusListener(new MyOnSlipStatusListener(
                    sll_main));
            complete.setTag(map.get(from[3]).toString());
            complete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    sll_main.setStatus(SwipeListLayout.Status.Close, true);
                    dataList.remove(arg0);
                    notifyDataSetChanged();
                    new MyThread2(uid,view.getTag().toString()).start();
                }
            });
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, Object> map = (HashMap<String, Object>) dataList.get(arg0);
                    Intent intent =  new Intent(getContext(), ChatActivity.class);
                    if(map.get(from[4]).toString().equals("1")){
                        intent.putExtra("myname",map.get(from[0]).toString());
                        intent.putExtra("objectname",map.get(from[5]).toString().split("@")[0]);
                    }else {
                        intent.putExtra("myname",map.get(from[5]).toString());
                        intent.putExtra("objectname",map.get(from[0]).toString().split("@")[0]);

                    }
                    intent.putExtra("chatId",map.get(from[3]).toString());

                    getContext().startActivity(intent);
                }
            });
            return view;
        }

    }
    class MyOnSlipStatusListener implements SwipeListLayout.OnSwipeStatusListener {

        private SwipeListLayout slipListLayout;

        public MyOnSlipStatusListener(SwipeListLayout slipListLayout) {
            this.slipListLayout = slipListLayout;
        }

        @Override
        public void onStatusChanged(SwipeListLayout.Status status) {
            if (status == SwipeListLayout.Status.Open) {
                //若有其他的item的状态为Open，则Close，然后移除
                if (sets.size() > 0) {
                    for (SwipeListLayout s : sets) {
                        s.setStatus(SwipeListLayout.Status.Close, true);
                        sets.remove(s);
                    }
                }
                sets.add(slipListLayout);
            } else {
                if (sets.contains(slipListLayout))
                    sets.remove(slipListLayout);
            }
        }

        @Override
        public void onStartCloseAnimation() {

        }

        @Override
        public void onStartOpenAnimation() {

        }

    }
    class MyThread extends Thread {

     private String uid;
     public MyThread(String uid){
    this.uid = uid;
}


    @Override
    public void run() {
        EasyHttp.get("appointment/getAppointmentList").params("uid",uid).execute(new SimpleCallBack<String>() {
            @Override
            public void onError(ApiException e) {
                Toast.makeText(mContext, "请求失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    int code = obj.optInt("code");
                    if (code == 1) {
                        myBeanList.clear();
                        JSONArray dataObj = obj.getJSONArray("dataList");
                        if (dataObj != null) {
                            int size = dataObj.length();


                            for(int i = 0;i<size;i++){
                                JSONObject json = (JSONObject) dataObj.getJSONObject(i);
                                myBeanList.add(new appointmentListBean(
                                        json.getString("id"),
                                        json.getString("objectname"),
                                        json.getString("moviename"),
                                        json.getString("time"),
                                        json.getString("initiative")));

                            }

                            initDataList();
                            adapter.notifyDataSetChanged();
                            empty.setVisibility(View.GONE);
                        }


                    }
                    else if(code == -1){
                        Toast.makeText(mContext, "未登录", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                    }else {

                        dataList.clear();
                        adapter.notifyDataSetChanged();
                        empty.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
    class MyThread2 extends Thread {

        private String uid;
        private String id;
        public MyThread2(String uid,String id){
            this.uid = uid;
            this.id = id;
        }


        @Override
        public void run() {
            EasyHttp.get("appointment/cancel").params("uid",uid).params("id", id).execute(new SimpleCallBack<String>() {
                @Override
                public void onError(ApiException e) {
                    Toast.makeText(getContext(), "操作失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.optInt("code");
                        if (code == 1) {
                            Toast.makeText(getContext(), "约看已结束", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "操作失败", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        }

    }

}
