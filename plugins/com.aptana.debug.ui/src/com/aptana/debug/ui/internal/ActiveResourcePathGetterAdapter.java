/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.debug.ui.internal;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PlatformUI;

import com.aptana.core.resources.UniformResourceStorage;
import com.aptana.debug.core.IActiveResourcePathGetterAdapter;
import com.aptana.debug.ui.DebugUiPlugin;

/**
 * @author Max Stepanov
 */
public class ActiveResourcePathGetterAdapter implements IActiveResourcePathGetterAdapter {

	/*
	 * @see com.aptana.debug.core.IActiveResourcePathGetterAdapter#getActiveResource()
	 */
	public IResource getActiveResource() {
		final IResource[] result = new IResource[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.getActiveEditor();
				if (editorPart != null) {
					IEditorInput editorInput = editorPart.getEditorInput();
					if (editorInput instanceof IFileEditorInput) {
						result[0] = ((IFileEditorInput) editorInput).getFile();
					}
				}
			}
		});
		if (result[0] != null) {
			result[0] = findConnectedResource(result[0]);
		}
		return result[0];
	}

	/*
	 * @see com.aptana.debug.core.IActiveResourcePathGetterAdapter#getActiveResourcePath()
	 */
	public IPath getActiveResourcePath() {
		final IPath[] result = new IPath[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.getActiveEditor();
				if (editorPart != null) {
					IEditorInput editorInput = editorPart.getEditorInput();
					if (editorInput instanceof IFileEditorInput) {
						result[0] = ((IFileEditorInput) editorInput).getFile().getLocation();
					} else if (editorInput instanceof IPathEditorInput) {
						result[0] = ((IPathEditorInput) editorInput).getPath();
					}
				}
			}
		});
		if (result[0] != null) {
			result[0] = findConnectedPath(result[0]);
		}
		return result[0];
	}

	/*
	 * @see com.aptana.debug.core.IActiveResourcePathGetterAdapter#getActiveResourceURL()
	 */
	public URL getActiveResourceURL() {
		final URL[] result = new URL[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.getActiveEditor();
				if (editorPart != null) {
					IEditorInput editorInput = editorPart.getEditorInput();
					if (editorInput instanceof UniformResourceStorageEditorInput) {
						IStorage storage = ((UniformResourceStorageEditorInput) editorInput).getStorage();
						if (storage instanceof UniformResourceStorage) {
							try {
								result[0] = ((UniformResourceStorage) storage).getURI().toURL();
							} catch (MalformedURLException e) {
								DebugUiPlugin.log(e);
							}
						}
					}
				}
			}
		});
		return result[0];
	}

	/*
	 * findConnectedResource
	 * 
	 * @param resource
	 * @return IResource
	 */
	private IResource findConnectedResource(IResource resource) {
		IPath location = resource.getLocation();
		IPath path = findConnectedPath(location);
		if (path != location) {
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
			if (file != null) {
				return file;
			}
		}
		return resource;
	}

	/*
	 * findConnectedPath
	 * 
	 * @param path
	 * @return IPath
	 */
	private IPath findConnectedPath(IPath path) {
		/* TODO
		 * String uri = path.toFile().toURI().toString(); // XXX: workaround for
		 * file:// issue in Profile if (uri.startsWith("file:/") &&
		 * uri.charAt(6) != '/') { //$NON-NLS-1$ uri = "file://" +
		 * uri.substring(6); //$NON-NLS-1$ } ProfileManager profileManager =
		 * UnifiedEditorsPlugin.getDefault().getProfileManager(); Profile
		 * profile = profileManager.getCurrentProfile(); if
		 * (profile.containsURI(uri) >= 0) { uri = profile.getURI(); if (uri !=
		 * null && uri.length() > 0) { try { File osFile = new File(new
		 * URI(uri).getSchemeSpecificPart()); return new
		 * Path(osFile.getCanonicalPath()); } catch (URISyntaxException e) {
		 * DebugUiPlugin.log(e); } catch (IOException e) { DebugUiPlugin.log(e);
		 * } } }
		 */
		return path;
	}

	@SuppressWarnings("rawtypes")
	public static class Factory implements IAdapterFactory {

		/*
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
		 */
		public Object getAdapter(Object adaptableObject, Class adapterType) {
			if (adapterType == IActiveResourcePathGetterAdapter.class) {
				return new ActiveResourcePathGetterAdapter();
			}
			return null;
		}

		/*
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
		 */
		public Class[] getAdapterList() {
			return new Class[] { IActiveResourcePathGetterAdapter.class };
		}
	}

}
