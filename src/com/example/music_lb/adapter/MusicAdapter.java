package com.example.music_lb.adapter;

import java.text.SimpleDateFormat;
import java.util.List;


import com.example.music_lb.R;
import com.example.music_lb.util.MusicResource;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicAdapter extends BaseAdapter{

	private List<MusicResource> oList;
	private Context oContext;
	private LayoutInflater oInflater;
	
	
	public MusicAdapter(List<MusicResource> oList, Context oContext) {
		super();
		this.oList = oList;
		this.oContext = oContext;
		this.oInflater = oInflater.from(oContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return oList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return oList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder oHolder = null;
		if (view ==null) {
			oHolder = new ViewHolder();
			view = oInflater.inflate(R.layout.list_item, null);
			oHolder.img = (ImageView) view.findViewById(R.id.mu_img);
			oHolder.name = (TextView) view.findViewById(R.id.song);
			oHolder.author = (TextView) view.findViewById(R.id.artist);
			oHolder.duration = (TextView) view.findViewById(R.id.duration);
			view.setTag(oHolder);
		} else {
			oHolder = (ViewHolder) view.getTag();
		}
		oHolder.img.setBackgroundResource(R.drawable.ic_launcher);
		oHolder.name.setText(oList.get(position).getName());
		oHolder.author.setText(oList.get(position).getAuthor());
		oHolder.duration.setText(getTime(oList.get(position).getDuration()));
		return view;
	}
	//将时间的long类型转换为string类型
	private String getTime(long time) {
		SimpleDateFormat formats = new SimpleDateFormat("mm:ss");
		String times = formats.format(time);
		return times;
	}
	class ViewHolder {
		ImageView img;
		TextView name;
		TextView author;
		TextView duration;
	}

}
