package zx31401425.zucc.mycurrencies;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    private List<Data> dataList = new ArrayList<Data>();
    private List<String> list1 = new ArrayList<String>();
    private List<String> list2 = new ArrayList<String>();
    private Spinner mySpinner1;
    private Spinner mySpinner2;
    private ArrayAdapter<String> adapter1;
    private ArrayAdapter<String> adapter2;
    String s1 = "all";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        dbHelper = new MyDatabaseHelper(this, "DateBase", null, 1);  //第二个参数为数据库名
        dbHelper.getWritableDatabase();//创建数据库
        final DataAdapter apdater = new DataAdapter(RecordActivity.this, R.layout.data_item, dataList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(apdater);

        init();//数据库中导入数据到LIST中
        apdater.notifyDataSetChanged(); //更新LIST
        init2();//更新下拉列表

        mySpinner1 = (Spinner) findViewById(R.id.Spinner_foregin);
        adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner1.setAdapter(adapter1);
        mySpinner1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                TextView tv = (TextView)arg1;
                tv.setTextColor(getResources().getColor(R.color.white));    //设置颜色

                tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);   //设置居中

                Toast.makeText(RecordActivity.this, ("您选择的是：" + adapter1.getItem(arg2)), Toast.LENGTH_LONG).show();
                if(!adapter1.getItem(arg2).equals("all")){
                    s1=adapter1.getItem(arg2);
                    init3(adapter1.getItem(arg2));
                    mySpinner2.setAdapter(adapter2);
                    checkforegin( adapter1.getItem(arg2));
                    apdater.notifyDataSetChanged();
                }
                else{
                    init();
                    apdater.notifyDataSetChanged();
                }
                /* 将mySpinner 显示*/
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                arg0.setVisibility(View.VISIBLE);
            }
        });

        mySpinner2 = (Spinner) findViewById(R.id.Spinner_home);
        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner2.setAdapter(adapter2);
        mySpinner2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                TextView tv = (TextView)arg1;
                tv.setTextColor(getResources().getColor(R.color.white));    //设置颜色

                tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);   //设置居中

                Toast.makeText(RecordActivity.this, ("您选择的是：" + adapter2.getItem(arg2)), Toast.LENGTH_LONG).show();
                /* 将mySpinner 显示*/
                if(!adapter2.getItem(arg2).equals("all")){
                    checkhome2(s1,adapter2.getItem(arg2));
                    apdater.notifyDataSetChanged();
                }
                else {
                    checkhome1(s1);
                    apdater.notifyDataSetChanged();
                }


                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                arg0.setVisibility(View.VISIBLE);
            }
        });


    }

    private void init2() {
        list1.clear();
        list1.add("all");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(true, "datebase", new String[]{"foregin"}, null, null, null, null, null, null);
        Data a;
        if (cursor.moveToFirst()) {
            do {
                String foregin = cursor.getString(cursor.getColumnIndex("foregin"));
                list1.add(foregin);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void checkforegin(String s) {
        dataList.clear();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("datebase",null ,"foregin = ?",new String[]{s}, null, null, null);
        Data a;
        if (cursor.moveToFirst()) {
            do {
                String home = cursor.getString(cursor.getColumnIndex("home"));
                double num = cursor.getDouble(cursor.getColumnIndex("num"));
                double forenum = cursor.getDouble(cursor.getColumnIndex("forenum"));
                a = new Data(s, home,forenum,num);
                dataList.add(a);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void init3(String s) {       //添加下拉列表
        list2.clear();
        list2.add("all");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(true, "datebase", new String[]{"home"}, "foregin = ?", new String[]{s}, null, null, null, null);
        Data a;
        if (cursor.moveToFirst()) {
            do {
                String home = cursor.getString(cursor.getColumnIndex("home"));
                list2.add(home);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void init() {
        dataList.clear();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("datebase", null, null, null, null, null, null);
        Data a;
        if (cursor.moveToFirst()) {
            do {
                String foregin = cursor.getString(cursor.getColumnIndex("foregin"));
                String home = cursor.getString(cursor.getColumnIndex("home"));
                double forenum = cursor.getDouble(cursor.getColumnIndex("forenum"));
                double num = cursor.getDouble(cursor.getColumnIndex("num"));
                a = new Data(foregin, home, forenum,num);
                dataList.add(a);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void checkhome1(String s1) {
        dataList.clear();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("datebase",null ,"foregin = ?",new String[]{s1}, null, null, null);
        Data a;
        if (cursor.moveToFirst()) {
            do {
                String home = cursor.getString(cursor.getColumnIndex("home"));
                double num = cursor.getDouble(cursor.getColumnIndex("num"));
                double forenum = cursor.getDouble(cursor.getColumnIndex("forenum"));
                a = new Data(s1, home,forenum,num);
                dataList.add(a);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void checkhome2(String s1,String s) {
        dataList.clear();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("datebase",null ,"foregin = ? and home = ?",new String[]{s1,s}, null, null, null);
        Data a;
        if (cursor.moveToFirst()) {
            do {
                double num = cursor.getDouble(cursor.getColumnIndex("num"));
                double forenum = cursor.getDouble(cursor.getColumnIndex("forenum"));
                a = new Data(s1, s,forenum,num);
                dataList.add(a);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}

