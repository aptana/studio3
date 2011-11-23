package com.aptana.index.core.build;

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeMatcher;

import com.aptana.core.build.IProblem;
import com.aptana.core.util.IOUtil;
import com.aptana.index.core.IndexPlugin;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseRootNode;

public class BuildContext
{

	public static final IContentType[] NO_CONTENT_TYPES = new IContentType[0];

	private IFile file;
	protected Map<String, Collection<IProblem>> problems;
	private IParseState fParseState;

	private String fContents;

	public BuildContext(IFile file)
	{
		this.file = file;
		this.problems = new HashMap<String, Collection<IProblem>>();
		this.fParseState = null;
	}

	public IProject getProject()
	{
		return getFile().getProject();
	}

	public IFile getFile()
	{
		return file;
	}

	public URI getURI()
	{
		return getFile().getLocationURI();
	}

	public synchronized IParseRootNode getAST() throws CoreException
	{
		return getAST(new ParseState());
	}

	public synchronized IParseRootNode getAST(IParseState parseState) throws CoreException
	{
		boolean reparse = false;
		if (fParseState == null)
		{
			reparse = true;
			fParseState = parseState;
		}
		else
		{
			// TODO Check equality of the parse states to see if anything has changed (and therefore we need to
			// re-parse)?
		}

		if (reparse)
		{
			// FIXME What if we fail to parse? Should we catch and log that exception here and return null?
			try
			{
				fParseState.setEditState(getContents(), null, 0, 0);
				// FIXME The parsers need to throw a specific SyntaxException or something for us to differentiate
				// between those and IO errors!
				IParseRootNode ast = ParserPoolFactory.parse(getContentType(), parseState);
				fParseState.setParseResult(ast);

				// TODO Turn fParseState.getErrors() into IProblems on this context?
			}
			catch (CoreException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				throw new CoreException(new Status(IStatus.ERROR, IndexPlugin.PLUGIN_ID, e.getMessage(), e));
			}
			finally
			{
				// Wipe the source out of the parse state to clean up RAM?
				fParseState.setEditState(null, null, 0, 0);
			}
		}
		if (fParseState == null)
		{
			return null;
		}
		return fParseState.getParseResult();
	}

	public synchronized void resetAST()
	{
		fParseState = null;
	}

	public synchronized String getContents() throws CoreException
	{
		if (fContents == null)
		{
			fContents = IOUtil.read(openInputStream(new NullProgressMonitor()), getCharset());
		}
		return fContents;
	}

	protected String getCharset() throws CoreException
	{
		return file.getCharset(true);
	}

	public String getContentType() throws CoreException
	{
		// TODO Cache this?
		IContentType[] types = getContentTypes();
		if (types == null || types.length == 0)
		{
			return null;
		}
		return types[0].getId();
	}

	protected IContentType[] getContentTypes() throws CoreException
	{
		IContentTypeMatcher matcher = getProject().getContentTypeMatcher();
		return matcher.findContentTypesFor(getName());
	}

	public String getName()
	{
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
		return Collections.unmodifiableMap(problems);
	}

	public Collection<IParseError> getParseErrors()
	{
		// FIXME Maybe we should force getAST() if fParseState == null?
		
		return Collections.unmodifiableCollection(fParseState.getErrors());
	}

	public InputStream openInputStream(IProgressMonitor monitor) throws CoreException
	{
		return file.getContents();
	}
}
