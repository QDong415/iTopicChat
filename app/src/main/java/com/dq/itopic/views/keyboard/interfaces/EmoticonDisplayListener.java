package com.dq.itopic.views.keyboard.interfaces;

import android.view.ViewGroup;

import com.dq.itopic.views.keyboard.adpater.EmoticonsAdapter;

public interface EmoticonDisplayListener<T> {

    void onBindView(int position, ViewGroup parent, EmoticonsAdapter.ViewHolder viewHolder, T t, boolean isDelBtn);
}
