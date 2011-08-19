/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.ui.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;

import com.aptana.core.logging.IdeLog;
import com.aptana.debug.core.IEditorOpenAdapter;
import com.aptana.debug.ui.DebugUiPlugin;
import com.aptana.debug.ui.SourceDisplayUtil;

/**
 * @author Max Stepanov
 */
public class EditorOpenAdapter implements IEditorOpenAdapter {

	/*
	 * (non-Javadoc)
	 * @see com.aptana.debug.core.IEditorOpenAdapter#openInEditor(java.lang.Object)
	 */
	public void openInEditor(Object sourceElement) {
		final IEditorInput editorInput = SourceDisplayUtil.getEditorInput(sourceElement);
		if (editorInput != null) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					try {
						SourceDisplayUtil.openInEditor(editorInput, -1);
					} catch (CoreException e) {
						IdeLog.logError(DebugUiPlugin.getDefault(), e);

					}
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().forceActive();
				}
			});
		}
	}

	@SuppressWarnings("rawtypes")
	public static class Factory implements IAdapterFactory {

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
		 */
		public Object getAdapter(Object adaptableObject, Class adapterType) {
			if (IEditorOpenAdapter.class.equals(adapterType)) {
				return new EditorOpenAdapter();
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
		 */
		public Class[] getAdapterList() {
			return new Class[] { IEditorOpenAdapter.class };
		}
	}
}
