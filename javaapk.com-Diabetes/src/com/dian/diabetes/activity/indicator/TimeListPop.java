package com.dian.diabetes.activity.indicator;

import java.util.Collection;
import java.util.List;

import android.content.Context;

import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.dialog.GPopDialog;
import com.dian.diabetes.dto.InfoDto;
import com.dian.diabetes.dto.ListDto;
import com.dian.diabetes.utils.DateUtil;

/**
 * 时间弹出选择框
 * @author longbh
 *
 */
public class TimeListPop extends GPopDialog {

	public TimeListPop(Context context) {
		super(context);
	}

	@Override
	protected void initData(List<MapModel> data) {
	}

	public void addData(Collection<ListDto> lists) {
		data.clear();
		for (ListDto dto : lists) {
			MapModel model = new MapModel("", DateUtil.parseToDate(dto.time));
			model.setChild(dto.datas);
			data.add(model);
		}
	}
	
	public void addData(InfoDto dataDto){
		boolean state = false;
		for(MapModel model : data){
			if(model.getValue().equals(dataDto.date)){
				model.getChild().add(dataDto.matrixCode);
				state = true;
			}
		}
		if(!state){
			MapModel model = new MapModel("", dataDto.date);
			model.addList(dataDto.matrixCode);
			data.add(model);
		}
	}
}