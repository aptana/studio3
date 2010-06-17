package com.aptana.editor.js.contentassist;

public class LocationTypeRange
{
	public final LocationType location;
	public final int startingOffset;
	public final int endingOffset;
	
	public LocationTypeRange(LocationType location, int offset)
	{
		this.location = location;
		this.startingOffset = this.endingOffset = offset;
	}
	
	public LocationTypeRange(LocationType location, int startingOffset, int endingOffset)
	{
		this.location = location;
		this.startingOffset = startingOffset;
		this.endingOffset = endingOffset;
	}
}
