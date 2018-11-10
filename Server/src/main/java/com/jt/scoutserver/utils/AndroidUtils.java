package com.jt.scoutserver.utils;

import java.io.IOException;
import java.util.List;

import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.RemoteFile;

public class AndroidUtils {

	public static boolean hasFile(JadbDevice device, RemoteFile remoteFile) {
		try {
			List<RemoteFile> files = device.list(getParentFile(remoteFile));
			if (files.contains(remoteFile))
				return true;
			else
				return false;

		} catch (IOException | JadbException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getParentFile(RemoteFile remoteFile) {
		return remoteFile.getPath().substring(0, remoteFile.getPath().indexOf("//"));

	}

}
