/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Untar;
import org.apache.tools.ant.taskdefs.Untar.UntarCompressionMethod;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

/**
 * Tar utils.
 * 
 * @author sgibly@appcelerator.com
 */
public class TarUtil
{
	/**
	 * Extract a tar.gz file.
	 * 
	 * @param source
	 * @param destination
	 */
	public static IStatus extractTGZFile(IPath source, IPath destination)
	{
		File destinationFile = destination.toFile();
		File sourceFile = source.toFile();

		if (!Platform.OS_WIN32.equals(Platform.getOS()))
		{
			try
			{
				Process process = Runtime.getRuntime().exec(
						new String[] { "tar", "-C", destinationFile.getAbsolutePath(), "-xzf", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								sourceFile.getAbsolutePath() });
				process.waitFor();
			}
			catch (IOException e)
			{
				IdeLog.logWarning(CorePlugin.getDefault(), "Error unpacking .tgz - " + source.toOSString(), e); //$NON-NLS-1$
				return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Error unpacking .tgz - " + source.toOSString(), //$NON-NLS-1$
						e);
			}
			catch (InterruptedException e)
			{
				IdeLog.logWarning(CorePlugin.getDefault(), e);
				return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Error unpacking .tgz. Process interrupted."); //$NON-NLS-1$
			}
		}
		else
		{
			Untar file = new Untar();
			UntarCompressionMethod method = new UntarCompressionMethod();
			method.setValue("gzip"); //$NON-NLS-1$
			file.setCompression(method);
			file.setDest(destinationFile);
			file.setSrc(sourceFile);

			try
			{
				file.execute();
			}
			catch (BuildException e)
			{
				IdeLog.logError(CorePlugin.getDefault(), "Error unpacking .tgz - " + source.toOSString(), e); //$NON-NLS-1$
				return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Error unpacking .tgz - " + source.toOSString(), //$NON-NLS-1$
						e);
			}
		}
		return Status.OK_STATUS;
	}

}
