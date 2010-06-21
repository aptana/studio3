package com.aptana.index.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class IndexManager
{
	private static IndexManager instance;
	private Map<String, Index> indexes;

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
		if (instance == null)
			instance = new IndexManager();
		return instance;
	}

	/**
	 * IndexManager
	 */
	private IndexManager()
	{
		indexes = new HashMap<String, Index>();
	}

	/**
	 * computeIndexLocation
	 * 
	 * @param path
	 * @return
	 */
	public IPath computeIndexLocation(String path)
	{
		CRC32 crc = new CRC32();
		crc.reset();
		crc.update(path.getBytes());
		String fileName = Long.toString(crc.getValue()) + ".index"; //$NON-NLS-1$
		return IndexActivator.getDefault().getStateLocation().append(fileName);
	}

	/**
	 * getIndex
	 * 
	 * @param path
	 * @return
	 */
	public Index getIndex(String path)
	{
		Index index = indexes.get(path);
		if (index == null)
		{
			try
			{
				index = new Index(path);
				indexes.put(path, index);
			}
			catch (IOException e)
			{
				IndexActivator.logError("An error occurred while trying to access an index", e);
			}
		}
		return index;
	}

	/**
	 * Removes the index for a given path. This is a no-op if the index did not exist.
	 */
	public synchronized void removeIndex(String path)
	{
		Index index = getIndex(path);
		File indexFile = null;
		if (index != null)
		{
			index.monitor = null;
			indexFile = index.getIndexFile();
		}
		if (indexFile.exists())
		{
			indexFile.delete();
		}
		this.indexes.remove(path);
	}
}
