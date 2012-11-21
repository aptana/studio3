/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.index;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ObjectPool;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.json.Schema;
import com.aptana.json.SchemaBuilder;
import com.aptana.json.SchemaContext;
import com.aptana.json.SchemaHandler;
import com.aptana.json.SchemaReader;

/**
 * JSCAReader
 */
public class JSCAReader extends SchemaReader
{
	private static class SchemaPool extends ObjectPool<Schema>
	{
		private static final String JSCA_METADATA_SCHEMA = "/metadata/JSMetadataSchema.json"; //$NON-NLS-1$

		SchemaPool()
		{
			// No need for schemas to expire
			super(-1);
		}

		public Schema create()
		{
			InputStreamReader isr = null;
			Schema result = null;

			try
			{
				// grab the JSCA schema's URL
				Bundle bundle = JSCorePlugin.getDefault().getBundle();
				Path path = new Path(JSCA_METADATA_SCHEMA);
				URL url = FileLocator.find(bundle, path, null);

				// create input stream reader for the schema
				InputStream is = url.openStream();
				isr = new InputStreamReader(is);

				// create a schema context and associate a handler with it
				SchemaContext context = new SchemaContext();
				SchemaHandler handler = new SchemaHandler();
				context.setHandler(handler);

				// read the JSCA schema and build a Schema as a side-effect
				SchemaBuilder reader = new SchemaBuilder();
				reader.read(isr, context);

				// grab the resulting JSCA Schema
				result = handler.getSchema();
			}
			catch (Throwable t)
			{
				IdeLog.logError(JSCorePlugin.getDefault(), t);
			}
			finally
			{
				if (isr != null)
				{
					try
					{
						isr.close();
					}
					catch (IOException e) // $codepro.audit.disable emptyCatchClause
					{
					}
				}
			}

			return result;
		}

		public void expire(Schema o)
		{
			// do nothing
		}

		public boolean validate(Schema o)
		{
			return o != null;
		}
	}

	private static SchemaPool SCHEMA_POOL;

	/**
	 * NOTE: The chances of multiple threads grabbing the schema pool is very rare, so there's no need to optimize this
	 * method's lock
	 * 
	 * @return
	 */
	protected static synchronized SchemaPool getSchemaPool()
	{
		// NOTE: The chances of multiple threads grabbing the schema pool is very rare, so there's no need
		// to optimize this method's lock
		if (SCHEMA_POOL == null)
		{
			SCHEMA_POOL = new SchemaPool();
		}

		return SCHEMA_POOL;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.SchemaReader#read(java.io.Reader, com.aptana.json.SchemaContext)
	 */
	@Override
	public void read(Reader input, SchemaContext context)
	{
		// grab schema
		SchemaPool schemaPool = getSchemaPool();
		Schema schema = schemaPool.checkOut();

		this.setSchema(schema);

		// process input
		super.read(input, context);

		// free schema
		schemaPool.checkIn(schema);
	}
}
