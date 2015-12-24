package com.dian.diabetes.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.dian.diabetes.db.dao.User;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table user.
*/
public class UserDao extends AbstractDao<User, Long> {

    public static final String TABLENAME = "user";

    /**
     * Properties of entity User.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Phone = new Property(1, String.class, "phone", false, "PHONE");
        public final static Property Nick_name = new Property(2, String.class, "nick_name", false, "NICK_NAME");
        public final static Property Service_mid = new Property(3, String.class, "service_mid", false, "SERVICE_MID");
        public final static Property Service_uid = new Property(4, String.class, "service_uid", false, "SERVICE_UID");
        public final static Property Support = new Property(5, float.class, "support", false, "SUPPORT");
        public final static Property Update_time = new Property(6, long.class, "update_time", false, "UPDATE_TIME");
        public final static Property Diabetes_time = new Property(7, long.class, "diabetes_time", false, "DIABETES_TIME");
        public final static Property Eat_time = new Property(8, long.class, "eat_time", false, "EAT_TIME");
        public final static Property Sport_time = new Property(9, long.class, "sport_time", false, "SPORT_TIME");
        public final static Property Medicine_time = new Property(10, long.class, "medicine_time", false, "MEDICINE_TIME");
        public final static Property Surport_time = new Property(11, long.class, "surport_time", false, "SURPORT_TIME");
        public final static Property Time_update = new Property(12, long.class, "time_update", false, "TIME_UPDATE");
        public final static Property Plan_update = new Property(13, long.class, "plan_update", false, "PLAN_UPDATE");
        public final static Property Indicate_update = new Property(14, long.class, "indicate_update", false, "INDICATE_UPDATE");
    };

    private DaoSession daoSession;


    public UserDao(DaoConfig config) {
        super(config);
    }
    
    public UserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'user' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'PHONE' TEXT NOT NULL ," + // 1: phone
                "'NICK_NAME' TEXT NOT NULL ," + // 2: nick_name
                "'SERVICE_MID' TEXT NOT NULL ," + // 3: service_mid
                "'SERVICE_UID' TEXT NOT NULL ," + // 4: service_uid
                "'SUPPORT' REAL NOT NULL ," + // 5: support
                "'UPDATE_TIME' INTEGER NOT NULL ," + // 6: update_time
                "'DIABETES_TIME' INTEGER NOT NULL ," + // 7: diabetes_time
                "'EAT_TIME' INTEGER NOT NULL ," + // 8: eat_time
                "'SPORT_TIME' INTEGER NOT NULL ," + // 9: sport_time
                "'MEDICINE_TIME' INTEGER NOT NULL ," + // 10: medicine_time
                "'SURPORT_TIME' INTEGER NOT NULL ," + // 11: surport_time
                "'TIME_UPDATE' INTEGER NOT NULL ," + // 12: time_update
                "'PLAN_UPDATE' INTEGER NOT NULL ," + // 13: plan_update
                "'INDICATE_UPDATE' INTEGER NOT NULL );"); // 14: indicate_update
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'user'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, User entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getPhone());
        stmt.bindString(3, entity.getNick_name());
        stmt.bindString(4, entity.getService_mid());
        stmt.bindString(5, entity.getService_uid());
        stmt.bindDouble(6, entity.getSupport());
        stmt.bindLong(7, entity.getUpdate_time());
        stmt.bindLong(8, entity.getDiabetes_time());
        stmt.bindLong(9, entity.getEat_time());
        stmt.bindLong(10, entity.getSport_time());
        stmt.bindLong(11, entity.getMedicine_time());
        stmt.bindLong(12, entity.getSurport_time());
        stmt.bindLong(13, entity.getTime_update());
        stmt.bindLong(14, entity.getPlan_update());
        stmt.bindLong(15, entity.getIndicate_update());
    }

    @Override
    protected void attachEntity(User entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public User readEntity(Cursor cursor, int offset) {
        User entity = new User( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // phone
            cursor.getString(offset + 2), // nick_name
            cursor.getString(offset + 3), // service_mid
            cursor.getString(offset + 4), // service_uid
            cursor.getFloat(offset + 5), // support
            cursor.getLong(offset + 6), // update_time
            cursor.getLong(offset + 7), // diabetes_time
            cursor.getLong(offset + 8), // eat_time
            cursor.getLong(offset + 9), // sport_time
            cursor.getLong(offset + 10), // medicine_time
            cursor.getLong(offset + 11), // surport_time
            cursor.getLong(offset + 12), // time_update
            cursor.getLong(offset + 13), // plan_update
            cursor.getLong(offset + 14) // indicate_update
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, User entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPhone(cursor.getString(offset + 1));
        entity.setNick_name(cursor.getString(offset + 2));
        entity.setService_mid(cursor.getString(offset + 3));
        entity.setService_uid(cursor.getString(offset + 4));
        entity.setSupport(cursor.getFloat(offset + 5));
        entity.setUpdate_time(cursor.getLong(offset + 6));
        entity.setDiabetes_time(cursor.getLong(offset + 7));
        entity.setEat_time(cursor.getLong(offset + 8));
        entity.setSport_time(cursor.getLong(offset + 9));
        entity.setMedicine_time(cursor.getLong(offset + 10));
        entity.setSurport_time(cursor.getLong(offset + 11));
        entity.setTime_update(cursor.getLong(offset + 12));
        entity.setPlan_update(cursor.getLong(offset + 13));
        entity.setIndicate_update(cursor.getLong(offset + 14));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(User entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(User entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
