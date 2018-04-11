/**
 *Created by zgz on 16/9/21.
 *Copyright © 2016年 quantong. All rights reserved.
 */

package com.qtong.brower;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaInterfaceImpl;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.engine.SystemWebChromeClient;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewClient;
import org.apache.cordova.engine.SystemWebViewEngine;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WebActivity extends CordovaActivity {

	private static SystemWebView systemWebView;
	private RelativeLayout main_brower_titlebar;
	private ImageView main_cordova_brower_back;
	private TextView main_brower_title;
	private TextView main_brower_line;
	private ImageView main_qtbrower_menu;
	private Button main_cordova_brower_backbt;
	private Button main_cordova_brower_close;
	private Button main_cordova_brower_menu_btn;
	private static MyBrowserPopupMenu myPopupMenu;
	private ProgressBar main_brower_myProgressBar;
	private List<String> titles;
	private List<List<String>> item_names; // 选项名称
	private List<List<Integer>> item_images; // 选项图标
	String urlString, htmlString, appName;
	static String oldurl = "";
	private static BrowerLoadingDialog dialog;
	private CordovaWebView cordovaWebView;
	protected static final String tag = "HomeActivity";
    private String mainUrl = null;
    private static final int MSG_PAGE_TIMEOUT = 0;
    private long time = 20000;   
    private boolean timeout; 
	
	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_activity);

//		String urlString = getIntent().getStringExtra("url");
		String jsonStr = getIntent().getStringExtra("userInfo");

		JSONObject jo;
		try {
			jo = new JSONObject(jsonStr);
			urlString = jo.getString("url");
			appName = jo.getString("appName");
			Log.e(tag, urlString);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		main_brower_titlebar = (RelativeLayout) findViewById(R.id.main_brower_titlebar);
		main_cordova_brower_back = (ImageView) findViewById(R.id.main_cordova_brower_back);
		main_brower_myProgressBar = (ProgressBar) findViewById(R.id.main_brower_myProgressBar);
		main_brower_title = (TextView) findViewById(R.id.main_brower_title);
		main_brower_line = (TextView) findViewById(R.id.main_brower_line);
		main_cordova_brower_backbt = (Button) findViewById(R.id.main_cordova_brower_backbt);
		main_cordova_brower_close = (Button) findViewById(R.id.main_cordova_brower_close);
		// main_qtbrower_menu = (ImageView) findViewById(R.id.main_brower_menu);
		main_cordova_brower_menu_btn = (Button) findViewById(R.id.main_cordova_brower_menu_btn);
		systemWebView = (SystemWebView) findViewById(R.id.main_brower_webview);

		// 为初始化CordovaWebView提供参数
		ConfigXmlParser parser = new ConfigXmlParser();
		// 这里会解析res/xml/config.xml配置文件
		parser.parse(this);
		// 创建一个cordovawebview
		SystemWebViewEngine cordovaWebViewEngine = new SystemWebViewEngine(systemWebView);
		cordovaWebView = new CordovaWebViewImpl(cordovaWebViewEngine);
		cordovaInterface = new CordovaInterfaceImpl(this);
		// 初始化CordovaWebView
		if (!cordovaWebView.isInitialized()) {
			cordovaWebView.init(cordovaInterface, parser.getPluginEntries(), parser.getPreferences());
		}

		systemWebView.getSettings().setJavaScriptEnabled(true);
		systemWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
//		systemWebView.getSettings().setDomStorageEnabled(true);
		systemWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		systemWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");

		// 关闭
		main_cordova_brower_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				systemWebView.loadUrl("javascript:window.local_obj.showSource('<head>'+"
						+ "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
				finish();
			}
		});
		
		// 返回
		main_cordova_brower_backbt.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				Log.d(TAG, mainUrl);
				Log.d(TAG, parseUrl(systemWebView.getUrl()));

				if (!systemWebView.getUrl().contains("file:///android_asset/404.html")) {
					if(mainUrl!=null && mainUrl.equals(parseUrl(systemWebView.getUrl()))){
						Log.e(TAG, mainUrl);
						mainUrl=null;
						finish();
					}else{
						systemWebView.evaluateJavascript("(function(){ return typeof window.back})()",
								new ValueCallback<String>() {
							@Override
							public void onReceiveValue(String arg0) {
								//页面包含function方法。
								if (arg0.contains("function")) {
									// systemWebView.loadUrl("javascript:back()");
									systemWebView.evaluateJavascript("back();", new ValueCallback<String>() {
										@Override
										public void onReceiveValue(String arg0) {
											if (arg0 == null || !"true".equals(arg0)) {
												Log.e(TAG, "等于了false");
											}else{
												Log.e(TAG, "等于了true");
												mainUrl=null;
												finish();
											}
										}
									});
								} else {
									//产品共享平台的返回工作台判断
									systemWebView.evaluateJavascript("isHome();", new ValueCallback<String>() {
										@Override
										public void onReceiveValue(String arg0) {
											if (arg0 == null || !"true".equals(arg0)) {
												Log.e(TAG, "等于Home了false");
											}else{
												Log.e(TAG, "等于Home了true");
												mainUrl=null;
												finish();
											}
										}
									});
									Log.e(TAG, "不包含function");
									if(systemWebView.canGoBack()){
										systemWebView.goBack();
									}else{
										mainUrl=null;
										finish();
									}
								}
							}
						});
					}
				} else {
					mainUrl=null;
					finish();
				}
			}
			
		});
		// 对话框
		dialog = new BrowerLoadingDialog(this);
		dialog.setCanceledOnTouchOutside(false);

		// 菜单
		main_cordova_brower_menu_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (myPopupMenu.isShowing()) {
					myPopupMenu.dismiss();
				} else {
					/**
					 * 这句代码可以使菜单栏如对话框一样弹出的效果
					 * myPopupMenu.setAnimationStyle(android
					 * .R.style.Animation_Dialog);
					 */
					// 设置菜单栏显示位置
					myPopupMenu.showAtLocation(findViewById(R.id.layout), Gravity.BOTTOM, 0, 0);
					myPopupMenu.isShowing();
				}
			}
		});

		// 开始结束loading和title
		SystemWebViewClient swv = new SystemWebViewClient(cordovaWebViewEngine) {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// loading show
				dialog.show();
				setPopMemu();
				super.onPageStarted(view, url, favicon);
				
				timeout = true;
				new Thread(new Runnable() {  
		            @Override  
		            public void run() {  
		                try {  
		                    Thread.sleep(time);  
		                } catch (InterruptedException e) {  
		                    e.printStackTrace();  
		                }  
		                if(timeout) {  
		                	dialog.cancel();
		                    Message m = new Message();  
		                    m.what = MSG_PAGE_TIMEOUT ;  
		                    mHandler.sendMessage(m);  
		                    timeout = false;
		                }  
		            }  
		        }).start(); 
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				view.loadUrl("javascript:window.local_obj.showSource('<head>'+"
						+ "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
				String title = view.getTitle();
				if(appName == null){
					if (title != null && !title.contains(".")) {
						if (title.length() > 6) {
							title = title.substring(0, 6) + "...";
						}
						main_brower_title.setText(title);
					}
				}else{
					main_brower_title.setText(appName);
				}
				
				if(mainUrl == null){
					 mainUrl = parseUrl(url);
				}
				Log.i(TAG, "加载完成url=="+mainUrl);
				dialog.cancel();
				
				timeout = false;
				super.onPageFinished(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				// 这里进行无网络或错误处理，具体可以根据errorCode的值进行判断，做跟详细的处理。
				try {
					systemWebView.loadUrl("file:///android_asset/404.html");
					oldurl = failingUrl;
				} catch (Exception e) {
					e.getMessage();
				}
			}

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				
				if (url.contains("sms")) {
					url = url.replace("sms", "smsto");
					Uri smsToUri = Uri.parse(url);
					Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO, smsToUri);
					startActivity(mIntent);
					return true;
				} else if (url.contains("tel")) {
					Intent intent = new Intent(Intent.ACTION_DIAL);
					intent.setData(Uri.parse(url));
					if (intent.resolveActivity(getPackageManager()) != null) {
						startActivity(intent);
					}
					return true;	//webview处理url是根据程序来执行的 
				} else if (url.contains("mailto")) {
					Uri uri = Uri.parse(url);
					Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
					intent.putExtra(Intent.EXTRA_SUBJECT, "这是邮件的主题部分"); // 主题
					intent.putExtra(Intent.EXTRA_TEXT, "这是邮件的正文部分"); // 正文
					startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
					return true;	//webview处理url是根据程序来执行的 
				}else if(url.endsWith(".apk")){
            		Uri uri = Uri.parse(url);
            		Intent viewIntent = new Intent(Intent.ACTION_VIEW,uri);
            		startActivity(viewIntent);
            		return true;
				}else if(url.contains("partyschool")){
					System.out.println(mainUrl);
					Uri uri = Uri.parse(mainUrl);
            		Intent viewIntent = new Intent(Intent.ACTION_VIEW,uri);
            		startActivity(viewIntent);
					return true;
				}else{
					return false;	//webview处理url是在webview内部执行
				}
			}

		};
		systemWebView.setWebViewClient(swv);

		// 进度条
		SystemWebChromeClient swc = new SystemWebChromeClient(cordovaWebViewEngine) {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				Log.d("progress", String.valueOf(newProgress));
				if (newProgress == 100) {
					main_brower_myProgressBar.setVisibility(View.GONE);
					main_brower_line.setVisibility(View.VISIBLE);
				} else {
					if (View.GONE == main_brower_myProgressBar.getVisibility()) {
						main_brower_myProgressBar.setVisibility(View.VISIBLE);
						main_brower_line.setVisibility(View.GONE);
					}
					main_brower_myProgressBar.setProgress(newProgress);
				}
				super.onProgressChanged(view, newProgress);
			}
		};
		systemWebView.setWebChromeClient(swc);
//		systemWebView.clearCache(true);
		
		systemWebView.loadUrl(urlString);// 加载网页
	}

	/**
	 * android后退、返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && systemWebView.canGoBack()) {
			systemWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		cordovaWebView.handleDestroy();
	}
	
	Handler mHandler = new Handler(){  
	   @Override  
	   public void handleMessage(Message msg) {  
	       switch(msg.what){         
	           case MSG_PAGE_TIMEOUT :  
	        	   //这里对已经显示出页面且加载超时的情况不做处理     
	               if(systemWebView.getProgress() < 100 )  {
	            	   dialog.cancel();
	            	   Toast.makeText(WebActivity.this, "网络超时，请刷新重试！", Toast.LENGTH_SHORT).show();
	               }  
	           break ;    
	       }  
	   }  
	};
	
	private void setPopMemu() {
		/**
		 * 菜单栏分类标题
		 */
		titles = new ArrayList<String>();
		titles = addItems(new String[] { "常用" });

		/**
		 * 选项图标
		 */
		item_images = new ArrayList<List<Integer>>();
		item_images.add(addItems(new Integer[] { R.drawable.ic_action_copy, R.drawable.ic_action_refresh,
				R.drawable.ic_action_web_site, R.drawable.ic_action_share }));
		/**
		 * 选项名称
		 */
		item_names = new ArrayList<List<String>>();
		item_names.add(addItems(new String[] { "刷新", "取消", "返回工作台" }));
		myPopupMenu = new MyBrowserPopupMenu(this, titles, item_names, item_images);
		myPopupMenu.setBackgroundDrawable(getResources().getDrawable(R.drawable.gv_brower_title_background));
		/**
		 * 设置菜单栏推拉动画效果 res/anim中的xml文件与styles.xml中的style配合使用
		 */
		myPopupMenu.setAnimationStyle(R.style.PopupAnimation);
	}

	/**
	 * 转换为List<String> 用于菜单栏中的菜单项图标赋值
	 * 
	 * @param values
	 * @return
	 */
	private List<String> addItems(String[] values) {

		List<String> list = new ArrayList<String>();
		for (String var : values) {
			list.add(var);
		}

		return list;
	}

	/**
	 * 转换为List<Integer> 用于菜单栏中的标题赋值
	 * 
	 * @param values
	 * @return
	 */
	private List<Integer> addItems(Integer[] values) {

		List<Integer> list = new ArrayList<Integer>();
		for (Integer var : values) {
			list.add(var);
		}

		return list;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/**
		 * 系统菜单必须要加一个，才有效果
		 */
		menu.add("");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {

		if (myPopupMenu.isShowing()) {
			myPopupMenu.dismiss();
		} else {
			/**
			 * 这句代码可以使菜单栏如对话框一样弹出的效果
			 * myPopupMenu.setAnimationStyle(android.R.style.Animation_Dialog);
			 */
			// 设置菜单栏显示位置
			myPopupMenu.showAtLocation(findViewById(R.id.layout), Gravity.BOTTOM, 0, 0);
			myPopupMenu.isShowing();
		}
		return false;
	}

	public static void browerReflesh() {
		systemWebView.refreshDrawableState();
		if (oldurl.equals("")) {
			systemWebView.reload();
		} else {
			systemWebView.loadUrl(oldurl);
			oldurl = "";
		}
		myPopupMenu.dismiss();
	}

	public static void browerMenuClose() {
		myPopupMenu.dismiss();
	}

	final class InJavaScriptLocalObj {
		@JavascriptInterface
		public void showSource(String html) {
			htmlString = html;
		}
	}

	private String parseUrl(String url) {
		if (url != null) {
			if (url.indexOf("?") != -1) {
				url = url.substring(0, url.indexOf("?") - 1);
			}
		}
		return url;
	}


}
