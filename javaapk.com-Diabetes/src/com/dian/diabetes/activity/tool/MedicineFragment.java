package com.dian.diabetes.activity.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
import com.dian.diabetes.service.SportService;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.widget.NGridView;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 公共选择器界面， MedicineFragment
 * 
 * @author Administrator
 * 
 */
public class MedicineFragment extends BasicFragmentDialog implements
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
	private Stack<String> names;
	
	private ToolListAdapter toolAdapter;
	private ToolGridAdapter gridAdapter;
	private ToolListAdapter searchAdapter;
	private BasicActivity activity;
	private CallBack callBack;

	// 分类
	private long groupId = -1;
	private String category = "";
	private boolean isCreate = false;

	public static MedicineFragment getInstance() {
		MedicineFragment toolDialog = new MedicineFragment();
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
		names = new Stack<String>();
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
				if(callBack != null){
					callBack.callBack(data.get(position));
				}
				dismiss();
			}
		});
		classGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View parent,
					int position, long id) {
				GroupDto normal = curData.get(position);
				ids.push(groupId);
				names.push(category);
				loadData(normal.id,normal.name);
			}
		});
		dataList.setAdapter(toolAdapter);
		classGridView.setAdapter(gridAdapter);
		searchLis.setAdapter(searchAdapter);
		searchLis.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(callBack != null){
					callBack.callBack(searchData.get(position));
				}
				dismiss();
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
		HttpServiceUtil.request(ContantsUtil.MEDICINE_TOOL_SEARCH, "post", params, new HttpServiceUtil.CallBack() {
			@Override
			public void callback(String json) {
				loadingGress.setVisibility(View.GONE);
				try {
					JSONObject dataJson = new JSONObject(json);
					if(CheckUtil.checkStatusOk(dataJson.getInt("status"))){
						searchData.clear();
						SportService service = new SportService();
						service.convertMedicineSearch(searchData, dataJson.getJSONArray("data"),ContantsUtil.MEDICINE);
						searchAdapter.setLocal(false);
						searchAdapter.notifyDataSetChanged();
					}else{
						ToastTool.showToast(dataJson.getInt("status"), context);
					}
				} catch (JSONException e) {
					Toast.makeText(context, "拉取药品分类失败", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		});
	}
	
	//初始化组
	private void loadGroup(){
		loading.setVisibility(View.VISIBLE);
		HttpServiceUtil.request(ContantsUtil.MEDICINE_TOOL, "post", null, new HttpServiceUtil.CallBack() {
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
						Toast.makeText(context, groupData.getString("message"), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					Toast.makeText(context, "获取药品列表失败", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		});
		List<Normal> temp = normalBo.listNormal(ContantsUtil.MEDICINE);
		data.addAll(temp);
		toolAdapter.setLocal(true);
	}

	//load 详情
	private void loadData(long parentId,final String category) {
		groupId = parentId;
		this.category = category;
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
		HttpServiceUtil.request(ContantsUtil.MEDICINE_TOOL_DATA, "post", params, new HttpServiceUtil.CallBack() {
			@Override
			public void callback(String json) {
				loading.setVisibility(View.GONE);
				try {
					JSONObject dataJson = new JSONObject(json);
					if(CheckUtil.checkStatusOk(dataJson.getInt("status"))){
						data.clear();
						SportService service = new SportService();
						service.convertMedicine(data, dataJson.getJSONArray("data"),ContantsUtil.MEDICINE,category);
						toolAdapter.setLocal(false);
						toolAdapter.notifyDataSetChanged();
					}else{
						Toast.makeText(context, dataJson.getString("message"), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					Toast.makeText(context, "拉取用药分类失败", Toast.LENGTH_SHORT).show();
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
		void callBack(Normal normal);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			dismiss();
			break;
		}
	}
	
	//监听后退键，返回上一层
	protected void onBackPressed(){
		if(searchLis.getVisibility() == View.VISIBLE){
			searchLis.setVisibility(View.GONE);
			return;
		}
		if(!ids.empty()){
			long id = ids.pop();
			String temp = names.pop();
			loadData(id,temp);
		}else{
			dismiss();
		}
	}
}
