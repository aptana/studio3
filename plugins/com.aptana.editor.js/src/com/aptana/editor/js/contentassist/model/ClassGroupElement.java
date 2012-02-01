/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.model;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.aptana.editor.js.contentassist.JSIndexQueryHelper;
import com.aptana.editor.js.contentassist.index.IJSIndexConstants;
import com.aptana.editor.js.contentassist.index.JSIndexReader;
import com.aptana.editor.js.inferencing.JSTypeUtil;
import com.aptana.index.core.CategoryInfo;
import com.aptana.index.core.Index;
import com.aptana.index.core.ui.views.IPropertyInformation;

/**
 * ClassGroupElement
 */
public class ClassGroupElement extends BaseElement<ClassGroupElement.Property>
{
	enum Category
	{
		// @formatter:off
		COUNTS(Messages.ClassGroupElement_Counts),
		MINS(Messages.ClassGroupElement_MinLengths),
		MAXS(Messages.ClassGroupElement_MaxLengths),
		SUMS(Messages.ClassGroupElement_Sums),
		MEDIANS(Messages.ClassGroupElement_Medians),
		AVERAGES(Messages.ClassGroupElement_Averages);
		// @formatter:on

		private String name;

		private Category(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}
	}

	enum Property implements IPropertyInformation<ClassGroupElement>
	{
		CLASS_COUNT(Messages.ClassGroupElement_ClassCount, Category.COUNTS)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getClassCount();
			}
		},

		RAW_TYPE_COUNT(Messages.ClassGroupElement_TypeCount, Category.COUNTS)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getTypeInfo().count;
			}
		},
		MIN_TYPE_LENGTH(Messages.ClassGroupElement_TypeMinLength, Category.MINS)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getTypeInfo().minLength;
			}
		},
		MAX_TYPE_LENGTH(Messages.ClassGroupElement_TypeMaxLength, Category.MAXS)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getTypeInfo().maxLength;
			}
		},
		SUM_TYPE_LENGTHS(Messages.ClassGroupElement_TypeLengthSums, Category.SUMS)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getTypeInfo().sum;
			}
		},
		MEDIAN_TYPE_LENGTH(Messages.ClassGroupElement_TypeMedianLength, Category.MEDIANS)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getTypeInfo().median;
			}
		},
		AVERAGE_TYPE_LENGTH(Messages.ClassGroupElement_TypeAverageLength, Category.AVERAGES)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getTypeInfo().average;
			}
		},

		RAW_PROPERTY_COUNT(Messages.ClassGroupElement_PropertyCount, Category.COUNTS)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getPropertyInfo().count;
			}
		},
		MIN_PROPERTY_LENGTH(Messages.ClassGroupElement_PropertyMinLength, Category.MINS)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getPropertyInfo().minLength;
			}
		},
		MAX_PROPERTY_LENGTH(Messages.ClassGroupElement_PropertyMaxLength, Category.MAXS)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getPropertyInfo().maxLength;
			}
		},
		SUM_PROPERTY_LENGTHS(Messages.ClassGroupElement_PropertyLengthSums, Category.SUMS)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getPropertyInfo().sum;
			}
		},
		MEDIAN_PROPERTY_LENGTH(Messages.ClassGroupElement_PropertyMedianLength, Category.MEDIANS)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getPropertyInfo().median;
			}
		},
		AVERAGE_PROPERTY_LENGTH(Messages.ClassGroupElement_PropertyAverageLength, Category.AVERAGES)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getPropertyInfo().average;
			}
		},

		RAW_FUNCTION_COUNT(Messages.ClassGroupElement_MethodCount, Category.COUNTS)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getFunctionInfo().count;
			}
		},
		MIN_FUNCTION_LENGTH(Messages.ClassGroupElement_MethodMinLength, Category.MINS)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getFunctionInfo().minLength;
			}
		},
		MAX_FUNCTION_LENGTH(Messages.ClassGroupElement_MethodMaxLength, Category.MAXS)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getFunctionInfo().maxLength;
			}
		},
		SUM_FUNCTION_LENGTH(Messages.ClassGroupElement_MethodLengthSums, Category.SUMS)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getFunctionInfo().sum;
			}
		},
		MEDIAN_FUNCTION_LENGTH(Messages.ClassGroupElement_MethodMedianLength, Category.MEDIANS)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getFunctionInfo().median;
			}
		},
		AVERAGE_FUNCTION_LENGTH(Messages.ClassGroupElement_MethodAverageLength, Category.AVERAGES)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getFunctionInfo().average;
			}
		};

		private String header;
		private String category;

		private Property(String header) // $codepro.audit.disable unusedMethod
		{
			this.header = header;
		}

		private Property(String header, Category category)
		{
			this.header = header;
			this.category = category.getName();
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
	private List<ClassElement> classes;
	private int typeCount = -1;
	private CategoryInfo typeInfo;
	private CategoryInfo propertyInfo;
	private CategoryInfo functionInfo;

	/**
	 * ClassGroupElement
	 * 
	 * @param name
	 * @param classes
	 */
	public ClassGroupElement(String name, Index index)
	{
		this.index = index;
		setName(name);
	}

	/**
	 * getClassCount
	 * 
	 * @return
	 */
	public int getClassCount()
	{
		if (typeCount == -1)
		{
			JSIndexReader reader = new JSIndexReader();
			List<String> typeNames = reader.getTypeNames(index);
			Set<String> mergedTypeNames = new HashSet<String>();

			for (String typeName : typeNames)
			{
				boolean isClassType = JSTypeUtil.isClassType(typeName);
				String baseName = isClassType ? JSTypeUtil.getClassType(typeName) : typeName;

				mergedTypeNames.add(baseName);
			}

			typeCount = mergedTypeNames.size();
		}

		return typeCount;
	}

	/**
	 * @return the classes
	 */
	public List<ClassElement> getClasses()
	{
		if (classes == null)
		{
			JSIndexQueryHelper queryHelper = new JSIndexQueryHelper();
			List<TypeElement> types = queryHelper.getTypes(index);

			classes = JSTypeUtil.typesToClasses(types);
		}

		return classes;
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
	 * Return the grand total number of function entries in the index.
	 * 
	 * @return
	 */
	public CategoryInfo getFunctionInfo()
	{
		if (functionInfo == null)
		{
			JSIndexReader reader = new JSIndexReader();

			functionInfo = reader.getCategoryInfo(index, IJSIndexConstants.FUNCTION);
		}

		return functionInfo;
	}

	/**
	 * Return the grand total number of property entries in the index.
	 * 
	 * @return
	 */
	public CategoryInfo getPropertyInfo()
	{
		if (propertyInfo == null)
		{
			JSIndexReader reader = new JSIndexReader();

			propertyInfo = reader.getCategoryInfo(index, IJSIndexConstants.PROPERTY);
		}

		return propertyInfo;
	}

	/**
	 * Return the grand total number of type entries in the index. This includes Class<MyClass> and MyClass.
	 * 
	 * @return
	 */
	public CategoryInfo getTypeInfo()
	{
		if (typeInfo == null)
		{
			JSIndexReader reader = new JSIndexReader();

			typeInfo = reader.getCategoryInfo(index, IJSIndexConstants.TYPE);
		}

		return typeInfo;
	}

	/**
	 * Try to determine if this element has any content without actually loading classes into memory
	 * 
	 * @return
	 */
	public boolean hasChildren()
	{
		return getTypeInfo().count > 0;
	}
}
