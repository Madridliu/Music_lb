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
	
	private int state = 0x11; //0x11:��һ�β��Ÿ�����0x12:��ͣ�� 0x13:��������
	
	private int curPosition, duration; //��ǰ����ʱ�䣬��ʱ��
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
//		ע��㲥
		MyBroadcastReceiver receiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter("com.chong.Service");
		registerReceiver(receiver, filter);
		//��ǰ����������ɺ���ø÷���
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
			newmusic = intent.getIntExtra("newmusic", -1); //�����Ƿ񲥷ŵ����¸���
			
			if (newmusic != -1) {
				music = (MusicResource) intent.getSerializableExtra("music"); //��ø�������
				if (music != null) {
					playmusic(music);
					state = 0x12;
				}
			}
			int isplay = intent.getIntExtra("isplay", -1);
			if (isplay != -1) {
				switch (state) {
				//��һ�β��Ÿ���
				case 0x11:
					music = (MusicResource) intent.getSerializableExtra("music"); //��ø�������
					playmusic(music);
					state = 0x12;
					break;
					//��ͣ����
				case 0x12:
					player.pause();
					state = 0x13;
					break;
					//��������
				case 0x13:
					player.start();
					state = 0x12;
					break;
				default:
					break;
				}
			}
			
			int progress = intent.getIntExtra("progress", -1);
			if (progress != -1) { // �����϶�
				curPosition = (int) (((progress * 1.0) / 100) * duration); //�ѵ�ǰ����λ��ת���ɺ���
				player.seekTo(curPosition);
			}
			
			Intent intent2 = new Intent("com.chong.Activity");
			intent2.putExtra("state", state);
			sendBroadcast(intent2); //�ѵ�ǰ״̬���͸�Activity
		}
		
	}
	//���Ÿ���
	public void playmusic(MusicResource resource) {
		if (player != null) {
			//ֹͣ����
			player.stop();
			player.reset();
			try {
				//��ȡ����·��
				player.setDataSource(resource.getPath());
				player.prepare(); //׼��
				player.start(); //����
				duration = player.getDuration();//��ȡ��ǰ����ʱ��
				new Thread() {
					public void run() {
						while(curPosition < duration) {
							try {
								sleep(1000);
								curPosition = player.getCurrentPosition(); //��õ�ǰ����ʱ��
								Intent intent = new Intent("com.chong.Activity");
								intent.putExtra("curPosition", curPosition);
								intent.putExtra("duration", duration);
								sendBroadcast(intent);//�ѵ�ǰ����ʱ�����ʱ�䷢�͸�Activity
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
