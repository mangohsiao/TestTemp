package com.mango.tcpclient;

import java.io.IOException;
import java.io.InputStream;

public class ReadThread implements Runnable {

	InputStream is;

	public ReadThread(InputStream is) {
		super();
		this.is = is;
	}

	@Override
	public void run() {
		byte[] rBuf = new byte[8192];
		int rtvl = -1;
		short len = 0;
		String strIn;
		boolean err = false;
		try {
			while (!err) {
				rtvl = is.read(rBuf, 0, 2);
				if(rtvl < 0){
					err = true;
				}
				if (rBuf[0] == 0x02) {
					System.out.println("HB 0x02");
					continue;
				}
				rtvl = is.read(rBuf, 2, 2);
				if(rtvl < 0){
					err = true;
				}
				short l1 = (short)rBuf[2];
				short l0 = (short)rBuf[3];
				l1 <<= 8;
				len = (short)(l1|l0);
				rtvl = is.read(rBuf, 4, len);
				if(rtvl < 0){
					err = true;
				}
				System.out.println("len=" + len);
				strIn = new String(rBuf, 4, len);
				System.out.println("content: " + strIn);
			}
			System.out.println(" closed.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
