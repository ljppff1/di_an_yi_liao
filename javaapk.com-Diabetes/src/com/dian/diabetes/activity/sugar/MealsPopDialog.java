package com.dian.diabetes.activity.sugar;

import java.util.List;

import android.content.Context;

import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.dialog.PopDialog;

public class MealsPopDialog extends PopDialog {

	public MealsPopDialog(Context context) {
		super(context);
	}

	@Override
	protected void initData(List<MapModel> data) {
		MapModel modela = new MapModel("eat_pre", "餐前");
		data.add(modela);
		MapModel model = new MapModel("eat_after", "餐后");
		data.add(model);
	}

}