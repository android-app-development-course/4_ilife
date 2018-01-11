package com.example.administrator.ilife_manager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class addMoneyRecord extends AppCompatActivity {
    View output,input;
    List<View> list;
    ViewPager viewPager;
    LinearLayout outPutTypeBack;
    Button outputBtn,inputBtn;
    EditText moneyTV,instruction;
    TextView outPutTypeTV,outPutExpression;
    Button eatting,transportation,purchase,house,entertainment,hospital,education,friend,other,accident;
    Button input_accidentMoney,input_bonus,input_borrow,input_invest,input_livingExpense,input_other,input_parttime,input_redParket,input_reimburse,input_salary;

    public static  String typeName[]={"","","","eatting","transportation","house" ,"purchase","entertainment","education" ,"hospital","friend","other ","accident","salary","redpacket","livingexpense","bonus","reimburse","parttime","borrow","invest","accidentmoney","input_other"};

    Button date,time;
    int mYear,mMonth,mDay,mHour,mMinute;
    int type= MonthData.EATTING;

    RecordItemData recordItemHelper;
    MonthData monthDataHelper;

    boolean expressionOrNot=false;
    boolean pointAvailable=true;
    boolean isInOrOut=false;//true为收入，false为支出。

    private DatePickerDialog.OnDateSetListener mdateListener;
    private TimePickerDialog.OnTimeSetListener mtimeListener;


    boolean isEdit=false;//判断是否是编辑页面
    double previousMonney;
    String preYear,preMonth;
    int preType;
    String id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_money_record);
        init();
        initListener();
        getMyIntent();

    }

    private void init()
    {
        recordItemHelper =new RecordItemData(this);
        monthDataHelper=new MonthData(this);

        date=(Button)findViewById(R.id.chooseDate);
        time=(Button)findViewById(R.id.chooseTime);

        //初始化数据输入组件
        instruction=(EditText)findViewById(R.id.instructionContent);
        outPutTypeBack=(LinearLayout)findViewById(R.id.moneyInput);
        moneyTV =(EditText)findViewById(R.id.outPutMoney) ;
        outPutTypeTV=(TextView)findViewById(R.id.outputTypeTV);
        outPutExpression=(TextView)findViewById(R.id.output_expression);

        outputBtn=(Button)findViewById(R.id.outputBtn);
        inputBtn=(Button)findViewById(R.id.inputBtn);
        outputBtn.setSelected(true);

        //初始化ViewPager
        list=new ArrayList<View>();
        viewPager=(ViewPager)findViewById(R.id.type_vp);
        LayoutInflater inflater=getLayoutInflater();
        output = inflater.inflate(R.layout.output, null);
        input = inflater.inflate(R.layout.input, null);
        list.add(output);
        list.add(input);
        viewPager.setAdapter(pagerAdapter);

        //初始化支出类别按钮
        eatting=(Button)output.findViewById(R.id.eatting);
        transportation=(Button)output.findViewById(R.id.transportation);
        purchase=(Button)output.findViewById(R.id.purchase);
        house=(Button)output.findViewById(R.id.house);
        entertainment=(Button)output.findViewById(R.id.entertainment);
        hospital=(Button)output.findViewById(R.id.hospital);
        education=(Button)output.findViewById(R.id.education);
        friend=(Button)output.findViewById(R.id.friend);
        other=(Button)output.findViewById(R.id.other);
        accident=(Button)output.findViewById(R.id.accident);

        eatting.setSelected(true);

        //初始化收入类别按钮
        input_accidentMoney=(Button)input.findViewById(R.id.accidentMoney);
        input_bonus=(Button)input.findViewById(R.id.bonus);
        input_borrow=(Button)input.findViewById(R.id.borrow);
        input_invest=(Button)input.findViewById(R.id.invest);
        input_livingExpense=(Button)input.findViewById(R.id.livingExpenses);
        input_other=(Button)input.findViewById(R.id.input_other);
        input_parttime=(Button)input.findViewById(R.id.parttime);
        input_redParket=(Button)input.findViewById(R.id.redPacket);
        input_reimburse=(Button)input.findViewById(R.id.reimburse);
        input_salary=(Button)input.findViewById(R.id.salary);

        //获得系统时间
        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH)+1;//得到实际月份
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        mHour=ca.get(Calendar.HOUR_OF_DAY);
        mMinute=ca.get(Calendar.MINUTE);

        getMyIntent();
        date.setText(mYear + "-" + mMonth + "-" + mDay);
        time.setText(mHour+" : "+mMinute);


        //创建时间选择对话框的监听器，将所选时间赋给类变量，设置页面显示时间
        mdateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                mYear = year;
                mMonth = 1+monthOfYear;
                mDay = dayOfMonth;
                date.setText(mYear + "-" + (mMonth) + "-" + mDay );
            }
        };
        mtimeListener=new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                mHour=hour;
                mMinute=minute;
                time.setText(mHour+" : "+mMinute);
            }
        };

    }


    private void initListener()
    {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(position==0)
                {
                    outputBtn.setSelected(true);
                    inputBtn.setSelected(false);
                    eatting.performClick();
                }
                else
                {
                    inputBtn.setSelected(true);
                    outputBtn.setSelected(false);
                    input_salary.performClick();
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //给时间选择按钮添加监听器，点击弹出时间对话框
        date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //showDialog(DATE_DIALOG);
                DatePickerDialog dateDialog=new DatePickerDialog(addMoneyRecord.this,mdateListener,mYear,mMonth-1,mDay);
                dateDialog.show();
            }
        });
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //timeDialog=new TimeDialog(addMoneyRecord.this);
                //timeDialog.show();
                TimePickerDialog timeDialog=new TimePickerDialog(addMoneyRecord.this,mtimeListener,mHour,mMinute,true);
                timeDialog.show();
            }
        });
    }


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
            return list.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            // TODO Auto-generated method stub
            container.removeView(list.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            container.addView(list.get(position));
            return list.get(position);
        }
    };

    //类别按钮的响应函数
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.outputBtn:
                viewPager.setCurrentItem(0);
                outputBtn.setSelected(true);
                inputBtn.setSelected(false);
                eatting.performClick();
                break;
            case R.id.inputBtn:
                viewPager.setCurrentItem(1);
                inputBtn.setSelected(true);
                outputBtn.setSelected(false);
                input_salary.performClick();
                break;

            case R.id.eatting:
                setNoSelected();
                eatting.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.light_orange));
                outPutTypeTV.setText("餐饮");
                type= MonthData.EATTING;
                isInOrOut=false;

                break;
            case R.id.transportation:
                setNoSelected();
                transportation.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.dark_green));
                outPutTypeTV.setText("交通");
                type= MonthData.TRANSPORTATION;
                isInOrOut=false;
                break;
            case R.id.purchase:
                setNoSelected();
                purchase.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.dark_red));
                outPutTypeTV.setText("购物");
                type= MonthData.PURCHASE;
                isInOrOut=false;
                break;
            case R.id.entertainment:
                setNoSelected();
                entertainment.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.light_blue));
                outPutTypeTV.setText("娱乐");
                type= MonthData.ENTERTAIMENT;
                isInOrOut=false;
                break;
            case R.id.house:
                setNoSelected();
                house.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.light_red));
                outPutTypeTV.setText("居住");
                type= MonthData.HOUSE;
                isInOrOut=false;
                break;
            case R.id.other:
                setNoSelected();
                other.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.light_gray));
                outPutTypeTV.setText("其他");
                type= MonthData.OTHER;
                isInOrOut=false;
                break;
            case R.id.hospital:
                setNoSelected();
                hospital.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.dark_red));
                outPutTypeTV.setText("医疗");
                type= MonthData.HOSPITAL;
                isInOrOut=false;
                break;
            case R.id.education:
                setNoSelected();
                education.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.light_green));
                outPutTypeTV.setText("教育");
                type= MonthData.EDUCATION;
                isInOrOut=false;
                break;
            case R.id.accident:
                setNoSelected();
                accident.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.dark_blue));
                outPutTypeTV.setText("意外");
                type= MonthData.ACCIDENT;
                isInOrOut=false;
                break;
            case R.id.friend:
                setNoSelected();
                friend.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.dark_orange));
                outPutTypeTV.setText("人情");
                type= MonthData.FRIEND;
                isInOrOut=false;
                break;

            case R.id.salary:
                setNoSelected();
                input_salary.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.light_orange));
                outPutTypeTV.setText("工资");
                type= MonthData.SALARY;
                isInOrOut=true;

                break;

            case R.id.redPacket:
                setNoSelected();
                input_redParket.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.dark_red));
                outPutTypeTV.setText("收红包");
                type= MonthData.REDPACKET;
                isInOrOut=true;

                break;

            case R.id.livingExpenses:
                setNoSelected();
                input_livingExpense.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.dark_orange));
                outPutTypeTV.setText("生活费");
                type= MonthData.LIVINGEXPENSE;
                isInOrOut=true;

                break;

            case R.id.bonus:
                setNoSelected();
                input_bonus.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.dark_green));
                outPutTypeTV.setText("奖金");
                type= MonthData.BONUS;
                isInOrOut=true;
                break;

            case R.id.reimburse:
                setNoSelected();
                input_reimburse.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.dark_blue));
                outPutTypeTV.setText("报销");
                type= MonthData.REIMBURSE;
                isInOrOut=true;

                break;

            case R.id.parttime:
                setNoSelected();
                input_parttime.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.dark_green));
                outPutTypeTV.setText("兼职");
                type= MonthData.PARTTIME;
                isInOrOut=true;

                break;

            case R.id.borrow:
                setNoSelected();
                input_borrow.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.light_red));
                outPutTypeTV.setText("借入款");
                type= MonthData.BORROW;
                isInOrOut=true;

                break;

            case R.id.invest:
                setNoSelected();
                input_invest.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.dark_red));
                outPutTypeTV.setText("投资");
                type= MonthData.INVEST;
                isInOrOut=true;

                break;

            case R.id.accidentMoney:
                setNoSelected();
                input_accidentMoney.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.light_gray));
                outPutTypeTV.setText("捡到钱");
                type= MonthData.ACCIDENTMONEY;
                isInOrOut=true;

                break;

            case R.id.input_other:
                setNoSelected();
                input_other.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.black));
                outPutTypeTV.setText("其他");
                type= MonthData.INPUT_OTHER;
                isInOrOut=true;

                break;


            case R.id.addAccountRecord_return:
                super.onBackPressed();
                finish();
                break;
        }
    }

    //输入键盘按钮响应函数
    public void keyBoardListener(View v)
    {
        switch (v.getId())
        {
            //为数字0-9添加响应函数
            case R.id.num1:
                if(expressionOrNot)//如果输入的是表达式，即带有运算符，执行计算
                {
                    outPutExpression.append("1");
                    moneyTV.setText(Calculate.calculate(Transfer.transfer(Transfer.toArray(outPutExpression.getText().toString()))));
                }
                else
                    moneyTV.setText(moneyTV.getText().toString()+"1");
                break;
            case R.id.num2:
                if(expressionOrNot)
                {
                    outPutExpression.append("2");
                    moneyTV.setText(Calculate.calculate(Transfer.transfer(Transfer.toArray(outPutExpression.getText().toString()))));
                }
                else
                moneyTV.setText(moneyTV.getText().toString()+"2");
                break;
            case R.id.num3:
                if(expressionOrNot)
                {
                    outPutExpression.append("3");
                    moneyTV.setText(Calculate.calculate(Transfer.transfer(Transfer.toArray(outPutExpression.getText().toString()))));
                }
                else
                moneyTV.setText(moneyTV.getText().toString()+"3");
                break;
            case R.id.num4:
                if(expressionOrNot)
                {
                    outPutExpression.append("4");
                    moneyTV.setText(Calculate.calculate(Transfer.transfer(Transfer.toArray(outPutExpression.getText().toString()))));
                }
                else
                moneyTV.setText(moneyTV.getText().toString()+"4");
                break;
            case R.id.num5:
                if(expressionOrNot)
                {
                    outPutExpression.append("5");
                    moneyTV.setText(Calculate.calculate(Transfer.transfer(Transfer.toArray(outPutExpression.getText().toString()))));
                }
                else
                moneyTV.setText(moneyTV.getText().toString()+"5");
                break;
            case R.id.num6:
                if(expressionOrNot)
                {
                    outPutExpression.append("6");
                    moneyTV.setText(Calculate.calculate(Transfer.transfer(Transfer.toArray(outPutExpression.getText().toString()))));
                }
                else
                moneyTV.setText(moneyTV.getText().toString()+"6");
                break;
            case R.id.num7:
                if(expressionOrNot)
                {
                    outPutExpression.append("7");
                    moneyTV.setText(Calculate.calculate(Transfer.transfer(Transfer.toArray(outPutExpression.getText().toString()))));
                }
                else
                moneyTV.setText(moneyTV.getText().toString()+"7");
                break;
            case R.id.num8:
                if(expressionOrNot)
                {
                    outPutExpression.append("8");
                    moneyTV.setText(Calculate.calculate(Transfer.transfer(Transfer.toArray(outPutExpression.getText().toString()))));
                }
                else
                moneyTV.setText(moneyTV.getText().toString()+"8");
                break;
            case R.id.num9:
                if(expressionOrNot)
                {
                    outPutExpression.append("9");
                    moneyTV.setText(Calculate.calculate(Transfer.transfer(Transfer.toArray(outPutExpression.getText().toString()))));
                }
                else
                moneyTV.setText(moneyTV.getText().toString()+"9");
                break;
            case R.id.num0:
                if(expressionOrNot)
                {
                    outPutExpression.append("0");
                    moneyTV.setText(Calculate.calculate(Transfer.transfer(Transfer.toArray(outPutExpression.getText().toString()))));
                }
                else
                moneyTV.setText(moneyTV.getText().toString()+"0");
                break;
            case R.id.numPoint:
                if(expressionOrNot&&pointAvailable)
                {
                    outPutExpression.append(".");
                }
                else if (!expressionOrNot&&pointAvailable)
                moneyTV.setText(moneyTV.getText().toString()+".");
                pointAvailable=false;
                break;
            case R.id.numBack:
                pointAvailable=true;
                if(expressionOrNot)
                {
                    int length=outPutExpression.getText().toString().length();
                    if(length>1)//表达式长度大于等于2，去掉最后一个字符后，如果最后一个字符为数字，计算表达式，否则再去掉一个字符后计算
                    {
                        outPutExpression.setText(outPutExpression.getText().toString().substring(0,length-1));
                        int i=outPutExpression.getText().toString().length();
                        if(outPutExpression.getText().toString().charAt(i-1)!='+'&&outPutExpression.getText().toString().charAt(i-1)!='-')
                            moneyTV.setText(Calculate.calculate(Transfer.transfer(Transfer.toArray(outPutExpression.getText().toString()))));
                        else
                            moneyTV.setText(Calculate.calculate(Transfer.transfer(Transfer.toArray(outPutExpression.getText().toString().substring(0,i-1)))));
                    }
                    else if(length==1)
                    {
                        outPutExpression.setText("");
                        moneyTV.setText("");
                        expressionOrNot=false;
                    }
                } else//不是表达式
                    {
                        int length= moneyTV.getText().toString().length();
                        if(length>0)//长度大于0才删减
                            moneyTV.setText(moneyTV.getText().toString().substring(0,length-1));
                    }

                break;

            case R.id.numPlus:
                if(expressionOrNot==false)
                {
                    outPutExpression.setText(moneyTV.getText().toString());
                    expressionOrNot=true;
                }
                outPutExpression.setText(outPutExpression.getText()+"+");
                pointAvailable=true;
                break;

            case R.id.numMinus:
                if(expressionOrNot==false)
                {
                    outPutExpression.setText(moneyTV.getText().toString());
                    expressionOrNot=true;
                }
                outPutExpression.setText(outPutExpression.getText()+"-");
                pointAvailable=true;
                break;

            case R.id.numOk:
                if(!moneyTV.getText().toString().equals("")&&Float.valueOf(moneyTV.getText().toString())>0)
                {
                    saveMoneyData();
                    //changeTotal();
                    changeMonthData();
                    if(isEdit) {
                        itemDelete();
                        setResult(1);
                    }
                    finish();

                }
                else Toast.makeText(this,"请输入有效金额",Toast.LENGTH_SHORT).show();
                break;
            case R.id.numOneMore:
                if(!moneyTV.getText().toString().equals("")&&Float.valueOf(moneyTV.getText().toString())>0)
                {
                    saveMoneyData();
                    Toast.makeText(this,"记录已添加，再记一笔吧",Toast.LENGTH_SHORT).show();
                    changeMonthData();
                    if(isEdit) {
                        itemDelete();
                    }
                    moneyTV.setText("");
                    instruction.setText("");
                }
                else Toast.makeText(this,"请输入有效金额",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setNoSelected()
    {
        eatting.setSelected(false);
        transportation.setSelected(false);
        purchase.setSelected(false);
        house.setSelected(false);
        entertainment.setSelected(false);
        hospital.setSelected(false);
        education.setSelected(false);
        friend.setSelected(false);
        other.setSelected(false);
        accident.setSelected(false);

        input_salary.setSelected(false);
        input_redParket.setSelected(false);
        input_livingExpense.setSelected(false);
        input_bonus.setSelected(false);
        input_reimburse.setSelected(false);
        input_parttime.setSelected(false);
        input_borrow.setSelected(false);
        input_invest.setSelected(false);
        input_accidentMoney.setSelected(false);
        input_other.setSelected(false);

    }

    private void saveMoneyData()
    {
        //修改个位数时间为2位数
        String month=mMonth<10?"0"+Integer.toString(mMonth):Integer.toString(mMonth);
        String year=Integer.toString(mYear);
        String day=mDay<10?"0"+Integer.toString(mDay):Integer.toString(mDay);
        String hour=mHour<10?"0"+Integer.toString(mHour):Integer.toString(mHour);
        String minute=mMinute<10?"0"+Integer.toString(mMinute):Integer.toString(mMinute);
        String money;
        if(isInOrOut)
            money="+"+ moneyTV.getText().toString();
        else money="-"+moneyTV.getText().toString();

        SQLiteDatabase db= recordItemHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(RecordItemData.instruction,instruction.getText().toString());
        values.put(RecordItemData.type,type);
        values.put(RecordItemData.money,money);
        values.put(RecordItemData.time,Long.valueOf(year+month+day+hour+minute));
        db.insert(RecordItemData.MoneyTable,null,values);
        db.close();
    }

    /*private void changeTotal()
    {
        String type=outPutTypeTV.getText().toString();
        SharedPreferences sp=addMoneyRecord.this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        float num=sp.getFloat(type,0)+Float.valueOf(moneyTV.getText().toString());
        editor.putFloat(type,num);
        editor.commit();
    }*/

    //改月表信息
    private void changeMonthData()
    {
        SQLiteDatabase db=monthDataHelper.getWritableDatabase();

        if(isEdit)//判断是否编辑，是的话先减去原纪录金额
        {
            Cursor cursor=db.query("month_data",null,"time=?",new String[]{preYear+preMonth},null,null,null);
            ContentValues values=new ContentValues();
            cursor.moveToFirst();
            Double preMoney=Double.valueOf(cursor.getString(preType));
            values.put(typeName[preType],Double.toString(preMoney-previousMonney));
            if(preType<=MonthData.ACCIDENT)
            {
                Double preAll=Double.valueOf(cursor.getString(MonthData.OUT_ALL));
                values.put("outputall",Double.toString(preAll-previousMonney));
            }
            else{
                Double preAll=Double.valueOf(cursor.getString(MonthData.IN_ALL));
                values.put("incomeall",Double.toString(preAll-previousMonney));
            }

            db.update(MonthData.monthTable,values,"time=?",new String[]{preYear+preMonth});
        }


        String year=Integer.toString(mYear);
        String month=mMonth<10?"0"+Integer.toString(mMonth):Integer.toString(mMonth);
        Cursor cursor=db.query("month_data",null,"time=?",new String[]{year+month},null,null,null);
        ContentValues values=new ContentValues();



        double m;
        if(cursor.getCount()==0)//如果没有该月信息，则添加一条新的月记录
        {
            Toast.makeText(this,"iwnidj",Toast.LENGTH_SHORT).show();
            values.put("time",year+month);
            if(!isInOrOut)
                m=-Double.valueOf(moneyTV.getText().toString());//支出取负数
            else m=Double.valueOf(moneyTV.getText().toString());
            values.put(typeName[type], m);
            if(preType<=MonthData.ACCIDENT)
            {
                values.put("outputall","-"+moneyTV.getText().toString());
            }
            else{
                values.put("incomeall",moneyTV.getText().toString());
            }
            db.insert(MonthData.monthTable,null,values);
        }
        else{
            cursor.moveToFirst();
            String preMoney=cursor.getString(type);

            if(!isInOrOut)
                m=-Double.valueOf(moneyTV.getText().toString());//支出取负数
            else m=Double.valueOf(moneyTV.getText().toString());

            double money=(preMoney==null?m:Double.valueOf(cursor.getString(type))+m);

            values.put(typeName[type],money);

            if(type<=MonthData.ACCIDENT)
            {
                String previousAll=cursor.getString(MonthData.OUT_ALL);
                Double preAll=previousAll==null?0:Double.valueOf(cursor.getString(MonthData.OUT_ALL));
                values.put("outputall",preAll-Double.valueOf(moneyTV.getText().toString()));
            }
            else{
                String previousAll=cursor.getString(MonthData.IN_ALL);
                Double preAll=previousAll==null?0:Double.valueOf(cursor.getString(MonthData.IN_ALL));
                values.put("incomeall",preAll+Double.valueOf(moneyTV.getText().toString()));
            }
            db.update(MonthData.monthTable,values,"time=?",new String[]{year+month});
        }
        cursor.close();
        db.close();
    }

    private void itemDelete()
    {
        SQLiteDatabase itemdb= recordItemHelper.getWritableDatabase();
        itemdb.delete(RecordItemData.MoneyTable,"_id=?",new String[]{id});
        itemdb.close();
    }

    //获得从查看账本页面传过来的记录信息
    private void getMyIntent()
    {
            Intent intent=getIntent();
            String time = intent.getStringExtra("time");
            if(!time.equals(""))//如果不是添加记录，而是编辑
            {
                isEdit=true;
                //获得时间并设置
            preYear=time.substring(0,4);
            preMonth= time.substring(4, 6);
            String day = time.substring(6, 8);
            String hour = time.substring(8, 10);
            String minute = time.substring(10, 12);
            mYear = Integer.valueOf(preYear);
            mMonth = Integer.valueOf(preMonth);
            mDay = Integer.valueOf(day);
            mHour = Integer.valueOf(hour);
            mMinute = Integer.valueOf(minute);

            //获得金额等信息
                double moneyGet=Double.valueOf(intent.getStringExtra("money"));
            moneyTV.setText(Double.toString(Math.abs(moneyGet)));
            previousMonney=Double.valueOf(intent.getStringExtra("money"));
            instruction.setText(intent.getStringExtra("ins"));
            id=intent.getStringExtra("id");
            preType=Integer.valueOf(intent.getStringExtra("type"));

            //根据获得的类型设置页面状态、Type
            switch (preType)
                {
                    case 3:eatting.performClick();
                    break;
                    case 4:transportation.performClick();
                    break;
                    case 5:purchase.performClick();
                    break;
                    case 6:house.performClick();
                    break;
                    case 7:entertainment.performClick();
                    break;
                    case 9:hospital.performClick();
                    break;
                    case 8:education.performClick();
                    break;
                    case 10:friend.performClick();
                    break;
                    case 11:other.performClick();
                    break;
                    case 12:accident.performClick();
                    break;
                }

            /*if(typeS.equals("餐饮"))
            {
                eatting.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.light_orange));
                type= MonthData.EATTING;

            }
            else if(typeS.equals("交通"))
            {
                transportation.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.dark_green));
                type= MonthData.TRANSPORTATION;
            }
            else if(typeS.equals("购物"))
            {
                purchase.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.dark_red));
                type= MonthData.PURCHASE;
            }
            else if(typeS.equals("居住"))
            {
                house.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.light_red));
                type= MonthData.HOUSE;
            }
            else if(typeS.equals("娱乐"))
            {
                entertainment.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.light_blue));
                type= MonthData.ENTERTAIMENT;
            }
            else if(typeS.equals("教育"))
            {
                education.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.light_green));
                type= MonthData.EDUCATION;
            }
            else if(typeS.equals("医疗"))
            {
                hospital.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.dark_red));
                type= MonthData.HOSPITAL;
            }
            else if(typeS.equals("其他"))
            {
                other.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.light_gray));
                type= MonthData.OTHER;
            }
            else if(typeS.equals("意外"))
            {
                accident.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.dark_blue));
                type= MonthData.ACCIDENT;
            }
            else if(typeS.equals("娱乐"))
            {
                entertainment.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.light_blue));
                type= MonthData.ENTERTAIMENT;
            }
            else if(typeS.equals("人情"))
            {
                friend.setSelected(true);
                outPutTypeBack.setBackgroundColor(getResources().getColor(R.color.dark_orange));
                type= MonthData.FRIEND;
            }*/

        }
    }
}
