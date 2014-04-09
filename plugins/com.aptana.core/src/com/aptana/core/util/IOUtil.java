/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
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
import java.nio.channels.FileChannel;
import java.text.MessageFormat;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

public abstract class IOUtil
{

	public static final String UTF_8 = "UTF-8"; //$NON-NLS-1$
	private static final int BUFFER_SIZE = 8192;

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
	 * Recursively copy one directory to a new destination directory while showing progress. If a file is passed in
	 * instead of a directory, this method will return an error. Various tests for existence, readability, and
	 * writability are performed before copying. If any of these tests fail, the copy be aborted. Note that this means
	 * that if a failure occurs somewhere in a descendant file/directory, all files up to that point will exist, but no
	 * files after that point will be copied.
	 * 
	 * @param source
	 * @param destination
	 * @param monitor
	 * @param cancelable
	 * @throws IOException
	 */
	public static IStatus copyDirectoryWithProgress(File source, File destination, IProgressMonitor monitor,
			boolean cancelable)
	{
		if (source.isDirectory())
		{
			int updateSize = 2;

			// Setup the progress monitor
			if (monitor != null)
			{
				// Batch up operations so we don't constantly update the progress monitor
				int totalFiles = countFiles(source);
				updateSize = determineProgressBatchUpdateCount(totalFiles);
				int totalWork = totalFiles / updateSize;
				if (totalFiles % updateSize > 0)
				{
					totalWork++;
				}
				monitor = SubMonitor.convert(monitor, totalWork);
			}

			try
			{
				return copyDirectory(source, destination, monitor, new int[] { 0 }, updateSize, cancelable);
			}
			catch (IOException e)
			{
				return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR,
						Messages.IOUtil_Directory_Copy_Error, e);
			}
		}

		return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR, Messages.IOUtil_Source_Not_Directory_Error, null);
	}

	private static int countFiles(File source)
	{
		String[] list = source.list();
		int count = list.length;
		for (String file : list)
		{
			File child = new File(source, file);
			if (child.isDirectory())
			{
				count += countFiles(child);
			}
		}

		return count;
	}

	/**
	 * Recursively copy one directory to a new destination directory while showing progress. If a file is passed in
	 * instead of a directory, this method will delegate to copyFile to perform the copy. Various tests for existence,
	 * readability, and writability are performed before copying. If any of these tests fail, the copy be aborted. Note
	 * that this means that if a failure occurs somewhere in a descendant file/directory, all files up to that point
	 * will exist, but no files after that point will be copied.
	 * 
	 * @param source
	 * @param destination
	 * @param monitor
	 * @param count
	 * @param updateSize
	 * @param cancelable
	 * @throws IOException
	 */
	private static IStatus copyDirectory(File source, File destination, IProgressMonitor monitor, int[] count,
			int updateSize, boolean cancelable) throws IOException
	{
		if (monitor != null)
		{
			if (cancelable && monitor.isCanceled())
			{
				return new Status(IStatus.CANCEL, CorePlugin.PLUGIN_ID, IStatus.CANCEL, StringUtil.EMPTY, null);
			}

			count[0]++;
			if (updateSize < 2 || count[0] % updateSize == 0)
			{
				monitor.setTaskName(MessageFormat.format(Messages.IOUtil_Copy_Label, destination.toString()));
				monitor.worked(1);
			}
		}

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
					IStatus status = copyDirectory(new File(source, filename), new File(destination, filename),
							monitor, count, updateSize, cancelable);
					if (status != null && !status.isOK())
					{
						return status;
					}
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

				return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR, message, null);
			}
		}
		else
		{
			IFileStore src = EFS.getLocalFileSystem().fromLocalFile(source);
			try
			{
				src.copy(EFS.getLocalFileSystem().fromLocalFile(destination), EFS.OVERWRITE, new NullProgressMonitor());
			}
			catch (CoreException e)
			{
				return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e);
			}
		}

		return Status.OK_STATUS;
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
	public static IStatus copyDirectory(File source, File destination) throws IOException
	{
		return copyDirectory(source, destination, null, new int[] { 0 }, 0, false);
	}

	/**
	 * Copy the contents of one file to another. Uses Attempts to use channels for files < 20Mb, uses streams for larger
	 * files. Closes the streams after transfer.
	 * 
	 * @param source
	 * @param destination
	 * @throws IOException
	 */
	public static void copyFile(File source, File destination) throws IOException
	{
		long fileSize = source.length();
		FileInputStream in = new FileInputStream(source);
		FileOutputStream out = new FileOutputStream(destination);
		// for larger files (20Mb) use streams
		if (fileSize > 20971520l)
		{
			try
			{
				pipe(in, out);
			}
			finally
			{
				try
				{
					if (in != null)
					{
						in.close();
					}
				}
				catch (Exception e)
				{
					// ignore
				}

				try
				{
					if (out != null)
					{
						out.close();
					}
				}
				catch (Exception e)
				{
					// ignore
				}
			}
		}
		// smaller files, use channels
		else
		{
			copy(in, out);
		}
	}

	/**
	 * Special optimized version of copying a {@link FileInputStream} to a {@link FileOutputStream}. Uses
	 * {@link FileChannel#transferTo(long, long, java.nio.channels.WritableByteChannel)}. Closes the streams after
	 * copying.
	 * 
	 * @param iStream
	 * @param oStream
	 * @throws IOException
	 */
	private static void copy(FileInputStream iStream, FileOutputStream oStream) throws IOException
	{
		try
		{
			FileChannel inChannel = iStream.getChannel();
			FileChannel outChannel = oStream.getChannel();
			long fileSize = inChannel.size();
			long offs = 0, doneCnt = 0, copyCnt = Math.min(65536, fileSize);
			do
			{
				doneCnt = inChannel.transferTo(offs, copyCnt, outChannel);
				offs += doneCnt;
				fileSize -= doneCnt;
			}
			while (fileSize > 0);
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
	static void extractFile(String bundleId, IPath path, File file) throws IOException
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
			pipe(in, out);
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
	 * Used for IProgressMonitor updates. Determines how many updates to batch up before actually notifying the
	 * IProgressMonitor.
	 * 
	 * @param numberOfUpdates
	 * @return
	 */
	private static int determineProgressBatchUpdateCount(int numberOfUpdates)
	{
		int updateSize = 2;

		if (numberOfUpdates > 1000)
		{
			updateSize = 25;
		}
		else if (numberOfUpdates > 100)
		{
			updateSize = 5;
		}

		return updateSize;
	}

	/**
	 * Pipes from input stream to output stream. Uses a byte buffer of size 8192. Does no flushing or closing of
	 * streams!
	 * 
	 * @param inputStream
	 * @param outputStream
	 * @throws IOException
	 */
	public static void pipe(InputStream input, OutputStream output) throws IOException
	{
		byte[] buffer = new byte[BUFFER_SIZE];
		for (int bytes = input.read(buffer); bytes >= 0; bytes = input.read(buffer))
		{
			output.write(buffer, 0, bytes);
		}
	}

	/**
	 * Returns true if there are write permissions on the given directory.
	 * 
	 * @param dir
	 * @return <code>true</code> if we can write to the directory; <code>false</code> otherwise.
	 */
	public static boolean isWritableDirectory(File dir)
	{
		if (dir.isDirectory() && dir.exists())
		{
			File tempFile = null;
			try
			{
				tempFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".tmp", dir); //$NON-NLS-1$
				boolean canWrite = tempFile != null && tempFile.exists();

				return canWrite;
			}
			catch (IOException e)
			{
				IdeLog.logWarning(CorePlugin.getDefault(),
						"Failed to create a temporary file to check if the directory is writable.", e); //$NON-NLS-1$
			}
			finally
			{
				if (tempFile != null)
				{
					tempFile.delete();
				}
			}
		}
		return false;
	}

	/**
	 * Returns true if the path is writable
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isWritablePath(IPath path)
	{
		if (path.toFile().isFile())
		{
			return false;
		}

		for (int i = 1; i <= path.segmentCount(); i++)
		{
			if (IOUtil.isWritableDirectory(path.removeLastSegments(i).toFile()))
			{
				return true;
			}
		}

		return false;
	}
}
