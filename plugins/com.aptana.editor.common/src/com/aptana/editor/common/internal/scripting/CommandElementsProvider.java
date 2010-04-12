package com.aptana.editor.common.internal.scripting;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
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

	@Override
	public void execute(CommandElement commandElement)
	{
		CommandResult commandResult = CommandExecutionUtils.executeCommand(commandElement, InvocationType.KEY_BINDING, textViewer, textEditor);
		CommandExecutionUtils.processCommandResult(commandElement, commandResult, textEditor);
	}

	@Override
	public List<CommandElement> getCommandElements(KeySequence keySequence)
	{
		List<CommandElement> commandElements = new LinkedList<CommandElement>();
		int caretOffset = TextEditorUtils.getCaretOffset(textEditor);
		IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		try
		{
			String contentTypeAtOffset = CommonEditorPlugin.getDefault().getDocumentScopeManager().getScopeAtOffset(document, caretOffset);
			IModelFilter filter = new ScopeFilter(contentTypeAtOffset);

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
