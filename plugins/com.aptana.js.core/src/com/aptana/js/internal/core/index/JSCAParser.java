/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.index;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.aptana.core.logging.IdeLog;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.model.AliasElement;
import com.aptana.js.core.model.TypeElement;

/**
 * @author cwilliams
 */
public class JSCAParser implements IJSCAParser
{

	/*
	 * (non-Javadoc)
	 * @see com.aptana.js.internal.core.index.IJSCAParser#parse(java.io.InputStream)
	 */
	public IJSCAModel parse(InputStream is)
	{
		try
		{
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(new InputStreamReader(is), new ContainerFactory()
			{

				public Map createObjectContainer()
				{
					// Use JSONObjects
					return null;
				}

				public List creatArrayContainer()
				{
					return new ArrayList();
				}
			});

			return new JSCAModel(json);
		}
		catch (IOException e)
		{
			IdeLog.logError(JSCorePlugin.getDefault(), e);
		}
		catch (ParseException e)
		{
			IdeLog.logError(JSCorePlugin.getDefault(), e);
		}
		return new NullJSCAModel();
	}

	private class NullJSCAModel implements IJSCAModel
	{
		public List<AliasElement> getAliases()
		{
			return Collections.emptyList();
		}

		public List<TypeElement> getTypes()
		{
			return Collections.emptyList();
		}
	}

}
