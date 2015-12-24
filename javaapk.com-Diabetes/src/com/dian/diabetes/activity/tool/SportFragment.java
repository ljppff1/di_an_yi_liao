package com.dian.diabetes.activity.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.activity.BasicFragmentDialog;
import com.dian.diabetes.activity.tool.adapter.ToolGridAdapter;
import com.dian.diabetes.activity.tool.adapter.ToolListAdapter;
import com.dian.diabetes.db.NormalBo;
import com.dian.diabetes.db.dao.Normal;
import com.dian.diabetes.dto.GroupDto;
import com.dian.diabetes.dto.NormalDto;
import com.dian.diabetes.service.SportService;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.widget.NGridView;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 公共选择器界面， SPORT_TYPE
 * 
 * @author Administrator
 * 
 */
public class SportFragment extends BasicFragmentDialog implements
		OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.gridview)
	private NGridView classGridView;
	@ViewInject(id = R.id.data_list)
	private ListView dataList;
	@ViewInject(id = R.id.content)
	private EditText searchView;
	@ViewInject(id = R.id.search_list)
	private ListView searchLis;
	@ViewInject(id = R.id.loading_pb)
	private LinearLayout loading;
	@ViewInject(id = R.id.loading)
	private ProgressBar loadingGress;

	private NormalBo normalBo;

	private List<Normal> data;
	private List<GroupDto> gridData;
	private List<GroupDto> curData;
	private List<Normal> searchData;
	private Stack<Long> ids;
	
	private ToolListAdapter toolAdapter;
	private ToolGridAdapter gridAdapter;
	private ToolListAdapter searchAdapter;
	private BasicActivity activity;
	private CallBack callBack;

	// 分类
	private long groupId = -1;
	private boolean isCreate = false;

	public static SportFragment getInstance() {
		SportFragment toolDialog = new SportFragment();
		return toolDialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (BasicActivity) context;
		data = new ArrayList<Normal>();
		searchData = new ArrayList<Normal>();
		normalBo = new NormalBo(activity);
		gridData = new ArrayList<GroupDto>();
		curData = new ArrayList<GroupDto>();
		ids = new Stack<Long>();
		toolAdapter = new ToolListAdapter(activity, data, normalBo);
		gridAdapter = new ToolGridAdapter(activity, curData);
		searchAdapter = new ToolListAdapter(activity, searchData, normalBo);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tool_dialog, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		backBtn.setOnClickListener(this);
		dataList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View parent,
					int position, long id) {
				loadItem(data.get(position));
			}
		});
		classGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View parent,
					int position, long id) {
				GroupDto normal = curData.get(position);
				ids.push(groupId);
				loadData(normal.id);
			}
		});
		dataList.setAdapter(toolAdapter);
		classGridView.setAdapter(gridAdapter);
		searchLis.setAdapter(searchAdapter);
		searchLis.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				loadItem(searchData.get(position));
			}
		});

		// 搜索框
		searchView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable edit) {
				if(CheckUtil.isNull(edit)){
					searchLis.setVisibility(View.GONE);
					searchData.clear();
				}else{
					searchLis.setVisibility(View.VISIBLE);
					loadSearch(edit+"");
				}
			}
		});
		searchView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				searchLis.setVisibility(View.VISIBLE);
			}
		});
		if(!isCreate){
			loadGroup();
			isCreate = true;
		}
	}
	
	private void loadSearch(String key){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("keyword", key);
		loadingGress.setVisibility(View.VISIBLE);
		HttpServiceUtil.request(ContantsUtil.SPORT_TOOL_SEARCH, "post", params, new HttpServiceUtil.CallBack() {
			@Override
			public void callback(String json) {
				loadingGress.setVisibility(View.GONE);
				try {
					JSONObject dataJson = new JSONObject(json);
					if(CheckUtil.checkStatusOk(dataJson.getInt("status"))){
						searchData.clear();
						SportService service = new SportService();
						service.convertArray(searchData, dataJson.getJSONArray("data"),ContantsUtil.SPORT_TYPE);
						searchAdapter.setLocal(false);
						searchAdapter.notifyDataSetChanged();
					}else{
						ToastTool.showToast(dataJson.getInt("status"), context);
					}
				} catch (JSONException e) {
					Toast.makeText(context, "拉取运动分类失败", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		});
	}
	
	private void loadItem(final Normal normal){
		loading.setVisibility(View.VISIBLE);
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("issueId", normal.getIssueId());
		HttpServiceUtil.request(ContantsUtil.SPORT_TOOL_ITEM, "post", params, new HttpServiceUtil.CallBack() {
			@Override
			public void callback(String json) {
				loading.setVisibility(View.GONE);
				try {
					JSONObject groupData = new JSONObject(json);
					if(CheckUtil.checkStatusOk(groupData.getInt("status"))){
						List<NormalDto> lists = new ArrayList<NormalDto>();
						JSONArray array = groupData.getJSONArray("data");
						for(int i=0;i<array.length();i++){
							NormalDto dto = new NormalDto();
							dto.of(array.getJSONObject(i));
							lists.add(dto);
						}
						normalBo.saveNormal(normal);
						if (callBack != null) {
							callBack.callBack(lists,normal,array.toString());
						}
					}else{
						ToastTool.showToast(groupData.getInt("status"), context);
					}
				} catch (JSONException e) {
					Toast.makeText(context, "运动列表失败", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				dismiss();
			}
		});
	}
	
	//初始化组
	private void loadGroup(){
		loading.setVisibility(View.VISIBLE);
		HttpServiceUtil.request(ContantsUtil.SPORT_TOOL, "post", null, new HttpServiceUtil.CallBack() {
			@Override
			public void callback(String json) {
				loading.setVisibility(View.GONE);
				try {
					JSONObject groupData = new JSONObject(json);
					if(CheckUtil.checkStatusOk(groupData.getInt("status"))){
						curData.clear();
						SportService service = new SportService();
						service.convertGroupArray(gridData,curData, groupData.getJSONArray("data"),groupId);
						gridAdapter.notifyDataSetChanged();
					}else{
						ToastTool.showToast(groupData.getInt("status"), context);
					}
				} catch (JSONException e) {
					Toast.makeText(context, "运动列表失败", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		});
		List<Normal> temp = normalBo.listNormal(ContantsUtil.SPORT_TYPE);
		data.addAll(temp);
		toolAdapter.setLocal(true);
	}

	//load 详情
	private void loadData(long parentId) {
		groupId = parentId;
		curData.clear();
		for(GroupDto dto : gridData){
			if(dto.parentId == groupId){
				curData.add(dto);
			}
		}
		gridAdapter.notifyDataSetChanged();
		if(curData.size() != 0){
			data.clear();
			toolAdapter.notifyDataSetChanged();
			return;
		}
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("categoryId", parentId);
		loading.setVisibility(View.VISIBLE);
		HttpServiceUtil.request(ContantsUtil.SPORT_TOOL_DATA, "post", params, new HttpServiceUtil.CallBack() {
			@Override
			public void callback(String json) {
				loading.setVisibility(View.GONE);
				try {
					JSONObject dataJson = new JSONObject(json);
					if(CheckUtil.checkStatusOk(dataJson.getInt("status"))){
						data.clear();
						SportService service = new SportService();
						service.convertArray(data, dataJson.getJSONArray("data"),ContantsUtil.SPORT_TYPE);
						toolAdapter.setLocal(false);
						toolAdapter.notifyDataSetChanged();
					}else{
						ToastTool.showToast(dataJson.getInt("status"), context);
					}
				} catch (JSONException e) {
					Toast.makeText(context, "拉取运动分类失败", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		});
		toolAdapter.notifyDataSetChanged();
	}

	public void setCallBack(CallBack back) {
		this.callBack = back;
	}

	public interface CallBack {
		void callBack(List<NormalDto> data,Normal normal,String dtoStr);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			dismiss();
			break;
		}
	}
	
	protected void onBackPressed(){
		if(searchLis.getVisibility() == View.VISIBLE){
			searchLis.setVisibility(View.GONE);
			return;
		}
		if(!ids.empty()){
			long id = ids.pop();
			loadData(id);
		}else{
			dismiss();
		}
	}
}
