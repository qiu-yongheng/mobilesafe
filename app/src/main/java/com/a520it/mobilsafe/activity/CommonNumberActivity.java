package com.a520it.mobilsafe.activity;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.dao.CommonNumDAO;
import com.a520it.mobilsafe.utils.ToastUtils;

/**
 * @author 邱永恒
 * @time 2016/7/30  12:19
 * @desc ${TODD}
 */
public class CommonNumberActivity extends Activity{
    private ExpandableListView mElv_parent;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_num);

        //获取数据库对象
        db = SQLiteDatabase.openDatabase("/data/data/com.a520it.mobilsafe/files/commonnum.db", null, SQLiteDatabase.OPEN_READONLY);

        //初始化控件
        initView();
        //绑定自定义适配器
        CommonNumberAdapter adapter = new CommonNumberAdapter();
        mElv_parent.setAdapter(adapter);

        //设置子listView的点击事件
        mElv_parent.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                ToastUtils.showToast(CommonNumberActivity.this, "父" + i + "子" + i1);
                return false;
            }
        });
    }

    private class CommonNumberAdapter extends BaseExpandableListAdapter {

        /**
         * 有多少个分组
         * @return
         */
        @Override
        public int getGroupCount() {
            return CommonNumDAO.getGroupCount(db);
        }

        /**
         * 每个分组中有多少个孩子
         * @param
         * @return
         */
        @Override
        public int getChildrenCount(int groupPosition) {
            return CommonNumDAO.getChildrenCount(db, groupPosition);
            }


        /**
         * 获取父分组
         * @param
         * @return
         */
        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }


        /**
         * 获取最外层的分组名称
         * @param groupPosition
         * @param isExpanded
         * @param convertView
         * @param parent
         * @return
         */
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView tv = new TextView(getApplicationContext());
            tv.setTextSize(20);
            tv.setTextColor(Color.RED);
            tv.setText("     " + CommonNumDAO.getGroupView(db, groupPosition));
            return tv;
        }

        /**
         * 获取父控件对应的子控件的名称
         * @param groupPosition
         * @param childPosition
         * @param isLastChild
         * @param convertView
         * @param parent
         * @return
         */
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView tv = new TextView(getApplicationContext());
            tv.setText(CommonNumDAO.getChilderView(db, groupPosition, childPosition));
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(16);
            return tv;
        }

        //设置子孩子的item是否可以获取焦点
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private void initView() {
        mElv_parent = (ExpandableListView) findViewById(R.id.elv_parent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        db.close();
    }
}
