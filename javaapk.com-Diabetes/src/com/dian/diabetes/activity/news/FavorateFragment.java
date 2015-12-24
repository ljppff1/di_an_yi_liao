package com.dian.diabetes.activity.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicFragmentDialog;
import com.dian.diabetes.activity.TranLoading;
import com.dian.diabetes.activity.news.adapter.FavorateAdapter;
import com.dian.diabetes.db.FavorateNewsBo;
import com.dian.diabetes.db.dao.Favorate;
import com.dian.diabetes.db.dao.News;
import com.dian.diabetes.service.NewsService;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

public class FavorateFragment extends BasicFragmentDialog implements
		OnClickListener {
	
	@ViewInject(id = R.id.news_list)
	private ListView listView;
	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.edit)
	private Button editBtn;
	@ViewInject(id = R.id.loading)
	private ProgressBar loading;
	
	private NewListActivity activity;

	private List<News> data;
	private FavorateAdapter adapter;
	private FavorateNewsBo bo;
	private NewsService service;

	public static FavorateFragment getInstance() {
		return new FavorateFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bo = new FavorateNewsBo(context);
		activity = (NewListActivity) getActivity();
		service = new NewsService();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_favorate_list, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		backBtn.setOnClickListener(this);
		editBtn.setOnClickListener(this);
		data = new ArrayList<News>();
		adapter = new FavorateAdapter(context, data,this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				openDetailFragment(data.get(position));
			}
		});
		loadData();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			dismiss();
			break;
		case R.id.edit:
			if(adapter.getEdit()){
				adapter.setEdit(false);
				editBtn.setText("编辑");
			}else{
				adapter.setEdit(true);
				editBtn.setText("完成");
			}
			adapter.notifyDataSetChanged();
			break;
		}
	}
	
	private void loadData() {
		Map<String, Object> params = new HashMap<String, Object>();
		loading.setVisibility(View.VISIBLE);
		HttpServiceUtil.request(ContantsUtil.FAVORATE_LIST, "post", params,
				new HttpServiceUtil.CallBack() {
					@Override
					public void callback(String json) {
						loading.setVisibility(View.GONE);
						try {
							JSONObject jsonData = new JSONObject(json);
							if (CheckUtil.checkStatusOk(jsonData.getInt("status"))) {
								List<News> pageList = service.convertCommon(jsonData.getString("data"));
								data.addAll(pageList);
								adapter.notifyDataSetChanged();
							} else {
								ToastTool.showToast(jsonData.getInt("status"),context);
							}
						} catch (JSONException e) {
//							Toast.makeText(context, "数据解析出错",
//									Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
				});
	}

	private void openDetailFragment(News detail) {
		String tag = "news_detail_fragment";
		FragmentManager manager = context.getSupportFragmentManager();
		NewsDetailFragment tempFragment = (NewsDetailFragment) context
				.getSupportFragmentManager().findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = NewsDetailFragment.getInstance();
		}
		tempFragment.setNews(detail);
		if (!tempFragment.isAdded()) {
			tempFragment.show(manager, tag);
		}
	}

	public void showLoading(){
		loading.setVisibility(View.VISIBLE);
	}
	
	public void hideLoading(){
		loading.setVisibility(View.GONE);
	}
}
