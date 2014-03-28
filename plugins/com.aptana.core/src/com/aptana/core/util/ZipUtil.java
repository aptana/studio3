/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IStatusHandler;

import com.aptana.core.CorePlugin;
import com.aptana.core.IDebugScopes;
import com.aptana.core.logging.IdeLog;

/**
 * @author Max Stepanov
 */
public final class ZipUtil
{
	/**
	 * What to do in case we run into a conflict in extracting a file to a destination (a file already exists).
	 * 
	 * @author cwilliams
	 */
	public enum Conflict
	{
		OVERWRITE, PROMPT, SKIP;
	}

	private static final int ATTR_SYMLINK = 0xA000;

	/**
	 * Special error code for conflicts in zip extraction!
	 */
	private static final int ERR_CONFLICTS = 128;

	/**
	 * Destination directory doesn't have write permissions.
	 */
	private static final int ERR_NoWritePermission = 130;

	/**
	 * 
	 */
	private ZipUtil()
	{
	}

	/**
	 * Extract zip file into specified local path. By default, file that exist in the destination path will not be
	 * overwritten.
	 * 
	 * @param zipFile
	 * @param destinationPath
	 * @param monitor
	 * @throws IOException
	 */
	public static IStatus extract(File zipFile, File destinationPath, IProgressMonitor monitor) throws IOException
	{
		if (canDoNativeUnzip(Conflict.SKIP, null))
		{
			return nativeUnzip(zipFile, destinationPath, Conflict.SKIP, monitor);
		}
		return extract(new ZipFile(zipFile), destinationPath, monitor);
	}

	/**
	 * Extract zip file into specified local path. File that exist in the destination path will be overwritten if the
	 * <code>overwrite</code> flag is <code>true</code>.
	 * 
	 * @param zipFile
	 * @param destinationPath
	 * @param overwrite
	 * @param monitor
	 * @throws IOException
	 */
	public static IStatus extract(File zipFile, File destinationPath, boolean overwrite, IProgressMonitor monitor)
			throws IOException
	{
		return extract(zipFile, destinationPath, overwrite, true, monitor);
	}

	/**
	 * Extract zip file into specified local path. File that exist in the destination path will be overwritten if the
	 * <code>overwrite</code> flag is <code>true</code>. Native unzip mechanism will be attempted if the
	 * <code>isNative</code> flag is <code>true</code>
	 * 
	 * @param zipFile
	 * @param destinationPath
	 * @param overwrite
	 * @param isNative
	 * @param monitor
	 * @return
	 * @throws IOException
	 */
	public static IStatus extract(File zipFile, File destinationPath, boolean overwrite, boolean isNative,
			IProgressMonitor monitor) throws IOException
	{
		Conflict whatToDo = overwrite ? Conflict.OVERWRITE : Conflict.SKIP;
		if (canDoNativeUnzip(whatToDo, null) && isNative)
		{
			return nativeUnzip(zipFile, destinationPath, whatToDo, monitor);
		}
		return extract(new ZipFile(zipFile), destinationPath, overwrite, monitor);
	}

	/**
	 * @param file
	 * @param location
	 * @param prompt
	 * @param monitor
	 */
	public static IStatus extract(File file, IPath location, Conflict prompt, IProgressMonitor monitor)
			throws IOException
	{
		if (canDoNativeUnzip(prompt, null))
		{
			return nativeUnzip(file, location.toFile(), prompt, monitor);
		}
		return extract(file, location.toFile(), prompt, null, monitor);
	}

	/**
	 * @param zipFile
	 * @param destinationPath
	 * @param overwrite
	 * @param transformer
	 * @param monitor
	 * @return
	 * @throws IOException
	 */
	public static IStatus extract(File zipFile, File destinationPath, Conflict overwrite,
			IInputStreamTransformer transformer, IProgressMonitor monitor) throws IOException
	{
		if (canDoNativeUnzip(overwrite, transformer))
		{
			return nativeUnzip(zipFile, destinationPath, overwrite, monitor);
		}

		ZipFile zip = new ZipFile(zipFile);
		return extract(zip, zip.getEntries(), destinationPath, overwrite, transformer, monitor);
	}

	/**
	 * Are the conditions right that we can cheat and use native unzip? Must not be on Windows, be doing no
	 * transformations on the contents and not be prompting in case of conflicts. Oh, and they have to have "unzip" on
	 * their PATH.
	 * 
	 * @param whatToDo
	 * @param transformer
	 * @return
	 */
	private static boolean canDoNativeUnzip(Conflict whatToDo, IInputStreamTransformer transformer)
	{
		if (transformer != null || PlatformUtil.isWindows() || whatToDo == Conflict.PROMPT)
		{
			return false;
		}
		IPath unzip = ExecutableUtil.find("unzip", false, null); //$NON-NLS-1$
		return unzip.toFile().isFile();
	}

	/**
	 * A speed hack! Native unzip command is way faster since it can handle doing it all at once and preserve
	 * permissions. That way we don't need to run a million chmods.
	 * 
	 * @return
	 */
	private static IStatus nativeUnzip(File zip, File destination, Conflict overwrite, IProgressMonitor monitor)
	{
		String overwriteFlag = "-o"; //$NON-NLS-1$
		if (overwrite == Conflict.SKIP)
		{
			overwriteFlag = "-n"; //$NON-NLS-1$
		}
		// We use this call so we pipe output to the monitor.
		return new ProcessRunner().run(null, null, null,
				CollectionsUtil.newList("unzip", overwriteFlag, zip.getAbsolutePath(), "-d", //$NON-NLS-1$ //$NON-NLS-2$
						destination.getAbsolutePath()), monitor);
	}

	/**
	 * Extract zip file into specified local path. By default, file that exist in the destination path will not be
	 * overwritten.
	 * 
	 * @param zip
	 * @param destinationPath
	 * @param monitor
	 * @throws IOException
	 */
	private static IStatus extract(ZipFile zip, File destinationPath, IProgressMonitor monitor) throws IOException
	{
		return extract(zip, zip.getEntries(), destinationPath, monitor);
	}

	/**
	 * Extract zip file into specified local path. File that exist in the destination path will be overwritten if the
	 * <code>overwrite</code> flag is <code>true</code>.
	 * 
	 * @param zip
	 * @param destinationPath
	 * @param overwrite
	 * @param monitor
	 * @throws IOException
	 */
	private static IStatus extract(ZipFile zip, File destinationPath, boolean overwrite, IProgressMonitor monitor)
			throws IOException
	{
		return extract(zip, zip.getEntries(), destinationPath, overwrite ? Conflict.OVERWRITE : Conflict.SKIP, null,
				monitor);
	}

	/**
	 * Open input stream for specified zip entry.
	 * 
	 * @param zipFile
	 * @param path
	 * @return
	 * @throws IOException
	 */
	static InputStream openEntry(File zipFile, IPath path) throws IOException
	{
		ZipFile zip = new ZipFile(zipFile);
		ZipEntry entry = zip.getEntry(path.makeRelative().toPortableString());
		if (entry != null)
		{
			return zip.getInputStream(entry);
		}
		return null;
	}

	/**
	 * Extract specified list of entries from zip file to local path. By default, file that exist in the destination
	 * path will not be overwritten.
	 * 
	 * @param zip
	 * @param entries
	 * @param destinationPath
	 * @param monitor
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private static IStatus extract(ZipFile zip, Enumeration entries, File destinationPath, IProgressMonitor monitor)
			throws IOException
	{
		return extract(zip, entries, destinationPath, Conflict.SKIP, null, monitor);
	}

	/**
	 * Extract specified list of entries from zip file to local path. File that exist in the destination path will be
	 * overwritten if the <code>overwrite</code> flag is <code>true</code>. Only updates the monitor at given intervals
	 * 
	 * @param zip
	 * @param entries
	 * @param destinationPath
	 * @param overwrite
	 *            - Indicate if existing folders and files should be overwritten during the extraction.
	 * @param monitor
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static IStatus extract(ZipFile zip, Enumeration entries, File destinationPath, Conflict overwrite,
			IInputStreamTransformer transformer, IProgressMonitor monitor) throws IOException
	{
		Collection collection = Collections.list(entries);
		MultiStatus multiStatus = new MultiStatus(CorePlugin.PLUGIN_ID, 0, null, null);
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.ZipUtil_default_extract_label, collection.size());
		try
		{
			// Create directories first
			for (Object i : collection)
			{
				ZipEntry entry = (ZipEntry) i;
				createDirectory(entry, destinationPath);
				if (subMonitor.isCanceled())
				{
					return Status.CANCEL_STATUS;
				}
			}

			// Extract files
			Set<IPath> conflicts = new HashSet<IPath>();
			for (Object i : collection)
			{
				ZipEntry entry = (ZipEntry) i;
				IStatus fileStatus = extractEntry(zip, entry, destinationPath, transformer, overwrite,
						subMonitor.newChild(1));
				// We need to add entries to a conflict list if we can't overwrite them!
				if (fileStatus.getCode() == ERR_CONFLICTS)
				{
					conflicts.add(Path.fromPortableString(entry.getName()));
				}
				else
				{
					multiStatus.merge(fileStatus);
				}
				if (subMonitor.isCanceled())
				{
					return Status.CANCEL_STATUS;
				}
			}

			// Now handle the conflicts, prompt to see if user wants to overwrite
			if (overwrite == Conflict.PROMPT && !conflicts.isEmpty())
			{
				IStatus status = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, ERR_CONFLICTS,
						Messages.ZipUtil_ConflictsError, null);
				IStatusHandler handler = DebugPlugin.getDefault().getStatusHandler(status);
				if (handler != null)
				{
					Object result = handler.handleStatus(status, conflicts);
					if (result instanceof IPath[])
					{
						// extract the entries!
						IPath[] toOverwrite = (IPath[]) result;
						for (IPath file : toOverwrite)
						{
							ZipEntry entry = zip.getEntry(file.toPortableString());
							multiStatus.merge(extractEntry(zip, entry, destinationPath, transformer,
									Conflict.OVERWRITE, subMonitor));
							if (subMonitor.isCanceled())
							{
								return Status.CANCEL_STATUS;
							}
						}
					}
				}
			}

			return multiStatus;
		}
		catch (CoreException ce)
		{
			return ce.getStatus();
		}
		finally
		{
			subMonitor.done();
			ZipFile.closeQuietly(zip);
		}
	}

	/**
	 * Attempts to ensure the destination directory structure is generated. If there's a problem with write permissions,
	 * a {@link CoreException} is thrown.
	 * 
	 * @param entry
	 * @param destinationPath
	 * @throws CoreException
	 */
	private static void createDirectory(ZipEntry entry, File destinationPath) throws CoreException
	{
		String name = entry.getName();
		File file = new File(destinationPath, name);
		if (entry.isDirectory())
		{
			createDirectoryIfNecessary(file);
		}
		else if (name.indexOf('/') != -1)
		{
			createDirectoryIfNecessary(file.getParentFile());
		}
	}

	/**
	 * If the directory doesn't exist, we attempt to create it. If the directory is not writable, we throw a
	 * {@link CoreException}
	 * 
	 * @param file
	 * @throws CoreException
	 */
	private static void createDirectoryIfNecessary(File file) throws CoreException
	{
		if (!file.exists())
		{
			if (IdeLog.isInfoEnabled(CorePlugin.getDefault(), IDebugScopes.ZIPUTIL))
			{
				IdeLog.logInfo(CorePlugin.getDefault(),
						MessageFormat.format("Creating directory {0}", file.getAbsolutePath()), IDebugScopes.ZIPUTIL); //$NON-NLS-1$
			}
			file.mkdirs(); // FIXME Should we throw a CoreException here?
		}
		if (!IOUtil.isWritableDirectory(file))
		{
			throw new CoreException(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, ERR_NoWritePermission,
					MessageFormat.format(Messages.ZipUtil_ERR_NoWritePermission, file), null));
		}
	}

	/**
	 * Do the dirty work of actually extracting a {@link ZipEntry} to it's destination.
	 * 
	 * @param zip
	 * @param entry
	 * @param destinationPath
	 * @param transformer
	 * @param conflicts
	 * @param howToResolve
	 * @param monitor
	 * @return
	 */
	private static IStatus extractEntry(ZipFile zip, ZipEntry entry, File destinationPath,
			IInputStreamTransformer transformer, Conflict howToResolve, IProgressMonitor monitor)
	{
		// Return early since this is only supposed to handle files.
		if (entry.isDirectory())
		{
			return Status.OK_STATUS;
		}

		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
		String name = entry.getName();
		File file = new File(destinationPath, name);
		if (IdeLog.isInfoEnabled(CorePlugin.getDefault(), IDebugScopes.ZIPUTIL))
		{
			IdeLog.logInfo(CorePlugin.getDefault(),
					MessageFormat.format("Extracting {0} as {1}", name, file.getAbsolutePath()), IDebugScopes.ZIPUTIL); //$NON-NLS-1$
		}
		subMonitor.setTaskName(Messages.ZipUtil_extract_prefix_label + name);
		subMonitor.worked(2);
		try
		{
			if (file.exists())
			{
				switch (howToResolve)
				{
					case OVERWRITE:
						if (IdeLog.isInfoEnabled(CorePlugin.getDefault(), IDebugScopes.ZIPUTIL))
						{
							IdeLog.logInfo(
									CorePlugin.getDefault(),
									MessageFormat.format(
											"Deleting a file/directory before overwrite {0}", file.getAbsolutePath()), IDebugScopes.ZIPUTIL); //$NON-NLS-1$
						}
						FileUtil.deleteRecursively(file);
						break;

					case SKIP:
						return Status.OK_STATUS;

					case PROMPT:
						return new Status(IStatus.INFO, CorePlugin.PLUGIN_ID, ERR_CONFLICTS, name, null);
				}
			}
			subMonitor.setWorkRemaining(95);

			extractFile(zip, entry, destinationPath, file, transformer, subMonitor.newChild(95));
		}

		finally
		{
			subMonitor.done();
		}

		return Status.OK_STATUS;
	}

	/**
	 * Extracts the {@link ZipEntry} to disk.
	 * 
	 * @param zip
	 * @param entry
	 * @param destinationPath
	 * @param file
	 * @param transformer
	 * @param monitor
	 * @return
	 */
	private static IStatus extractFile(ZipFile zip, ZipEntry entry, File destinationPath, File file,
			IInputStreamTransformer transformer, IProgressMonitor monitor)
	{
		if (isSymlink(entry))
		{
			return extractSymlink(zip, entry, destinationPath, file);
		}

		// handle non-symlinks
		OutputStream out = null;
		InputStream in = null;
		try
		{
			file.getParentFile().mkdirs();
			// Run an IInputStreamTransformer on the input here if it's not a symlink!
			in = zip.getInputStream(entry);
			if (transformer != null)
			{
				in = transformer.transform(in, Path.fromPortableString(entry.getName()));
			}
			out = new FileOutputStream(file);
			// TODO Can we pass in an IProgressMonitor to get progress here?
			IOUtil.pipe(in, out);
		}
		catch (ZipException e)
		{
			return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, MessageFormat.format(
					"Error getting input stream for zip entry {0}", entry.getName()), e); //$NON-NLS-1$
		}
		catch (IOException e)
		{
			// TODO
			return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, MessageFormat.format(
					"IOException while extracting file {0}", file.getAbsolutePath()), e); //$NON-NLS-1$
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException ignore)
				{
				}
			}
			if (out != null)
			{
				try
				{
					out.close();
				}
				catch (IOException ignore)
				{
				}
			}
		}

		// Set permissions
		int unixMode = entry.getUnixMode();
		if (!PlatformUtil.isWindows() && unixMode != 0)
		{
			String permString = Integer.toOctalString(unixMode & 0x0FFF);
			return FileUtil.chmod(permString, file);
		}
		return Status.OK_STATUS;
	}

	/**
	 * On non-Windows OSes, we generate a symlink using "ln -s". On Windows, does nothing.
	 * 
	 * @param zip
	 * @param entry
	 * @param destinationPath
	 * @param file
	 * @return
	 */
	private static IStatus extractSymlink(ZipFile zip, ZipEntry entry, File destinationPath, File file)
	{
		if (!PlatformUtil.isWindows())
		{
			try
			{
				file.getParentFile().mkdirs();
				String target = IOUtil.read(zip.getInputStream(entry), IOUtil.UTF_8);
				return new ProcessRunner().runInBackground(
						"ln", "-s", new File(destinationPath, target).getAbsolutePath(), file.getAbsolutePath()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			catch (ZipException e)
			{
				return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, MessageFormat.format(
						"Error getting input stream for zip entry {0}", entry.getName()), e); //$NON-NLS-1$
			}
			catch (IOException e)
			{
				return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, MessageFormat.format(
						"IOException while extracting file {0}", file.getAbsolutePath()), e); //$NON-NLS-1$
			}
		}
		return Status.OK_STATUS;
	}

	/**
	 * Creates a zipped file that contains the source files
	 * 
	 * @param destination
	 * @param sourceFiles
	 * @return
	 */
	public static boolean compress(String destination, String[] sourceFiles)
	{
		return compress(destination, sourceFiles, null);
	}

	/**
	 * Creates a zipped file that contains the source files
	 * 
	 * @param destination
	 * @param sourceFiles
	 * @return
	 */
	public static boolean compress(String destination, String[] sourceFiles, String sourcePathRoot)
	{
		if (!StringUtil.isEmpty(destination))
		{
			try
			{
				ZipOutputStream output = new ZipOutputStream(new FileOutputStream(destination));

				addToZip(sourceFiles, sourcePathRoot, output);

				output.close();
				return true;
			}
			catch (IOException e)
			{
				IdeLog.logError(CorePlugin.getDefault(), MessageFormat.format("Error creating zip {0}", destination), e); //$NON-NLS-1$
			}
		}

		return false;
	}

	private static void addToZip(String[] sourceFiles, String sourcePathRoot, ZipOutputStream output)
			throws FileNotFoundException, IOException
	{
		byte[] buffer = new byte[1024];
		for (String file : sourceFiles)
		{
			File content = new File(file);
			if (content.isDirectory())
			{
				File[] children = content.listFiles();
				String[] childrenPaths = new String[children.length];
				for (int i = 0; i < children.length; i++)
				{
					childrenPaths[i] = children[i].getAbsolutePath();
				}

				addToZip(childrenPaths, sourcePathRoot, output);
			}
			else if (content.canRead())
			{
				FileInputStream input = new FileInputStream(file);
				String name;
				if (sourcePathRoot == null)
				{
					name = file;
				}
				else
				{
					name = Path.fromOSString(file).makeRelativeTo(Path.fromOSString(sourcePathRoot)).toOSString();
				}
				output.putNextEntry(new java.util.zip.ZipEntry(name));

				int length;
				while ((length = input.read(buffer)) > 0)
				{
					output.write(buffer, 0, length);
				}

				output.closeEntry();
				input.close();
			}
		}
	}

	private static boolean isSymlink(ZipEntry entry)
	{
		return (entry.getUnixMode() & ATTR_SYMLINK) == ATTR_SYMLINK;
	}

	/**
	 * Transforms the {@link InputStream} from the raw version we get from a {@link ZipEntry} to the ultimate contents
	 * we write to the file.
	 * 
	 * @author cwilliams
	 */
	public static interface IInputStreamTransformer
	{
		public InputStream transform(InputStream in, IPath relativePath);
	}
}
