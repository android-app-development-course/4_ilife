package com.example.administrator.ilife_manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    MonthData helper;

    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;
    View moneyView,timeView,myView;
    List<View> viewList;
    private MenuItem menuItem;

    Button addRecord,checkRecord,checkChart;
    TextView outAllTV,inAllTV;

    TextView accountName,email;
    ImageView myPhoto;
    LinearLayout information;

    Button noteBtn;
    View goals;
    ListView goalList;
    ViewPager goalsViewPager;
    List<View> goalViewList=new ArrayList<>();

    View recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initGoalList();
        initListener();
        innitInformation();
        initMoneyData();
        /*RecordItemData recordItemDataHelper =new RecordItemData(this);
        SQLiteDatabase db= recordItemDataHelper.getWritableDatabase();
        db.delete(RecordItemData.MoneyTable,null,null);
        db.close();
        MonthData helper=new MonthData(this);
        SQLiteDatabase db2=helper.getWritableDatabase();
        db2.delete(MonthData.monthTable,null,null);
        db2.close();*/
        /*NoteHelper noteHelper=new NoteHelper(this);
        SQLiteDatabase db= noteHelper.getWritableDatabase();
        db.delete("notebook",null,null);
        db.close();*/



    }

    public PendingIntent getDefalutIntent(int flags){
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }
    private void init()
    {

        //初始化底部导航栏和ViewPager
        bottomNavigationView=(BottomNavigationView)findViewById(R.id.navigation);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        LayoutInflater inflater=getLayoutInflater();
        viewList=new ArrayList<View>();
        moneyView = inflater.inflate(R.layout.money, null);
        timeView = inflater.inflate(R.layout.time,null);
        myView = inflater.inflate(R.layout.my, null);
        viewList.add(moneyView);
        viewList.add(timeView);
        viewList.add(myView);
        viewPager.setAdapter(pagerAdapter);

        //初始化主界面控件
        addRecord=(Button)moneyView.findViewById(R.id.add) ;
        checkRecord=(Button)moneyView.findViewById(R.id.check);
        checkChart=(Button)moneyView.findViewById(R.id.chart) ;
        information =(LinearLayout)myView.findViewById(R.id.myAccount) ;
        accountName=(TextView)myView.findViewById(R.id.accountName);
        email=(TextView)myView.findViewById(R.id.email) ;
        myPhoto=(ImageView)myView.findViewById(R.id.iv_my_photo);
        recorder=(View) myView.findViewById(R.id.record_reminder);
        outAllTV=(TextView)moneyView.findViewById(R.id.expenseNum);
        inAllTV=(TextView)moneyView.findViewById(R.id.incomeNum);
        noteBtn=(Button)timeView.findViewById(R.id.noteBtn) ;

        innitInformation();//初始化个人信息的显示

        goals=View.inflate(this,R.layout.goals_listview,null);
        goalList=goals.findViewById(R.id.goalsListView);
        goalsViewPager=timeView.findViewById(R.id.tickViewPager);
    }

    //初始化个人信息的显示
    private void innitInformation()
    {
        Map<String,String> userInfo=FilesaveInfo.getUserInfo(this);
        if(userInfo!=null)
        {
            accountName.setText(userInfo.get("nickname"));
            email.setText(userInfo.get("email"));
        }
        String path= "/sdcard/my_info_photo.png";
        File file = new File(path);
        if(file.exists())
        {
            Bitmap tempbitmap = BitmapFactory.decodeFile(path);
            tempbitmap=dealBitmap(tempbitmap);
            myPhoto.setImageBitmap(tempbitmap);
        }
    }

    //设置头像图片参数
    private Bitmap dealBitmap(Bitmap bitMap)
    {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        // 设置想要的大小
        int newWidth = 100;
        int newHeight = 100;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        bitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix,
                true);
        return bitMap;
    }

    //为各组件添加监听
    private void initListener()
    {
        //监听ViewPager滑动事件，切换底部导航栏选中的Item
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //时间主界面按钮监听
        addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,addMoneyRecord.class);
                intent.putExtra("time","");
                startActivityForResult(intent,0);
            }
        });
        checkRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,checkAccountBook.class);
                startActivityForResult(intent,0);
            }
        });
        checkChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,Chart.class);
                startActivity(intent);
            }
        });

        //底部导航栏监听，切换ViewPager
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_money:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.navigation_time:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.navigation_my:
                                viewPager.setCurrentItem(2);
                                break;
                        }
                        return false;
                    }
                });

        //为个人信息栏添加监听，跳转到信息修改页面
        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,infomation.class);
                startActivityForResult(intent,11);
            }
        });


        recorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,TimeRecord.class);
                startActivity(intent);
            }
        });

        noteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,note.class);
                startActivity(intent);
            }
        });
    }

    private void initMoneyData()
    {
        helper=new MonthData(this);
        SQLiteDatabase db=helper.getReadableDatabase();
        final Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        int Month = ca.get(Calendar.MONTH)+1;//得到实际月份
        String month=Month<10?"0"+Integer.toString(Month):Integer.toString(Month);
        Cursor cursor=db.query(MonthData.monthTable,null,"time=?",new String[]{""+year+month},null,null,null);
        String outAll,inAll;
        if(cursor.getCount()==0)
            outAll=inAll="0";
        else{
            cursor.moveToFirst();
            outAll=(cursor.getString(MonthData.OUT_ALL)==null?"0":cursor.getString(MonthData.OUT_ALL));
            inAll=(cursor.getString(MonthData.IN_ALL)==null?"0":cursor.getString(MonthData.IN_ALL));
        }
        if(Double.valueOf(outAll)!=0)
            outAll=outAll.substring(1,outAll.length());

        java.text.DecimalFormat myformat = new java.text.DecimalFormat("0.00");
        outAll=myformat.format(Double.valueOf(outAll));
        inAll=myformat.format(Double.valueOf(inAll));


        outAllTV.setText(outAll);
        inAllTV.setText(inAll);

        cursor.close();
        db.close();
    }
    private void initGoalList()
    {
        goalViewList.add(goals);
        goalsViewPager.setAdapter(goalsPagerAdapter);
        goalsViewPager.setCurrentItem(0);
        goalList.setAdapter(new GoalListAdpter());
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            if (resultCode == 22) {
                Toast.makeText(this,"个人信息已修改", Toast.LENGTH_SHORT).show();
                innitInformation();
            }
        }
        if(requestCode==0)
            initMoneyData();
    }

    //创建ViewPager的适配器实例
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
            return viewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            // TODO Auto-generated method stub
            container.removeView(viewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            container.addView(viewList.get(position));
            return viewList.get(position);
        }
    };

    PagerAdapter goalsPagerAdapter = new PagerAdapter()
    {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return goalViewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            // TODO Auto-generated method stub
            container.removeView(goalViewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            container.addView(goalViewList.get(position));
            return goalViewList.get(position);
        }
    };

    class  GoalListAdpter extends BaseAdapter
    {

        String goals[]={"学习","弹琴","运动"};
        @Override
        public int getCount() {
            return goals.length;
        }

        @Override
        public Object getItem(int i) {
            return goals[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1=View.inflate(MainActivity.this,R.layout.tick_item,null);
            TextView goalContent=view1.findViewById(R.id.goalContent);
            goalContent.setText(goals[i]);
            return view1;
        }
    }
}
