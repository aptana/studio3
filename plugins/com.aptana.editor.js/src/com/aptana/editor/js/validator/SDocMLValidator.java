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

import com.aptana.editor.common.validator.IValidationItem;
import com.aptana.editor.common.validator.IValidationManager;
import com.aptana.editor.common.validator.IValidator;
import com.aptana.editor.js.contentassist.index.JSMetadataReader;
import com.aptana.sax.IValidatingReaderLogger;

public class SDocMLValidator implements IValidator
{
	private class Collector implements IValidatingReaderLogger
	{
		private URI path;
		private IValidationManager manager;
		private List<IValidationItem> items = new ArrayList<IValidationItem>();

		/**
		 * Collector
		 * 
		 * @param manager
		 * @param path
		 */
		public Collector(IValidationManager manager, URI path)
		{
			this.path = path;
			this.manager = manager;
			this.items = new ArrayList<IValidationItem>();
		}

		/**
		 * getItems
		 * 
		 * @return
		 */
		public List<IValidationItem> getItems()
		{
			return this.items;
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.sax.IValidatingReaderLogger#logError(java.lang.String, int, int)
		 */
		public void logError(String message, int line, int column)
		{
			items.add(manager.addError(message, line, column, 0, this.path));
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
			items.add(manager.addWarning(message, line, column, 0, this.path));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.validator.IValidator#validate(java.lang.String, java.net.URI,
	 * com.aptana.editor.common.validator.IValidationManager)
	 */
	public List<IValidationItem> validate(String source, URI path, IValidationManager manager)
	{
		JSMetadataReader reader = new JSMetadataReader();
		ByteArrayInputStream input = new ByteArrayInputStream(source.getBytes());
		Collector collector = new Collector(manager, path);

		reader.setLogger(collector);

		try
		{
			reader.loadXML(input);
		}
		catch (Exception e)
		{
		}

		return collector.getItems();
	}
}
