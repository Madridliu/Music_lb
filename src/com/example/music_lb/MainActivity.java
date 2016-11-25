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
	private int flag = 0; // 0为列表循环，1为单曲循环，2为随机播放
	private int state = 0x11;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        init(); //初始化操作
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
				SBar.setProgress( (int) ((curPosition * 1.0) / duration * 100)); //为拖动条设置当前播放进度
				/*关于此处为啥要给curPosition * 1.0 的问题，
				 * 因为curPosition和duration都为int整形，而且当前时长必定小于总市场，相除结果必定为小于0，此时计算机
				 * 记过就会取整形部分0，结果就都为0，无法达到效果，
				 * 但给curPositon * 1.0，结果就变成浮点型，再除以duration结果也为浮点型，最后*100就为进度
				 * */
				
				time.setText(initime(curPosition, duration)); //显示时间
			}
			
			boolean isover = intent.getBooleanExtra("Over", false);
			if (isover == true) {
				Intent intent2 = new Intent("com.chong.Service");
				if (flag == 0) { //顺序播放即下一曲
				if (index == oList.size() - 1) {
					index = 0;
				} else {
					index++;
				}
				music = oList.get(index);
				intent2.putExtra("newmusic", 1);
				intent2.putExtra("music", music);
				sendBroadcast(intent2);
			} else if (flag == 1) { //单曲循环
				music = oList.get(index);
				intent2.putExtra("newmusic", 1);
				intent2.putExtra("music", music);
				sendBroadcast(intent2);
				
			} else { //随机播放
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
			index = position;  //获得当前下标
			music = oList.get(position); //获得当前选中的位置
			Intent intent = new Intent("com.chong.Service");
			intent.putExtra("music", music);
			intent.putExtra("newmusic", 1);
			sendBroadcast(intent); //发送广播Service服务
		}
	};
	
	private OnClickListener oListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent("com.chong.Service");
			switch (v.getId()) {
			//上一曲
			case R.id.btn_pre:
				if (index == 0) {
					//如果为第一首歌，点击上一曲按钮后
					index = oList.size() - 1; //当前下标为最后一首歌曲下标
				} else {
					index--;
				}
				music = oList.get(index);
				intent.putExtra("newmusic", 1);
				intent.putExtra("music", music);
				break;
			//暂停
			case R.id.btn_play:
				/*if(state == 0x11){
					btn_play.setBackgroundResource(R.drawable.bf);
					state = 0x12;
				}else if (state == 0x12){
					btn_play.setBackgroundResource(R.drawable.zt);
					state = 0x11;
				}*/
				if (music == null) {  //如果第一次进入播放器
					music = oList.get(index); //播放第一首歌曲
					intent.putExtra("music", music);
				}
				intent.putExtra("isplay", 1);//当前是否在播放
				break;
			//下一曲
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
			//播放模式	
			case R.id.btn_mode:
				flag++;
				if (flag > 2) {
					flag = 0;
				} if (flag == 0) {
					imageButton_mode.setImageResource(R.drawable.list);
					Toast.makeText(oContext, "列表循环", 2000).show();
				} else if (flag == 1) {
					imageButton_mode.setImageResource(R.drawable.danqu);
					Toast.makeText(oContext, "单曲循环", 2000).show();
				} else {
					imageButton_mode.setImageResource(R.drawable.random);
					Toast.makeText(oContext, "随机播放", 2000).show();
				}
				
				default:
					break;
			}
			sendBroadcast(intent);
		}
	};
	
	private void seekbarChange() {
		SBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			//当拖动条停止后调用该方法
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				Intent intent = new Intent("com.chong.Service");
				intent.putExtra("progress", seekBar.getProgress());//获取当前拖动条位置
				sendBroadcast(intent);
			}
			//当开始拖动拖动条调用该方法
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			//当拖动条正在拖动调用该方法
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private String initime(int cur, int dur) {
		int cur_fen = cur / 1000 / 60;//分钟
		int cur_miao = cur / 1000 % 60; //秒
		
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
    
//控件初始化
    private void init() {
    	listview = (ListView) findViewById(R.id.list);
    	btn_pre = (ImageButton) findViewById(R.id.btn_pre);
    	btn_play = (ImageButton) findViewById(R.id.btn_play);
    	btn_next = (ImageButton) findViewById(R.id.btn_next);
    	imageButton_mode = (ImageButton) findViewById(R.id.btn_mode);
    	SBar = (SeekBar) findViewById(R.id.seekBar);
    	time = (TextView) findViewById(R.id.time);
    	oContext = MainActivity.this;
//    	注册广播
    	MyBroadCastActivity receiver = new MyBroadCastActivity();
    	IntentFilter filter = new IntentFilter("com.chong.Activity");
    	registerReceiver(receiver, filter);
    	//启动服务
    	Intent intent = new Intent(oContext, MusicService.class);
    	startService(intent);
    }
    
}
