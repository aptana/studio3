/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mortbay.util.ajax.JSON.Output;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ObjectUtil;
import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.index.core.IndexUtil;

public class PropertyElement extends BaseElement<PropertyElement.Property>
{
	enum Property implements IPropertyInformation<PropertyElement>
	{
		NAME(Messages.PropertyElement_Name)
		{
			public Object getPropertyValue(PropertyElement node)
			{
				return node.getName();
			}
		},
		DESCRIPTION(Messages.PropertyElement_Description)
		{
			public Object getPropertyValue(PropertyElement node)
			{
				return node.getDescription();
			}
		},
		OWNING_TYPE(Messages.PropertyElement_OwningType)
		{
			public Object getPropertyValue(PropertyElement node)
			{
				return node.getOwningType();
			}
		},
		CLASS_PROPERTY(Messages.PropertyElement_StaticProperty)
		{
			public Object getPropertyValue(PropertyElement node)
			{
				return node._isClassProperty;
			}
		},
		INSTANCE_PROPERTY(Messages.PropertyElement_InstanceProperty)
		{
			public Object getPropertyValue(PropertyElement node)
			{
				return node._isInstanceProperty;
			}
		},
		RETURN_TYPES(Messages.PropertyElement_ReturnTypes)
		{
			public Object getPropertyValue(PropertyElement node)
			{
				if (node instanceof FunctionElement)
				{
					return StringUtil.join(", ", ((FunctionElement) node).getReturnTypeNames()); //$NON-NLS-1$
				}
				else
				{
					return StringUtil.EMPTY;
				}
			}
		},
		TYPES(Messages.PropertyElement_Types)
		{
			public Object getPropertyValue(PropertyElement node)
			{
				return StringUtil.join(", ", node.getTypeNames()); //$NON-NLS-1$
			}
		},
		DOCUMENTS(Messages.PropertyElement_Documents)
		{
			public Object getPropertyValue(PropertyElement node)
			{
				return StringUtil.join(", ", node.getDocuments()); //$NON-NLS-1$
			}
		};

		private String header;

		private Property(String header) // $codepro.audit.disable unusedMethod
		{
			this.header = header;
		}

		public String getHeader()
		{
			return header;
		}
	}

	private static final String EXAMPLES_PROPERTY = "examples"; //$NON-NLS-1$
	private static final String TYPES_PROPERTY = "types"; //$NON-NLS-1$
	private static final String IS_INTERNAL_PROPERTY = "isInternal"; //$NON-NLS-1$
	private static final String IS_INSTANCE_PROPERTY = "isInstanceProperty"; //$NON-NLS-1$
	private static final String IS_CLASS_PROPERTY = "isClassProperty"; //$NON-NLS-1$
	private static final String OWNING_TYPE_PROPERTY = "owningType"; //$NON-NLS-1$

	private String _owningType;
	private boolean _isInstanceProperty;
	private boolean _isClassProperty;
	private boolean _isInternal;
	private List<ReturnTypeElement> _types;
	private List<String> _examples;

	/**
	 * PropertyElement
	 */
	public PropertyElement()
	{
	}

	/**
	 * PropertyElement
	 * 
	 * @param base
	 */
	public PropertyElement(PropertyElement base)
	{
		// NOTE: this is a shallow clone, so references are shared in lists
		this._owningType = base.getOwningType();
		this._isInstanceProperty = base.isInstanceProperty();
		this._isClassProperty = base.isClassProperty();
		this._isInternal = base.isInternal();
		this._types = new ArrayList<ReturnTypeElement>(base.getTypes());
		this._examples = new ArrayList<String>(base.getExamples());
	}

	/**
	 * addExample
	 * 
	 * @param example
	 */
	public void addExample(String example)
	{
		if (example != null && example.length() > 0)
		{
			if (this._examples == null)
			{
				this._examples = new ArrayList<String>();
			}

			this._examples.add(example);
		}
	}

	/**
	 * addType
	 * 
	 * @param type
	 */
	public void addType(ReturnTypeElement type)
	{
		if (type != null)
		{
			if (this._types == null)
			{
				this._types = new ArrayList<ReturnTypeElement>();
			}

			int index = this._types.indexOf(type);

			if (index != -1)
			{
				this._types.set(index, type);
			}
			else
			{
				this._types.add(type);
			}
		}
	}

	/**
	 * addType
	 * 
	 * @param type
	 */
	public void addType(String type)
	{
		if (type != null && type.length() > 0)
		{
			ReturnTypeElement returnType = new ReturnTypeElement();

			returnType.setType(type);

			this.addType(returnType);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return toSource().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof PropertyElement)
		{
			return ObjectUtil.areEqual(toSource(), ((PropertyElement) obj).toSource());
		}
		else
		{
			return super.equals(obj);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.BaseElement#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void fromJSON(Map object)
	{
		super.fromJSON(object);

		this.setOwningType(StringUtil.getStringValue(object.get(OWNING_TYPE_PROPERTY)));
		this.setIsClassProperty(Boolean.TRUE == object.get(IS_CLASS_PROPERTY)); // $codepro.audit.disable useEquals
		this.setIsInstanceProperty(Boolean.TRUE == object.get(IS_INSTANCE_PROPERTY)); // $codepro.audit.disable
																						// useEquals
		this.setIsInternal(Boolean.TRUE == object.get(IS_INTERNAL_PROPERTY)); // $codepro.audit.disable useEquals

		this._types = IndexUtil.createList(object.get(TYPES_PROPERTY), ReturnTypeElement.class);
		this._examples = IndexUtil.createList(object.get(EXAMPLES_PROPERTY));
	}

	/**
	 * getExamples
	 * 
	 * @return
	 */
	public List<String> getExamples()
	{
		return CollectionsUtil.getListValue(this._examples);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.BaseElement#getPropertyInfoSet()
	 */
	@Override
	protected Set<Property> getPropertyInfoSet()
	{
		return EnumSet.allOf(Property.class);
	}

	/**
	 * getOwningType
	 * 
	 * @return
	 */
	public String getOwningType()
	{
		return StringUtil.getStringValue(this._owningType);
	}

	/**
	 * getTypeNames
	 * 
	 * @return
	 */
	public List<String> getTypeNames()
	{
		List<String> result = new ArrayList<String>();

		for (ReturnTypeElement type : this.getTypes())
		{
			result.add(type.getType());
		}

		return result;
	}

	/**
	 * getTypes
	 * 
	 * @return
	 */
	public List<ReturnTypeElement> getTypes()
	{
		return CollectionsUtil.getListValue(this._types);
	}

	/**
	 * isClassProperty
	 * 
	 * @return
	 */
	public boolean isClassProperty()
	{
		return this._isClassProperty;
	}

	/**
	 * isInstanceProperty
	 * 
	 * @return
	 */
	public boolean isInstanceProperty()
	{
		return this._isInstanceProperty;
	}

	/**
	 * isInternal
	 * 
	 * @return
	 */
	public boolean isInternal()
	{
		return this._isInternal;
	}

	/**
	 * setIsClassProperty
	 * 
	 * @param value
	 */
	public void setIsClassProperty(boolean value)
	{
		this._isClassProperty = value;
	}

	/**
	 * setIsInstanceProperty
	 * 
	 * @param value
	 */
	public void setIsInstanceProperty(boolean value)
	{
		this._isInstanceProperty = value;
	}

	/**
	 * setIsInternal
	 * 
	 * @param value
	 */
	public void setIsInternal(boolean value)
	{
		this._isInternal = value;
	}

	/**
	 * setOwningType
	 * 
	 * @param type
	 */
	public void setOwningType(String type)
	{
		this._owningType = type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.BaseElement#toJSON(org.mortbay.util.ajax.JSON.Output)
	 */
	@Override
	public void toJSON(Output out)
	{
		super.toJSON(out);

		out.add(OWNING_TYPE_PROPERTY, this.getOwningType());
		out.add(IS_CLASS_PROPERTY, this.isClassProperty());
		out.add(IS_INSTANCE_PROPERTY, this.isInstanceProperty());
		out.add(IS_INTERNAL_PROPERTY, this.isInternal());
		out.add(TYPES_PROPERTY, this.getTypes());
		out.add(EXAMPLES_PROPERTY, this.getExamples());
	}

	/**
	 * toSource
	 * 
	 * @param printer
	 */
	public void toSource(SourcePrinter printer)
	{
		printer.printIndent();

		if (this.isClassProperty())
		{
			printer.print("static "); //$NON-NLS-1$
		}
		if (this.isInternal())
		{
			printer.print("internal "); //$NON-NLS-1$
		}

		printer.print(this.getName());
		printer.print(" : "); //$NON-NLS-1$

		List<String> types = this.getTypeNames();

		if (types != null && types.size() > 0)
		{
			printer.print(StringUtil.join(",", this.getTypeNames())); //$NON-NLS-1$
		}
		else
		{
			printer.print(JSTypeConstants.UNDEFINED_TYPE);
		}
	}
}
