package com.aptana.editor.js.contentassist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.net.URI;
import java.util.List;

import com.aptana.editor.js.contentassist.index.JSIndexConstants;
import com.aptana.editor.js.contentassist.index.JSIndexReader;
import com.aptana.editor.js.contentassist.model.FieldSelector;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

public class JSIndexQueryHelper
{
	private static final EnumSet<FieldSelector> PARENT_TYPES = EnumSet.of(FieldSelector.PARENT_TYPES);
	private static final String WINDOW_TYPE = "Window";

	private JSIndexReader _reader;

	/**
	 * JSContentAssistant
	 */
	public JSIndexQueryHelper()
	{
		this._reader = new JSIndexReader();
	}

	/**
	 * getCoreGlobal
	 * 
	 * @param name
	 * @param fields
	 * @return
	 */
	public PropertyElement getCoreGlobal(String name, EnumSet<FieldSelector> fields)
	{
		return this.getCoreTypeMember(WINDOW_TYPE, name, fields);
	}

	/**
	 * getCoreGlobalFunction
	 * 
	 * @param name
	 * @param fields
	 * @return
	 */
	public FunctionElement getCoreGlobalFunction(String name, EnumSet<FieldSelector> fields)
	{
		return this.getCoreTypeMethod(WINDOW_TYPE, name, fields);
	}

	/**
	 * getGlobals
	 * 
	 * @param fields
	 * @return
	 */
	public List<PropertyElement> getCoreGlobals(EnumSet<FieldSelector> fields)
	{
		return this.getCoreTypeProperties(WINDOW_TYPE, fields);
	}

	/**
	 * getCoreType
	 * 
	 * @param typeName
	 * @param fields
	 * @return
	 */
	public TypeElement getCoreType(String typeName, EnumSet<FieldSelector> fields)
	{
		return this._reader.getType(getIndex(), typeName, fields);
	}

	/**
	 * getCoreTypeMember
	 * 
	 * @param typeName
	 * @param memberName
	 * @param fields
	 * @return
	 */
	public PropertyElement getCoreTypeMember(String typeName, String memberName, EnumSet<FieldSelector> fields)
	{
		PropertyElement result = this.getCoreTypeProperty(typeName, memberName, fields);

		if (result == null)
		{
			result = this.getCoreTypeMethod(typeName, memberName, fields);
		}

		return result;
	}

	/**
	 * getCoreTypeMembers
	 * 
	 * @param typeName
	 * @param fields
	 * @return
	 */
	public List<PropertyElement> getCoreTypeMembers(String typeName, EnumSet<FieldSelector> fields)
	{
		List<PropertyElement> result = new ArrayList<PropertyElement>();
		
		result.addAll(this.getCoreTypeProperties(typeName, fields));
		result.addAll(this.getCoreTypeMethods(typeName, fields));
		
		return result;
	}
	
	/**
	 * getCoreTypeMethod
	 * 
	 * @param typeName
	 * @param methodName
	 * @param fields
	 * @return
	 */
	public FunctionElement getCoreTypeMethod(String typeName, String methodName, EnumSet<FieldSelector> fields)
	{
		FunctionElement result = null;
		
		try
		{
			result = this._reader.getFunction(getIndex(), typeName, methodName, fields);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * getCoreTypeMethods
	 * 
	 * @param typeName
	 * @param fields
	 * @return
	 */
	public List<FunctionElement> getCoreTypeMethods(String typeName, EnumSet<FieldSelector> fields)
	{
		List<FunctionElement> result = null;
		
		try
		{
			result = this._reader.getFunctions(getIndex(), typeName, fields);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * getCoreTypeProperties
	 * 
	 * @param typeName
	 * @param fields
	 * @return
	 */
	public List<PropertyElement> getCoreTypeProperties(String typeName, EnumSet<FieldSelector> fields)
	{
		List<PropertyElement> result = null;
		
		try
		{
			result = this._reader.getProperties(getIndex(), typeName, fields);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * getCoreTypeProperty
	 * 
	 * @param typeName
	 * @param propertyName
	 * @param fields
	 * @return
	 */
	public PropertyElement getCoreTypeProperty(String typeName, String propertyName, EnumSet<FieldSelector> fields)
	{
		PropertyElement result = null;
		
		try
		{
			result = this._reader.getProperty(getIndex(), typeName, propertyName, fields);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * getGlobal
	 * 
	 * @param index
	 * @param name
	 * @param fields
	 * @return
	 */
	public PropertyElement getGlobal(Index index, String name, EnumSet<FieldSelector> fields)
	{
		PropertyElement result = this.getProjectGlobal(index, name, fields);

		if (result == null)
		{
			result = this.getCoreGlobal(name, fields);
		}

		return result;
	}

	/**
	 * getGlobalFunction
	 * 
	 * @param index
	 * @param name
	 * @param fields
	 * @return
	 */
	public FunctionElement getGlobalFunction(Index index, String name, EnumSet<FieldSelector> fields)
	{
		FunctionElement result = this.getProjectGlobalFunction(index, name, fields);

		if (result == null)
		{
			result = this.getCoreGlobalFunction(name, fields);
		}

		return result;
	}

	/**
	 * getIndex
	 * 
	 * @return
	 */
	public static Index getIndex()
	{
		return IndexManager.getInstance().getIndex(URI.create(JSIndexConstants.METADATA));
	}

	/**
	 * getProjectGlobal
	 * 
	 * @param index
	 * @param name
	 * @param fields
	 * @return
	 */
	public PropertyElement getProjectGlobal(Index index, String name, EnumSet<FieldSelector> fields)
	{
		return this.getProjectTypeMember(index, WINDOW_TYPE, name, fields);
	}

	/**
	 * getProjectGlobalFunction
	 * 
	 * @param index
	 * @param name
	 * @param fields
	 * @return
	 */
	public FunctionElement getProjectGlobalFunction(Index index, String name, EnumSet<FieldSelector> fields)
	{
		return this.getProjectTypeMethod(index, WINDOW_TYPE, name, fields);
	}

	/**
	 * getProjectGlobals
	 * 
	 * @param index
	 * @param fields
	 * @return
	 */
	public List<PropertyElement> getProjectGlobals(Index index, EnumSet<FieldSelector> fields)
	{
		return this.getProjectTypeMembers(index, WINDOW_TYPE, fields);
	}

	/**
	 * getProjectType
	 * 
	 * @param index
	 * @param typeName
	 * @param fields
	 * @return
	 */
	public TypeElement getProjectType(Index index, String typeName, EnumSet<FieldSelector> fields)
	{
		return this._reader.getType(index, typeName, fields);
	}

	/**
	 * getProjectTypeMember
	 * 
	 * @param index
	 * @param typeName
	 * @param memberName
	 * @param fields
	 * @return
	 */
	public PropertyElement getProjectTypeMember(Index index, String typeName, String memberName, EnumSet<FieldSelector> fields)
	{
		PropertyElement result = this.getProjectTypeProperty(index, typeName, memberName, fields);

		if (result == null)
		{
			result = this.getProjectTypeMethod(index, typeName, memberName, fields);
		}

		return result;
	}
	
	/**
	 * getProjectTypeMembers
	 * 
	 * @param typeName
	 * @param fields
	 * @return
	 */
	public List<PropertyElement> getProjectTypeMembers(Index index, String typeName, EnumSet<FieldSelector> fields)
	{
		List<PropertyElement> result = new ArrayList<PropertyElement>();
		
		result.addAll(this.getProjectTypeProperties(index, typeName, fields));
		result.addAll(this.getProjectTypeMethods(index, typeName, fields));
		
		return result;
	}
	
	/**
	 * getProjectTypeMethod
	 * 
	 * @param index
	 * @param typeName
	 * @param methodName
	 * @param fields
	 * @return
	 */
	public FunctionElement getProjectTypeMethod(Index index, String typeName, String methodName, EnumSet<FieldSelector> fields)
	{
		FunctionElement result = null;
		
		try
		{
			result = this._reader.getFunction(index, typeName, methodName, fields);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * getProjectTypeMethods
	 * 
	 * @param index
	 * @param typeName
	 * @param fields
	 * @return
	 */
	public List<FunctionElement> getProjectTypeMethods(Index index, String typeName, EnumSet<FieldSelector> fields)
	{
		List<FunctionElement> result = null;
		
		try
		{
			result = this._reader.getFunctions(index, typeName, fields);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * getProjectTypeProperties
	 * 
	 * @param index
	 * @param typeName
	 * @param fields
	 * @return
	 */
	public List<PropertyElement> getProjectTypeProperties(Index index, String typeName, EnumSet<FieldSelector> fields)
	{
		List<PropertyElement> result = null;
		
		try
		{
			result = this._reader.getProperties(index, typeName, fields);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * getProjectTypeProperty
	 * 
	 * @param index
	 * @param typeName
	 * @param propertyName
	 * @param fields
	 * @return
	 */
	public PropertyElement getProjectTypeProperty(Index index, String typeName, String propertyName, EnumSet<FieldSelector> fields)
	{
		PropertyElement result = null;
		
		try
		{
			result = this._reader.getProperty(index, typeName, propertyName, fields);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * getType
	 * 
	 * @param index
	 * @param typeName
	 * @param fields
	 * @return
	 */
	public TypeElement getType(Index index, String typeName, EnumSet<FieldSelector> fields)
	{
		TypeElement result = this.getProjectType(index, typeName, fields);

		if (result == null)
		{
			result = this.getCoreType(typeName, fields);
		}

		return result;
	}

	/**
	 * getTypeMember
	 * 
	 * @param index
	 * @param typeName
	 * @param memberName
	 * @param fields
	 * @return
	 */
	public PropertyElement getTypeMember(Index index, String typeName, String memberName, EnumSet<FieldSelector> fields)
	{
		PropertyElement result = this.getTypeProperty(index, typeName, memberName, fields);
		
		if (result == null)
		{
			result = this.getTypeMethod(index, typeName, memberName, fields);
		}
		
		return result;
	}
	
	/**
	 * getTypeMembers
	 * 
	 * @param index
	 * @param typeName
	 * @param fields
	 * @return
	 */
	public List<PropertyElement> getTypeMembers(Index index, String typeName, EnumSet<FieldSelector> fields)
	{
		List<PropertyElement> result = new ArrayList<PropertyElement>();
		
		result.addAll(this.getCoreTypeMembers(typeName, fields));
		result.addAll(this.getProjectTypeMembers(index, typeName, fields));
		
		return result;
	}
	
	/**
	 * getTypeMethod
	 * 
	 * @param index
	 * @param typeName
	 * @param methodName
	 * @param fields
	 * @return
	 */
	public FunctionElement getTypeMethod(Index index, String typeName, String methodName, EnumSet<FieldSelector> fields)
	{
		FunctionElement result = this.getProjectTypeMethod(index, typeName, methodName, fields);
		
		if (result == null)
		{
			result = this.getCoreTypeMethod(typeName, methodName, fields);
		}
		
		return result;
	}
	
	/**
	 * getTypeMethodRecursive
	 * 
	 * @param index
	 * @param typeName
	 * @param methodName
	 * @param fields
	 * @return
	 */
	public FunctionElement getTypeMethodRecursive(Index index, String typeName, String methodName, EnumSet<FieldSelector> fields)
	{
		FunctionElement result = null;
		LinkedList<String> types = new LinkedList<String>();

		types.add(typeName);

		try
		{
			while (types.size() > 0)
			{
				String currentType = types.remove();

				// check in core globals
				result = this._reader.getFunction(index, currentType, methodName, fields);

				if (result != null)
				{
					break;
				}
				else
				{
					TypeElement type = this._reader.getType(index, currentType, PARENT_TYPES);

					for (String superclass : type.getParentTypes())
					{
						types.add(superclass);
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * getTypeMethods
	 * 
	 * @param index
	 * @param typeName
	 * @param fields
	 * @return
	 */
	public List<FunctionElement> getTypeMethods(Index index, String typeName, EnumSet<FieldSelector> fields)
	{
		List<FunctionElement> result = this.getProjectTypeMethods(index, typeName, fields);

		if (result == null)
		{
			result = this.getCoreTypeMethods(typeName, fields);
		}

		return result;
	}

	/**
	 * getTypeProperties
	 * 
	 * @param index
	 * @param typeName
	 * @param fields
	 * @return
	 */
	public List<PropertyElement> getTypeProperties(Index index, String typeName, EnumSet<FieldSelector> fields)
	{
		List<PropertyElement> result = this.getProjectTypeProperties(index, typeName, fields);

		if (result == null)
		{
			result = this.getCoreTypeProperties(typeName, fields);
		}

		return result;
	}

	/**
	 * getTypeProperty
	 * 
	 * @param index
	 * @param typeName
	 * @param propertyName
	 * @param fields
	 * @return
	 */
	public PropertyElement getTypeProperty(Index index, String typeName, String propertyName, EnumSet<FieldSelector> fields)
	{
		PropertyElement result = this.getProjectTypeProperty(index, typeName, propertyName, fields);

		if (result == null)
		{
			result = this.getCoreTypeProperty(typeName, propertyName, fields);
		}

		return result;
	}

	/**
	 * getTypePropertyRecursive
	 * 
	 * @param index
	 * @param typeName
	 * @param propertyName
	 * @param fields
	 * @return
	 */
	public PropertyElement getTypePropertyRecursive(Index index, String typeName, String propertyName, EnumSet<FieldSelector> fields)
	{
		PropertyElement result = null;
		LinkedList<String> types = new LinkedList<String>();

		types.add(typeName);

		try
		{
			while (types.size() > 0)
			{
				String currentType = types.remove();

				// check in core globals
				result = this._reader.getProperty(index, currentType, propertyName, fields);

				if (result != null)
				{
					break;
				}
				else
				{
					// prevent possible infinite loop if Object returns Object
					// as its super-type
					if (currentType.equals("Object") == false)
					{
						TypeElement type = this._reader.getType(index, currentType, PARENT_TYPES);

						for (String superclass : type.getParentTypes())
						{
							types.add(superclass);
						}
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return result;
	}
}
