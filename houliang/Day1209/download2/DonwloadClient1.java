package com.eoe.se2.day07.download2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DonwloadClient1 {
	static final String DEST_PATH = "d:/java_test/dest/";
	static final String FILENAME = "cocos2d_x.zip";
	static final String RECORD_FILENAME = "cocos2d_x_record.dat";
	static int THREAD_COUNT = 4;
	static Record[] records;
	static RecordInfo recordInfo;
	static boolean isContinue = true;
	static ExecutorService pools;

	public static void main(String[] args) {
		pools = Executors.newFixedThreadPool(20);
		pools.execute(new MonitorThread());
		if (!readRecord()) {
			try {
				readResponse();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			/*
			 * 若是断点续传，则向服务端发送一个不是filename和download的请求类型，
			 * 目的是消耗服务端的 Record
			 * record=responseToClient(socket);
			 */
			try {
				Socket socket = new Socket("127.0.0.1", 19999);
				ObjectOutputStream oos = new ObjectOutputStream(
						socket.getOutputStream());
				RecordInfo info = new RecordInfo();
				info.setRequestType("record");
				oos.writeObject(info);
				oos.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (int i = 0; i < records.length; i++) {
			pools.execute(new DownloadTask(i));
		}
		pools.shutdownNow();
	}

	static class DownloadTask implements Runnable {
		int index;

		public DownloadTask(int index) {
			this.index = index;
		}

		@Override
		public void run() {
			RandomAccessFile raf = null;
			Socket socket = null;
			RecordInfo info = null;
			try {
				socket = new Socket("127.0.0.1", 19999);
				InputStream in = socket.getInputStream();
				info = new RecordInfo();
				info.setRequestType("download");
				Record record = records[index];
				info.setRecord(record);
				ObjectOutputStream oos = new ObjectOutputStream(
						socket.getOutputStream());
				oos.writeObject(info);
				raf = new RandomAccessFile(DEST_PATH + record.getFilename(),
						"rw");
				int len = 0;
				byte[] buffer = new byte[1024];
				long start = record.getStart();
				long end = record.getEnd();
				raf.seek(start);
				System.out.println(record.getFilename() + "第" + index + "块开始下载");
				while (start < end && isContinue) {
					len = in.read(buffer);
					raf.write(buffer, 0, len);
					start += len;
				}
				if (!isContinue) {
					records[index].setStart(start);
					saveRecord();
				} else {
					System.out.println(record.getFilename() + "第" + index
							+ "块下载完毕");
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					FileInputStream fis=new FileInputStream(DEST_PATH+FILENAME);
					long filesize=fis.available();
					if(filesize>=recordInfo.getFileSize()){
						File file=new File(DEST_PATH+RECORD_FILENAME);
						file.delete();
					}
					if (socket != null) {
						socket.close();
					}
					if (raf != null) {
						raf.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	static void readResponse() throws Exception {
		Socket socket = new Socket("127.0.0.1", 19999);
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		recordInfo = new RecordInfo();
		recordInfo.setRequestType("filename");
		recordInfo.getRecord().setFilename(FILENAME);
		oos.writeObject(recordInfo);
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		//读取服务端返回的包含文件长度的对象
		recordInfo = (RecordInfo) ois.readObject();
		ois.close();
		oos.close();
		socket.close();
		long blockSize = recordInfo.getFileSize() / THREAD_COUNT;
		for (int i = 0; i < records.length; i++) {
			records[i] = new Record();
			records[i].setStart(i * blockSize);
			records[i].setEnd((i + 1) * blockSize - 1);
			records[i].setFilename(FILENAME);
			records[i].setThreadi(i);
		}
		records[THREAD_COUNT - 1].setEnd(recordInfo.getFileSize() - 1);
	}

	static void saveRecord() {
		File file = new File(DEST_PATH + RECORD_FILENAME);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(records);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != oos) {
				try {
					oos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	static boolean readRecord() {
		File file = new File(DEST_PATH + RECORD_FILENAME);
		try {
			if (!file.exists()) {
				file.createNewFile();
				records = new Record[THREAD_COUNT];
				return false;
			}
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			records = (Record[]) ois.readObject();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	static class MonitorThread extends Thread {
		@Override
		public void run() {
			Scanner scanner = new Scanner(System.in);
			while (isContinue) {
				System.out.println("输入q停止下载");
				String s = scanner.next();
				if ("q".equals(s)) {
					isContinue = false;
				}
			}
		}
	}
}





