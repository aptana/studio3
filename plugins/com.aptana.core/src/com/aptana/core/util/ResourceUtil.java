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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.aptana.core.CorePlugin;
import com.aptana.core.resources.IUniformResource;

public class ResourceUtil
{
	private static final String UNC_PREFIX = "//"; //$NON-NLS-1$
	private static final String SCHEME_FILE = "file"; //$NON-NLS-1$

	// Leaving these here at the moment, since I can't think of a better place to put them
	private static final String APTANA_NATURE_PREFIX = "com.aptana."; //$NON-NLS-1$
	private static final String RAILS_NATURE_PREFIX = "org.radrails.rails."; //$NON-NLS-1$
	
	private ResourceUtil()
	{
	}

	/**
	 * resourcePathToFile
	 * 
	 * @param url
	 * @return
	 */
	static public File resourcePathToFile(URL url)
	{
		File result = null;

		if (url != null)
		{
			try
			{
				URL fileURL = FileLocator.toFileURL(url);
				URI fileURI = toURI(fileURL); // Use Eclipse to get around Java 1.5 bug on Windows
				result = new File(fileURI);
			}
			catch (IOException e)
			{
				String message = MessageFormat.format(Messages.ResourceUtils_URL_To_File_URL_Conversion_Error,
						new Object[] { url });

				CorePlugin.logError(message, e);
			}
			catch (URISyntaxException e)
			{
				String message = MessageFormat.format(Messages.ResourceUtils_File_URL_To_URI_Conversion_Error,
						new Object[] { url });

				CorePlugin.logError(message, e);
			}
		}

		return result;
	}

	/**
	 * resourcePathToString
	 * 
	 * @param url
	 * @return
	 */
	static public String resourcePathToString(URL url)
	{
		String result = null;
		File file = resourcePathToFile(url);

		if (file != null)
		{
			result = file.getAbsolutePath();
		}

		return result;
	}

	/**
	 * Returns the URL as a URI. This method will handle URLs that are not properly encoded (for example they contain
	 * unencoded space characters).
	 * 
	 * @param url
	 *            The URL to convert into a URI
	 * @return A URI representing the given URL
	 */
	public static URI toURI(URL url) throws URISyntaxException
	{
		// URL behaves differently across platforms so for file: URLs we parse from string form
		if (SCHEME_FILE.equals(url.getProtocol()))
		{
			String pathString = url.toExternalForm().substring(5);

			// ensure there is a leading slash to handle common malformed URLs such as file:c:/tmp
			if (pathString.indexOf('/') != 0)
			{
				pathString = '/' + pathString;
			}
			else if (pathString.startsWith(UNC_PREFIX) && !pathString.startsWith(UNC_PREFIX, 2))
			{
				// URL encodes UNC path with two slashes, but URI uses four (see bug 207103)
				pathString = ensureUNCPath(pathString);
			}

			return new URI(SCHEME_FILE, null, pathString, null);
		}
		try
		{
			return new URI(url.toExternalForm());
		}
		catch (URISyntaxException e)
		{
			// try multi-argument URI constructor to perform encoding
			return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
					url.getQuery(), url.getRef());
		}
	}

	/**
	 * Ensures the given path string starts with exactly four leading slashes.
	 */
	private static String ensureUNCPath(String path)
	{
		int len = path.length();
		StringBuffer result = new StringBuffer(len);

		for (int i = 0; i < 4; i++)
		{
			if (i >= len || path.charAt(i) != '/')
			{
				result.append('/');
			}
		}

		result.append(path);

		return result.toString();
	}

	/**
	 * Returns the value that is currently stored for the line separator. In case an IProject reference is given, the
	 * returned value will be the one that was, potentially, set specifically to that project.
	 * 
	 * @param project
	 *            An {@link IProject} reference. Can be null.
	 * @return the currently stored line separator
	 */
	public static String getLineSeparatorValue(IProject project)
	{
		IScopeContext scope;
		if (project != null)
		{
			scope = new ProjectScope(project);
		}
		else
		{
			scope = new InstanceScope();
		}

		IScopeContext[] scopeContext = new IScopeContext[] { scope };
		IEclipsePreferences node = scopeContext[0].getNode(Platform.PI_RUNTIME);
		return node.get(Platform.PREF_LINE_SEPARATOR, System.getProperty("line.separator")); //$NON-NLS-1$
	}

	/**
	 * Add a builder to the given project. Return boolean indicating if it was added (if already exists on project we'll
	 * return a false. if there's an error, we'll throw a CoreException).
	 * 
	 * @param project
	 * @param id
	 * @throws CoreException
	 */
	public static boolean addBuilder(IProject project, String id) throws CoreException
	{
		IProjectDescription desc = project.getDescription();
		if (addBuilder(desc, id))
		{
			project.setDescription(desc, null);
			return true;
		}
		return false;
	}

	/**
	 * Add a builder to the given project description. Does NOT save/set on project. Return boolean indicating if it was
	 * added (if already exists on description we'll return a false).
	 * 
	 * @param description
	 * @param builderId
	 * @throws CoreException
	 */
	public static boolean addBuilder(IProjectDescription description, String builderId)
	{
		ICommand[] commands = description.getBuildSpec();
		boolean addBuilder = true;
		// Don't add duplicate
		for (int i = 0; i < commands.length; ++i)
		{
			if (commands[i].getBuilderName().equals(builderId))
			{
				addBuilder = false;
				break;
			}
		}
		// add builder to project
		if (addBuilder)
		{
			ICommand command = description.newCommand();
			command.setBuilderName(builderId);
			ICommand[] nc = new ICommand[commands.length + 1];
			// Add it before other builders.
			System.arraycopy(commands, 0, nc, 1, commands.length);
			nc[0] = command;
			description.setBuildSpec(nc);
		}
		return addBuilder;
	}
	
	/**
	 * getPath
	 *
	 * @param element
	 * @return path
	 */
	public static String getPath( Object element ) {
		if ( element instanceof IUniformResource ) {
			IUniformResource resource = (IUniformResource) element;
			IPath path = (IPath) resource.getAdapter(IPath.class);
			if (path == null) {
				IStorage storage = (IStorage) resource.getAdapter(IStorage.class);
				if (storage != null) {
					path = (IPath) storage.getAdapter(IPath.class);
				}
			}
			if ( path != null ) {
				return path.toOSString();	
			} else {
				return resource.getURI().toString();
			}			
		}
		if ( element instanceof String ) {
			try {
				element = new URI((String) element);
			} catch (URISyntaxException e) {
			}	
		}
		if ( element instanceof URI ) {
			URI uri = (URI) element;
			if ( "file".equals(uri.getScheme()) ) //$NON-NLS-1$
			{
				return uri.getSchemeSpecificPart();
			}
			return uri.toString();
		}
		return null;
	}
	
	/**
	 * findWorkspaceFile
	 *
	 * @param filePath
	 * @return IFile
	 */
	public static IFile findWorkspaceFile(String filePath) {
		IPath path = new Path(filePath);
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
		if ( resource instanceof IFile )
		{
			return (IFile) resource;
		}
		return null;
	}

	/**
		 * Remove a builder from the given project. Return boolean indicating if it was removed (if doesn't exist on the
		 * project we'll return a false. if there's an error, we'll throw a CoreException).
		 * 
		 * @param project
		 * @param id
		 * @throws CoreException
		 */
		public static boolean removeBuilder(IProject project, String id) throws CoreException
		{
			IProjectDescription desc = project.getDescription();
			if (removeBuilder(desc, id))
			{
				project.setDescription(desc, null);
				return true;
			}
			return false;
		}
	
		/**
		 * Remove a builder from the given project description. Does NOT save/set on project. Return boolean indicating if
		 * it was removed (if already removed from description we'll return a false).
		 * 
		 * @param description
		 * @param builderId
		 * @throws CoreException
		 */
		public static boolean removeBuilder(IProjectDescription description, String builderId)
		{
			ICommand[] commands = description.getBuildSpec();
			boolean removeBuilder = false;
	
			ArrayList<ICommand> builders = new ArrayList<ICommand>();
			for (int i = 0; i < commands.length; i++)
			{
				ICommand iCommand = commands[i];
				if (!iCommand.getBuilderName().equals(builderId))
				{
					builders.add(iCommand);
				}
				else
				{
					removeBuilder = true;
				}
			}
			description.setBuildSpec(builders.toArray(new ICommand[0]));
			return removeBuilder;
		}
	
		/**
		 * Add a nature to the given project. Return boolean indicating if it was added (if already exists on the project
		 * we'll return a false. if there's an error, we'll throw a CoreException).
		 * 
		 * @param project
		 * @param id
		 * @throws CoreException
		 */
		public static boolean addNature(IProject project, String id) throws CoreException
		{
			IProjectDescription desc = project.getDescription();
			if (addNature(desc, id))
			{
				project.setDescription(desc, null);
				return true;
			}
			return false;
		}
	
		/**
		 * Adds a nature to the project. Returns true if added, false if the nature already existed on the project
		 * @param description
		 * @param natureId
		 * @return
		 */
		public static boolean addNature(IProjectDescription description, String natureId)
		{
			String[] natures = description.getNatureIds();
			boolean addNature = true;
			// Don't add duplicate
			for (int i = 0; i < natures.length; ++i)
			{
				if (natures[i].equals(natureId))
				{
					addNature = false;
					break;
				}
			}
			// add nature to project
			if (addNature)
			{
				String[] newNatures = new String[natures.length + 1];
				System.arraycopy(natures, 0, newNatures, 0, natures.length);
				newNatures[natures.length] = natureId;
				description.setNatureIds(newNatures);
			}

			return addNature;
		}
	
		/**
		 * Remove a nature from the given project. Return boolean indicating if it was removed (if doesn't exist on the
		 * project we'll return a false. if there's an error, we'll throw a CoreException).
		 * 
		 * @param project
		 * @param id
		 * @throws CoreException
		 */
		public static boolean removeNature(IProject project, String id) throws CoreException
		{
			IProjectDescription desc = project.getDescription();
			if (removeNature(desc, id))
			{
				project.setDescription(desc, null);
				return true;
			}
			return false;
		}
	
		/**
		 * Remove nature from the project. Returns true if removed, false if the nature did not exist on the project
		 * @param description
		 * @param natureId
		 * @return
		 */
		public static boolean removeNature(IProjectDescription description, String natureId)
		{
			String[] natures = description.getNatureIds();
			boolean removeNature = false;
	
			ArrayList<String> newNatures = new ArrayList<String>(); 
			for (int i = 0; i < natures.length; i++)
			{
				if (!natures[i].equals(natureId))
				{
					newNatures.add(natures[i]);
				}
				else
				{
					removeNature = true;
				}
			}
	
			description.setNatureIds(newNatures.toArray(new String[0]));
			return removeNature;
		}
		
		/**
		 * Determines if the nature is one belonging to Aptana
		 * 
		 * @param natureId
		 *            The natureID in question
		 * @return
		 */
		public static boolean isAptanaNature(String natureId)
		{
			return natureId != null
					&& (natureId.startsWith(APTANA_NATURE_PREFIX) || natureId.startsWith(RAILS_NATURE_PREFIX));
		}
	
		/**
		 * Reurns a list of all the natures that belong to Aptana.
		 * 
		 * @param description
		 * @return
		 */
		public static String[] getAptanaNatures(IProjectDescription description)
		{
			String[] natures = description.getNatureIds();
			ArrayList<String> newNatures = new ArrayList<String>();
	
			// Add Aptana natures to list
			for (int i = 0; i < natures.length; i++)
			{
				if (isAptanaNature(natures[i]))
				{
					newNatures.add(natures[i]);
				}
			}
	
			return newNatures.toArray(new String[0]);
		}
	
		/**
		 * Removes the passed-in builder if there are zero Aptana natures left on the project.
		 * 
		 * @param description
		 * @param builderId
		 * @return
		 * @throws CoreException 
		 */
		public static boolean removeBuilderIfOrphaned(IProject project, String builderId) throws CoreException
		{
			String[] natures = getAptanaNatures(project.getDescription());
			if (natures.length == 0)
			{
				return removeBuilder(project, builderId);
			}
			else
			{
				return false;
			}
	
		}
	
	
}
