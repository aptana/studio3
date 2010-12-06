/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
		URL url = FileLocator.find(Activator.getDefault().getBundle(), Path.fromPortableString(path), null);
		if (url != null) {
			try {
				url = FileLocator.toFileURL(url);
				File file = new File(url.getPath());
				if (file.exists()) {
					return Path.fromOSString(file.getAbsolutePath());
				}
			} catch (IOException e) {
				Activator.log(e);
			}
		}
		return null;
	}
	
	public static boolean extract(IPath destination) {
		IPath zipExecutable = getBundlePath(ZIP_EXECUTABLE);
		IPath archivePath = getBundlePath(ARCHIVE_PATH);
		if (zipExecutable == null || archivePath == null) {
			Activator.log("Something is missing here."); //$NON-NLS-1$
			return false;
		}
		File destinationFile = destination.toFile();
		if (!destinationFile.exists() && !destinationFile.mkdirs()) {
			Activator.log("Failed to create destination directory "+destinationFile.getAbsolutePath()); //$NON-NLS-1$
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
			Activator.log(e);
			return false;
		} catch (InterruptedException e) {
			return false;
		} finally {
			Activator.log(output.toString());
		}
	}

}
