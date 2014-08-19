/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.index;

import java.net.URI;
import java.text.MessageFormat;
import java.util.List;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexWriter;
import com.aptana.js.core.IDebugScopes;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.JSTypeConstants;
import com.aptana.js.core.index.IJSIndexConstants;
import com.aptana.js.core.model.EventElement;
import com.aptana.js.core.model.FunctionElement;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.TypeElement;

public class JSIndexWriter extends IndexWriter
{
	/**
	 * getDocumentPath
	 * 
	 * @return
	 */
	protected URI getDocumentPath()
	{
		return URI.create(IJSIndexConstants.METADATA_FILE_LOCATION);
	}

	/**
	 * writeEvent
	 * 
	 * @param index
	 * @param event
	 * @param location
	 */
	protected void writeEvent(Index index, EventElement event, URI location)
	{
		// @formatter:off
		String value = StringUtil.join(
			IJSIndexConstants.DELIMITER,
			event.getOwningType(),
			event.getName(),
			this.serialize(event)
		);
		// @formatter:on

		if (IdeLog.isTraceEnabled(JSCorePlugin.getDefault(), IDebugScopes.INDEX_WRITES))
		{
			// @formatter:off
			String message = MessageFormat.format(
				"Writing event ''{0}.{1}'' from location ''{2}'' to index ''{3}''", //$NON-NLS-1$
				event.getOwningType(),
				event.getName(),
				location.toString(),
				index.toString()
			);
			// @formatter:on

			IdeLog.logTrace(JSCorePlugin.getDefault(), message, IDebugScopes.INDEX_WRITES);
		}

		index.addEntry(IJSIndexConstants.EVENT, value, location);
	}

	/**
	 * writeFunction
	 * 
	 * @param index
	 * @param function
	 * @param location
	 */
	protected void writeFunction(Index index, FunctionElement function, URI location)
	{
		// @formatter:off
		String value = StringUtil.join(
			IJSIndexConstants.DELIMITER,
			function.getOwningType(),
			function.getName(),
			this.serialize(function)
		);
		// @formatter:on

		if (IdeLog.isTraceEnabled(JSCorePlugin.getDefault(), IDebugScopes.INDEX_WRITES))
		{
			// @formatter:off
			String message = MessageFormat.format(
				"Writing function ''{0}.{1}'' from location ''{2}'' to index ''{3}''", //$NON-NLS-1$
				function.getOwningType(),
				function.getName(),
				location.toString(),
				index.toString()
			);
			// @formatter:on

			IdeLog.logTrace(JSCorePlugin.getDefault(), message, IDebugScopes.INDEX_WRITES);
		}

		index.addEntry(IJSIndexConstants.FUNCTION, value, location);
	}

	/**
	 * writeProperty
	 * 
	 * @param index
	 * @param property
	 * @param location
	 */
	public void writeProperty(Index index, PropertyElement property, URI location)
	{
		// @formatter:off
		String value = StringUtil.join(
			IJSIndexConstants.DELIMITER,
			property.getOwningType(),
			property.getName(),
			this.serialize(property)
		);
		// @formatter:on

		if (IdeLog.isTraceEnabled(JSCorePlugin.getDefault(), IDebugScopes.INDEX_WRITES))
		{
			// @formatter:off
			String message = MessageFormat.format(
				"Writing property ''{0}.{1}'' from location ''{2}'' to index ''{3}''", //$NON-NLS-1$
				property.getOwningType(),
				property.getName(),
				location.toString(),
				index.toString()
			);
			// @formatter:on

			IdeLog.logTrace(JSCorePlugin.getDefault(), message, IDebugScopes.INDEX_WRITES);
		}

		index.addEntry(IJSIndexConstants.PROPERTY, value, location);
	}

	/**
	 * writeType
	 * 
	 * @param index
	 * @param type
	 */
	public void writeType(Index index, TypeElement type)
	{
		this.writeType(index, type, this.getDocumentPath());
	}

	/**
	 * writeType
	 * 
	 * @param index
	 * @param type
	 * @param location
	 */
	public void writeType(Index index, TypeElement type, URI location)
	{
		if (index != null && type != null && location != null)
		{
			List<String> parentTypes = type.getParentTypes();
			String parentType;

			if (!parentTypes.isEmpty())
			{
				parentType = StringUtil.join(IJSIndexConstants.SUB_DELIMITER, parentTypes);
			}
			else if (!type.getName().equals(JSTypeConstants.OBJECT_TYPE))
			{
				parentType = JSTypeConstants.OBJECT_TYPE;
			}
			else
			{
				//
				parentType = StringUtil.EMPTY;
			}

			// calculate key value and add to index
			// @formatter:off
			String value = StringUtil.join(
				IJSIndexConstants.DELIMITER,
				type.getName(),
				parentType,
				type.getDescription(),
				type.isDeprecated() ? '1' : '0'
			);
			// @formatter:on

			if (IdeLog.isTraceEnabled(JSCorePlugin.getDefault(), IDebugScopes.INDEX_WRITES))
			{
				// @formatter:off
				String message = MessageFormat.format(
					"Writing type ''{0}'' from location ''{1}'' to index ''{2}''", //$NON-NLS-1$
					type.getName(),
					location.toString(),
					index.toString()
				);
				// @formatter:on

				IdeLog.logTrace(JSCorePlugin.getDefault(), message, IDebugScopes.INDEX_WRITES);
			}

			index.addEntry(IJSIndexConstants.TYPE, value, location);

			// write properties
			for (PropertyElement property : type.getProperties())
			{
				if (!property.getOwningType().equals(type.getName()))
				{
					// Should we enforce that the property's owning type must match the type name?
					IdeLog.logWarning(JSCorePlugin.getDefault(), MessageFormat.format(
							"Property is attached to type {0}, but states it's owning type is {1}", type.getName(),
							property.getOwningType()));
				}
				if (property instanceof FunctionElement)
				{
					this.writeFunction(index, (FunctionElement) property, location);
				}
				else
				{
					this.writeProperty(index, property, location);
				}
			}

			// write events
			for (EventElement event : type.getEvents())
			{
				this.writeEvent(index, event, location);
			}
		}
	}
}
