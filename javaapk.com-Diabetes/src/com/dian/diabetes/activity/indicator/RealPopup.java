package com.dian.diabetes.activity.indicator;

import java.util.List;

import android.content.Context;

import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.dialog.GPopDialog;
import com.dian.diabetes.dto.IndicateDto;

public class RealPopup extends GPopDialog{

	public RealPopup(Context context) {
		super(context);
	}

	@Override
	protected void initData(List<MapModel> data) {
	}
	
	public void addData(List<IndicateDto> temp){
		data.clear();
		for(IndicateDto dto : temp){
			MapModel model = new MapModel(dto.item_name, dto.item_name);
			data.add(model);
		}
	}

}
