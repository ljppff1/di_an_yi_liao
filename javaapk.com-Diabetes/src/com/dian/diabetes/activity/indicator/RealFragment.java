package com.dian.diabetes.activity.indicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.activity.indicator.adapter.RealAdapter;
import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.dto.IndicateDto;
import com.dian.diabetes.dto.InfoDto;
import com.dian.diabetes.dto.ListDto;
import com.dian.diabetes.tool.CommonUtil;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.widget.anotation.ViewInject;
import com.dian.diabetes.zxing.CaptureActivity;

/**
 * 实验室指标首页，顶部html，下面是一个listview, 新版修正为app方式
 * 
 * @author longbh
 * 
 */
public class RealFragment extends BaseFragment implements OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.data_list)
	private ListView listView;
	@ViewInject(id = R.id.time_switch)
	private TextView timeView;
	@ViewInject(id = R.id.matrix_code)
	private TextView matrixCode;
	@ViewInject(id = R.id.time_con)
	private LinearLayout timeCon;
	@ViewInject(id = R.id.scancer)
	private ImageButton matrix;

	// head view
	private View headerView;
	private TextView nameLabel;
	private TextView sexLabel;
	private TextView ageLabel;
	private TextView hospitalLabel;
	private LinearLayout generaCon;
	private TextView dataList;
	private TextView generaSee;
	private TextView generaCheck;
	private TextView headerTitle;
	private LinearLayout headerContent;

	// dialog
	private TimeListPop pop;
	private MatrixPop matrixPop;

	private List<IndicateDto> data;
	private LinkedHashMap<Long, ListDto> popList;
	private RealAdapter adapter;
	private boolean isCreate = false;
	private InfoDto dataDto;
	private final int CAPTURE = 2001;
	private int type = 1;
	private String pre_code = "";

	private String mainScanCode;
	private String typeCode;

	private IndicatorActivity activity;

	public static RealFragment getInstance(String code, String type) {
		RealFragment fragment = new RealFragment();
		Bundle bundle = new Bundle();
		bundle.putString("code", code);
		bundle.putString("type", type);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainScanCode = getArguments().getString("code");
		typeCode = getArguments().getString("type");
		activity = (IndicatorActivity) getActivity();
		data = new ArrayList<IndicateDto>();
		popList = new LinkedHashMap<Long, ListDto>();
		adapter = new RealAdapter(activity, data);
		pop = new TimeListPop(activity);
		pop.setCallBack(new TimeListPop.CallBack() {
			@Override
			public void callBack(int model) {
				MapModel map = pop.getModel(model);
				List<String> lists = (List<String>) map.getChild();
				matrixCode.setText("条形码　" + lists.get(0));
				getAll(lists.get(0), -1);
				matrixPop.addData(lists);
			}
		});
		matrixPop = new MatrixPop(activity);
		matrixPop.setCallBack(new MatrixPop.CallBack() {
			@Override
			public void callBack(int model) {
				MapModel map = matrixPop.getModel(model);
				matrixCode.setText("条形码　" + map.getValue());
				getAll(map.getValue(), -1);
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_real_layout, container,
				false);
		fieldView(view);
		initView(inflater, view);
		return view;
	}

	private void initView(LayoutInflater inflater, View view) {
		matrixCode.setText("条形码");
		timeView.setText("时间选择");
		backBtn.setOnClickListener(this);
		matrix.setOnClickListener(this);
		matrixCode.setOnClickListener(this);
		timeCon.setOnClickListener(this);
		headerView = inflater.inflate(R.layout.real_report_header, null);
		initHeaderView();
		listView.addHeaderView(headerView);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View parent,
					int position, long id) {
				if (position == 0) {
					return;
				}
				IndicateDto realValue = data.get(position - 1);
				ContantsUtil.REAL_UPDATE = false;
				activity.startRealDetail(realValue.units, realValue.item_name,
						realValue.max, realValue.min, realValue.hasChart,
						realValue.result, data);
			}
		});
		listView.setSelection(lastVisiblePosition);
		if (!isCreate) {
			if (!CheckUtil.isNull(mainScanCode)) {
				pre_code = mainScanCode;
				int flag = 0;
				if ("QR_CODE".equals(typeCode)) {
					flag = 0;
				} else {
					flag = 1;
				}
				getAll(pre_code, flag);
			} else {
				loadList();
			}
			isCreate = true;
		} else {
			setPathology(dataDto);
		}
		listView.setAdapter(adapter);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case CAPTURE:
			if (data == null) {
				break;
			}
			Bundle bundle = data.getExtras();
			pre_code = bundle.getString("result");
			int flag = 0;
			if ("QR_CODE".equals(bundle.getString("name"))) {
				flag = 0;
			} else {
				flag = 1;
			}
			Toast.makeText(context, "二维码扫描成功：正在查询结果", Toast.LENGTH_SHORT)
					.show();
			getAll(pre_code, flag);
			break;
		}
	}

	private void initHeaderView() {
		nameLabel = (TextView) headerView.findViewById(R.id.name);
		sexLabel = (TextView) headerView.findViewById(R.id.sex);
		ageLabel = (TextView) headerView.findViewById(R.id.age);
		hospitalLabel = (TextView) headerView.findViewById(R.id.hospital);
		generaCon = (LinearLayout) headerView.findViewById(R.id.bingli);
		generaCheck = (TextView) headerView.findViewById(R.id.bingli_check);
		generaSee = (TextView) headerView.findViewById(R.id.bingli_see);
		dataList = (TextView) headerView.findViewById(R.id.data_list);
		headerTitle = (TextView) headerView.findViewById(R.id.header_title);
		headerContent = (LinearLayout) headerView
				.findViewById(R.id.header_content);
		headerTitle.setOnClickListener(this);
	}

	private void setPathology(InfoDto dataDto) {
		if (dataDto == null) {
			return;
		}
		generaSee.setText(dataDto.seeinfo);
		generaCheck.setText(dataDto.rap_idea);
		nameLabel.setText(dataDto.patient_name);
		sexLabel.setText(CommonUtil.getValue("indicatesex" + dataDto.sex));
		ageLabel.setText(dataDto.age + dataDto.ageunit);
		hospitalLabel.setText(dataDto.dia_doctor);
		timeView.setText(dataDto.date);
		matrixCode.setText("条形码　" + dataDto.matrixCode);
		data.clear();
		if (dataDto.datas.size() > 0) {
			data.addAll(dataDto.datas);
		}
		if (type == 0) {
			dataList.setVisibility(View.GONE);
			generaCon.setVisibility(View.VISIBLE);
		} else if (type == 1) {
			dataList.setVisibility(View.VISIBLE);
			generaCon.setVisibility(View.GONE);
		} else {
			dataList.setVisibility(View.GONE);
			generaCon.setVisibility(View.GONE);
		}
		adapter.notifyDataSetChanged();
	}

	private void loadList() {
		activity.show();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mid", ContantsUtil.DEFAULT_TEMP_UID);
		HttpServiceUtil.request(ContantsUtil.LIST_USER_INDICATE, "post",
				params, new HttpServiceUtil.CallBack() {
					@Override
					public void callback(String json) {
						activity.hide();
						try {
							JSONObject groupData = new JSONObject(json);
							if (CheckUtil.checkStatusOk(groupData
									.getInt("status"))) {
								if (groupData.has("data")) {
									JSONArray dataObj = groupData
											.getJSONArray("data");
									String firstCode = "";
									long firstTime = 0;
									for (int i = 0; i < dataObj.length(); i++) {
										JSONObject itemObj = dataObj
												.getJSONObject(i);
										Long key = itemObj
												.getLong("appr_date2");
										String barCode = itemObj
												.getString("bar_code");
										if (i == 0) {
											firstTime = key;
											firstCode = barCode;
										}
										ListDto dto = popList.get(key);
										if (dto == null) {
											dto = new ListDto();
											dto.time = key;
											dto.addData(barCode);
										} else {
											dto.addData(barCode);
										}
										popList.put(key, dto);
									}
									pop.addData(popList.values());
									MapModel map = pop.getModel(0);
									matrixPop.addData(map.getChild());
									if (CheckUtil.isNull(firstCode)) {
										Toast.makeText(context, "当前用户暂无数据",
												Toast.LENGTH_SHORT).show();
									} else {
										getAll(firstCode, -1);
										timeView.setText(DateUtil
												.parseToDate(firstTime));
										matrixCode.setText(firstCode);
										listView.setAdapter(adapter);
									}
								}
							} else {
								ToastTool.showToast(groupData.getInt("status"),
										context);
							}
						} catch (JSONException e) {
							Toast.makeText(context, "读取指标数据失败",
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
				});
	}

	// 获取一个二维码的数据
	private void getAll(String code, final int barFlag) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bar_code", code);
		params.put("flagBar", barFlag);
		params.put("mid", ContantsUtil.DEFAULT_TEMP_UID);
		activity.show();
		HttpServiceUtil.request(ContantsUtil.GET_ALL, "post", params,
				new HttpServiceUtil.CallBack() {
					@Override
					public void callback(String json) {
						activity.hide();
						try {
							JSONObject groupData = new JSONObject(json);
							if (CheckUtil.checkStatusOk(groupData
									.getInt("status"))) {
								if (groupData.has("data")) {
									dataDto = new InfoDto();
									JSONObject data = groupData
											.getJSONObject("data");
									type = data.getInt("type");
									dataDto.clear();
									if (type == 0) {
										dataDto.of(data
												.getJSONObject("plafPathology"));
										setPathology(dataDto);
										if (barFlag != -1) {
											if (CheckUtil.checkEquels(
													timeView.getText(),
													dataDto.date)) {
												matrixPop
														.addString(dataDto.matrixCode);
											}
											pop.addData(dataDto);
										}
									} else if (type == 1) {
										if (data.has("des")) {
											dataDto.ofDes(data
													.getJSONObject("des"));
										}
										if (data.has("projects")) {
											dataDto.ofIndicate(data
													.getJSONArray("projects"));
										}
										if (barFlag != -1) {
											if (CheckUtil.checkEquels(
													timeView.getText(),
													dataDto.date)) {
												matrixPop
														.addString(dataDto.matrixCode);
											}
											pop.addData(dataDto);
										}
										setPathology(dataDto);
									} else {
										Toast.makeText(context,
												"扫描到的二维码对应数据不存在",
												Toast.LENGTH_SHORT).show();
									}
								}
							} else {
								ToastTool.showToast(groupData.getInt("status"),
										context);
							}
						} catch (JSONException e) {
							Toast.makeText(context, "读取指标数据失败",
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
				});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			activity.finish();
			break;
		case R.id.scancer:
			Intent intent = new Intent();
			intent.setClass(context, CaptureActivity.class);
			startActivityForResult(intent, CAPTURE);
			break;
		case R.id.time_con:
			if (popList.size() > 1) {
				pop.showAsDropDown(view);
			}
			break;
		case R.id.matrix_code:
			if (matrixPop.size() > 1) {
				matrixPop.showAsDropDown(view);
			}
			break;
		case R.id.header_title:
			if (headerContent.getVisibility() == View.GONE) {
				headerContent.setVisibility(View.VISIBLE);
			} else {
				headerContent.setVisibility(View.GONE);
			}
			break;
		}
	}

	public void onStop() {
		super.onStop();
		lastVisiblePosition = listView.getFirstVisiblePosition();
	}
}
