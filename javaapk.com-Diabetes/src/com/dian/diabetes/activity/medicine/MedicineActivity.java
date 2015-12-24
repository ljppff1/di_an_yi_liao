package com.dian.diabetes.activity.medicine;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.activity.medicine.adapter.MedicineAdapter;
import com.dian.diabetes.db.MedicineBo;
import com.dian.diabetes.db.dao.Medicine;
import com.dian.diabetes.tool.CallBack;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 用药数据列表，读取创建的用药数据，展现到列表中
 * @author hua
 *
 */
public class MedicineActivity extends BasicActivity implements OnClickListener {

	@ViewInject(id = R.id.new_medicine)
	private ImageButton newMedicine;
	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.medicine_list)
	private ListView listView;
	@ViewInject(id = R.id.toast_container)
	private RelativeLayout toastCon;

	private List<Medicine> datas;
	private MedicineAdapter adapter;
	private MedicineBo mbo;
	private Preference preference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_medicine_layout);
		mbo = new MedicineBo(context);
		preference = Preference.instance(context);
		initActivity();
	}

	private void initActivity() {
		newMedicine.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Medicine medicine = datas.get(position);
				toAddFragment(false, medicine);
			}
		});
		datas = new ArrayList<Medicine>();
		datas.addAll(mbo.listMedicines(ContantsUtil.DEFAULT_TEMP_UID));
		if(datas.size() == 0){
			toastCon.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		}else{
			toastCon.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}
		adapter = new MedicineAdapter(context, datas);
		listView.setAdapter(adapter);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.new_medicine:
			toAddFragment(true, null);
			break;
		case R.id.back_btn:
			finish();
			break;
		}
	}

	private void toAddFragment(boolean isadd, Medicine medicine) {
		String tag = "medicine_add_fragment";
		FragmentManager manager = context.getSupportFragmentManager();
		AddMedicineFragment tempFragment = (AddMedicineFragment) context
				.getSupportFragmentManager().findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = AddMedicineFragment.getInstance(isadd);
			tempFragment.setCallBack(new CallBack() {
				@Override
				public void callBack() {
					datas.clear();
					datas.addAll(mbo
							.listMedicines(ContantsUtil.DEFAULT_TEMP_UID));
					if(datas.size() == 0){
						toastCon.setVisibility(View.VISIBLE);
						listView.setVisibility(View.GONE);
					}else{
						toastCon.setVisibility(View.GONE);
						listView.setVisibility(View.VISIBLE);
					}
					adapter.notifyDataSetChanged();
				}
			});
		}
		if (medicine != null) {
			tempFragment.setMedicine(medicine);
		}
		if (!tempFragment.isAdded()) {
			tempFragment.show(manager, tag);
		}
	}

	public Preference getPreference() {
		return preference;
	}

}
