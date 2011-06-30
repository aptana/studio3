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

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

/**
 * @author Max Stepanov
 */
public final class ZipUtil {

	/**
	 * 
	 */
	private ZipUtil() {
	}

	/**
	 * Extract zip file into specified local path
	 * 
	 * @param zipFile
	 * @param destinationPath
	 * @throws IOException
	 */
	public static IStatus extract(File zipFile, File destinationPath, IProgressMonitor monitor) throws IOException {
		return extract(new ZipFile(zipFile), destinationPath, monitor);
	}

	/**
	 * Extract zip file into specified local path
	 * 
	 * @param zip
	 * @param destinationPath
	 * @throws IOException
	 */
	public static IStatus extract(ZipFile zip, File destinationPath, IProgressMonitor monitor) throws IOException {
		return extract(zip, zip.getEntries(), destinationPath, monitor);
	}

	/**
	 * Open iput stream for specified zip entry
	 * 
	 * @param zipFile
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static InputStream openEntry(File zipFile, IPath path) throws IOException {
		ZipFile zip = new ZipFile(zipFile);
		ZipEntry entry = zip.getEntry(path.makeRelative().toPortableString());
		if (entry != null) {
			return zip.getInputStream(entry);
		}
		return null;
	}

	/**
	 * Extract specified list of entries from zip file to local path
	 * 
	 * @param zip
	 * @param entries
	 * @param destinationPath
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static IStatus extract(ZipFile zip, Enumeration entries, File destinationPath, IProgressMonitor monitor) throws IOException {
		Collection collection = Collections.list(entries);

		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.ZipUtil_default_extract_label, collection.size());
		try {
			/* Create directories first */
			for (Object i : collection) {
				ZipEntry entry = (ZipEntry) i;
				String name = entry.getName();
				File file = new File(destinationPath, name);
				if (entry.isDirectory() && !file.exists()) {
					file.mkdirs();
				} else if (name.indexOf('/') != -1) {
					File parent = file.getParentFile();
					if (!parent.exists()) {
						parent.mkdirs();
					}
				}
				if (subMonitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
			}
			byte[] buffer = new byte[0x1000];
			int n;
			/* Extract files */
			for (Object i : collection) {
				ZipEntry entry = (ZipEntry) i;
				String name = entry.getName();
				File file = new File(destinationPath, name);
				subMonitor.setTaskName(Messages.ZipUtil_extract_prefix_label + name);
				subMonitor.worked(1);
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
					if (!Platform.OS_WIN32.equals(Platform.getOS())) {
						try {
							Runtime.getRuntime().exec(new String[] { "chmod", Integer.toOctalString(entry.getUnixMode() & 0x0FFF), file.getAbsolutePath() }); //$NON-NLS-1$
						} catch (Exception ignore) {
						}
					}
				}
				if (subMonitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
			}
			return Status.OK_STATUS;
		} finally {
			subMonitor.done();
		}
	}
}
