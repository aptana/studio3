package com.aptana.editor.js.vsdoc.parsing;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import com.aptana.editor.common.contentassist.MetadataReader;
import com.aptana.editor.js.sdoc.model.Block;
import com.aptana.editor.js.sdoc.model.ParamTag;
import com.aptana.editor.js.sdoc.model.Parameter;
import com.aptana.editor.js.sdoc.model.Tag;
import com.aptana.editor.js.sdoc.model.TagType;
import com.aptana.editor.js.sdoc.model.TagWithTypes;
import com.aptana.editor.js.sdoc.model.Type;
import com.aptana.editor.js.sdoc.model.Usage;

public class VSDocReader extends MetadataReader
{
	private static final String METADATA_SCHEMA_XML = "/metadata/VSDocSchema.xml"; //$NON-NLS-1$

	private String _summary;
	private List<Tag> _tags;
	private Parameter _currentParameter;
	private String _currentType;

	private Block _block;
	
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
	 * process param element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterParam(String ns, String name, String qname, Attributes attributes)
	{
		String parameterName = attributes.getValue("name");
		boolean optional = Boolean.parseBoolean(attributes.getValue("optional"));
		boolean parameterArray = Boolean.parseBoolean(attributes.getValue("parameterArray"));
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
		
		this._currentType = attributes.getValue("type");
		
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
		this._currentType = attributes.getValue("type");
		
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
		this._block = new Block(this._summary, this._tags, 0, 0);
		
		// clean up
		this._summary = null;
		this._tags = null;
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
		types.add(new Type(this._currentType != null ? this._currentType : "Object"));
		
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
		
		this._tags.add(new Tag(TagType.PRIVATE, text));
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
		types.add(new Type(this._currentType != null ? this._currentType : "Object"));
		
		this._tags.add(new TagWithTypes(TagType.RETURN, types, text));
		
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
	public Block getBlock()
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
		return this.getClass().getResourceAsStream(METADATA_SCHEMA_XML);
	}
}
