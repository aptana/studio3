/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.junit.Test;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("nls")
public class RegionsTest {

	@Test
	public void testSingleRegion() {
		Iterator<IRegion> i = new Regions(new Region(10, 5)).iterator();
		IRegion r = i.next();
		assertEquals("Offset doesn't match", 10, r.getOffset());
		assertEquals("Length doesn't match", 5, r.getLength());
		assertFalse("Last region element", i.hasNext());
	}

	@Test
	public void testSameSingleRegion() {
		Iterator<IRegion> i = new Regions(new Region(10, 5), new Region(10, 5)).iterator();
		IRegion r = i.next();
		assertEquals("Offset doesn't match", 10, r.getOffset());
		assertEquals("Length doesn't match", 5, r.getLength());
		assertFalse("Last region element", i.hasNext());
	}

	@Test
	public void testDefault() {
		Regions regions = new Regions();
		assertFalse("Last region element", regions.iterator().hasNext());
		assertTrue("Regions is empty", regions.isEmpty());
	}

	@Test
	public void testZeroLength() {
		Regions regions = new Regions(new Region(10, 0));
		assertFalse("Last region element", regions.iterator().hasNext());
		assertTrue("Regions is empty", regions.isEmpty());
	}

	@Test
	public void testAppendZeroLength() {
		Iterator<IRegion> i = new Regions(new Region(10, 5), new Region(10, 0)).iterator();
		IRegion r = i.next();
		assertEquals("Offset doesn't match", 10, r.getOffset());
		assertEquals("Length doesn't match", 5, r.getLength());
		assertFalse("Last region element", i.hasNext());
	}

	@Test
	public void testWithGap() {
		Iterator<IRegion> i = new Regions(new Region(10, 5), new Region(20, 7)).iterator();
		IRegion r = i.next();
		assertEquals("Offset doesn't match", 10, r.getOffset());
		assertEquals("Length doesn't match", 5, r.getLength());
		r = i.next();
		assertEquals("Offset doesn't match", 20, r.getOffset());
		assertEquals("Length doesn't match", 7, r.getLength());
		assertFalse("Last region element", i.hasNext());
	}

	@Test
	public void testNoGap() {
		Iterator<IRegion> i = new Regions(new Region(10, 5), new Region(15, 7)).iterator();
		IRegion r = i.next();
		assertEquals("Offset doesn't match", 10, r.getOffset());
		assertEquals("Length doesn't match", 12, r.getLength());
		assertFalse("Last region element", i.hasNext());
	}

	@Test
	public void testOverlaping() {
		Iterator<IRegion> i = new Regions(new Region(10, 5), new Region(14, 7)).iterator();
		IRegion r = i.next();
		assertEquals("Offset doesn't match", 10, r.getOffset());
		assertEquals("Length doesn't match", 11, r.getLength());
		assertFalse("Last region element", i.hasNext());
	}

	@Test
	public void testOverlapingGap() {
		Iterator<IRegion> i = new Regions(new Region(10, 5), new Region(20, 5), new Region(13, 9)).iterator();
		IRegion r = i.next();
		assertEquals("Offset doesn't match", 10, r.getOffset());
		assertEquals("Length doesn't match", 15, r.getLength());
		assertFalse("Last region element", i.hasNext());
	}

	@Test
	public void testOverlapingGapWideLeft() {
		Iterator<IRegion> i = new Regions(new Region(10, 5), new Region(20, 5), new Region(5, 19)).iterator();
		IRegion r = i.next();
		assertEquals("Offset doesn't match", 5, r.getOffset());
		assertEquals("Length doesn't match", 20, r.getLength());
		assertFalse("Last region element", i.hasNext());
	}

	@Test
	public void testOverlapingGapWideRight() {
		Iterator<IRegion> i = new Regions(new Region(10, 5), new Region(20, 5), new Region(14, 16)).iterator();
		IRegion r = i.next();
		assertEquals("Offset doesn't match", 10, r.getOffset());
		assertEquals("Length doesn't match", 20, r.getLength());
		assertFalse("Last region element", i.hasNext());
	}

	@Test
	public void testOverlapingGapWideBoth() {
		Iterator<IRegion> i = new Regions(new Region(10, 5), new Region(20, 5), new Region(5, 30)).iterator();
		IRegion r = i.next();
		assertEquals("Offset doesn't match", 5, r.getOffset());
		assertEquals("Length doesn't match", 30, r.getLength());
		assertFalse("Last region element", i.hasNext());
	}

	@Test
	public void testOverlapingWide() {
		Iterator<IRegion> i = new Regions(new Region(10, 5), new Region(5, 15)).iterator();
		IRegion r = i.next();
		assertEquals("Offset doesn't match", 5, r.getOffset());
		assertEquals("Length doesn't match", 15, r.getLength());
		assertFalse("Last region element", i.hasNext());
	}

	@Test
	public void testMakeGap() {
		Regions regions = new Regions(new Region(10, 20));
		regions.remove(new Region(15, 5));
		Iterator<IRegion> i = regions.iterator();
		IRegion r = i.next();
		assertEquals("Offset doesn't match", 10, r.getOffset());
		assertEquals("Length doesn't match", 5, r.getLength());
		r = i.next();
		assertEquals("Offset doesn't match", 20, r.getOffset());
		assertEquals("Length doesn't match", 10, r.getLength());
		assertFalse("Last region element", i.hasNext());
	}

	@Test
	public void testCutLeft() {
		Regions regions = new Regions(new Region(10, 20));
		regions.remove(new Region(10, 5));
		Iterator<IRegion> i = regions.iterator();
		IRegion r = i.next();
		assertEquals("Offset doesn't match", 15, r.getOffset());
		assertEquals("Length doesn't match", 15, r.getLength());
		assertFalse("Last region element", i.hasNext());
	}

	@Test
	public void testCutLeftWide() {
		Regions regions = new Regions(new Region(10, 20));
		regions.remove(new Region(5, 10));
		Iterator<IRegion> i = regions.iterator();
		IRegion r = i.next();
		assertEquals("Offset doesn't match", 15, r.getOffset());
		assertEquals("Length doesn't match", 15, r.getLength());
		assertFalse("Last region element", i.hasNext());
	}

	@Test
	public void testCutRight() {
		Regions regions = new Regions(new Region(10, 20));
		regions.remove(new Region(25, 5));
		Iterator<IRegion> i = regions.iterator();
		IRegion r = i.next();
		assertEquals("Offset doesn't match", 10, r.getOffset());
		assertEquals("Length doesn't match", 15, r.getLength());
		assertFalse("Last region element", i.hasNext());
	}

	@Test
	public void testCutRightWide() {
		Regions regions = new Regions(new Region(10, 20));
		regions.remove(new Region(25, 10));
		Iterator<IRegion> i = regions.iterator();
		IRegion r = i.next();
		assertEquals("Offset doesn't match", 10, r.getOffset());
		assertEquals("Length doesn't match", 15, r.getLength());
		assertFalse("Last region element", i.hasNext());
	}

	@Test
	public void testCutGap() {
		Regions regions = new Regions(new Region(10, 5), new Region(20, 5));
		regions.remove(new Region(15, 10));
		Iterator<IRegion> i = regions.iterator();
		IRegion r = i.next();
		assertEquals("Offset doesn't match", 10, r.getOffset());
		assertEquals("Length doesn't match", 5, r.getLength());
		assertFalse("Last region element", i.hasNext());
	}

	@Test
	public void testCutGapWide() {
		Regions regions = new Regions(new Region(10, 5), new Region(20, 5));
		regions.remove(new Region(15, 20));
		Iterator<IRegion> i = regions.iterator();
		IRegion r = i.next();
		assertEquals("Offset doesn't match", 10, r.getOffset());
		assertEquals("Length doesn't match", 5, r.getLength());
		assertFalse("Last region element", i.hasNext());
	}

	@Test
	public void testOverlapNone() {
		IRegion r = new Regions(new Region(10, 5)).overlap(new Region(5, 5));
		assertNull("Should be no overlap", r);
	}

	@Test
	public void testOverlapLeft() {
		IRegion r = new Regions(new Region(10, 5)).overlap(new Region(5, 7));
		assertEquals("Offset doesn't match", 10, r.getOffset());
		assertEquals("Length doesn't match", 2, r.getLength());
	}

	@Test
	public void testOverlapRight() {
		IRegion r = new Regions(new Region(10, 5)).overlap(new Region(13, 4));
		assertEquals("Offset doesn't match", 13, r.getOffset());
		assertEquals("Length doesn't match", 2, r.getLength());
	}

	@Test
	public void testOverlapWide() {
		IRegion r = new Regions(new Region(10, 5)).overlap(new Region(8, 9));
		assertEquals("Offset doesn't match", 10, r.getOffset());
		assertEquals("Length doesn't match", 5, r.getLength());
	}

	@Test
	public void testOverlapInner() {
		IRegion r = new Regions(new Region(10, 5)).overlap(new Region(12, 2));
		assertEquals("Offset doesn't match", 12, r.getOffset());
		assertEquals("Length doesn't match", 2, r.getLength());
	}

	@Test
	public void testOverlapWithGap() {
		IRegion r = new Regions(new Region(10, 5), new Region(20, 5)).overlap(new Region(12, 10));
		assertEquals("Offset doesn't match", 12, r.getOffset());
		assertEquals("Length doesn't match", 3, r.getLength());
	}

	@Test
	public void testOverlapWithGapWide() {
		IRegion r = new Regions(new Region(10, 5), new Region(20, 5)).overlap(new Region(8, 20));
		assertEquals("Offset doesn't match", 10, r.getOffset());
		assertEquals("Length doesn't match", 5, r.getLength());
	}

	@Test
	public void testToString() {
		assertNotNull(new Regions(new Region(10, 5), new Region(20, 7)).toString());
	}

	@Test
	public void testNegative() {
		Regions regions = new Regions();
		try {
			regions.append(new Region(10, -5));
			fail("Negative length should fail");
		} catch (IllegalArgumentException e) {
		}
		assertTrue("Regions is empty", regions.isEmpty());
	}

	@Test
	public void testClear() {
		Regions regions = new Regions(new Region(10, 5));
		assertFalse("Regions should not be empty", regions.isEmpty());
		regions.clear();
		assertTrue("Regions is empty", regions.isEmpty());
	}

}
