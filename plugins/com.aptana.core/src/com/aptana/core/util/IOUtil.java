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
package com.aptana.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

import com.aptana.core.CorePlugin;

public abstract class IOUtil
{

	/**
	 * Reads an InputStream into a String. Safely closes the stream after reading, or if any exceptions occur. Returns
	 * null if the stream is null or an exception occurs reading in the stream.
	 * 
	 * @param stream
	 * @return
	 */
	public static String read(InputStream stream)
	{
		return read(stream, null);
	}

	/**
	 * Newlines will get converted into \n.
	 * 
	 * @param stream
	 * @param charset
	 * @return
	 */
	public static String read(InputStream stream, String charset)
	{
		if (stream == null)
			return null;
		try
		{
			InputStreamReader inReader;
			if (charset != null)
				inReader = new InputStreamReader(stream, charset);
			else
				inReader = new InputStreamReader(stream);
			BufferedReader reader = new BufferedReader(inReader);
			StringBuilder template = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				template.append(line);
				template.append("\n"); //$NON-NLS-1$
			}
			if (template.length() > 0)
				template.deleteCharAt(template.length() - 1); // delete last extraneous newline
			return template.toString();
		}
		catch (IOException e)
		{
			log(e);
		}
		finally
		{
			try
			{
				stream.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
		return null;
	}

	private static void log(Exception e)
	{
		if (CorePlugin.getDefault() == null)
			return;
		CorePlugin.getDefault().getLog().log(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, e.getMessage(), e));
	}

	/**
	 * Recursively copy one directory to a new destination directory. If a file
	 * is passed in instead of a directory, this method will delegate to
	 * copyFile to perform the copy. Various tests for existence, readability,
	 * and writability are performed before copying. If any of these tests fail,
	 * the copy be aborted. Note that this means that if a failure occurs
	 * somewhere in a descendant file/directory, all files up to that point will
	 * exist, but no files after that point will be copied. 
	 * 
	 * @param source
	 * @param destination
	 * @throws IOException
	 */
	public static void copyDirectory(File source, File destination) throws IOException
	{
		if (source.isDirectory())
		{
			String error = null;

			// make sure we can read the source directory and that we have a
			// writable destination directory
			if (source.canRead() == false)
			{
				error = Messages.IOUtil_Source_Directory_Not_Readable;
			}
			else if (destination.exists() == false)
			{
				if (destination.mkdir() == false)
				{
					error = Messages.IOUtil_Destination_Directory_Uncreatable;
				}
			}
			else if (destination.isDirectory() == false)
			{
				error = Messages.IOUtil_Destination_Is_Not_A_Directory;
			}
			else if (destination.canWrite() == false)
			{
				error = Messages.IOUtil_Destination_Directory_Not_Writable;
			}

			if (error == null)
			{
				// copy all files in the source directory
				for (String filename : source.list())
				{
					copyDirectory(new File(source, filename), new File(destination, filename));
				}
			}
			else
			{
				String message = MessageFormat.format( //
					Messages.IOUtil_Unable_To_Copy_Because, //
					source, //
					destination, //
					error //
				);

				CorePlugin.logError(message, null);
			}
		}
		else
		{
			copyFile(source, destination);
		}
	}

	/**
	 * Copy the contents of one file to another. This is a byte-wise copy
	 * 
	 * @param source
	 * @param destination
	 * @throws IOException
	 */
	public static void copyFile(File source, File destination) throws IOException
	{
		InputStream iStream = null;
		OutputStream oStream = null;
		byte[] buffer = new byte[1024];

		try
		{
			iStream = new FileInputStream(source);
			oStream = new FileOutputStream(destination);

			int readCount = iStream.read(buffer);

			while (readCount > 0)
			{
				oStream.write(buffer, 0, readCount);
				readCount = iStream.read(buffer);
			}
		}
		finally
		{
			try
			{
				if (iStream != null)
				{
					iStream.close();
				}
			}
			catch (Exception e)
			{
				// ignore
			}

			try
			{
				if (oStream != null)
				{
					oStream.close();
				}
			}
			catch (Exception e)
			{
				// ignore
			}
		}
	}

	/**
	 * extractFile
	 * 
	 * @param path
	 * @param file
	 * @throws IOException
	 */
	public static void extractFile(String bundleId, IPath path, File file) throws IOException
	{
		URL url = FileLocator.find(Platform.getBundle(bundleId), path, null);
		InputStream in = null;
		FileOutputStream out = null;
		try
		{
			in = url.openStream();
			out = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int n;
			while ((n = in.read(buffer)) > 0)
			{
				out.write(buffer, 0, n);
			}
		}
		finally
		{
			if (out != null)
			{
				try
				{
					out.close();
				}
				catch (IOException e)
				{
				}
			}
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
				}
			}
		}
	}

	public static void write(OutputStream stream, String rawSource)
	{
		if (stream == null)
		{
			return;
		}

		Writer writer = null;
		try
		{
			writer = new OutputStreamWriter(stream);
			writer.write(rawSource);
		}
		catch (IOException e)
		{
			log(e);
		}
		finally
		{
			try
			{
				if (writer != null)
				{
					writer.close();
				}
			}
			catch (IOException e)
			{
				// ignore
			}
		}
	}

}
