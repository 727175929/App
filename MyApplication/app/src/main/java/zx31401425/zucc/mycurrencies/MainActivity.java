package zx31401425.zucc.mycurrencies;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static zx31401425.zucc.mycurrencies.SplashActivity.KEY_ARRAYLIST;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Button mCalcButton;
    private TextView mConvertedTextView;
    private EditText mAmountEditText;
    private Spinner mForSpinner,mHomSpinner;
    private String[] mCurrencies;
    public static final String HOM="HOM_CURRENCY";
    public static final String FOR="FOR_CURRENCY";
    private String mKey;
    public static final String RATES="rates";
    public static final String URL_BASE =
            "http://openexchangerates.org/api/latest.json?app_id=";
    //used to format data from openexchangerates.org
    private static final DecimalFormat DECIMAL_FORMAT = new
            DecimalFormat("#,##0.00000");

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
        setContentView(R.layout.activity_main);
        dbHelper = new MyDatabaseHelper(this, "DateBase", null, 1);  //第二个参数为数据库名
        dbHelper.getWritableDatabase();//创建数据库

        ArrayList<String> arrayList=((ArrayList<String>)
                getIntent().getSerializableExtra(KEY_ARRAYLIST));
        Collections.sort(arrayList);
        mCurrencies=arrayList.toArray(new String[arrayList.size()]);

        mConvertedTextView=(TextView)findViewById(R.id.txt_converted);
        mAmountEditText=(EditText)findViewById(R.id.edt_amount);
        mCalcButton=(Button)findViewById(R.id.btn_calc);
        mForSpinner=(Spinner)findViewById(R.id.spn_for);
        mHomSpinner=(Spinner)findViewById(R.id.spn_hom);

        ArrayAdapter<String> arrayAdapter =new ArrayAdapter<String>(
                this,
                R.layout.spinner_closed,
                mCurrencies
        );
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHomSpinner.setAdapter(arrayAdapter);
        mForSpinner.setAdapter(arrayAdapter);

        //listener
        mForSpinner.setOnItemSelectedListener(this);
        mHomSpinner.setOnItemSelectedListener(this);

        if(savedInstanceState==null&&(PrefsMgr.getString(this,FOR)==null&&PrefsMgr.getString(this,HOM)==null)){
            mForSpinner.setSelection(findPositionGivenCode("USD",mCurrencies));
            mForSpinner.setSelection(findPositionGivenCode("CNY",mCurrencies));

            PrefsMgr.setString(this,FOR,"USD");
            PrefsMgr.setString(this,HOM,"CNY");
        }
        else{
            mForSpinner.setSelection(findPositionGivenCode(PrefsMgr.getString(this,FOR),mCurrencies));
            mForSpinner.setSelection(findPositionGivenCode(PrefsMgr.getString(this,HOM),mCurrencies));
        }
        mCalcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strAmount = mAmountEditText.getText().toString();
                if(strAmount.length()==0){
                    Toast.makeText(MainActivity.this, "数据不能为空", Toast.LENGTH_SHORT).show();
                }
                else
                new CurrencyConverterTask().execute(URL_BASE+mKey);

            }
        });
        mKey = getKey("open_key");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.mnu_invert:
                invertCurrencies();
                break;
            case R.id.mnu_codes:
                launchBrowser(SplashActivity.URL_CODES);   //打开一个JSON数据
                break;
            case R.id.mnu_record:
                //跳转到记录的界面
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,RecordActivity.class);
                MainActivity.this.startActivity(intent);
                break;
            case R.id.mnu_rate:
                //跳转到记录的界面
                Intent intent2 = new Intent();
                intent2.setClass(MainActivity.this,RateActivity.class);
                MainActivity.this.startActivity(intent2);
                break;
            case R.id.mnu_exit:
                finish();
                break;

        }
        return true;
        // return super.onPrepareOptionsMenu(item);

    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
    private void launchBrowser(String strUri) {
        if (isOnline()) {
            Uri uri = Uri.parse(strUri);
//call an implicit intent
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }
    private void invertCurrencies() {
        int nFor = mForSpinner.getSelectedItemPosition();    //汇率反转
        int nHom = mHomSpinner.getSelectedItemPosition();
        mForSpinner.setSelection(nHom);
        mHomSpinner.setSelection(nFor);
        mConvertedTextView.setText("");
        PrefsMgr.setString(this, FOR, extractCodeFromCurrency((String)
                mForSpinner.getSelectedItem()));
        PrefsMgr.setString(this, HOM, extractCodeFromCurrency((String)
                mHomSpinner.getSelectedItem()));

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spn_for:
                PrefsMgr.setString(this, FOR,
                        extractCodeFromCurrency((String)mForSpinner.getSelectedItem()));
                break;

            case R.id.spn_hom:
                PrefsMgr.setString(this, HOM,
                        extractCodeFromCurrency((String)mHomSpinner.getSelectedItem()));
                break;

            default:
                break;
        }

        mConvertedTextView.setText("");

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    private int findPositionGivenCode(String code, String[] currencies) {

        for (int i = 0; i < currencies.length; i++) {
            if (extractCodeFromCurrency(currencies[i]).equalsIgnoreCase(code)) {
                return i;
            }
        }
        //default
        return 0;
    }

    private String extractCodeFromCurrency(String currency) {
        return (currency).substring(0,3);
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
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Calculating Result...");
            progressDialog.setMessage("One moment please...");
            progressDialog.setCancelable(true);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                    "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CurrencyConverterTask.this.cancel(true);
                            progressDialog.dismiss();
                        }
                    });
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            double dCalculated = 0.0;
            String strForCode =
                    extractCodeFromCurrency(mCurrencies[mForSpinner.getSelectedItemPosition()]);
            String strHomCode = extractCodeFromCurrency(mCurrencies[mHomSpinner.
                    getSelectedItemPosition()]);
            String strAmount = mAmountEditText.getText().toString();
            try {
                if (jsonObject == null){
                    throw new JSONException("no data available.");
                }
                JSONObject jsonRates = jsonObject.getJSONObject(RATES);
                if (strHomCode.equalsIgnoreCase("USD")){
                    dCalculated = Double.parseDouble(strAmount) / jsonRates.getDouble(strForCode);
                } else if (strForCode.equalsIgnoreCase("USD")) {
                    dCalculated = Double.parseDouble(strAmount) * jsonRates.getDouble(strHomCode) ;
                }
                else {
                    dCalculated = Double.parseDouble(strAmount) * jsonRates.getDouble(strHomCode)
                            / jsonRates.getDouble(strForCode) ;
                }
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("foregin", strForCode);
                values.put("home", strHomCode);
                values.put("num", Double.parseDouble(strAmount));
                values.put("forenum",dCalculated);
                db.insert("datebase", null, values);
            } catch (JSONException e) {
                Toast.makeText(
                        MainActivity.this,
                        "There's been a exception: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
                mConvertedTextView.setText("");
                e.printStackTrace();
            }
            mConvertedTextView.setText(DECIMAL_FORMAT.format(dCalculated) + " " + strHomCode);
            progressDialog.dismiss();

        }
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
