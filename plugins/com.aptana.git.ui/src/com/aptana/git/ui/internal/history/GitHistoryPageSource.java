/*******************************************************************************
 * Copyright (C) 2007, Robin Rosenberg <robin.rosenberg@dewire.com>
 * Copyright (C) 2008, Shawn O. Pearce <spearce@spearce.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.aptana.git.ui.internal.history;

import org.eclipse.core.resources.IResource;
import org.eclipse.team.ui.history.HistoryPageSource;
import org.eclipse.team.ui.history.IHistoryPageSource;
import org.eclipse.ui.part.Page;

/**
 * A helper class for constructing the {@link GitHistoryPage}.
 */
public class GitHistoryPageSource extends HistoryPageSource
{
	private static final IResource[] NO_RESOURCES = new IResource[0];
	private static GitHistoryPageSource instance;

	public boolean canShowHistoryFor(final Object object)
	{
		return GitHistoryPage.canShowHistoryFor(object);
	}

	public Page createPage(final Object object)
	{
		final IResource[] input;

		if (object instanceof IResource[])
		{
			input = (IResource[]) object;
		}
		else if (object instanceof IResource)
		{
			input = new IResource[] { (IResource) object };
		}
		else
		{
			input = NO_RESOURCES;
		}

		final GitHistoryPage pg = new GitHistoryPage();
		pg.setInput(input);
		return pg;
	}

	public synchronized static IHistoryPageSource getInstance()
	{
		if (instance == null)
		{
			instance = new GitHistoryPageSource();
		}
		return instance;
	}
}
