package com.dian.diabetes.activity.sugar;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jiuan.androidBg.Bluetooth.BG5Control;
import jiuan.androidBg.Comm.BGCommManager;
import jiuan.androidBg.Observer.Interface_Observer_BG;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicFragmentDialog;
import com.dian.diabetes.activity.sugar.adapter.SugarCacheAdapter;
import com.dian.diabetes.db.BloodBo;
import com.dian.diabetes.db.CacheBo;
import com.dian.diabetes.db.dao.Diabetes;
import com.dian.diabetes.db.dao.DiabetesCache;
import com.dian.diabetes.tool.CallBack;
import com.dian.diabetes.tool.Config;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.tool.ToastTool;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;
import com.dian.diabetes.utils.StringUtil;
import com.dian.diabetes.widget.anotation.ViewInject;

/**
 * 同步缓存数据
 * 
 * @author longbh
 * 
 */
public class SugarCacheFragment extends BasicFragmentDialog implements
		OnClickListener, Interface_Observer_BG {

	@ViewInject(id = R.id.cache_list)
	private ListView cacheList;
	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.delete)
	private ImageButton deleteBtn;
	@ViewInject(id = R.id.save_btn)
	private Button okBtn;
	@ViewInject(id = R.id.check_all)
	private CheckBox checkAll;
	@ViewInject(id = R.id.top_text)
	private TextView topText;
	@ViewInject(id = R.id.sycn_btn)
	private ImageView sycnData;
	@ViewInject(id = R.id.loading)
	private ProgressBar loading;

	private List<DiabetesCache> data;
	private SugarActivity activity;
	private SugarCacheAdapter adapter;
	private float levelPre[];
	private float levelAfter[];
	private CacheBo bo;
	private int cacheNum = 0;
	private CallBack callBack;
	// 蓝牙
	private BGCommManager connector;
	private BG5Control bg5Control;
	private boolean isNew = false;
	private BluetoothAdapter mAdapter;

	public static SugarCacheFragment Instance() {
		return new SugarCacheFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (SugarActivity) context;
		data = new ArrayList<DiabetesCache>();
		adapter = new SugarCacheAdapter(activity, data);
		bo = new CacheBo(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sugar_cache, container,
				false);
		fieldView(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		backBtn.setOnClickListener(this);
		okBtn.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);
		sycnData.setOnClickListener(this);
		checkAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton button, boolean state) {
				for (DiabetesCache item : data) {
					item.setSelect(state);
				}
				adapter.notifyDataSetChanged();
			}
		});
		
		cacheList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				data.get(position).check();
				adapter.notifyDataSetChanged();
			}
		});

		levelPre = new float[] {
				Config.getFloatPro("levelLow" + ContantsUtil.EAT_PRE),
				Config.getFloatPro("levelMid" + ContantsUtil.EAT_PRE),
				Config.getFloatPro("levelHigh" + ContantsUtil.EAT_PRE) };
		levelAfter = new float[] {
				Config.getFloatPro("levelLow" + ContantsUtil.EAT_AFTER),
				Config.getFloatPro("levelMid" + ContantsUtil.EAT_AFTER),
				Config.getFloatPro("levelHigh" + ContantsUtil.EAT_AFTER) };
		adapter.setLevelAfter(levelAfter);
		adapter.setLevelPre(levelPre);
		cacheList.setAdapter(adapter);

		initData();
		topText.setText("点击按钮开始同步血糖数据");
	}

	private void initData() {
		data.clear();
		data.addAll(bo.listData());
		cacheNum = activity.getPreference().getInt(Preference.CACHE_NUM, 0);
		adapter.setCacheNum(cacheNum);
		adapter.notifyDataSetChanged();
		
		loading.setVisibility(View.GONE);
		topText.setText("新同步数据" + cacheNum + "条");
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			dismiss();
			break;
		case R.id.delete:
			deleteData();
			break;
		case R.id.ok_btn:
			okBtn();
			break;
		case R.id.sycn_btn:
			sycnData("");
			break;
		case R.id.save_btn:
			okBtn();
			break;
		}
	}

	private void deleteData() {
		List<Long> ids = new ArrayList<Long>();
		for (DiabetesCache itemData : data) {
			if (itemData.isSelect()) {
				ids.add(itemData.getId());
			}
		}
		if (ids.size() != 0) {
			bo.deleteKeys(ids);
		}
		activity.getPreference().putInt(Preference.CACHE_NUM, bo.count());
	}

	private void sycnData(String device) {
		bg5Control = BGCommManager.getBG5Control(device);
		if (bg5Control != null) {
			bg5Control.bg5subject.attach(this);
			bg5Control.connect(activity, ContantsUtil.bluthUserId, ContantsUtil.clientID, ContantsUtil.clientSecret);
		} else {
			openEquipList();
		}
	}

	private void openEquipList() {
		String tag = "equip_list_dialog";
		FragmentManager manager = context.getSupportFragmentManager();
		EquipListDialog tempFragment = (EquipListDialog) context
				.getSupportFragmentManager().findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = EquipListDialog.getInstance();
			tempFragment.setCallBack(new EquipListDialog.CallBack() {
				@Override
				public void callBack(String device) {
					sycnData(device);
					topText.setText("正在读取缓存数据，请勿关闭当前界面");
					loading.setVisibility(View.VISIBLE);
				}
			});
		}
		if(connector == null){
			connector = new BGCommManager(activity);
			isNew = true;
		}
		tempFragment.setConnect(connector);
		tempFragment.show(manager, tag);
	}

	/**
	 * 保存数据
	 */
	private void okBtn() {
		List<Diabetes> diaList = new ArrayList<Diabetes>();
		BloodBo bloodBo = new BloodBo(activity);
		boolean hashUpdate = false;
		int size = 0;
		for(DiabetesCache item : data){
			if(item.isSelect()){
				Diabetes diabetes = new Diabetes();
				diabetes.setCreate_time(item.getCreate_time());
				diabetes.setDay(DateUtil.parseToString(item.getCreate_time(), DateUtil.yyyyMMdd));
				diabetes.setStatus((short) 0);		
				diabetes.setValue(item.getValue());
				diabetes.setUpdate_time(System.currentTimeMillis());
				int[] type = Config.getEatStateArry(item.getCreate_time());
				diabetes.setSub_type(type[1]);
				if (type[0] == ContantsUtil.SLEEP_PRE) {
					diabetes.setSub_type(ContantsUtil.EAT_PRE);
				}
				diabetes.setService_mid(ContantsUtil.DEFAULT_TEMP_UID);
				diabetes.setType((short)type[0]);
				diabetes.setLevel(Config.getBloodState(item.getValue(), diabetes.getSub_type()));
				diaList.add(diabetes);
				bloodBo.saveUpdateDiabetes(diabetes);
				bo.delete(item.getId());
				hashUpdate = true;
				size ++;
			}
		}
		if(hashUpdate){
			ContantsUtil.ENTRY_UPDATE = false;
			ContantsUtil.EFFECT_UPDATE = false;
			ContantsUtil.TOTAL_UPDATE = false;
			activity.getPreference().putBoolean(Preference.HAS_UPDATE, true);
			activity.getPreference().putInt(Preference.CACHE_NUM, bo.count());
		}
		if(callBack != null){
			callBack.callBack();
		}
		Toast.makeText(activity, "成功同步数据"+size+"条", Toast.LENGTH_SHORT).show();
		dismiss();
	}

	@Override
	public void msgBGError(int state) {
		Log.d("data", "msgBGError");
		Message msg = new Message();
		msg.arg1 = state;
		msg.what = 3;
		handler.sendMessage(msg);
	}

	@Override
	public void msgBGGetBlood() {
	}

	@Override
	public void msgBGPowerOff() {
	}

	@Override
	public void msgBGResult(int result) {
	}

	@Override
	public void msgBGStripIn() {
	}

	@Override
	public void msgBGStripOut() {
	}

	@Override
	public void msgUserStatus(int status) {
		Log.d("data", "msgUserStatus");
		Message msg = new Message();
		msg.arg1 = status;
		msg.what = 1;
		handler.sendMessage(msg);
		if (status > 0 && status < 5) {
			thread.start();
		}
	}

	private Thread thread = new Thread() {
		public void run() {
			if (bg5Control.setParams(1, 1)) {
				// 读取离线数据
				String offline = bg5Control.readOfflineData();
				Log.e("data", offline);
				// 读完后清空
				if (bg5Control.deleteOfflineData()) {
					Message message3 = new Message();
					message3.what = 6;
					message3.obj = offline;
					handler.sendMessage(message3);
				} else {
					Message message3 = new Message();
					message3.what = 7;
					handler.sendMessage(message3);
				}
			}
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				String tostStr = ToastTool.toastBluth(msg.arg1);
				Toast.makeText(context, tostStr, Toast.LENGTH_SHORT).show();
				break;
			case 3:
				String str = ToastTool.toastErrorBluth(msg.arg1);
				Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
				break;
			case 6:
				saveCacheData(msg.obj + "");
				initData();
				break;
			case 7:
				Toast.makeText(context, "读取缓存数据失败", Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

	private void saveCacheData(String offline) {
		List<DiabetesCache> cacheData = new ArrayList<DiabetesCache>();
		try {
			JSONArray jsonData = new JSONObject(offline).getJSONArray("bgdata");
			for (int i = 0; i < jsonData.length(); i++) {
				JSONObject item = jsonData.getJSONObject(i);
				DiabetesCache cacheItem = new DiabetesCache();
				cacheItem.setValue(StringUtil.toFloat(item.get("value")));
				cacheItem.setCreate_time(DateUtil.formatDateMills(item.getString("date"), DateUtil.yyyyMMddHHmmss));
				cacheItem.setNew(true);
				cacheData.add(cacheItem);
			}
			cacheNum = cacheData.size();
			bo.saveList(cacheData);
			activity.getPreference().putInt(Preference.CACHE_NUM, bo.count());
			Toast.makeText(activity, "数据同步成功", Toast.LENGTH_SHORT).show();
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(activity, "缓存数据出错", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	public void setConnect(BGCommManager connector){
		this.connector = connector;
	}
	
	public void setCallBack(CallBack callback){
		this.callBack = callback;
	}

	public void onDestroy() {
		super.onDestroy();
		if (bg5Control != null) {
			bg5Control.stop();
		}
		if(isNew){
			connector.stop();
			mAdapter = BluetoothAdapter.getDefaultAdapter();
			if(mAdapter.isEnabled()){
				mAdapter.disable();
			}
		}
	}
}
