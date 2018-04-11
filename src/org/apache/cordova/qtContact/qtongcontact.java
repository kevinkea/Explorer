package org.apache.cordova.qtContact;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class qtongcontact extends CordovaPlugin{
	@Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if("getPersonalInfo".equals(action)){
            String dataStr= cordova.getActivity().getIntent().getStringExtra("userInfo");

            JSONObject data=new JSONObject(dataStr);
            JSONObject sendData=new JSONObject();
            sendData.put("mobile",data.getString("phone"));
            sendData.put("companyId",data.getString("firm"));
            sendData.put("depId",data.getString("dept"));
            callbackContext.success(sendData);
            return true;
        }
        return false;
    }

}
