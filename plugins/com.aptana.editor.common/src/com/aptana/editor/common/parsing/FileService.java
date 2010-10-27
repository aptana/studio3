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
package com.aptana.editor.common.parsing;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.IDocument;

import com.aptana.editor.common.outline.IParseListener;
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
				fParseState.setEditState(source, null, 0, 0);

				try
				{
					ParserPoolFactory.parse(fLanguage, fParseState);

					for (IParseListener listener : listeners)
					{
						listener.parseFinished();
					}
				}
				catch (Exception e)
				{
					// not logging the parsing error here since the source could be in an intermediate state of being
					// edited by the user
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
