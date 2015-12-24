package com.dian.diabetes.activity.sugar.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.activity.SBaseAdapter;
import com.dian.diabetes.db.dao.DiabetesCache;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.ValueWidget;

public class SugarCacheAdapter extends SBaseAdapter {

	private DecimalFormat format;
	private float levelPre[];
	private float levelAfter[];
	private int cacheNum = 0;

	public SugarCacheAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_sugar_cache);
		format = new DecimalFormat("##0.00");
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.dayView = (TextView) convertView.findViewById(R.id.cache_day);
		holder.timeView = (TextView) convertView.findViewById(R.id.cache_time);
		holder.valueView = (ValueWidget) convertView.findViewById(R.id.value);
		holder.selectView = (ImageView) convertView.findViewById(R.id.check);
		holder.newState = (TextView) convertView.findViewById(R.id.new_state);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject,int position) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		DiabetesCache cache = (DiabetesCache) itemObject;
		holder.dayView.setText(DateUtil.parseToString(cache.getCreate_time(),
				DateUtil.yyyyMMdd));
		holder.timeView.setText(DateUtil.parseToString(cache.getCreate_time(),
				DateUtil.HHmm));
		int level = -1;
		// if(modelData.getSub_type() == ContantsUtil.EAT_PRE){
		// 方案待定
		level = StringUtil.level(cache.getValue(), levelPre);
		// }else{
		// level = StringUtil.level(modelData.getValue(), levelAfter);
		// }
		holder.valueView.setValue(format, cache.getValue(), level);
		if (cache.isSelect()) {
			holder.selectView.setImageResource(R.drawable.check_select);
		} else {
			holder.selectView.setImageResource(R.drawable.check_normal);
		}
		if(cache.isNew()){
			holder.newState.setVisibility(View.VISIBLE);	
		}else{
			holder.newState.setVisibility(View.GONE);
		}
	}

	class ViewHolder {
		TextView dayView;
		TextView timeView;
		TextView newState;
		ValueWidget valueView;
		ImageView selectView;
	}

	public void setLevelPre(float[] levelPre) {
		this.levelPre = levelPre;
	}

	public void setLevelAfter(float[] levelAfter) {
		this.levelAfter = levelAfter;
	}

	public void setCacheNum(int cacheNum) {
		this.cacheNum = cacheNum;
	}

	public void check(){
		
	}
}
