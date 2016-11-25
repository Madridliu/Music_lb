package com.example.music_lb;

import java.util.List;

import com.example.music_lb.adapter.MusicAdapter;
import com.example.music_lb.util.MusicResource;
import com.example.music_lb.util.MusicUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	private ListView listview;
	private ImageButton btn_pre, btn_play, btn_next, imageButton_mode;
	private SeekBar SBar;
	private TextView time;
	private MusicAdapter adapter;
	private List<MusicResource> oList;
	private Context oContext;
	private MusicResource music;
	private int index = 0;
	private int flag = 0; // 0Ϊ�б�ѭ����1Ϊ����ѭ����2Ϊ�������
	private int state = 0x11;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        init(); //��ʼ������
        oList = MusicUtil.getMusicDate(oContext);
        adapter = new MusicAdapter(oList, oContext);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(onClickListener);
        btn_pre.setOnClickListener(oListener);
        btn_play.setOnClickListener(oListener);
        btn_next.setOnClickListener(oListener);
        imageButton_mode.setOnClickListener(oListener);
        seekbarChange();
    }
    
    public class MyBroadCastActivity extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			intent.getIntExtra("newmusic", -1);
			state = intent.getIntExtra("state", -1);
			switch (state) {
			case 0x11:
				btn_play.setImageResource(R.drawable.zt);
				break;
			case 0x12:
				btn_play.setImageResource(R.drawable.bf);
				break;
			case 0x13:
				btn_play.setImageResource(R.drawable.zt);
				break;
			default:
				break;
			}
			
			int duration = intent.getIntExtra("duration", -1);
			int curPosition = intent.getIntExtra("curPosition", -1);
			if (curPosition != -1) {
				SBar.setProgress( (int) ((curPosition * 1.0) / duration * 100)); //Ϊ�϶������õ�ǰ���Ž���
				/*���ڴ˴�ΪɶҪ��curPosition * 1.0 �����⣬
				 * ��ΪcurPosition��duration��Ϊint���Σ����ҵ�ǰʱ���ض�С�����г����������ض�ΪС��0����ʱ�����
				 * �ǹ��ͻ�ȡ���β���0������Ͷ�Ϊ0���޷��ﵽЧ����
				 * ����curPositon * 1.0������ͱ�ɸ����ͣ��ٳ���duration���ҲΪ�����ͣ����*100��Ϊ����
				 * */
				
				time.setText(initime(curPosition, duration)); //��ʾʱ��
			}
			
			boolean isover = intent.getBooleanExtra("Over", false);
			if (isover == true) {
				Intent intent2 = new Intent("com.chong.Service");
				if (flag == 0) { //˳�򲥷ż���һ��
				if (index == oList.size() - 1) {
					index = 0;
				} else {
					index++;
				}
				music = oList.get(index);
				intent2.putExtra("newmusic", 1);
				intent2.putExtra("music", music);
				sendBroadcast(intent2);
			} else if (flag == 1) { //����ѭ��
				music = oList.get(index);
				intent2.putExtra("newmusic", 1);
				intent2.putExtra("music", music);
				sendBroadcast(intent2);
				
			} else { //�������
				index = (int) (Math.random() * oList.size());
				music = oList.get(index);
				intent2.putExtra("newmusic", 1);
				intent2.putExtra("music", music);
				sendBroadcast(intent2);
			}
		  }
		}
    	
    }
    
    private OnItemClickListener onClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			index = position;  //��õ�ǰ�±�
			music = oList.get(position); //��õ�ǰѡ�е�λ��
			Intent intent = new Intent("com.chong.Service");
			intent.putExtra("music", music);
			intent.putExtra("newmusic", 1);
			sendBroadcast(intent); //���͹㲥Service����
		}
	};
	
	private OnClickListener oListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent("com.chong.Service");
			switch (v.getId()) {
			//��һ��
			case R.id.btn_pre:
				if (index == 0) {
					//���Ϊ��һ�׸裬�����һ����ť��
					index = oList.size() - 1; //��ǰ�±�Ϊ���һ�׸����±�
				} else {
					index--;
				}
				music = oList.get(index);
				intent.putExtra("newmusic", 1);
				intent.putExtra("music", music);
				break;
			//��ͣ
			case R.id.btn_play:
				/*if(state == 0x11){
					btn_play.setBackgroundResource(R.drawable.bf);
					state = 0x12;
				}else if (state == 0x12){
					btn_play.setBackgroundResource(R.drawable.zt);
					state = 0x11;
				}*/
				if (music == null) {  //�����һ�ν��벥����
					music = oList.get(index); //���ŵ�һ�׸���
					intent.putExtra("music", music);
				}
				intent.putExtra("isplay", 1);//��ǰ�Ƿ��ڲ���
				break;
			//��һ��
			case R.id.btn_next:
				if (index == oList.size() - 1) {
					index = 0;
				} else {
					index++;
				}
				music = oList.get(index);
				intent.putExtra("newmusic", 1);
				intent.putExtra("music", music);
				break;
			//����ģʽ	
			case R.id.btn_mode:
				flag++;
				if (flag > 2) {
					flag = 0;
				} if (flag == 0) {
					imageButton_mode.setImageResource(R.drawable.list);
					Toast.makeText(oContext, "�б�ѭ��", 2000).show();
				} else if (flag == 1) {
					imageButton_mode.setImageResource(R.drawable.danqu);
					Toast.makeText(oContext, "����ѭ��", 2000).show();
				} else {
					imageButton_mode.setImageResource(R.drawable.random);
					Toast.makeText(oContext, "�������", 2000).show();
				}
				
				default:
					break;
			}
			sendBroadcast(intent);
		}
	};
	
	private void seekbarChange() {
		SBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			//���϶���ֹͣ����ø÷���
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				Intent intent = new Intent("com.chong.Service");
				intent.putExtra("progress", seekBar.getProgress());//��ȡ��ǰ�϶���λ��
				sendBroadcast(intent);
			}
			//����ʼ�϶��϶������ø÷���
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			//���϶��������϶����ø÷���
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private String initime(int cur, int dur) {
		int cur_fen = cur / 1000 / 60;//����
		int cur_miao = cur / 1000 % 60; //��
		
		int dur_fen = dur / 1000 / 60;
		int dur_miao = dur / 1000 % 60;
		return getT(cur_fen) + ":" + getT(cur_miao) + "/" + getT(dur_fen) + ":" + getT(dur_miao);
	}
	
	private String getT(int time) {
		if (time < 10) {
			return "0" + time;
		} else {
			return time + "";
		}
	}
    
//�ؼ���ʼ��
    private void init() {
    	listview = (ListView) findViewById(R.id.list);
    	btn_pre = (ImageButton) findViewById(R.id.btn_pre);
    	btn_play = (ImageButton) findViewById(R.id.btn_play);
    	btn_next = (ImageButton) findViewById(R.id.btn_next);
    	imageButton_mode = (ImageButton) findViewById(R.id.btn_mode);
    	SBar = (SeekBar) findViewById(R.id.seekBar);
    	time = (TextView) findViewById(R.id.time);
    	oContext = MainActivity.this;
//    	ע��㲥
    	MyBroadCastActivity receiver = new MyBroadCastActivity();
    	IntentFilter filter = new IntentFilter("com.chong.Activity");
    	registerReceiver(receiver, filter);
    	//��������
    	Intent intent = new Intent(oContext, MusicService.class);
    	startService(intent);
    }
    
}
