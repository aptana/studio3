/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.preview;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.ui.IEditorInput;

/**
 * @author Max Stepanov
 * 
 */
public final class SourceConfig {

	private IEditorInput editorInput;
	private IProject project;
	private IPath location;
	private String content;
	private IFileStore fileStore;
	private IContentType contentType;

	/**
	 * 
	 */
	public SourceConfig(IEditorInput editorInput, IProject project, IPath location, String content, IContentType contentType) {
		this.editorInput = editorInput;
		this.project = project;
		this.location = location;
		this.content = content;
		this.contentType = contentType;
		IPath path = location;
		if (project != null) {
			path = ResourcesPlugin.getWorkspace().getRoot().getFile(location).getLocation();
		}
		this.fileStore = EFS.getLocalFileSystem().getStore(path);
	}

	/**
	 * @return the editorInput
	 */
	public IEditorInput getEditorInput() {
		return editorInput;
	}

	/**
	 * @return the project
	 */
	public IProject getProject() {
		return project;
	}

	/**
	 * @return the location
	 */
	public IPath getLocation() {
		return location;
	}

	/**
	 * @return the fileStore
	 */
	public IFileStore getFileStore() {
		return fileStore;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @return the contentType
	 */
	public IContentType getContentType() {
		return contentType;
	}

}
