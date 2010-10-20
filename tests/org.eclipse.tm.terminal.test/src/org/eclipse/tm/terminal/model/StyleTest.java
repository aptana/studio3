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

public class StyleTest extends TestCase {
	final StyleColor c1=StyleColor.getStyleColor("c1");
	final StyleColor c2=StyleColor.getStyleColor("c2");
	final StyleColor c3=StyleColor.getStyleColor("c3");

	public void testGetStyle() {
		Style s1=Style.getStyle(c1, c2, true, false, true, false);
		Style s2=Style.getStyle(c1, c2, true, false, true, false);
		assertEquals(s1,s2);
		assertSame(s1,s2);
		s1=s1.setBlink(!s1.isBlink());
		assertNotSame(s1,s2);
		assertFalse(s1.equals(s2));
		s1=s1.setBlink(!s1.isBlink());
		assertSame(s1,s2);
	}

	public void testSetForground() {
		Style s1=Style.getStyle(c1, c2, true, false, true, false);
		Style s2=s1;
		s2=s1.setForground(c3);
		assertNotSame(s1,s2);
		assertFalse(s1.equals(s2));
		assertSame(s2.getForground(), c3);
		assertSame(s1.getForground(), c1);
		assertSame(s1.getBackground(), c2);
		assertSame(s2.getBackground(), c2);
		s2=s2.setForground(c1);
		assertSame(s1, s2);
	}

	public void testSetBackground() {
		Style s1=Style.getStyle(c1, c2, true, false, true, false);
		Style s2=s1;
		s2=s1.setBackground(c3);
		assertNotSame(s1,s2);
		assertFalse(s1.equals(s2));
		assertSame(s2.getForground(), c1);
		assertSame(s1.getForground(), c1);
		assertSame(s1.getBackground(), c2);
		assertSame(s2.getBackground(), c3);
		s2=s2.setBackground(c2);
		assertSame(s1, s2);
	}

	public void testSetBold() {
		Style s1=getDefaultStyle();
		Style s2=s1;
		assertSame(s1,s2);
		assertFalse(s2.isBold());
		s2=s2.setBold(true);
		assertNotSame(s1,s2);
		assertTrue(s2.isBold());
		s2=s2.setBold(false);
		assertSame(s1,s2);
		assertFalse(s2.isBold());
	}

	public void testSetBlink() {
		Style s1=getDefaultStyle();
		Style s2=s1;
		assertSame(s1,s2);
		assertFalse(s2.isBlink());
		s2=s2.setBlink(true);
		assertNotSame(s1,s2);
		assertTrue(s2.isBlink());
		s2=s2.setBlink(false);
		assertSame(s1,s2);
		assertFalse(s2.isBlink());
	}

	public void testSetUnderline() {
		Style s1=getDefaultStyle();
		Style s2=s1;
		assertSame(s1,s2);
		assertFalse(s2.isUnderline());
		s2=s2.setUnderline(true);
		assertNotSame(s1,s2);
		assertTrue(s2.isUnderline());
		s2=s2.setUnderline(false);
		assertSame(s1,s2);
		assertFalse(s2.isUnderline());
	}

	public void testSetReverse() {
		Style s1=getDefaultStyle();
		Style s2=s1;
		assertSame(s1,s2);
		assertFalse(s2.isReverse());
		s2=s2.setReverse(true);
		assertNotSame(s1,s2);
		assertTrue(s2.isReverse());
		s2=s2.setReverse(false);
		assertSame(s1,s2);
		assertFalse(s2.isReverse());
	}

	private Style getDefaultStyle() {
		return Style.getStyle(c1, c2, false, false, false, false);
	}

}
