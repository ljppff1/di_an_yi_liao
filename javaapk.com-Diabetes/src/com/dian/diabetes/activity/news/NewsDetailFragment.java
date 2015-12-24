package com.dian.diabetes.activity.news;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicFragmentDialog;
import com.dian.diabetes.db.FavorateNewsBo;
import com.dian.diabetes.db.dao.Favorate;
import com.dian.diabetes.db.dao.News;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class NewsDetailFragment extends BasicFragmentDialog implements
		OnClickListener {

	// 顶部标题
	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.info_detail_content)
	private WebView webview;
	@ViewInject(id = R.id.title)
	private TextView titleView;
	// 加载状态相关控件
	@ViewInject(id = R.id.loading)
	private RelativeLayout loadingView;
	@ViewInject(id = R.id.loading_fail)
	private LinearLayout loadFailView;
	@ViewInject(id = R.id.fail_img)
	private ImageView loadAgain;
	@ViewInject(id = R.id.favorate)
	private Button favorate;

	private FavorateNewsBo favorateBo;
	private News newsDetail;

	public static NewsDetailFragment getInstance() {
		return new NewsDetailFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		favorateBo = new FavorateNewsBo(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_news_detail, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		backBtn.setOnClickListener(this);
		// 加载提示处理
		loadAgain.setOnClickListener(this);
		favorate.setOnClickListener(this);

		// 网页加载
		WebSettings settings = webview.getSettings();
		settings.setJavaScriptEnabled(true);
		//settings.setBlockNetworkImage(true);
		settings.setDefaultTextEncodingName("utf-8");
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webview.setWebViewClient(client);
		webview.setBackgroundColor(0);
		webview.getBackground().setAlpha(0);
		loadWebView();
	}

	private void loadWebView() {
		titleView.setText(newsDetail.getTitle());
		String content = "";
		String meta = "<html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0,max-scale=1.0, user-scalable=no\">";
		content += meta
				+ "</head><body style=\"padding-top:52px;color:#444444 !important;\">";
		content += "<h3>" + newsDetail.getSummary() + "</h3>";
		content += "<p style=\"font-size:13px;\">发布时间："
				+ DateUtil.parseToString(newsDetail.getDay()) + "</p>";
		content += "<div style=\"font-size:18px;line-height:150%;letter-spacing: 0.3mm;\">"
				+ newsDetail.getContent() + "</div>";
		content += "</body></html>";
		webview.loadDataWithBaseURL(null, content, "text/html", "utf-8",
				"about:blank");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			dismiss();
			break;
		case R.id.fail_img:
			webview.reload();
			break;
		case R.id.favorate:
			if (CheckUtil.isNull(ContantsUtil.DEFAULT_TEMP_UID)) {
				Toast.makeText(context, "您还未登陆请先登录", Toast.LENGTH_SHORT).show();
				return;
			}
			favorateServer(newsDetail.getSid());
			// Favorate favorate = new Favorate();
			// favorate.setAuthor(newsDetail.getAuthor());
			// favorate.setContent(newsDetail.getContent());
			// favorate.setDay(newsDetail.getDay());
			// favorate.setServer_mid(ContantsUtil.DEFAULT_TEMP_UID);
			// favorate.setSummary(newsDetail.getSummary());
			// favorate.setTitle(newsDetail.getTitle());
			// favorate.setThumbnail(newsDetail.getThumbnail());
			// favorateBo.saveNews(favorate);
			break;
		}
	}

	private void favorateServer(long newsId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("postId", newsId);
		HttpServiceUtil.request(ContantsUtil.NEWS_FAVORATE, "post", params,
				new HttpServiceUtil.CallBack() {
					@Override
					public void callback(String json) {
						try {
							JSONObject jsonData = new JSONObject(json);
							if (CheckUtil.checkStatusOk(jsonData
									.getInt("status"))) {
								Toast.makeText(context, "收藏成功",
										Toast.LENGTH_SHORT).show();
								dismiss();
							} else {
								Toast.makeText(context, "收藏失败",
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(context, "收藏失败", Toast.LENGTH_SHORT)
									.show();
						}
					}
				});
	}

	public void setNews(News detail) {
		this.newsDetail = detail;
	}

	/**
	 * webview 加载状态监听
	 */
	private WebViewClient client = new WebViewClient() {

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			Log.d("onReceivedError", "error");
		}
	};

	/**
	 * 当加载失败时
	 */
	public void toFailPage() {
		loadFailView.setVisibility(View.VISIBLE);
		loadingView.setVisibility(View.GONE);
	}

}
