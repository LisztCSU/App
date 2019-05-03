package com.liszt.wesee.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.liszt.wesee.R;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.ReceivedListAdapter;
import bean.appointmentListBean;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReceivedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReceivedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceivedFragment extends Fragment {
    private View rootView;
    private static ReceivedFragment receivedFragment;
    private ListView listview;
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private String uid;
    private List<appointmentListBean> myBeanList= new ArrayList<>();
    private ReceivedListAdapter adapter;
    private static final String from[] ={"object","movie","time","operation1","operation2"};
    List<Map<String, Object>> dataList = new ArrayList<>();
    public ReceivedFragment(){}
    public static ReceivedFragment getNewInstance(){
        if (receivedFragment ==null){
            receivedFragment =new ReceivedFragment();
        }
        return receivedFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_received, container, false);
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
        listview = (ListView) getView().findViewById(R.id.list_receive);
        adapter = new ReceivedListAdapter(mContext, dataList,
                R.layout.receive_list, from,
                new int[] {R.id.txt_objectname,R.id.txt_moviename,R.id.txt_time,R.id.bt_operation1,R.id.bt_operation2});

        listview.setAdapter(adapter);

    }
    @Override
    public void onResume() {
        super.onResume();
    }



    public List<Map<String,Object>> initDataList(List<appointmentListBean> beanList){
        List<Map<String,Object>> list = new ArrayList<>();
        for(appointmentListBean bean : beanList){
            Map<String,Object> map = new HashMap<>();
            map.put(from[0],bean.getObjectname());
            map.put(from[1],bean.getMoviename());
            map.put(from[2],bean.getTime());
            map.put(from[3],bean.getId());
            map.put(from[4],bean.getId());
            list.add(map);
        }
        adapter.notifyDataSetChanged();

        return list;

    }
    class MyThread extends Thread {

        private String uid;
        public MyThread(String uid){
            this.uid = uid;
        }


        @Override
        public void run() {
            EasyHttp.get("appointment/getReceiveList").params("uid",uid).execute(new SimpleCallBack<String>() {
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

                                dataList = initDataList(myBeanList);
                            }


                        } else {
                            Toast.makeText(mContext, "获取列表失败", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }
}

