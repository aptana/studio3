/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portablegit.win32;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Max Stepanov
 *
 */
/* package */ class Extractor {

	private static final String ZIP_EXECUTABLE = "$os$/7za.exe"; //$NON-NLS-1$
	private static final String ARCHIVE_PATH = "$os$/PortableGit-1.7.3.1-preview20101002.7z"; //$NON-NLS-1$

	/**
	 * 
	 */
	private Extractor() {
	}
	
	private static IPath getBundlePath(String path) {
		URL url = FileLocator.find(PortableGitPlugin.getDefault().getBundle(), Path.fromPortableString(path), null);
		if (url != null) {
			try {
				url = FileLocator.toFileURL(url);
				File file = new File(url.getPath());
				if (file.exists()) {
					return Path.fromOSString(file.getAbsolutePath());
				}
			} catch (IOException e) {
				PortableGitPlugin.log(e);
			}
		}
		return null;
	}
	
	public static boolean extract(IPath destination) {
		IPath zipExecutable = getBundlePath(ZIP_EXECUTABLE);
		IPath archivePath = getBundlePath(ARCHIVE_PATH);
		if (zipExecutable == null || archivePath == null) {
			PortableGitPlugin.log("Something is missing here."); //$NON-NLS-1$
			return false;
		}
		File destinationFile = destination.toFile();
		if (!destinationFile.exists() && !destinationFile.mkdirs()) {
			PortableGitPlugin.log("Failed to create destination directory "+destinationFile.getAbsolutePath()); //$NON-NLS-1$
			return false;
		}
		ProcessBuilder processBuilder = new ProcessBuilder(
				zipExecutable.toOSString(),
				"x", //$NON-NLS-1$
				"-o"+destination.lastSegment(), //$NON-NLS-1$
				"-y", //$NON-NLS-1$
				archivePath.toOSString());
		processBuilder.directory(destinationFile.getParentFile());
		processBuilder.redirectErrorStream(true);
		StringBuffer output = new StringBuffer();
		try {
			Process process = processBuilder.start();
			LineNumberReader reader = new LineNumberReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line);
			}
			process.waitFor();
			return process.exitValue() == 0;
		} catch (IOException e) {
			PortableGitPlugin.log(e);
			return false;
		} catch (InterruptedException e) {
			return false;
		} finally {
			PortableGitPlugin.log(output.toString());
		}
	}

}
