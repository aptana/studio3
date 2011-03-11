/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

/**
 * EventType
 */
public enum SchemaEventType
{
	START_PARSE,
	START_OBJECT,
	START_ARRAY,
	START_OBJECT_ENTRY,
	START_ARRAY_ENTRY,
	PRIMITIVE,
	END_OBJECT,
	END_ARRAY,
	END_OBJECT_ENTRY,
	END_ARRAY_ENTRY,
	END_PARSE;
}
