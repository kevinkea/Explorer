package com.qtong.worker;

import java.io.BufferedReader;
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

import com.qtong.adapter.GridviewAdapter;
import com.qtong.bean.bean;
import com.qtong.brower.R;
import com.qtong.brower.WebActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MyWorkerActivity extends Activity {

	private static final String TAG = "MyWorkerActivity";
	private GridView gridView;
	private GridviewAdapter adapter;
	private List<bean> mlist;
	private Map<String,String> els;
	
	

    private static JSONObject data=new JSONObject();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brower_popmenu);
		gridView = (GridView) findViewById(R.id.gridview);

		doGet doGet = new doGet(this);
		doGet.execute();

	}

	String urlAddress = "http://112.33.5.158:8090/lapp_platform/userapp/getUserApp/116-45097-45211-45392/13911313118/1/0";
	URL url;
	HttpURLConnection uRLConnection;
	String response = "";

	class doGet extends AsyncTask<Void, Integer, String> {
		private Context context;

		doGet(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			Toast.makeText(context, "开始执行", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				url = new URL(urlAddress);
				uRLConnection = (HttpURLConnection) url.openConnection();
				InputStream is = uRLConnection.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String readLine = null;
				while ((readLine = br.readLine()) != null) {
					// response = br.readLine();
					response = response + readLine;
				}
				is.close();
				br.close();
				uRLConnection.disconnect();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			return response;
		}

		@Override
		protected void onPostExecute(String string) {
			Toast.makeText(context, "执行完毕", Toast.LENGTH_SHORT).show();
			mlist = new ArrayList<bean>();
			try {
				JSONArray jsonArray = new JSONArray(string);
				if (jsonArray.getJSONObject(0).getInt("resultcode") == 1) {
					for (int i = 1; i < jsonArray.length(); i++) {
						String appName = jsonArray.getJSONObject(i).getString("appName");
						String icon = jsonArray.getJSONObject(i).getString("icon");
						String frontUrl = jsonArray.getJSONObject(i).getString("frontUrl");
						bean b = new bean();
						b.setAppName(appName);
						b.setIcon(icon);
						b.setFrontUrl(frontUrl);
						mlist.add(b);

					}
					adapter = new GridviewAdapter(MyWorkerActivity.this, mlist);
					gridView.setAdapter(adapter);
					
					gridView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
							Intent i = new Intent();
							els=new HashMap<String,String>();;
							try {
						        if(!els.containsKey("phone")){
						            els.put("phone",getIntent().getStringExtra("phoneText"));
						            if(!data.isNull("phone")){
						              data.getString("phone");
						            }

						        }
						        if(!els.containsKey("firm")){
						            els.put("firm",getIntent().getStringExtra("firm"));
						            if(!data.isNull("firm")){
						                data.getString("firm");
						            }

						        }
						        if(!els.containsKey("dept")){
						            els.put("dept",getIntent().getStringExtra("dept"));
						            if(!data.isNull("dept")){
						                data.getString("dept");
						            }
						        }
						        if(!els.containsKey("url")){
						            els.put("url",mlist.get(arg2).getFrontUrl().toString());
						            if(!data.isNull("url")){
						                data.getString("url");
						            }
						        }
						        } catch (JSONException e) {
						            e.printStackTrace();
						        }
							 Log.i(TAG, getIntent().getStringExtra("firm"));
							for(String key:els.keySet()){
			                    String text= (String) els.get(key);
			                    String val=text.toString();
			                    if(val==null || "".equals(val)){
			                        Toast.makeText(arg0.getContext(),key+"不允许为空！", Toast.LENGTH_SHORT).show();
			                        return;
			                    }else{
			                        try {
			                            data.put(key,val);
			                        } catch (JSONException e) {
			                            e.printStackTrace();
			                        }
			                    }
			                }
							i.setClass(MyWorkerActivity.this, WebActivity.class);
							i.putExtra("userInfo", data.toString());
//							i.putExtra("url", mlist.get(arg2).getFrontUrl());
							startActivity(i);
						}
					});
				} else {
				}
			} catch (JSONException e) {
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		}
	}
	
}
