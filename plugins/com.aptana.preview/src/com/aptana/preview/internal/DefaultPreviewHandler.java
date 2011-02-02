/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.preview.internal;

import java.net.MalformedURLException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;

import com.aptana.preview.Activator;
import com.aptana.preview.IPreviewHandler;
import com.aptana.preview.PreviewConfig;
import com.aptana.preview.SourceConfig;

/**
 * @author Max Stepanov
 * 
 */
public final class DefaultPreviewHandler implements IPreviewHandler {

	private static DefaultPreviewHandler instance;
	private IContentType contentTypeHTML;

	/**
	 * 
	 */
	private DefaultPreviewHandler() {
		contentTypeHTML = Platform.getContentTypeManager().findContentTypeFor("index.html"); //$NON-NLS-1$
	}

	public static DefaultPreviewHandler getInstance() {
		if (instance == null) {
			instance = new DefaultPreviewHandler();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aptana.preview.IPreviewHandler#handle(com.aptana.preview.SourceConfig
	 * )
	 */
	public PreviewConfig handle(SourceConfig config) throws CoreException {
		if (contentTypeHTML != null && contentTypeHTML.isAssociatedWith(config.getLocation().lastSegment())) {
			try {
				IPath location = config.getLocation();
				if (config.getProject() != null) {
					location = ResourcesPlugin.getWorkspace().getRoot().getFile(location).getLocation();
				}
				return new PreviewConfig(location.toFile().toURI().toURL());
			} catch (MalformedURLException e) {
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "", e)); //$NON-NLS-1$
			}
		}
		return null;
	}

}
