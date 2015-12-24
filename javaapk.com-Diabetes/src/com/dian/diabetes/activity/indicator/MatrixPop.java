package com.dian.diabetes.activity.indicator;

import java.util.List;

import android.content.Context;

import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.dialog.GPopDialog;

public class MatrixPop extends GPopDialog {

	public MatrixPop(Context context) {
		super(context);
	}

	@Override
	protected void initData(List<MapModel> data) {
	}

	public void addData(List<String> lists) {
		data.clear();
		for (String dto : lists) {
			MapModel model = new MapModel("", dto);
			data.add(model);
		}
	}

	public void addString(String date) {
		boolean has = false;
		for(MapModel item : data){
			if(item.getValue().equals(date)){
				has = true;
				break;
			}
		}
		if(!has){
			MapModel model = new MapModel("", date);
			data.add(model);
		}
	}

	public void clearData() {
		data.clear();
	}
}