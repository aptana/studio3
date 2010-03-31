package com.aptana.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.WorkbenchEncoding;

/**
 * Editing utilities.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class EditorUtils
{
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
		return node.get(Platform.PREF_LINE_SEPARATOR, System.getProperty("line.separator"));
	}

	/**
	 * Returns the defined encoding (char-set) for the given file.
	 * 
	 * <pre>
	 * The search for the encoding is done in this order:
	 * 1. Check the encoding that is set specifically to the given IFile.
	 * 2. Check the workspace default charset.
	 * 3. If all the above fails, get ResourcesPlugin.getEncoding(), which actually gets the encoding from the system.
	 * </pre>
	 * 
	 * @param file
	 *            An {@link IFile} reference (can be null).
	 * @return The file's encoding, or the workspace default encoding if the file is null.
	 */
	public static String getEncoding(IFile file)
	{
		String charset = null;
		try
		{
			if (file != null)
			{
				String fileCharset = file.getCharset(true);
				if (fileCharset != null)
				{
					charset = fileCharset;
				}
			}
		}
		catch (Throwable e)
		{
			// If there is any error, return the default
		}
		if (charset == null)
		{
			try
			{
				IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
				charset = workspaceRoot.getDefaultCharset(true);
			}
			catch (CoreException ce)
			{
				charset = WorkbenchEncoding.getWorkbenchDefaultEncoding();
			}
		}
		if (charset == null)
		{
			// Use the system's encoding
			charset = ResourcesPlugin.getEncoding();
		}
		return charset;
	}
}
