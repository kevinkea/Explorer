package com.qtong.brower;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BrowerLoadingDialog extends Dialog {
	private TextView tv;  
	  
    public BrowerLoadingDialog(Context context) {  
        super(context, R.style.browerloadingDialogStyle);  
    }  
  
    private BrowerLoadingDialog(Context context, int theme) {  
        super(context, theme);  
    }  
  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.brower_dialog_loading);  
        tv = (TextView) this.findViewById(R.id.brower_loading_tv);  
        tv.setText("正在加载");  
//        LinearLayout linearLayout = (LinearLayout) this.findViewById(R.id.ll_brower_LinearLayout);  
//        linearLayout.getBackground().setAlpha(210);  
    }  
}
