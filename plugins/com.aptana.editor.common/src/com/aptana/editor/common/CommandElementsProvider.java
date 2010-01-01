package com.aptana.editor.common;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;

import com.aptana.editor.common.scripting.commands.CommandExecutionUtils;
import com.aptana.editor.common.scripting.commands.TextEditorUtils;
import com.aptana.editor.common.tmp.ContentTypeTranslation;
import com.aptana.scripting.keybindings.ICommandElementsProvider;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.CommandResult;
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
		CommandResult commandResult = CommandExecutionUtils.executeCommand(commandElement, textViewer, abstractThemeableEditor);
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
			String contentTypeAtOffset = getContentTypeAtOffset(document, caretOffset);
			CommandElement[] commandsFromScope = BundleManager.getInstance().getCommandsFromScope(contentTypeAtOffset);
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

	private String getContentTypeAtOffset(IDocument document, int offset) throws BadLocationException
	{
		QualifiedContentType contentType = DocumentContentTypeManager.getInstance().getContentType(document, offset);
		if (contentType != null)
		{
			return ContentTypeTranslation.getDefault().translate(contentType).toString();
		}
		return document.getContentType(offset);
	}

}
