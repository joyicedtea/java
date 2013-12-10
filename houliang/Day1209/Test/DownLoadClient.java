package Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class DownLoadClient {
	public static void main(String[] args) {
		try {
			Socket socket=new Socket("127.0.0.1", 19999);
			String filelist=receivefilelist(socket);
			System.out.println(filelist);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String receivefilelist(Socket socket) {
		String filelist;
		try {
			BufferedReader reader=new BufferedReader(new InputStreamReader
					                  (socket.getInputStream()));
			filelist = reader.readLine();
			return filelist;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
