package com.dian.diabetes.activity.sugar;

import java.util.List;

import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.dialog.PopDialog;

import android.content.Context;

/**
 * 统计弹出选择框  最近月、最近三月、最近一星期
 */
public class TimePopUp extends PopDialog {

	public TimePopUp(Context context) {
		super(context);
	}

	@Override
	protected void initData(List<MapModel> data) {
		MapModel modela = new MapModel("last_week", "最近一周");
		data.add(modela);
		MapModel model = new MapModel("last_month", "最近一个月");
		data.add(model);
		MapModel model1 = new MapModel("last_three_month", "最近三个月");
		data.add(model1);
	}

}
