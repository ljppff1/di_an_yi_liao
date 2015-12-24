package com.dian.diabetes.activity.sport;

import java.util.List;

import android.content.Context;

import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.dialog.BaseListDialog;
import com.dian.diabetes.dto.NormalDto;
import com.dian.diabetes.tool.CommonUtil;
import com.dian.diabetes.utils.CheckUtil;

public class SportFeelDialog extends BaseListDialog{
	
	public SportFeelDialog(Context context) {
		super(context);
	}
	
	public void setData(List<NormalDto> dtos){
		data.clear();
		for(NormalDto dto : dtos){
			MapModel model = new MapModel("strength" + dto.strength, CommonUtil.getValue("strength" + dto.strength));
			model.setPrice(dto.heat);
			model.setStrength(dto.strength);
			data.add(model);
		}
	}
	
	public void addModel(NormalDto dto){
		MapModel model = new MapModel("strength" + dto.strength, CommonUtil.getValue("strength" + dto.strength));
		model.setPrice(dto.heat);
		model.setStrength(dto.strength);
		data.add(model);
	}
	
	public void clear(){
		data.clear();
	}

	@Override
	protected void initData(List<MapModel> data) {
	}

	public float getValue(int strenth){
		float price = 0;
		for(MapModel model : data){
			if(model.getStrength() == strenth){
				price = model.getPrice();
				break;
			}
		}
		return price;
	}
	
	public float getValue(String stenth){
		float price = 0;
		for(MapModel model : data){
			if(CheckUtil.checkEquels(model.getStrength(),stenth)){
				price = model.getPrice();
				break;
			}
		}
		return price;
	}
}
