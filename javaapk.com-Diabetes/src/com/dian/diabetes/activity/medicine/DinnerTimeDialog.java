package com.dian.diabetes.activity.medicine;

import java.util.List;

import android.content.Context;

import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.dialog.BaseListDialog;

public class DinnerTimeDialog extends BaseListDialog{

	public DinnerTimeDialog(Context context) {
		super(context);
	}

	@Override
	protected void initData(List<MapModel> data) {
		MapModel model = new MapModel("eat_pre", "餐前");
		data.add(model);
		model = new MapModel("eat_after", "餐后");
		data.add(model);
	}

}
