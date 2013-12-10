package com.eoe.se2.day07.download2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadServer {
	static final String SRC_PATH="d:/java_test/src/";
	public static void main(String[] args) {
		ExecutorService pools=Executors.newCachedThreadPool();
		try {
			ServerSocket server=new ServerSocket(19999);
			System.out.println("等待客户端下载请求...");
			while(true){
				Socket socket=server.accept();
				Record record=responseToClient(socket);
				if(null!=record){
					pools.execute(new DownloadTask(socket,record));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static Record responseToClient(Socket socket) {
		Record record=null;
		try {
			ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
			RecordInfo info=(RecordInfo) ois.readObject();
			if("download".equals(info.getRequestType())){
				record=info.getRecord();
			}else if(info.getRequestType().equals("filename")){
				FileInputStream fis=new FileInputStream(SRC_PATH+info.getRecord().getFilename());
				long fileSize=fis.available();
				info.setFileSize(fileSize);
				oos.writeObject(info);
			}else if("record".equals(info.getRequestType())){
				socket.close();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return record;
	}
	static class DownloadTask implements Runnable{
		Socket socket;
		Record record;
		
		/**
		 * @param socket
		 * @param record
		 */
		public DownloadTask(Socket socket, Record record) {
			super();
			this.socket = socket;
			this.record = record;
		}


		@Override
		public void run() {
			File file=new File(SRC_PATH+record.getFilename());
			try {
				RandomAccessFile raf=new RandomAccessFile(file, "r");
				OutputStream out=socket.getOutputStream();
				long start=record.getStart();
				long end=record.getEnd();
				raf.seek(start);
				byte[] buffer=new byte[1024];
				int len;
				//System.out.println(record.getFilename()+"-"+record.getThreadi()+"开始下载");
				while(start<end){
					len=raf.read(buffer);
						start+=len;
//					if(start+len<end){
//					}else{
//						len=(int)(end-start);
//						start=end;
//					}
					if(len!=-1){
					out.write(buffer, 0, len);
					}
				}
				//System.out.println(record.getFilename()+"-"+record.getThreadi()+"下载完毕");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(SocketException e){
				System.out.println(record.getThreadi()+"下载完毕");
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
