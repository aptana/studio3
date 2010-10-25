/*******************************************************************************
 * Copyright (c) 2007 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Michael Scharf (Wind River) - initial API and implementation
 *******************************************************************************/
package org.eclipse.tm.terminal.model;

import junit.framework.TestCase;

public class StyleColorTest extends TestCase {


	public void testEqualsObject() {
		assertEquals(StyleColor.getStyleColor("foo"),StyleColor.getStyleColor("foo"));
		assertFalse(StyleColor.getStyleColor("foox").equals(StyleColor.getStyleColor("foo")));
	}

	public void testSameObject() {
		assertSame(StyleColor.getStyleColor("foo"),StyleColor.getStyleColor("foo"));
		assertNotSame(StyleColor.getStyleColor("foox"),StyleColor.getStyleColor("foo"));
	}

	public void testToString() {
		assertEquals("xxx", StyleColor.getStyleColor("xxx").toString());
	}

	public void testGetName() {
		assertEquals("xxx", StyleColor.getStyleColor("xxx").getName());
	}

}
