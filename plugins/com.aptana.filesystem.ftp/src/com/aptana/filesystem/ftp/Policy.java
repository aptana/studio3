/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable exceptionUsage.exceptionCreation

package com.aptana.filesystem.ftp;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;

import com.aptana.core.io.vfs.IExtendedFileInfo;
import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;

/**
 * @author Max Stepanov
 */
public final class Policy {

	/**
	 * 
	 */
	private Policy() {
	}

	public static String generateAuthId(String proto, IBaseRemoteConnectionPoint connectionPoint) {
		return generateAuthId(proto, connectionPoint.getLogin(), connectionPoint.getHost(), connectionPoint.getPort());
	}

	public static String generateAuthId(String proto, String login, String host, int port) {
		if (host != null && host.length() > 0 && port > 0 && login != null && login.length() > 0) {
			return MessageFormat.format("{0}/{1}@{2}:{3}", new Object[] { //$NON-NLS-1$
					proto, login, host, Integer.toString(port) });
		}
		return null;
	}

	public static long permissionsFromString(String string) {
		long permissions = 0;
		if (string != null && string.length() >= 10) {
			int index = 1;
			permissions |= (string.charAt(index++) == 'r') ? IExtendedFileInfo.PERMISSION_OWNER_READ : 0;
			permissions |= (string.charAt(index++) == 'w') ? IExtendedFileInfo.PERMISSION_OWNER_WRITE : 0;
			permissions |= (string.charAt(index++) == 'x') ? IExtendedFileInfo.PERMISSION_OWNER_EXECUTE : 0;

			permissions |= (string.charAt(index++) == 'r') ? IExtendedFileInfo.PERMISSION_GROUP_READ : 0;
			permissions |= (string.charAt(index++) == 'w') ? IExtendedFileInfo.PERMISSION_GROUP_WRITE : 0;
			permissions |= (string.charAt(index++) == 'x') ? IExtendedFileInfo.PERMISSION_GROUP_EXECUTE : 0;

			permissions |= (string.charAt(index++) == 'r') ? IExtendedFileInfo.PERMISSION_OTHERS_READ : 0;
			permissions |= (string.charAt(index++) == 'w') ? IExtendedFileInfo.PERMISSION_OTHERS_WRITE : 0;
			permissions |= (string.charAt(index++) == 'x') ? IExtendedFileInfo.PERMISSION_OTHERS_EXECUTE : 0;
		}
		return permissions;
	}

	public static IProgressMonitor monitorFor(IProgressMonitor monitor) {
		return (monitor == null) ? new NullProgressMonitor() : monitor;
	}

	public static IProgressMonitor subMonitorFor(IProgressMonitor monitor, int ticks) {
		if (monitor == null) {
			return new NullProgressMonitor();
		}
		if (monitor instanceof NullProgressMonitor) {
			return monitor;
		}
		return new SubProgressMonitor(monitor, ticks);
	}

	public static void checkCanceled(IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
	}

}
