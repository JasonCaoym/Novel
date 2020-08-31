package com.duoyue.app.upgrade.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.duoyue.lib.base.log.Logger;

/**
 * 文件下载数据库管理
 * 
 * @author fanwentao
 */
public class DaoDownloadManager {
	private static final String TAG = "App#DaoDownloadManager";
	private static DaoDownloadManager dao = null;
	private Context mContext;

	private DaoDownloadManager(Context context) {
		mContext = context;
	}

	public static DaoDownloadManager getInstance(Context context) {
		if (dao == null) {
			dao = new DaoDownloadManager(context);
		}
		return dao;
	}

	public SQLiteDatabase getConnection() {
		SQLiteDatabase sqliteDatabase = null;
		try {
			sqliteDatabase = new DBHelper(mContext).getReadableDatabase();
		} catch (Exception e) {

		}
		return sqliteDatabase;
	}

	public synchronized void saveInfos(DownloadInfo info) {
		Cursor cursor = null;
		SQLiteDatabase database = getConnection();
		try {
			String sql = "select * from " + DBHelper.DOWNTABLE
					+ " where fileName=?";
			ContentValues values = new ContentValues();
			values.put(DBHelper.URL, info.getUrl());
			values.put(DBHelper.FILE_SIZE, info.getFileSize());
			values.put(DBHelper.COMPLETE_SIZE, info.getCompleteSize());
			values.put(DBHelper.DOWN_STATE, info.getDownState());
			values.put(DBHelper.DOWN_PATH, info.getDownPath());
			values.put(DBHelper.FILE_NAME, info.getFileName());
			values.put(DBHelper.VERSION_NAME, info.getVersionName());
			values.put(DBHelper.VERSION_CODE, info.getVersionCode());

			cursor = database.rawQuery(sql, new String[] { info.getFileName() });

			if (cursor != null && cursor.getCount() > 0) {
				database.update(DBHelper.DOWNTABLE, values, "fileName=?",
						new String[] { info.getFileName() });
			} else {
				database.insert(DBHelper.DOWNTABLE, null, values);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (database != null) {
				database.close();
			}
		}
	}

	public synchronized DownloadInfo getInfos(String fileName) {
		DownloadInfo downloadInfo = null;
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = "select * from " + DBHelper.DOWNTABLE
					+ " where " + DBHelper.FILE_NAME + "=?";
			cursor = database.rawQuery(sql, new String[] { fileName });
			if (cursor != null && cursor.moveToFirst()) {
				downloadInfo = new DownloadInfo();
				downloadInfo.setUrl(cursor.getString(cursor
						.getColumnIndex(DBHelper.URL)));
				downloadInfo.setFileSize(cursor.getLong(cursor
						.getColumnIndex(DBHelper.FILE_SIZE)));
				downloadInfo.setCompleteSize(cursor.getLong(cursor
						.getColumnIndex(DBHelper.COMPLETE_SIZE)));
				downloadInfo.setDownState(cursor.getInt(cursor
						.getColumnIndex(DBHelper.DOWN_STATE)));
				downloadInfo.setDownPath(cursor.getString(cursor
						.getColumnIndex(DBHelper.DOWN_PATH)));
				downloadInfo.setFileName(cursor.getString(cursor
						.getColumnIndex(DBHelper.FILE_NAME)));
                downloadInfo.setVersionName(cursor.getString(cursor
                        .getColumnIndex(DBHelper.VERSION_NAME)));
                downloadInfo.setVersionCode(cursor.getInt(cursor
                        .getColumnIndex(DBHelper.VERSION_CODE)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}

		return downloadInfo;
	}

	public synchronized void delete(String fileName) {
		SQLiteDatabase database = getConnection();
		try {
			int result = database.delete(DBHelper.DOWNTABLE, DBHelper.FILE_NAME + "=?",
					new String[] { fileName });
			if (result > 0) {
				Logger.e(TAG, "apk数据库删除成功");
			} else {
                Logger.e(TAG, "apk数据库删除失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}

}
