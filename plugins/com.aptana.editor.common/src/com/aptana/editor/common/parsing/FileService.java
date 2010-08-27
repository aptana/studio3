package com.aptana.editor.common.parsing;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.IDocument;

import com.aptana.editor.common.outline.IParseListener;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;

public class FileService
{
	private IDocument fDocument;
	private IParseState fParseState;
	private int fLastSourceHash;
	private Set<IParseListener> listeners = new HashSet<IParseListener>();
	private String fLanguage;

	public FileService(String language)
	{
		this(language, new ParseState());
	}

	public FileService(String language, IParseState parseState)
	{
		this.fLanguage = language;
		this.fParseState = parseState;
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
	 * Parse.<br>
	 * This call is just like calling {@link #parse(boolean)} with false.
	 */
	public void parse()
	{
		parse(false);
	}

	/**
	 * Parse, with an option to force a parsing even when the source did not change.
	 * 
	 * @param force
	 */
	public synchronized void parse(boolean force)
	{
		if (fLanguage != null && fDocument != null)
		{
			String source = fDocument.get();
			int sourceHash = source.hashCode();

			if (force || sourceHash != fLastSourceHash)
			{
				fLastSourceHash = sourceHash;

				IParserPool pool = ParserPoolFactory.getInstance().getParserPool(fLanguage);
				if (pool != null)
				{
					IParser parser = pool.checkOut();
					if (parser != null)
					{
						// TODO: at some point, we'll want to use this call to indicate the
						// actual edit with the theory that we'll be able to perform
						// incremental lexing and parsing based on that info.
						fParseState.setEditState(source, source, 0, 0);
		
						try
						{
							parser.parse(fParseState);
		
							for (IParseListener listener : listeners)
							{
								listener.parseFinished();
							}
						}
						catch (Exception e)
						{
							// not logging the parsing error here since the source could be in an intermediate state of being
							// edited
							// by
							// the user
						}
						pool.checkIn(parser);
					}
				}
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
}
