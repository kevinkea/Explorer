package org.apache.cordova.GPS;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class GPS extends CordovaPlugin {

    private Activity activity = null;

    private LocationManager locationManager;
    
    /** JS回调接口对象 */
    public static CallbackContext cbCtx = null;

    /** 百度定位客户端 */
    public LocationClient mLocationClient = null;


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        activity = cordova.getActivity();
        locationManager = (LocationManager) activity.getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        if ("GPSIsOpen".equals(action)) {
            GPSIsOpen(callbackContext);
            return true;
        } else if ("openGPS".equals(action)) {
            openGPS(callbackContext);
            return true;
        } else if ("getCurrentPosition".equals(action)) {
            getCurrentPosition(args, callbackContext);
            return true;
        }

        return false;
    }

    public void GPSIsOpen( CallbackContext callbackContext) {
        callbackContext.success(String.valueOf(isOPen(activity.getBaseContext())));
    }

//    public void openGPS(CallbackContext callbackContext) {  
//        Intent GPSIntent = new Intent();  
//        GPSIntent.setClassName("com.android.settings",  
//                "com.android.settings.widget.SettingsAppWidgetProvider");  
//        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");  
//        GPSIntent.setData(Uri.parse("custom:3"));  
//        try {  
//            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();  
//        } catch (CanceledException e) {  
//            e.printStackTrace();  
//        }  
//    }
    
    public void openGPS(CallbackContext callbackContext) {

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try
        {
            cordova.getActivity().getBaseContext().startActivity(intent);
        } catch(ActivityNotFoundException ex){
            intent.setAction(Settings.ACTION_SETTINGS);
            try {
                cordova.getActivity().getBaseContext().startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
      //  GPSIsOpen(args, callbackContext);

    }

     private boolean isOPen(final Context context) {
    	 if(Build.VERSION.SDK_INT>=23){
    		 int checkPermission = ContextCompat.checkSelfPermission(cordova.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
             //权限允许
    		 if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                 ActivityCompat.requestPermissions(cordova.getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                 return false;
             //权限禁止
             }else{
            	 return false;
             }
    	 }else{
    		 // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
             boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
             // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
             boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        	 if(gps && network){
        		 return true;
        	 }else if(gps){
        		 return true;
        	 }else if(network){
        		 return true;
        	 }else{
        		 return false;
        	 }
    	 }	 
     }

//    private boolean isOPen(final Context context) {
////    	LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);  
//        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
//        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
//        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        if (gps || network) {
//            return true;
//        }
//        return false;
//    }
    

    /** 百度定位监听 */
    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            try {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                
//                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, json);
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, lng+","+lat);
                pluginResult.setKeepCallback(true);
                cbCtx.sendPluginResult(pluginResult);
            } finally {
                mLocationClient.stop();
            }
        }
    };

    /**
     * 插件主入口
     */

    public boolean getCurrentPosition(final JSONArray args, final CallbackContext callbackContext) throws JSONException{

            cbCtx = callbackContext;

            PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(true);
            cbCtx.sendPluginResult(pluginResult);

            if (mLocationClient == null) {
                mLocationClient = new LocationClient(this.webView.getContext());
                mLocationClient.registerLocationListener(myListener);

                // 配置定位SDK参数
                initLocation();
            }

            mLocationClient.start();

        return true;
    }

    /**
     * 配置定位SDK参数
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        // 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationMode.Hight_Accuracy);
        // 可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");
        // 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        // option.setScanSpan(0);
        // 可选，设置是否需要地址信息，默认不需要
        // option.setIsNeedAddress(false);
        // 可选，默认false,设置是否使用gps
        option.setOpenGps(true);
        // 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setLocationNotify(false);
        /* 可选，默认false，设置是否需要位置语义化结果，
         * 可以在BDLocation.getLocationDescribe里得到，
         * 结果类似于“在北京天安门附近”
         */
        // option.setIsNeedLocationDescribe(true);
        // 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        // option.setIsNeedLocationPoiList(true);
        // 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        // option.setIgnoreKillProcess(false);
        // 可选，默认false，设置是否收集CRASH信息，默认收集
        // option.SetIgnoreCacheException(true);
        // 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        // option.setEnableSimulateGps(false);
        mLocationClient.setLocOption(option);
    }

}