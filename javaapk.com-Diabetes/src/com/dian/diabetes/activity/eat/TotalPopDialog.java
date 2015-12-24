package com.dian.diabetes.activity.eat;

import java.util.List;

import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.dialog.PopDialog;

import android.content.Context;

/**
 * 统计弹出选择框
 */
public class TotalPopDialog extends PopDialog {

	public TotalPopDialog(Context context) {
		super(context);
	}

	@Override
	protected void initData(List<MapModel> data) {
		MapModel modela = new MapModel("calore_all", "综合统计");
		data.add(modela);
		MapModel model = new MapModel("calore_total", "总体热量");
		data.add(model);
		MapModel model1 = new MapModel("calore_net", "热量净值");
		data.add(model1);
		MapModel model2 = new MapModel("calore_struct", "热量结构");
		data.add(model2);
		MapModel model3 = new MapModel("eat_struct", "饮食结构");
		data.add(model3);
		MapModel model4 = new MapModel("eat_struct", "营养结构");
		data.add(model4);
	}

}
