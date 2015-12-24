package com.dian.diabetes.activity.sugar;

import java.util.List;

import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.dialog.PopDialog;

import android.content.Context;

/**
 * 模式弹出选择框
 */
public class TotalListPopDialog extends PopDialog {

	public TotalListPopDialog(Context context) {
		super(context);
	}

	@Override
	protected void initData(List<MapModel> data) {
		MapModel modela = new MapModel("count", "次数");
		data.add(modela);
		MapModel model = new MapModel("avg_value", "平均值");
		data.add(model);
		MapModel model1 = new MapModel("high_value", "最高值");
		data.add(model1);
		MapModel model2 = new MapModel("lower_value", "最低值");
		data.add(model2);
	}

}
