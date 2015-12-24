package com.dian.diabetes.activity.indicator;

import java.util.List;

import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.dialog.PopDialog;

import android.content.Context;

/**
 * 统计弹出选择框 最近月、最近三月、最近一星期
 */
public class TimePopUp extends PopDialog {

	public TimePopUp(Context context) {
		super(context);
	}

	@Override
	protected void initData(List<MapModel> data) {
		MapModel model = new MapModel("last_month", "最近一月");
		data.add(model);
		MapModel model1 = new MapModel("half_year", "最近半年");
		data.add(model1);
		MapModel modela = new MapModel("year", "最近一年");
		data.add(modela);
		modela = new MapModel("tow_year", "最近两年");
		data.add(modela);
		modela = new MapModel("three_year", "最近三年");
		data.add(modela);
	}

}
