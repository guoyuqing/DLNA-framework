package com.geniusgithub.mediarender.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.geniusgithub.mediarender.RenderApplication;

public class SharedUtils {
	private static final String SP_NAME = "dlna_soniq";
	private static SharedPreferences sharedPreferences = RenderApplication
			.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
	private static Editor editor = sharedPreferences.edit();

	public static String getString(String key, String defaultValue) {
		return sharedPreferences.getString(key, defaultValue);
	}

	public static int getInt(String key, int defaultValue) {
		return sharedPreferences.getInt(key, defaultValue);
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		return sharedPreferences.getBoolean(key, defaultValue);
	}

	public static void setString(String key, String value) {
		editor.putString(key, value);
		editor.commit();

	}

	public static void setInt(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public static void setBoolean(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static int getServerState() {
		return sharedPreferences.getInt("server_state", 1);

	}

	public static void setServerState(int state) {
		editor.putInt("server_state", state);
		editor.commit();
	}

	public static int getNameIndex() {
		return sharedPreferences.getInt("name_index", 0);

	}

	public static void setNameIndex(int state) {
		editor.putInt("name_index", state);
		editor.commit();
	}
}
