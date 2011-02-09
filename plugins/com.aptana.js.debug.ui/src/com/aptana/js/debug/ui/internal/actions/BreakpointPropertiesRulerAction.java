/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.actions;

import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.core.util.StringUtil;

/**
 * @author Max Stepanov
 */
public class BreakpointPropertiesRulerAction extends AbstractBreakpointRulerAction {
	/**
	 * Creates the action to enable/disable breakpoints
	 * 
	 * @param editor
	 * @param info
	 */
	public BreakpointPropertiesRulerAction(ITextEditor editor, IVerticalRulerInfo info) {
		setInfo(info);
		setTextEditor(editor);
		setText(StringUtil.ellipsify(Messages.BreakpointPropertiesRulerAction_BreakpointProperties));
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		if (getBreakpoint() != null) {
			PropertyDialogAction action = new PropertyDialogAction(new SameShellProvider(getTextEditor()
					.getEditorSite().getShell()), new ISelectionProvider() {
				public void addSelectionChangedListener(ISelectionChangedListener listener) {
				}

				public ISelection getSelection() {
					return new StructuredSelection(getBreakpoint());
				}

				public void removeSelectionChangedListener(ISelectionChangedListener listener) {
				}

				public void setSelection(ISelection selection) {
				}
			});
			action.run();
		}
	}

	/**
	 * @see org.eclipse.ui.texteditor.IUpdate#update()
	 */
	public void update() {
		setBreakpoint(determineBreakpoint());
		if (getBreakpoint() == null || !(getBreakpoint() instanceof ILineBreakpoint)) {
			setBreakpoint(null);
			setEnabled(false);
			return;
		}
		setEnabled(true);
	}
}
