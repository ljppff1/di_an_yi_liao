package com.dian.diabetes.activity.indicator;

import java.util.List;

import android.content.Context;

import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.dialog.PopDialog;

public class IndicateDialog extends PopDialog {

	public IndicateDialog(Context context) {
		super(context);
	}

	@Override
	protected void initData(List<MapModel> data) {
		MapModel model = new MapModel("weight", "体重");
		data.add(model);
		model = new MapModel("waist", "腰围");
		data.add(model);
		model = new MapModel("bmi", "BMI");
		data.add(model);
		model = new MapModel("openPress", "血压");
		data.add(model);
		model = new MapModel("ch", "血脂");
		data.add(model);
		model = new MapModel("heart", "心率");
		data.add(model);
		model = new MapModel("protein", "血红蛋白");
		data.add(model);
	}

}
