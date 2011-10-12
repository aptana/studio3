/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.parsing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.text.IDocument;

import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.outline.IParseListener;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.common.validator.IValidationManager;
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
	private String contentType;
	private ValidationManager fValidationManager;
	private boolean fHasValidParseResult;

	private IPreferenceChangeListener fPreferenceListener = new IPreferenceChangeListener()
	{

		public void preferenceChange(PreferenceChangeEvent event)
		{
			if (event.getKey().endsWith(IPreferenceConstants.PARSE_ERROR_ENABLED))
			{
				parse(true, new NullProgressMonitor());
				validate();
			}
		}
	};

	public FileService(String contentType)
	{
		this(contentType, new ParseState());
	}

	public FileService(String contentType, IParseState parseState)
	{
		this.contentType = contentType;
		this.fParseState = parseState;
		fValidationManager = new ValidationManager(this);
		EclipseUtil.instanceScope().getNode(CommonEditorPlugin.PLUGIN_ID)
				.addPreferenceChangeListener(fPreferenceListener);
	}

	public void dispose()
	{
		EclipseUtil.instanceScope().getNode(CommonEditorPlugin.PLUGIN_ID)
				.removePreferenceChangeListener(fPreferenceListener);
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

	public IValidationManager getValidationManager()
	{
		return fValidationManager;
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
	 * 
	 * @deprecated use FileService.parse(IProgressMonitor monitor)
	 */
	public boolean parse()
	{
		return parse(null);
	}

	/**
	 * Parse.<br>
	 * This call is just like calling {@link #parse(boolean)} with false.
	 * 
	 * @param monitor
	 */
	public boolean parse(IProgressMonitor monitor)
	{
		return parse(false, monitor);
	}

	/**
	 * Parse, with an option to force a parsing even when the source did not change.
	 * 
	 * @param force
	 * @return true if parsing occurred, false otherwise
	 */
	public synchronized boolean parse(boolean force, IProgressMonitor monitor)
	{
		if (contentType != null && fDocument != null)
		{
			String source = fDocument.get();
			int sourceHash = source.hashCode();
			if (!force && sourceHash == fLastSourceHash)
			{
				return false;
			}

			// assume failure
			this.fHasValidParseResult = false;

			fLastSourceHash = sourceHash;
			fParseState.setEditState(source, null, 0, 0);
			fParseState.setProgressMonitor(monitor);

			// make a local copy to avoid potential concurrent modification errors.
			List<IParseListener> listenersCopy = new ArrayList<IParseListener>(this.listeners);

			try
			{

				// fire pre-parse listeners
				for (IParseListener listener : listenersCopy)
				{
					listener.beforeParse(fParseState);
				}

				ParserPoolFactory.parse(contentType, fParseState);

				// indicate current parse result is now valid
				this.fHasValidParseResult = true;

				// fire successful-parse listeners
				for (IParseListener listener : listenersCopy)
				{
					listener.parseCompletedSuccessfully();
				}
			}
			catch (Exception e)
			{
				// not logging the parsing error here since the source could be in an intermediate state of being
				// edited by the user
			}
			finally
			{
				// fire post-parse listeners
				for (IParseListener listener : listenersCopy)
				{
					listener.afterParse(fParseState);
				}
			}
		}
		else
		{
			// indicate failure
			this.fHasValidParseResult = false;
		}
		return true;
	}

	public void validate()
	{
		if (contentType != null && fDocument != null)
		{
			fValidationManager.validate(fDocument.get(), contentType);
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
