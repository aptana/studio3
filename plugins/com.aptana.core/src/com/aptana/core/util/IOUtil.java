/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
import org.eclipse.core.runtime.Platform;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

public abstract class IOUtil
{

	private static final String UTF_8 = "UTF-8"; //$NON-NLS-1$
	private static final int BUFFER_SIZE = 4096;

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
		{
			return null;
		}

		BufferedReader reader = null;
		try
		{
			if (charset == null)
			{
				if (stream.markSupported())
				{
					// Try to detect the charset!
					CharsetDetector detector = new CharsetDetector();
					CharsetMatch match = detector.setText(stream).detect();
					charset = match.getName();
					reader = new BufferedReader(match.getReader());
				}
				else
				{
					// Now what? Assume UTF-8?
					charset = UTF_8;
					reader = new BufferedReader(new InputStreamReader(stream, charset));
				}
			}
			else
			{
				reader = new BufferedReader(new InputStreamReader(stream, charset));
			}
			StringBuilder output = new StringBuilder();

			// Some editors emit a BOM (EF BB BF) for UTF-8 encodings which the JVM converts to \uFEFF. For lots of
			// whining and an explanation of why this won't be fixed see
			// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4508058. Below, we try to read the first character for
			// utf-8 encodings only. If it matches \uFEFF, then we skip that character; otherwise, we emit it into our
			// output buffer
			if (charset.toUpperCase().equals(UTF_8))
			{
				char[] bomBuffer = new char[1];

				if (reader.read(bomBuffer) != -1 && bomBuffer[0] != '\ufeff')
				{
					output.append(bomBuffer, 0, bomBuffer.length);
				}
			}

			// emit the rest of the stream into the output buffer
			char[] buffer = new char[BUFFER_SIZE];
			int read = 0;

			while ((read = reader.read(buffer)) != -1)
			{
				output.append(buffer, 0, read);
			}

			return output.toString();
		}
		catch (IOException e)
		{
			log(e);
		}
		finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
				else if (stream != null)
				{
					stream.close();
				}
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
		{
			return;
		}
		IdeLog.logError(CorePlugin.getDefault(), e);
	}

	/**
	 * Recursively copy one directory to a new destination directory. If a file is passed in instead of a directory,
	 * this method will delegate to copyFile to perform the copy. Various tests for existence, readability, and
	 * writability are performed before copying. If any of these tests fail, the copy be aborted. Note that this means
	 * that if a failure occurs somewhere in a descendant file/directory, all files up to that point will exist, but no
	 * files after that point will be copied.
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

				IdeLog.logError(CorePlugin.getDefault(), message);
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

		if (url == null)
		{
			return;
		}

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
		write(stream, rawSource, null);
	}

	public static void write(OutputStream stream, String rawSource, String charset)
	{
		if (stream == null)
		{
			return;
		}

		if (rawSource == null)
		{
			rawSource = StringUtil.EMPTY;
		}
		if (charset == null)
		{
			charset = UTF_8;
		}

		Writer writer = null;
		try
		{
			writer = new OutputStreamWriter(stream, charset);
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

	/**
	 * Pipes from input stream to output stream. Uses a byte buffer of size 1024. Does no flushing or closing of
	 * streams!
	 * 
	 * @param inputStream
	 * @param outputStream
	 * @throws IOException
	 */
	public static void pipe(InputStream input, OutputStream output) throws IOException
	{
		byte[] buffer = new byte[1024];
		for (int bytes = input.read(buffer); bytes >= 0; bytes = input.read(buffer))
		{
			output.write(buffer, 0, bytes);
		}
	}
}
