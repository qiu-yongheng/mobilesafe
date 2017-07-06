package com.a520it.mobilsafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.utils.ContactInfoUtils;

import java.util.List;

/**
 * @author 邱永恒
 * @time 2016/7/26  0:27
 * @desc ${TODD}
 */
public class SelectContactActivity extends Activity{
    private ListView lv_contact;
    private List<ContactInfoUtils.ContactInfo> selectInfo;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            //绑定适配器
            lv_contact.setAdapter(new SelectContactAdapter());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        //获取listView控件
        lv_contact = (ListView) findViewById(R.id.lv_contact);

        //在子线程中获取联系人的数据
        new Thread(){
            public void run(){
                selectInfo = ContactInfoUtils.getContactInfo(SelectContactActivity.this);

                //不能再子线程修改UI
                //发送空消息给主线程(告诉主线程已经获得数据)
                handler.sendEmptyMessage(0);

            }
        }.start();

        //设置组件的点击事件
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone = selectInfo.get(position).phone;

                Intent intent = new Intent();

                intent.putExtra("phone", phone);

                //设置结果码, 返回数据
                setResult(0, intent);

                //结束界面
                finish();
            }
        });
    }

    //创建一个自定义适配器
    class SelectContactAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return selectInfo.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           View view = View.inflate(SelectContactActivity.this, R.layout.item_select, null);

            //获取控件
            TextView tv_username = (TextView) view.findViewById(R.id.tv_username);
            TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);

            //给控件设置值
            tv_username.setText(selectInfo.get(position).name);
            tv_phone.setText(selectInfo.get(position).phone);
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


    }
}
