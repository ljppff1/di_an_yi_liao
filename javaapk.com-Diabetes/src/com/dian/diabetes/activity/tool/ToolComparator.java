package com.dian.diabetes.activity.tool;

import java.util.Comparator;

import com.dian.diabetes.db.dao.Normal;

public class ToolComparator implements Comparator<Normal> {

	@Override
	public int compare(Normal o1, Normal o2) {
		if (o1.getNum() > o2.getNum()) {
			return -1;
		} else if (o1.getNum() == o2.getNum()) {
			int flag = o1.getId().compareTo(o2.getId());
			return flag;
		} else {
			return 1;
		}
	}

}
