package com.mango.tcpclient;

import java.io.IOException;
import java.io.OutputStream;

public class Works implements Runnable{

	OutputStream os;		
	
	public Works(OutputStream os) {
		super();
		this.os = os;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		byte[] heartB = new byte[2];
		heartB[0] = 0x01;
		heartB[1] = 0x00;
		while(true){
			try {
				os.write(heartB, 0, 2);
				os.flush();
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
}