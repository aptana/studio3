package com.aptana.editor.common.parsing;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.IDocument;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.Messages;
import com.aptana.editor.common.outline.IParseListener;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;

public class FileService
{

	private IDocument fDocument;
	private IParser fParser;
	private IParseState fParseState;
	private Set<IParseListener> listeners = new HashSet<IParseListener>();

	public FileService()
	{
		fParseState = new ParseState();
	}

	public void parse()
	{
		if (fParser == null || fDocument == null)
		{
			return;
		}

		String source = fDocument.get();
		fParseState.setEditState(source, source, 0, 0);
		try
		{
			fParser.parse(fParseState);
			for (IParseListener listener : listeners)
				listener.parseFinished();
		}
		catch (Exception e)
		{
			CommonEditorPlugin.logError(Messages.FileService_FailedToParse, e);
		}
	}

	public IParseState getParseState()
	{
		return fParseState;
	}

	public IParseNode getParseResult()
	{
		return fParseState.getParseResult();
	}

	public void setParser(IParser parser)
	{
		fParser = parser;
	}

	public void setParseState(IParseState parseState)
	{
		fParseState = parseState;
	}

	public void setDocument(IDocument document)
	{
		fDocument = document;
	}

	public void addListener(IParseListener listener)
	{
		listeners.add(listener);		
	}

	public void removeListener(IParseListener fListener)
	{
		listeners.remove(fListener);
	}
}
