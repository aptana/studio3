/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.index.core;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class IndexManager
{
	private static IndexManager INSTANCE;
	private Map<URI, Index> indexes;

	static final ISchedulingRule MUTEX_RULE = new ISchedulingRule()
	{
		public boolean contains(ISchedulingRule rule)
		{
			return rule == this;
		}

		public boolean isConflicting(ISchedulingRule rule)
		{
			return rule == this;
		}
	};

	/**
	 * getInstance
	 * 
	 * @return
	 */
	public synchronized static IndexManager getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new IndexManager();
		}

		return INSTANCE;
	}

	/**
	 * IndexManager
	 */
	private IndexManager()
	{
		this.indexes = new HashMap<URI, Index>();
	}

	/**
	 * getIndex
	 * 
	 * @param path
	 * @return
	 */
	public synchronized Index getIndex(URI path)
	{
		Index index = this.indexes.get(path);

		if (index == null)
		{
			try
			{
				// First try to re-use an existing file if possible
				index = new Index(path, true);
				indexes.put(path, index);
			}
			catch (IOException e)
			{
				try
				{
					// We failed. Most likely disk index signature changed or got corrupted.
					// Don't re-use the file (create an empty index file)
					index = new Index(path, false);
					this.indexes.put(path, index);

					// force a rebuild of the index.
					new RebuildIndexJob(path).schedule();
				}
				catch (IOException e1)
				{
					IndexActivator.logError("An error occurred while trying to access an index", e1); //$NON-NLS-1$
				}
			}
		}

		return index;
	}

	/**
	 * getIndexPaths
	 * 
	 * @return
	 */
	public synchronized List<URI> getIndexPaths()
	{
		return new ArrayList<URI>(indexes.keySet());
	}

	/**
	 * Removes the index for a given path. This is a no-op if the index did not exist.
	 */
	public synchronized void removeIndex(URI path)
	{
		Index index = getIndex(path);

		if (index != null)
		{
			index.deleteIndexFile();
		}

		this.indexes.remove(path);
	}
}
