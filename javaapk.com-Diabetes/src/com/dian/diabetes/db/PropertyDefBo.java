package com.dian.diabetes.db;

import java.util.List;
import android.content.Context;

import com.dian.diabetes.db.dao.DaoSession;
import com.dian.diabetes.db.dao.DefSet;
import com.dian.diabetes.db.dao.DefSetDao;
import com.dian.diabetes.db.dao.DefSetDao.Properties;

import de.greenrobot.dao.query.QueryBuilder;

public class PropertyDefBo {

	private DefSetDao dao;

	public PropertyDefBo(Context context) {
		DaoSession mDaoSession = DbApplication.getDaoSession(context);
		dao = mDaoSession.getDefSetDao();
	}

	/**
	 * 根据key获取值
	 * 
	 * @param key
	 * @param userId
	 * @return
	 */
	public DefSet getByKey(String key, String userId) {
		QueryBuilder<DefSet> qb = dao.queryBuilder();
		qb.where(Properties.Key.eq(key));
		try {
			return qb.unique();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取所有配置
	 * 
	 * @param userId
	 * @return
	 */
	public List<DefSet> getList() {
		QueryBuilder<DefSet> qb = dao.queryBuilder();
		return qb.list();
	}

	/**
	 * 更新
	 * 
	 * @param data
	 */
	public void saveUpdate(List<DefSet> data) {
		dao.deleteAll();
		dao.insertInTx(data);
	}

}
