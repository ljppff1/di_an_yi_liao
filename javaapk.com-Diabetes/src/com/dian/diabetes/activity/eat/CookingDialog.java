package com.dian.diabetes.activity.eat;

import java.util.List;

import android.content.Context;

import com.dian.diabetes.activity.sugar.model.MapModel;
import com.dian.diabetes.dialog.BaseListDialog;
import com.dian.diabetes.dto.EatNormalDto;

/**
 * 烹饪方式弹出框，暂时废弃了
 * @author hua
 *
 */
public class CookingDialog extends BaseListDialog {

	public CookingDialog(Context context) {
		super(context);
	}

	@Override
	protected void initData(List<MapModel> data) {
	}
	
	public void setData(List<EatNormalDto> dtos){
		for(EatNormalDto dto : dtos){
			MapModel model = new MapModel("cooking" + dto.cookId, dto.cookingName);
			model.setPrice(dto.heat);
			model.setHeatLevel(dto.heatLevel);
			data.add(model);
		}
	}

}
