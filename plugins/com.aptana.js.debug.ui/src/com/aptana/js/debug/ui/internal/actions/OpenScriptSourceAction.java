/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.actions;

import java.net.URI;
import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.aptana.js.debug.core.model.IJSScriptElement;
import com.aptana.ui.util.UIUtils;
import com.aptana.debug.ui.DebugUiPlugin;
import com.aptana.debug.ui.SourceDisplayUtil;

/**
 * @author Max Stepanov
 */
public class OpenScriptSourceAction extends SelectionProviderAction {
	private IJSScriptElement scriptElement;

	/**
	 * @param provider
	 */
	public OpenScriptSourceAction(ISelectionProvider provider) {
		super(provider, Messages.OpenScriptSourceAction_GoToFile);
		setToolTipText(Messages.OpenScriptSourceAction_GoToFileForScript);
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				"com.aptana.debug.ui", "icons/full/elcl16/gotoobj_tsk.gif")); //$NON-NLS-1$ //$NON-NLS-2$
		setEnabled(false);
	}

	/**
	 * @see org.eclipse.ui.actions.SelectionProviderAction#selectionChanged(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void selectionChanged(IStructuredSelection selection) {
		if (selection.size() == 1) {
			Object element = selection.getFirstElement();
			if (element instanceof IJSScriptElement) {
				scriptElement = (IJSScriptElement) element;
				URI location = scriptElement.getLocation();
				if (location != null) {
					setEnabled(true);
					int lineNumber = scriptElement.getBaseLine();
					Object sourceElement = DebugUITools.lookupSource(scriptElement, getSourceLocator(scriptElement))
							.getSourceElement();
					IEditorInput editorInput = SourceDisplayUtil.getEditorInput(sourceElement);
					if (editorInput != null) {
						IEditorPart editorPart = SourceDisplayUtil.findEditor(editorInput);
						if (editorPart != null) {
							SourceDisplayUtil.revealLineInEditor(editorPart, lineNumber);
						}
					}
					return;
				}
			}
		} else {
			scriptElement = null;
		}
		setEnabled(false);
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		if (scriptElement == null) {
			selectionChanged(getStructuredSelection());
			if (scriptElement == null) {
				return;
			}
		}
		URI location = scriptElement.getLocation();
		if (location == null) {
			return;
		}
		int lineNumber = scriptElement.getBaseLine();

		try {
			Object sourceElement = DebugUITools.lookupSource(scriptElement, getSourceLocator(scriptElement))
					.getSourceElement();
			IEditorInput editorInput = SourceDisplayUtil.getEditorInput(sourceElement);
			if (editorInput != null) {
				SourceDisplayUtil.openInEditor(editorInput, lineNumber);
				return;
			}
			MessageDialog.openInformation(UIUtils.getActiveShell(), Messages.OpenScriptSourceAction_Information,
					MessageFormat.format(Messages.OpenScriptSourceAction_SourceNotFoundFor_0, location.getPath()));
		} catch (CoreException e) {
			DebugUiPlugin.errorDialog(Messages.OpenScriptSourceAction_ExceptionWhileOpeningScriptSource, e);
		}
	}

	private ISourceLocator getSourceLocator(IDebugElement debugElement) {
		ISourceLocator sourceLocator = null;
		ILaunch launch = debugElement.getLaunch();
		if (launch != null) {
			sourceLocator = launch.getSourceLocator();
		}
		return sourceLocator;
	}
}
