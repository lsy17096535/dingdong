package com.intexh.bidong.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import com.intexh.bidong.R;
import com.intexh.bidong.constants.Constants;

public class WXEntryActivity_old extends Activity implements IWXAPIEventHandler{
	
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	
	private Button gotoBtn, regBtn, launchBtn, checkBtn;
	
	// IWXAPI �ǵ���app��΢��ͨ�ŵ�openapi�ӿ�
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.entry);
        
        // ͨ��WXAPIFactory��������ȡIWXAPI��ʵ��
    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);

    	regBtn = (Button) findViewById(R.id.reg_btn);
    	regBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// ����appע�ᵽ΢��
			    api.registerApp(Constants.APP_ID);    	
			}
		});
    	
        gotoBtn = (Button) findViewById(R.id.goto_send_btn);
//        gotoBtn.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//		        startActivity(new Intent(WXEntryActivity_old.this, SendToWXActivity.class));
//		        finish();
//			}
//		});
        
        launchBtn = (Button) findViewById(R.id.launch_wx_btn);
        launchBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(WXEntryActivity_old.this, "launch result = " + api.openWXApp(), Toast.LENGTH_LONG).show();
			}
		});
        
        checkBtn = (Button) findViewById(R.id.check_timeline_supported_btn);
        checkBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int wxSdkVersion = api.getWXAppSupportAPI();
				if (wxSdkVersion >= TIMELINE_SUPPORTED_VERSION) {
					Toast.makeText(WXEntryActivity_old.this, "wxSdkVersion = " + Integer.toHexString(wxSdkVersion) + "\ntimeline supported", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(WXEntryActivity_old.this, "wxSdkVersion = " + Integer.toHexString(wxSdkVersion) + "\ntimeline not supported", Toast.LENGTH_LONG).show();
				}
			}
		});
        
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	// ΢�ŷ������󵽵���Ӧ��ʱ����ص����÷���
	@Override
	public void onReq(BaseReq req) {
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			goToGetMsg();		
			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			break;
		default:
			break;
		}
	}

	// ����Ӧ�÷��͵�΢�ŵ�����������Ӧ����ص����÷���
	@Override
	public void onResp(BaseResp resp) {
		String result = "";

		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = "成功";
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = "用户取消";
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = "授权失败";
			break;
			case BaseResp.ErrCode.ERR_COMM:{
				result = "未知原因";
				break;
			}
			case BaseResp.ErrCode.ERR_UNSUPPORT:{
				result = "不支持";
				break;
			}
			case BaseResp.ErrCode.ERR_SENT_FAILED:{
				result = "发送失败";
				break;
			}
		default:
			break;
		}

		finish();
	}
	
	private void goToGetMsg() {
	}
	
	private void goToShowMsg() {
//		WXMediaMessage wxMsg = showReq.message;
//		WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;
//
//		StringBuffer msg = new StringBuffer(); // ��֯һ������ʾ����Ϣ����
//		msg.append("description: ");
//		msg.append(wxMsg.description);
//		msg.append("\n");
//		msg.append("extInfo: ");
//		msg.append(obj.extInfo);
//		msg.append("\n");
//		msg.append("filePath: ");
//		msg.append(obj.filePath);
//
//		Intent intent = new Intent(this, ShowFromWXActivity.class);
//		intent.putExtra(Constants.ShowMsgActivity.STitle, wxMsg.title);
//		intent.putExtra(Constants.ShowMsgActivity.SMessage, msg.toString());
//		intent.putExtra(Constants.ShowMsgActivity.BAThumbData, wxMsg.thumbData);
//		startActivity(intent);
//		finish();
	}
}