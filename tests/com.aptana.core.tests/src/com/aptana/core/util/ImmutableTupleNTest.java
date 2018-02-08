/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Fabio
 */
public class ImmutableTupleNTest
{

	@Test
	public void testImmutableTuple() throws Exception
	{
		ImmutableTupleN tuple1 = new ImmutableTupleN(new Object[] { null });
		ImmutableTupleN tuple2 = new ImmutableTupleN(new Object[] { 1, 2 });
		ImmutableTupleN tuple3 = new ImmutableTupleN(new Object[] { 1, "3" });
		ImmutableTupleN tuple4 = new ImmutableTupleN(new Object[] { 1, "3" });
		ImmutableTupleN tuple5 = new ImmutableTupleN(new Object[] { 1, 2, 3 });
		ImmutableTupleN tuple6 = new ImmutableTupleN(new Object[] { 1 });

		assertEquals(tuple3.hashCode(), tuple4.hashCode());
		assertTrue(tuple3.equals(tuple4));

		assertFalse(tuple3.equals(tuple1));
		assertFalse(tuple3.equals(tuple2));

		assertFalse(tuple2.hashCode() == tuple1.hashCode());
		assertFalse("Should not equal!", tuple6.equals(tuple1));
		assertFalse("Should not equal!", tuple1.equals(tuple6));

		ImmutableTupleN immutableTupleN = new ImmutableTupleN(tuple1, tuple2, tuple3, tuple4, tuple5);
		for (int i = 0; i < immutableTupleN.size(); i++)
		{
			immutableTupleN.getAt(i).toString();
		}
		for (int i = 0; i < immutableTupleN.size(); i++)
		{
			immutableTupleN.getAt(i).hashCode();
		}
	}
}
