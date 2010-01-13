package com.aptana.editor.common;

import org.eclipse.jface.text.IDocument;

import com.aptana.parsing.IParser;
import com.aptana.parsing.ast.IParseNode;

public class FileService
{

	private IDocument fDocument;
	private IParser fParser;
	private IParseNode fResult;

	public FileService()
	{
	}

	public void parse()
	{
		if (fParser == null || fDocument == null)
		{
			return;
		}

		String source = fDocument.get();
		try
		{
			fResult = fParser.parse(source);
		}
		catch (Exception e)
		{
			CommonEditorPlugin.logError(Messages.FileService_FailedToParse, e);
		}
	}

	public IParseNode getParseResult()
	{
		if (fResult == null)
		{
			// performs an initial parse
			parse();
		}
		return fResult;
	}

	public void setParser(IParser parser)
	{
		fParser = parser;
	}

	public void setDocument(IDocument document)
	{
		fDocument = document;
	}
}
