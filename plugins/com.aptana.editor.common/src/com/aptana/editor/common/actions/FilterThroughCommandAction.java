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
package com.aptana.editor.common.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import com.aptana.editor.common.scripting.commands.CommandExecutionUtils;
import com.aptana.editor.common.scripting.commands.FilterThroughCommandDialog;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.InvocationType;

public class FilterThroughCommandAction extends TextEditorAction {
	
	public static IAction create(ITextEditor textEditor) {
		return new FilterThroughCommandAction(ResourceBundle.getBundle(FilterThroughCommandAction.class.getName()),
				"FilterThroughCommandAction.", textEditor);	//$NON-NLS-1$
	}
	
	public static final String COMMAND_ID = "com.aptana.editor.common.scripting.commands.FilterThroughCommand";	//$NON-NLS-1$
	
	private boolean deactivated = false;

	protected FilterThroughCommandAction(ResourceBundle bundle, String prefix, ITextEditor editor) {
		super(bundle, prefix, editor);
		setActionDefinitionId(COMMAND_ID);
	}
	
	@Override
	public void run() {
		ITextEditor textEditor = getTextEditor();
		
		IWorkbenchWindow workbenchWindow = textEditor.getEditorSite().getWorkbenchWindow();
		// TODO: probably need to grab or generate a ENV map from a Command here
		//Map<String, String> environment = CommandExecutionUtils.computeEnvironment(textEditor);
		Map<String,String> environment = new HashMap<String,String>();
		
		FilterThroughCommandDialog filterThroughCommandDialog = new FilterThroughCommandDialog(workbenchWindow.getShell(), environment);
		if (filterThroughCommandDialog.open() == Window.OK) {
			CommandElement command = new CommandElement(null); // Use null value for path to create a one off command
			command.setInputType(filterThroughCommandDialog.getInputType().getName());
			command.setOutputType(filterThroughCommandDialog.getOuputType().getName());
			command.setInvoke(filterThroughCommandDialog.getCommand());
			CommandResult commandResult = CommandExecutionUtils.executeCommand(command, InvocationType.UNKNOWN, textEditor);
			CommandExecutionUtils.processCommandResult(command, commandResult, textEditor);
		}
	}

	void adjustHandledState() {
		if (isDeactivated()) {
			deactivate();
			return;
		}
		if (!getTextEditor().isEditable()) {
			deactivate();
			return;
		}
		activate();
	}

	boolean isDeactivated() {
		return deactivated;
	}

	void setDeactivated(boolean deactivated) {
		this.deactivated = deactivated;
		adjustHandledState();
	}

	void activate() {
		getTextEditor().setAction(COMMAND_ID, this);
	}
	
	void deactivate() {
		getTextEditor().setAction(COMMAND_ID, null);
	}
}
