/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.inferencing;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.IMap;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.Index;
import com.aptana.js.core.JSTypeConstants;
import com.aptana.js.core.index.JSIndexQueryHelper;
import com.aptana.js.core.inferencing.JSNodeTypeInferrer;
import com.aptana.js.core.inferencing.JSPropertyCollection;
import com.aptana.js.core.inferencing.JSScope;
import com.aptana.js.core.inferencing.JSTypeUtil;
import com.aptana.js.core.model.FunctionElement;
import com.aptana.js.core.model.ParameterElement;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.TypeElement;
import com.aptana.js.core.parsing.ast.JSAssignmentNode;
import com.aptana.js.core.parsing.ast.JSFunctionNode;
import com.aptana.js.core.parsing.ast.JSIdentifierNode;
import com.aptana.js.core.parsing.ast.JSNode;
import com.aptana.js.core.parsing.ast.JSObjectNode;
import com.aptana.js.internal.core.index.JSIndexWriter;
import com.aptana.js.internal.core.parsing.sdoc.model.DocumentationBlock;
import com.aptana.js.internal.core.parsing.sdoc.model.TagType;
import com.aptana.parsing.ast.IParseNode;

public class JSSymbolTypeInferrer
{
	private static final String NO_TYPE = StringUtil.EMPTY;

	private final Index index;
	private final JSScope activeScope;
	private final URI location;
	private final JSIndexWriter writer;
	private final JSIndexQueryHelper queryHelper;

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

		// Set the name first so we can validate we don't end up setting self as a parent type
		String name = null;
		List<JSNode> values = property.getValues();
		if (!CollectionsUtil.isEmpty(values))
		{
			// NOTE: Walk backwards so latest definition that has a valid name
			// wins
			for (int i = values.size() - 1; i >= 0; i--)
			{
				JSNode value = values.get(i);
				String candidate = JSTypeUtil.getName(value);

				if (!StringUtil.isEmpty(candidate))
				{
					name = candidate;
					break;
				}
			}
		}

		// if not generated from values, then use property chain as type name
		if (StringUtil.isEmpty(name))
		{
			name = property.getQualifiedName();
		}

		// if still no name, then generate a unique name
		if (StringUtil.isEmpty(name))
		{
			name = JSTypeUtil.getUniqueTypeName();
		}

		result.setName(name);

		// set parent types
		// FIXME We can't possibly have multiple super types. What the hell is going on here?
		if (!CollectionsUtil.isEmpty(types))
		{
			for (String superType : types)
			{
				result.addParentType(superType);
			}
		}

		return result;
	}

	/**
	 * @param activeScope
	 * @param index
	 * @param location
	 *            The current file/location
	 * @param queryHelper
	 *            The query helper that knows the build path order. We can lookup types/etc from it.
	 */
	public JSSymbolTypeInferrer(JSScope activeScope, Index index, URI location, JSIndexQueryHelper queryHelper)
	{
		this.index = index;
		this.activeScope = activeScope;
		this.location = location;
		this.queryHelper = queryHelper;
		this.writer = new JSIndexWriter();
	}

	/**
	 * applyDocumentation
	 * 
	 * @param property
	 * @param object
	 * @param monitor
	 */
	private void applyDocumentation(PropertyElement property, JSPropertyCollection object, IProgressMonitor monitor)
	{
		if (property == null || object == null)
		{
			return;
		}

		Queue<JSNode> queue = new LinkedList<JSNode>();
		Set<IParseNode> visitedSymbols = new HashSet<IParseNode>();

		// prime the queue
		queue.addAll(object.getValues());
		SubMonitor sub = SubMonitor.convert(monitor, queue.size());

		while (!queue.isEmpty())
		{
			sub.setWorkRemaining(queue.size());
			JSNode node = queue.poll();

			if (!visitedSymbols.contains(node))
			{
				visitedSymbols.add(node);
				// If we have docs for a value, apply it to the property we created.
				DocumentationBlock docs = node.getDocumentation();
				if (docs != null)
				{
					JSTypeUtil.applyDocumentation(property, node, docs);
					break;
				}
				// Otherwise I guess we try to track the values back to find docs?

				// If a value of the property collection is a function and the property is a function
				// Then add the parameters of the value as parameters of the property. WHY?!?!?!
				if (property instanceof FunctionElement && node instanceof JSFunctionNode)
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
				// Track an identifier back to it's definition...
				else if (node instanceof JSIdentifierNode)
				{
					// grab name
					String symbol = node.getText();

					JSPropertyCollection p = this.getSymbolProperty(activeScope.getObject(), symbol);

					if (p != null)
					{
						for (JSNode value : p.getValues())
						{
							queue.offer(value);
						}
					}
				}
				// Track an assignment to it's assigned value...
				else if (node instanceof JSAssignmentNode)
				{
					IParseNode rhs = node.getLastChild();

					if (rhs instanceof JSNode)
					{
						queue.offer((JSNode) rhs);
					}
				}
			}
			sub.worked(1);
		}
	}

	/**
	 * This is a utility method used to create PropertyElements or FunctionElements based on a list of types. If the
	 * list of types contains a function, then a FunctionElement will be created; otherwise, a PropertyElement will be
	 * created
	 * 
	 * @param types
	 *            A set of type names. This value may be empty or null
	 * @return Returns a PropertyElement or a FunctionElement
	 */
	private PropertyElement createPropertyElement(Set<String> types)
	{
		// determine if any of the types are functions
		if (!CollectionsUtil.isEmpty(types))
		{
			for (String type : types)
			{
				if (JSTypeUtil.isFunctionPrefix(type))
				{
					return new FunctionElement();
				}
			}
		}

		return new PropertyElement();
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
			if (!propertyMap.containsKey(name) || JSTypeConstants.PROTOTYPE_PROPERTY.equals(name))
			{
				additionalProperties.add(name);
			}
		}

		return additionalProperties;
	}

	/**
	 * Generate a list of PropertyElements (and its descendent classes), one for each symbol defined in the currently
	 * active scope
	 * 
	 * @return Returns a list of PropertyElements. This value will always be defined even if the list is empty
	 */
	public List<PropertyElement> getScopeProperties(IProgressMonitor monitor)
	{
		List<String> symbolNames = activeScope.getLocalSymbolNames();
		final SubMonitor sub = SubMonitor.convert(monitor, symbolNames.size());
		return CollectionsUtil.map(symbolNames, new IMap<String, PropertyElement>()
		{
			public PropertyElement map(String symbol)
			{
				return getSymbolPropertyElement(symbol, sub.newChild(1));
			}
		});
	}

	/**
	 * Get the property collection for the specified symbol. If the symbol does not exist in the specified collections,
	 * then the active scope is used.
	 * 
	 * @param activeObject
	 *            The collection to use for default lookup of the specified symbol
	 * @param symbol
	 *            The name of the symbol to locate
	 * @return Return a property collection, possibly null
	 */
	private JSPropertyCollection getSymbolProperty(JSPropertyCollection activeObject, String symbol)
	{
		// try to grab property from active object
		JSPropertyCollection property = activeObject.getProperty(symbol);

		// if no property, then try a scope-based lookup
		if (property == null)
		{
			property = activeScope.getSymbol(symbol);
		}

		return property;
	}

	/**
	 * Return a PropertyElement for the specified symbol in the specified property collection. This method uses cached
	 * values as it can, creating new elements if none exists in the cache
	 * 
	 * @param symbol
	 *            The property collection to use when processing the specified symbol
	 * @param monitor
	 *            The name of the symbol to process
	 * @return Returns a new PropertyElement (or FunctionElement). This value will not be null
	 * @throws OperationCanceledException
	 *             When user cancels the long-running operation
	 */
	public PropertyElement getSymbolPropertyElement(JSPropertyCollection activeObject, String symbol,
			IProgressMonitor monitor) throws OperationCanceledException
	{
		JSPropertyCollection property = this.getSymbolProperty(activeObject, symbol);
		PropertyElement result = null;
		SubMonitor sub = SubMonitor.convert(monitor, 50);
		if (property != null)
		{
			// Try to use a cached copy of the PropertyElement we created for this collection!
			if (property.hasElement())
			{
				return property.getElement();
			}

			// Using linked hash set to preserve add order
			Set<String> types = new LinkedHashSet<String>();

			if (property.hasTypes())
			{
				// used cached types
				types.addAll(property.getTypes());
			}
			else
			{
				if (sub.isCanceled())
				{
					throw new OperationCanceledException();
				}
				// infer value types
				this.processValues(property, types, sub.newChild(10));

				if (sub.isCanceled())
				{
					throw new OperationCanceledException();
				}
				// process additional properties, possibly generating a new type
				this.processProperties(property, types, sub.newChild(10));
			}

			sub.setWorkRemaining(30);

			// add types to property
			result = this.createPropertyElement(types);

			if (!CollectionsUtil.isEmpty(types))
			{
				int part = 20 / types.size();
				for (String typeName : types)
				{
					if (sub.isCanceled())
					{
						throw new OperationCanceledException();
					}
					JSTypeUtil.applySignature(result, typeName);
					sub.worked(part);
				}
			}
			sub.setWorkRemaining(10);

			if (sub.isCanceled())
			{
				throw new OperationCanceledException();
			}

			// apply any docs info we have to the property
			this.applyDocumentation(result, property, sub.newChild(10));

			// Cache the property we generated for this collection!
			property.setElement(result);
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
	 * Return a PropertyElement for the specified symbol in the currently active scope.
	 * 
	 * @param symbol
	 *            The name of the symbol to process in the current scope
	 * @return Returns a new PropertyElement (or FunctionElement). This value will not be null
	 */
	public PropertyElement getSymbolPropertyElement(String symbol, IProgressMonitor monitor)
	{
		return this.getSymbolPropertyElement(activeScope.getObject(), symbol, monitor);
	}

	/**
	 * Generate a mapping of property names to their property elements. The properties are a collection generated from
	 * the specified list of types and of those type's ancestor types
	 * 
	 * @param types
	 *            A set of type names
	 * @return Returns a map of property name to property element
	 */
	private Map<String, PropertyElement> getTypePropertyMap(Set<String> types)
	{
		// create a unique set of type names and their ancestor types
		Set<String> ancestors = new HashSet<String>();

		for (String type : types)
		{
			ancestors.add(type);
			ancestors.addAll(queryHelper.getTypeAncestorNames(type));
		}

		// grab property elements for all collected types
		List<String> typesAndAncestors = new ArrayList<String>(ancestors);
		Collection<PropertyElement> typeMembers = queryHelper.getTypeMembers(typesAndAncestors);

		// generate map of property name to its property element
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
	public void processProperties(JSPropertyCollection property, Set<String> types, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		if (property.hasProperties())
		{
			List<String> additionalProperties = this.getAdditionalProperties(property, types);

			if (!additionalProperties.isEmpty())
			{
				// create new type
				TypeElement subType = generateType(property, types);
				sub.worked(5);

				// Preserve function return types and add to property type
				List<String> returnTypes = new ArrayList<String>();

				if (!CollectionsUtil.isEmpty(types))
				{
					int unit = 20 / types.size();
					for (String type : types)
					{
						if (JSTypeUtil.isFunctionPrefix(type))
						{
							returnTypes.addAll(JSTypeUtil.getFunctionSignatureReturnTypeNames(type));
						}
						sub.worked(unit);
					}
				}
				sub.setWorkRemaining(75);

				String propertyType = subType.getName();

				// FIXME What if propertyType is already Function<Something> here?!
				if (!JSTypeUtil.isFunctionPrefix(propertyType) && !returnTypes.isEmpty())
				{
					propertyType += JSTypeUtil.toFunctionType(returnTypes);
				}

				// reset list to contain only this newly generated type
				types.clear();
				types.add(propertyType);

				// go ahead and cache this new type to prevent possible recursion
				property.addType(propertyType);

				// infer types of the additional properties
				if (!CollectionsUtil.isEmpty(additionalProperties))
				{
					// FIXME This gives busted progress here
					// If user hung additional properties off of "prototype" elevate those up to the type.
					if (additionalProperties.contains(JSTypeConstants.PROTOTYPE_PROPERTY))
					{
						JSPropertyCollection collection = property.getProperty(JSTypeConstants.PROTOTYPE_PROPERTY);
						if (collection.hasProperties())
						{
							for (String pname : collection.getPropertyNames())
							{
								PropertyElement pe = this.getSymbolPropertyElement(collection, pname,
										new NullProgressMonitor());
								pe.setIsInstanceProperty(true);
								pe.setIsClassProperty(false);
								subType.addProperty(pe);
							}
							additionalProperties.remove(JSTypeConstants.PROTOTYPE_PROPERTY);
						}
					}
					// Now do all the non-prototype properties!
					if (!CollectionsUtil.isEmpty(additionalProperties))
					{
						int work = 70 / additionalProperties.size();
						for (String pname : additionalProperties)
						{
							PropertyElement pe = this.getSymbolPropertyElement(property, pname, sub.newChild(work));
							pe.setIsClassProperty(true);
							subType.addProperty(pe);
						}
					}
				}

				// We're generating a new type. Let's give it a prototype property if we don't have one yet.
				if (subType.getProperty(JSTypeConstants.PROTOTYPE_PROPERTY) == null)
				{
					PropertyElement pe = new PropertyElement();
					pe.setName(JSTypeConstants.PROTOTYPE_PROPERTY);
					pe.addType(JSTypeConstants.OBJECT_TYPE);
					pe.setIsClassProperty(true);
					pe.setIsInstanceProperty(false);
					subType.addProperty(pe);
				}

				sub.setWorkRemaining(5);

				// push type to the current index
				this.writeType(subType);
				sub.worked(5);
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
	 * Collect all types for a given property collection. Types are determined by documentation. However, if no
	 * documentation is associated with a given property value, then inferencing is used to determine the value type
	 * 
	 * @param property
	 *            The property to process
	 * @param types
	 *            The collection of types
	 * @throws OperationCanceledException
	 */
	private void processValues(JSPropertyCollection property, Set<String> types, IProgressMonitor monitor)
			throws OperationCanceledException
	{
		List<JSNode> values = property.getValues();
		SubMonitor sub = SubMonitor.convert(monitor, values.size());

		for (JSNode value : values)
		{
			if (sub.isCanceled())
			{
				throw new OperationCanceledException();
			}

			boolean isFunction = value instanceof JSFunctionNode;
			DocumentationBlock docs = value.getDocumentation();

			TagType tagToCheck = isFunction ? TagType.RETURN : TagType.TYPE;
			if (docs == null || !docs.hasTag(tagToCheck))
			{
				if (value instanceof JSObjectNode)
				{
					types.add(JSTypeConstants.OBJECT_TYPE);
				}
				else
				{
					JSNodeTypeInferrer inferrer = getNodeInferrer(sub);
					property.addType(isFunction ? JSTypeConstants.FUNCTION_TYPE : NO_TYPE);
					inferrer.visit(value);
					property.clearTypes();
					types.addAll(inferrer.getTypes());
				}
			}
			else if (isFunction)
			{
				FunctionElement f = new FunctionElement();
				JSTypeUtil.applyDocumentation(f, value, docs);
				types.addAll(f.getSignatureTypes());
			}
			else
			{
				PropertyElement p = new PropertyElement();
				JSTypeUtil.applyDocumentation(p, value, docs);
				types.addAll(p.getTypeNames());
			}
			sub.worked(1);
		}
	}

	private JSNodeTypeInferrer getNodeInferrer(SubMonitor sub)
	{
		// FIXME Keep one around and re-use it? Can we just pass in a new monitor?
		return new JSNodeTypeInferrer(activeScope, index, location, queryHelper, sub);
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
				property.setHasAllUserAgents();
			}

			// write the type to the index
			writer.writeType(index, type, location);
		}
	}
}
