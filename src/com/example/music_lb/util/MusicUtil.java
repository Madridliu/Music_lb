package com.example.music_lb.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class MusicUtil {
	public static List<MusicResource> getMusicDate(Context context) {
		List<MusicResource> oList = new ArrayList<MusicResource>();
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, 
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		
		while(cursor.moveToNext()){
			MusicResource music = new MusicResource();
			//得到歌曲名
			String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
			//获取演唱者
			String author = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
			//获取路径
			String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
			//获取时间
			Long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
			//当歌曲名为unkonw，显示未知艺术家
			if(author.equals("<unknown>")) {
				author = "未知艺术家";
			}
			
			//当歌曲时长大于20秒，才获取歌曲
			if(duration > 60000) {
				music.setName(name);
				music.setAuthor(author);
				music.setPath(path);
				music.setDuration(duration);
				oList.add(music);
			} 
		}
		
		return oList;
		
	}
}
