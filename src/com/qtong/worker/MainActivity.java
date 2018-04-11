package com.qtong.worker;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qtong.brower.BrowerLoadingDialog;
import com.qtong.brower.R;
import com.qtong.brower.WebActivity;

import android.app.Activity;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private GridView mygrid;
	private ArrayList<HashMap<String, Object>> lstImageItem;
	private MyAdapter adapter;
	private String url, mobile, firm, dept, result, link;
	/** 存储的文件名 */
	public static final String SELF_INFO = "SELF_INFO";  
	private static BrowerLoadingDialog dialog;
	private static JSONObject data = new JSONObject();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		//对话框
		dialog = new BrowerLoadingDialog(this);  
		dialog.setCanceledOnTouchOutside(false);
				
		mygrid = (GridView) findViewById(R.id.main_gridview);
		lstImageItem = new ArrayList<HashMap<String, Object>>();
		adapter = new MyAdapter(lstImageItem);
		mygrid.setAdapter(adapter);
		Bundle bundle = this.getIntent().getExtras();
		mobile = bundle.getString("phoneText");
		firm = bundle.getString("firm");
		dept = bundle.getString("dept");

		url = "http://112.33.5.158:8090/lapp_platform/userapp/getUserApp/"+ firm +"-45097-45211-"+dept+"/"
				+ mobile + "/1/0";
		link = url;
		System.out.println("==="+url);
//		new getThread(url).start();
		if(isNetworkAvailable(this)){
			new MyAsyncTask().execute();
		}else{
			Toast.makeText(MainActivity.this, "网络不可用！", 3000).show();
		}
		
		SharedPreferences sp = getSharedPreferences(SELF_INFO,  Activity.MODE_PRIVATE);  
        // 获取Editor对象  
        Editor editor = sp.edit();  
        editor.putString("phone", mobile);
        editor.putString("dept", "45392");
        editor.putString("firm", "116");
        editor.commit();
	}
	
	/** 
     * 检测当的网络（WLAN、3G/2G）状态 
     * @param context Context 
     * @return true 表示网络可用 
     */  
    public static boolean isNetworkAvailable(Context context) {  
        ConnectivityManager connectivity = (ConnectivityManager) context  
                .getSystemService(Context.CONNECTIVITY_SERVICE);  
        if (connectivity != null) {  
            NetworkInfo info = connectivity.getActiveNetworkInfo();  
            if (info != null && info.isConnected()){  
                // 当前网络是连接的  
                if (info.getState() == NetworkInfo.State.CONNECTED){  
                    // 当前所连接的网络可用  
                    return true;  
                }  
            }  
        }  
        return false;  
    } 
	
	class MyAdapter extends BaseAdapter {

		private ArrayList<HashMap<String, Object>> arrayList;

		public MyAdapter(ArrayList<HashMap<String, Object>> arrayList) {
			this.arrayList = arrayList;
		}

		@Override
		public int getCount() {
			return arrayList.size();
		}

		@Override
		public Object getItem(int position) {
			return arrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				view = getLayoutInflater().inflate(
						R.layout.activity_main_gridview_item, null);
			} else {
				view = convertView;
			}
			final HashMap<String, Object> map = arrayList.get(position);
			RelativeLayout itemView = (RelativeLayout) view.findViewById(R.id.main);
			ImageView imgview = (ImageView) view.findViewById(R.id.ItemImage);
			TextView text = (TextView) view.findViewById(R.id.ItemText);
			text.setText(map.get("ItemText").toString());
			downloadAsyncTask(imgview, map.get("ItemImage").toString());
			itemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this,
							com.qtong.brower.WebActivity.class);
					String depid = map.get("departid").toString();
					System.out.println("deptid==="+depid);
//					intent.putExtra("url","http://login.dangjian.chinamobile.com/app/andorid_install.html"map.get("ItemURL").toString());
					try {
						data.put("url", map.get("ItemURL").toString());
						data.put("firm", map.get("companyid").toString());
						data.put("phone", map.get("mobile").toString());
						data.put("dept",  depid.substring(depid.length()-5, depid.length()));
						data.put("appName", map.get("appName").toString());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					intent.putExtra("userInfo", data.toString());
					startActivity(intent);
				}
			});
			return view;
		}
	}

	class MyAsyncTask extends AsyncTask<Void, Integer, String> {
		
		@Override
		protected void onPreExecute() {
			dialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			HttpURLConnection connection = null;
			try {
				URL url = new URL(link);
                //打开网络链接
                connection = (HttpURLConnection) url.openConnection();
                
                if(connection.getResponseCode() == 200){
                	//获取响应的输入流对象
                    InputStream is = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                    result = "";
                    String line = "";
                    
                    while ((line = bufferedReader.readLine()) != null) {
                    	result += line;
    				}
    				is.close();
    				bufferedReader.close();
    				connection.disconnect();
    				return result;
//    				Message msg = new Message();
//                    msg.what = 0;
//                    handler.sendMessage(msg);
                }else{
                	return null;
                }
    				
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			
		}
		
		@Override
		protected void onPostExecute(String result) {
			dialog.cancel();
			try {
				if(result != null){
					System.out.println(result);
		    		JSONArray jsonArray = new JSONArray(result);
		    		JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());
		    		if (jsonObject.getString("resultcode").equals("1")) {
		    			for(int i=1;i<jsonArray.length();i++){
		    				JSONObject jsonObject1 = new JSONObject(jsonArray.get(i)
		    						.toString());
		    				HashMap<String, Object> map1 = new HashMap<String, Object>();
		    				map1.put("ItemImage", jsonObject1.getString("icon"));// 添加图像资源的ID
		    				map1.put("ItemText", jsonObject1.getString("appName"));// 按序号做ItemText
		    				map1.put("ItemURL", jsonObject1.getString("frontUrl"));// 按序号做ItemText
		    				map1.put("companyid", jsonObject1.getString("companyid"));// 按序号做ItemText
		    				map1.put("departid", jsonObject1.getString("departid"));// 按序号做ItemText
		    				map1.put("mobile", jsonObject1.getString("mobile"));// 按序号做ItemText
		    				map1.put("appName", jsonObject1.getString("appName"));// 按序号做ItemText
//		    				map1.put("ItemURL", "http://m.dianping.com/beijing");// 按序号做ItemText
		    				lstImageItem.add(map1);
		    			}
		    			adapter.notifyDataSetChanged();
		    		}else if(jsonObject.getString("resultcode").equals("2")){
		    			Toast.makeText(MainActivity.this, "输入手机号错误", 3000).show();
		    		}
				}else{
					Toast.makeText(MainActivity.this, "网络不给力啊！", 3000).show();
					finish();
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {

		}
		
		protected void onCancelled() {  
            super.onCancelled();  
        } 
	}
	
//	class getThread extends Thread{
//		String link;
//		public getThread(String url) {
//			link = url;
//		}
//
//        public void run() {
//            HttpURLConnection connection = null;
//            try {
//                URL url = new URL(link);
//                //打开网络链接
//                connection = (HttpURLConnection) url.openConnection();
//                
//                if(connection.getResponseCode() == 200){
//                	//获取响应的输入流对象
//                    InputStream is = connection.getInputStream();
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
//                    String result = "";
//                    String line = "";
//                    
//                    while ((line = bufferedReader.readLine()) != null) {
//                    	result += line;
//    				}
//    				is.close();
//    				bufferedReader.close();
//    				connection.disconnect();
//    				// 显示响应
//    				showResult(result);// 一个私有方法，将响应结果显示出来
//    				
//                    Message msg = new Message();
//                    msg.what = 0;
//                    handler.sendMessage(msg);
//                }else{
//                	Toast.makeText(MainActivity.this, "网络太差！", 3000).show();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                if(connection != null){
//                    connection.disconnect();
//                }
//            }
//        };
//    };
//    
//    public void showResult(String result) {
//    	try {
//    		System.out.println(result);
//    		JSONArray jsonArray = new JSONArray(result);
//    		JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());
//    		if (jsonObject.getString("resultcode").equals("1")) {
//    			for(int i=1;i<jsonArray.length();i++){
//    				JSONObject jsonObject1 = new JSONObject(jsonArray.get(i)
//    						.toString());
//    				HashMap<String, Object> map1 = new HashMap<String, Object>();
//    				map1.put("ItemImage", jsonObject1.getString("icon"));// 添加图像资源的ID
//    				map1.put("ItemText", jsonObject1.getString("appName"));// 按序号做ItemText
//    				map1.put("ItemURL", jsonObject1.getString("frontUrl"));// 按序号做ItemText
//    				map1.put("companyid", jsonObject1.getString("companyid"));// 按序号做ItemText
//    				map1.put("departid", jsonObject1.getString("departid"));// 按序号做ItemText
//    				map1.put("mobile", jsonObject1.getString("mobile"));// 按序号做ItemText
////    				map1.put("ItemURL", "http://m.dianping.com/beijing");// 按序号做ItemText
//    				lstImageItem.add(map1);
//    			}
//    		}else if(jsonObject.getString("resultcode").equals("2")){
//    			Toast.makeText(MainActivity.this, "输入手机号错误", 3000).show();
//    		}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//    	
//	}
//	
//	Handler handler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case 0:
//				adapter.notifyDataSetChanged();
//				break;
//			default:
//				break;
//			}
//		}
//
//	};

	/**
	 * 此方法用来异步加载图片
	 * 
	 * @param imageview
	 * @param path
	 */
	public void downloadAsyncTask(final ImageView imageview, final String path) {
		new AsyncTask<String, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {
				return getBitmap(path);
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				if (result != null && imageview != null) {
					imageview.setImageBitmap(result);
				}
			}

		}.execute(new String[] {});

	}

	/**
	 * 通过该网络路径获取Bitmap
	 * 
	 * @param 该图片的网络路径
	 */
	public static Bitmap getBitmap(String urlPath) {

		Bitmap bitmap = null;
		bitmap = downloadAndSaveBitmap(urlPath);
		return bitmap;
	}

	/**
	 * 下载保存图片
	 * 
	 * @param urlPath
	 *            下载路径
	 * @param fullName
	 *            文件保存路径+文件名
	 * @return
	 */
	private static Bitmap downloadAndSaveBitmap(String urlPath) {

		Bitmap bitmap = downloadImage(urlPath);
		return bitmap;
	}

	/**
	 * 下载图片
	 * 
	 * @param urlPath
	 * @return
	 */
	private static Bitmap downloadImage(String urlPath) {

		try {
			byte[] byteData = getImageByte(urlPath);
			if (byteData == null) {
				return null;
			}
			int len = byteData.length;

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			options.inJustDecodeBounds = false;
			if (len > 200000) {// 大于200K的进行压缩处理
				options.inSampleSize = 2;
			}

			return BitmapFactory.decodeByteArray(byteData, 0, len);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取图片的byte数组
	 * 
	 * @param urlPath
	 * @return
	 */
	private static byte[] getImageByte(String urlPath) {
		InputStream in = null;
		byte[] result = null;
		try {
			URL url = new URL(urlPath);
			HttpURLConnection httpURLconnection = (HttpURLConnection) url
					.openConnection();
			httpURLconnection.setDoInput(true);
			httpURLconnection.connect();
			if (httpURLconnection.getResponseCode() == 200) {
				in = httpURLconnection.getInputStream();
				result = readInputStream(in);
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * 将输入流转为byte数组
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	private static byte[] readInputStream(InputStream in) throws Exception {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = in.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		}
		baos.close();
		in.close();
		return baos.toByteArray();

	}
}