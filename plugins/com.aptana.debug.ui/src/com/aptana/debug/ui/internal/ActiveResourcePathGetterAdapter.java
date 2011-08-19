/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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

import com.aptana.core.logging.IdeLog;
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
					} else {
						result[0] = (IResource) editorInput.getAdapter(IFile.class);
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
					IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput
							.getAdapter(IFileEditorInput.class);
					IPathEditorInput pathEditorInput = (IPathEditorInput) editorInput
							.getAdapter(IPathEditorInput.class);
					if (fileEditorInput != null) {
						result[0] = fileEditorInput.getFile().getLocation();
					} else if (pathEditorInput != null) {
						result[0] = pathEditorInput.getPath();
					}
					// TODO: for IURIEditorInput use ILocationProvider(Extension)
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
								IdeLog.logError(DebugUiPlugin.getDefault(), e);
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
	 * @param resource
	 * @return IResource
	 */
	private IResource findConnectedResource(IResource resource) {
		IPath location = resource.getLocation();
		IPath path = findConnectedPath(location);
		if (path != location) { // $codepro.audit.disable useEquals
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
			if (file != null) {
				return file;
			}
		}
		return resource;
	}

	/*
	 * findConnectedPath
	 * @param path
	 * @return IPath
	 */
	private IPath findConnectedPath(IPath path) {
		/*
		 * TODO String uri = path.toFile().toURI().toString(); // XXX: workaround for file:// issue in Profile if
		 * (uri.startsWith("file:/") && uri.charAt(6) != '/') { //$NON-NLS-1$ uri = "file://" + uri.substring(6);
		 * //$NON-NLS-1$ } ProfileManager profileManager = UnifiedEditorsPlugin.getDefault().getProfileManager();
		 * Profile profile = profileManager.getCurrentProfile(); if (profile.containsURI(uri) >= 0) { uri =
		 * profile.getURI(); if (uri != null && uri.length() > 0) { try { File osFile = new File(new
		 * URI(uri).getSchemeSpecificPart()); return new Path(osFile.getCanonicalPath()); } catch (URISyntaxException e)
		 * { DebugUiPlugin.log(e); } catch (IOException e) { DebugUiPlugin.log(e); } } }
		 */
		return path;
	}

	@SuppressWarnings("rawtypes")
	public static class Factory implements IAdapterFactory {

		/*
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
		 */
		public Object getAdapter(Object adaptableObject, Class adapterType) {
			if (IActiveResourcePathGetterAdapter.class.equals(adapterType)) {
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
