/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.TestCase;

public class ImmutableTupleTest
{

	@Test
	public void testImmutableTuple() throws Exception
	{
		ImmutableTuple<Integer, Integer> tuple1 = new ImmutableTuple<Integer, Integer>(1, 2);
		ImmutableTuple<Integer, Integer> tuple2 = new ImmutableTuple<Integer, Integer>(null, 2);
		ImmutableTuple<Integer, Integer> tuple3 = new ImmutableTuple<Integer, Integer>(1, 2);
		assertEquals(tuple1, tuple3);
		assertFalse(tuple1.equals(tuple2));
		// Just calling to check if we have no exceptions
		tuple1.toString();
		tuple2.toString();
		tuple1.hashCode();
		tuple2.hashCode();
	}
}
