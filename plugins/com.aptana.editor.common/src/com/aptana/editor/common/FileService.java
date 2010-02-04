package com.aptana.editor.common;

import org.eclipse.jface.text.IDocument;

import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;

public class FileService
{

	private IDocument fDocument;
	private IParser fParser;
	private IParseState fParseState;

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
		}
		catch (Exception e)
		{
			CommonEditorPlugin.logError(Messages.FileService_FailedToParse, e);
		}
	}

	public IParseNode getParseResult()
	{
		IParseNode result = fParseState.getParseResult();
		if (result == null)
		{
			// performs an initial parse
			parse();
			result = fParseState.getParseResult();
		}
		return result;
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
}
