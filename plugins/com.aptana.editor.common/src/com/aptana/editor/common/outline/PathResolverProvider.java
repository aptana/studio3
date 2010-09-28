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
