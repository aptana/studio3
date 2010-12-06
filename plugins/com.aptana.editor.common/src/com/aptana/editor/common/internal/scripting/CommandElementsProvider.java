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
package com.aptana.editor.common.internal.scripting;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.scripting.commands.CommandExecutionUtils;
import com.aptana.editor.common.scripting.commands.TextEditorUtils;
import com.aptana.scripting.keybindings.ICommandElementsProvider;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.InvocationType;
import com.aptana.scripting.model.SnippetElement;
import com.aptana.scripting.model.filters.IModelFilter;
import com.aptana.scripting.model.filters.ScopeFilter;

public class CommandElementsProvider implements ICommandElementsProvider
{

	private final ITextEditor textEditor;
	private final ITextViewer textViewer;

	public CommandElementsProvider(ITextEditor textEditor, ITextViewer textViewer)
	{
		this.textEditor = textEditor;
		this.textViewer = textViewer;
	}

	public void execute(CommandElement commandElement)
	{
		CommandResult commandResult = CommandExecutionUtils.executeCommand(commandElement, InvocationType.KEY_BINDING, textViewer, textEditor);
		CommandExecutionUtils.processCommandResult(commandElement, commandResult, textEditor);
	}

	public List<CommandElement> getCommandElements(KeySequence keySequence)
	{
		List<CommandElement> commandElements = new LinkedList<CommandElement>();
		int caretOffset = TextEditorUtils.getCaretOffset(textEditor);

		try
		{
			String contentTypeAtOffset = CommonEditorPlugin.getDefault().getDocumentScopeManager().getScopeAtOffset(textViewer, caretOffset);
			IModelFilter filter = new ScopeFilter(contentTypeAtOffset);

			List<CommandElement> commandsFromScope = BundleManager.getInstance().getExecutableCommands(filter);
			for (CommandElement commandElement : commandsFromScope)
			{
				if (commandElement instanceof SnippetElement)
				{
					continue;
				}
				KeySequence[] commandElementKeySequences = commandElement.getKeySequences();
				for (KeySequence commandElementKeySequence : commandElementKeySequences)
				{
					if (keySequence.equals(commandElementKeySequence))
					{
						commandElements.add(commandElement);
						break;
					}
				}
			}
		}
		catch (BadLocationException e)
		{
			CommonEditorPlugin.logError(e);
		}
		return commandElements;
	}

	/* (non-Javadoc)
	 * @see com.aptana.scripting.keybindings.ICommandElementsProvider#getCommandElementsPopupLocation()
	 */
	public Point getCommandElementsPopupLocation()
	{
		Object control = textEditor.getAdapter(Control.class);
		if (control instanceof StyledText)
		{
			StyledText textWidget = (StyledText) control;
			int caretOffset = textWidget.getCaretOffset();
			Point locationAtOffset = textWidget.getLocationAtOffset(caretOffset);
			locationAtOffset = textWidget.toDisplay(locationAtOffset.x, locationAtOffset.y
					+ textWidget.getLineHeight(caretOffset) + 2);
			return locationAtOffset;
		}
		return null;
	}

}
