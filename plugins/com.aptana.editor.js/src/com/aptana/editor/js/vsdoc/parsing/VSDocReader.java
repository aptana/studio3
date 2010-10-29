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
package com.aptana.editor.js.vsdoc.parsing;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.xml.sax.Attributes;

import com.aptana.editor.common.contentassist.MetadataReader;
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.sdoc.model.DocumentationBlock;
import com.aptana.editor.js.sdoc.model.ExampleTag;
import com.aptana.editor.js.sdoc.model.ExceptionTag;
import com.aptana.editor.js.sdoc.model.ParamTag;
import com.aptana.editor.js.sdoc.model.Parameter;
import com.aptana.editor.js.sdoc.model.PrivateTag;
import com.aptana.editor.js.sdoc.model.ReturnTag;
import com.aptana.editor.js.sdoc.model.SeeTag;
import com.aptana.editor.js.sdoc.model.Tag;
import com.aptana.editor.js.sdoc.model.Type;
import com.aptana.editor.js.sdoc.model.Usage;

public class VSDocReader extends MetadataReader
{
	private static final String METADATA_SCHEMA_XML = "/metadata/VSDocSchema.xml"; //$NON-NLS-1$

	private String _summary;
	private List<Tag> _tags;
	private String _exceptionType;
	private Parameter _currentParameter;
	private String _currentType;

	private DocumentationBlock _block;

	/**
	 * process docs element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterDocs(String ns, String name, String qname, Attributes attributes)
	{
		this._tags = new ArrayList<Tag>();
	}

	/**
	 * process exception element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterException(String ns, String name, String qname, Attributes attributes)
	{
		this._exceptionType = attributes.getValue("cref"); //$NON-NLS-1$

		this.startTextBuffer();
	}

	/**
	 * process param element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterParam(String ns, String name, String qname, Attributes attributes)
	{
		String parameterName = attributes.getValue("name"); //$NON-NLS-1$
		boolean optional = Boolean.parseBoolean(attributes.getValue("optional")); //$NON-NLS-1$
		boolean parameterArray = Boolean.parseBoolean(attributes.getValue("parameterArray")); //$NON-NLS-1$
		Usage usage;

		if (optional)
		{
			if (parameterArray)
			{
				usage = Usage.ZERO_OR_MORE;
			}
			else
			{
				usage = Usage.OPTIONAL;
			}
		}
		else
		{
			if (parameterArray)
			{
				usage = Usage.ONE_OR_MORE;
			}
			else
			{
				usage = Usage.REQUIRED;
			}
		}

		this._currentParameter = new Parameter(parameterName);
		this._currentParameter.setUsage(usage);

		this._currentType = attributes.getValue("type"); //$NON-NLS-1$

		this.startTextBuffer();
	}

	/**
	 * process see element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterSee(String ns, String name, String qname, Attributes attributes)
	{
		String type = attributes.getValue("cref"); //$NON-NLS-1$

		this._tags.add(new SeeTag(type));
	}

	/**
	 * process returns element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterReturns(String ns, String name, String qname, Attributes attributes)
	{
		this._currentType = attributes.getValue("type"); //$NON-NLS-1$

		this.startTextBuffer();
	}

	/**
	 * Exit docs element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitDocs(String ns, String name, String qname)
	{
		this._block = new DocumentationBlock(this._summary, this._tags);

		// clean up
		this._summary = null;
		this._tags = null;
	}

	/**
	 * Exit example element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitExample(String ns, String name, String qname)
	{
		String text = this.getText();

		this._tags.add(new ExampleTag(text));
	}

	/**
	 * Exit exception element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitException(String ns, String name, String qname)
	{
		String text = this.getText();

		List<Type> types = new ArrayList<Type>();
		types.add(new Type(this._exceptionType != null ? this._exceptionType : JSTypeConstants.OBJECT_TYPE));

		this._tags.add(new ExceptionTag(types, text));

		// clean up
		this._exceptionType = null;
	}

	/**
	 * Exit para element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitParam(String ns, String name, String qname)
	{
		String text = this.getText();

		List<Type> types = new ArrayList<Type>();
		types.add(new Type(this._currentType != null ? this._currentType : JSTypeConstants.OBJECT_TYPE));

		this._tags.add(new ParamTag(this._currentParameter, types, text));

		// reset
		this._currentParameter = null;
		this._currentType = null;
	}

	/**
	 * Exit private element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void exitPrivate(String ns, String name, String qname)
	{
		String text = this.getText();

		this._tags.add(new PrivateTag(text));
	}

	/**
	 * Exit returns element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitReturns(String ns, String name, String qname)
	{
		String text = this.getText();

		List<Type> types = new ArrayList<Type>();
		types.add(new Type(this._currentType != null ? this._currentType : "Object")); //$NON-NLS-1$

		this._tags.add(new ReturnTag(types, text));

		// reset
		this._currentType = null;
	}

	/**
	 * Exit summary element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitSummary(String ns, String name, String qname)
	{
		this._summary = this.getText();
	}

	/**
	 * getBlock
	 * 
	 * @return
	 */
	public DocumentationBlock getBlock()
	{
		return this._block;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataReader#getSchemaStream()
	 */
	@Override
	protected InputStream getSchemaStream()
	{
		try
		{
			return FileLocator.openStream(Activator.getDefault().getBundle(),
					Path.fromPortableString(METADATA_SCHEMA_XML), false);
		}
		catch (IOException e)
		{
			return this.getClass().getResourceAsStream(METADATA_SCHEMA_XML);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataReader#getText()
	 */
	@Override
	protected String getText()
	{
		return this.normalizeText(super.getText());
	}
}
