/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
