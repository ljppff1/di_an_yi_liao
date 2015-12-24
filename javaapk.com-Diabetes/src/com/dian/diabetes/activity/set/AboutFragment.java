package com.dian.diabetes.activity.set;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicFragmentDialog;
import com.dian.diabetes.utils.FileUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

/**
 * 关于界面
 * @author Administrator
 *
 */
public class AboutFragment extends BasicFragmentDialog implements OnClickListener{
	
	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.web_content)
	private WebView webview;
	@ViewInject(id = R.id.title)
	private TextView titleView;
	
	private String content = "";

	public static AboutFragment getInstance(){
		AboutFragment fragment = new AboutFragment();
		return fragment;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getDialog().getWindow().getAttributes().windowAnimations = R.style.dialog_left_right_anim;		
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//content = FileUtil.readTxtFromAsset(context, "html/help.html");
	}
	
	/**
	 * onCreateView方法中才是真正的绘制Fragment的界面，即这个返回的View
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_help_layout,
				container, false);
		fieldView(view);
		initView(view);
		return view;
	}
	
	private void initView(View view){
		//网页设置
		WebSettings settings = webview.getSettings();
		//settings.setJavaScriptEnabled(true);
		//settings.setBlockNetworkImage(true);
		settings.setDefaultTextEncodingName("utf-8");
		webview.setBackgroundColor(0);
		webview.getBackground().setAlpha(0);
		webview.loadDataWithBaseURL(null, content, "text/html", "utf-8",
				"about:blank");
		webview.loadUrl("file:///android_asset/html/help.html");
		titleView.setText("帮助中心");
		backBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.back_btn:
			dismiss();
			break;
		}
	}
}
