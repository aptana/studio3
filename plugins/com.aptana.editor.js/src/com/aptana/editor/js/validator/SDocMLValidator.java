/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.validator;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.aptana.core.build.IProblem;
import com.aptana.editor.common.validator.IValidationManager;
import com.aptana.editor.common.validator.IValidator;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.contentassist.index.JSMetadataReader;
import com.aptana.sax.IValidatingReaderLogger;

public class SDocMLValidator implements IValidator
{
	private static class LogCollector implements IValidatingReaderLogger
	{
		private URI path;
		private IValidationManager manager;
		private List<IProblem> items = new ArrayList<IProblem>();

		/**
		 * Collector
		 * 
		 * @param manager
		 * @param path
		 */
		private LogCollector(IValidationManager manager, URI path, List<IProblem> items)
		{
			this.path = path;
			this.manager = manager;
			this.items = items;
		}

		/**
		 * addItem
		 * 
		 * @param item
		 */
		public void addItem(IProblem item)
		{
			items.add(item);
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.sax.IValidatingReaderLogger#logError(java.lang.String, int, int)
		 */
		public void logError(String message, int line, int column)
		{
			this.addItem(manager.createError(message, line, column, 0, this.path));
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.sax.IValidatingReaderLogger#logInfo(java.lang.String, int, int)
		 */
		public void logInfo(String message, int line, int column)
		{
			// not supported?
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.sax.IValidatingReaderLogger#logWarning(java.lang.String, int, int)
		 */
		public void logWarning(String message, int line, int column)
		{
			this.addItem(manager.createWarning(message, line, column, 0, this.path));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.validator.IValidator#validate(java.lang.String, java.net.URI,
	 * com.aptana.editor.common.validator.IValidationManager)
	 */
	public List<IProblem> validate(String source, URI path, IValidationManager manager)
	{
		List<IProblem> items = new ArrayList<IProblem>();
		JSMetadataReader reader = new JSMetadataReader();
		ByteArrayInputStream input = new ByteArrayInputStream(source.getBytes()); // $codepro.audit.disable closeWhereCreated
		LogCollector collector = new LogCollector(manager, path, items);

		manager.addParseErrors(items, IJSConstants.CONTENT_TYPE_JS);
		reader.setLogger(collector);

		try
		{
			reader.loadXML(input);
		}
		catch (Exception e)
		{
			collector.addItem(manager.createError(e.getMessage(), 0, 0, 0, path));
		}
		return items;
	}
}
