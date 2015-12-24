package com.dian.diabetes.db.dao;

import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;
import com.dian.diabetes.db.dao.DaoSession;
import com.dian.diabetes.utils.StringUtil;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table alarm.
 */
public class Alarm implements Parcelable {

	private Long id;
	/** Not-null value. */
	private String service_mid;
	private short type;
	private Short sub_type;
	private String title;
	private short hour;
	private short minite;
	private long alarm_time;
	private int day_of_week;
	private int un_week;
	private boolean enable;
	private String message;
	private int repeat;
	private long oId;
	private long createTime;
	private String uid;

	/** Used to resolve relations */
	private transient DaoSession daoSession;

	/** Used for active entity operations. */
	private transient AlarmDao myDao;

	// KEEP FIELDS - put your custom fields here
	// KEEP FIELDS END

	public static final Parcelable.Creator<Alarm> CREATOR = new Parcelable.Creator<Alarm>() {
		public Alarm createFromParcel(Parcel p) {
			return new Alarm(p);
		}

		public Alarm[] newArray(int size) {
			return new Alarm[size];
		}
	};

	public Alarm() {
	}

	public Alarm(Long id) {
		this.id = id;
	}

	public Alarm(Parcel p) {
		id = p.readLong();
		service_mid = p.readString();
		title = p.readString();
		alarm_time = p.readLong();
		enable = StringUtil.toBoolean(p.readString());
		type = (short) p.readInt();
		sub_type = (short) p.readInt();
		message = p.readString();
	}

	public Alarm(Long id, String service_mid, String title, short type,
			Short sub_type, short hour, short minite, long alarm_time,
			int day_of_week, int un_week, boolean enable, String message,
			long oId,int repeat,long createTime,String uid) {
		this.id = id;
		this.service_mid = service_mid;
		this.title = title;
		this.type = type;
		this.sub_type = sub_type;
		this.hour = hour;
		this.minite = minite;
		this.alarm_time = alarm_time;
		this.day_of_week = day_of_week;
		this.un_week = un_week;
		this.enable = enable;
		this.message = message;
		this.oId = oId;
		this.repeat = repeat;
		this.createTime = createTime;
		this.uid = uid;
	}

	/** called by internal mechanisms, do not call yourself. */
	public void __setDaoSession(DaoSession daoSession) {
		this.daoSession = daoSession;
		myDao = daoSession != null ? daoSession.getAlarmDao() : null;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public Short getSub_type() {
		return sub_type;
	}

	public void setSub_type(Short sub_type) {
		this.sub_type = sub_type;
	}

	public short getHour() {
		return hour;
	}

	public void setHour(short hour) {
		this.hour = hour;
	}

	public short getMinite() {
		return minite;
	}

	public void setMinite(short minite) {
		this.minite = minite;
	}

	public long getAlarm_time() {
		return alarm_time;
	}

	public long getoId() {
		return oId;
	}

	public void setoId(long oId) {
		this.oId = oId;
	}

	public long caculateAlarm_time() {
		Calendar c = Calendar.getInstance();
		if (repeat == -1) {
			// 自定义返回时间，解决某些需要循环的问题
			if (day_of_week == 0) {
				return 0;
			}
			c.setTimeInMillis(System.currentTimeMillis());

			int nowHour = c.get(Calendar.HOUR_OF_DAY);
			int nowMinute = c.get(Calendar.MINUTE);

			// if alarm is behind current time, advance one day
			if (hour < nowHour || hour == nowHour && minite <= nowMinute) {
				c.add(Calendar.DATE, 1);
			}
			c.set(Calendar.HOUR_OF_DAY, hour);
			c.set(Calendar.MINUTE, minite);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			int today = (c.get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1;
			int day = 0;
			int dayCount = 0;
			for (; dayCount < 7; dayCount++) {
				day = (today + dayCount) % 7;
				if (day == 0) {
					day = 7;
				}
				if (isSet(day)) {
					break;
				}
			}
			c.add(Calendar.DAY_OF_WEEK, dayCount);
		} else {
			c.setTimeInMillis(createTime);
			c.set(Calendar.HOUR_OF_DAY, hour);
			c.set(Calendar.MINUTE, minite);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			c.add(Calendar.DATE, repeat);
			if(c.getTimeInMillis() > System.currentTimeMillis()){
				c.setTimeInMillis(System.currentTimeMillis());
				int nowHour = c.get(Calendar.HOUR_OF_DAY);
				int nowMinute = c.get(Calendar.MINUTE);

				// if alarm is behind current time, advance one day
				if (hour < nowHour || hour == nowHour && minite <= nowMinute) {
					c.add(Calendar.DATE, 1);
				}
				c.set(Calendar.HOUR_OF_DAY, hour);
				c.set(Calendar.MINUTE, minite);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				return c.getTimeInMillis();
			}else{
				return 0;
			}
		}
		return c.getTimeInMillis();
	}

	public void setAlarm_time(long alarm_time) {
		this.alarm_time = alarm_time;
	}

	public int getDay_of_week() {
		return day_of_week;
	}

	public void setDay_of_week(int day_of_week) {
		this.day_of_week = day_of_week;
	}

	public boolean getEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getUn_week() {
		return un_week;
	}

	public void setUnweek(int unweek) {
		this.un_week = unweek;
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int flag) {
		p.writeLong(id);
		p.writeString(service_mid);
		p.writeString(title);
		p.writeLong(alarm_time);
		p.writeString(enable + "");
		p.writeInt(type);
		p.writeInt(sub_type);
		p.writeString(message);
	}

	public void set(int day, boolean set) {
		if (set) {
			day_of_week |= (1 << day);
		} else {
			day_of_week &= ~(1 << day);
		}
	}

	public boolean isSet(int day) {
		return ((day_of_week & (1 << day)) > 0);
	}

	public void setUn(int day, boolean set) {
		if (set) {
			un_week |= (1 << day);
		} else {
			un_week &= ~(1 << day);
		}
	}

	public boolean isSetUn(int day) {
		return ((un_week & (1 << day)) > 0);
	}
	
	public boolean isSetAll(int day){
		boolean state1 = ((un_week & (1 << day)) > 0);
		boolean state2 = ((day_of_week & (1 << day)) > 0);
		return state1 || state2;
	}

	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

}
