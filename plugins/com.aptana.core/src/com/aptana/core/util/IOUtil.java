package com.aptana.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

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
	
	// If targetLocation does not exist, it will be created.
	public static void copyDirectory(File sourceLocation, File targetLocation) throws IOException
	{

		if (sourceLocation.isDirectory())
		{
			if (!targetLocation.exists())
			{
				targetLocation.mkdir();
			}
			for (String child : sourceLocation.list())
			{
				copyDirectory(new File(sourceLocation, child), new File(targetLocation, child));
			}
		}
		else
		{
			copyFile(sourceLocation, targetLocation);
		}
	}

	public static void copyFile(File sourceLocation, File targetLocation) throws IOException
	{
		InputStream in = null;
		OutputStream out = null;
		try
		{
			in = new FileInputStream(sourceLocation);
			out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}
		}
		finally
		{
			try
			{
				if (in != null)
					in.close();
			}
			catch (Exception e)
			{
				// ignore
			}
			try
			{
				if (out != null)
					out.close();
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
	public static void extractFile(String bundleId, IPath path, File file) throws IOException {
		URL url = FileLocator.find(Platform.getBundle(bundleId), path, null);
		InputStream in = null;
		FileOutputStream out = null;
		try {
			in = url.openStream();
			out = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int n;
			while ((n = in.read(buffer)) > 0) {
				out.write(buffer, 0, n);
			}
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
