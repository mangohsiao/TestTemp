package com.mango.tcpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.ObjectInputStream.GetField;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

public class Main {

//	public static final String IP = "125.216.243.235";
	public static final String IP = "127.0.0.1";
	
	public static void sendMsg(OutputStream os, String s, short type) throws IOException{

		byte[] strBytes = s.getBytes();
		int len = strBytes.length;

		byte[] head = new byte[4 + len];
		head[0] = (byte)type;
		head[1] = 0x00;
		head[2] = (byte)((len & 0x0000ff00) >> 8);
		head[3] = (byte)(len & 0x000000ff);
		
		for (int i = 0, j=4; i < len; i++, j++) {
			head[j] = strBytes[i];
		}
		os.write(head, 0, len+4);
		os.flush();
	}
	
	public static void main(String[] args) {
		int servPort = 8899;
		String user = "default";
		String uuid = "default";
		System.out.println("args = " + args.length);
		if (args.length == 3) {
			servPort = Integer.parseInt(args[0]);
			System.out.println("port : " + servPort);
			user = args[1];
			System.out.println("user : " + user);
			uuid = args[2];
			System.out.println("uuid : " + uuid);
		}
		
		MsgHead msg = new MsgHead();
		short t0 = 0x00;
		short t1 = (short)(12 << 8);
		msg.type_and_resrv = (short) (t1 | t0);
		
		
		//header
		int type = (msg.type_and_resrv & 0xFF00) >> 8;
		int resrv = msg.type_and_resrv & 0x00FF;

		System.out.println("type = " + type);
		System.out.println("resrv = " + resrv);
		
		try {
			Socket client = new Socket(IP, servPort);
			client.setSoTimeout(30000);
			InputStream is = client.getInputStream();
			OutputStream os = client.getOutputStream();
			
			ReadThread mReadThread = new ReadThread(is);
			new Thread(mReadThread).start();

			Works mWorks = new Works(os);
			new Thread(mWorks).start();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String s;
			int msgType;
			short headerType = 0x00;
			while((s = br.readLine()) != null){
				JSONObject jsonToSend = new JSONObject();
				msgType = Integer.parseInt(s);
				headerType = (short) msgType;
				System.out.println("short = " + headerType);
				switch (msgType) {

				case MCommon.MSG_PHONE_LOGIN:
					jsonToSend.put("USER", user);
					jsonToSend.put("PSWD", "HEYJUDE");
					break;
				case MCommon.MSG_PHONE_LOGOUT:
					jsonToSend.put("USER", user);
					jsonToSend.put("PSWD", "HEYJUDE");
					break;
				case MCommon.MSG_PHONE_REG_UUID:
					jsonToSend.put("USER", user);
					jsonToSend.put("TYPE", "MSG_PHONE_REG_UUID");
					jsonToSend.put("UUID", uuid);
					jsonToSend.put("PSWD", "HEYJUDE");
					break;
				case MCommon.MSG_PHONE_UNREG_UUID:
					jsonToSend.put("USER", user);
					jsonToSend.put("UUID", uuid);
					jsonToSend.put("PSWD", "HEYJUDE");
					break;
				case MCommon.MSG_HOME_REG:
					jsonToSend.put("TYPE", "HOME_REG");
					jsonToSend.put("UUID", uuid);
					break;
				case MCommon.MSG_HOME_UNREG:
					jsonToSend.put("TYPE", "HOME_UNREG");
					jsonToSend.put("UUID", uuid);
					break;
				case MCommon.MSG_HOME_ALARM:
					jsonToSend.put("UUID", uuid);
					jsonToSend.put("MSG01", "hello");
					jsonToSend.put("MSG02", "mango");
					jsonToSend.put("MSG03", "yeah.");
					break;
				case MCommon.MSG_HOME_UPDATE:
					jsonToSend.put("UUID", uuid);
					jsonToSend.put("MSG01", "ONE");
					jsonToSend.put("MSG02", "TWO");
					jsonToSend.put("MSG03", "THREE.");
					break;
				case MCommon.MSG_HOME_SYNC:
					jsonToSend.put("UUID", uuid);
					jsonToSend.put("MSG01", "SYNC_01");
					jsonToSend.put("MSG02", "SYNC_SYNC");
					jsonToSend.put("MSG03", "AHHAHAHH.");
					break;
					
				default:
					break;
				}
				
//				JSONObject json = new JSONObject();
//				json.put("STR", s);
//				sendMsg(os, json.toString(), (short)44);
				sendMsg(os, jsonToSend.toString(), headerType);
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
