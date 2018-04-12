/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.index;

import java.io.IOException;
import java.net.URI;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.Bundle;

import com.aptana.core.logging.IdeLog;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.MetadataLoader;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.index.IJSIndexConstants;
import com.aptana.js.core.index.JSIndexQueryHelper;
import com.aptana.js.core.model.TypeElement;
import com.aptana.js.core.preferences.IPreferenceConstants;

public class JSMetadataLoader extends MetadataLoader<JSMetadataReader>
{
	/**
	 * MetadataLoader
	 */
	public JSMetadataLoader()
	{
		super(Messages.Loading_Metadata);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#createMetadataReader()
	 */
	@Override
	protected JSMetadataReader createMetadataReader()
	{
		return new JSMetadataReader();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#getBundle()
	 */
	@Override
	protected Bundle getBundle()
	{
		return JSCorePlugin.getDefault().getBundle();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#getIndexVersion()
	 */
	@Override
	protected double getIndexVersion()
	{
		return IJSIndexConstants.INDEX_VERSION;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#getIndexVersionKey()
	 */
	@Override
	protected String getIndexVersionKey()
	{
		return IPreferenceConstants.JS_INDEX_VERSION;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#getMetadataFiles()
	 */
	@Override
	protected String[] getMetadataFiles()
	{
		return new String[] { //
		"/metadata/js_core.xml", //$NON-NLS-1$
				"/metadata/dom_0.xml", //$NON-NLS-1$
				"/metadata/dom_2.xml", //$NON-NLS-1$
				"/metadata/dom_3.xml", //$NON-NLS-1$
				"/metadata/dom_5.xml" //$NON-NLS-1$;
		};
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#getPluginId()
	 */
	@Override
	protected String getPluginId()
	{
		return JSCorePlugin.PLUGIN_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#postRebuild()
	 */
	@Override
	protected void postRebuild()
	{
		super.postRebuild();

		this.rebuildProjectIndexes();
	}

	/**
	 * rebuildProjectIndexes
	 */
	private void rebuildProjectIndexes()
	{
		Job job = new Job(Messages.JSMetadataLoader_Rebuilding_Project_Indexes)
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				IWorkspace ws = ResourcesPlugin.getWorkspace();

				try
				{
					ws.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
				}
				catch (final CoreException e)
				{
					return e.getStatus();
				}

				return Status.OK_STATUS;
			}
		};

		job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataLoader#writeIndex(com.aptana.editor.common.contentassist.
	 * MetadataReader)
	 */
	@Override
	protected void writeIndex(JSMetadataReader reader)
	{
		// reset old index. This basically wipes it without deleting it. This way any current refs to it won't be broken
		getIndexManager().resetIndex(URI.create(IJSIndexConstants.METADATA_INDEX_LOCATION));

		JSIndexWriter indexer = new JSIndexWriter();

		// TODO: The following should be done in the index writer, but this will introduce a dependency to
		// com.aptana.parsing in com.aptana.index.core
		Index index = getIndex();

		// write types
		for (TypeElement type : reader.getTypes())
		{
			indexer.writeType(index, type);
		}

		try
		{
			index.save();
		}
		catch (IOException e)
		{
			IdeLog.logError(JSCorePlugin.getDefault(), e);
		}
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}

	@Override
	protected Index getIndex()
	{
		return JSIndexQueryHelper.getJSCoreIndex();
	}
}
