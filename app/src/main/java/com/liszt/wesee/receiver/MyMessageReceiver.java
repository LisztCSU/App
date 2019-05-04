package com.liszt.wesee.receiver;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.liszt.wesee.activity.AppointmentActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import datahelper.DatabaseHelper;

public class MyMessageReceiver extends MessageReceiver {
    // 消息接收部分的LOG_TAG
    public static final String REC_TAG = "receiver";

    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        // TODO 处理推送通知
        Log.e("MyMessageReceiver", "Receive notification, title: " + title + ", summary: " + summary + ", extraMap: " + extraMap);
    }
    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {
       DatabaseHelper helper = new DatabaseHelper(context, "chat_db", null, 1);
       SQLiteDatabase db = helper.getWritableDatabase();
        Log.e("MyMessageReceiver", "onMessage, messageId: " + cPushMessage.getMessageId() + ", title: " + cPushMessage.getTitle() + ", content:" + cPushMessage.getContent());
        String arr[] = cPushMessage.getTitle().split("&&");
        ContentValues values = new ContentValues();
        values.put("id",arr[1]);
        values.put("account",arr[0]);
        values.put("time",arr[2]);
        values.put("content",cPushMessage.getContent());
        //数据库执行插入命令
        db.insert("record", null, values);
        //传递数据
        EventBus.getDefault().postSticky(new Pair<String, String>(cPushMessage.getTitle(), cPushMessage.getContent()));
    }
    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        Log.e("MyMessageReceiver", "onNotificationOpened, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
        Intent intent = new Intent(context,AppointmentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(title.equals("新的邀请")){
            intent.putExtra("fragmentId","1");
        }
        context.startActivity(intent);

    }
    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        Log.e("MyMessageReceiver", "onNotificationClickedWithNoAction, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }
    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        Log.e("MyMessageReceiver", "onNotificationReceivedInApp, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap + ", openType:" + openType + ", openActivity:" + openActivity + ", openUrl:" + openUrl);
    }
    @Override
    protected void onNotificationRemoved(Context context, String messageId) {
        Log.e("MyMessageReceiver", "onNotificationRemoved");
    }
}