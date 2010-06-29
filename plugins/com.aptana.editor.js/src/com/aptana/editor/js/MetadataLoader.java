package com.aptana.editor.js;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.editor.js.contentassist.JSIndexQueryHelper;
import com.aptana.editor.js.contentassist.index.JSIndexWriter;
import com.aptana.editor.js.contentassist.index.ScriptDocException;

public class MetadataLoader extends Job
{
	/**
	 * MetadataLoader
	 */
	public MetadataLoader()
	{
		super("Loading JS metadata...");
		
		setPriority(Job.LONG);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor)
	{
		JSIndexWriter indexer = new JSIndexWriter();
		
		this.loadMetadata(
			indexer,
			"/metadata/js_core.xml", //$NON-NLS-1$
			"/metadata/dom_0.xml", //$NON-NLS-1$
			"/metadata/dom_2.xml", //$NON-NLS-1$
			"/metadata/dom_3.xml", //$NON-NLS-1$
			"/metadata/dom_5.xml" //$NON-NLS-1$
		);
		
		indexer.writeToIndex(JSIndexQueryHelper.getIndex());
		
		return Status.OK_STATUS;
	}

	/**
	 * loadMetadata
	 * 
	 * @param resources
	 */
	private void loadMetadata(JSIndexWriter indexer, String... resources)
	{
		for (String resource : resources)
		{
			URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(resource), null);

			if (url != null)
			{
				InputStream stream = null;

				try
				{
					stream = url.openStream();

					indexer.loadXML(stream);
				}
				catch (IOException e)
				{
					Activator.logError(Messages.Activator_Error_Loading_Metadata, e);
				}
				catch (ScriptDocException e)
				{
					Activator.logError(Messages.Activator_Error_Loading_Metadata, e);
				}
				catch (Throwable t)
				{
					Activator.logError(Messages.Activator_Error_Loading_Metadata, t);
				}
				finally
				{
					if (stream != null)
					{
						try
						{
							stream.close();
						}
						catch (IOException e)
						{
						}
					}
				}
			}
		}
	}
}
