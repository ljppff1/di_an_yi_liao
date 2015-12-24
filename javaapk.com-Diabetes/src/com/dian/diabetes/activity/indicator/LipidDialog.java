package com.dian.diabetes.activity.indicator;

import java.util.List;

import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.dialog.PopDialog;

import android.content.Context;

/**
 * 统计弹出选择框 选择血脂指标
 */
public class LipidDialog extends PopDialog {

	public LipidDialog(Context context) {
		super(context);
	}

	@Override
	protected void initData(List<MapModel> data) {
		MapModel model = new MapModel("ch", "胆固醇");
		data.add(model);
		MapModel model1 = new MapModel("tg", "甘油三脂");
		data.add(model1);
		MapModel modela = new MapModel("hdl", "高密度脂蛋白胆固醇");
		data.add(modela);
		modela = new MapModel("ldl", "低密度脂蛋白胆固醇");
		data.add(modela);
	}

}
