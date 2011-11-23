/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.index;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.model.AliasElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.index.core.AbstractFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.build.BuildContext;
import com.aptana.json.SchemaContext;

/**
 * JSCAFileIndexingParticipant
 */
public class JSCAFileIndexingParticipant extends AbstractFileIndexingParticipant
{
	public void index(BuildContext context, Index index, IProgressMonitor monitor) throws CoreException
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		try
		{
			Reader isr = null;

			sub.subTask(getIndexingMessage(index, context.getURI()));

			try
			{
				JSCAReader reader = new JSCAReader();
				SchemaContext schemaContext = new SchemaContext();
				JSCAHandler handler = new JSCAHandler();

				schemaContext.setHandler(handler);

				isr = new StringReader(context.getContents());

				// parse
				reader.read(isr, schemaContext);
				sub.worked(50);

				// create new Window type for this file
				JSIndexReader jsir = new JSIndexReader();
				TypeElement window = jsir.getType(index, JSTypeConstants.WINDOW_TYPE, true);

				if (window == null)
				{
					window = new TypeElement();
					window.setName(JSTypeConstants.WINDOW_TYPE);
				}

				// process results
				JSIndexWriter indexer = new JSIndexWriter();
				TypeElement[] types = handler.getTypes();
				AliasElement[] aliases = handler.getAliases();
				URI location = context.getURI();

				for (TypeElement type : types)
				{
					indexer.writeType(index, type, location);

					String typeName = type.getName();

					if (!typeName.contains(".") && !typeName.startsWith(JSTypeConstants.GENERIC_CLASS_OPEN)) //$NON-NLS-1$
					{
						PropertyElement property = window.getProperty(typeName);

						if (property == null)
						{
							property = new PropertyElement();

							property.setName(typeName);
							property.addType(typeName);

							window.addProperty(property);
						}
					}
				}

				for (AliasElement alias : aliases)
				{
					PropertyElement property = new PropertyElement();

					property.setName(alias.getName());
					property.addType(alias.getType());

					window.addProperty(property);
				}

				// write global type info
				indexer.writeType(index, window, location);
			}
			catch (Throwable e)
			{
				IdeLog.logError(JSPlugin.getDefault(), e);
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
		}
		finally
		{
			sub.done();
		}
	}
}
