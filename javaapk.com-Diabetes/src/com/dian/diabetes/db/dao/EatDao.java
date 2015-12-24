package com.dian.diabetes.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.dian.diabetes.db.dao.Eat;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table eat.
*/
public class EatDao extends AbstractDao<Eat, Long> {

    public static final String TABLENAME = "eat";

    /**
     * Properties of entity Eat.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Day = new Property(1, String.class, "day", false, "DAY");
        public final static Property DinnerType = new Property(2, int.class, "dinnerType", false, "DINNER_TYPE");
        public final static Property FoodName = new Property(3, String.class, "foodName", false, "FOOD_NAME");
        public final static Property FoodType = new Property(4, String.class, "foodType", false, "FOOD_TYPE");
        public final static Property FoodWeight = new Property(5, float.class, "foodWeight", false, "FOOD_WEIGHT");
        public final static Property Total = new Property(6, float.class, "total", false, "TOTAL");
        public final static Property CookType = new Property(7, String.class, "cookType", false, "COOK_TYPE");
        public final static Property CaloreType = new Property(8, String.class, "caloreType", false, "CALORE_TYPE");
        public final static Property NutriType = new Property(9, String.class, "nutriType", false, "NUTRI_TYPE");
        public final static Property Create_time = new Property(10, long.class, "create_time", false, "CREATE_TIME");
        public final static Property Update_time = new Property(11, long.class, "update_time", false, "UPDATE_TIME");
        public final static Property Surport = new Property(12, float.class, "surport", false, "SURPORT");
        public final static Property Mark = new Property(13, String.class, "mark", false, "MARK");
        public final static Property Service_mid = new Property(14, String.class, "service_mid", false, "SERVICE_MID");
        public final static Property Serverid = new Property(15, String.class, "serverid", false, "SERVERID");
        public final static Property Status = new Property(16, short.class, "status", false, "STATUS");
    };

    private DaoSession daoSession;


    public EatDao(DaoConfig config) {
        super(config);
    }
    
    public EatDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'eat' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'DAY' TEXT NOT NULL ," + // 1: day
                "'DINNER_TYPE' INTEGER NOT NULL ," + // 2: dinnerType
                "'FOOD_NAME' TEXT NOT NULL ," + // 3: foodName
                "'FOOD_TYPE' TEXT NOT NULL ," + // 4: foodType
                "'FOOD_WEIGHT' REAL NOT NULL ," + // 5: foodWeight
                "'TOTAL' REAL NOT NULL ," + // 6: total
                "'COOK_TYPE' TEXT NOT NULL ," + // 7: cookType
                "'CALORE_TYPE' TEXT NOT NULL ," + // 8: caloreType
                "'NUTRI_TYPE' TEXT NOT NULL ," + // 9: nutriType
                "'CREATE_TIME' INTEGER NOT NULL ," + // 10: create_time
                "'UPDATE_TIME' INTEGER NOT NULL ," + // 11: update_time
                "'SURPORT' REAL NOT NULL ," + // 12: surport
                "'MARK' TEXT," + // 13: mark
                "'SERVICE_MID' TEXT NOT NULL ," + // 14: service_mid
                "'SERVERID' TEXT," + // 15: serverid
                "'STATUS' INTEGER NOT NULL );"); // 16: status
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'eat'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Eat entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getDay());
        stmt.bindLong(3, entity.getDinnerType());
        stmt.bindString(4, entity.getFoodName());
        stmt.bindString(5, entity.getFoodType());
        stmt.bindDouble(6, entity.getFoodWeight());
        stmt.bindDouble(7, entity.getTotal());
        stmt.bindString(8, entity.getCookType());
        stmt.bindString(9, entity.getCaloreType());
        stmt.bindString(10, entity.getNutriType());
        stmt.bindLong(11, entity.getCreate_time());
        stmt.bindLong(12, entity.getUpdate_time());
        stmt.bindDouble(13, entity.getSurport());
 
        String mark = entity.getMark();
        if (mark != null) {
            stmt.bindString(14, mark);
        }
        stmt.bindString(15, entity.getService_mid());
 
        String serverid = entity.getServerid();
        if (serverid != null) {
            stmt.bindString(16, serverid);
        }
        stmt.bindLong(17, entity.getStatus());
    }

    @Override
    protected void attachEntity(Eat entity) {
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
    public Eat readEntity(Cursor cursor, int offset) {
        Eat entity = new Eat( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // day
            cursor.getInt(offset + 2), // dinnerType
            cursor.getString(offset + 3), // foodName
            cursor.getString(offset + 4), // foodType
            cursor.getFloat(offset + 5), // foodWeight
            cursor.getFloat(offset + 6), // total
            cursor.getString(offset + 7), // cookType
            cursor.getString(offset + 8), // caloreType
            cursor.getString(offset + 9), // nutriType
            cursor.getLong(offset + 10), // create_time
            cursor.getLong(offset + 11), // update_time
            cursor.getFloat(offset + 12), // surport
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // mark
            cursor.getString(offset + 14), // service_mid
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // serverid
            cursor.getShort(offset + 16) // status
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Eat entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDay(cursor.getString(offset + 1));
        entity.setDinnerType(cursor.getInt(offset + 2));
        entity.setFoodName(cursor.getString(offset + 3));
        entity.setFoodType(cursor.getString(offset + 4));
        entity.setFoodWeight(cursor.getFloat(offset + 5));
        entity.setTotal(cursor.getFloat(offset + 6));
        entity.setCookType(cursor.getString(offset + 7));
        entity.setCaloreType(cursor.getString(offset + 8));
        entity.setNutriType(cursor.getString(offset + 9));
        entity.setCreate_time(cursor.getLong(offset + 10));
        entity.setUpdate_time(cursor.getLong(offset + 11));
        entity.setSurport(cursor.getFloat(offset + 12));
        entity.setMark(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setService_mid(cursor.getString(offset + 14));
        entity.setServerid(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setStatus(cursor.getShort(offset + 16));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Eat entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Eat entity) {
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
