package com.soniq.mediahelper;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geniusgithub.mediarender.BaseActivity;
import com.geniusgithub.mediarender.DeviceUpdateBrocastFactory;
import com.geniusgithub.mediarender.RenderApplication;
import com.geniusgithub.mediarender.image.ImageActivity;
import com.geniusgithub.mediarender.util.DlnaUtils;
import com.geniusgithub.mediarender.util.SharedUtils;
import com.geniusgithub.mediarender.video.VideoActivity;

/**
 * @github https://github.com/geniusgithub
 */
public class MainActivity extends BaseActivity implements
		DeviceUpdateBrocastFactory.IDevUpdateListener, OnFocusChangeListener {
	private static MainActivity _mainActivity = null;

	public static MainActivity getInstance() {
		return _mainActivity;
	}

	private TextView txt_ip;
	private TextView txt_server_state;
	private TextView txt_name;
	private TextView txt_version;
	private String nameArr[] = new String[] { "客厅TV", "卧室TV" };
	private int nameIndex = 0;
	private int serverState = 0;
	private RelativeLayout layout_name;
	private RelativeLayout layout_server;

	private ImageView img_server_state_left;
	private ImageView img_server_state_right;

	private String str_ip = "";

	private int state = Constants.STATE_NONE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		nameIndex = SharedUtils.getNameIndex();
		serverState = SharedUtils.getServerState();
		_mainActivity = this;
		setupView();
		startService(new Intent(this, MediaService.class).putExtra(
				"isShowToast", true));
		// 启动后，自动检测版本更新
		ClientUpgrade cu = new ClientUpgrade(this);
		cu.startCheckVersion(null);
		registerBoradcastReceiver();
	}

	private void setupView() {
		img_server_state_left = (ImageView) findViewById(R.id.img_server_state_left);
		img_server_state_right = (ImageView) findViewById(R.id.img_server_state_right);
		txt_version = (TextView) findViewById(R.id.txt_version);
		layout_server = (RelativeLayout) findViewById(R.id.layout_server);
		layout_name = (RelativeLayout) findViewById(R.id.layout_name);
		txt_ip = (TextView) findViewById(R.id.txt_ip);
		str_ip = DlnaUtils.getLocalIP(this);
		if (str_ip == null || "".equals(str_ip)) {
			Toast.makeText(this, "网络无连接,请检查网络", Toast.LENGTH_SHORT).show();
		}
		txt_ip.setText(str_ip);
		txt_version.setText("当前版本：" + DlnaUtils.getVersionName(this));
		txt_server_state = (TextView) findViewById(R.id.txt_server_state);
		txt_name = (TextView) findViewById(R.id.txt_name);
		txt_name.setText(nameArr[nameIndex]);
		DlnaUtils.getFocus(layout_name);
		DlnaUtils.getFocus(layout_server);
		if (serverState == 0) {
			txt_server_state.setText("已关闭");
			state = Constants.STATE_CLOSEED;
		} else {
			txt_server_state.setText("已开启");
			state = Constants.STATE_OPENED;
			// mRenderProxy.startEngine();
			// startAriPlay();
		}
		// focus(txt_server_state, true);
		focusServerState(true);
		focus(txt_name, false);

		layout_server.setOnFocusChangeListener(this);
		layout_name.setOnFocusChangeListener(this);

	}

	private void focusServerState(boolean focus) {
		if (focus) {
			img_server_state_left
					.setImageResource(R.drawable.arrow_left_select);
			img_server_state_right
					.setImageResource(R.drawable.arrow_right_select);
		} else {
			img_server_state_left
					.setImageResource(R.drawable.arrow_left_nolmal);
			img_server_state_right
					.setImageResource(R.drawable.arrow_right_nolmal);
		}
		txt_server_state.setActivated(focus);

	}

	@Override
	public void onUpdate() {
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		System.out.println("state:::::::::::::::::" + state);
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			if (state == Constants.STATE_OPENED
					|| state == Constants.STATE_CLOSEED) {
				if (layout_server.isFocused()) {
					txtServerFocus();
				} else if (layout_name.isFocused()) {
					txtNameFocus(keyCode);
				}
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			if (state == Constants.STATE_OPENED
					|| state == Constants.STATE_CLOSEED) {
				if (layout_server.isFocused()) {
					txtServerFocus();
				} else if (layout_name.isFocused()) {
					txtNameFocus(keyCode);
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void txtServerFocus() {
		if (serverState == 0) {
			txt_server_state.setText("已开启");
			serverState = 1;

		} else {
			txt_server_state.setText("已关闭");
			serverState = 0;
		}
		SharedUtils.setServerState(serverState);
	}

	private void txtNameFocus(int keyCode) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			nameIndex--;
			if (nameIndex < 0) {
				nameIndex = nameArr.length - 1;
			}
			txt_name.setText(nameArr[nameIndex]);
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			nameIndex++;
			if (nameIndex >= nameArr.length) {
				nameIndex = 0;
			}
			txt_name.setText(nameArr[nameIndex]);
		}
		SharedUtils.setNameIndex(nameIndex);
		DlnaUtils.setDevName(this, nameArr[nameIndex]);
		Toast.makeText(this, "名称更改后关闭再开启生效", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onFocusChange(View v, boolean focus) {
		switch (v.getId()) {
		case R.id.layout_name:
			focus(txt_name, focus);
			break;
		case R.id.layout_server:
			// focus(txt_server_state, focus);
			focusServerState(focus);
			break;
		default:
			break;
		}
	}

	@SuppressLint("NewApi")
	private void focus(TextView txt, boolean focus) {
		if (focus) {

			Drawable drawableLeft = getResources().getDrawable(
					R.drawable.arrow_left_select);
			Drawable drawableRight = getResources().getDrawable(
					R.drawable.arrow_right_select);
			drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(),
					drawableLeft.getMinimumHeight());
			drawableRight.setBounds(0, 0, drawableRight.getMinimumWidth(),
					drawableRight.getMinimumHeight());
			txt.setCompoundDrawables(drawableLeft, null, drawableRight, null);
		} else {
			Drawable drawableLeft = getResources().getDrawable(
					R.drawable.arrow_left_nolmal);
			Drawable drawableRight = getResources().getDrawable(
					R.drawable.arrow_right_nolmal);
			drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(),
					drawableLeft.getMinimumHeight());
			drawableRight.setBounds(0, 0, drawableRight.getMinimumWidth(),
					drawableRight.getMinimumHeight());
			txt.setCompoundDrawables(drawableLeft, null, drawableRight, null);
		}
		txt.setActivated(focus);
	}

	public void showImage(String image_path) {
		RenderApplication.closeMusic();
		RenderApplication.closeVideo();
		Intent intent = new Intent();
		intent.putExtra("isAriPlay", true);
		intent.putExtra("img_path", image_path);
		intent.setClass(this, ImageActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}

	public void showVideo(String video_path) {
		RenderApplication.closeImg();
		RenderApplication.closeMusic();
		Intent intent = new Intent();
		intent.putExtra("isAriPlay", true);
		intent.putExtra("video_path", video_path);
		intent.setClass(this, VideoActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}

	/**
	 * 注册该广播
	 */
	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(Constants.OPEN_CLOSE_STATE);
		// 注册广播
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	/**
	 * 定义广播
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Constants.OPEN_CLOSE_STATE)) {
				int server_state = intent.getIntExtra("state",
						Constants.STATE_NONE);
				state = server_state;
				switch (state) {
				case Constants.STATE_NONE:

					break;
				case Constants.STATE_OPENED:
					txt_server_state.setText("已开启");

					break;
				case Constants.STATE_OPENING:
					txt_server_state.setText("开启中...");

					break;
				case Constants.STATE_CLOSEED:
					txt_server_state.setText("已关闭");
					break;
				case Constants.STATE_CLOSEING:
					txt_server_state.setText("关闭中...");

					break;
				default:
					break;
				}
			}
		}
	};

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver);
	};
}
