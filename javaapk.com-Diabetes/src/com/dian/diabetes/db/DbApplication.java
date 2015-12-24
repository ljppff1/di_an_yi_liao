package com.dian.diabetes.db;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dian.diabetes.db.dao.DaoMaster;
import com.dian.diabetes.db.dao.DaoMaster.OpenHelper;
import com.dian.diabetes.db.dao.DaoSession;

public class DbApplication extends Application {
	private static DbApplication mInstance;
	private static DaoMaster daoMaster;
	private static DaoSession daoSession;
	private static SQLiteDatabase sqLiteDatabase; 
	private static String DB_NAME = "com.dian.diabetes";

	@Override
	public void onCreate() {
		super.onCreate();
		if (mInstance == null)
			mInstance = this;
	}

	/**
	 * 取得DaoMaster
	 * 
	 * @param context
	 * @return
	 */
	public static DaoMaster getDaoMaster(Context context) {
		if (daoMaster == null) {
			OpenHelper helper = new DaoMaster.DevOpenHelper(context,
					DB_NAME, null);
			daoMaster = new DaoMaster(helper.getWritableDatabase());
		}
		return daoMaster;
	}

	/**
	 * 取得DaoSession
	 * 
	 * @param context
	 * @return
	 */
	public static DaoSession getDaoSession(Context context) {
		if (daoSession == null) {
			if (daoMaster == null) {
				daoMaster = getDaoMaster(context);
			}
			daoSession = daoMaster.newSession();
		}
		return daoSession;
	}
	
	public static SQLiteDatabase getSQLiteDatabase(Context context){
	    if (sqLiteDatabase == null) {
            OpenHelper helper = new DaoMaster.DevOpenHelper(context,
                    DB_NAME, null);
            sqLiteDatabase = helper.getWritableDatabase();
        }
	    return sqLiteDatabase;
	}
}
