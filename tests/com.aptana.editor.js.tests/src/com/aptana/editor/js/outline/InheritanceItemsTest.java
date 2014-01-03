/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.outline;

import org.junit.Test;

/**
 * @author Kevin Lindsey
 */
public class InheritanceItemsTest extends OutlineItemTestCase
{

	private void testInheritance(String source)
	{
		testItem(source, "/outline/object-literal", "Subclass", 1);
		testItem(source, "/outline/object-literal/boolean", "a");
	}

	@Test
	public void testDojoLangExtend()
	{
		testInheritance("dojo.lang.extend(Subclass, { a: true });");
	}

	@Test
	public void testMochiKitBaseUpdate()
	{
		testInheritance("MochiKit.Base.update(Subclass, { a: true });");
	}

	@Test
	public void testObjectExtend()
	{
		testInheritance("Object.extend(Subclass, { a: true });");
	}

	@Test
	public void testExtExtend()
	{
		testInheritance("Ext.extend(Subclass, Superclass, { a: true });");
	}

	@Test
	public void testQxClassDefine()
	{
		testInheritance("qx.Class.define(Subclass, { a: true });");
	}

	@Test
	public void testQxInterfaceDefine()
	{
		testInheritance("qx.Interface.define(Subclass, { a: true });");
	}

	@Test
	public void testQxThemeDefine()
	{
		testInheritance("qx.Theme.define(Subclass, { a: true });");
	}

	@Test
	public void testQxMixinDefine()
	{
		testInheritance("qx.Mixin.define(Subclass, { a: true });");
	}
}
