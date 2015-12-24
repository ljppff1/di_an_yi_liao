package com.dian.diabetes.activity.sugar;

import java.util.List;

import com.dian.diabetes.activity.BaseFragment;
import com.dian.diabetes.db.dao.Diabetes;

public abstract class EntryBaseFragment extends BaseFragment {

	public abstract void loadEntryData(List<Diabetes> data);
}
