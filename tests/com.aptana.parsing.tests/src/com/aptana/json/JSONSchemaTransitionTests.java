/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JSONSchemaTransitionTests
 */
@SuppressWarnings("nls")
public class JSONSchemaTransitionTests
{
	private static class EventResult
	{
		public final SchemaEventType event;
		public final Object value;
		public final boolean valid;

		public EventResult(SchemaEventType event, Object value, boolean valid)
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

	private static interface StateInitializer
	{
		void initialize(IState state);
	}

	private ISchemaContext _context;

	@Before
	public void setUp() throws Exception
	{
		this._context = new SchemaContext();
	}

	@After
	public void tearDown() throws Exception
	{
		this._context = null;
	}

	/**
	 * createGoodList
	 * 
	 * @param events
	 * @param value
	 * @return
	 */
	private List<EventResult> createGoodList(EnumSet<SchemaEventType> events, Object value)
	{
		List<EventResult> result = new ArrayList<EventResult>();

		for (SchemaEventType event : EnumSet.allOf(SchemaEventType.class))
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
	protected void testStates(IState state, EventResult... results)
	{
		this.testStates( //
				state, //
				new StateInitializer()
				{
					public void initialize(IState state)
					{
						state.enter();
					}
				}, //
				results //
		);
	}

	/**
	 * testStates
	 * 
	 * @param state
	 */
	@Test
	protected void testStates(IState state, StateInitializer initializer, EventResult... results)
	{
		for (EventResult result : results)
		{
			// reset the context
			this._context.reset();

			// initialize the state's state
			if (initializer != null)
			{
				initializer.initialize(state);
			}

			// try current event type test
			try
			{
				state.transition(this._context, result.event, result.value);

				if (result.valid == false)
				{
					fail("Was supposed to fail, but passed: " + result);
				}
			}
			catch (IllegalStateException e)
			{
				if (result.valid)
				{
					fail("Was supposed to pass, but failed: " + result + "\n" + e.getMessage());
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
	@Test
	protected void testPrimitive(Class<? extends IState> stateClass, Object goodValue, Object badValue)
	{
		try
		{
			IState state = stateClass.newInstance();
			List<EventResult> testList = this.createGoodList( //
					EnumSet.of(SchemaEventType.PRIMITIVE), //
					goodValue //
					);

			testList.add(new EventResult(SchemaEventType.PRIMITIVE, badValue, false));

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
	@Test
	public void testBooleanStates()
	{
		this.testPrimitive(SchemaBoolean.class, true, "true");
		this.testPrimitive(SchemaBoolean.class, false, "false");
	}

	/**
	 * testNullStates
	 */
	@Test
	public void testNullStates()
	{
		this.testPrimitive(SchemaNull.class, null, "null");
	}

	/**
	 * testNumberStates
	 */
	@Test
	public void testNumberStates()
	{
		this.testPrimitive(SchemaNumber.class, 10.1, "10g");
	}

	/**
	 * testStringStates
	 */
	@Test
	public void testStringStates()
	{
		this.testPrimitive(SchemaString.class, "abc", null);
	}

	/**
	 * testStartObjectStates
	 */
	@Test
	public void testStartObjectStates()
	{
		Schema schema = new Schema();
		SchemaObject state = schema.createObject();
		List<EventResult> goodList = this.createGoodList( //
				EnumSet.of(SchemaEventType.START_OBJECT), //
				"property" //
		);

		this.testStates(state, goodList.toArray(new EventResult[goodList.size()]));
	}

	/**
	 * testStartObjectEntryStates
	 */
	@Test
	public void testStartObjectEntryStates()
	{
		// create object and configure it
		Schema schema = new Schema();
		SchemaObject state = schema.createObject();
		String propertyName = "myProperty";
		state.addProperty(propertyName, "String");

		// build tests
		List<EventResult> goodList = this.createGoodList( //
				EnumSet.of(SchemaEventType.START_OBJECT_ENTRY, SchemaEventType.END_OBJECT), //
				propertyName //
				);

		// build initializer used before each test runs
		StateInitializer initializer = new StateInitializer()
		{
			public void initialize(IState state)
			{
				state.enter();
				state.transition(_context, SchemaEventType.START_OBJECT, null);
			}
		};

		this.testStates(state, initializer, goodList.toArray(new EventResult[goodList.size()]));
	}

	/**
	 * testEndObjectEntryStates
	 */
	@Test
	public void testEndObjectEntryStates()
	{
		// create object and configure it
		Schema schema = new Schema();
		SchemaObject state = schema.createObject();
		final String propertyName = "myProperty";
		state.addProperty(propertyName, "String");

		// build tests
		List<EventResult> goodList = this.createGoodList( //
				EnumSet.of(SchemaEventType.END_OBJECT_ENTRY), //
				propertyName //
				);

		// build initializer used before each test runs
		StateInitializer initializer = new StateInitializer()
		{
			public void initialize(IState state)
			{
				state.enter();
				state.transition(_context, SchemaEventType.START_OBJECT, null);
				state.transition(_context, SchemaEventType.START_OBJECT_ENTRY, propertyName);
			}
		};

		this.testStates(state, initializer, goodList.toArray(new EventResult[goodList.size()]));
	}

	/**
	 * testEndObjectStates
	 */
	@Test
	public void testEndObjectStates()
	{
		// create object and configure it
		Schema schema = new Schema();
		SchemaObject state = schema.createObject();
		String propertyName = "myProperty";
		state.addProperty(propertyName, "String");

		// build tests
		List<EventResult> goodList = this.createGoodList( //
				EnumSet.of(SchemaEventType.END_OBJECT, SchemaEventType.START_OBJECT_ENTRY), //
				propertyName //
				);

		// build initializer used before each test runs
		StateInitializer initializer = new StateInitializer()
		{
			public void initialize(IState state)
			{
				state.enter();
				state.transition(_context, SchemaEventType.START_OBJECT, null);
			}
		};

		this.testStates(state, initializer, goodList.toArray(new EventResult[goodList.size()]));
	}

	/**
	 * testStartArrayStates
	 */
	@Test
	public void testStartArrayStates()
	{
		// create array and configure it
		Schema schema = new Schema();
		SchemaArray state = schema.createArray("String");

		// build tests
		List<EventResult> goodList = this.createGoodList( //
				EnumSet.of(SchemaEventType.START_ARRAY), //
				null //
				);

		this.testStates(state, goodList.toArray(new EventResult[goodList.size()]));
	}

	/**
	 * testEndArrayStates
	 */
	@Test
	public void testStartArrayEntryStates()
	{
		// create array and configure it
		Schema schema = new Schema();
		SchemaArray state = schema.createArray("String");

		// build tests
		List<EventResult> goodList = this.createGoodList( //
				EnumSet.of(SchemaEventType.START_ARRAY_ENTRY, SchemaEventType.END_ARRAY), //
				null //
				);

		// build initializer used before each test runs
		StateInitializer initializer = new StateInitializer()
		{
			public void initialize(IState state)
			{
				state.enter();
				state.transition(_context, SchemaEventType.START_ARRAY, null);
			}
		};

		this.testStates(state, initializer, goodList.toArray(new EventResult[goodList.size()]));
	}

	/**
	 * testEndArrayStates
	 */
	@Test
	public void testEndArrayEntryStates()
	{
		// create array and configure it
		Schema schema = new Schema();
		SchemaArray state = schema.createArray("String");

		// build tests
		List<EventResult> goodList = this.createGoodList( //
				EnumSet.of(SchemaEventType.END_ARRAY_ENTRY), //
				null //
				);

		// build initializer used before each test runs
		StateInitializer initializer = new StateInitializer()
		{
			public void initialize(IState state)
			{
				state.enter();
				state.transition(_context, SchemaEventType.START_ARRAY, null);
				state.transition(_context, SchemaEventType.START_ARRAY_ENTRY, null);
			}
		};

		this.testStates(state, initializer, goodList.toArray(new EventResult[goodList.size()]));
	}

	/**
	 * testEndArrayStates
	 */
	@Test
	public void testEndArrayStates()
	{
		// create array and configure it
		Schema schema = new Schema();
		SchemaArray state = schema.createArray("String");

		// build tests
		List<EventResult> goodList = this.createGoodList( //
				EnumSet.of(SchemaEventType.END_ARRAY, SchemaEventType.START_ARRAY_ENTRY), //
				null //
				);

		// build initializer used before each test runs
		StateInitializer initializer = new StateInitializer()
		{
			public void initialize(IState state)
			{
				state.enter();
				state.transition(_context, SchemaEventType.START_ARRAY, null);
			}
		};

		this.testStates(state, initializer, goodList.toArray(new EventResult[goodList.size()]));
	}
}
