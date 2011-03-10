/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Max Stepanov
 *
 */
public final class ZipUtil {

	/**
	 * 
	 */
	private ZipUtil() {
	}

	/**
	 * Extract zip file into specified local path
	 * @param zip
	 * @param path
	 * @throws IOException
	 */
	public static void extract(ZipFile zip, File path) throws IOException {
		extract(zip, zip.entries(), path);
	}
	
	/**
	 * Extract specified list of entries from zip file to local path
	 * @param zip
	 * @param entries
	 * @param path
	 * @throws IOException
	 */
	public static void extract(ZipFile zip, Enumeration<? extends ZipEntry> entries, File path) throws IOException {
		Collection<? extends ZipEntry> collection = Collections.list(entries);
		/* Create directories first */
		for (ZipEntry entry : collection) {
			String name = entry.getName();
			File file = new File(path, name);
			if (entry.isDirectory() && !file.exists()) {
				file.mkdirs();
			} else if (name.indexOf('/') != -1) {
				File parent = file.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}
			}
		}
		byte[] buffer = new byte[0x1000];
		int n;
		/* Extract files */
		for (ZipEntry entry : collection) {
			String name = entry.getName();
			File file = new File(path, name);
			if (!entry.isDirectory() && !file.exists()) {
				if (!file.createNewFile()) {
					continue;
				}
				OutputStream out = new FileOutputStream(file);
				InputStream in = zip.getInputStream(entry);
				while ((n = in.read(buffer)) > 0) {
					out.write(buffer, 0, n);
				}
				in.close();
				out.close();
			}
		}
		
	}

}
