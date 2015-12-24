package com.dian.diabetes.activity.tool.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.SBaseAdapter;
import com.dian.diabetes.db.NormalBo;
import com.dian.diabetes.db.dao.Normal;

public class ToolListAdapter extends SBaseAdapter{
	
	private NormalBo bo;
	private boolean state = false;

	public ToolListAdapter(Context context, List<?> data,NormalBo bo) {
		super(context, data, R.layout.item_tool_layout);
		this.bo = bo;
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.toolName = (TextView) convertView.findViewById(R.id.tool_name);
		holder.deleteBtn = (ImageView) convertView.findViewById(R.id.delete);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject,final int position) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		final Normal normal = (Normal) itemObject;
		holder.toolName.setText(normal.getName());
		holder.deleteBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				bo.delete(normal.getId());
				data.remove(position);
				notifyDataSetChanged();
			}
		});
		if(state){
			holder.deleteBtn.setVisibility(View.VISIBLE);
		}else{
			holder.deleteBtn.setVisibility(View.GONE);
		}
	}

	class ViewHolder{
		TextView toolName;
		ImageView deleteBtn;
	}
	
	public void setLocal(boolean state){
		this.state = state;
	}
}
