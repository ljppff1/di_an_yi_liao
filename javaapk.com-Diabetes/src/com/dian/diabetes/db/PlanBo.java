package com.dian.diabetes.db;

import java.util.List;

import android.content.Context;

import com.dian.diabetes.db.dao.DaoSession;
import com.dian.diabetes.db.dao.Plan;
import com.dian.diabetes.db.dao.PlanDao;

import de.greenrobot.dao.query.QueryBuilder;

public class PlanBo {

	private PlanDao planDao;

	public PlanBo(Context context) {
		DaoSession mDaoSession = DbApplication.getDaoSession(context);
		planDao = mDaoSession.getPlanDao();
	}

	public List<Plan> listData() {
		QueryBuilder<Plan> qb = planDao.queryBuilder();
		qb.orderAsc(PlanDao.Properties.Idx);
		return qb.list();
	}

	public void save(Plan plan) {
		planDao.insert(plan);
	}

	public void saveList(List<Plan> lists) {
		planDao.deleteAll();
		planDao.insertInTx(lists);
	}

}
