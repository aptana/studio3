/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.index;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
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
		if (context == null || index == null)
		{
			return;
		}

		SubMonitor sub = SubMonitor.convert(monitor, 100);
		try
		{
			sub.subTask(getIndexingMessage(index, context.getURI()));

			JSCAReader reader = new JSCAReader();
			SchemaContext schemaContext = new SchemaContext();
			JSCAHandler handler = new JSCAHandler();
			schemaContext.setHandler(handler);

			Reader isr = null;
			try
			{
				// parse
				isr = new InputStreamReader(context.openInputStream(sub.newChild(5)));
				reader.read(isr, schemaContext);
				sub.worked(45);
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

			// create new Window type for this file
			JSIndexReader jsir = new JSIndexReader();
			List<TypeElement> windows = jsir.getType(index, JSTypeConstants.WINDOW_TYPE, true);
			TypeElement window;

			if (!CollectionsUtil.isEmpty(windows))
			{
				window = windows.get(windows.size() - 1);
			}
			else
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

				if (isGlobalProperty(type))
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
			if (window.hasProperties())
			{
				indexer.writeType(index, window, location);
			}
		}
		catch (Throwable e)
		{
			IdeLog.logError(JSPlugin.getDefault(), e);
		}
		finally
		{
			sub.done();
		}
	}

	/**
	 * Determine if the specified type should generate a global property
	 * 
	 * @param type
	 * @return
	 */
	protected boolean isGlobalProperty(TypeElement type)
	{
		boolean result = false;

		if (type != null)
		{
			if (!type.isInternal())
			{
				String typeName = type.getName();

				result = !typeName.contains(".") && !typeName.startsWith(JSTypeConstants.GENERIC_CLASS_OPEN); //$NON-NLS-1$
			}
		}

		return result;
	}
}
