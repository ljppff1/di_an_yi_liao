package com.dian.diabetes.activity.sugar.adapter;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.MBaseAdapter;
import com.dian.diabetes.db.dao.Common;
import com.dian.diabetes.db.dao.Diabetes;
import com.dian.diabetes.tool.CommonUtil;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.widget.ValueWidget;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SugarEntryAdapter extends MBaseAdapter {

	private DecimalFormat format;
	private DisplayImageOptions options;
	private Map<String, Common> plugMap;

	public SugarEntryAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_sugar_entry);
		format = new DecimalFormat("##0.00");
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.content_bg)
				.showImageForEmptyUri(R.drawable.content_bg)
				.showImageOnLoading(R.drawable.content_bg)
				.showImageOnFail(R.drawable.content_bg).cacheInMemory(true)
				.cacheOnDisc(true).build();
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.testType = (TextView) convertView
				.findViewById(R.id.sugar_test_type);
		holder.testTime = (TextView) convertView.findViewById(R.id.test_time);
		holder.value = (ValueWidget) convertView.findViewById(R.id.value);
		holder.maskState = (ImageView) convertView
				.findViewById(R.id.mask_state);
		holder.statePlug = (LinearLayout) convertView
				.findViewById(R.id.state_plug);
		holder.icon1 = (ImageView) convertView.findViewById(R.id.plug1);
		holder.icon2 = (ImageView) convertView.findViewById(R.id.plug2);
		holder.icon3 = (ImageView) convertView.findViewById(R.id.plug3);
		holder.icon4 = (ImageView) convertView.findViewById(R.id.plug4);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject) {
		Diabetes modelData = (Diabetes) itemObject;
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.icon1.setVisibility(View.GONE);
		holder.icon2.setVisibility(View.GONE);
		holder.icon3.setVisibility(View.GONE);
		holder.icon4.setVisibility(View.GONE);
		if (modelData.getId() == null) {
			if (modelData.getLevel() == -1) {
				holder.value.setValue("未测", -1);
			} else {
				holder.value.setValue("待测", -1);
			}
			holder.testTime.setText("");
			holder.maskState.setVisibility(View.GONE);
		} else {
			holder.testTime.setText(DateUtil.parseToString(
					modelData.getCreate_time(), DateUtil.HHmm));
			holder.value.setValueLevel(format, modelData.getValue(),
					modelData.getLevel());
			if (!CheckUtil.isNull(modelData.getMark())) {
				holder.maskState.setVisibility(View.VISIBLE);
			} else {
				holder.maskState.setVisibility(View.GONE);
			}

			String feel = modelData.getFeel();
			if (!CheckUtil.isNull(feel)) {
				String[] plugs = modelData.getFeel().split(",");
				int length = plugs.length;
				if (length > 0) {
					ImageLoader.getInstance().displayImage(
							plugMap.get(plugs[0]).getImage(), holder.icon1,
							options);
					holder.icon1.setVisibility(View.VISIBLE);
				}
				if (length > 1) {
					ImageLoader.getInstance().displayImage(
							plugMap.get(plugs[1]).getImage(), holder.icon2,
							options);
					holder.icon2.setVisibility(View.VISIBLE);
				}
				if (length > 2) {
					ImageLoader.getInstance().displayImage(
							plugMap.get(plugs[2]).getImage(), holder.icon3,
							options);
					// fetcher.loadImage(plugMap.get(plugs[2]).getImage(),
					// holder.icon3);
					holder.icon3.setVisibility(View.VISIBLE);
				}
				if (length > 3) {
					ImageLoader.getInstance().displayImage(
							plugMap.get(plugs[3]).getImage(), holder.icon4,
							options);
					// fetcher.loadImage(plugMap.get(plugs[3]).getImage(),
					// holder.icon4);
					holder.icon4.setVisibility(View.VISIBLE);
				}
			}
		}
		holder.testType.setText(CommonUtil.getValue("diabetes"
				+ modelData.getType() + modelData.getSub_type()));
	}

	class ViewHolder {
		TextView testType;
		TextView testTime;
		ValueWidget value;
		ImageView maskState;
		LinearLayout statePlug;

		ImageView icon1;
		ImageView icon2;
		ImageView icon3;
		ImageView icon4;
	}

	public void setPlugMap(Map<String, Common> plugMap) {
		this.plugMap = plugMap;
	}

}
