package com.liszt.wesee.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
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
import java.util.zip.Inflater;

import Cookies.PersistentCookieStore;
import adapter.NearbyListAdapter;
import bean.nearbyListBean;

public class MapActivity extends AppCompatActivity {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap =null;
    private ListView listView = null;
    private TextView empty = null;
    private Button refresh = null;
    private LocationClient mLocationClient;
    private boolean isFirstLocation;
    private NearbyListAdapter adapter;
    private Context mcontext = null;
    private static final String from[] ={"info","distance","invite"};
    private List<nearbyListBean> myBeanList = new ArrayList<>();
    List<Map<String, Object>> dataList = new ArrayList<>();
   
   SharedPreferences sharedPreferences;
   String uid;
   String mid;
   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("Cookies_Prefs",MODE_PRIVATE);
        uid = sharedPreferences.getString("uid","0");
        Intent intent = getIntent();
        mid = intent.getStringExtra("mid");
        mcontext = MapActivity.this;


        setContentView(R.layout.activity_map);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);


        List<String> permissionList=new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.
                permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.
                permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.
                permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this,permissions,1);
        }else {
            mBaiduMap.setMyLocationEnabled(true);
            mLocationClient = new LocationClient(getApplicationContext());
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true); // 打开gps
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setScanSpan(1000);
            mLocationClient.setLocOption(option);
            isFirstLocation = true;
            MyLocationListener myLocationListener = new MyLocationListener();
            mLocationClient.registerLocationListener(myLocationListener);
            mLocationClient.start();
        }
        refresh = (Button) findViewById(R.id.bt_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyThread2(uid,mid,myBeanList,dataList,mcontext,empty,adapter,mBaiduMap).start();


            }
        });
        listView = (ListView) findViewById(R.id.user_nearby);
        empty = (TextView) findViewById(R.id.id_emptyList);
        adapter = new NearbyListAdapter(MapActivity.this, dataList,
                R.layout.nearby_list, from,
                new int[] {R.id.user_info,R.id.user_distance,R.id.bt_invite});
        listView.setAdapter(adapter);

        }



    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时必须调用mMapView. onResume ()
        mMapView.onResume();

    }
    @Override
    protected void onPause() {

        //在activity执行onPause时必须调用mMapView. onPause ()
        mMapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时必须调用mMapView.onDestroy()
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
    }
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null){
                return;
            }
            if (isFirstLocation) {
                MapStatusUpdate update = null;
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                Log.e("lyl", "update:" + latLng);

                update = MapStatusUpdateFactory.zoomTo(16f);
                mBaiduMap.animateMapStatus(update);

                update = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(update);

//            update = MapStatusUpdateFactory.zoomTo(16f);
//            baiduMap.animateMapStatus(update);
                isFirstLocation = false;
            }

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
            Log.e("lyl", "update:" + latLng1);
            mBaiduMap.setMyLocationData(locData);

            new MapActivity.MyThread(uid, location.getLongitude(),location.getLatitude(),mcontext).start();

        }
    }
  static class MyThread extends Thread {
        private String uid;
        private double longitude;
        private double latitude;
        private Context mcontext;
        private SimpleAdapter adapter;

        public MyThread(String uid, double longitude, double latitude,Context mcontext ) {
            this.uid = uid;
            this.longitude = longitude;
            this.latitude = latitude;
            this.mcontext = mcontext;

        }

        @Override
        public void run() {
            EasyHttp.get("location/add").params("uid",uid).params("longitude",longitude+"").params("latitude",latitude+"").execute(new SimpleCallBack<String>() {
                @Override
                public void onError(ApiException e) {
                    Toast.makeText(mcontext, "请求失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.optInt("code");
                        if (code == -1) {
                            Toast.makeText(mcontext, "未登录", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(mcontext, LoginActivity.class);
                            mcontext.startActivity(intent);
                        } else if (code == 0) {

                            Toast.makeText(mcontext, "更新位置失败", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

static class MyThread2 extends Thread{
        private  String uid;
        private  String mid;
    private List<nearbyListBean> myBeanList ;
    private List<Map<String, Object>> dataList;
    private Context mcontext;
    private TextView empty;
    private SimpleAdapter adapter;
    private BaiduMap mBaiduMap;
       public MyThread2(String uid,String mid,List<nearbyListBean> myBeanList, List<Map<String, Object>> dataList,Context mcontext,TextView empty,SimpleAdapter adapter,  BaiduMap mBaiduMap){
           this.uid = uid;
           this.mid = mid;
           this.dataList = dataList;
           this.myBeanList = myBeanList;
           this.mcontext = mcontext;
           this.empty = empty;
           this.adapter = adapter;
           this.mBaiduMap = mBaiduMap;
       }
       @Override
      public  void run(){
           EasyHttp.get("nearby/getNearbyList").params("uid",uid).params("mid",mid).execute(new SimpleCallBack<String>() {
               @Override
               public void onError(ApiException e) {
                   Toast.makeText(mcontext, "请求失败", Toast.LENGTH_LONG).show();
               }

               @Override
               public void onSuccess(String result) {
                   try {
                       JSONObject obj = new JSONObject(result);
                       int code = obj.optInt("code");
                       if (code == 1) {
                           Toast.makeText(mcontext, "刷新列表成功", Toast.LENGTH_LONG).show();
                           myBeanList.clear();
                           dataList.clear();
                           JSONArray dataObj = obj.getJSONArray("dataList");
                           if (dataObj != null) {
                               int size = dataObj.length();

                               for(int i = 0;i<size;i++){
                                   JSONObject json = (JSONObject) dataObj.getJSONObject(i);

                                   myBeanList.add(new nearbyListBean(json.getString("uid2"),
                                                                     json.getString("username"),
                                                                     json.getString("nickname"),
                                                                     Double.parseDouble(json.getString("longitude")),
                                                                     Double.parseDouble(json.getString("latitude")),
                                                                     json.getString("distance"),
                                                                     mid));

                               }
                               for(nearbyListBean bean:myBeanList) {
                                   LatLng latLng = new LatLng(bean.getLatitude(),bean.getLongitude());
                                   View view = LayoutInflater.from(mcontext).inflate(R.layout.marker, null);
                                   TextView textView = (TextView) view.findViewById(R.id.marker_nickname);
                                   textView.setText(bean.getNickname());

                                   mBaiduMap.addOverlay(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromView(view)));
                               }
                               for(nearbyListBean bean : myBeanList){
                                   Map<String,Object> map = new HashMap<>();

                                   map.put(from[0],bean.getNickname()+"@"+bean.getUsername());
                                   map.put(from[1],"离你"+bean.getDistance()+"公里");
                                   map.put(from[2],bean.getUid2()+"@"+bean.getMid());

                                   dataList.add(map);
                               }
                               adapter.notifyDataSetChanged();
                                empty.setVisibility(View.GONE);
                           }


                       } else {
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
    
}
