package com.aptana.editor.common;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;

import com.aptana.editor.common.scripting.commands.CommandExecutionUtils;
import com.aptana.editor.common.scripting.commands.TextEditorUtils;
import com.aptana.scripting.keybindings.ICommandElementsProvider;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.InvocationType;
import com.aptana.scripting.model.ScopeContainsFilter;
import com.aptana.scripting.model.SnippetElement;

public class CommandElementsProvider implements ICommandElementsProvider
{

	private final AbstractThemeableEditor abstractThemeableEditor;
	private final ITextViewer textViewer;

	public CommandElementsProvider(AbstractThemeableEditor abstractThemeableEditor, ITextViewer textViewer)
	{
		this.abstractThemeableEditor = abstractThemeableEditor;
		this.textViewer = textViewer;
	}

	@Override
	public void execute(CommandElement commandElement)
	{
		CommandResult commandResult = CommandExecutionUtils.executeCommand(commandElement, InvocationType.KEY_BINDING, textViewer, abstractThemeableEditor);
		CommandExecutionUtils.processCommandResult(commandElement, commandResult, abstractThemeableEditor);
	}

	@Override
	public List<CommandElement> getCommandElements(KeySequence keySequence)
	{
		List<CommandElement> commandElements = new LinkedList<CommandElement>();
		int caretOffset = TextEditorUtils.getCaretOffset(abstractThemeableEditor);
		IDocument document = abstractThemeableEditor.getDocumentProvider().getDocument(
				abstractThemeableEditor.getEditorInput());
		try
		{
			String contentTypeAtOffset = DocumentContentTypeManager.getInstance().getContentTypeAtOffset(document, caretOffset);
			ScopeContainsFilter filter = new ScopeContainsFilter(contentTypeAtOffset);
			
			CommandElement[] commandsFromScope = BundleManager.getInstance().getCommands(filter);
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
	@Override
	public Point getCommandElementsPopupLocation()
	{
		StyledText textWidget = abstractThemeableEditor.getSourceViewerNonFinal().getTextWidget();
		int caretOffset = textWidget.getCaretOffset();
		Point locationAtOffset = textWidget.getLocationAtOffset(caretOffset);
		locationAtOffset = textWidget.toDisplay(locationAtOffset.x, locationAtOffset.y
				+ textWidget.getLineHeight(caretOffset) + 2);
		return locationAtOffset;
	}

}
