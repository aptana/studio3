/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core.build;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeMatcher;

import com.aptana.core.build.IProblem;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IDebugScopes;
import com.aptana.index.core.IndexPlugin;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseResult;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.WorkingParseResult;
import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseRootNode;

public class BuildContext
{

	public static final IContentType[] NO_CONTENT_TYPES = new IContentType[0];

	private IFile file;
	protected Map<String, Collection<IProblem>> problems;
	private ParseResult fParseResult;

	private String fContents;

	protected BuildContext()
	{
		this.problems = new HashMap<String, Collection<IProblem>>();
	}

	public BuildContext(IFile file)
	{
		this();
		this.file = file;
	}

	public IProject getProject()
	{
		IFile file = getFile();
		if (file == null)
		{
			return null;
		}
		return file.getProject();
	}

	public IFile getFile()
	{
		return file;
	}

	public URI getURI()
	{
		IFile file = getFile();
		if (file == null)
		{
			return null;
		}
		return file.getLocationURI();
	}

	public IParseRootNode getAST() throws CoreException
	{
		return getAST(new ParseState(getContents())).getRootNode();
	}

	/**
	 * Does not return null (must be an empty parse result in the case the ast == null).
	 */
	public synchronized ParseResult getAST(IParseState parseState) throws CoreException
	{
		try
		{
			// FIXME What if we fail to parse? Should we catch and log that exception here and return null?
			try
			{
				// FIXME The parsers need to throw a specific SyntaxException or something for us to differentiate
				// between those and IO errors!
				WorkingParseResult working = new WorkingParseResult();
				fParseResult = parse(getContentType(), parseState, working);
			}
			catch (CoreException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				throw new CoreException(new Status(IStatus.ERROR, IndexPlugin.PLUGIN_ID, e.getMessage(), e));
			}
			if (fParseResult == null)
			{
				return ParseResult.EMPTY;
			}
			return fParseResult;
		}
		finally
		{
			if (parseState != null)
			{
				// Wipe the source out of the parse state to clean up RAM?
				parseState.clearEditState();
			}
		}
	}

	protected ParseResult parse(String contentType, IParseState parseState, WorkingParseResult working)
			throws Exception
	{
		return ParserPoolFactory.parse(contentType, parseState);
	}

	public synchronized void resetAST()
	{
		fParseResult = null;
	}

	public synchronized String getContents()
	{
		if (fContents == null)
		{
			try
			{
				fContents = IOUtil.read(openInputStream(new NullProgressMonitor()), getCharset());
			}
			catch (CoreException e)
			{
				fContents = StringUtil.EMPTY;
			}
		}
		return fContents;
	}

	public String getCharset() throws CoreException
	{
		IFile file = getFile();
		if (file == null)
		{
			return null;
		}
		return file.getCharset(true);
	}

	public String getContentType() throws CoreException
	{
		IContentType[] types = getContentTypes();
		if (ArrayUtil.isEmpty(types))
		{
			return null;
		}
		return types[0].getId();
	}

	protected IContentType[] getContentTypes() throws CoreException
	{
		// TODO Cache this?
		IProject theProject = getProject();
		if (theProject != null)
		{
			IContentTypeMatcher matcher = theProject.getContentTypeMatcher();
			return matcher.findContentTypesFor(getName());
		}

		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		if (ArrayUtil.isEmpty(projects))
		{
			return Platform.getContentTypeManager().findContentTypesFor(getName());
		}

		for (IProject project : projects)
		{
			try
			{
				IContentType[] type = project.getContentTypeMatcher().findContentTypesFor(getName());
				if (type != null)
				{
					return type;
				}
			}
			catch (CoreException e)
			{
				IdeLog.logError(IndexPlugin.getDefault(), e);
			}
		}
		return NO_CONTENT_TYPES;
	}

	public String getName()
	{
		IFile file = getFile();
		if (file == null)
		{
			return null;
		}
		return file.getName();
	}

	public void removeProblems(String markerType)
	{
		this.problems.remove(markerType);
	}

	public void putProblems(String markerType, Collection<IProblem> problems)
	{
		// TODO Maybe just add problems?
		this.problems.put(markerType, problems);
	}

	public Map<String, Collection<IProblem>> getProblems()
	{
		// TODO Handle possible concurrent modification exceptions
		return Collections.unmodifiableMap(problems);
	}

	public Collection<IParseError> getParseErrors()
	{
		if (fParseResult == null)
		{
			// FIXME Maybe we should force getAST() if fParseState == null?
			return Collections.emptyList();
		}

		// Note: unmodifiable collection returned
		return fParseResult.getErrors();
	}

	public InputStream openInputStream(IProgressMonitor monitor) throws CoreException
	{
		IFile file = getFile();
		if (file == null)
		{
			return new ByteArrayInputStream(ArrayUtil.NO_BYTES);
		}
		file.refreshLocal(IResource.DEPTH_ZERO, null);
		if (!file.exists())
		{
			return new ByteArrayInputStream(ArrayUtil.NO_BYTES);
		}
		try
		{
			return file.getContents();
		}
		catch (Exception e)
		{
			IdeLog.logWarning(IndexPlugin.getDefault(), "Error while opening the input stream.", e, //$NON-NLS-1$
					IDebugScopes.INDEXER);
			return new ByteArrayInputStream(ArrayUtil.NO_BYTES);
		}
	}

	public boolean isReconcile()
	{
		return false;
	}
}
