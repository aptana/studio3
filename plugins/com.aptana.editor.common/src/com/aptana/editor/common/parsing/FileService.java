/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.parsing;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.IDocument;

import com.aptana.editor.common.outline.IParseListener;
import com.aptana.editor.common.validator.ValidationManager;
import com.aptana.parsing.IParseState;
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
	private ValidationManager fValidationManager;
	private boolean fHasValidParseResult;

	public FileService(String language)
	{
		this(language, new ParseState());
	}

	public FileService(String language, IParseState parseState)
	{
		this.fLanguage = language;
		this.fParseState = parseState;
		fValidationManager = new ValidationManager();
	}

	public void dispose()
	{
		fDocument = null;
		fParseState.clearEditState();
		fLastSourceHash = 0;
		fValidationManager.dispose();
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
		return getParseState().getParseResult();
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
	 * Return a flag indicating if the last parse was successful. If it was, then the parse result represents the result
	 * of that parse. If it was not, then the parse result is the result of the last successful parse
	 * 
	 * @return
	 */
	public boolean hasValidParseResult()
	{
		return fHasValidParseResult;
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
				// assume failure
				this.fHasValidParseResult = false;

				fLastSourceHash = sourceHash;
				fParseState.setEditState(source, null, 0, 0);

				try
				{
					ParserPoolFactory.parse(fLanguage, fParseState);

					// indicate current parse result is now valid
					this.fHasValidParseResult = true;

					// fire listeners
					for (IParseListener listener : listeners)
					{
						listener.parseFinished();
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					// not logging the parsing error here since the source could be in an intermediate state of being
					// edited by the user
				}

				fValidationManager.validate(source, fLanguage);
			}
		}
		else
		{
			// indicate failure
			this.fHasValidParseResult = false;
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
		fValidationManager.setDocument(document);
	}

	/**
	 * Sets the resource the file service is currently handling.
	 * 
	 * @param resource
	 *            should either be an {IResource} for workspace resource or {IUniformResource} for external resource
	 */
	public void setResource(Object resource)
	{
		fValidationManager.setResource(resource);
	}
}
