/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.internal.index;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.resources.TaskTag;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.coffee.parsing.ast.CoffeeCommentNode;
import com.aptana.index.core.AbstractFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.build.BuildContext;
import com.aptana.parsing.ast.IParseNode;

public class CoffeeFileIndexingParticipant extends AbstractFileIndexingParticipant
{

	public void index(BuildContext context, Index index, IProgressMonitor monitor) throws CoreException
	{
		// no-op, we don't currently index coffeescript...
		// FIXME Index coffeescript into the JS index for the project!
	}

	public void indexFileStore(final Index index, IFileStore store, IProgressMonitor monitor)
	{

	}
}