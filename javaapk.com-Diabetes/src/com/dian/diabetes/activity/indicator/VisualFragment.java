package com.dian.diabetes.activity.indicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.activity.indicator.adapter.IndicatorAdapter;
import com.dian.diabetes.db.IndicateBo;
import com.dian.diabetes.db.dao.Indicate;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 自测指标首页，adapter数据采取先读数据库，根据key来判断是否存在记录， 再确定是否新建,点击顶部按钮切换中间fragment
 * 
 * @author longbh
 * 
 */
public class VisualFragment extends BaseFragment implements OnClickListener {

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.data_list)
	private ListView listView;
	private IndicatorActivity activity;

	private IndicateBo bo;
	private List<Indicate> data;
	private Map<String, Indicate> maps;
	private IndicatorAdapter adapter;

	public static VisualFragment getInstance() {
		VisualFragment fragment = new VisualFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (IndicatorActivity) getActivity();
		bo = new IndicateBo(activity);
		data = new ArrayList<Indicate>();
		initData();
		adapter = new IndicatorAdapter(activity, data);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_visual_layout,
				container, false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initData() {
		maps = bo.getMapIndicate(ContantsUtil.DEFAULT_TEMP_UID);
		Indicate indicate = maps.get("weight");
		if (indicate == null) {
			indicate = new Indicate();
			indicate.setKey("weight");
			indicate.setLast_value(0);
			indicate.setLevel(0);
			indicate.setName("体重");
			indicate.setUpdate_time(System.currentTimeMillis());
			indicate.setUnion("kg");
			indicate.setUp_down(-1);
			indicate.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
			long id = bo.saveIndicate(indicate);
			indicate.setId(id);
			maps.put("weight", indicate);
		}
		indicate.setImg(R.drawable.indicate_weight);
		data.add(indicate);

		indicate = maps.get("waist");
		if (indicate == null) {
			indicate = new Indicate();
			indicate.setKey("waist");
			indicate.setLast_value(0);
			indicate.setLevel(0);
			indicate.setName("腰围");
			indicate.setUpdate_time(System.currentTimeMillis());
			indicate.setUnion("cm");
			indicate.setUp_down(-1);
			indicate.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
			long id = bo.saveIndicate(indicate);
			indicate.setId(id);
			maps.put("waist", indicate);
		}
		indicate.setImg(R.drawable.indicate_yao);
		data.add(indicate);

		indicate = maps.get("bmi");
		if (indicate == null) {
			indicate = new Indicate();
			indicate.setKey("bmi");
			indicate.setLast_value(0);
			indicate.setLevel(0);
			indicate.setName("BMI");
			indicate.setUpdate_time(System.currentTimeMillis());
			indicate.setUnion("kg/m^2");
			indicate.setUp_down(-1);
			indicate.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
			long id = bo.saveIndicate(indicate);
			indicate.setId(id);
			maps.put("bmi", indicate);
		}
		indicate.setImg(R.drawable.indicate_bmi);
		data.add(indicate);

		indicate = maps.get("openPress");
		if (indicate == null) {
			indicate = new Indicate();
			indicate.setKey("openPress");
			indicate.setLast_value(0);
			indicate.setLevel(0);
			indicate.setName("血压");
			indicate.setUpdate_time(System.currentTimeMillis());
			indicate.setUnion("mmHg");
			indicate.setUp_down(-1);
			indicate.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
			long id = bo.saveIndicate(indicate);
			indicate.setId(id);
			maps.put("openPress", indicate);
		}
		indicate.setImg(R.drawable.indicate_xueya);
		data.add(indicate);

		indicate = maps.get("ch");
		if (indicate == null) {
			indicate = new Indicate();
			indicate.setKey("ch");
			indicate.setLast_value(0);
			indicate.setLevel(0);
			indicate.setName("血脂");
			indicate.setUpdate_time(System.currentTimeMillis());
			indicate.setUnion("mmol/L");
			indicate.setUp_down(-1);
			indicate.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
			long id = bo.saveIndicate(indicate);
			indicate.setId(id);
			maps.put("ch", indicate);
		}
		indicate.setImg(R.drawable.indicate_xuezhi);
		data.add(indicate);
		// 心率
		indicate = maps.get("heart");
		if (indicate == null) {
			indicate = new Indicate();
			indicate.setKey("heart");
			indicate.setLast_value(0);
			indicate.setLevel(0);
			indicate.setName("心率");
			indicate.setUpdate_time(System.currentTimeMillis());
			indicate.setUnion("次/min");
			indicate.setUp_down(-1);
			indicate.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
			long id = bo.saveIndicate(indicate);
			indicate.setId(id);
			maps.put("heart", indicate);
		}
		indicate.setImg(R.drawable.indicate_heart);
		data.add(indicate);
		// 血红蛋白
		indicate = maps.get("protein");
		if (indicate == null) {
			indicate = new Indicate();
			indicate.setKey("protein");
			indicate.setLast_value(0);
			indicate.setLevel(0);
			indicate.setName("糖化血红蛋白");
			indicate.setUpdate_time(System.currentTimeMillis());
			indicate.setUnion("%");
			indicate.setUp_down(-1);
			indicate.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
			long id = bo.saveIndicate(indicate);
			indicate.setId(id);
			maps.put("protein", indicate);
		}
		indicate.setImg(R.drawable.icon_danbai);
		data.add(indicate);
		
		//血脂其他指标
		indicate = maps.get("tg");
		if (indicate == null) {
			indicate = new Indicate();
			indicate.setKey("tg");
			indicate.setLast_value(0);
			indicate.setLevel(0);
			indicate.setName("甘油三脂");
			indicate.setUpdate_time(System.currentTimeMillis());
			indicate.setUnion("mmol/L");
			indicate.setUp_down(-1);
			indicate.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
			long id = bo.saveIndicate(indicate);
			indicate.setId(id);
			maps.put("tg", indicate);
		}
		
		indicate = maps.get("hdl");
		if (indicate == null) {
			indicate = new Indicate();
			indicate.setKey("hdl");
			indicate.setLast_value(0);
			indicate.setLevel(0);
			indicate.setName("高密度脂蛋白胆固醇");
			indicate.setUpdate_time(System.currentTimeMillis());
			indicate.setUnion("mmol/L");
			indicate.setUp_down(-1);
			indicate.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
			long id = bo.saveIndicate(indicate);
			indicate.setId(id);
			maps.put("hdl", indicate);
		}
		
		indicate = maps.get("ldl");
		if (indicate == null) {
			indicate = new Indicate();
			indicate.setKey("ldl");
			indicate.setLast_value(0);
			indicate.setLevel(0);
			indicate.setName("低密度脂蛋白胆固醇");
			indicate.setUpdate_time(System.currentTimeMillis());
			indicate.setUnion("mmol/L");
			indicate.setUp_down(-1);
			indicate.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
			long id = bo.saveIndicate(indicate);
			indicate.setId(id);
			maps.put("ldl", indicate);
		}
	}

	private void initView(View view) {
		backBtn.setOnClickListener(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View parent,
					int position, long id) {
				Indicate indicate = data.get(position);
				ContantsUtil.IDICATE_UPDATE = false;
				activity.startIndicateDetail(indicate.getId(),
						indicate.getKey(), indicate.getUnion(),
						indicate.getName(), maps);
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			activity.finish();
			break;
		}
	}
}
