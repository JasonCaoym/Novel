package com.duoyue.app.upgrade.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
	private static int version = 1;
	private static String mDataBaseName = "leagoo_donwload.db";
	public static final String DOWNTABLE = "download_info";  // 文件下载数据表

	public static final String FILE_SIZE = "fileSize";
	public static final String COMPLETE_SIZE = "completeSize";
	public static final String URL = "url";
	public static final String VERSION_NAME = "versionName";
	public static final String VERSION_CODE = "versionCode";
	public static final String DOWN_STATE = "downState";
	public static final String DOWN_PATH = "downPath";
	public static final String FILE_NAME = "fileName";

	public DBHelper(Context context) {
		super(context, mDataBaseName, null, version);
	}
	
	private String getDownloadScript() {
		String sql = "create table "+ DOWNTABLE +"(_id integer PRIMARY KEY AUTOINCREMENT, "
						+ DBHelper.FILE_SIZE + " Long, "
                		+ DBHelper.COMPLETE_SIZE + " Long, "
						+ DBHelper.URL + " varchar, "
						+ DBHelper.VERSION_NAME + " varchar, "
						+ DBHelper.VERSION_CODE +" Integer, "
						+ DBHelper.DOWN_STATE + " integer, "
						+ DBHelper.DOWN_PATH + " varchar, "
						+ DBHelper.FILE_NAME + " varchar"
						+ ")";
		return sql;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();
		try {
			db.execSQL(getDownloadScript());
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		onCreate(arg0);
	}
	
}
