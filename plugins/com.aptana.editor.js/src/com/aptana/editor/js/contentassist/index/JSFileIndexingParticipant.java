package com.aptana.editor.js.contentassist.index;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.contentassist.JSContentAssistUtil;
import com.aptana.editor.js.contentassist.JSIndexQueryHelper;
import com.aptana.editor.js.contentassist.JSObject;
import com.aptana.editor.js.contentassist.JSScope;
import com.aptana.editor.js.contentassist.JSSymbolCollector;
import com.aptana.editor.js.contentassist.JSTypeInferrer;
import com.aptana.editor.js.contentassist.model.ContentSelector;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.ReturnTypeElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.xpath.ParseNodeXPath;

public class JSFileIndexingParticipant implements IFileStoreIndexingParticipant
{
	private static final EnumSet<ContentSelector> MEMBER_CONTENT = EnumSet.of(ContentSelector.NAME, ContentSelector.TYPES, ContentSelector.RETURN_TYPES);

	private JSIndexWriter _indexWriter;
	private List<TypeElement> _generatedTypes;

	/**
	 * JSFileIndexingParticipant
	 */
	public JSFileIndexingParticipant()
	{
		this._indexWriter = new JSIndexWriter();
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
	 * getGlobals
	 * 
	 * @param root
	 * @return
	 */
	protected JSScope getGlobals(IParseNode root)
	{
		JSScope result = null;

		if (root instanceof JSParseRootNode)
		{
			JSSymbolCollector s = new JSSymbolCollector();

			((JSParseRootNode) root).accept(s);

			result = s.getScope();
		}

		return result;
	}

	/**
	 * getScopeProperties
	 * 
	 * @param index
	 * @param globals
	 * @return
	 */
	private List<PropertyElement> getScopeProperties(Index index, JSScope globals)
	{
		List<PropertyElement> result = new ArrayList<PropertyElement>();

		for (String symbol : globals.getLocalSymbolNames())
		{
			PropertyElement p = this.getSymbolPropertyElement(index, globals, globals.getObject(), symbol);

			result.add(p);
		}

		return result;
	}

	/**
	 * getSymbolPropertyElement
	 * 
	 * @param index
	 * @param globals
	 * @param name
	 * @return
	 */
	private PropertyElement getSymbolPropertyElement(Index index, JSScope globals, JSObject activeObject, String symbol)
	{
		// create resulting property element
		PropertyElement result = new PropertyElement();
		result.setName(symbol);

		JSObject property = activeObject.getProperty(symbol);
		List<String> types = new ArrayList<String>();

		// infer types
		for (JSNode value : property.getValues())
		{
			JSTypeInferrer inferrer = new JSTypeInferrer(globals);

			inferrer.visit(value);

			types.addAll(inferrer.getTypes());
		}

		// if property has child properties, determine if they already exist in
		// one of the types (or their ancestors)
		if (property.hasProperties())
		{
			processSubProperties(index, globals, property, types);
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
	 * @param index
	 * @param type
	 * @return
	 */
	private Map<String, PropertyElement> getTypePropertyMap(Index index, List<String> types)
	{
		JSIndexQueryHelper helper = new JSIndexQueryHelper();

		// create a set from the specified types and their ancestors
		Set<String> ancestors = new HashSet<String>();

		for (String type : types)
		{
			ancestors.add(type);
			ancestors.addAll(helper.getTypeAncestorNames(index, type));
		}

		List<String> typesAndAncestors = new ArrayList<String>(ancestors);
		List<PropertyElement> typeMembers = helper.getTypeMembers(index, typesAndAncestors, MEMBER_CONTENT);
		Map<String, PropertyElement> propertyMap = new HashMap<String, PropertyElement>();

		for (PropertyElement propertyElement : typeMembers)
		{
			propertyMap.put(propertyElement.getName(), propertyElement);
		}

		return propertyMap;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.IFileIndexingParticipant#index(java.util.Set, com.aptana.index.core.Index,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void index(Set<IFileStore> files, Index index, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, files.size());

		for (IFileStore file : files)
		{
			if (sub.isCanceled())
			{
				return;
			}

			try
			{
				if (file == null)
				{
					continue;
				}

				sub.subTask(file.getName());

				try
				{
					// grab the source of the file we're going to parse
					String source = IOUtil.read(file.openInputStream(EFS.NONE, sub.newChild(-1)));

					// minor optimization when creating a new empty file
					if (source != null && source.length() > 0)
					{
						// create parser and associated parse state
						IParserPool pool = ParserPoolFactory.getInstance().getParserPool(IJSParserConstants.LANGUAGE);

						if (pool != null)
						{
							IParser parser = pool.checkOut();

							// apply the source to the parse state and parse
							ParseState parseState = new ParseState();
							parseState.setEditState(source, source, 0, 0);
							parser.parse(parseState);

							pool.checkIn(parser);

							// process results
							this.processParseResults(index, file, parseState.getParseResult());
						}
					}
				}
				catch (CoreException e)
				{
					Activator.logError(e.getMessage(), e);
				}
				catch (Throwable e)
				{
					Activator.logError(e.getMessage(), e);
				}
			}
			finally
			{
				sub.worked(1);
			}
		}

		monitor.done();
	}

	/**
	 * processScopes
	 * 
	 * @param index
	 * @param globals
	 * @param ast
	 * @param location
	 */
	@SuppressWarnings("unchecked")
	private List<PropertyElement> processLambdas(Index index, JSScope globals, IParseNode ast, URI location)
	{
		List<PropertyElement> result = Collections.emptyList();

		try
		{
			XPath xpath = new ParseNodeXPath("invoke[position() = 1]/group/function|invoke[position() = 1]/function");
			Object queryResult = xpath.evaluate(ast);

			if (queryResult != null)
			{
				List<JSFunctionNode> functions = (List<JSFunctionNode>) queryResult;

				for (JSFunctionNode function : functions)
				{
					JSScope scope = globals.getScopeAtOffset(function.getBody().getStartingOffset());

					JSObject object = scope.getObject();

					System.out.println(object.toSource());
					// JSTypeWalker.getScopeProperties(scope, index, location);
					//					
					// if (scope != null)
					// {
					// result = this.processWindowAssignments(index, scope);
					// }
				}
			}
		}
		catch (JaxenException e)
		{
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * processParseResults
	 * 
	 * @param index
	 * @param file
	 * @param parseState
	 */
	private void processParseResults(Index index, IFileStore file, IParseNode ast)
	{
		URI location = file.toURI();
		JSScope globals = this.getGlobals(ast);

		if (globals != null)
		{
			// create new Window type for this file
			TypeElement type = new TypeElement();
			type.setName("Window"); //$NON-NLS-1$

			// add declared variables and functions from the global scope
			for (PropertyElement property : this.getScopeProperties(index, globals))
			{
				type.addProperty(property);
			}

			// // include any assignments to Window
			// for (PropertyElement property : processWindowAssignments(index, globals))
			// {
			// type.addProperty(property);
			// }

			// process scopes (self-invoking functions)
			for (PropertyElement property : processLambdas(index, globals, ast, location))
			{
				type.addProperty(property);
			}

			// write generated types
			if (this._generatedTypes != null)
			{
				for (TypeElement generatedType : this._generatedTypes)
				{
					this._indexWriter.writeType(index, generatedType, location);
				}

				this._generatedTypes.clear();
			}

			// write new Window type to index
			this._indexWriter.writeType(index, type, location);
		}
	}

	/**
	 * processSubProperties
	 * 
	 * @param index
	 * @param globals
	 * @param activeObject
	 * @param types
	 */
	private void processSubProperties(Index index, JSScope globals, JSObject activeObject, List<String> types)
	{
		Map<String, PropertyElement> propertyMap = this.getTypePropertyMap(index, types);
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
				PropertyElement p = this.getSymbolPropertyElement(index, globals, activeObject, pname);

				subType.addProperty(p);
			}
		}
	}

	/**
	 * processWindowAssignments
	 * 
	 * @param index
	 * @param symbols
	 */
	@SuppressWarnings("unchecked")
	private List<PropertyElement> processWindowAssignments(Index index, JSScope symbols)
	{
		List<PropertyElement> properties = new ArrayList<PropertyElement>();

		// XPath xpath = new
		// ParseNodeXPath("get_property[position() = 1 and descendant-or-self::identifier[position() = 1 and string()='window']]");
		// Object result = xpath.evaluate(symbols.getAssignments());
		Object result = null;

		if (result != null)
		{
			List<IParseNode> leftHandSides = (List<IParseNode>) result;

			for (IParseNode lhs : leftHandSides)
			{
				IParseNode rhs = lhs.getNextSibling();
				JSTypeInferrer walker = new JSTypeInferrer(symbols, index);

				((JSNode) rhs).accept(walker);

				List<String> types = walker.getTypes();

				boolean isFunction = true;

				for (String type : types)
				{
					if (type.startsWith("Function:") == false)
					{
						isFunction = false;
						break;
					}
				}

				PropertyElement property = (isFunction) ? new FunctionElement() : new PropertyElement();
				property.setName(lhs.getLastChild().getText());

				for (String type : walker.getTypes())
				{
					if (isFunction)
					{
						int i = type.indexOf(':');

						if (i != -1)
						{
							type = type.substring(i + 1);
						}
					}

					ReturnTypeElement returnType = new ReturnTypeElement();

					returnType.setType(type);

					property.addType(returnType);
				}

				properties.add(property);
			}
		}

		return properties;
	}
}
