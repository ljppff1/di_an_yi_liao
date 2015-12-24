package com.dian.diabetes.activity.sport;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.activity.assess.AssessActivity;
import com.dian.diabetes.activity.eat.adapter.EatAdapter;
import com.dian.diabetes.activity.sport.adapter.SportAdapter;
import com.dian.diabetes.db.EatBo;
import com.dian.diabetes.db.PropertyBo;
import com.dian.diabetes.db.SportBo;
import com.dian.diabetes.db.UserBo;
import com.dian.diabetes.db.dao.Eat;
import com.dian.diabetes.db.dao.Sport;
import android.os.AsyncTask;
import com.dian.diabetes.tool.CallBack;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.MutiProgress;
import com.dian.diabetes.widget.NListView;
import com.dian.diabetes.widget.anotation.ViewInject;

public class DateViewFragment extends BaseFragment implements OnClickListener {
	
	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.new_eat)
	private ImageButton newEat;
	@ViewInject(id = R.id.eat_list)
	private NListView dataList;
	@ViewInject(id = R.id.sport_list)
	private NListView sportList;
	@ViewInject(id = R.id.total)
	private TextView totalValue;
	@ViewInject(id = R.id.total_net)
	private TextView totalNetView;
	@ViewInject(id = R.id.sport_out)
	private TextView sportOutView;
	@ViewInject(id = R.id.sport_out1)
	private TextView sportOutView1;
	@ViewInject(id = R.id.eat_in)
	private TextView eatView;
	@ViewInject(id = R.id.eat_in1)
	private TextView eatView1;
	@ViewInject(id = R.id.calore_progress)
	private MutiProgress progress;
	@ViewInject(id = R.id.eat_controller)
	private RelativeLayout eatController;
	@ViewInject(id = R.id.sub_value)
	private TextView netView;
	@ViewInject(id = R.id.pre)
	private ImageButton preBtn;
	@ViewInject(id = R.id.next)
	private ImageButton nextBtn;
	@ViewInject(id = R.id.current_day)
	private TextView currentDay;
	@ViewInject(id = R.id.sport_controller)
	private RelativeLayout sportController;
	@ViewInject(id = R.id.eat_contain)
	private LinearLayout eatContainer;
	@ViewInject(id = R.id.sport_null)
	private TextView sportNull;
	@ViewInject(id = R.id.eat_null)
	private TextView eatNull;
	@ViewInject(id = R.id.loading)
	private ProgressBar loading;
	
	private EatBo bo;
	private SportBo sportBo;
	private PropertyBo proBo;
	private List<Eat> data;
	private List<Sport> sportData;
	private BasicActivity activity;
	private EatAdapter adapter;
	private SportAdapter sportAdapter;
	private boolean isCreate = false;
	private boolean sportOpen = false;
	private Date curentDate = new Date();
	private DecimalFormat format;
	
	//统计数据
	private float subNet = 0;
	private float sportOut = 0;
	private float eatIn = 0;
	private float totalSurport = 3000;

	public static DateViewFragment getInstance() {
		return new DateViewFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (BasicActivity) context;
		data = new ArrayList<Eat>();
		adapter = new EatAdapter(activity, data);
		bo = new EatBo(activity);
		sportBo = new SportBo(activity);
		sportData = new ArrayList<Sport>();
		sportAdapter = new SportAdapter(activity, sportData);
		totalSurport = ContantsUtil.curUser.getSupport();
		format = new DecimalFormat("##0.0");
		proBo = new PropertyBo(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sport_dataview, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		newEat.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		preBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		eatController.setOnClickListener(this);
		loadData(0);
		if(!isCreate){
			loadSurport();
			isCreate = true;
		}else{
			setViewValue();
		}
		if(sportOpen){
			dataList.setVisibility(View.VISIBLE);
		}else{
			dataList.setVisibility(View.GONE);
		}
		dataList.setAdapter(adapter);
		sportList.setAdapter(sportAdapter);
		sportList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View parent, int position,
					long id) {
				Sport sport = sportData.get(position);
				toAddFragment(sport,false);
			}
		});
		loadSurport();
	}
	
	private void loadData(int sub){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(curentDate);
		calendar.add(Calendar.DATE, sub);
		curentDate = new Date(calendar.getTimeInMillis());
		String today = DateUtil.parseToDate(System.currentTimeMillis());
		String temp = DateUtil.parseToDate(curentDate.getTime());
		if (today.equals(temp)) {
			currentDay.setText("今天");
			nextBtn.setEnabled(false);
		} else {
			currentDay.setText(DateUtil.parseToString(curentDate,
					DateUtil.yyyyMMdd));
			nextBtn.setEnabled(true);
		}
		new DataTask().execute();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			activity.finish();
			break;
		case R.id.new_eat:
			toAddFragment(null,true);
			break;
		case R.id.eat_controller:
			sportOpen = !sportOpen;
			if(sportOpen){
				eatContainer.setVisibility(View.VISIBLE);
			}else{
				eatContainer.setVisibility(View.GONE);
			}
			break;
		case R.id.pre:
			loadData(-1);
			break;
		case R.id.next:
			loadData(1);
			break;
		}
	}

	private void toAddFragment(Sport sport,boolean isAdd) {
		String tag = "new_sport";
		FragmentManager manager = context.getSupportFragmentManager();
		SportAddFragment tempFragment = (SportAddFragment) context
				.getSupportFragmentManager().findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = SportAddFragment.getInstance(isAdd,totalSurport);
			tempFragment.setCallback(new CallBack() {
				@Override
				public void callBack() {
					loadData(0);
				}
			});
		}
		if(sport != null){
			tempFragment.setSport(sport);
		}
		if (!tempFragment.isAdded()) {
			tempFragment.setDate(curentDate);
			tempFragment.show(manager, tag);
		}
	}
	
	boolean isLoad = false;
	private void loadSurport(){
		if(isLoad){
			return;
		}
		isLoad = true;
		long time = ContantsUtil.curUser.getSurport_time();
		if(System.currentTimeMillis() - time > ContantsUtil.deltaDay){
			Map<String,Object> params = new HashMap<String, Object>();
			params.put("mid", ContantsUtil.DEFAULT_TEMP_UID);
			loading.setVisibility(View.VISIBLE);
			HttpServiceUtil.request(ContantsUtil.CALORE_SURPORT, "post", params, new HttpServiceUtil.CallBack() {
				@Override
				public void callback(String json) {
					isLoad = false;
					if(getActivity() == null){
						return;
					}
					loading.setVisibility(View.GONE);
					try {
						JSONObject dataJson = new JSONObject(json);
						if(CheckUtil.checkStatusOk(dataJson.getInt("status"))){
							totalSurport = StringUtil.toFloat(dataJson.get("data"));
							totalValue.setText(format.format(totalSurport) + "");
							totalNetView.setText(format.format(subNet-totalSurport)+"");
							ContantsUtil.curUser.setSupport(totalSurport);
							ContantsUtil.curUser.setSurport_time(System.currentTimeMillis());
							new UserBo(context).updateUser(ContantsUtil.curUser);
							setNetPesent();
						}else if(dataJson.getInt("status") == 5000110){
							activity.finish();
							Toast.makeText(context, "您还未做一次完整的问卷答题，请先答题", Toast.LENGTH_SHORT).show();
							activity.startActivity(null, AssessActivity.class);
						} else{
							ToastTool.showToast(dataJson.getInt("status"),context);
						}
					} catch (JSONException e) {
						Toast.makeText(context, "数据解析出错", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}
			});
		}else{
			totalSurport = ContantsUtil.curUser.getSupport();
			totalValue.setText(totalSurport + "");
			totalNetView.setText((subNet-totalSurport)+"");
		}
	}
	
	private class DataTask extends AsyncTask<Object, Object, Object> {
		
		private List<Eat> eats;
		private List<Sport> sports;
		
		@Override
		protected Object doInBackground(Object... arg0) {
			eatIn = 0;
			sportOut = 0;
			eats = bo.listDayEat(ContantsUtil.DEFAULT_TEMP_UID, DateUtil.parseToString(curentDate, DateUtil.yyyyMMdd));
			for(Eat item : eats){
				eatIn += item.getTotal();
			}
			sports = sportBo.listDaySport(ContantsUtil.DEFAULT_TEMP_UID, DateUtil.parseToString(curentDate, DateUtil.yyyyMMdd));
			for(Sport item : sports){
				sportOut += item.getTotal();
			}
			subNet = eatIn - sportOut;
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			sportData.clear();
			data.clear();
			data.addAll(eats);
			if(eats.size() != 0){
				totalSurport = eats.get(0).getSurport();
			}
			sportData.addAll(sports);
			if(sports.size() != 0){
				totalSurport = sports.get(0).getSuport();
			}
			if(sports.size() == 0 && eats.size()==0){
				totalSurport = ContantsUtil.curUser.getSupport();
			}
			adapter.notifyDataSetChanged();
			sportAdapter.notifyDataSetChanged();
			setViewValue();
		}
	}
	
	private void setViewValue(){
		eatView.setText(format.format(eatIn) + "");
		eatView1.setText(format.format(eatIn) + "");
		sportOutView.setText(format.format(sportOut) + "");
		sportOutView1.setText("-" + format.format(sportOut));
		totalValue.setText(format.format(totalSurport));
		totalNetView.setText(format.format(subNet-totalSurport)+"");
		progress.setValue(sportOut, eatIn);
		setNetPesent();
		if(data.size() == 0){
			eatNull.setVisibility(View.VISIBLE);
			dataList.setVisibility(View.GONE);
		}else{
			eatNull.setVisibility(View.GONE);
			dataList.setVisibility(View.VISIBLE);
		}
		if(sportData.size() == 0){
			sportNull.setVisibility(View.VISIBLE);
			sportList.setVisibility(View.GONE);
		}else{
			sportNull.setVisibility(View.GONE);
			sportList.setVisibility(View.VISIBLE);
		}
	}
	
	private void setNetPesent() {
		float persent = (subNet - totalSurport)/totalSurport;
		if (persent > Config.getFloatPro("highCalore")) {
			netView.setText("热量过高");
			netView.setTextColor(activity.getResources().getColor(
					R.color.ear_color3));
		} else if (persent < Config.getFloatPro("lowCalore")) {
			netView.setText("热量不足");
			netView.setTextColor(activity.getResources().getColor(
					R.color.ear_color1));
		} else {
			netView.setText("热量均衡");
			netView.setTextColor(activity.getResources().getColor(
					R.color.ear_color2));
		}
	}
}
