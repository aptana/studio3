package com.aptana.ide.core.io.downloader;

import java.net.URL;

import org.eclipse.core.runtime.IStatus;

/**
 * A single content download request.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class ContentDownloadRequest
{
	private URL url;

	public ContentDownloadRequest(URL url)
	{
		this.url = url;
	}

	public IStatus getResult()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
