package zx31401425.zucc.mycurrencies;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;

public class ShareActivity extends Activity implements IWXAPIEventHandler{

    private static final int THUMB_SIZE = 150;

    private int mTargetScene = SendMessageToWX.Req.WXSceneSession;


    private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();

    private IWXAPI api;

    private Button shareBtn;

    private void regTowx(){
        api = WXAPIFactory.createWXAPI(this,Constants.APP_ID,true);// 通过WXAPIFactory工厂，获取IWXAPI的实例
        api.registerApp(Constants.APP_ID);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        regTowx();

        shareBtn = (Button) findViewById(R.id.share);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = "https://github.com/727175929/App";
                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = "各个国家汇率查询的app";
                msg.description = "历史记录查询，微信分享，图表查看，汇率更新通知等";
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.send_music_thumb);
                Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
                bmp.recycle();
                msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");   //transaction字段用于唯一的表示请求
                req.message = msg;
                req.scene = mTargetScene;

                //调用api接口发送数据到微信
                api.sendReq(req);
            }
        });
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {

    }
}
