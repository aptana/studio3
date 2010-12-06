/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.editor.css.Activator;
import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.css.contentassist.index.CSSIndexReader;
import com.aptana.editor.css.contentassist.index.CSSMetadataReader;
import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.editor.css.contentassist.model.PseudoClassElement;
import com.aptana.editor.css.contentassist.model.PseudoElementElement;
import com.aptana.index.core.Index;

public class CSSIndexQueryHelper
{
	private CSSIndexReader _reader;
	private CSSMetadataReader _metadata;

	/**
	 * CSSIndexQueryHelper
	 */
	public CSSIndexQueryHelper()
	{
	}

	/**
	 * getClasses
	 * 
	 * @return
	 */
	public Map<String, String> getClasses(Index index)
	{
		return this.getReader().getValues(index, CSSIndexConstants.CLASS);
	}

	/**
	 * getElements
	 * 
	 * @return
	 */
	public List<ElementElement> getElements()
	{
		return this.getMetadata().getElements();
	}
	
	/**
	 * getPseudoElements
	 * 
	 * @return
	 */
	public List<PseudoElementElement> getPseudoElements()
	{
		return this.getMetadata().getPseudoElements();
	}
	
	/**
	 * getPseudoClasses
	 * 
	 * @return
	 */
	public List<PseudoClassElement> getPseudoClasses()
	{
		return this.getMetadata().getPseudoClasses();
	}

	/**
	 * getIDs
	 * 
	 * @param index
	 * @return
	 */
	public Map<String, String> getIDs(Index index)
	{
		return this.getReader().getValues(index, CSSIndexConstants.IDENTIFIER);
	}

	/**
	 * getColors - Returns the unique set of colors used within the project.
	 * 
	 * @param index
	 * @return
	 */
	public Set<String> getColors(Index index)
	{
		if (index == null)
			return Collections.emptySet();
		Map<String, String> colorMap = this.getReader().getValues(index, CSSIndexConstants.COLOR);
		if (colorMap == null)
			return Collections.emptySet();
		return colorMap.keySet();
	}

	/**
	 * getMetadata
	 */
	private CSSMetadataReader getMetadata()
	{
		if (this._metadata == null)
		{
			this._metadata = new CSSMetadataReader();
			String[] resources = this.getMetadataResources();

			for (String resource : resources)
			{
				URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(resource), null);

				if (url != null)
				{
					InputStream stream = null;

					try
					{
						stream = url.openStream();

						this._metadata.loadXML(stream);
					}
					catch (IOException e)
					{
						Activator.logError(Messages.CSSIndexQueryHelper_Error_Reading_Metadata, e);
					}
					catch (Throwable t)
					{
						Activator.logError(Messages.CSSIndexQueryHelper_Error_Reading_Metadata, t);
					}
					finally
					{
						if (stream != null)
						{
							try
							{
								stream.close();
							}
							catch (IOException e)
							{
							}
						}
					}
				}
			}
		}

		return this._metadata;
	}

	/**
	 * getMetadataResources
	 * 
	 * @return
	 */
	protected String[] getMetadataResources()
	{
		return new String[] { "/metadata/css_metadata.xml" }; //$NON-NLS-1$
	}

	/**
	 * getProperties
	 * 
	 * @return
	 */
	public List<PropertyElement> getProperties()
	{
		return this.getMetadata().getProperties();
	}

	/**
	 * getProperty
	 * 
	 * @return
	 */
	public PropertyElement getProperty(String name)
	{
		PropertyElement result = null;

		if (name != null && name.length() > 0)
		{
			// TODO: optimize with name->property hash
			for (PropertyElement property : this.getProperties())
			{
				if (name.equals(property.getName()))
				{
					result = property;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * getReader
	 * 
	 * @return
	 */
	protected CSSIndexReader getReader()
	{
		if (this._reader == null)
		{
			this._reader = new CSSIndexReader();
		}

		return this._reader;
	}
}
