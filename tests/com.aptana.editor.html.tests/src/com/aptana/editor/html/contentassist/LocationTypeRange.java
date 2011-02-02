/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist;

import com.aptana.editor.html.contentassist.HTMLContentAssistProcessor.LocationType;

class LocationTypeRange
{
	public final LocationType LocationType;
	public final int startingOffset;
	public final int endingOffset;

	public LocationTypeRange(LocationType LocationType, int offset)
	{
		this.LocationType = LocationType;
		this.startingOffset = this.endingOffset = offset;
	}

	public LocationTypeRange(LocationType LocationType, int startingOffset, int endingOffset)
	{
		this.LocationType = LocationType;
		this.startingOffset = startingOffset;
		this.endingOffset = endingOffset;
	}
}