package com.dq.itopic.activity.chat.keyboard;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.dq.itopic.R;
import com.dq.itopic.views.emojitextview.EmojiconHandler;
import com.sj.emoji.EmojiBean;

import java.io.IOException;

import com.dq.itopic.views.keyboard.adpater.EmoticonsAdapter;
import com.dq.itopic.views.keyboard.adpater.PageSetAdapter;
import com.dq.itopic.views.keyboard.data.EmoticonEntity;
import com.dq.itopic.views.keyboard.data.EmoticonPageEntity;
import com.dq.itopic.views.keyboard.data.EmoticonPageSetEntity;
import com.dq.itopic.views.keyboard.interfaces.EmoticonClickListener;
import com.dq.itopic.views.keyboard.interfaces.EmoticonDisplayListener;
import com.dq.itopic.views.keyboard.interfaces.PageViewInstantiateListener;
import com.dq.itopic.views.keyboard.utils.EmoticonsKeyboardUtils;
import com.dq.itopic.views.keyboard.utils.imageloader.ImageLoader;
import com.dq.itopic.views.keyboard.widget.EmoticonPageView;
import com.dq.itopic.views.keyboard.widget.EmoticonsEditText;

public class QqUtils extends SimpleCommonUtils {

    public static void initEmoticonsEditText(EmoticonsEditText etContent) {
        etContent.addEmoticonFilter(new QqFilter());
    }

    public static EmoticonClickListener getCommonEmoticonClickListener(final EditText editText) {
        return new EmoticonClickListener() {
            @Override
            public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {
                if (isDelBtn) {
                    QqUtils.delClick(editText);
                } else {
                    if (o == null) {
                        return;
                    }
                    if (actionType == EmojiConstants.EMOTICON_CLICK_TEXT) {
                        String content = null;
                        if (o instanceof EmojiBean) {
                            content = ((EmojiBean) o).emoji;
                        } else if (o instanceof EmoticonEntity) {
                            content = ((EmoticonEntity) o).getContent();
                        }

                        if (TextUtils.isEmpty(content)) {
                            return;
                        }
                        int index = editText.getSelectionStart();
                        Editable editable = editText.getText();
                        editable.insert(index, content);
                    }
                }
            }
        };
    }

    public static PageSetAdapter sCommonPageSetAdapter;

    public static PageSetAdapter getCommonAdapter(Context context, EmoticonClickListener emoticonClickListener) {

        if(sCommonPageSetAdapter != null){
            return sCommonPageSetAdapter;
        }

        PageSetAdapter pageSetAdapter = new PageSetAdapter();

        addQqPageSetEntity(pageSetAdapter, context, emoticonClickListener);

//        PageSetEntity pageSetEntity1 = new PageSetEntity.Builder()
//                .addPageEntity(new PageEntity(new SimpleQqGridView(context)))
//                .setIconUri(R.drawable.expression_1)
//                .setShowIndicator(false)
//                .build();
//        pageSetAdapter.add(pageSetEntity1);
//
//        PageSetEntity pageSetEntity2 = new PageSetEntity.Builder()
//                .addPageEntity(new PageEntity(new SimpleQqGridView(context)))
//                .setIconUri(R.drawable.expression_1)
//                .setShowIndicator(false)
//                .build();
//        pageSetAdapter.add(pageSetEntity2);

        return pageSetAdapter;
    }

    public static void addQqPageSetEntity(PageSetAdapter pageSetAdapter, Context context, final EmoticonClickListener emoticonClickListener) {
        EmoticonPageSetEntity kaomojiPageSetEntity
                = new EmoticonPageSetEntity.Builder()
                .setLine(3)
                .setRow(7)
                .setEmoticonList(ParseDataUtils.ParseQqData(EmojiconHandler.sQQFaceMap))
                .setIPageViewInstantiateItem(new PageViewInstantiateListener<EmoticonPageEntity>() {
                    @Override
                    public View instantiateItem(ViewGroup container, int position, EmoticonPageEntity pageEntity) {
                        if (pageEntity.getRootView() == null) {
                            EmoticonPageView pageView = new EmoticonPageView(container.getContext());
                            pageView.setNumColumns(pageEntity.getRow());
                            pageEntity.setRootView(pageView);
                            try {
                                EmoticonsAdapter adapter = new EmoticonsAdapter(container.getContext(), pageEntity, emoticonClickListener);
                                adapter.setItemHeightMaxRatio(1.8);
                                adapter.setOnDisPlayListener(getEmoticonDisplayListener(emoticonClickListener));
                                pageView.getEmoticonsGridView().setAdapter(adapter);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return pageEntity.getRootView();
                    }
                })
                .setShowDelBtn(EmoticonPageEntity.DelBtnStatus.LAST)
                .setIconUri(R.drawable.expression_1)
                .build();
        pageSetAdapter.add(kaomojiPageSetEntity);
    }

    public static EmoticonDisplayListener<Object> getEmoticonDisplayListener(final EmoticonClickListener emoticonClickListener){
        return new EmoticonDisplayListener<Object>() {
            @Override
            public void onBindView(int position, ViewGroup parent, EmoticonsAdapter.ViewHolder viewHolder, Object object, final boolean isDelBtn) {
                final EmoticonEntity emoticonEntity = (EmoticonEntity) object;
                if (emoticonEntity == null && !isDelBtn) {
                    return;
                }
                viewHolder.ly_root.setBackgroundResource(R.drawable.bg_emoticon);

                if (isDelBtn) {
                    viewHolder.iv_emoticon.setImageResource(R.drawable.icon_del);
                } else {
                    try {
                        ImageLoader.getInstance(viewHolder.iv_emoticon.getContext()).displayImage(emoticonEntity.getIconUri(), viewHolder.iv_emoticon);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (emoticonClickListener != null) {
                            emoticonClickListener.onEmoticonClick(emoticonEntity, EmojiConstants.EMOTICON_CLICK_TEXT, isDelBtn);
                        }
                    }
                });
            }
        };
    }

    public static void spannableEmoticonFilter(TextView tv_content, String content) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
        Spannable spannable = QqFilter.spannableFilter(tv_content.getContext(),
                spannableStringBuilder,
                content,
                EmoticonsKeyboardUtils.getFontHeight(tv_content),
                null);
        tv_content.setText(spannable);
    }
}
