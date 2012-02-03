/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist.model;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.html.contentassist.HTMLIndexQueryHelper;
import com.aptana.index.core.Index;
import com.aptana.index.core.ui.views.IPropertyInformation;

/**
 * JSResourceGroup
 */
public class CSSReferencesGroup extends BaseElement<CSSReferencesGroup.Property>
{
	enum Property implements IPropertyInformation<CSSReferencesGroup>
	{
		NAME(Messages.CSSReferencesGroup_NameLabel)
		{
			public Object getPropertyValue(CSSReferencesGroup node)
			{
				return node.getName();
			}
		},
		COUNT(Messages.CSSReferencesGroup_CountLabel)
		{
			public Object getPropertyValue(CSSReferencesGroup node)
			{
				return node.getReferences().size();
			}
		};

		private String header;
		private String category;

		private Property(String header) // $codepro.audit.disable unusedMethod
		{
			this.header = header;
		}

		private Property(String header, String category)
		{
			this.category = category;
		}

		public String getCategory()
		{
			return category;
		}

		public String getHeader()
		{
			return header;
		}
	}

	private Index index;
	private List<CSSReference> references;

	/**
	 * CSSResourceGroup
	 * 
	 * @param index
	 */
	public CSSReferencesGroup(Index index)
	{
		this.index = index;
		setName(Messages.CSSReferencesGroup_CSSReferencesElementName);
	}

	/**
	 * getIndex
	 * 
	 * @return
	 */
	public Index getIndex()
	{
		return index;
	}

	@Override
	protected Set<Property> getPropertyInfoSet()
	{
		return EnumSet.allOf(Property.class);
	}

	/**
	 * getReferences
	 * 
	 * @return
	 */
	public List<CSSReference> getReferences()
	{
		if (references == null)
		{
			HTMLIndexQueryHelper queryHelper = new HTMLIndexQueryHelper();
			Map<String, String> resourceMap = queryHelper.getCSSReferences(index);

			if (!CollectionsUtil.isEmpty(resourceMap))
			{
				references = new ArrayList<CSSReference>();

				for (Map.Entry<String, String> entry : resourceMap.entrySet())
				{
					try
					{
						String path = entry.getKey();
						URI uri = new URI(path);
						File file = new File(uri);
						CSSReference reference = new CSSReference(file.getName(), path);

						references.add(reference);
					}
					catch (URISyntaxException e)
					{
						String message = "An error occurred while converting a CSS reference to a URI: "
								+ e.getMessage();

						IdeLog.logError(CSSPlugin.getDefault(), message);
					}
				}
			}
			else
			{
				references = Collections.emptyList();
			}
		}

		return references;
	}
}
