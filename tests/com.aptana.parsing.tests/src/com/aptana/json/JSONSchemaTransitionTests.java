/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

import junit.framework.TestCase;

/**
 * JSONSchemaTransitionTests
 */
public class JSONSchemaTransitionTests extends TestCase
{
	private static class EventResult
	{
		public final EventType event;
		public final Object value;
		public final boolean valid;
		
		public EventResult(EventType event, boolean valid)
		{
			this(event, null, valid);
		}
		
		public EventResult(EventType event, Object value, boolean valid)
		{
			this.event = event;
			this.value = value;
			this.valid = valid;
		}
	}
	
	/**
	 * testStates
	 * 
	 * @param state
	 */
	protected void testStates(State state, EventResult... results)
	{
		Context context = new Context();
		
		for (EventResult result : results)
		{
			try
			{
				state.transition(context, result.event, result.value);
				
				if (result.valid == false)
				{
					fail("Was supposed to fail, but passed");
				}
			}
			catch (IllegalStateException e)
			{
				if (result.valid)
				{
					fail("Was supposed to pass, but failed");
				}
			}
		}
	}
	
	public void testNullStates()
	{
		State schemaNull = new SchemaNull();
		
		testStates( //
			schemaNull, //
			new EventResult(EventType.END_ARRAY, false) //
		);
	}
}
