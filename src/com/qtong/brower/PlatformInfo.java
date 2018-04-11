package com.qtong.brower;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class PlatformInfo {
	private String appinfo;
	private String mobile,pid,compid;
	Context context;
	Handler myHandler;
	public static final int GET_DATA_FROM_BROWER=5099;
	/** 存储的文件名 */
	public static final String SELF_INFO = "SELF_INFO";
	
	public PlatformInfo(Context context,Handler handler) {
		this.context=context;	
		this.myHandler=handler;
		SharedPreferences sp = context.getSharedPreferences(SELF_INFO,
				Activity.MODE_PRIVATE);
		String telstring= sp.getString("telArray", "");
		String[] tel = telstring.split(",");
		this.mobile = tel[0];
		if(sp.getString("Pid", "").equals("")){
			this.pid = "116";
		}else{
			this.pid = sp.getString("Pid", "");			
		}
		//由于新媒暂时不能提供所在省公司Id，暂时写死
//		this.compid = sp.getString("compId", "");
		if(sp.getString("compId", "").equals("")){
			this.compid = "24120";			
		}else{			
			this.compid = sp.getString("compId", "");	
		}
		String url = "http://112.33.5.160:8090/lapp_platform/userapp/getUserApp/116-45097-45211-45392/"+ mobile + "/2/0";
//		String url = "http://112.33.5.159:8081/lapp_platform/userapp/getUserApp/"+pid+"/"+compid+"/"
//				+ mobile + "/1/1";
		System.out.println("=======================测试开始");
		System.out.println("=======================传入值mobile" + mobile);
		System.out.println("=======================传入值telstring" + telstring);
		System.out.println("=======================传入值pid" + pid);
		System.out.println("=======================传入值compid" + compid);
		System.out.println("=======================传入值url" + url);
		System.out.println("=======================测试结束");
		new GetPlatform(url).start();
	}

		
	class GetPlatform extends Thread {
		String string;

		public GetPlatform(String url) {
			string = url;
		}

		public void run() {
			Looper.prepare();
			// 生成请求对象
			HttpGet httpGet = new HttpGet(string);
			HttpClient httpClient = new DefaultHttpClient();
			// 发送请求
			try {
				HttpResponse response = httpClient.execute(httpGet);
				// 显示响应
				showResponseResult(response);// 一个私有方法，将响应结果显示出来
				Message msg=new Message();
	            msg.obj=appinfo;
	            msg.what=GET_DATA_FROM_BROWER;
				myHandler.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Looper.loop();
		}
	}
	private void showResponseResult(HttpResponse response) {
		if (null == response) {
			return;
		}
		HttpEntity httpEntity = response.getEntity();
		try {
			InputStream inputStream = httpEntity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));
			String result = "";
			String line = "";
			while (null != (line = reader.readLine())) {
				result += line;
			}
			appinfo = result;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
