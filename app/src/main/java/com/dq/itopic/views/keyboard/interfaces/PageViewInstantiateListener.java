package com.dq.itopic.views.keyboard.interfaces;

import android.view.View;
import android.view.ViewGroup;

import com.dq.itopic.views.keyboard.data.PageEntity;

public interface PageViewInstantiateListener<T extends PageEntity> {

    View instantiateItem(ViewGroup container, int position, T pageEntity);
}
