/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.inferencing;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.JSIndexQueryHelper;
import com.aptana.editor.js.contentassist.index.JSIndexWriter;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.ParameterElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.ReturnTypeElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.parsing.ast.JSAssignmentNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSIdentifierNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSObjectNode;
import com.aptana.editor.js.sdoc.model.DocumentationBlock;
import com.aptana.index.core.Index;
import com.aptana.parsing.ast.IParseNode;

public class JSSymbolTypeInferrer
{
	private static final String NO_TYPE = ""; //$NON-NLS-1$

	private Index _index;
	private JSScope _activeScope;
	private URI _location;

	private JSIndexWriter _writer;

	/**
	 * generateType
	 * 
	 * @param property
	 * @param types
	 * @return
	 */
	private static TypeElement generateType(JSPropertyCollection property, Set<String> types)
	{
		// create new type
		TypeElement result = new TypeElement();

		// set parent types
		boolean isFunction = false;

		if (types != null)
		{
			for (String superType : types)
			{
				if (JSTypeUtil.isFunctionPrefix(superType))
				{
					isFunction = true;

					result.addParentType(JSTypeConstants.FUNCTION_TYPE);
				}
				else
				{
					result.addParentType(superType);
				}
			}
		}

		String name = null;
		List<JSNode> values = property.getValues();

		if (values != null && values.size() > 0)
		{
			// NOTE: Walk backwards so latest definition that has a valid name
			// wins
			for (int i = values.size() - 1; i >= 0; i--)
			{
				JSNode value = values.get(i);
				String candidate = JSTypeUtil.getName(value);

				if (candidate != null && candidate.length() > 0)
				{
					name = candidate;
					break;
				}
			}
		}

		// generate a unique name if we didn't find one
		if (name == null || name.length() == 0)
		{
			name = JSTypeUtil.getUniqueTypeName();
		}

		// wrap the name
		if (isFunction)
		{
			name = JSTypeConstants.FUNCTION_TYPE + JSTypeConstants.GENERIC_OPEN + name + JSTypeConstants.GENERIC_CLOSE;
		}

		// give type a unique name
		result.setName(name);

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
			Queue<JSNode> queue = new LinkedList<JSNode>();
			Set<IParseNode> visitedSymbols = new HashSet<IParseNode>();

			// prime the queue
			queue.addAll(object.getValues());

			while (queue.isEmpty() == false)
			{
				JSNode node = queue.poll();

				if (visitedSymbols.contains(node) == false)
				{
					visitedSymbols.add(node);

					DocumentationBlock docs = node.getDocumentation();

					if (docs != null)
					{
						JSTypeUtil.applyDocumentation(property, docs);
						break;
					}
					else if (property instanceof FunctionElement && node instanceof JSFunctionNode)
					{
						FunctionElement functionElement = (FunctionElement) property;
						JSFunctionNode functionNode = (JSFunctionNode) node;

						for (IParseNode parameterNode : functionNode.getParameters())
						{
							ParameterElement parameterElement = new ParameterElement();

							parameterElement.setName(parameterNode.getText());
							parameterElement.addType(JSTypeConstants.OBJECT_TYPE);

							functionElement.addParameter(parameterElement);
						}
					}
					else if (node instanceof JSIdentifierNode)
					{
						// grab name
						String symbol = node.getText();

						JSPropertyCollection p = this.getSymbolProperty(this._activeScope.getObject(), symbol);

						if (p != null)
						{
							for (JSNode value : p.getValues())
							{
								queue.offer(value);
							}
						}
					}
					else if (node instanceof JSAssignmentNode)
					{
						IParseNode rhs = node.getLastChild();

						if (rhs instanceof JSNode)
						{
							queue.offer((JSNode) rhs);
						}
					}
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
		boolean isFunction = false;
		PropertyElement result;

		// determine if any of the types are functions
		if (types != null && types.size() > 0)
		{
			for (String type : types)
			{
				if (JSTypeUtil.isFunctionPrefix(type))
				{
					isFunction = true;
					break;
				}
			}
		}

		if (isFunction)
		{
			result = new FunctionElement();
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
			if (propertyMap.containsKey(name) == false || JSTypeConstants.PROTOTYPE_PROPERTY.equals(name))
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
				JSTypeUtil.applySignature(result, typeName);
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
		List<PropertyElement> typeMembers = helper.getTypeMembers(this._index, typesAndAncestors);
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
	public void processProperties(JSPropertyCollection property, Set<String> types)
	{
		if (property.hasProperties())
		{
			List<String> additionalProperties = this.getAdditionalProperties(property, types);

			if (additionalProperties.isEmpty() == false)
			{
				// create new type
				TypeElement subType = generateType(property, types);

				// Preserve function return types and add to property type
				List<String> returnTypes = new ArrayList<String>();

				for (String type : types)
				{
					if (JSTypeUtil.isFunctionPrefix(type))
					{
						returnTypes.addAll(JSTypeUtil.getFunctionSignatureReturnTypeNames(type));
					}
				}

				String propertyType = subType.getName();

				if (returnTypes.isEmpty() == false)
				{
					propertyType += JSTypeConstants.FUNCTION_SIGNATURE_DELIMITER + StringUtil.join(JSTypeConstants.RETURN_TYPE_DELIMITER, returnTypes);
				}

				// reset list to contain only this newly generated type
				types.clear();
				types.add(propertyType);

				// go ahead and cache this new type to prevent possible recursion
				property.addType(propertyType);

				// infer types of the additional properties
				for (String pname : additionalProperties)
				{
					PropertyElement pe = this.getSymbolPropertyElement(property, pname);

					subType.addProperty(pe);
				}

				// push type to the current index
				this.writeType(subType);
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

				if (value instanceof JSObjectNode)
				{
					inferrer.addType(JSTypeConstants.OBJECT_TYPE);
				}
				else if (isFunction)
				{
					property.addType(JSTypeConstants.FUNCTION_TYPE);
					inferrer.visit(value);
					property.clearTypes();
				}
				else
				{
					property.addType(NO_TYPE);
					inferrer.visit(value);
					property.clearTypes();
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
