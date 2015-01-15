package com.geniusgithub.mediarender.music;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.geniusgithub.mediarender.center.DlnaMediaModel;
import com.geniusgithub.mediarender.center.DlnaMediaModelFactory;
import com.geniusgithub.mediarender.center.MediaControlBrocastFactory;
import com.geniusgithub.mediarender.player.MusicPlayEngineImpl;

public class MusicService extends Service implements
		MediaControlBrocastFactory.IMediaControlListener {

	private MusicPlayEngineImpl mPlayerEngineImpl;

	private DlnaMediaModel mMediaInfo = new DlnaMediaModel();
	private MediaControlBrocastFactory mMediaControlBorcastFactory;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		System.out.println("play::::::::state----------onCreateonCreate");

		mPlayerEngineImpl = new MusicPlayEngineImpl(this);
		mMediaControlBorcastFactory = new MediaControlBrocastFactory(this);
		mMediaControlBorcastFactory.register(this);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		refreshIntent(intent);
		return super.onStartCommand(intent, flags, startId);
	}

	private void refreshIntent(Intent intent) {
		System.out.println("play::::::::state----------playrefreshIntent");
		if (intent != null) {
			mMediaInfo = DlnaMediaModelFactory.createFromIntent(intent);
			mPlayerEngineImpl.playMedia(mMediaInfo);
		}

	}

	@Override
	public void onDestroy() {
		mPlayerEngineImpl.exit();
		super.onDestroy();
	}

	public void play() {
		System.out.println("play::::::::state----------play");
		mPlayerEngineImpl.play();
	}

	public void pause() {
		System.out.println("play::::::::state----------pause");

		mPlayerEngineImpl.pause();
	}

	public void stop() {
		System.out.println("play::::::::state----------stop");

		mPlayerEngineImpl.stop();
		stopSelf();
	}

	public void seek(int pos) {
		System.out.println("play::::::::state----------seek");

		mPlayerEngineImpl.skipTo(pos);

	}

	@Override
	public void onPlayCommand() {
		play();
	}

	@Override
	public void onPauseCommand() {
		pause();
	}

	@Override
	public void onStopCommand() {
		stop();
	}

	@Override
	public void onSeekCommand(int time) {
		seek(time);
	}

}
