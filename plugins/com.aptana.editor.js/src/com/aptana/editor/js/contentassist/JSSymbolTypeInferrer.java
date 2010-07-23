package com.aptana.editor.js.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.model.ContentSelector;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.ReturnTypeElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.sdoc.model.DocumentationBlock;
import com.aptana.index.core.Index;

public class JSSymbolTypeInferrer
{
	private static final EnumSet<ContentSelector> MEMBER_CONTENT = EnumSet.of(ContentSelector.NAME, ContentSelector.TYPES, ContentSelector.RETURN_TYPES);
	private static List<TypeElement> GENERATED_TYPES;
	private static final Object TYPE_LOCK = new Object();

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
	 * clearGeneratedTypes
	 */
	public static void clearGeneratedTypes()
	{
		synchronized (TYPE_LOCK)
		{
			GENERATED_TYPES = null;
		}
	}
	
	/**
	 * createPropertyElement
	 * 
	 * @param types
	 * @return
	 */
	private PropertyElement createPropertyElement(Set<String> types)
	{
		PropertyElement result;

		if (types != null && types.size() > 0)
		{
			boolean hasFunction = false;
			boolean hasNonFunction = false;

			for (String type : types)
			{
				if (type.startsWith(JSTypeConstants.FUNCTION))
				{
					hasFunction = true;
				}
				else
				{
					hasNonFunction = true;
				}
			}

			if (hasFunction && hasNonFunction == false)
			{
				result = new FunctionElement();
			}
			else
			{
				result = new PropertyElement();
			}
		}
		else
		{
			result = new PropertyElement();
		}

		return result;
	}

	/**
	 * generateType
	 * 
	 * @return
	 */
	private static TypeElement generateType(Set<String> types)
	{
		// create new type
		TypeElement result = new TypeElement();

		// give type a unique name
		result.setName(JSTypeUtil.getUniqueTypeName());

		// set parent types
		if (types != null)
		{
			for (String superType : types)
			{
				result.addParentType(superType);
			}
		}

		synchronized (TYPE_LOCK)
		{
			// save type for future reference
			if (GENERATED_TYPES == null)
			{
				GENERATED_TYPES = new ArrayList<TypeElement>();
			}
	
			GENERATED_TYPES.add(result);
		}

		return result;
	}

	/**
	 * getAdditionalProperties
	 * 
	 * @param activeObject
	 * @param types
	 * @return
	 */
	private List<String> getAdditionalProperties(JSObject activeObject, Set<String> types)
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
	public static List<TypeElement> getGeneratedTypes()
	{
		List<TypeElement> result;
		
		synchronized (TYPE_LOCK)
		{
			if (GENERATED_TYPES != null)
			{
				result = new ArrayList<TypeElement>(GENERATED_TYPES);
			}
			else
			{
				result = Collections.emptyList();
			}
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
	 * getSymbolProperty
	 * 
	 * @param activeObject
	 * @param symbol
	 * @return
	 */
	private JSObject getSymbolProperty(JSObject activeObject, String symbol)
	{
		// try to grab property from active object
		JSObject property = activeObject.getProperty(symbol);

		// if no property, then try a scope-based lookup
		if (property == null)
		{
			property = this._activeScope.getSymbol(symbol);
		}

		return property;
	}

	/**
	 * getSymbolPropertyElement
	 * 
	 * @param name
	 * @return
	 */
	public PropertyElement getSymbolPropertyElement(JSObject activeObject, String symbol)
	{
		JSObject property = this.getSymbolProperty(activeObject, symbol);
		PropertyElement result = null;

		if (property != null)
		{
			// Using linked hash set to preserve add order
			Set<String> types = new LinkedHashSet<String>();

			if (property.hasTypes())
			{
				// used cached types
				types.addAll(property.getTypes());
			}
			else
			{
				// infer types
				this.processValues(property, types);

				// process additional properties, possibly generating a new type
				this.processProperties(property, types);
			}

			// add types to property
			result = this.createPropertyElement(types);

			for (String typeName : types)
			{
				if (result instanceof FunctionElement)
				{
					if (typeName.startsWith(JSTypeConstants.FUNCTION + ":"))
					{
						typeName = typeName.substring(JSTypeConstants.FUNCTION.length() + 1);
					}
				}
				
				result.addType(typeName);
			}
		}
		else
		{
			result = new PropertyElement();
		}

		// set name
		result.setName(symbol);

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
	private Map<String, PropertyElement> getTypePropertyMap(Set<String> types)
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
	 * processProperties
	 * 
	 * @param property
	 * @param types
	 */
	private void processProperties(JSObject property, Set<String> types)
	{
		if (property.hasProperties())
		{
			List<String> additionalProperties = this.getAdditionalProperties(property, types);

			if (additionalProperties.isEmpty() == false)
			{
				// create new type
				TypeElement subType = generateType(types);

				// reset list to contain only this newly generated type
				types.clear();
				types.add(subType.getName());

				// go ahead and cache this new type to prevent possible recursion
				property.addType(subType.getName());

				// infer types of the
				for (String pname : additionalProperties)
				{
					PropertyElement pe = this.getSymbolPropertyElement(property, pname);

					subType.addProperty(pe);
				}
			}
		}

		for (String typeName : types)
		{
			property.addType(typeName);
		}
	}

	/**
	 * processValues
	 * 
	 * @param property
	 * @param types
	 */
	private void processValues(JSObject property, Set<String> types)
	{
		for (JSNode value : property.getValues())
		{
			boolean isFunction = value instanceof JSFunctionNode;
			DocumentationBlock docs = value.getDocumentation();

			if (docs != null)
			{
				if (isFunction)
				{
					FunctionElement f = new FunctionElement();

					JSTypeUtil.applyDocumentation(f, docs);

					types.add(f.getSignature());
				}
				else
				{
					PropertyElement p = new PropertyElement();

					JSTypeUtil.applyDocumentation(p, docs);

					for (ReturnTypeElement typeElement : p.getTypes())
					{
						types.add(typeElement.getType());
					}
				}
			}
			else
			{
				JSTypeInferrer inferrer = new JSTypeInferrer(this._activeScope);

				if (isFunction)
				{
					property.addType(JSTypeConstants.FUNCTION);
					inferrer.visit(value);
					property.clearTypes();
				}
				else
				{
					inferrer.visit(value);
				}

				types.addAll(inferrer.getTypes());
			}
		}
	}
}
