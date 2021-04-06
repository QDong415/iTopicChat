package com.dq.itopic.views;

import com.dq.itopic.R;
import com.dq.itopic.activity.common.BaseActivity;
import com.dq.itopic.activity.login.SMSBaseActivity;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 自定义提示Toast
 * 
 * @author xm
 */
public class TipsToast extends Toast {

    public TipsToast(Context context) {
        super(context);
    }

    public static TipsToast makeText(Context context, CharSequence text, int duration) {
        TipsToast result = new TipsToast(context);

        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.toast_tips, null);
        TextView tv = (TextView) v.findViewById(R.id.tips_msg);
        tv.setText(text);

        result.setView(v);
        result.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        result.setDuration(duration);

        return result;
    }

    public static TipsToast makeText(Context context, int resId, int duration) throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId), duration);
    }

    public void setIcon(int iconResId) {
        if (getView() != null) {
        	ImageView iv = (ImageView) getView().findViewById(R.id.tips_icon);
        	iv.setImageResource(iconResId);
        }
    }
    
    @Override
    public void setText(CharSequence s) {
        if (getView() != null) {
            TextView tv = (TextView) getView().findViewById(R.id.tips_msg);
            tv.setText(s);
        } 
    }
}
