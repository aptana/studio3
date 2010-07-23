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
import com.aptana.editor.js.sdoc.model.DocumentationBlock;
import com.aptana.index.core.Index;

public class JSSymbolTypeInferrer
{
	private static final EnumSet<ContentSelector> MEMBER_CONTENT = EnumSet.of(ContentSelector.NAME, ContentSelector.TYPES, ContentSelector.RETURN_TYPES);
	private static List<TypeElement> _generatedTypes;

	private Index _index;
	private JSScope _activeScope;

	/**
	 * JSSymbolTypeInferrer
	 * 
	 * @param index
	 * @param activeScope
	 */
	public JSSymbolTypeInferrer(Index index, JSScope activeScope)
	{
		this._index = index;
		this._activeScope = activeScope;
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
		result.setName(JSTypeUtil.getUniqueTypeName());

		// save type for future reference
		if (_generatedTypes == null)
		{
			_generatedTypes = new ArrayList<TypeElement>();
		}

		_generatedTypes.add(result);

		return result;
	}

	/**
	 * getAdditionalProperties
	 * 
	 * @param activeObject
	 * @param types
	 * @return
	 */
	private List<String> getAdditionalProperties(JSObject activeObject, List<String> types)
	{
		Map<String, PropertyElement> propertyMap = this.getTypePropertyMap(types);
		List<String> additionalProperties = new ArrayList<String>();

		// create a list of properties that are not in the ancestor chain of the
		// types passed into this method
		for (String name : activeObject.getPropertyNames())
		{
			// TODO: Treat as new property if names match but not their types?
			if (propertyMap.containsKey(name) == false)
			{
				additionalProperties.add(name);
			}
		}
		
		return additionalProperties;
	}

	/**
	 * getGeneratedTypes
	 * 
	 * @return
	 */
	public List<TypeElement> getGeneratedTypes()
	{
		List<TypeElement> result = _generatedTypes;

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

		for (String symbol : this._activeScope.getLocalSymbolNames())
		{
			PropertyElement p = this.getSymbolPropertyElement(symbol);

			result.add(p);
		}

		return result;
	}

	/**
	 * getSymbolPropertyElement
	 * 
	 * @param name
	 * @return
	 */
	public PropertyElement getSymbolPropertyElement(JSObject activeObject, String symbol)
	{
		List<String> types = new ArrayList<String>();

		// create resulting property element
		PropertyElement result = new PropertyElement();
		result.setName(symbol);

		// try to grab property from active object
		JSObject property = activeObject.getProperty(symbol);

		// if no property, then try a scope-based lookup
		if (property == null)
		{
			property = this._activeScope.getSymbol(symbol);
		}

		if (property != null)
		{
			if (property.hasTypes())
			{
				types = property.getTypes();
			}
			else
			{
				// infer types
				for (JSNode value : property.getValues())
				{
					DocumentationBlock docs = value.getDocumentation();
	
					if (docs != null)
					{
						JSTypeUtil.applyDocumentation(result, docs);
					}
					else
					{
						JSTypeInferrer inferrer = new JSTypeInferrer(this._activeScope);
	
						inferrer.visit(value);
	
						types.addAll(inferrer.getTypes());
					}
				}
	
				// process additional properties, possibly creating a new type in the
				// process
				if (property.hasProperties())
				{
					List<String> additionalProperties = getAdditionalProperties(property, types);
	
					if (additionalProperties.isEmpty() == false)
					{
						// create new type
						TypeElement subType = this.generateType();
	
						// make the current list of types parent types of this type
						for (String superType : types)
						{
							subType.addParentType(superType);
						}
	
						// reset list to contain only this newly generated type
						types.clear();
						types.add(subType.getName());
						
						property.addType(subType.getName());
	
						// infer types of the
						for (String pname : additionalProperties)
						{
							PropertyElement p = this.getSymbolPropertyElement(property, pname);
	
							subType.addProperty(p);
						}
					}
				}
				else
				{
					for (String typeName : types)
					{
						property.addType(typeName);
					}
				}
			}
		}

		// add types to property
		for (String typeName : types)
		{
			result.addType(typeName);
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
		return this.getSymbolPropertyElement(this._activeScope.getObject(), symbol);
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
}
