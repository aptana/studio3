/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNatureDescriptor;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

public class ResourceUtil
{
	private static final String UNC_PREFIX = "//"; //$NON-NLS-1$
	private static final String SCHEME_FILE = "file"; //$NON-NLS-1$

	// Leaving these here at the moment, since I can't think of a better place to put them
	private static final String APTANA_NATURE_PREFIX = "com.aptana."; //$NON-NLS-1$
	private static final String RAILS_NATURE_PREFIX = "org.radrails.rails."; //$NON-NLS-1$
	private static final String APPCELERATOR_NATURE_PREFIX = "com.appcelerator."; //$NON-NLS-1$

	private ResourceUtil()
	{
	}

	/**
	 * resourcePathToFile
	 * 
	 * @param url
	 * @return
	 */
	public static File resourcePathToFile(URL url)
	{
		URI fileURI = resourcePathToURI(url);
		return (fileURI == null) ? null : new File(fileURI);
	}

	/**
	 * resourcePathToString
	 * 
	 * @param url
	 * @return
	 */
	public static String resourcePathToString(URL url)
	{
		File file = resourcePathToFile(url);
		return (file == null) ? null : file.getAbsolutePath();
	}

	/**
	 * resourcePathToURI
	 * 
	 * @param url
	 * @return
	 */
	public static URI resourcePathToURI(URL url)
	{
		if (url == null)
		{
			return null;
		}

		try
		{
			URL fileURL = FileLocator.toFileURL(url);

			return toURI(fileURL); // Use Eclipse to get around Java 1.5 bug on Windows
		}
		catch (IOException e)
		{
			String message = MessageFormat.format(Messages.ResourceUtils_URL_To_File_URL_Conversion_Error,
					new Object[] { url });
			IdeLog.logError(CorePlugin.getDefault(), message, e);
		}
		catch (URISyntaxException e)
		{
			String message = MessageFormat.format(Messages.ResourceUtils_File_URL_To_URI_Conversion_Error,
					new Object[] { url });
			IdeLog.logError(CorePlugin.getDefault(), message, e);
		}
		return null;
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
		if (url == null)
		{
			return null;
		}

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
		StringBuilder result = new StringBuilder(len);

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
			scope = EclipseUtil.instanceScope();
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
		if (project == null)
		{
			return false;
		}
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
	@SuppressWarnings("rawtypes")
	public static boolean addBuilder(IProjectDescription description, String builderId)
	{
		ICommand[] commands = description.getBuildSpec();
		boolean addBuilder = true;
		// Don't add duplicate
		for (ICommand command : commands)
		{
			if (command.getBuilderName().equals(builderId))
			{
				addBuilder = false;
				break;
			}
			// when a builder is disabled, Eclipse turns it into an external tool builder, so we have to do a little
			// hack to see if the new builder id matches one of the disabled builds
			Map arguments = command.getArguments();
			if (arguments != null)
			{
				Object value = arguments.get("LaunchConfigHandle"); //$NON-NLS-1$
				if (value != null)
				{
					String configHandler = value.toString(); // $codepro.audit.disable
																// com.instantiations.assist.eclipse.analysis.unnecessaryToString
					if (configHandler != null && configHandler.indexOf(builderId) > -1)
					{
						addBuilder = false;
						break;
					}
				}
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
		List<ICommand> builders = new ArrayList<ICommand>();
		for (ICommand command : commands)
		{
			if (!command.getBuilderName().equals(builderId))
			{
				builders.add(command);
			}
			else
			{
				removeBuilder = true;
			}
		}
		description.setBuildSpec(builders.toArray(new ICommand[builders.size()]));
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
	 * 
	 * @param description
	 * @param natureId
	 * @return
	 */
	public static boolean addNature(IProjectDescription description, String natureId)
	{
		String[] natures = description.getNatureIds();
		boolean addNature = true;
		// Don't add duplicate
		for (String nature : natures)
		{
			if (nature.equals(natureId))
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
	 * 
	 * @param description
	 * @param natureId
	 * @return
	 */
	public static boolean removeNature(IProjectDescription description, String natureId)
	{
		String[] natures = description.getNatureIds();
		boolean removeNature = false;
		List<String> newNatures = new ArrayList<String>();
		for (String nature : natures)
		{
			if (!nature.equals(natureId))
			{
				newNatures.add(nature);
			}
			else
			{
				removeNature = true;
			}
		}

		description.setNatureIds(newNatures.toArray(new String[newNatures.size()]));
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
				&& (natureId.startsWith(APTANA_NATURE_PREFIX) || natureId.startsWith(RAILS_NATURE_PREFIX) || natureId
						.startsWith(APPCELERATOR_NATURE_PREFIX));
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
		List<String> newNatures = new ArrayList<String>();
		// Add Aptana natures to list
		for (String nature : natures)
		{
			if (isAptanaNature(nature))
			{
				newNatures.add(nature);
			}
		}

		return newNatures.toArray(new String[newNatures.size()]);
	}

	/**
	 * Return a map of Aptana nature name to nature id
	 * 
	 * @return
	 */
	public static Map<String, String> getAptanaNaturesMap()
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProjectNatureDescriptor[] natureDescriptors = workspace.getNatureDescriptors();
		Map<String, String> result = new HashMap<String, String>();

		// collect Studio-only natures
		for (IProjectNatureDescriptor natureDescriptor : natureDescriptors)
		{
			if (isAptanaNature(natureDescriptor.getNatureId()))
			{
				result.put(natureDescriptor.getLabel(), natureDescriptor.getNatureId());
			}
		}

		return result;
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
		return false;
	}

	/**
	 * If the file is null, doesn't exist, is derived or is team private this returns true. Used to skip files for
	 * build/reconcile.
	 * 
	 * @param file
	 * @return
	 */
	public static boolean shouldIgnore(IFile file)
	{
		return file == null || !file.exists() || file.isTeamPrivateMember(IResource.CHECK_ANCESTORS)
				|| file.isDerived(IResource.CHECK_ANCESTORS);
	}

	/**
	 * Returns <code>true</code> if the given project is not <code>null</code> and is accessible.
	 * 
	 * @param project
	 * @return <code>true</code> if accessible; <code>false</code> otherwise.
	 * @see IProject#isAccessible()
	 */
	public static boolean isAccessible(IProject project)
	{
		return project != null && project.isAccessible();
	}

	/**
	 * @param projectPath
	 *            the project location
	 * @param natureIds
	 *            the list of required natures
	 * @param builderIds
	 *            the list of required builders
	 * @return a project description for the project that includes the list of required natures and builders
	 */
	public static IProjectDescription getProjectDescription(IPath projectPath, String[] natureIds, String[] builderIds)
	{
		if (projectPath == null)
		{
			return null;
		}

		IProjectDescription description = null;
		IPath dotProjectPath = projectPath.append(IProjectDescription.DESCRIPTION_FILE_NAME);
		File dotProjectFile = dotProjectPath.toFile();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (dotProjectFile.exists())
		{
			// loads description from the existing .project file
			try
			{
				description = workspace.loadProjectDescription(dotProjectPath);
				if (Platform.getLocation().isPrefixOf(projectPath))
				{
					description.setLocation(null);
				}
			}
			catch (CoreException e)
			{
				IdeLog.logWarning(CorePlugin.getDefault(), "Failed to load the existing .project file.", e); //$NON-NLS-1$
			}
		}
		if (description == null)
		{
			// creates a new project description
			description = workspace.newProjectDescription(projectPath.lastSegment());
			if (Platform.getLocation().isPrefixOf(projectPath))
			{
				description.setLocation(null);
			}
			else
			{
				description.setLocation(projectPath);
			}
		}

		// adds the required natures to the project description
		if (!ArrayUtil.isEmpty(natureIds))
		{
			Set<String> natures = CollectionsUtil.newInOrderSet(natureIds);
			CollectionsUtil.addToSet(natures, description.getNatureIds());
			description.setNatureIds(natures.toArray(new String[natures.size()]));
		}

		// adds the required builders to the project description
		if (!ArrayUtil.isEmpty(builderIds))
		{
			ICommand[] existingBuilders = description.getBuildSpec();
			List<ICommand> builders = CollectionsUtil.newList(existingBuilders);
			for (String builderId : builderIds)
			{
				if (!hasBuilder(builderId, existingBuilders))
				{
					ICommand newBuilder = description.newCommand();
					newBuilder.setBuilderName(builderId);
					builders.add(newBuilder);
				}
			}
			description.setBuildSpec(builders.toArray(new ICommand[builders.size()]));
		}

		return description;
	}

	private static boolean hasBuilder(String builderName, ICommand[] builders)
	{
		if (StringUtil.isEmpty(builderName))
		{
			return false;
		}
		for (ICommand builder : builders)
		{
			if (builderName.equals(builder.getBuilderName()))
			{
				return true;
			}
		}
		return false;
	}
}
