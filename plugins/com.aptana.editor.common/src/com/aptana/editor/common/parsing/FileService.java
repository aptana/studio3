package com.aptana.editor.common.parsing;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.IDocument;

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

	/**
	 * FileService
	 */
	public FileService()
	{
		fParseState = new ParseState();
	}

	/**
	 * addListener
	 * 
	 * @param listener
	 */
	public void addListener(IParseListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * getParseResult
	 * 
	 * @return
	 */
	public IParseNode getParseResult()
	{
		return fParseState.getParseResult();
	}

	/**
	 * getParseState
	 * 
	 * @return
	 */
	public IParseState getParseState()
	{
		return fParseState;
	}

	/**
	 * parse
	 */
	public void parse()
	{
		if (fParser != null && fDocument != null)
		{
			String source = fDocument.get();

			// TODO: at some point, we'll want to use this call to indicate the
			// actual edit with the theory that we'll be able to perform
			// incremental lexing and parsing based on that info.
			fParseState.setEditState(source, source, 0, 0);

			try
			{
				fParser.parse(fParseState);

				for (IParseListener listener : listeners)
				{
					listener.parseFinished();
				}
			}
			catch (Exception e)
			{
				// not logging the parsing error here since the source could be in an intermediate state of being edited
				// by
				// the user
			}
		}
	}

	/**
	 * removeListener
	 * 
	 * @param fListener
	 */
	public void removeListener(IParseListener fListener)
	{
		listeners.remove(fListener);
	}

	/**
	 * setDocument
	 * 
	 * @param document
	 */
	public void setDocument(IDocument document)
	{
		fDocument = document;
	}

	/**
	 * setParser
	 * 
	 * @param parser
	 */
	public void setParser(IParser parser)
	{
		fParser = parser;
	}

	/**
	 * setParseState
	 * 
	 * @param parseState
	 */
	public void setParseState(IParseState parseState)
	{
		fParseState = parseState;
	}
}
