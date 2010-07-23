package com.aptana.editor.js.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.editor.js.contentassist.model.ContentSelector;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.index.core.Index;

public class JSSymbolTypeInferrer
{
	private static final EnumSet<ContentSelector> MEMBER_CONTENT = EnumSet.of(ContentSelector.NAME, ContentSelector.TYPES, ContentSelector.RETURN_TYPES);

	private Index _index;
	private JSScope _globals;
	private List<TypeElement> _generatedTypes;

	public JSSymbolTypeInferrer(Index index, JSScope globals)
	{
		this._index = index;
		this._globals = globals;
	}
	
	/**
	 * generateType
	 * 
	 * @return
	 */
	private TypeElement generateType()
	{
		// create new type and give it a unique name
		TypeElement result = new TypeElement();
		result.setName(JSContentAssistUtil.getUniqueTypeName());

		// save type for future reference
		if (this._generatedTypes == null)
		{
			this._generatedTypes = new ArrayList<TypeElement>();
		}

		this._generatedTypes.add(result);

		return result;
	}

	/**
	 * getGeneratedTypes
	 * 
	 * @return
	 */
	public List<TypeElement> getGeneratedTypes()
	{
		List<TypeElement> result = this._generatedTypes;
		
		if (result == null)
		{
			result = Collections.emptyList();
		}
		
		return result;
	}
	
	/**
	 * getScopeProperties
	 * 
	 * @return
	 */
	public List<PropertyElement> getScopeProperties()
	{
		List<PropertyElement> result = new ArrayList<PropertyElement>();
		
		for (String symbol : this._globals.getLocalSymbolNames())
		{
			PropertyElement p = this.getSymbolPropertyElement(symbol);

			result.add(p);
		}

		return result;
	}

	/**
	 * getSymbolPropertyElement
	 * 
	 * @param symbol
	 * @return
	 */
	public PropertyElement getSymbolPropertyElement(String symbol)
	{
		return this.getSymbolPropertyElement(this._globals.getObject(), symbol);
	}
	
	/**
	 * getSymbolPropertyElement
	 * 
	 * @param name
	 * @return
	 */
	public PropertyElement getSymbolPropertyElement(JSObject activeObject, String symbol)
	{
		// create resulting property element
		PropertyElement result = new PropertyElement();
		result.setName(symbol);

		JSObject property = activeObject.getProperty(symbol);
		List<String> types = new ArrayList<String>();

		// infer types
		for (JSNode value : property.getValues())
		{
			JSTypeInferrer inferrer = new JSTypeInferrer(this._globals);

			inferrer.visit(value);

			types.addAll(inferrer.getTypes());
		}

		// process additional properties, possibly creating a new type in the
		// process
		if (property.hasProperties())
		{
			this.processSubProperties(property, types);
		}

		// add types to property
		for (String typeName : types)
		{
			result.addType(typeName);
		}

		return result;
	}

	/**
	 * getTypePropertyMap
	 * 
	 * @param type
	 * @return
	 */
	private Map<String, PropertyElement> getTypePropertyMap(List<String> types)
	{
		JSIndexQueryHelper helper = new JSIndexQueryHelper();

		// create a set from the specified types and their ancestors
		Set<String> ancestors = new HashSet<String>();

		for (String type : types)
		{
			ancestors.add(type);
			ancestors.addAll(helper.getTypeAncestorNames(this._index, type));
		}

		List<String> typesAndAncestors = new ArrayList<String>(ancestors);
		List<PropertyElement> typeMembers = helper.getTypeMembers(this._index, typesAndAncestors, MEMBER_CONTENT);
		Map<String, PropertyElement> propertyMap = new HashMap<String, PropertyElement>();

		for (PropertyElement propertyElement : typeMembers)
		{
			propertyMap.put(propertyElement.getName(), propertyElement);
		}

		return propertyMap;
	}

	/**
	 * processSubProperties
	 * 
	 * @param activeObject
	 * @param types
	 */
	private void processSubProperties(JSObject activeObject, List<String> types)
	{
		Map<String, PropertyElement> propertyMap = this.getTypePropertyMap(types);
		List<String> additionalProperties = new ArrayList<String>();

		// create a list of properties that are not in the ancestor chain of the
		// types passed into this method
		for (String name : activeObject.getPropertyNames())
		{
			// TODO: Treat as new property if types don't match?
			if (propertyMap.containsKey(name) == false)
			{
				additionalProperties.add(name);
			}
		}

		// if we have new properties, create a new sub-type and add the new
		// properties to it
		if (additionalProperties.isEmpty() == false)
		{
			// create new type
			TypeElement subType = this.generateType();

			// make the current list of types parent types of this type
			for (String superType : types)
			{
				subType.addParentType(superType);
			}

			// reset the type list to this newly generated type
			types.clear();
			types.add(subType.getName());

			// infer types of the
			for (String pname : additionalProperties)
			{
				PropertyElement p = this.getSymbolPropertyElement(activeObject, pname);

				subType.addProperty(p);
			}
		}
	}
}
