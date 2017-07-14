package zx31401425.zucc.mycurrencies.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import zx31401425.zucc.mycurrencies.Constants;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	

	// IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);

        try {
        	api.handleIntent(getIntent(), this);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {

	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		int result = 0;
		
		//Toast.makeText(this, "baseresp.getType = " + resp.getType(), Toast.LENGTH_SHORT).show();


		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
	}

	

}