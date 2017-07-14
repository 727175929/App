package zx31401425.zucc.mycurrencies;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import lecho.lib.hellocharts.formatter.LineChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class RateActivity extends AppCompatActivity {
    private TextView textView;
    private String mKey;
    public static final String RATES="rates";
    public static final String URL_BASE =
            "http://openexchangerates.org/api/latest.json?app_id=";
    //used to format data from openexchangerates.org
    private static final DecimalFormat DECIMAL_FORMAT = new
            DecimalFormat("#,##0.00000");
    private LineChartView lineChart;
    int num = 10;
    String[] date = {"1","2","3","4","5","6","7","8","9","10"};//X轴的标注
    double[] score= {6.801939,6.801939,6.801939,6.801939,6.801939,6.801939,6.801939,6.801839,6.8020000,6.801839};//图表的数据点
    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    private int i = 0;
    private int TIME = 20000;
    NotificationManager manager;
    int notification_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        Button rate_button = (Button) findViewById(R.id.button_rate);
        textView = (TextView) findViewById(R.id.text_rate);
        lineChart = (LineChartView)findViewById(R.id.line_chart);
        getAxisXLables();//获取x轴的标注
        getAxisPoints();//获取坐标点
        initLineChart();//初始化


        rate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RateActivity.CurrencyConverterTask().execute(URL_BASE+mKey);

            }
        });
        mKey = getKey("open_key");

        CountDownTimer cdt = new CountDownTimer(1000000, 10000) {  //第一个参数表示总时间，第二个参数表示间隔时间
            @Override
            public void onTick(long millisUntilFinished) {
                new RateActivity.CurrencyConverterTask().execute(URL_BASE+mKey);
            }
            @Override
            public void onFinish() {

            }
        };

        cdt.start();//定时器
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Button change = (Button) findViewById(R.id.button_change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("2");
            }

        });
        textView.addTextChangedListener(textWatcher);
    }
    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            getNotification(String.valueOf(textView.getText()));
        }
    }; //改变的监听器

    void getNotification(String s){
        Notification.Builder builder = new Notification.Builder(RateActivity.this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setTicker("World");
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle("当前汇率");
        builder.setContentText("目前的1美元可以兑换"+s+"人民币");

//        Intent intent = new Intent(RateActivity.this, Activity.class);
//        PendingIntent ma = PendingIntent.getActivity(RateActivity.this,0,intent,0);
//        builder.setContentIntent(ma);//设置点击过后跳转的activity

                /*builder.setDefaults(Notification.DEFAULT_SOUND);//设置声音
                builder.setDefaults(Notification.DEFAULT_LIGHTS);//设置指示灯
                builder.setDefaults(Notification.DEFAULT_VIBRATE);//设置震动*/
        //    提示音，闪光灯，震动效果需要添加权限 : <uses-permission android:name="android.permission.VIBRATE"> </uses-permission>
        builder.setDefaults(Notification.DEFAULT_ALL);//设置全部

        Notification notification = builder.build();//4.1以上用.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;// 点击通知的时候cancel掉
        manager.notify(notification_id,notification);
    }

    private String getKey(String keyName){
        AssetManager assetManager = this.getResources().getAssets();
        Properties properties = new Properties();
        try {
            InputStream inputStream = assetManager.open("keys.properties");
            properties.load(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return  properties.getProperty(keyName);

    }

    private class CurrencyConverterTask extends AsyncTask<String,Void,JSONObject>
    {
        private ProgressDialog progressDialog;
        @Override
        protected JSONObject doInBackground(String... params) {
            return new JSONParser().getJSONFromUrl(params[0]);        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(RateActivity.this);
            progressDialog.setTitle("Getting Result...");
            progressDialog.setMessage("One moment please...");
            progressDialog.setCancelable(true);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                    "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RateActivity.CurrencyConverterTask.this.cancel(true);
                            progressDialog.dismiss();
                        }
                    });
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            double dCalculated = 0.0;
            try {
                if (jsonObject == null){
                    throw new JSONException("no data available.");
                }
                JSONObject jsonRates = jsonObject.getJSONObject(RATES);
                dCalculated = jsonRates.getDouble("CNY") ;
                textView.setText(String.valueOf(dCalculated));
                insert(num,dCalculated);   //插入点和对应的数据
                num++;
            } catch (JSONException e) {
                Toast.makeText(
                        RateActivity.this,
                        "There's been a JSON exception: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
                e.printStackTrace();
            }
            progressDialog.dismiss();

        }
    }

    /**
     * 设置X 轴的显示
     */
    private void getAxisXLables(){
        for (int i = 0; i < date.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(date[i]));
        }
    }
    /**
     * 图表的每个点的显示
     */
    private void getAxisPoints() {
        for (int i = 0; i < score.length; i++) {
            mPointValues.add(new PointValue(i, (float)score[i]));
        }
    }

    private void initLineChart(){
        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));  //设置点的颜色
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);
        line.setCubic(false);   //曲线是否平滑，即是曲线还是折线
        LineChartValueFormatter chartValueFormatter = new SimpleLineChartValueFormatter(6);
        line.setFormatter(chartValueFormatter);//显示小数点
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
//	    line.setStrokeWidth(3);
        line.setFilled(false);//是否填充曲线的面积
        line.setHasLabels(true); //曲线的数据坐标是否加上备注
//		line.setHasLabelsOnlyForSelected(true);/
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);


        Axis axisX = new Axis(); //X
        axisX.setHasTiltedLabels(true);

        axisX.setTextColor(Color.parseColor("#000000"));//


        axisX.setTextSize(11);//
        axisX.setMaxLabelChars(7);
        axisX.setValues(mAxisXValues);
        data.setAxisXBottom(axisX);
//	    data.setAxisXTop(axisX);
        axisX.setHasLines(true);


        Axis axisY = new Axis();
        axisY.setName("");
        axisY.setTextSize(11);
        data.setAxisYLeft(axisY);

        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 3);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);

        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 0;
        v.right= 7;
        lineChart.setCurrentViewport(v);
    }

    private void insert(int i,double s){
        mAxisXValues.add(new AxisValue(i).setLabel(String.valueOf(i+1)));
        mPointValues.add(new PointValue(i, (float)s));
        initLineChart();
    }

}
