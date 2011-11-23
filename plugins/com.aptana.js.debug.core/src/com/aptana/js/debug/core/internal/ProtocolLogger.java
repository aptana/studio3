/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2011 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.js.debug.core.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;

import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.js.debug.core.JSDebugPlugin;

/**
 * @author Max Stepanov
 */
public class ProtocolLogger {

	private PrintWriter writer;

	/**
	 * @throws FileNotFoundException
	 */
	public ProtocolLogger(String filename, String pluginId) throws DebugException {
		this(Platform.getStateLocation(Platform.getBundle(pluginId))
				.append("logs").append(filename).addFileExtension("log"), pluginId); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @throws FileNotFoundException
	 */
	public ProtocolLogger(IPath location, String pluginId) throws DebugException {
		File file = location.toFile();
		file.getParentFile().mkdirs();
		try {
			writer = new PrintWriter(new FileOutputStream(file), true);
			writer.println(MessageFormat.format("Logger started at: {0,date,full} {0,time,full}", new Date())); //$NON-NLS-1$
			writer.println(MessageFormat.format("Component: {0}", pluginId)); //$NON-NLS-1$
			writer.println(MessageFormat.format("Version: {0}", EclipseUtil.getPluginVersion(pluginId))); //$NON-NLS-1$
		} catch (FileNotFoundException e) {
			throw new DebugException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID,
					DebugException.TARGET_REQUEST_FAILED, StringUtil.EMPTY, e));
		}
	}

	public void log(boolean recv, String message) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		if (message.length() > 1024) {
			message = message.substring(0, 1024)+"..."+Integer.toString(message.length()-1024); //$NON-NLS-1$
		}
		writer.println(MessageFormat
				.format("[{0,number,##}:{1,number,##}.{2,number,###}] {3}: >{4}<", cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND), recv ? "Recv" : "Sent", message)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void close() {
		writer.println("----------End of file----------"); //$NON-NLS-1$
		writer.close();
	}

}
