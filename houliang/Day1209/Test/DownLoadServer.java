package Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class DownLoadServer {
	static String src="d:/java_test/src/";
	static ArrayList<String> filelist=new ArrayList<>();
	public static void main(String[] args) {
		try {
			ServerSocket server=new ServerSocket(19999);
			Socket socket=server.accept();
			sendFileName(socket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void sendFileName(Socket socket){
		File file=new File(src); 
		String[] files=file.list();
		for(int i=0;i<files.length;i++){
			File file2=new File(src+files[i]);
			if(file2.isFile()){
				filelist.add(files[i]);
			}
		}
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<filelist.size();i++){
			sb.append(i+1+"."+filelist.get(i)+"  ");
		}
		String filename=sb.toString();
		try {
			PrintWriter writer=new PrintWriter(socket.getOutputStream());
			writer.println(filename);
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
