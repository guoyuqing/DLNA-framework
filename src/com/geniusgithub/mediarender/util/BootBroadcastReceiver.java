/**
 * 
 */
package com.geniusgithub.mediarender.util;

import com.soniq.mediahelper.MediaService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * @author Administrator
 * 
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// // if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
		// // CrashHandler handler = CrashHandler.getInstance();
		// // handler.init(context);
		context.startService(new Intent(context, MediaService.class).putExtra(
				"isShowToast", false));
		//
		// // }

	}
}
