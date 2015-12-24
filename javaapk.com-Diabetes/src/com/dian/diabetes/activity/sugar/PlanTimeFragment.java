package com.dian.diabetes.activity.sugar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
import com.dian.diabetes.activity.BasicFragmentDialog;
import com.dian.diabetes.activity.sugar.adapter.PlanTimeAdapter;
import com.dian.diabetes.activity.sugar.model.TimeModel;
import com.dian.diabetes.db.AlarmBo;
import com.dian.diabetes.db.PropertyBo;
import com.dian.diabetes.db.UserBo;
import com.dian.diabetes.db.dao.User;
import com.dian.diabetes.dialog.TimeDialog;
import com.dian.diabetes.dialog.TimeDialog.CallBack;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

public class PlanTimeFragment extends BasicFragmentDialog implements
		OnClickListener {
	@ViewInject(id = R.id.time_list)
	private ListView timeList;
	@ViewInject(id = R.id.back_btn)
	private Button backBtn;

	private List<TimeModel> data;
	private PlanActivity activity;
	private PlanTimeAdapter adapter;
	private PropertyBo bo;
	private AlarmBo alarmBo;
	private UserBo userBo;
	private User user;
	private TimeDialog timeDialog;
	private DecimalFormat format;

	public static PlanTimeFragment Instance() {
		return new PlanTimeFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (PlanActivity) context;
		bo = new PropertyBo(activity);
		alarmBo = new AlarmBo(activity);
		userBo = new UserBo(activity);
		data = new ArrayList<TimeModel>();
		format = new DecimalFormat("00");
		initData();
		timeDialog = new TimeDialog(activity, "时间设置");
		adapter = new PlanTimeAdapter(activity, data);
	}

	private void initData() {
		data.add(new TimeModel(ContantsUtil.BRECKFAST, ContantsUtil.EAT_PRE,
				"早餐前"));
		data.add(new TimeModel(ContantsUtil.BRECKFAST, ContantsUtil.EAT_AFTER,
				"早餐后"));
		data.add(new TimeModel(ContantsUtil.LAUNCH, ContantsUtil.EAT_PRE, "中餐前"));
		data.add(new TimeModel(ContantsUtil.LAUNCH, ContantsUtil.EAT_AFTER,
				"中餐后"));
		data.add(new TimeModel(ContantsUtil.DINNER, ContantsUtil.EAT_PRE, "晚餐前"));
		data.add(new TimeModel(ContantsUtil.DINNER, ContantsUtil.EAT_AFTER,
				"晚餐后"));
		data.add(new TimeModel(ContantsUtil.SLEEP_PRE, ContantsUtil.EAT_PRE,
				"睡前"));
		user = userBo.getUserByServerId(ContantsUtil.DEFAULT_TEMP_UID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_plan_time, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		backBtn.setOnClickListener(this);
		timeList.setAdapter(adapter);
		timeList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final int type = data.get(position).getType();
				final int subType = data.get(position).getSubType();
				String value = (String) Config.getProperty("clock" + type
						+ subType);
				String args[] = value.split(":");
				timeDialog.show(StringUtil.toInt(args[0]),
						StringUtil.toInt(args[1]));
				timeDialog.setCallBack(new CallBack() {
					@Override
					public boolean callBack(int hour, int mini) {
						String value = format.format(hour) + ":"
								+ format.format(mini);
						bo.updateByKey("clock" + type + subType,
								ContantsUtil.DEFAULT_TEMP_UID, value);
						alarmBo.updateAlarm(ContantsUtil.DEFAULT_TEMP_UID,
								ContantsUtil.curUser.getService_uid(),
								ContantsUtil.SUGAR_ALARM,
								StringUtil.toInt(type + "" + subType), hour,
								mini);
						Config.setProperty("clock" + type + subType, value);
						adapter.notifyDataSetChanged();
						user.setTime_update(System.currentTimeMillis());
						userBo.updateUser(user);
						activity.getPreference().putBoolean(
								Preference.HAS_UPDATE, true);
						return true;
					}
				});
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			dismiss();
			break;
		}
	}
}
