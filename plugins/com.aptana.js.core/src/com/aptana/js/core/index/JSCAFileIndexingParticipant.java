/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.index;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.index.core.AbstractFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.build.BuildContext;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.JSTypeConstants;
import com.aptana.js.core.inferencing.JSTypeUtil;
import com.aptana.js.core.model.AliasElement;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.TypeElement;
import com.aptana.js.internal.core.index.IJSCAModel;
import com.aptana.js.internal.core.index.IJSCAParser;
import com.aptana.js.internal.core.index.JSIndexReader;
import com.aptana.js.internal.core.index.JSIndexWriter;
import com.aptana.js.internal.core.index.JSCAParser;

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

			IJSCAParser parser = getJSCAParser();
			IJSCAModel model = null;
			InputStream is = null;
			try
			{
				// parse
				model = parser.parse(is = context.openInputStream(sub.newChild(5)));
				sub.worked(45);
			}
			finally
			{
				if (is != null)
				{
					try
					{
						is.close();
					}
					catch (IOException e) // $codepro.audit.disable emptyCatchClause
					{
					}
				}
			}

			// create new Window type for this file
			JSIndexReader jsir = new JSIndexReader();
			List<TypeElement> globalTypes = jsir.getType(index, JSTypeConstants.GLOBAL_TYPE, true);
			TypeElement global;
			if (!CollectionsUtil.isEmpty(globalTypes))
			{
				global = globalTypes.get(globalTypes.size() - 1);
			}
			else
			{
				global = JSTypeUtil.createGlobalType(JSTypeConstants.GLOBAL_TYPE);
			}

			// process results
			JSIndexWriter indexer = new JSIndexWriter();
			List<TypeElement> types = model.getTypes();
			List<AliasElement> aliases = model.getAliases();
			URI location = context.getURI();

			for (TypeElement type : types)
			{
				indexer.writeType(index, type, location);

				String typeName = type.getName();

				if (isGlobalProperty(type))
				{
					PropertyElement property = global.getProperty(typeName);

					if (property == null)
					{
						property = new PropertyElement();

						property.setName(typeName);
						property.addType(typeName);

						global.addProperty(property);
					}
				}
			}

			for (AliasElement alias : aliases)
			{
				PropertyElement property = new PropertyElement();

				property.setName(alias.getName());
				property.addType(alias.getType());

				global.addProperty(property);
			}

			// write global type info
			if (global.hasProperties())
			{
				indexer.writeType(index, global, location);
			}
		}
		catch (Throwable e)
		{
			IdeLog.logError(JSCorePlugin.getDefault(), e);
		}
		finally
		{
			sub.done();
		}
	}

	protected IJSCAParser getJSCAParser()
	{
		return new JSCAParser();
	}

	/**
	 * Determine if the specified type should generate a global property
	 * 
	 * @param type
	 * @return
	 */
	protected boolean isGlobalProperty(TypeElement type)
	{
		if (type == null || type.isInternal())
		{
			return false;
		}
		String typeName = type.getName();
		return !typeName.contains(".") && !typeName.startsWith(JSTypeConstants.GENERIC_CLASS_OPEN); //$NON-NLS-1$
	}
}
