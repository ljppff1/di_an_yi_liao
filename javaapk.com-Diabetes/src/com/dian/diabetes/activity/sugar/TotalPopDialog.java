package com.dian.diabetes.activity.sugar;

import java.util.List;

import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.dialog.GPopDialog;
import android.content.Context;

/**
 * 统计弹出选择框
 */
public class TotalPopDialog extends GPopDialog {

	public TotalPopDialog(Context context) {
		super(context);
	}

	@Override
	protected void initData(List<MapModel> data) {
		MapModel modela = new MapModel("breakfast_pre", "总体");
		data.add(modela);
		MapModel model = new MapModel("breakfast_pre", "早餐前");
		data.add(model);
		MapModel model1 = new MapModel("breakfast_after", "早餐后");
		data.add(model1);
		MapModel model2 = new MapModel("lunch_pre", "中餐前");
		data.add(model2);
		MapModel model3 = new MapModel("lunch_after", "中餐后");
		data.add(model3);
		MapModel model4 = new MapModel("dinner_pre", "晚餐前");
		data.add(model4);
		MapModel model5 = new MapModel("dinner_after", "晚餐后");
		data.add(model5);
		MapModel model6 = new MapModel("dinner_after", "睡前");
		data.add(model6);
	}

}
