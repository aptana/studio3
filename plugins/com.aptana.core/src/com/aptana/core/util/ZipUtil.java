/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
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

import com.aptana.core.CorePlugin;
import com.aptana.core.IDebugScopes;
import com.aptana.core.logging.IdeLog;

/**
 * @author Max Stepanov
 */
public final class ZipUtil {

	private static final int ATTR_SYMLINK = 0xA000;

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
					IdeLog.logInfo(CorePlugin.getDefault(), MessageFormat.format("Creating directory {0}", file.getAbsolutePath()), IDebugScopes.ZIPUTIL);
					file.mkdirs();
				} else if (name.indexOf('/') != -1) {
					File parent = file.getParentFile();
					if (!parent.exists()) {
						IdeLog.logInfo(CorePlugin.getDefault(), MessageFormat.format("Creating directory {0}", parent.getAbsolutePath()), IDebugScopes.ZIPUTIL);
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
				IdeLog.logInfo(CorePlugin.getDefault(), MessageFormat.format("Extracting {0} as {1}", name, file.getAbsolutePath()), IDebugScopes.ZIPUTIL);
				subMonitor.setTaskName(Messages.ZipUtil_extract_prefix_label + name);
				subMonitor.worked(1);
				if (!entry.isDirectory() && !file.exists()) {
					file.getParentFile().mkdirs();
					if (!file.createNewFile()) {
						continue;
					}
					boolean symlink = isSymlink(entry);
					if (symlink) {
						IdeLog.logInfo(CorePlugin.getDefault(), MessageFormat.format("Deleting symlink {0}", file.getAbsolutePath()), IDebugScopes.ZIPUTIL);
						file.delete();
					}
					OutputStream out = symlink ? new ByteArrayOutputStream() : new FileOutputStream(file);
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
						if (symlink) {
							String target = new String(((ByteArrayOutputStream) out).toByteArray(), "UTF-8");
							Runtime.getRuntime().exec(new String[] { "ln", "-s", new File(destinationPath, target).getAbsolutePath(), file.getAbsolutePath() }); //$NON-NLS-1$ //$NON-NLS-2$
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

	private static boolean isSymlink(ZipEntry entry) {
		return (entry.getUnixMode() & ATTR_SYMLINK) == ATTR_SYMLINK;
	}
}
