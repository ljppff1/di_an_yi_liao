package com.dian.diabetes.activity.assess;

import java.util.List;

import android.content.Context;

import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.dialog.BaseListDialog;

/**
 * 答题种用到的弹出框，选择强度之类的
 * @author hua
 *
 */
public class StrongDialog extends BaseListDialog {

	public StrongDialog(Context context) {
		super(context);
	}

	@Override
	protected void initData(List<MapModel> data) {
//		MapModel model = new MapModel("low", "轻体力劳动");
//		data.add(model);
//		model = new MapModel("mid", "中体力劳动");
//		data.add(model);
//		model = new MapModel("high", "重体力劳动");
//		data.add(model);
	}

	public void addData(String message) {
		String[] splite = message.split(";");
		for (int i = 0; i < splite.length; i++) {
			MapModel model = new MapModel("strong" + i, splite[i]);
			data.add(model);
		}
	}

}
