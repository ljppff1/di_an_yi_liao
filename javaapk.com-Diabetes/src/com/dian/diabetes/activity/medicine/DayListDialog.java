package com.dian.diabetes.activity.medicine;

import java.util.List;

import android.content.Context;

import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.dialog.BaseListDialog;

public class DayListDialog extends BaseListDialog {
	
	public DayListDialog(Context context) {
		super(context);
	}

	@Override
	protected void initData(List<MapModel> data) {
		MapModel model = new MapModel("one_one", "一天一次");
		data.add(model);
		model = new MapModel("one_tow", "一天两次");
		data.add(model);
		model = new MapModel("one_tow", "一天三次");
		data.add(model);
	}
}
