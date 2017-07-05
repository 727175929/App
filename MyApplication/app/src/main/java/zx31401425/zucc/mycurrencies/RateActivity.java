package zx31401425.zucc.mycurrencies;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.Properties;

public class RateActivity extends AppCompatActivity {
    private String mKey;
    public static final String RATES="rates";
    public static final String URL_BASE =
            "http://openexchangerates.org/api/latest.json?app_id=";
    //used to format data from openexchangerates.org
    private static final DecimalFormat DECIMAL_FORMAT = new
            DecimalFormat("#,##0.00000");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        Button rate_button = (Button) findViewById(R.id.button_rate);

        rate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RateActivity.CurrencyConverterTask().execute(URL_BASE+mKey);

            }
        });
        mKey = getKey("open_key");

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

}
