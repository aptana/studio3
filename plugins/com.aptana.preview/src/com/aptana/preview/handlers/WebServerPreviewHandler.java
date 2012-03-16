/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.preview.handlers;

import java.net.MalformedURLException;
import java.net.URI;

import org.eclipse.core.runtime.CoreException;

import com.aptana.core.IURIMapper;
import com.aptana.preview.PreviewPlugin;
import com.aptana.preview.IPreviewHandler;
import com.aptana.preview.PreviewConfig;
import com.aptana.preview.ProjectPreviewUtil;
import com.aptana.preview.SourceConfig;
import com.aptana.webserver.core.WebServerCorePlugin;

/**
 * @author Max Stepanov
 * 
 */
public class WebServerPreviewHandler implements IPreviewHandler {

	/*
	 * (non-Javadoc)
	 * @see com.aptana.preview.IPreviewHandler#handle(com.aptana.preview.SourceConfig)
	 */
	public PreviewConfig handle(SourceConfig config) throws CoreException {
		if (config.getContent() != null) {
			return null; // we're not handling content preview requests
		}
		IURIMapper serverConfiguration = ProjectPreviewUtil.getServerConfiguration(config.getProject());
		try {
			if (serverConfiguration != null) {
				URI uri = serverConfiguration.resolve(config.getFileStore());
				if (uri != null) {
					return new PreviewConfig(uri.toURL());
				}
			} else {
				for (IURIMapper configuration : WebServerCorePlugin.getDefault().getServerManager().getServers()) {
					URI uri = configuration.resolve(config.getFileStore());
					if (uri != null) {
						return new PreviewConfig(uri.toURL());
					}
				}
			}
		} catch (MalformedURLException e) {
			PreviewPlugin.log(e);
		}
		return null;
	}
}
