package com.dq.itopic.activity.chat.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dq.itopic.R;
import com.dq.itopic.activity.common.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchTagActivity extends BaseActivity{

    private EditText search_et;

    private ListView historyListView;
    private HistoryAdapter historyAdapter;
    private List<String> historyList;

    private ListView suggestListView;
    private LinearLayout history_ll;

    private List<HashMap<String,String>> suggestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tag);
        initData();
        initListener();
    }

//    @Override
//    protected void initStatusBar() {
//        StatusBarUtils.from(this)
//                .setTransparentStatusbar(true)
//                .setStatusBarColor(0xffE5E5E5)
//                .setLightStatusBar(true)
//                .process(this);
//    }

    private void initData() {

        final SharedPreferences pref = getSharedPreferences("HISTORY_TAG", Activity.MODE_PRIVATE);
        final Set<String> historyArray = pref.getStringSet("HISTORY_TAG", new HashSet<String>());
        historyList = new ArrayList<>();
        for (String string :historyArray) {
            historyList.add(string);
        }

        //把两个按钮从布局文件中找到
        search_et = (EditText) findViewById(R.id.searchbar_et);
        history_ll = (LinearLayout) findViewById(R.id.history_ll);
        historyListView = (ListView)findViewById(R.id.gridviewolddata);

        suggestListView = (ListView) findViewById(R.id.listview);
        suggestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                // TODO Auto-generated method stub
                HashMap<String,String> map = suggestList.get(arg2 - suggestListView.getHeaderViewsCount());
                intentToNewActivity(map.get("name"));
            }
        });

        findViewById(R.id.send_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (search_et.getText().toString().trim().length() > 0){
                    intentToNewActivity(search_et.getText().toString().trim());
                }
            }
        });

        historyAdapter = new HistoryAdapter(SearchTagActivity.this, historyList);
        historyListView.setAdapter(historyAdapter);
    }

    private void intentToNewActivity(String keyword){
        final SharedPreferences pref = getSharedPreferences("HISTORY_TAG", Activity.MODE_PRIVATE);
        final Set<String> historyArray = pref.getStringSet("HISTORY_TAG", new HashSet<String>());
        historyArray.add(keyword);
        SharedPreferences.Editor editor =  pref.edit().clear();
        editor.putStringSet("HISTORY_TAG", historyArray);
        editor.commit();

        String activityName = getIntent().getStringExtra("newActivity");
        try {
            Intent intent = new Intent(SearchTagActivity.this, Class.forName(activityName));
            intent.putExtra("title","搜索结果");
            intent.putExtra("keyword",keyword);
            startActivity(intent);
            finish();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //文本观察者
    private class MyTextWatcher implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }
        //当文本改变时候的操作
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            //如果编辑框中文本的长度大于0就显示删除按钮否则不显示
            if (search_et.getText().toString().trim().length() > 0){
                suggestListView.setVisibility(View.VISIBLE);
                history_ll.setVisibility(View.GONE);
            } else {
                suggestListView.setVisibility(View.GONE);
                history_ll.setVisibility(View.VISIBLE);
            }

//            HashMap<String, String> data = new HashMap<String, String>();
//            data.put("page", "1");
//            data.put("keyword", search_et.getText().toString().trim());
//            OkHttpHelper.getInstance().get(HttpUtil.IP+"building/getsimplelist", data, this, new CompleteCallback<HashMapListResponse>(HashMapListResponse.class,getITopicApplication()) {
//
//                @Override
//                public void onComplete(Response okhttpResponse, HashMapListResponse response) {
//                    // TODO Auto-generated method stub
//                    if (response.isSuccess()) {
//                        suggestList = response.getData().getItems();
//                        SuggestAdapter adapter = new SuggestAdapter(SearchTagActivity.this, suggestList);
//                        suggestListView.setAdapter(adapter);
//                    } else {
//                    }
//                }
//            });
        }
    }

    private void initListener() {
        //给编辑框添加文本改变事件
        search_et.addTextChangedListener(new MyTextWatcher());
        search_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    intentToNewActivity(search_et.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intentToNewActivity(historyList.get(position - historyListView.getHeaderViewsCount()).trim());
            }
        });

        search_et.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                showKeyboard(search_et);
            }
        }, 250);
    }

    private class SuggestAdapter extends BaseAdapter {
        private List<HashMap<String,String>> list;
        private LayoutInflater mInflater;
        public SuggestAdapter(Context mContext, List<HashMap<String,String>> list) {
            this.list = list;
            mInflater = LayoutInflater.from(mContext);
        }

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView,ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.listitem_searched_textview, null);
                viewHolder.item_name = (TextView) convertView.findViewById(R.id.item_name);
                Drawable drawable = getResources().getDrawable(R.drawable.search_left_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                viewHolder.item_name.setCompoundDrawables(drawable, null,null, null);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final HashMap<String,String> bean = list.get(position);
            viewHolder.item_name.setText(bean.get("name"));
            return convertView;
        }
    }

    private static class ViewHolder {
        private TextView item_name;
    }

    public class HistoryAdapter extends BaseAdapter {

        private List<String> list;
        private LayoutInflater mInflater;

        public HistoryAdapter(Context context, List<String> list) {
            this.list = list;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return Math.min(list.size(),12);
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder holder ;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.listitem_searched_textview, null);
                holder.item_name = (TextView) convertView.findViewById(R.id.item_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.item_name.setText(list.get(i));
            return convertView;
        }
    }

}
