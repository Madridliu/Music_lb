package com.example.music_lb;

import java.io.IOException;

import com.example.music_lb.util.MusicResource;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;

public class MusicService extends Service{
	
	private int newmusic;
	private MusicResource music;
	private MediaPlayer player = new MediaPlayer();
	
	private int state = 0x11; //0x11:第一次播放歌曲，0x12:暂停， 0x13:继续播放
	
	private int curPosition, duration; //当前音乐时间，总时间
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
//		注册广播
		MyBroadcastReceiver receiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter("com.chong.Service");
		registerReceiver(receiver, filter);
		//当前歌曲播放完成后调用该方法
		player.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				Intent intent = new Intent("com.chong.Activity");
				intent.putExtra("Over", true);
				sendBroadcast(intent);
				curPosition = 0;
				duration = 0;
			}
		});
		super.onCreate();
	}
	
	public class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			newmusic = intent.getIntExtra("newmusic", -1); //接收是否播放的是新歌曲
			
			if (newmusic != -1) {
				music = (MusicResource) intent.getSerializableExtra("music"); //获得歌曲对象
				if (music != null) {
					playmusic(music);
					state = 0x12;
				}
			}
			int isplay = intent.getIntExtra("isplay", -1);
			if (isplay != -1) {
				switch (state) {
				//第一次播放歌曲
				case 0x11:
					music = (MusicResource) intent.getSerializableExtra("music"); //获得歌曲对象
					playmusic(music);
					state = 0x12;
					break;
					//暂停歌曲
				case 0x12:
					player.pause();
					state = 0x13;
					break;
					//继续播放
				case 0x13:
					player.start();
					state = 0x12;
					break;
				default:
					break;
				}
			}
			
			int progress = intent.getIntExtra("progress", -1);
			if (progress != -1) { // 正在拖动
				curPosition = (int) (((progress * 1.0) / 100) * duration); //把当前歌曲位置转换成毫秒
				player.seekTo(curPosition);
			}
			
			Intent intent2 = new Intent("com.chong.Activity");
			intent2.putExtra("state", state);
			sendBroadcast(intent2); //把当前状态发送给Activity
		}
		
	}
	//播放歌曲
	public void playmusic(MusicResource resource) {
		if (player != null) {
			//停止播放
			player.stop();
			player.reset();
			try {
				//获取播放路径
				player.setDataSource(resource.getPath());
				player.prepare(); //准备
				player.start(); //播放
				duration = player.getDuration();//获取当前歌曲时长
				new Thread() {
					public void run() {
						while(curPosition < duration) {
							try {
								sleep(1000);
								curPosition = player.getCurrentPosition(); //获得当前音乐时间
								Intent intent = new Intent("com.chong.Activity");
								intent.putExtra("curPosition", curPosition);
								intent.putExtra("duration", duration);
								sendBroadcast(intent);//把当前音乐时间和总时间发送给Activity
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					};
				}.start();
			}  catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
