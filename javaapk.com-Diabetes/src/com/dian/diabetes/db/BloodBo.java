package com.dian.diabetes.db;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import com.dian.diabetes.db.dao.DaoSession;
import com.dian.diabetes.db.dao.Diabetes;
import com.dian.diabetes.db.dao.DiabetesDao;
import com.dian.diabetes.db.dao.DiabetesDao.Properties;
import com.dian.diabetes.utils.CheckUtil;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.DateUtil;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * 血糖bo文件
 * @author hua
 *
 */
public class BloodBo {

	private DiabetesDao diabetesDao;

	public BloodBo(Context context) {
		DaoSession mDaoSession = DbApplication.getDaoSession(context);
		diabetesDao = mDaoSession.getDiabetesDao();
	}

	public Diabetes isSavedByServer(Diabetes diabetes) {
		QueryBuilder<Diabetes> qb = diabetesDao.queryBuilder();
		qb.where(Properties.Type.eq(diabetes.getType()),
				Properties.Sub_type.eq(diabetes.getSub_type()),
				Properties.Day.eq(diabetes.getDay()),
				Properties.Service_mid.eq(diabetes.getService_mid()));
		if (qb.list().size() == 0) {
			return null;
		}else{
			return qb.list().get(0);
		}
	}

	public Diabetes getById(long id) {
		return diabetesDao.load(id);
	}

	/**
	 * 保存或修改
	 * 
	 * @param diabetes
	 * @return
	 */
	public long saveUpdateDiabetes(Diabetes diabetes) {
		Diabetes ret = isSavedByServer(diabetes);
		if (ret == null) {
			if (diabetes.getId() != null) {				
				//deleteLocal(diabetes);
				diabetesDao.update(diabetes);
				return diabetes.getId();
			}else{
				return diabetesDao.insert(diabetes);
			}
		} else {
			ret.setValue(diabetes.getValue());
			ret.setMark(diabetes.getMark());
			ret.setFeel(diabetes.getFeel());
			ret.setStatus((short)ContantsUtil.NO_SERVER);
			ret.setLevel(diabetes.getLevel());
			ret.setUpdate_time(System.currentTimeMillis());
			if (diabetes.getId() != null
					&& !diabetes.getId().equals(ret.getId())) {
				deleteLocal(diabetes);
			}
			diabetesDao.update(ret);
			return ret.getId();
		}
	}
	
	public void saveByServerId(Diabetes diabetes){
		QueryBuilder<Diabetes> qb = diabetesDao.queryBuilder();
		qb.where(Properties.Serverid.eq(diabetes.getServerid()));
		List<Diabetes> list = qb.list();
		if(list.size() > 1){
			diabetes.setId(list.get(0).getId());
			diabetesDao.update(diabetes);
		}else{
			diabetesDao.insert(diabetes);
		}
	}

	/**
	 * 获取某一天的所有值
	 * 
	 * @param mid
	 * @param day
	 * @return
	 */
	public List<Diabetes> listDayDiabetes(String mid, String day) {
		long pre = DateUtil.parseToDateLong(day, DateUtil.yyyymmdd);
		long after = pre + 24 * 60 * 60 * 1000;
		QueryBuilder<Diabetes> qb = diabetesDao.queryBuilder();
		qb.where(Properties.Status.notEq(ContantsUtil.DELETE),
				Properties.Service_mid.eq(mid), Properties.Create_time.gt(pre),
				Properties.Create_time.lt(after));
		return qb.list();
	}

	/**
	 * 获取某一天的所有值
	 * 
	 * @param mid
	 * @param day
	 * @return
	 */
	public List<Diabetes> listDayDiabetesSort(String mid, String day) {
		long pre = DateUtil.parseToDateLong(day, DateUtil.yyyymmdd);
		long after = pre + 24 * 60 * 60 * 1000;
		QueryBuilder<Diabetes> qb = diabetesDao.queryBuilder();
		qb.where(Properties.Status.notEq(ContantsUtil.DELETE),
				Properties.Service_mid.eq(mid), Properties.Create_time.gt(pre),
				Properties.Create_time.lt(after)).orderAsc(Properties.Type,
				Properties.Sub_type);
		return qb.list();
	}

	public Map<String, Diabetes> listDayDiabetesSort(String mid, long time) {
		QueryBuilder<Diabetes> qb = diabetesDao.queryBuilder();
		qb.where(Properties.Status.notEq(ContantsUtil.DELETE),
				Properties.Service_mid.eq(mid), Properties.Create_time.gt(time))
				.orderAsc(Properties.Type, Properties.Sub_type);

		Map<String, Diabetes> mapData = new HashMap<String, Diabetes>();
		for (Diabetes diabetes : qb.list()) {
			mapData.put(DateUtil.parseToDate(diabetes.getCreate_time())
					+ diabetes.getType() + diabetes.getSub_type(), diabetes);
		}
		return mapData;
	}

	/**
	 * 加载最近多长时间的数据
	 * 
	 * @param mid
	 * @param lastTime
	 * @return
	 */
	public LinkedHashMap<String, Diabetes> loadTotal(String mid, long lastTime) {
		QueryBuilder<Diabetes> qb = diabetesDao.queryBuilder();
		qb.where(Properties.Status.notEq(ContantsUtil.DELETE),
				Properties.Service_mid.eq(mid),
				Properties.Create_time.gt(lastTime));
		LinkedHashMap<String, Diabetes> mapData = new LinkedHashMap<String, Diabetes>();
		for (Diabetes diabetes : qb.list()) {
			mapData.put(DateUtil.parseToDate(diabetes.getCreate_time())
					+ diabetes.getType() + diabetes.getSub_type(), diabetes);
		}
		return mapData;
	}
	
	/**
	 * 读取未同步服务器数据
	 * @param userId
	 * @return
	 */
	public List<Diabetes> listUserUpdateData(String userId){
		QueryBuilder<Diabetes> qb = diabetesDao.queryBuilder();
		qb.where(Properties.Service_mid.eq(userId),
				Properties.Status.notEq(ContantsUtil.SERVER));
		return qb.list();
	}

	/**
	 * 加载某类型，早餐前或者后
	 * 
	 * @param mid
	 * @param lastTime
	 * @param type
	 * @return
	 */
	public List<Diabetes> loadLastType(String mid, long lastTime, int type) {
		QueryBuilder<Diabetes> qb = diabetesDao.queryBuilder();
		qb.where(Properties.Status.notEq(ContantsUtil.DELETE),
				Properties.Service_mid.eq(mid), Properties.Sub_type.eq(type),
				Properties.Create_time.gt(lastTime));
		return qb.list();
	}
	
	public List<Diabetes> loadSubType(String mid, long lastTime, long afterTime,int type) {
		QueryBuilder<Diabetes> qb = diabetesDao.queryBuilder();
		qb.where(Properties.Status.notEq(ContantsUtil.DELETE),
				Properties.Service_mid.eq(mid), Properties.Sub_type.eq(type),
				Properties.Create_time.gt(lastTime),
				Properties.Create_time.lt(afterTime));
		return qb.list();
	}

	/**
	 * 查询需要同步到服务端数据
	 * 
	 * @return
	 */
	public List<Diabetes> loadSycnData() {
		QueryBuilder<Diabetes> qb = diabetesDao.queryBuilder();
		qb.where(Properties.Status.notEq(ContantsUtil.SERVER));
		return qb.list();
	}
	
	public void update(Diabetes diabetes){
		diabetesDao.update(diabetes);
	}
	
	/**
	 * 本地删除
	 * @param diabetes
	 */
	public void deleteLocal(Diabetes diabetes){
		if(CheckUtil.isNull(diabetes.getServerid())){
			diabetesDao.delete(diabetes);
		}else{
			diabetes.setStatus((short) ContantsUtil.DELETE);
			diabetesDao.update(diabetes);
		}
	}

	/**
	 * 根据服务端id删除本地数据
	 * 
	 * @param serverId
	 */
	public void deleteData(String serverId) {
		QueryBuilder<Diabetes> qb = diabetesDao.queryBuilder();
		qb.where(Properties.Serverid.eq(serverId));
		diabetesDao.deleteInTx(qb.list());
	}

	public void deleteData(long id) {
		Diabetes diabetes = new Diabetes(id);
		diabetesDao.delete(diabetes);
	}
	
	public void deleteTransaction(List<Long> ids){
		diabetesDao.deleteByKeyInTx(ids);
	}

	/**
	 * 查询数据是否超标等
	 * 
	 * @param data
	 * @param year
	 * @param month
	 * @param mid
	 */
	public void queryLevelData(Map<String, Integer> data, int year, int month,
			String mid) {
		data.clear();
		Calendar preCal = Calendar.getInstance();
		preCal.set(Calendar.YEAR, year);
		preCal.set(Calendar.MONTH, month);
		preCal.set(Calendar.DAY_OF_MONTH, 1);
		preCal.set(Calendar.HOUR_OF_DAY, 0);
		preCal.set(Calendar.MINUTE, 0);
		preCal.set(Calendar.SECOND, 0);
		preCal.set(Calendar.MILLISECOND, 0);
		long afterTime = preCal.getTimeInMillis();
		preCal.add(Calendar.MONTH, -1);
		long preTime = preCal.getTimeInMillis();
		Cursor cursor = diabetesDao
				.getDatabase()
				.query(DiabetesDao.TABLENAME,
						new String[] { Properties.Day.columnName,
								Properties.Level.columnName },
						"CREATE_TIME>? and CREATE_TIME<? and SERVICE_MID=? and STATUS<>?",
						new String[] { preTime + "", afterTime + "", mid,
								ContantsUtil.DELETE + "" }, null, null,
						"CREATE_TIME asc");
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			String day = cursor.getString(0);
			int level = cursor.getInt(1);
			while (!cursor.isAfterLast()) {
				String tempday = cursor.getString(0);
				int templevel = cursor.getInt(1);
				level = Math.max(level, templevel);
				if (!day.equals(tempday)) {					
					data.put(day, level);
					day = tempday;
					level = 0;
				}
				cursor.moveToNext();
			}
			data.put(day, level);
			cursor.close();
		}
	}

}
