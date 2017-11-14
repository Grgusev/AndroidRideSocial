package com.pjtech.android.ridesocial.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class LogUtil {

	public static final boolean WRITE_ENABLE = true;
	private static String logFileName = "AppLog.txt";
	public static void writeDebugLog(String tag, String msg) {
		Log.e(tag, msg);
		if (WRITE_ENABLE) {
			String sdstr = Environment.getExternalStorageDirectory().getPath();
			if (sdstr != null)
				sdstr += "/rideside/";
	
			File sdcard = new File(sdstr);
			if (!sdcard.exists())
				sdcard.mkdirs();
			File file = new File(sdcard, logFileName);
	
			if (file.exists() == false) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	
			try {
				// BufferedWriter for performance, true to set append to file flag
				BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
				String strAddr = "Tag : " + tag + "\n";
				buf.append(strAddr);
				String strData = "Msg : " + msg + "\n";
				buf.append(strData);
				buf.close();
				//
				Log.e(tag, msg);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void deleteLogFile() {
		String sdstr = Environment.getExternalStorageDirectory().getPath();
		if (sdstr != null)
			sdstr += "/rideside/";

		File sdcard = new File(sdstr);
		if (!sdcard.exists())
			sdcard.mkdirs();
		File file = new File(sdcard, logFileName);

		if (file.exists()) {
			try {
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void makeLogFileName() {
		if (WRITE_ENABLE) {
			Date dt = new Date();
			logFileName = "AppLog_"+String.valueOf(dt.getTime());
		}
	}
}