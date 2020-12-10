package com.jt.scoutserver;

import javax.swing.UIManager;

import com.jt.scoutserver.utils.SystemUtils;

public class Main {

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
 
		Server server = new Server();
		while (true) {
			try {
				if (SystemUtils.hasNewDevices()) {
					System.out.println("Pulling from new device...");
					server.pull();
					server.flashConsole();
				}
				Thread.sleep(100);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

	}

}
