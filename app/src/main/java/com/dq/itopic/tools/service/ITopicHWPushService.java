
package com.dq.itopic.tools.service;

import com.dq.itopic.manager.ITopicApplication;
import com.dq.itopic.tools.LogUtil;
import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

public class ITopicHWPushService extends HmsMessageService {
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        LogUtil.e("ITopicHWTokenService = "+token);
        ITopicApplication application = (ITopicApplication)getApplication();
        application.getMyUserBeanManager().setHwPushToken(token);
    }

    //dq 2019-11-02 这里只能
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().length() > 0) {
            LogUtil.e("华为 Message data payload: " + remoteMessage.getData());
            ITopicApplication application = (ITopicApplication)getApplication();
            if (application.getChatManager() != null)
                application.getChatManager().handleThirdPartyPush(remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            LogUtil.e("华为 Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }
}
