package com.pjtech.android.ridesocial.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

public class MyDialog extends Dialog {

	public MyDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public MyDialog(Context context) {
		super(context, android.R.style.Theme_Holo_Dialog_NoActionBar);
		// TODO Auto-generated constructor stub
		init(context);
	}

	@SuppressWarnings("deprecation")
	private void init(Context context) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setBackgroundDrawable(new ColorDrawable(0));

		LayoutParams params = getWindow().getAttributes();
        params.width = LayoutParams.FILL_PARENT;
        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        
        setCancelable(false);
	}
	
	@Override
	public void setCancelable(boolean flag) {
		// TODO Auto-generated method stub
		setCanceledOnTouchOutside(flag);
		super.setCancelable(flag);
	}
}
