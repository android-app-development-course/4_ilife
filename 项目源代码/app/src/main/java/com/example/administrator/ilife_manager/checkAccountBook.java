package com.example.administrator.ilife_manager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class checkAccountBook extends AppCompatActivity {
    private SwipeMenuListView listView;//带滑动菜单的listView，来自gitHub的项目
    private ArrayList<Data> mDatas;//mDatas为从数据库读取的纯数据链表，addTimeDatas为加了时间节点的数据链表，用于listView适配器
    MyBaseAdpter myBaseAdpter;
    Button year,month;//对话框中选择年、月的按钮

    RecordItemData recordItemDataHelper;
    MonthData monthHelper;//存每月各类别收支总额的表

    //对话框组件
    AlertDialog dialog;
    ViewPager timeChooseViewPager;
    List<View> pagerList =new ArrayList<View>();//存选择时间对话框中年、月两个view，用于pagerView的适配器
    Button[] monthButtonGroup;//月份选择按钮数组


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_account_book);


        //初始化pagerView数组
        View yearView=LayoutInflater.from(this).inflate(R.layout.year_choose,null);
        View monthView=LayoutInflater.from(this).inflate(R.layout.month_choose,null);
        pagerList.add(yearView);
        pagerList.add(monthView);


        recordItemDataHelper =new RecordItemData(this);
        monthHelper=new MonthData(this);


        //ListView初始化与显示
        listView=(SwipeMenuListView) findViewById(R.id.listView);
        getDB_datas();//获取数据库数据到链表mDatas
        myBaseAdpter=new MyBaseAdpter();
        listView.setAdapter(myBaseAdpter);
        listView.setMenuCreator(creator);
        listView.setDividerHeight(0);

        //初始化监听器
        initListener();
    }


    private void showDialog()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        dialog=builder.create();
        View dialogView=View.inflate(this,R.layout.time_choose_dialog,null);
        timeChooseViewPager=dialogView.findViewById(R.id.timeChooseViewPager);
        timeChooseViewPager.setAdapter(pagerAdapter);
        year=(Button)dialogView.findViewById(R.id.chooseYear);
        month=(Button)dialogView.findViewById(R.id.chooseMonth);
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        Window window = dialog.getWindow();
        //设置显示位置
        WindowManager.LayoutParams lp = window.getAttributes();
        //不设置lp.width 和lp.height，对话框大小由布局决定
        lp.width = width; //大小

        //自定义位置
        //lp.x = 0;
        lp.y = 105;
        //lp.alpha=0.5f;
        lp.gravity = Gravity.LEFT | Gravity.TOP;//不设置这个时,lp.x和lp.y无效
        //window.setGravity(Gravity.CENTER);//居中显示
        window.setAttributes(lp);
        window.setContentView(dialogView);

        //时间选择对话框中按钮、ViewPager的关联
        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timeChooseViewPager.setCurrentItem(1);
            }
        });
        year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeChooseViewPager.setCurrentItem(0);
            }
        });
        timeChooseViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                if(position==0)
                {
                    year.setSelected(true);
                    month.setSelected(false);
                }
                if(position==1)
                {
                    month.setSelected(true);
                    year.setSelected(false);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        month.performClick();
    }


    private void initListener()
    {

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch(index)
                {
                    //编辑记录，通过intent传递数据给编辑页面
                    case 0:
                        Intent intent=new Intent(checkAccountBook.this,addMoneyRecord.class);
                        Data data= mDatas.get(position);
                        intent.putExtra("money",data.money);
                        intent.putExtra("ins",data.ins);
                        intent.putExtra("type",data.type);
                        intent.putExtra("time",data.time);
                        intent.putExtra("id",data.id);
                        startActivityForResult(intent,1);
                        break;

                    //删除数据
                    case 1:
                        String id= mDatas.get(position).id;

                        //单条记录表中删除该条记录
                        SQLiteDatabase db= recordItemDataHelper.getWritableDatabase();
                        db.delete(RecordItemData.MoneyTable,"_id=?",new String[]{id});
                        db.close();



                        //月表中该月该类别减去这条记录的金额
                        SQLiteDatabase monDb=monthHelper.getWritableDatabase();
                        //得到该月份记录
                        Cursor cursor=monDb.query(MonthData.monthTable,null,"time=?",new String[]{mDatas.get(position).time.substring(0,6)},null,null,null);
                        int type=Integer.valueOf(mDatas.get(position).type);
                        cursor.moveToFirst();
                        double num=Double.valueOf(cursor.getString(type)) - Double.valueOf(mDatas.get(position).money);
                        ContentValues values=new ContentValues();
                        values.put(addMoneyRecord.typeName[type],Double.toString(num));
                        if(type<=MonthData.ACCIDENT)
                        {
                            String previousAll=cursor.getString(MonthData.OUT_ALL);
                            Double preAll=Double.valueOf(previousAll);
                            values.put("outputall",preAll-Double.valueOf(mDatas.get(position).money));
                        }
                        else{
                            String previousAll=cursor.getString(MonthData.IN_ALL);
                            Double preAll=Double.valueOf(previousAll);
                            values.put("incomeall",preAll-Double.valueOf(mDatas.get(position).money));
                        }
                        monDb.update(MonthData.monthTable,values,"time=?",new String[]{mDatas.get(position).time.substring(0,6)});
                        cursor.close();
                        monDb.close();


                        //删除后重置listView，显示新数据
                        getDB_datas();
                        listView.setAdapter(myBaseAdpter);
                }

                return true;
            }
        });
    }

    public void getDB_datas() {
        mDatas = new ArrayList<>();

        SQLiteDatabase db;
        db = recordItemDataHelper.getReadableDatabase();
        //对所有记录按时间排序
        Cursor cursor = db.query("account_book", null, null, null, null, null, RecordItemData.time);

        if (cursor.getCount() == 0)
            Toast.makeText(this, "暂无数据，快来记一笔吧~", Toast.LENGTH_SHORT).show();
        else {
            cursor.moveToLast();
            int timeNode=0;
            Data recordData = new Data();//存记录
            Data timeData=new Data();//存日期
            recordData.type = cursor.getString(1);//类型
            recordData.ins = cursor.getString(2);//备注
            recordData.money = cursor.getString(3);//金额
            recordData.time = (cursor.getString(4));//时间
            recordData.id=cursor.getString(0);//数据id

            //创建时间节点
            String day=recordData.time.substring(0,8);
            timeData.type="time";
            timeData.time=day;
            timeData.total+=Float.valueOf(recordData.money);//相同日期的金额加到时间节点的total中
            mDatas.add(timeData);
            mDatas.add(recordData);


            while (cursor.moveToPrevious()) {
                recordData = new Data();
                recordData.type = cursor.getString(1);
                recordData.ins = cursor.getString(2);
                recordData.money = cursor.getString(3);
                recordData.time = cursor.getString(4);
                recordData.id=cursor.getString(0);

                if(!day.equals(recordData.time.substring(0,8)))//与前一条记录日期不同，创建新的日期
                {
                    timeData=new Data();
                    day=recordData.time.substring(0,8);
                    timeData.time=day;
                    timeData.type="time";
                    mDatas.add(timeData);
                    timeNode=mDatas.size()-1;
                }
                mDatas.add(recordData);
                mDatas.get(timeNode).total+=Float.valueOf(recordData.money);
            }

            cursor.close();
            db.close();

        }

    }



    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            // create "open" item
            switch (menu.getViewType()) {
                case 0://时间节点的item,不用添加滑动菜单
                    break;
                case 1:
                    SwipeMenuItem openItem = new SwipeMenuItem(
                            getApplicationContext());
                    // set item background
                    openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                            0xCE)));
                    // set item width
                    openItem.setWidth(200);
                    // set item title
                    openItem.setIcon(R.drawable.ic_action_edit);
                    //openItem.setTitle("Open");
                    // set item title fontsize
                    openItem.setTitleSize(18);
                    // set item title font color
                    openItem.setTitleColor(Color.WHITE);
                    // add to menu
                    menu.addMenuItem(openItem);



                    // create "delete" item
                    SwipeMenuItem deleteItem = new SwipeMenuItem(
                            getApplicationContext());
                    // set item background
                    deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                            0x3F, 0x25)));
                    // set item width
                    deleteItem.setWidth(200);
                    // set a icon
                    deleteItem.setIcon(R.drawable.ic_action_name);
                    // add to menu
                    menu.addMenuItem(deleteItem);
                    break;
            }

        }
    };


    protected void onActivityResult(int request,int result ,Intent intent)
    {
        super.onActivityResult(request,result,intent);
        {
            //编辑返回时重置ListView
            if(request==1&&result==1) {
                getDB_datas();
                listView.setAdapter(myBaseAdpter);
            }
        }
    }


    public void checkOnclick(View v)
    {
        switch (v.getId())
        {
            case R.id.chooseAccountTime:

                showDialog();

             break;

            case R.id.checkReturn:
                finish();
                super.onBackPressed();
                break;

            case R.id.jan:
                //dialog.dismiss();
                break;
        }
    }

    //数据内容类
    public class Data implements Serializable
    {
        String money;
        String ins;
        String  type;
        String time;
        String id;
        int total=0;
    }

    //时间选择对话框中ViewPager的适配器
    PagerAdapter pagerAdapter = new PagerAdapter()
    {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return pagerList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            // TODO Auto-generated method stub
            container.removeView(pagerList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            container.addView(pagerList.get(position));
            return pagerList.get(position);
        }
    };

    //ListView的适配器
    class  MyBaseAdpter extends BaseAdapter {
        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //获取View类型，0为日期，1为记录数据
        public int getItemViewType(int position)
        {
            if(mDatas.get(position).type.equals("time"))
                return 0;
            else return 1;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=null;
            Data data=mDatas.get(position);

            if(data.type.equals("time"))
            {
                view=View.inflate(checkAccountBook.this,R.layout.time_item,null);
                TextView timeTextView=view.findViewById(R.id.time_TV);
                timeTextView.setText(data.time.substring(0,4)+"-"+ data.time.substring(4,6)+"-"+ data.time.substring(6,8));
                TextView total=view.findViewById(R.id.totalMoney);
                String totalNum=(data.total>0?"+"+Double.toString(data.total):Double.toString(data.total));
                total.setText(totalNum);
                return view;
            }

            //节点为数据时判断类型，根据不同类型返回不同View
            else if(data.type.equals("3"))
                view=View.inflate(checkAccountBook.this,R.layout.eatting_item,null);
            else  if(data.type.equals("4"))
                view=View.inflate(checkAccountBook.this,R.layout.trans_item,null);
            else  if(data.type.equals("6"))
                view=View.inflate(checkAccountBook.this,R.layout.house_item,null);
            else  if(data.type.equals("5"))
                view=View.inflate(checkAccountBook.this,R.layout.purchase_item,null);
            else  if(data.type.equals("7"))
                view=View.inflate(checkAccountBook.this,R.layout.enter_item,null);
            else  if(data.type.equals("8"))
                view=View.inflate(checkAccountBook.this,R.layout.edu_item,null);
            else  if(data.type.equals("9"))
                view=View.inflate(checkAccountBook.this,R.layout.hos_item,null);
            else  if(data.type.equals("10"))
                view=View.inflate(checkAccountBook.this,R.layout.friend_item,null);
            else  if(data.type.equals("11"))
                view=View.inflate(checkAccountBook.this,R.layout.other_item,null);
            else  if(data.type.equals("12"))
                view=View.inflate(checkAccountBook.this,R.layout.acci_item,null);
            else if(data.type.equals("13"))
                view=View.inflate(checkAccountBook.this,R.layout.salary_item,null);
            else if(data.type.equals("14"))
                view=View.inflate(checkAccountBook.this,R.layout.redpackage_item,null);
            else if(data.type.equals("15"))
                view=View.inflate(checkAccountBook.this,R.layout.life_expense_item,null);
            else if(data.type.equals("16"))
                view=View.inflate(checkAccountBook.this,R.layout.bonus_item,null);
            else if(data.type.equals("17"))
                view=View.inflate(checkAccountBook.this,R.layout.reimburse_item,null);
            else if(data.type.equals("18"))
                view=View.inflate(checkAccountBook.this,R.layout.parttime_item,null);
            else if(data.type.equals("19"))
                view=View.inflate(checkAccountBook.this,R.layout.borrow_item,null);
            else if(data.type.equals("20"))
                view=View.inflate(checkAccountBook.this,R.layout.invest_item,null);
            else if(data.type.equals("21"))
                view=View.inflate(checkAccountBook.this,R.layout.pickup_item,null);
            else if(data.type.equals("22"))
                view=View.inflate(checkAccountBook.this,R.layout.other_in_item,null);


            //类型
           /* TextView typeTextView=(TextView)view.findViewById(R.id.tv_accountItem_type);
            int type=Integer.valueOf(data.type);
            typeTextView.setText(types[type-3]);*/

            //备注
            TextView type_contentTextView=(TextView)view.findViewById(R.id.tv_accountItem_type_content);
            type_contentTextView.setText(data.ins);

            //金额
            TextView accountTextView=(TextView)view.findViewById(R.id.tv_accountItem_account);
            accountTextView.setText(data.money);

            return view;
        }
    }
}
