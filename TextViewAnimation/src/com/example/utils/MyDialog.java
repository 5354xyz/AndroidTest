package com.example.utils;

import com.example.textviewanimation.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/** 
 * @author  5354xyz
 * @version 2013-11-13 下午9:57:57 
 * @Eacer-mail: xiaoyizong@126.com
 */
public class MyDialog {
	/** 
     * 得到自定义的progressDialog 
     * @param context 
     * @param msg 
     * @return 
     */  
	
    public static Dialog createLoadingDialog(Context context, int theme,String msg) {  
  
        LayoutInflater inflater = LayoutInflater.from(context);  
        View v = inflater.inflate(R.layout.loading, null);// 得到加载view  
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局  
        // main.xml中的ImageView  
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);  
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字  
        // 加载动画  
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(  
                context, R.anim.loading_animation);  
        // 使用ImageView显示动画  
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);  
        tipTextView.setText(msg);// 设置加载信息  
  
        Dialog loadingDialog = new Dialog(context, R.style.custom_dialog);// 创建自定义样式dialog  
  
        loadingDialog.setCancelable(false);// 不可以用“返回键”取消  
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.MATCH_PARENT,  
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局  
        return loadingDialog;  
  
    }
    
    public static Dialog createInterestDialog(Context context, int theme) {  
    	  
    	LayoutInflater inflater = LayoutInflater.from(context);  
        View v = inflater.inflate(R.layout.intrest_dialog, null);// 得到加载view  
        Dialog interestDialog = new Dialog(context,theme);// 创建自定义样式dialog 
        Window window = interestDialog.getWindow();
		window.setGravity(Gravity.TOP);//在底部弹出
		window.setWindowAnimations(R.style.InterestDialog);
  
        interestDialog.setCancelable(false);// 不可以用“返回键”取消  
        interestDialog.setContentView(v);// 设置布局  
        return interestDialog;  
  
    }
    
    public static Dialog createAboutDialog(Context context, int theme) {  
  	  
    	LayoutInflater inflater = LayoutInflater.from(context);  
        View v = inflater.inflate(R.layout.about_dialog, null);// 得到加载view  
        Dialog interestDialog = new Dialog(context,theme);// 创建自定义样式dialog 
        Window window = interestDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);//在底部弹出
		window.setWindowAnimations(R.style.InterestDialog);
		interestDialog.setCanceledOnTouchOutside(true);
        interestDialog.setCancelable(true);// 不可以用“返回键”取消  
        interestDialog.setContentView(v);// 设置布局  
        return interestDialog;  
  
    }
   
    public static Dialog createAccountDialog(Context context, int theme) {  
    	  
    	LayoutInflater inflater = LayoutInflater.from(context);  
        View v = inflater.inflate(R.layout.account_dialog, null);// 得到加载view  
        Dialog interestDialog = new Dialog(context,theme);// 创建自定义样式dialog 
        Window window = interestDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);//在底部弹出
		window.setWindowAnimations(R.style.InterestDialog);
		interestDialog.setCanceledOnTouchOutside(true);
        interestDialog.setCancelable(true);// 不可以用“返回键”取消  
        interestDialog.setContentView(v);// 设置布局  
        return interestDialog;  
  
    }
}
