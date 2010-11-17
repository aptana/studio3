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
