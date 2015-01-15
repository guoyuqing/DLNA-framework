package com.geniusgithub.mediarender.image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.geniusgithub.mediarender.BaseActivity;
import com.geniusgithub.mediarender.RenderApplication;
import com.geniusgithub.mediarender.center.DlnaMediaModel;
import com.geniusgithub.mediarender.center.DlnaMediaModelFactory;
import com.geniusgithub.mediarender.center.MediaControlBrocastFactory;
import com.geniusgithub.mediarender.util.BitmapUtils;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.CommonUtil;
import com.geniusgithub.mediarender.util.FileHelper;
import com.geniusgithub.mediarender.util.LogFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.DiscCacheUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.soniq.mediahelper.R;
import com.soniq.mediahelper.UniversalImageLoadTool;

public class ImageActivity extends BaseActivity implements
		MediaControlBrocastFactory.IMediaControlListener,
		DownLoadHelper.IDownLoadCallback {

	private static final CommonLog log = LogFactory.createLog();

	private int mScreenWidth = 0;
	private int mScreenHeight = 0;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	private Handler mHandler;
	private UIManager mUIManager;
	private DownLoadHelper mDownLoadHelper;
	private DelCacheFileManager mDelCacheFileManager;

	private DlnaMediaModel mMediaInfo = new DlnaMediaModel();
	private MediaControlBrocastFactory mMediaControlBorcastFactor;

	private boolean isAriPlay = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		log.e("onCreate");
		isAriPlay = getIntent().getBooleanExtra("isAriPlay", false);
		setContentView(R.layout.image_player_layout);
		RenderApplication.setImg(this);
		imageLoader = UniversalImageLoadTool.getImageLoader();
		options = new DisplayImageOptions.Builder().cacheOnDisc(true)
				.bitmapConfig(Bitmap.Config.ARGB_8888).build();
		initView();
		initData();
		if (isAriPlay) {
			String imagePath = getIntent().getStringExtra("img_path");
			onTransDelLoadResult(true, imagePath);
		} else {
			refreshIntent(getIntent());
		}
	}

	@Override
	protected void onDestroy() {
		log.e("onDestroy");

		mMediaControlBorcastFactor.unregister();
		mDownLoadHelper.unInit();
		mDelCacheFileManager.start(FileManager.getSaveRootDir());
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		isAriPlay = intent.getBooleanExtra("isAriPlay", false);
		if (isAriPlay) {
			String imagePath = intent.getStringExtra("img_path");
			onTransDelLoadResult(true, imagePath);
		} else {
			refreshIntent(intent);
		}
	}

	private void initView() {
		mUIManager = new UIManager();
	}

	private static final int REFRESH_SPEED = 0x0001;
	private static final int EXIT_ACTIVITY = 0x0002;

	private void initData() {
		mScreenWidth = CommonUtil.getScreenWidth(this);
		mScreenHeight = CommonUtil.getScreenHeight(this);

		mMediaControlBorcastFactor = new MediaControlBrocastFactory(this);
		mMediaControlBorcastFactor.register(this);

		mDownLoadHelper = new DownLoadHelper();
		mDownLoadHelper.init();

		mDelCacheFileManager = new DelCacheFileManager();

		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case EXIT_ACTIVITY:
					finish();
					break;
				}
			}

		};

	}

	private void refreshIntent(Intent intent) {
		removeExitMessage();
		if (intent != null) {
			mMediaInfo = DlnaMediaModelFactory.createFromIntent(intent);
		}

		String requesUrl = mMediaInfo.getUrl();
		String saveUri = FileManager.getSaveFullPath(requesUrl, this);
		if (null == saveUri || saveUri.length() < 1) {
			return;
		}

		mUIManager.showProgress(true);
		// mDownLoadHelper.syncDownLoadFile(mMediaInfo.getUrl(),
		// FileManager.getSaveFullPath(requesUrl, this), this);
		imageLoader.displayImage(mMediaInfo.getUrl(), mUIManager.mImageView,
				options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						mUIManager.showProgress(true);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						String message = null;
						switch (failReason.getType()) {
						case IO_ERROR:
							message = "图片无法加载，请检查网络是否正常！";
							break;
						case DECODING_ERROR:
							message = "图片无法显示";
							break;
						case NETWORK_DENIED:
							message = "网络有问题，无法下载";
							break;
						case OUT_OF_MEMORY:
							message = "图片太大无法显示";
							break;
						case UNKNOWN:
							message = "未知的错误";
							break;
						}
						Toast.makeText(ImageActivity.this, message,
								Toast.LENGTH_SHORT);
						mUIManager.showProgress(false);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						if (ImageActivity.this.isFinishing()) {
							return;
						}
						mUIManager.showProgress(false);
						int angle = BitmapUtils.readPictureDegree(DiscCacheUtil
								.findInCache(imageUri,
										imageLoader.getDiscCache())
								.getAbsolutePath());
						if (angle != 0) {
							loadedImage = BitmapUtils.rotaingImageView(angle,
									loadedImage);
							mUIManager.setBitmap(loadedImage);
						}
					}
				});
	}

	private void removeExitMessage() {
		mHandler.removeMessages(EXIT_ACTIVITY);
	}

	private static final int EXIT_DELAY_TIME = 2000;

	private void delayToExit() {
		removeExitMessage();
		mHandler.sendEmptyMessageDelayed(EXIT_ACTIVITY, EXIT_DELAY_TIME);
	}

	class UIManager {
		public ImageView mImageView;
		public View mLoadView;

		public Bitmap recycleBitmap;
		public boolean mIsScalBitmap = false;

		public UIManager() {
			initView();
		}

		private void initView() {
			mImageView = (ImageView) findViewById(R.id.imageview);
			mLoadView = findViewById(R.id.show_load_progress);
		}

		public void setBitmap(Bitmap bitmap) {
			if (recycleBitmap != null && !recycleBitmap.isRecycled()) {
				mImageView.setImageBitmap(null);
				recycleBitmap.recycle();
				recycleBitmap = null;
			}

			// if (mIsScalBitmap) {
			// mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			// } else {
			// mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			// }
			mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

			recycleBitmap = bitmap;
			mImageView.setImageBitmap(recycleBitmap);
		}

		public boolean isLoadViewShow() {
			if (mLoadView.getVisibility() == View.VISIBLE) {
				return true;
			}
			return false;
		}

		public void showProgress(boolean bShow) {

			if (bShow) {
				mLoadView.setVisibility(View.VISIBLE);
			} else {
				mLoadView.setVisibility(View.GONE);
			}

		}

		public void showLoadFailTip() {
			showToask(R.string.load_image_fail);
		}

		public void showParseFailTip() {
			showToask(R.string.parse_image_fail);
		}

		private void showToask(int tip) {
			Toast.makeText(ImageActivity.this, tip, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void downLoadResult(boolean isSuccess, String savePath) {

		onTransDelLoadResult(isSuccess, savePath);
	}

	private void onTransDelLoadResult(final boolean isSuccess,
			final String savePath) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mUIManager.showProgress(false);

				if (!isSuccess) {
					mUIManager.showLoadFailTip();
					return;
				}
				Bitmap bitmap = decodeOptionsFile(savePath);
				if (bitmap == null) {
					mUIManager.showParseFailTip();
					return;
				}
				int angle = BitmapUtils.readPictureDegree(savePath);
				System.out.println("旋转：：：：：：：：：：：：：：：："
						+ BitmapUtils.readPictureDegree(savePath));
				bitmap = BitmapUtils.rotaingImageView(angle, bitmap);

				System.out.println("img::::::::::::" + bitmap.getWidth()
						+ "       " + bitmap.getHeight());
				mUIManager.setBitmap(bitmap);
			}
		});

	}

	public Bitmap decodeOptionsFile(String filePath) {
		System.out.println("path::::::::::::::" + filePath);
		if (filePath == null) {
			return null;
		}
		try {
			File file = new File(filePath);
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(file), null, o);
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			if (width_tmp <= mScreenWidth && height_tmp <= mScreenHeight) {
				scale = 1;
				mUIManager.mIsScalBitmap = false;
			} else {
				double widthFit = width_tmp * 1.0 / mScreenWidth;
				double heightFit = height_tmp * 1.0 / mScreenHeight;
				double fit = widthFit > heightFit ? widthFit : heightFit;
				scale = (int) (fit + 0.5);
				mUIManager.mIsScalBitmap = true;
			}
			Bitmap bitmap = null;
			if (scale == 1) {
				byte[] data = readStream(new FileInputStream(file));
				if (data != null) {
					bitmap = BitmapFactory
							.decodeByteArray(data, 0, data.length);
				}
				// bitmap = BitmapFactory.decodeStream(new
				// FileInputStream(file));
				if (bitmap != null) {
					log.e("scale = 1 bitmap.size = " + bitmap.getRowBytes()
							* bitmap.getHeight());
				} else {
					System.out.println("eeeeeeeeeeeeeeeee:====");

				}
			} else {
				BitmapFactory.Options o2 = new BitmapFactory.Options();
				o2.inSampleSize = scale;
				bitmap = BitmapFactory.decodeStream(new FileInputStream(file),
						null, o2);
				// byte[] data = readStream(new FileInputStream(file));
				// if (data != null) {
				// bitmap = BitmapFactory
				// .decodeByteArray(data, 0, data.length);
				// }
				if (bitmap != null) {
					log.e("scale = " + o2.inSampleSize + " bitmap.size = "
							+ bitmap.getRowBytes() * bitmap.getHeight());
				} else {
					System.out.println("eeeeeeeeeeeeeeeee:++++++++++");

				}
			}

			return bitmap;

		} catch (Exception e) {
			log.e("fileNotFoundException, e: " + e.toString());
			System.out.println("eeeeeeeeeeeeeeeee:" + e.toString());
		}
		return null;
	}

	/*
	 * 得到图片字节流 数组大小
	 */
	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}

	class DelCacheFileManager implements Runnable {
		private Thread mThread;
		private String mFilePath;

		public DelCacheFileManager() {

		}

		@Override
		public void run() {

			long time = System.currentTimeMillis();
			log.e("DelCacheFileManager run...");
			try {
				FileHelper.deleteDirectory(mFilePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			long interval = System.currentTimeMillis() - time;
			log.e("DelCacheFileManager del over, cost time = " + interval);
		}

		public boolean start(String directory) {
			if (mThread != null) {
				if (mThread.isAlive()) {
					return false;
				}
			}
			mFilePath = directory;
			mThread = new Thread(this);
			mThread.start();

			return true;
		}

	}

	@Override
	public void onPlayCommand() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPauseCommand() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopCommand() {
		log.e("onStopCommand");
		delayToExit();
	}

	@Override
	public void onSeekCommand(int time) {

	}

}
