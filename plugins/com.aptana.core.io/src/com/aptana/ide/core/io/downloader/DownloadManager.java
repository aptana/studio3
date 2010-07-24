package com.aptana.ide.core.io.downloader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.ide.core.io.CoreIOPlugin;

/**
 * A file download manager.<br>
 * This manager can accept multiple files URLs to download. It then connects and retrieve the content while providing
 * progress information with time estimations.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class DownloadManager
{
	private List<ContentDownloadRequest> downloads;

	/**
	 * Constructs a new DownloadManager
	 */
	public DownloadManager()
	{
		downloads = new ArrayList<ContentDownloadRequest>();
	}

	public void addURL(URL url)
	{
		if (url != null)
		{
			this.downloads.add(new ContentDownloadRequest(url));
		}
	}
	
	public void addURLs(List<URL> urls)
	{
		if (urls != null)
		{
			for (URL url : urls)
			{
				if (url != null)
				{
					this.downloads.add(new ContentDownloadRequest(url));
				}
			}
		}
	}
	
	/**
	 * Starts the downloads.
	 * Returns a status for the overall operation. 
	 */
	public IStatus start(IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Downloading Content...", 1000);
		try {
			if (downloads.isEmpty())
				return Status.OK_STATUS;
			// TODO
			/*IQueryable<IArtifactRepository> repoQueryable = provContext.getArtifactRepositories(subMonitor.newChild(250));
			IQuery<IArtifactRepository> all = new ExpressionMatchQuery<IArtifactRepository>(IArtifactRepository.class, ExpressionUtil.TRUE_EXPRESSION);
			IArtifactRepository[] repositories = repoQueryable.query(all, subMonitor.newChild(250)).toArray(IArtifactRepository.class);
			if (repositories.length == 0)
				return new Status(IStatus.ERROR, EngineActivator.ID, Messages.download_no_repository, new Exception(Collect.NO_ARTIFACT_REPOSITORIES_AVAILABLE));
			fetch(repositories, subMonitor.newChild(500));*/
			return getStatus(monitor);
		} finally {
			subMonitor.done();
		}
	}
	
	private IStatus getStatus(IProgressMonitor monitor) {
		if (monitor != null && monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		if (downloads.isEmpty()) {
			return Status.OK_STATUS;
		}

		MultiStatus result = new MultiStatus(CoreIOPlugin.PLUGIN_ID, IStatus.OK, null, null);
		for (ContentDownloadRequest request : downloads) {
			IStatus failed = request.getResult();
			if (failed != null && !failed.isOK())
				result.add(failed);
		}
		return result;
	}
}
