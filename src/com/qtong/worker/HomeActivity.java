package com.qtong.worker;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.qtong.brower.R;
import com.qtong.brower.WebActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	protected static final String TAG = "HomeActivity";

	private Map<String, View> els = new HashMap<String, View>();

	private Button login;

	private static JSONObject data = new JSONObject();
	private EditText phoneText;
	private EditText firm;
	private EditText dept;
	private EditText url;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		phoneText = (EditText) findViewById(R.id.phoneText);
		firm = (EditText) findViewById(R.id.firmText);
		dept = (EditText) findViewById(R.id.deptText);
		url = (EditText) findViewById(R.id.urlText);
		login = (Button) findViewById(R.id.login);

		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent i = new Intent();
//				i.setClass(HomeActivity.this, MyWorkerActivity.class);
				i.setClass(getBaseContext(), MainActivity.class);
				i.putExtra("phoneText", phoneText.getText().toString());
				i.putExtra("firm", firm.getText().toString());
				i.putExtra("dept", dept.getText().toString());
				i.putExtra("url", url.getText().toString());
				
//				i.setClass(getBaseContext(), WebActivity.class);
//				try {
//					data.put("phone", phoneText.getText().toString());
//					data.put("firm", firm.getText().toString());
//					data.put("dept", dept.getText().toString());
//					data.put("url", url.getText().toString());
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}	
//				i.putExtra("userInfo",data.toString());
//				Log.i(TAG,  phoneText.getText().toString());
				startActivity(i);
			}
		});

	}

}
