/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.IFilter;
import com.aptana.core.IMap;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.model.EventElement;
import com.aptana.js.core.model.EventPropertyElement;
import com.aptana.js.core.model.FunctionElement;
import com.aptana.js.core.model.ParameterElement;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.TypeElement;

public class JSCAParserTest
{

	private IJSCAParser parser;

	@Before
	public void setUp() throws Exception
	{
		parser = new JSCAParser();
	}

	@After
	public void tearDown() throws Exception
	{
		parser = null;
	}

	@Test
	public void testTISTUD5079() throws Exception
	{
		// Ensure we can parse a JSCA file that has an arbitrary property we don't recognize (we just ignore it).
		IJSCAModel model = parse("indexing/arbitrary_property.jsca");

		List<TypeElement> types = model.getTypes();
		assertEquals(1, types.size());
	}

	@Test
	public void testCreatesImplicitOwningType() throws Exception
	{
		// We define a "Titanium.UI type, but not "Titanium" explicitly, it generates it for us.
		IJSCAModel model = parse("indexing/implicit_owner.jsca");

		List<TypeElement> types = model.getTypes();
		assertEquals(2, types.size());
		List<String> typeNames = CollectionsUtil.map(types, new IMap<TypeElement, String>()
		{
			public String map(TypeElement item)
			{
				return item.getName();
			}
		});
		assertTrue(typeNames.contains("Titanium.UI"));
		assertTrue(typeNames.contains("Titanium"));
	}

	@Test
	public void testParsePropertyWithConstants() throws Exception
	{
		IJSCAModel model = parse("indexing/property_with_constants.jsca");

		List<TypeElement> types = model.getTypes();
		assertEquals(1, types.size());
		TypeElement type = types.get(0);
		PropertyElement prop = type.getProperty("appearance");
		List<String> constants = prop.getConstants();
		assertEquals(2, constants.size());
		assertTrue(constants.contains("Titanium.UI.KEYBOARD_APPEARANCE_ALERT"));
		assertTrue(constants.contains("Titanium.UI.KEYBOARD_APPEARANCE_DEFAULT"));
	}

	@Test
	public void testParseEventPropertyWithConstants() throws Exception
	{
		IJSCAModel model = parse("indexing/event_property_with_constants.jsca");

		List<TypeElement> types = model.getTypes();
		assertEquals(1, types.size());
		TypeElement type = types.get(0);
		EventElement event = type.getEvent("beforeload");
		List<EventPropertyElement> properties = event.getProperties();
		EventPropertyElement navType = CollectionsUtil.find(properties, new IFilter<EventPropertyElement>()
		{
			public boolean include(EventPropertyElement item)
			{

				return item.getName().equals("navigationType");
			}
		});
		List<String> constants = navType.getConstants();
		assertEquals(6, constants.size());
		assertTrue(constants.contains("Titanium.UI.iOS.WEBVIEW_NAVIGATIONTYPE_LINK_CLICKED"));
		assertTrue(constants.contains("Titanium.UI.iOS.WEBVIEW_NAVIGATIONTYPE_FORM_SUBMITTED"));
		assertTrue(constants.contains("Titanium.UI.iOS.WEBVIEW_NAVIGATIONTYPE_BACK_FORWARD"));
		assertTrue(constants.contains("Titanium.UI.iOS.WEBVIEW_NAVIGATIONTYPE_RELOAD"));
		assertTrue(constants.contains("Titanium.UI.iOS.WEBVIEW_NAVIGATIONTYPE_FORM_RESUBMITTED"));
		assertTrue(constants.contains("Titanium.UI.iOS.WEBVIEW_NAVIGATIONTYPE_OTHER"));
	}

	@Test
	public void testParameterWithConstants() throws Exception
	{
		IJSCAModel model = parse("indexing/parameter_with_constants.jsca");

		List<TypeElement> types = model.getTypes();
		assertEquals(1, types.size());
		TypeElement type = types.get(0);
		FunctionElement func = (FunctionElement) type.getProperty("convertUnits");
		List<ParameterElement> params = func.getParameters();
		ParameterElement param = CollectionsUtil.find(params, new IFilter<ParameterElement>()
		{
			public boolean include(ParameterElement item)
			{

				return item.getName().equals("convertToUnits");
			}
		});

		List<String> constants = param.getConstants();
		assertEquals(5, constants.size());
		assertTrue(constants.contains("Titanium.UI.UNIT_CM"));
		assertTrue(constants.contains("Titanium.UI.UNIT_DIP"));
		assertTrue(constants.contains("Titanium.UI.UNIT_IN"));
		assertTrue(constants.contains("Titanium.UI.UNIT_MM"));
		assertTrue(constants.contains("Titanium.UI.UNIT_PX"));
	}

	protected IJSCAModel parse(String filePath) throws IOException
	{
		URL url = FileLocator.find(JSCorePlugin.getDefault().getBundle(), Path.fromPortableString(filePath), null);

		IJSCAModel model = parser.parse(url.openStream());
		return model;
	}

}
