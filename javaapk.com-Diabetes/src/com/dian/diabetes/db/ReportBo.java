package com.dian.diabetes.db;

import java.util.List;

import android.content.Context;

import com.dian.diabetes.db.dao.DaoSession;
import com.dian.diabetes.db.dao.Report;
import com.dian.diabetes.db.dao.ReportDao;

import de.greenrobot.dao.query.QueryBuilder;

public class ReportBo {
	private ReportDao reportDao;

	public ReportBo(Context context) {
		DaoSession mDaoSession = DbApplication.getDaoSession(context);
		reportDao = mDaoSession.getReportDao();
	}

	public List<Report> listData() {
		QueryBuilder<Report> qb = reportDao.queryBuilder();
		return qb.list();
	}
	
	public Report getReport(String mid){
		QueryBuilder<Report> qb = reportDao.queryBuilder();
		qb.where(ReportDao.Properties.Service_uid.eq(mid));
		List<Report> lists = qb.list();
		if(lists.size() == 0){
			return null;
		}else{
			return lists.get(0);
		}
	}

	public void save(Report plan) {
		reportDao.insertOrReplace(plan);
	}

	public void update(Report report) {
		reportDao.update(report);
	}

	public void saveList(List<Report> lists) {
		reportDao.deleteAll();
		reportDao.insertInTx(lists);
	}
}
