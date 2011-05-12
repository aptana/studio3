/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.vsdoc.parsing;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.xml.sax.Attributes;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.contentassist.MetadataReader;
import com.aptana.editor.js.JSPlugin;
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
import com.aptana.editor.js.sdoc.model.UserAgent;
import com.aptana.editor.js.sdoc.parsing.SDocParser;

public class VSDocReader extends MetadataReader
{
	private static final String METADATA_SCHEMA_XML = "/metadata/VSDocSchema.xml"; //$NON-NLS-1$

	private String _summary;
	private List<Tag> _tags;
	private List<Type> _exceptionTypes;
	private Parameter _currentParameter;
	private List<Type> _currentTypes;

	private DocumentationBlock _block;
	private SDocParser _typeParser;

	/**
	 * VSDocReader
	 */
	public VSDocReader()
	{
		this._typeParser = new SDocParser();
	}

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
		this._exceptionTypes = this.parseTypes(attributes.getValue("cref")); //$NON-NLS-1$

		this.startTextBuffer();
	}

	/**
	 * process para element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterPara(String ns, String name, String qname, Attributes attributes)
	{
		if (this.isBufferingText())
		{
			// grab (normalized) content and add new line before paragraph
			String text = this.getText() + "&x0A;"; //$NON-NLS-1$

			// restart text buffering
			this.startTextBuffer();

			// add updated text
			this.characters(text.toCharArray(), 0, text.length());
		}
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

		this._currentTypes = this.parseTypes(attributes.getValue("type")); //$NON-NLS-1$

		this.startTextBuffer();
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
		this._currentTypes = this.parseTypes(attributes.getValue("type")); //$NON-NLS-1$

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
	 * process userAgent element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterUserAgent(String ns, String name, String qname, Attributes attributes)
	{
		UserAgent ua = new UserAgent();
		String uaName = attributes.getValue("name"); //$NON-NLS-1$
		String version = attributes.getValue("version"); //$NON-NLS-1$

		ua.setName(uaName);
		ua.setVersion(version);

		this._tags.add(ua);
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

		if (this._exceptionTypes != null)
		{
			types.addAll(this._exceptionTypes);
		}
		else
		{
			types.add(new Type(JSTypeConstants.OBJECT_TYPE));
		}

		this._tags.add(new ExceptionTag(types, text));

		// clean up
		this._exceptionTypes = null;
	}

	/**
	 * Exit param element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitParam(String ns, String name, String qname)
	{
		String text = this.getText();
		List<Type> types = new ArrayList<Type>();

		if (this._currentTypes != null)
		{
			types.addAll(this._currentTypes);
		}
		else
		{
			types.add(new Type(JSTypeConstants.OBJECT_TYPE));
		}

		this._tags.add(new ParamTag(this._currentParameter, types, text));

		// reset
		this._currentParameter = null;
		this._currentTypes = null;
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

		if (this._currentTypes != null)
		{
			types.addAll(this._currentTypes);
		}
		else
		{
			types.add(new Type(JSTypeConstants.OBJECT_TYPE));
		}

		this._tags.add(new ReturnTag(types, text));

		// reset
		this._currentTypes = null;
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
		this._summary = this.resolveEntities(this.getText());
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
			return FileLocator.openStream(JSPlugin.getDefault().getBundle(), Path.fromPortableString(METADATA_SCHEMA_XML), false);
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

	/**
	 * parseTypes
	 * 
	 * @param types
	 */
	protected List<Type> parseTypes(String types)
	{
		List<Type> result = Collections.emptyList();

		if (StringUtil.isEmpty(types) == false)
		{
			try
			{
				result = this._typeParser.parseType(types);
			}
			catch (Exception e)
			{
				// default to Object if we couldn't parse the types
				result.add(new Type(JSTypeConstants.OBJECT_TYPE));
			}
		}
		else
		{
			// default to Object
			result.add(new Type(JSTypeConstants.OBJECT_TYPE));
		}

		return result;
	}
}
