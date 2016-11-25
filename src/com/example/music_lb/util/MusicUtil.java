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
			//�õ�������
			String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
			//��ȡ�ݳ���
			String author = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
			//��ȡ·��
			String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
			//��ȡʱ��
			Long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
			//��������Ϊunkonw����ʾδ֪������
			if(author.equals("<unknown>")) {
				author = "δ֪������";
			}
			
			//������ʱ������20�룬�Ż�ȡ����
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
