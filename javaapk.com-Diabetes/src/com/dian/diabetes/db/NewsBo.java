package com.dian.diabetes.db;

import java.util.List;

import android.content.Context;

import com.dian.diabetes.db.dao.DaoSession;
import com.dian.diabetes.db.dao.News;
import com.dian.diabetes.db.dao.NewsDao;
import de.greenrobot.dao.query.QueryBuilder;

public class NewsBo {

	private NewsDao dao;

	public NewsBo(Context context) {
		DaoSession mDaoSession = DbApplication.getDaoSession(context);
		dao = mDaoSession.getNewsDao();
	}

	public long saveNews(News medicine) {
		return dao.insertOrReplace(medicine);
	}
	
	public void saveArray(List<News> list) {
		dao.insertInTx(list);
	}

	public List<News> listNews() {
		QueryBuilder<News> qb = dao.queryBuilder();
		return qb.list();
	}

	public News getById(long id) {
		return dao.load(id);
	}

	public void delete(long id) {
		dao.deleteByKey(id);
	}

	public void clearData(){
		dao.deleteAll();
	}
}
