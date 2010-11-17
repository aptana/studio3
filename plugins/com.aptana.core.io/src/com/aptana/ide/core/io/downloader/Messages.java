package com.aptana.ide.core.io.downloader;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.core.io.downloader.messages"; //$NON-NLS-1$
	public static String ContentDownloadRequest_downloading;
	public static String ContentDownloadRequest_tempFilePrefix;
	public static String DownloadManager_downloadngContent;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
