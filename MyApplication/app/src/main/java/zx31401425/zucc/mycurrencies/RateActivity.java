package zx31401425.zucc.mycurrencies;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        Button rate_button = (Button) findViewById(R.id.button_rate);

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

        CountDownTimer cdt = new CountDownTimer(1000000, 10000) {
            @Override
            public void onTick(long millisUntilFinished) {
                new RateActivity.CurrencyConverterTask().execute(URL_BASE+mKey);
            }
            @Override
            public void onFinish() {

            }
        };

        cdt.start();

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
            progressDialog.setTitle("Calculating Result...");
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
                TextView textView = (TextView)findViewById(R.id.text_rate);
                textView.setText(String.valueOf(dCalculated));
                insert(num,dCalculated);
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
        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));  //   ߵ   ɫ
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);
        line.setCubic(false);
        LineChartValueFormatter chartValueFormatter = new SimpleLineChartValueFormatter(6);
        line.setFormatter(chartValueFormatter);//显示小数点
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
//	    line.setStrokeWidth(3);
        line.setFilled(false);
        line.setHasLabels(true);
//		line.setHasLabelsOnlyForSelected(true);/
        line.setHasLines(true);
        line.setHasPoints(true);
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
