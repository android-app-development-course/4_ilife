package com.example.administrator.ilife_manager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class Chart extends AppCompatActivity {

    TabHost chartTab;
    View pieView, lineView;
    List<View> list;
    ViewPager viewPager;
    Button pieBtn, lineBtn;
    PieChartView outPie;
    PieChartView inPie;
    ListView outListView,inListView;
    MonthData helper = new MonthData(this);
    List<String> yearArr = new ArrayList<String>();
    List<String> monthArr = new ArrayList<String>();
    List<TypeData> outputList = new ArrayList<TypeData>();
    List<TypeData> incomeList = new ArrayList<TypeData>();
    Spinner yearSpin;
    Spinner monthSpin;
    String eatN,tranN,eduN,hosN,enterN,houseN,outputOtherN,purN,accN,friendN;
    String salaryN, redpacketN, livingexpenseN, bonusN, reimburseN, parttimeN, borrowN,investN,accidentmoneyN, inputotherN;
    String salaryPer, redpacketPer, livingexpensePer, bonusPer, reimbursePer, parttimePer, borrowPer,investPer,accidentmoneyPer, inputotherPer;
    String eatPer, transPer, eduPer, enterPer, hosPer, housePer, otherPer, purPer, friendPer, acciPer;
    float outputAll=0,inputAll=0;


    private boolean hasLabels = true;                   //是否有标语
    private boolean hasLabelsOutside = true;           //扇形外面是否有标语
    private boolean hasCenterCircle = false;            //是否有中心圆
    //private boolean hasCenterText1 = false;             //是否有中心的文字
    //private boolean hasCenterText2 = false;             //是否有中心的文字2
    //private boolean isExploded = true;                  //是否是炸开的图像
    private boolean hasLabelForSelected = false;         //选中的扇形显示标语

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);
        init();
        initSpinner();
        initListener();


        //获得系统时间
        final Calendar ca = Calendar.getInstance();
        int mYear = ca.get(Calendar.YEAR);
        int mMonth = ca.get(Calendar.MONTH);//得到实际月份-1
        String time=Integer.toString(mYear)+Integer.toString(mMonth);

        yearSpin.setSelection(mYear-2018);
        monthSpin.setSelection(mMonth);




    }
    private void initSpinner()
    {

        final Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH);//得到实际月份-1
        for(int i=2018;i<=year;i++)
            yearArr.add(Integer.toString(year)+"年");
        monthArr.add("01月");
        monthArr.add("02月");
        monthArr.add("03月");
        monthArr.add("04月");
        monthArr.add("05月");
        monthArr.add("06月");
        monthArr.add("07月");
        monthArr.add("08月");
        monthArr.add("09月");
        monthArr.add("10月");
        monthArr.add("11月");
        monthArr.add("12月");

        monthSpin = (Spinner) findViewById(R.id.monthSpin);
        yearSpin = (Spinner) findViewById(R.id.yearSpin);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, yearArr);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, monthArr);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        yearSpin.setAdapter(yearAdapter);
        monthSpin.setAdapter(monthAdapter);


    }

    private void init() {

        viewPager = findViewById(R.id.chartViewPager);
        LayoutInflater inflater = getLayoutInflater();
        pieView = inflater.inflate(R.layout.pie_view, null);
        lineView = inflater.inflate(R.layout.line_view, null);
        list = new ArrayList<View>();
        list.add(pieView);
        list.add(lineView);
        viewPager.setAdapter(pagerAdapter);

        outPie = (PieChartView) pieView.findViewById(R.id.outputPie);
        inPie=(PieChartView)pieView.findViewById(R.id.inputPie) ;
        chartTab = pieView.findViewById(R.id.chartTab);
        chartTab.setup();
        chartTab.addTab(chartTab.newTabSpec("支出").setIndicator("支出").setContent(R.id.chartOut));
        chartTab.addTab(chartTab.newTabSpec("收入").setIndicator("收入").setContent(R.id.chartIn));


        pieBtn = (Button) findViewById(R.id.pie);
        lineBtn = (Button) findViewById(R.id.line);


        outListView = (ListView) pieView.findViewById(R.id.outPutListView);
        inListView=(ListView)pieView.findViewById(R.id.incomeListView);
    }

    private void initListener() {
        pieBtn.setSelected(true);

        monthSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String year=((String) yearSpin.getSelectedItem()).substring(0,4);
                String month=monthArr.get(i).substring(0,2);
                String time=year+month;
                makeOutputPie(time);
                makeIncomePie(time);
                makeOutputTypeArray();
                makeInputTypeArray();
                if(outputAll!=0)

                    outListView.setAdapter(new outListAdapter());
                else
                    outListView.setAdapter(null);
                if(inputAll!=0)
                    inListView.setAdapter(new inListAdapter());
                else
                    inListView.setAdapter(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        yearSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String month=((String) monthSpin.getSelectedItem()).substring(0,2);
                String year=yearArr.get(i).substring(0,4);
                String time=year+month;
                makeOutputPie(time);
                makeIncomePie(time);
                makeOutputTypeArray();
                makeInputTypeArray();
                if(outputAll!=0)
                outListView.setAdapter(new outListAdapter());
                else
                    outListView.setAdapter(null);
                if(inputAll!=0)
                inListView.setAdapter(new inListAdapter());
                else
                    inListView.setAdapter(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    PagerAdapter pagerAdapter = new PagerAdapter() {

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

    private void makeOutputPie(String month) {

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(MonthData.monthTable, null, "time=?", new String[]{month}, null, null, null);
        if (cursor.getCount() == 0)
        {
            outputAll=0;
            outPie.setPieChartData(null);
        }

        else {
            java.text.DecimalFormat myformat = new java.text.DecimalFormat("0.0");
            cursor.moveToFirst();
            String eatting = cursor.getString(MonthData.EATTING);
            String trans = cursor.getString(MonthData.TRANSPORTATION);
            String edu = cursor.getString(MonthData.EDUCATION);
            String hospital = cursor.getString(MonthData.HOSPITAL);
            String entertaiment = cursor.getString(MonthData.ENTERTAIMENT);
            String house = cursor.getString(MonthData.HOUSE);
            String other = cursor.getString(MonthData.OTHER);
            String accident = cursor.getString(MonthData.ACCIDENT);
            String purchase = cursor.getString(MonthData.PURCHASE);
            String friend = cursor.getString(MonthData.FRIEND);
            eatN = eatting == null ? "0" : myformat.format(Float.valueOf(eatting));
            tranN = trans == null ? "0" : myformat.format(Float.valueOf(trans));
            eduN = edu == null ? "0" : myformat.format(Float.valueOf(edu));
            hosN = hospital == null ? "0" : myformat.format(Float.valueOf(hospital));
            enterN = entertaiment == null ? "0" : myformat.format(Float.valueOf(entertaiment));
            houseN = house == null ? "0" : myformat.format(Float.valueOf(house));
            outputOtherN = other == null ? "0" : myformat.format(Float.valueOf(other));
            purN = purchase == null ? "0" : myformat.format(Float.valueOf(purchase));
            accN = accident == null ? "0" : myformat.format(Float.valueOf(accident));
            friendN = friend == null ? "0" : myformat.format(Float.valueOf(friend));
            outputAll = Float.valueOf(eatN) + Float.valueOf(tranN) + Float.valueOf(eduN) + Float.valueOf(hosN) + Float.valueOf(enterN) + Float.valueOf(houseN) + Float.valueOf(outputOtherN) + Float.valueOf(purN) + Float.valueOf(accN) + Float.valueOf(friendN);
            PieChartData data;         //存放数据
            List<SliceValue> values = new ArrayList<SliceValue>();
            eatPer = myformat.format(Float.valueOf(eatN) / outputAll * 100) + "%";
            SliceValue eatVal = new SliceValue(Float.valueOf(eatN), getResources().getColor(R.color.light_orange));
            eatVal.setLabel("餐饮：" + eatPer);
            transPer = myformat.format(Float.valueOf(tranN) / outputAll * 100) + "%";
            SliceValue transVal = new SliceValue(Float.valueOf(tranN), getResources().getColor(R.color.dark_green));
            transVal.setLabel("交通" + transPer);
            SliceValue eduVal = new SliceValue(Float.valueOf(eduN), getResources().getColor(R.color.light_green));
            eduPer = myformat.format(Float.valueOf(eduN) / outputAll * 100) + "%";
            eduVal.setLabel("教育" + eduPer);
            SliceValue enterVal = new SliceValue(Float.valueOf(enterN), getResources().getColor(R.color.light_blue));
            enterPer = myformat.format(Float.valueOf(enterN) / outputAll * 100) + "%";
            enterVal.setLabel("娱乐" + enterPer);
            SliceValue hosVal = new SliceValue(Float.valueOf(hosN), getResources().getColor(R.color.light_red));
            hosPer = myformat.format(Float.valueOf(hosN) / outputAll * 100) + "%";
            hosVal.setLabel("医疗" + hosPer);
            SliceValue houseVal = new SliceValue(Float.valueOf(houseN), getResources().getColor(R.color.light_red));
            housePer = myformat.format(Float.valueOf(houseN) / outputAll * 100) + "%";
            houseVal.setLabel("居住" + housePer);
            SliceValue otherVal = new SliceValue(Float.valueOf(outputOtherN), getResources().getColor(R.color.light_gray));
            otherPer = myformat.format(Float.valueOf(outputOtherN) / outputAll * 100) + "%";
            otherVal.setLabel("其他" + otherPer);
            SliceValue purVal = new SliceValue(Float.valueOf(purN), getResources().getColor(R.color.dark_red));
            purPer = myformat.format(Float.valueOf(purN) / outputAll * 100) + "%";
            purVal.setLabel("购物" + purPer);
            SliceValue friendVal = new SliceValue(Float.valueOf(friendN), getResources().getColor(R.color.dark_orange));
            friendPer = myformat.format(Float.valueOf(friendN) / outputAll * 100) + "%";
            friendVal.setLabel("人情" + friendPer);
            SliceValue acciVal = new SliceValue(Float.valueOf(accN), getResources().getColor(R.color.dark_blue));
            acciPer = myformat.format(Float.valueOf(accN) / outputAll * 100) + "%";
            acciVal.setLabel("意外" + acciPer);


            if (Float.valueOf(eatN) != 0) values.add(eatVal);
            if (Float.valueOf(tranN) != 0) values.add(transVal);
            if (Float.valueOf(eduN) != 0) values.add(eduVal);
            if (Float.valueOf(hosN) != 0) values.add(hosVal);
            if (Float.valueOf(enterN) != 0) values.add(enterVal);
            if (Float.valueOf(houseN) != 0) values.add(houseVal);
            if (Float.valueOf(outputOtherN) != 0) values.add(otherVal);
            if (Float.valueOf(purN) != 0) values.add(purVal);
            if (Float.valueOf(friendN) != 0) values.add(friendVal);
            if (Float.valueOf(accN) != 0) values.add(acciVal);


            data = new PieChartData(values);
            data.setHasLabels(hasLabels);
            data.setHasLabelsOnlyForSelected(hasLabelForSelected);
            data.setHasLabelsOutside(hasLabelsOutside);
            data.setHasCenterCircle(!hasCenterCircle);
            data.setSlicesSpacing(5);
            data.setCenterText1(Float.toString(-outputAll));
            data.setCenterText2("总支出");
            data.setCenterCircleScale(0.8f);

            outPie.setCircleFillRatio(0.65f);
            outPie.setPieChartData(data);

        }
        cursor.close();
        db.close();

    }

    private void makeIncomePie(String month) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(MonthData.monthTable, null, "time=?", new String[]{month}, null, null, null);
        if (cursor.getCount() == 0)
        {
            inputAll=0;
            inPie.setPieChartData(null);
        }

        else {
            java.text.DecimalFormat myformat = new java.text.DecimalFormat("0.0");
            cursor.moveToFirst();
            String salary = cursor.getString(MonthData.SALARY);
            String redpacket = cursor.getString(MonthData.REDPACKET);
            String livingexpense= cursor.getString(MonthData.LIVINGEXPENSE);
            String bonus= cursor.getString(MonthData.BONUS);
            String reimburse = cursor.getString(MonthData.REIMBURSE);
            String parttime = cursor.getString(MonthData.PARTTIME);
            String borrow = cursor.getString(MonthData.BORROW);
            String invest = cursor.getString(MonthData.INVEST);
            String accidentmoney = cursor.getString(MonthData.ACCIDENTMONEY);
            String inputOther = cursor.getString(MonthData.INPUT_OTHER);
            salaryN = salary == null ? "0" : myformat.format(Float.valueOf(salary));
            redpacketN = redpacket == null ? "0" : myformat.format(Float.valueOf(redpacket));
            livingexpenseN = livingexpense == null ? "0" : myformat.format(Float.valueOf(livingexpense));
            bonusN = bonus == null ? "0" : myformat.format(Float.valueOf(bonus));
            reimburseN =reimburse == null ? "0" : myformat.format(Float.valueOf(reimburse));
            parttimeN = parttime == null ? "0" : myformat.format(Float.valueOf(parttime));
            borrowN = borrow == null ? "0" : myformat.format(Float.valueOf(borrow));
            investN = invest == null ? "0" : myformat.format(Float.valueOf(invest));
            accidentmoneyN= accidentmoney == null ? "0" : myformat.format(Float.valueOf(accidentmoney));
            inputotherN= inputOther == null ? "0" : myformat.format(Float.valueOf(inputOther));
            inputAll = Float.valueOf(salaryN) + Float.valueOf(redpacketN) + Float.valueOf(livingexpenseN) + Float.valueOf(bonusN) + Float.valueOf(reimburseN) + Float.valueOf(parttimeN) + Float.valueOf(borrowN) + Float.valueOf(investN) + Float.valueOf(accidentmoneyN) + Float.valueOf(inputotherN);
            PieChartData data;         //存放数据
            List<SliceValue> values = new ArrayList<SliceValue>();
            salaryPer = myformat.format(Float.valueOf(salaryN) / inputAll * 100) + "%";
            SliceValue salaryVal = new SliceValue(Float.valueOf(salaryN), getResources().getColor(R.color.light_orange));
            salaryVal.setLabel("工资：" + salaryPer);
            redpacketPer = myformat.format(Float.valueOf(redpacketN) / inputAll * 100) + "%";
            SliceValue redpacketVal = new SliceValue(Float.valueOf(redpacketN), getResources().getColor(R.color.dark_red));
            redpacketVal.setLabel("收红包" + redpacketPer);
            SliceValue livingexpenseVal = new SliceValue(Float.valueOf(livingexpenseN), getResources().getColor(R.color.light_orange));
            livingexpensePer = myformat.format(Float.valueOf(livingexpenseN) / inputAll * 100) + "%";
            livingexpenseVal.setLabel("生活费" + livingexpensePer);
            SliceValue bonusVal = new SliceValue(Float.valueOf(bonusN), getResources().getColor(R.color.dark_green));
            bonusPer = myformat.format(Float.valueOf(bonusN) / inputAll * 100) + "%";
            bonusVal.setLabel("奖金" + bonusPer);
            SliceValue reimburseVal = new SliceValue(Float.valueOf(reimburseN), getResources().getColor(R.color.dark_blue));
            reimbursePer = myformat.format(Float.valueOf(reimburseN) / inputAll * 100) + "%";
            reimburseVal.setLabel("报销" + reimbursePer);
            SliceValue parttimeVal = new SliceValue(Float.valueOf(parttimeN), getResources().getColor(R.color.dark_green));
            parttimePer = myformat.format(Float.valueOf(parttimeN) / inputAll * 100) + "%";
            parttimeVal.setLabel("兼职" + parttimePer);
            SliceValue borrowVal = new SliceValue(Float.valueOf(borrowN), getResources().getColor(R.color.light_red));
            borrowPer = myformat.format(Float.valueOf(borrowN) /inputAll * 100) + "%";
            borrowVal.setLabel("借入款" + borrowPer);
            SliceValue investVal = new SliceValue(Float.valueOf(investN), getResources().getColor(R.color.dark_red));
            investPer = myformat.format(Float.valueOf(investN) / inputAll * 100) + "%";
            investVal.setLabel("投资" + investPer);
            SliceValue accidentmoneyVal = new SliceValue(Float.valueOf(accidentmoneyN), getResources().getColor(R.color.light_gray));
            accidentmoneyPer = myformat.format(Float.valueOf(accidentmoneyN) /inputAll* 100) + "%";
            accidentmoneyVal.setLabel("捡到钱" + accidentmoneyPer);
            SliceValue inputotherVal = new SliceValue(Float.valueOf(inputotherN), getResources().getColor(R.color.black));
            inputotherPer = myformat.format(Float.valueOf(inputotherN) / inputAll * 100) + "%";
            inputotherVal.setLabel("其他" + inputotherPer);


            if (Float.valueOf(salaryN) != 0) values.add(salaryVal);
            if (Float.valueOf(redpacketN) != 0) values.add(redpacketVal);
            if (Float.valueOf(livingexpenseN) != 0) values.add(livingexpenseVal);
            if (Float.valueOf(bonusN) != 0) values.add(bonusVal);
            if (Float.valueOf(reimburseN) != 0) values.add(reimburseVal);
            if (Float.valueOf(parttimeN) != 0) values.add(parttimeVal);
            if (Float.valueOf(borrowN) != 0) values.add(borrowVal);
            if (Float.valueOf(investN) != 0) values.add(investVal);
            if (Float.valueOf(accidentmoneyN) != 0) values.add(accidentmoneyVal);
            if (Float.valueOf(inputotherN) != 0) values.add(inputotherVal);


            data = new PieChartData(values);
            data.setHasLabels(hasLabels);
            data.setHasLabelsOnlyForSelected(hasLabelForSelected);
            data.setHasLabelsOutside(hasLabelsOutside);
            data.setHasCenterCircle(!hasCenterCircle);
            data.setSlicesSpacing(5);
            data.setCenterText1(Float.toString(inputAll));
            data.setCenterText2("总收入");
            data.setCenterCircleScale(0.8f);

            inPie.setCircleFillRatio(0.65f);
            inPie.setPieChartData(data);
            inPie.setAnimation(new Animation() {
                @Override
                protected Animation clone() throws CloneNotSupportedException {
                    return super.clone();
                }
            });

        }
        cursor.close();
        db.close();



    }



    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pie:
                viewPager.setCurrentItem(0);
                break;
            case R.id.line:
                viewPager.setCurrentItem(1);
                break;
        }
    }

    class outListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return outputList.size();
        }

        @Override
        public Object getItem(int i) {
            return outputList.get(i).type;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView = null;
            TypeData data = outputList.get(i);
            if (data.type.equals("餐饮")) {
                myView = View.inflate(Chart.this, R.layout.type_eatting_item, null);
            } else if (data.type.equals("交通"))
                myView = View.inflate(Chart.this, R.layout.type_trans_item, null);
            else if (data.type.equals("购物"))
                myView = View.inflate(Chart.this, R.layout.type_pur_item, null);
            else if (data.type.equals("居住"))
                myView = View.inflate(Chart.this, R.layout.type_house_item, null);
            else if (data.type.equals("交通"))
                myView = View.inflate(Chart.this, R.layout.type_trans_item, null);
            else if (data.type.equals("娱乐"))
                myView = View.inflate(Chart.this, R.layout.type_enter_item, null);
            else if (data.type.equals("医疗"))
                myView = View.inflate(Chart.this, R.layout.type_hos_item, null);
            else if (data.type.equals("教育"))
                myView = View.inflate(Chart.this, R.layout.type_edu_item, null);
            else if (data.type.equals("人情"))
                myView = View.inflate(Chart.this, R.layout.type_friend_item, null);
            else if (data.type.equals("其他"))
                myView = View.inflate(Chart.this, R.layout.type_other_item, null);
            else if (data.type.equals("意外"))
                myView = View.inflate(Chart.this, R.layout.type_acci_item, null);


            TextView typeName = myView.findViewById(R.id.typeName);
            TextView typeRatio = myView.findViewById(R.id.typeRatio);
            TextView typeTotal = myView.findViewById(R.id.typeTotal);
            typeName.setText(data.type);
            typeRatio.setText(data.ratio);
            typeTotal.setText(data.total.substring(1,data.total.length()));
            return myView;

        }
    }

    class inListAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return incomeList.size();
        }

        @Override
        public Object getItem(int i) {
            return incomeList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView = null;
            TypeData data = incomeList.get(i);
            if (data.type.equals("工资")) {
                myView = View.inflate(Chart.this, R.layout.type_salary_item, null);
            }
            else if (data.type.equals("红包"))
                myView = View.inflate(Chart.this, R.layout.type_read_pakage_item, null);
            else if (data.type.equals("生活费"))
                myView = View.inflate(Chart.this, R.layout.type_life_item, null);
            else if (data.type.equals("奖金"))
                myView = View.inflate(Chart.this, R.layout.type_bonus_item, null);
            else if (data.type.equals("报销"))
                myView = View.inflate(Chart.this, R.layout.type_reimburse_item, null);
            else if (data.type.equals("兼职"))
                myView = View.inflate(Chart.this, R.layout.type_parttime_item, null);
            else if (data.type.equals("借入款"))
                myView = View.inflate(Chart.this, R.layout.type_borrow_item, null);
            else if (data.type.equals("投资"))
                myView = View.inflate(Chart.this, R.layout.type_invest_item, null);
            else if (data.type.equals("捡到钱"))
                myView = View.inflate(Chart.this, R.layout.type_pickup_money_item, null);
            else if (data.type.equals("其他"))
                myView = View.inflate(Chart.this, R.layout.type_other_item, null);

            TextView typeName = myView.findViewById(R.id.typeName);
            TextView typeRatio = myView.findViewById(R.id.typeRatio);
            TextView typeTotal = myView.findViewById(R.id.typeTotal);
            typeName.setText(data.type);
            typeRatio.setText(data.ratio);
            typeTotal.setText(data.total);
            return myView;
        }
    }

    class TypeData {
        public TypeData(String total, String ratio, String type) {
            this.total = total;
            this.ratio = ratio;
            this.type = type;
        }

        String total;
        String ratio;
        String type;
    }

    private void makeOutputTypeArray() {

        outputList.clear();
        String type;
        if (Float.valueOf(eatN) != 0) {

            type = "餐饮";
            TypeData t = new TypeData(eatN, eatPer, type);
            outputList.add(t);
        }
        if (Float.valueOf(tranN) != 0) {

            type = "交通";
            TypeData t = new TypeData(tranN, transPer, type);
            outputList.add(t);
        }
        if (Float.valueOf(purN) != 0) {

            type = "购物";
            TypeData t = new TypeData(purN, purPer, type);
            outputList.add(t);
        }
        if (Float.valueOf(houseN) != 0) {

            type = "居住";
            TypeData t = new TypeData(houseN, housePer, type);
            outputList.add(t);
        }
        if (Float.valueOf(enterN) != 0) {
            type = "娱乐";
            TypeData t = new TypeData(enterN, enterPer, type);
            outputList.add(t);
        }
        if (Float.valueOf(hosN) != 0) {
            type = "医疗";
            TypeData t = new TypeData(hosN, hosPer, type);
            outputList.add(t);
        }
        if (Float.valueOf(eduN) != 0) {
            type = "教育";
            TypeData t = new TypeData(eduN, eduPer, type);
            outputList.add(t);
        }
        if (Float.valueOf(friendN) != 0) {
            type = "人情";
            TypeData t = new TypeData(friendN, friendPer, type);
            outputList.add(t);
        }
        if (Float.valueOf(outputOtherN) != 0) {
            type = "其他";
            TypeData t = new TypeData(outputOtherN, otherPer, type);
            outputList.add(t);
        }
        if (Float.valueOf(accN) != 0) {
            type = "意外";
            TypeData t = new TypeData(accN, acciPer, type);
            outputList.add(t);
        }
    }

    private void makeInputTypeArray()
    {
        incomeList.clear();
        String type;
        if (Float.valueOf(salaryN) != 0) {

            type = "工资";
            TypeData t = new TypeData(salaryN, salaryPer, type);
            incomeList.add(t);
        }
        if (Float.valueOf(redpacketN) != 0) {

            type = "红包";
            TypeData t = new TypeData(redpacketN, redpacketPer, type);
            incomeList.add(t);
        }
        if (Float.valueOf(livingexpenseN) != 0) {

            type = "生活费";
            TypeData t = new TypeData(livingexpenseN, livingexpensePer, type);
            incomeList.add(t);
        }
        if (Float.valueOf(bonusN) != 0) {

            type = "奖金";
            TypeData t = new TypeData(bonusN, bonusPer, type);
            incomeList.add(t);
        }
        if (Float.valueOf(reimburseN) != 0) {

            type = "报销";
            TypeData t = new TypeData(reimburseN, reimbursePer, type);
            incomeList.add(t);
        }
        if (Float.valueOf(parttimeN) != 0) {

            type = "兼职";
            TypeData t = new TypeData(parttimeN,parttimePer, type);
            incomeList.add(t);
        }
        if (Float.valueOf(borrowN) != 0) {

            type = "借入款";
            TypeData t = new TypeData(borrowN, borrowPer, type);
            incomeList.add(t);
        }
        if (Float.valueOf(investN) != 0) {

            type = "投资";
            TypeData t = new TypeData(investN, investPer, type);
            incomeList.add(t);
        }
        if (Float.valueOf(accidentmoneyN) != 0) {

            type = "捡到钱";
            TypeData t = new TypeData(accidentmoneyN, accidentmoneyPer, type);
            incomeList.add(t);
        }
        if (Float.valueOf(inputotherN) != 0) {

            type = "其他";
            TypeData t = new TypeData(inputotherN, inputotherPer, type);
            incomeList.add(t);
        }
    }
}
