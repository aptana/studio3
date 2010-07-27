package com.aptana.editor.js.inferencing;

import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.JSIndexQueryHelper;
import com.aptana.editor.js.contentassist.index.JSIndexWriter;
import com.aptana.editor.js.contentassist.model.ContentSelector;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.ReturnTypeElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.parsing.ast.JSAssignmentNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSNodeTypes;
import com.aptana.editor.js.sdoc.model.DocumentationBlock;
import com.aptana.index.core.Index;

public class JSSymbolTypeInferrer
{
	private static final String NO_TYPE = ""; //$NON-NLS-1$
	private static final EnumSet<ContentSelector> MEMBER_CONTENT = EnumSet.of(ContentSelector.NAME, ContentSelector.TYPES, ContentSelector.RETURN_TYPES);

	private Index _index;
	private JSScope _activeScope;
	private URI _location;

	private JSIndexWriter _writer;
	
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

		return result;
	}

	/**
	 * JSSymbolTypeInferrer
	 * 
	 * @param activeScope
	 * @param index
	 * @param location
	 */
	public JSSymbolTypeInferrer(JSScope activeScope, Index index, URI location)
	{
		this._index = index;
		this._activeScope = activeScope;
		this._location = location;
	}
	
	/**
	 * applyDocumentation
	 * 
	 * @param property
	 * @param object
	 */
	private void applyDocumentation(PropertyElement property, JSPropertyCollection object)
	{
		if (property != null && object != null)
		{
			for (JSNode value : object.getValues())
			{
				DocumentationBlock docs = value.getDocumentation();

				if (docs != null)
				{
					JSTypeUtil.applyDocumentation(property, docs);
				}
			}
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
				if (type.startsWith(JSTypeConstants.FUNCTION_TYPE))
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
	 * getAdditionalProperties
	 * 
	 * @param activeObject
	 * @param types
	 * @return
	 */
	private List<String> getAdditionalProperties(JSPropertyCollection activeObject, Set<String> types)
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
	private JSPropertyCollection getSymbolProperty(JSPropertyCollection activeObject, String symbol)
	{
		// try to grab property from active object
		JSPropertyCollection property = activeObject.getProperty(symbol);

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
	public PropertyElement getSymbolPropertyElement(JSPropertyCollection activeObject, String symbol)
	{
		JSPropertyCollection property = this.getSymbolProperty(activeObject, symbol);
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
					if (typeName.startsWith(JSTypeConstants.FUNCTION_SIGNATURE_PREFIX))
					{
						typeName = typeName.substring(JSTypeConstants.FUNCTION_SIGNATURE_PREFIX.length());
					}
				}

				result.addType(typeName);
			}

			// apply any docs info we have to the property
			this.applyDocumentation(result, property);
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
	private void processProperties(JSPropertyCollection property, Set<String> types)
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

				this.writeType(subType);
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
	private void processValues(JSPropertyCollection property, Set<String> types)
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
				JSNodeTypeInferrer inferrer = new JSNodeTypeInferrer(this._activeScope, this._index, this._location);

				if (isFunction)
				{
					property.addType(JSTypeConstants.FUNCTION_TYPE);
					inferrer.visit(value);
					property.clearTypes();
				}
				else if (value instanceof JSAssignmentNode && value.getNodeType() != JSNodeTypes.ASSIGN)
				{
					property.addType(NO_TYPE);
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

	/**
	 * writeType
	 * 
	 * @param type
	 */
	private void writeType(TypeElement type)
	{
		if (type != null)
		{
			// add user agents to all generated properties
			for (PropertyElement property : type.getProperties())
			{
				JSTypeUtil.addAllUserAgents(property);
			}
			
			// make sure we have an index writer
			if (this._writer == null)
			{
				this._writer = new JSIndexWriter();
			}
	
			// write the type to the index
			this._writer.writeType(this._index, type, this._location);
		}
	}
}
