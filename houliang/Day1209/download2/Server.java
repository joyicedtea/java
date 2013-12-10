package com.eoe.se2.day07.download2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
	static String src="d:/java_test/src/";
	public static void main(String[] args) {
		ExecutorService pools=Executors.newCachedThreadPool();
		try {
			ServerSocket server=new ServerSocket(19999);
			System.out.println("等待客户端接入"); 
			while(true){
				Socket socket=server.accept();
				Record record=responseToClient(socket);
				if(null!=record){
					pools.execute(new DownLoad());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static Record responseToClient(Socket socket) {
		Record record=null;
		
		return record;
	}
	static class DownLoad implements Runnable{
		@Override
		public void run() {
			
		}
		
	}

}
