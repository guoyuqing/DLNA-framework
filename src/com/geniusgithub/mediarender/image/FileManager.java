package com.geniusgithub.mediarender.image;

import java.io.File;

import android.content.Context;

import com.geniusgithub.mediarender.util.BitmapUtils;
import com.geniusgithub.mediarender.util.CommonUtil;

public class FileManager {

	public static String getSaveRootDir() {
		if (CommonUtil.hasSDCard()) {

			return CommonUtil.getRootFilePath() + "icons/";
		} else {
			return CommonUtil.getRootFilePath()
					+ "com.soniq.mediahelper/icons/";
		}
	}

	public static String getSaveFullPath(String uri, Context context) {
		// return getSaveRootDir() + getFormatUri(uri);
		// return getSaveRootDir() + BitmapUtils.Md5(uri)
		// + uri.substring(uri.length() - 4, uri.length());
		return context.getFilesDir() + File.separator + BitmapUtils.Md5(uri)
				+ uri.substring(uri.length() - 4, uri.length());
	}

	public static String getFormatUri(String uri) {
		uri = uri.replace("/", "_");
		uri = uri.replace(":", "");
		uri = uri.replace("?", "_");
		uri = uri.replace("%", "_");

		int length = uri.length();
		if (length > 150) {
			uri = uri.substring(length - 150);
		}

		return uri;
	}

}
