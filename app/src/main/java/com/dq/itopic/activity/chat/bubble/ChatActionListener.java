package com.dq.itopic.activity.chat.bubble;

import android.view.View;

public interface ChatActionListener {
    public void onAvatarClick(View view);
    public boolean onBubbleLongClick(View view);
    public void onBubbleClick(View view);
}
