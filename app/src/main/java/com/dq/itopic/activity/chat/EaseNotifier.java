/************************************************************
 *  * EaseMob CONFIDENTIAL 
 * __________________ 
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved. 
 *  
 * NOTICE: All information contained herein is, and remains 
 * the property of EaseMob Technologies.
 * Dissemination of this information or reproduction of this material 
 * is strictly forbidden unless prior written permission is obtained
 * from EaseMob Technologies.
 */
package com.dq.itopic.activity.chat;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Vibrator;
import androidx.core.app.NotificationCompat;

import com.dq.itopic.R;
import com.dq.itopic.activity.common.MainActivity;
import com.dq.itopic.bean.ChatBean;
import com.dq.itopic.bean.UserBean;
import com.dq.itopic.manager.ITopicApplication;
import com.dq.itopic.tools.LogUtil;

import java.util.HashMap;
import java.util.List;

/**
 * 新消息提醒class
 */
public class EaseNotifier {

    private static int notifyID = 0525; // start notification id
    private static int foregroundNotifyID = 0555;

    private NotificationManager notificationManager;

    private ITopicApplication mApp;
    private long lastNotifiyTime;

    /**
     * 开发者可以重载此函数
     * @param mApp
     * @return
     */
    public EaseNotifier (ITopicApplication mApp){
        this.mApp = mApp;
        notificationManager = (NotificationManager) mApp.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void cancelNotificaton() {
        if (notificationManager != null)
            notificationManager.cancel(notifyID);
    }

    public void cancelAllNotificaton() {
        if (notificationManager != null)
            notificationManager.cancelAll();
    }

    /**
     * 处理新收到的消息，然后发送通知
     * 
     * 开发者可以重载此函数
     * this function can be override
     */
    public synchronized void ringViberate(HashMap<String,String> payloadMessage) {
        // 判断app是否在后台
        if (isAppRunningForeground(mApp)) {
            LogUtil.e("前台");
            viberateAndPlayTone();
//            sendNotification(payloadMessage, true);
        } else {
            LogUtil.e("后台");
            viberateAndPlayTone();
            sendNotification(payloadMessage, false);
        }
    }

    /**
     * 发送通知栏提示
     * This can be override by subclass to provide customer implementation
     */
    protected void sendNotification(HashMap<String,String> payloadMessage, boolean isForeground) {
        try {

            PackageManager packageManager = mApp.getPackageManager();
            String appname = (String) packageManager.getApplicationLabel(mApp.getApplicationInfo());
            
            // notification titile
            String contentTitle = appname;

            // create and send notificaiton
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mApp)
            															.setSmallIcon(R.drawable.ic_launcher_tiny)
                                                                        .setWhen(System.currentTimeMillis())
                                                                        .setAutoCancel(true);

//            Intent msgIntent = mApp.getPackageManager().getLaunchIntentForPackage(packageName);

            Intent intent = new Intent(mApp, MainActivity.class);
//            PayloadData payloadData = payloadMessage.getPayloadData();
            intent.putExtra("swip", "1");

            PendingIntent pendingIntent = PendingIntent.getActivity(mApp, notifyID, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentTitle(contentTitle);
            mBuilder.setTicker(payloadMessage.get("title"));
            mBuilder.setContentText(payloadMessage.get("body"));
            mBuilder.setContentIntent(pendingIntent);
            // mBuilder.setNumber(notificationNum);
            Notification notification = mBuilder.build();

            if (isForeground) {
                notificationManager.notify(foregroundNotifyID, notification);
                notificationManager.cancel(foregroundNotifyID);
            } else {
                notificationManager.notify(notifyID, notification);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 手机震动和声音提示
     */
    public void viberateAndPlayTone() {
        UserBean userBean = mApp.getMyUserBeanManager().getInstance();
        if(userBean != null && userBean.getSlience() == 1){
            //静音模式
            return;
        }

        if (System.currentTimeMillis() - lastNotifiyTime < 1000) {
            return;
        }

        try {
            lastNotifiyTime = System.currentTimeMillis();

            MediaPlayer player = MediaPlayer.create(mApp, RingtoneManager
                    .getActualDefaultRingtoneUri(mApp, RingtoneManager.TYPE_NOTIFICATION));
            player.setLooping(false);
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isAppRunningForeground(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        try {
            List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
            if (tasks == null || tasks.size() < 1) {
                return false;
            }
            boolean b = ctx.getPackageName().equalsIgnoreCase(tasks.get(0).baseActivity.getPackageName());
            return b;
        } catch (SecurityException e) {
            LogUtil.e("Apk doesn't hold GET_TASKS permission");
            e.printStackTrace();
        }
        return false;
    }
}
