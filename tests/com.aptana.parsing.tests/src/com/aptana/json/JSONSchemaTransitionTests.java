/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

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

		public EventResult(EventType event, Object value, boolean valid)
		{
			this.event = event;
			this.value = value;
			this.valid = valid;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return "event=" + event.name() + ", value=" + value + ", expected=" + valid;
		}
	}

	/**
	 * createGoodList
	 * 
	 * @param value
	 * @param events
	 * @return
	 */
	private List<EventResult> createGoodList(String value, EnumSet<EventType> events)
	{
		List<EventResult> result = new ArrayList<EventResult>();

		for (EventType event : EnumSet.allOf(EventType.class))
		{
			boolean valid = events.contains(event);
			EventResult er = new EventResult(event, value, valid);

			result.add(er);
		}

		return result;
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
					fail("Was supposed to fail, but passed: " + result);
				}
			}
			catch (IllegalStateException e)
			{
				if (result.valid)
				{
					fail("Was supposed to pass, but failed: " + result);
				}
			}
		}
	}

	/**
	 * testPrimitive
	 * 
	 * @param stateClass
	 * @param goodValue
	 * @param badValue
	 */
	protected void testPrimitive(Class<? extends State> stateClass, String goodValue, String badValue)
	{
		try
		{
			State state = stateClass.newInstance();
			List<EventResult> testList = this.createGoodList( //
				goodValue, //
				EnumSet.of(EventType.PRIMITIVE) //
				);

			testList.add(new EventResult(EventType.PRIMITIVE, badValue, false));

			testStates( //
				state, //
				testList.toArray(new EventResult[testList.size()]) //
			);

		}
		catch (InstantiationException e)
		{
			fail(e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * testFalseStates
	 */
	public void testFalseStates()
	{
		this.testPrimitive(SchemaFalse.class, "false", "FALSE");
	}

	/**
	 * testNullStates
	 */
	public void testNullStates()
	{
		this.testPrimitive(SchemaNull.class, "null", "NULL");
	}

	/**
	 * testNumberStates
	 */
	public void testNumberStates()
	{
		this.testPrimitive(SchemaNumber.class, "10.1", "10g");
	}

	/**
	 * testStringStates
	 */
	public void testStringStates()
	{
		this.testPrimitive(SchemaString.class, "abc", null);
	}

	/**
	 * testTrueStates
	 */
	public void testTrueStates()
	{
		this.testPrimitive(SchemaTrue.class, "true", "TRUE");
	}

	/**
	 * testObjectStates
	 */
	public void testObjectStates()
	{

	}

	/**
	 * testArrayStates
	 */
	public void testArrayStates()
	{

	}
}
