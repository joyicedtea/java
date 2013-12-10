package com.eoe.se2.day07.download2;

import java.io.Serializable;

public class Record implements Serializable {

	private static final long serialVersionUID = 1L;
	private long start;
	private long end;
	private String filename;
	private int threadi;
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public int getThreadi() {
		return threadi;
	}
	public void setThreadi(int threadi) {
		this.threadi = threadi;
	}
}
