/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.outline;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IURIEditorInput;

import com.aptana.editor.common.resolver.IPathResolver;
import com.aptana.editor.common.resolver.URIResolver;

/**
 * @author Pavel Petrochenko
 * @author Chris Williams
 */
public final class PathResolverProvider
{

	/**
	 * @author Pavel Petrochenko resolver which is not able to resolve anything
	 */
	private static final class NullResolver implements IPathResolver
	{
		/**
		 * @see com.aptana.editor.common.resolver.ide.views.outline.IPathResolver#resolveSource(java.lang.String)
		 */
		public String resolveSource(String path, IProgressMonitor monitor) throws Exception
		{
			return null;
		}

		public URI resolveURI(String path)
		{
			try
			{
				return new URI(path);
			}
			catch (URISyntaxException e)
			{
			}
			return null;
		}
	}

	private PathResolverProvider()
	{
	}

	/**
	 * returns path resolver for a given editor input
	 * 
	 * @param input
	 * @return path resolver for a given editor input
	 */
	public static IPathResolver getResolver(IEditorInput input)
	{
		if (input instanceof IURIEditorInput)
		{
			URI uri = ((IURIEditorInput) input).getURI();
			return new URIResolver(uri);
		}

		IPathEditorInput fInput;
		if (input instanceof IPathEditorInput)
		{
			fInput = (IPathEditorInput) (input);
		}
		else
		{
			fInput = (IPathEditorInput) input.getAdapter(IPathEditorInput.class);
		}
		if (fInput != null)
		{
			return new URIResolver(fInput.getPath().toFile().toURI());
		}
		return new NullResolver();
	}
}
