package com.dian.diabetes.activity.eat;

import java.util.List;

import android.content.Context;

import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.dialog.BaseListDialog;

/**
 * 餐别下拉列表
 * @author hua
 *
 */
public class DinStageDialog extends BaseListDialog {

	public DinStageDialog(Context context) {
		super(context);
	}

	@Override
	protected void initData(List<MapModel> data) {
		MapModel model = new MapModel(0, "break", "早餐");
		data.add(model);
		model = new MapModel(1, "break_add", "上午加餐");
		data.add(model);
		model = new MapModel(2, "lunch", "中餐");
		data.add(model);
		model = new MapModel(3, "lunch_add", "下午加餐");
		data.add(model);
		model = new MapModel(4, "dinner", "晚餐");
		data.add(model);
		model = new MapModel(5, "dinner_add", "晚上加餐");
		data.add(model);
	}

}
