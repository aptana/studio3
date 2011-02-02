/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.preview.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorPart;

import com.aptana.preview.Activator;
import com.aptana.preview.IEditorPreviewDelegate;

/**
 * @author Max Stepanov
 * 
 */
public final class Editors {

	private static final String EXTENSION_POINT_ID = Activator.PLUGIN_ID + ".editors"; //$NON-NLS-1$
	private static final String TAG_EDITOR = "editor"; //$NON-NLS-1$
	private static final String TAG_PREVIEW_DELEGATE = "previewDelegate"; //$NON-NLS-1$
	private static final String ATT_CLASS = "class"; //$NON-NLS-1$
	private static final String ATT_TARGET_ID = "targetId"; //$NON-NLS-1$

	private static Editors instance = null;
	private Map<String, IConfigurationElement> previewDelegates = new HashMap<String, IConfigurationElement>();

	/**
	 * 
	 */
	private Editors() {
		readExtensionRegistry();
	}

	public static Editors getInstance() {
		if (instance == null) {
			instance = new Editors();
		}
		return instance;
	}

	private void readExtensionRegistry() {
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
				EXTENSION_POINT_ID);
		for (int i = 0; i < elements.length; ++i) {
			readElement(elements[i], TAG_EDITOR);
		}
	}

	private void readElement(IConfigurationElement element, String elementName) {
		if (!elementName.equals(element.getName())) {
			return;
		}
		if (TAG_EDITOR.equals(element.getName())) {
			String targetId = element.getAttribute(ATT_TARGET_ID);
			if (targetId == null || targetId.length() == 0) {
				return;
			}
			IConfigurationElement[] children = element.getChildren(TAG_PREVIEW_DELEGATE);
			if (children != null && children.length == 1) {
				readElement(children[0], TAG_PREVIEW_DELEGATE);
			}

		}
		if (TAG_PREVIEW_DELEGATE.equals(element.getName())) {
			String clazz = element.getAttribute(ATT_CLASS);
			if (clazz == null || clazz.length() == 0) {
				return;
			}
			String targetId = ((IConfigurationElement) element.getParent()).getAttribute(ATT_TARGET_ID);
			previewDelegates.put(targetId, element);
		}
	}

	public IEditorPreviewDelegate getEditorPreviewDelegate(IEditorPart editorPart) {
		return getEditorPreviewDelegate(editorPart.getSite().getId());
	}

	public IEditorPreviewDelegate getEditorPreviewDelegate(String editorId) {
		IConfigurationElement element = previewDelegates.get(editorId);
		if (element != null) {
			try {
				return (IEditorPreviewDelegate) element.createExecutableExtension(ATT_CLASS);
			} catch (CoreException e) {
				Activator.log(e);
			}
		}
		return null;
	}

}
