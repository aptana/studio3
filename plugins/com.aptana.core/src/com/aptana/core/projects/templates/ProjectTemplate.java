/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.projects.templates;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;
import com.aptana.core.util.ZipUtil;
import com.aptana.core.util.ZipUtil.IInputStreamTransformer;
import com.aptana.core.util.replace.SimpleTextPatternReplacer;

/**
 * Project template that is loaded from the <code>"projectTemplates"</code> extension point.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class ProjectTemplate implements IProjectTemplate
{

	private final class TemplateSubstitutionTransformer implements IInputStreamTransformer
	{
		private final IProject project;

		private TemplateSubstitutionTransformer(IProject project)
		{
			this.project = project;
		}

		public InputStream transform(InputStream in, IPath relativePath)
		{
			if (!isReplacingParameters)
			{
				return in;
			}

			IFile file = project.getFile(relativePath);
			if (!isSupportedFile(file))
			{
				return in;
			}

			// Now try applying template substitution
			String content = null;
			try
			{
				content = applyTemplateVariables(in, file, project);
			}
			catch (Exception e)
			{
				IdeLog.logWarning(CorePlugin.getDefault(),
						"Error applying a template. Trying to write the file as is, without template evaluation.", e); //$NON-NLS-1$
			}

			if (content == null)
			{
				// In case we should not evaluate template tags, or had a previous error, read without template
				// substitution.
				content = IOUtil.read(in);
			}
			try
			{
				return new ByteArrayInputStream(content.getBytes(IOUtil.UTF_8));
			}
			catch (UnsupportedEncodingException ignore)
			{
				// should never happen
			}
			return in;
		}
	}

	private TemplateType type;
	private String path;
	private String description;
	private String name;
	private String id;
	private URL iconURL;
	private boolean isReplacingParameters;
	private int priority;
	private List<String> tags;
	private boolean isPrePackaged;

	/**
	 * Constructs a new ProjectTemplate
	 * 
	 * @param path
	 * @param type
	 * @param name
	 * @param isReplacingParameters
	 * @param description
	 * @param iconURL
	 * @param id
	 * @param priority
	 */
	public ProjectTemplate(String path, TemplateType type, String name, boolean isReplacingParameters,
			String description, URL iconURL, String id)
	{
		this(path, type, name, isReplacingParameters, description, iconURL, id, 0, null, false);
	}

	/**
	 * Constructs a new ProjectTemplate
	 * 
	 * @param path
	 * @param type
	 * @param name
	 * @param isReplacingParameters
	 * @param description
	 * @param iconURL
	 * @param id
	 * @param priority
	 */
	public ProjectTemplate(String path, TemplateType type, String name, boolean isReplacingParameters,
			String description, URL iconURL, String id, int priority, List<String> tags)
	{
		this(path, type, name, isReplacingParameters, description, iconURL, id, 0, tags, false);
	}

	/**
	 * Constructs a new ProjectTemplate
	 * 
	 * @param path
	 * @param type
	 * @param name
	 * @param isReplacingParameters
	 * @param description
	 * @param iconURL
	 * @param id
	 * @param priority
	 */
	public ProjectTemplate(String path, TemplateType type, String name, boolean isReplacingParameters,
			String description, URL iconURL, String id, int priority, List<String> tags, boolean isPrePackaged)
	{
		this.type = type;
		this.path = path;
		this.name = name;
		this.isReplacingParameters = isReplacingParameters;
		this.description = description;
		this.iconURL = iconURL;
		this.id = id;
		this.priority = priority;
		this.tags = CollectionsUtil.isEmpty(tags) ? null : new ArrayList<String>(tags);
		this.isPrePackaged = isPrePackaged;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#getPath()
	 */
	public String getPath()
	{
		return path;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#getDisplayName()
	 */
	public String getDisplayName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#getDescription()
	 */
	public String getDescription()
	{
		return description;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#getDirectory()
	 */
	public File getDirectory()
	{
		return new File(path).getParentFile();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#getLocation()
	 */
	public String getLocation()
	{
		return Path.fromOSString(path).lastSegment();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#getType()
	 */
	public TemplateType getType()
	{
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#getIconPath()
	 */
	public URL getIconURL()
	{
		return iconURL;
	}

	public int getPriority()
	{
		return priority;
	}

	public List<String> getTags()
	{
		return tags;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#isReplacingParameters()
	 */
	public boolean isReplacingParameters()
	{
		return isReplacingParameters;
	}

	protected void toSource(SourcePrinter printer)
	{
	}

	@Override
	public String toString()
	{
		SourcePrinter printer = new SourcePrinter();

		// open element
		printer.printWithIndent("project_template"); //$NON-NLS-1$
		printer.print(" \"").print(this.getDisplayName()).println("\" {").increaseIndent(); //$NON-NLS-1$ //$NON-NLS-2$

		printBody(printer, false, this);

		// close element
		printer.decreaseIndent().printlnWithIndent("}"); //$NON-NLS-1$
		return printer.toString();
	}

	/**
	 * Prints the interior body of the template element
	 * 
	 * @param printer
	 * @param includeBlocks
	 * @param template
	 */
	public static void printBody(SourcePrinter printer, boolean includeBlocks, IProjectTemplate template)
	{
		printer.printWithIndent("path: ").println(template.getPath()); //$NON-NLS-1$
		printer.printWithIndent("name: ").println(template.getDisplayName()); //$NON-NLS-1$
		printer.printWithIndent("location: ").println(template.getLocation()); //$NON-NLS-1$
		printer.printWithIndent("id: ").println(template.getId()); //$NON-NLS-1$
		printer.printWithIndent("type: ").println(template.getType().name()); //$NON-NLS-1$
		printer.printWithIndent("replaceParameters: ").println(Boolean.toString(template.isReplacingParameters())); //$NON-NLS-1$

		if (template.getDescription() != null)
		{
			printer.printWithIndent("description: ").println(template.getDescription()); //$NON-NLS-1$
		}

		if (template.getIconURL() != null)
		{
			printer.printWithIndent("iconURL: ").println(template.getIconURL().toString()); //$NON-NLS-1$
		}

		if (!CollectionsUtil.isEmpty(template.getTags()))
		{
			printer.printlnWithIndent("tags: ").println(StringUtil.join(",", template.getTags())); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#getId()
	 */
	public String getId()
	{
		return id;
	}

	public IStatus apply(final IProject project, boolean promptForOverwrite)
	{
		IInputStreamTransformer transform = new TemplateSubstitutionTransformer(project);
		File zipFile = new File(getDirectory(), getLocation());
		try
		{
			return ZipUtil.extract(zipFile, project.getLocation().toFile(),
					promptForOverwrite ? ZipUtil.Conflict.PROMPT : ZipUtil.Conflict.OVERWRITE, transform,
					new NullProgressMonitor());
		}
		catch (IOException e)
		{
			return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, MessageFormat.format(
					"IOException reading zipfile {0}", zipFile.getAbsolutePath()), e); //$NON-NLS-1$
		}
	}

	/**
	 * Returns the index files to open after a project creation. The default implementation returns <code>null</code>,
	 * and subclasses should overwrite when needed.
	 * 
	 * @see com.aptana.core.projects.templates.IProjectTemplate#getIndexFiles()
	 */
	public IPath[] getIndexFiles()
	{
		return null;
	}

	/**
	 * Returns true if the given file can be evaluated for template-variables.<br>
	 * There is no good way of detecting what is binary and what is not, so we decide what is supported by checking if
	 * the content type is a sub-type of text.
	 * 
	 * @param file
	 * @return true if the file can be processed; false, otherwise.
	 */
	private static boolean isSupportedFile(IFile file)
	{
		IContentTypeManager manager = Platform.getContentTypeManager();
		if (manager == null)
		{
			return false;
		}
		IContentType contentType = manager.findContentTypeFor(file.getName());
		if (contentType == null)
		{
			return false;
		}
		IContentType text = manager.getContentType("org.eclipse.core.runtime.text"); //$NON-NLS-1$
		return contentType.isKindOf(text);
	}

	/**
	 * Apply the project-template variables on the files that were extracted as the project contents.
	 * 
	 * @param inputStream
	 * @param file
	 * @param project
	 * @return A string content of the {@link InputStream}, <b>after</b> the variables substitution.
	 * @throws CoreException
	 */
	private static String applyTemplateVariables(InputStream inputStream, IFile file, IProject project)
			throws CoreException
	{
		SimpleTextPatternReplacer replacer = new SimpleTextPatternReplacer();

		try
		{
			IPath absoluteFilePath = file.getLocation();
			String filePathString = absoluteFilePath.toOSString();

			replacer.addPattern("${TM_NEW_FILE_BASENAME}", absoluteFilePath.removeFileExtension().lastSegment()); //$NON-NLS-1$
			replacer.addPattern("${TM_NEW_FILE}", filePathString); //$NON-NLS-1$
			replacer.addPattern("${TM_NEW_FILE_DIRECTORY}", absoluteFilePath.removeLastSegments(1).toOSString()); //$NON-NLS-1$
			replacer.addPattern("${TM_PROJECTNAME}", project.getName()); //$NON-NLS-1$
			Calendar calendar = Calendar.getInstance();
			replacer.addPattern("${TIME}", calendar.getTime().toString()); //$NON-NLS-1$
			replacer.addPattern("${YEAR}", Integer.toString(calendar.get(Calendar.YEAR))); //$NON-NLS-1$

			String text = IOUtil.read(inputStream);
			return replacer.searchAndReplace(text);
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID,
					Messages.NewProjectWizard_templateVariableApplyError));
		}
		finally
		{
			if (inputStream != null)
			{
				try
				{
					inputStream.close();
				}
				catch (IOException e)
				{
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#isPrePackaged()
	 */
	public boolean isPrePackaged()
	{
		return isPrePackaged;
	}
}
