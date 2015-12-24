package com.dian.diabetes.db.dao;

import com.dian.diabetes.db.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table medicine.
 */
public class Medicine {

	private Long id;
	/** Not-null value. */
	private String type;
	/** Not-null value. */
	private String name;
	private int numType;
	private int stage;
	/** Not-null value. */
	private String weight;
	private int eatDay;
	/** Not-null value. */
	private String unit;
	private long createTime;
	private long updateTime;
	private boolean isOut;
	private boolean istoast;
	private String rmdBreakfast;
	private String rmdLunch;
	private String rmdsupper;
	private String rmdSleep;
	/** Not-null value. */
	private String service_mid;
	private String serverid;
	private short status;

	/** Used to resolve relations */
	private transient DaoSession daoSession;

	/** Used for active entity operations. */
	private transient MedicineDao myDao;

	// KEEP FIELDS - put your custom fields here
	// KEEP FIELDS END

	public Medicine() {
	}

	public Medicine(Long id) {
		this.id = id;
	}

	public Medicine(Long id, String type, String name, int numType, int stage,
			String weight, int eatDay, String unit, long createTime,
			long updateTime, boolean isOut, boolean istoast,
			String rmdBreakfast, String rmdLunch, String rmdsupper,
			String service_mid, String serverid, short status,String rmdSleep) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.numType = numType;
		this.stage = stage;
		this.weight = weight;
		this.eatDay = eatDay;
		this.unit = unit;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.isOut = isOut;
		this.istoast = istoast;
		this.rmdBreakfast = rmdBreakfast;
		this.rmdLunch = rmdLunch;
		this.rmdsupper = rmdsupper;
		this.service_mid = service_mid;
		this.serverid = serverid;
		this.status = status;
		this.rmdSleep = rmdSleep;
	}

	/** called by internal mechanisms, do not call yourself. */
	public void __setDaoSession(DaoSession daoSession) {
		this.daoSession = daoSession;
		myDao = daoSession != null ? daoSession.getMedicineDao() : null;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/** Not-null value. */
	public String getType() {
		return type;
	}

	/**
	 * Not-null value; ensure this value is available before it is saved to the
	 * database.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/** Not-null value. */
	public String getName() {
		return name;
	}

	/**
	 * Not-null value; ensure this value is available before it is saved to the
	 * database.
	 */
	public void setName(String name) {
		this.name = name;
	}

	public int getNumType() {
		return numType;
	}

	public void setNumType(int numType) {
		this.numType = numType;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	/** Not-null value. */
	public String getWeight() {
		return weight;
	}

	/**
	 * Not-null value; ensure this value is available before it is saved to the
	 * database.
	 */
	public void setWeight(String weight) {
		this.weight = weight;
	}

	public int getEatDay() {
		return eatDay;
	}

	public void setEatDay(int eatDay) {
		this.eatDay = eatDay;
	}

	/** Not-null value. */
	public String getUnit() {
		return unit;
	}

	/**
	 * Not-null value; ensure this value is available before it is saved to the
	 * database.
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public boolean getIsOut() {
		return isOut;
	}

	public void setIsOut(boolean isOut) {
		this.isOut = isOut;
	}

	public boolean getIstoast() {
		return istoast;
	}

	public void setIstoast(boolean istoast) {
		this.istoast = istoast;
	}

	public String getRmdBreakfast() {
		return rmdBreakfast;
	}

	public void setRmdBreakfast(String rmdBreakfast) {
		this.rmdBreakfast = rmdBreakfast;
	}

	public String getRmdLunch() {
		return rmdLunch;
	}

	public void setRmdLunch(String rmdLunch) {
		this.rmdLunch = rmdLunch;
	}

	public String getRmdsupper() {
		return rmdsupper;
	}

	public void setRmdsupper(String rmdsupper) {
		this.rmdsupper = rmdsupper;
	}

	/** Not-null value. */
	public String getService_mid() {
		return service_mid;
	}

	/**
	 * Not-null value; ensure this value is available before it is saved to the
	 * database.
	 */
	public void setService_mid(String service_mid) {
		this.service_mid = service_mid;
	}

	public String getServerid() {
		return serverid;
	}

	public void setServerid(String serverid) {
		this.serverid = serverid;
	}

	public short getStatus() {
		return status;
	}

	public void setStatus(short status) {
		this.status = status;
	}

	public String getRmdSleep() {
		return rmdSleep;
	}

	public void setRmdSleep(String rmdSleep) {
		this.rmdSleep = rmdSleep;
	}

	/**
	 * Convenient call for {@link AbstractDao#delete(Object)}. Entity must
	 * attached to an entity context.
	 */
	public void delete() {
		if (myDao == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		myDao.delete(this);
	}

	/**
	 * Convenient call for {@link AbstractDao#update(Object)}. Entity must
	 * attached to an entity context.
	 */
	public void update() {
		if (myDao == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		myDao.update(this);
	}

	/**
	 * Convenient call for {@link AbstractDao#refresh(Object)}. Entity must
	 * attached to an entity context.
	 */
	public void refresh() {
		if (myDao == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		myDao.refresh(this);
	}

	// KEEP METHODS - put your custom methods here
	// KEEP METHODS END

}
