package com.example.music_lb.util;

import java.io.Serializable;

public class MusicResource implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String name;
	private String author;
	private String path;
	private Long duration;
	
	public static long getSerialversionuid(  ) {
		return serialVersionUID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}
	
	
}
	