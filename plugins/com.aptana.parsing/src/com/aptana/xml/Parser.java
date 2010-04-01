/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Kevin Lindsey
 */
public class Parser extends DefaultHandler
{
	private static final Map<Class<?>,Class<?>> PRIMITIVE_TO_CLASS = new HashMap<Class<?>,Class<?>>();
	private static final Pattern PARSE_ERROR_LINE_NUMBER = Pattern.compile(" line (\\d+)"); //$NON-NLS-1$

	/**
	 * static constructor
	 */
	static
	{
		PRIMITIVE_TO_CLASS.put(boolean.class, Boolean.class);
		PRIMITIVE_TO_CLASS.put(char.class, Character.class);
		PRIMITIVE_TO_CLASS.put(int.class, Integer.class);
		PRIMITIVE_TO_CLASS.put(long.class, Long.class);
		PRIMITIVE_TO_CLASS.put(float.class, Float.class);
		PRIMITIVE_TO_CLASS.put(double.class, Double.class);
	}
	
	private String _namespace;
	private List<String> _packages;
	private List<String> _suffixes;
	private Map<Class<?>,IConverter> _convertersByClass;

	private Map<String,Class<?>> _classByName;
	private INode _currentNode;
	private Stack<INode> _nodes;
	private DocumentNode _documentNode;
	private StringBuffer _textBuffer;
	private Locator _locator;
	
	private Class<?> _unknownElementClass;
	private BundleClassLoader _classLoader;

	private IErrorHandler _errorHandler;
	private boolean _cacheClasses;

	/**
	 * Parser
	 */
	public Parser()
	{
		this(null);
	}

	/**
	 * Parser
	 * 
	 * @param namespace
	 */
	public Parser(String namespace)
	{
		this._namespace = namespace;

		this._packages = new ArrayList<String>();
		this._suffixes = new ArrayList<String>();
		this._convertersByClass = new HashMap<Class<?>,IConverter>();
		this._classByName = new HashMap<String,Class<?>>();

		this._nodes = new Stack<INode>();
		this._textBuffer = new StringBuffer();

		this.addConverters();
		this.addPackages();
		this.addSuffixes();

		this._classLoader = new BundleClassLoader();
		//this.addBundle(Platform.getBundle("com.aptana.parsing.lexer")); //$NON-NLS-1$
		
		this._unknownElementClass = NodeBase.class;
		
		this._cacheClasses = true;
	}

	/**
	 * addBundle
	 * 
	 * @param bundle
	 */
	public void addBundle(Bundle bundle)
	{
		if (bundle != null)
		{
			this._classLoader.addBundle(bundle);
		}
	}

	/**
	 * addClass
	 * 
	 * @param name
	 * @param type
	 */
	public void addClass(String name, Class<?> type)
	{
		this._classByName.put(name, type);
	}

	/**
	 * addConverter
	 * 
	 * @param targetType
	 * @param converter
	 */
	public void addConverter(Class<?> targetType, IConverter converter)
	{
		this._convertersByClass.put(targetType, converter);
	}

	/**
	 * addConverters
	 */
	protected void addConverters()
	{
		this.addConverter(Boolean.class, new BooleanConverter());
		this.addConverter(Character.class, new CharacterConverter());
		this.addConverter(Double.class, new DoubleConverter());
		this.addConverter(Float.class, new FloatConverter());
		this.addConverter(Integer.class, new IntegerConverter());

		this.addConverter(boolean.class, new BooleanConverter());
		this.addConverter(char.class, new CharacterConverter());
		this.addConverter(double.class, new DoubleConverter());
		this.addConverter(float.class, new FloatConverter());
		this.addConverter(int.class, new IntegerConverter());
	}

	/**
	 * addPackage
	 * 
	 * @param packageName
	 */
	public void addPackage(String packageName)
	{
		if (this._packages.contains(packageName) == false)
		{
			this._packages.add(packageName);
		}
	}

	/**
	 * addPackages
	 */
	protected void addPackages()
	{
		java.lang.Package thisPackage = this.getClass().getPackage();
		String packageName = thisPackage.getName();

		this.addPackage(packageName);
	}

	/**
	 * addSuffix
	 * 
	 * @param suffix
	 */
	public void addSuffix(String suffix)
	{
		if (this._suffixes.contains(suffix) == false)
		{
			this._suffixes.add(suffix);
		}
	}

	/**
	 * addSuffixes
	 */
	protected void addSuffixes()
	{
		// this causes the mapped class name (from toClassName) to be tested first
		this.addSuffix(""); //$NON-NLS-1$
	}

	/**
	 * appendText
	 */
	private void appendText()
	{
		if (this._currentNode != null)
		{
			this._currentNode.appendText(this._textBuffer.toString());
		}

		this._textBuffer.setLength(0);
	}

	/**
	 * applyAttributes
	 * 
	 * @param node
	 * @param attributes
	 */
	private void applyAttributes(INode node, Attributes attributes)
	{
		for (int i = 0; i < attributes.getLength(); i++)
		{
			String name = attributes.getLocalName(i);
			String value = attributes.getValue(i);
			Method setter = this.findSetter(node, name);

			if (setter != null)
			{
				Class<?> parameterType = setter.getParameterTypes()[0];

				// map primitives to their "boxed" class types
				if (parameterType.isPrimitive() && PRIMITIVE_TO_CLASS.containsKey(parameterType))
				{
					parameterType = PRIMITIVE_TO_CLASS.get(parameterType);
				}

				if (parameterType == String.class)
				{
					try
					{
						setter.invoke(node, new Object[] { value });
					}
					catch (Exception e)
					{
						String message = MessageFormat.format(
							Messages.Parser_Error_Invoking_Setter,
							new Object[] {
								setter.getName(),
								name
							}
						);
						this.sendError(message);
					}
				}
				else
				{
					// get converter
					IConverter converter = this.findConverter(parameterType);

					if (converter != null)
					{
						// create argument array
						Object ary = Array.newInstance(parameterType, 1);

						// set value in argument array
						Array.set(ary, 0, converter.fromString(value));

						try
						{
							Object[] argsArray = (Object[]) ary;

							setter.invoke(node, argsArray);
						}
						catch (Exception e)
						{
							String message = MessageFormat.format(
								Messages.Parser_Error_Invoking_Setter,
								new Object[] {
									setter.getName(),
									name
								}
							);
							this.sendError(message);
						}
					}
					else
					{
						String message = MessageFormat.format(
							Messages.Parser_No_Converter,
							new Object[] {
								parameterType.getName()
							}
						);
						this.sendError(message);
					}
				}
			}
			else
			{
				// TODO: add setProperty(name,value) to INode and call that method here
				
				String message = MessageFormat.format(
					Messages.Parser_No_Setter,
					new Object[] {
						name
					}
				);
				this.sendWarning(message);
			}
		}
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		this._textBuffer.append(ch, start, length);
	}

	/**
	 * createDocumentNode
	 * 
	 * @return DocumentNode
	 */
	protected DocumentNode createDocumentNode()
	{
		return new DocumentNode();
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#endDocument()
	 */
	public void endDocument() throws SAXException
	{
		this._currentNode = null;
		this._nodes.clear();
		this._textBuffer.setLength(0);
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		this.appendText();

		if (this._namespace == null || this._namespace.equals(uri))
		{
			if (localName.indexOf('.') == -1)
			{
				this.exitObject();
			}
			else
			{
				this.setProperty(localName);
			}
		}
	}

	/**
	 * enterMatcher
	 * 
	 * @param name
	 * @param attributes
	 */
	private void enterObject(String name, Attributes attributes) throws Exception
	{
		// get class for this element
		String className = this.toClassName(name);

		// NOTE: findClass never returns null
		Class<?> nodeClass = this.findClass(className);

		this.appendText();

		try
		{
			Constructor<?> ctor = nodeClass.getConstructor(new Class[0]);
			INode node = (INode) ctor.newInstance(new Object[0]);

			node.setLineNumber(this._locator.getLineNumber());
			node.setColumnNumber(this._locator.getColumnNumber());

			// set properties
			this.applyAttributes(node, attributes);

			if (this._currentNode != null)
			{
				// add node to stack
				this._nodes.push(this._currentNode);
			}

			// update current node;
			this._currentNode = node;
		}
		catch (Exception e)
		{
			String message = MessageFormat.format(
				Messages.Parser_Could_Not_Create_Class,
				new Object[] {
					nodeClass.getName(), name
				}
			);

			this.sendError(message);
		}
	}

	/**
	 * exitMatcher
	 */
	private void exitObject()
	{
		if (this._nodes.size() > 0)
		{
			// special processing for Package Element
			if (this._currentNode instanceof Package)
			{
				String packageName = this._currentNode.getText();

				this.addPackage(packageName);
			}
			else if (this._currentNode instanceof com.aptana.xml.Bundle)
			{
				String bundleName = this._currentNode.getText();
				Bundle bundle = Platform.getBundle(bundleName);

				if (bundle != null)
				{
					this.addBundle(bundle);
				}
				else
				{
					this.sendWarning(Messages.Parser_Bundle_Not_Found + bundleName);
				}
			}

			// remove parent from stack
			INode parent = this._nodes.pop();

			try
			{
				if (this._currentNode != null)
				{
					// add current node to parent
					parent.appendChild(this._currentNode);
				}
			}
			catch (IllegalArgumentException e)
			{
				this.sendError(e.getMessage());
			}

			// set parent as current node
			this._currentNode = parent;
		}
		else
		{
			// NOTE: this should never happen
			this._currentNode = null;
		}
	}

	/**
	 * findClass
	 * 
	 * @param className
	 * @return Class
	 */
	private Class<?> findClass(String className)
	{
		Class<?> result;
		
		if (this._classByName.containsKey(className) == false)
		{
			Class<?> candidate = null;

			// loop through the packages to see if we can find our class
			for (int i = 0; i < this._packages.size(); i++)
			{
				String pkg = this._packages.get(i);
				String fullClassName = pkg + "." + className; //$NON-NLS-1$

				for (int j = 0; j < this._suffixes.size(); j++)
				{
					String suffix = this._suffixes.get(j);
					String extendedName = fullClassName + suffix;

					try
					{
						candidate = this._classLoader.loadClass(extendedName);
					}
					catch (ClassNotFoundException e)
					{
						// ignore class not founds
					}

					if (candidate != null)
					{
						if (INode.class.isAssignableFrom(candidate))
						{
							break;
						}
						else
						{
							candidate = null;
						}
						
						// TODO: may want to add info stating that a class was skipped
						// since it wasn't type compatible with INode
					}
				}

				// exit if we found a class
				if (candidate != null)
				{
					break;
				}
			}

			// fallback to generic node if we didn't find anything
			if (candidate == null)
			{
				candidate = this._unknownElementClass;

				String message = MessageFormat.format(
					Messages.Parser_Class_Not_Found_Using_Replacement,
					new Object[] {
						className,
						this._unknownElementClass.getName()
					}
				);
				
				this.sendError(message);
			}

			// cache results to avoid future lookups
			if (this._cacheClasses)
			{
				this.addClass(className, candidate);
			}
			
			result = candidate;
		}
		else
		{
			result = this._classByName.get(className);
		}

		return result;
	}

	/**
	 * findConverter
	 * 
	 * @param parameterType
	 * @return IConverter
	 */
	private IConverter findConverter(Class<?> parameterType)
	{
		if (this._convertersByClass.containsKey(parameterType) == false)
		{
			Class<?> candidate = null;
			String converterName = parameterType.getName() + "Converter"; //$NON-NLS-1$
			IConverter instance = null;

			// loop through the packages to see if we can find our class
			for (int i = 0; i < this._packages.size(); i++)
			{
				String pkg = this._packages.get(i);
				String fullConverterName = pkg + "." + converterName; //$NON-NLS-1$

				try
				{
					candidate = this._classLoader.loadClass(fullConverterName);
				}
				catch (ClassNotFoundException e)
				{
					// ignore class not founds
				}

				if (candidate != null)
				{
					break;
				}
			}

			// create an instance, if we found a class
			if (candidate != null)
			{
				try
				{
					Constructor<?> ctor = candidate.getConstructor(new Class[0]);
					Object converter = ctor.newInstance(new Object[0]);

					// use this instance if it's an IConverter
					if (converter instanceof IConverter)
					{
						instance = (IConverter) converter;
					}
				}
				catch (Exception e)
				{
					// e.printStackTrace();
				}
			}

			this.addConverter(parameterType, instance);
		}

		return this._convertersByClass.get(parameterType);
	}
	
	/**
	 * findSetter
	 * 
	 * @param object
	 * @param name
	 * @return Method or null
	 */
	private Method findSetter(Object object, String name)
	{
		Method result = null;
		String methodName = this.toMethodName(name);
		Method[] methods = object.getClass().getMethods();
		List<Method> candidates = new ArrayList<Method>();

		for (int i = 0; i < methods.length; i++)
		{
			Method method = methods[i];
			Class<?>[] parameterTypes = method.getParameterTypes();

			if (method.getName().equals(methodName) && parameterTypes.length == 1)
			{
				if (parameterTypes[0] == String.class)
				{
					// prefer a String arg over all other types
					candidates.add(0, method);
				}
				else
				{
					candidates.add(method);
				}
			}
		}

		if (candidates.size() > 0)
		{
			result = candidates.get(0);
		}

		// TODO: cache results

		return result;
	}

	/**
	 * cacheClasses
	 *
	 * @return boolean
	 */
	public boolean cacheClasses()
	{
		return this._cacheClasses;
	}
	
	/**
	 * getClassLoader
	 * 
	 * @return ClassLoader
	 */
	public ClassLoader getClassLoader()
	{
		return this._classLoader;
	}

	/**
	 * getUnknownElementClass
	 *
	 * @return Class
	 */
	public Class<?> getUnknownElementClass()
	{
		return this._unknownElementClass;
	}

	/**
	 * Load the specified binary grammar file
	 * 
	 * @param file
	 * @return Object or null
	 */
	public DocumentNode loadXML(File file)
	{
		DocumentNode result = null;

		try
		{
			FileInputStream inputStream = new FileInputStream(file);

			result = this.loadXML(inputStream);
		}
		catch (FileNotFoundException e)
		{
			// e.printStackTrace();
		}

		return result;
	}

	/**
	 * load
	 * 
	 * @param in
	 * @return DocumentNode
	 */
	public DocumentNode loadXML(InputStream in)
	{
		try
		{
			// create a new SAX factory class
			SAXParserFactory factory = SAXParserFactory.newInstance();

			// make sure it generates namespace aware parsers
			factory.setNamespaceAware(true);

			// create the parser
			SAXParser saxParser = factory.newSAXParser();

			// parse the XML
			saxParser.parse(in, this);
		}
		catch (Exception e)
		{
			this.sendError(e.getMessage());
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
				}
			}
		}

		return this._documentNode;
	}

	/**
	 * removeBundle
	 * 
	 * @param bundle
	 */
	public void removeBundle(Bundle bundle)
	{
		if (bundle != null)
		{
			this._classLoader.removeBundle(bundle);
		}
	}

	/**
	 * removeClass
	 * 
	 * @param name
	 */
	public void removeClass(String name)
	{
		this._classByName.remove(name);
	}
	
	/**
	 * removeConverter
	 * 
	 * @param targetType
	 */
	public void removeConverter(Class<?> targetType)
	{
		this._convertersByClass.remove(targetType);
	}

	/**
	 * removePackage
	 * 
	 * @param packageName
	 */
	public void removePackage(String packageName)
	{
		this._packages.remove(packageName);
	}

	/**
	 * removeSuffix
	 * 
	 * @param suffix
	 */
	public void removeSuffix(String suffix)
	{
		this._suffixes.remove(suffix);
	}

	/**
	 * sendError
	 *
	 * @param message
	 */
	private void sendError(String message)
	{
		if (this._errorHandler != null)
		{
			int line = this._locator.getLineNumber();
			int column = this._locator.getColumnNumber();
			
			if (line == -1)
			{
				Matcher m = PARSE_ERROR_LINE_NUMBER.matcher(message);
				
				if (m.find())
				{
					line = Integer.parseInt(m.group(1));
				}
				else
				{
					line = 1;
				}
			}
			
			if (column == -1)
			{
				column = 0;
			}
			
			this._errorHandler.handleError(line, column, message);
		}
	}
	
//	/**
//	 * sendInfo
//	 *
//	 * @param message
//	 */
//	private void sendInfo(String message)
//	{
//		if (this._errorHandler != null)
//		{
//			int line = this._locator.getLineNumber();
//			int column = this._locator.getColumnNumber();
//			
//			this._errorHandler.handleInfo(line, column, message);
//		}
//	}
	
	/**
	 * sendWarning
	 *
	 * @param message
	 */
	private void sendWarning(String message)
	{
		if (this._errorHandler != null)
		{
			int line = this._locator.getLineNumber();
			int column = this._locator.getColumnNumber();
			
			if (line == -1)
			{
				Matcher m = PARSE_ERROR_LINE_NUMBER.matcher(message);
				
				if (m.find())
				{
					line = Integer.parseInt(m.group(1));
				}
				else
				{
					line = 1;
				}
			}
			
			if (column == -1)
			{
				column = 0;
			}
			
			this._errorHandler.handleWarning(line, column, message);
		}
	}
	
	/**
	 * setCacheClasses
	 *
	 * @param value
	 */
	public void setCacheClasses(boolean value)
	{
		this._cacheClasses = value;
	}
	
	/**
	 * @see org.xml.sax.helpers.DefaultHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	public void setDocumentLocator(Locator locator)
	{
		super.setDocumentLocator(locator);

		this._locator = locator;
	}

	/**
	 * setErrorHandler
	 *
	 * @param errorHandler
	 */
	public void setErrorHandler(IErrorHandler errorHandler)
	{
		this._errorHandler = errorHandler;
	}

	/**
	 * setProperty
	 * 
	 * @param localName
	 * @param dotIndex
	 */
	private void setProperty(String localName)
	{
		int dotIndex = localName.indexOf('.');

		if (this._nodes.size() > 0 && this._currentNode.getChildCount() > 0)
		{
			INode parent = this._nodes.pop();
			INode value = this._currentNode.getChild(0);

			if (dotIndex < localName.length() - 1)
			{
				String name = localName.substring(dotIndex + 1);
				String methodName = this.toMethodName(name);
				Method[] methods = parent.getClass().getMethods();

				for (int i = 0; i < methods.length; i++)
				{
					Method method = methods[i];
					
					if (methodName.equals(method.getName()))
					{
						Class<?>[] parameterTypes = method.getParameterTypes();
		
						if (parameterTypes.length == 1)
						{
							Class<?> parameterType = parameterTypes[0];
							
							if (parameterType.isInstance(value))
							{
								// create argument array
								Object ary = Array.newInstance(parameterType, 1);
		
								// set value in argument array
								Array.set(ary, 0, value);
		
								try
								{
									method.invoke(parent, (Object[]) ary);
									break;
								}
								catch (Exception e)
								{
									// e.printStackTrace();
								}
		
								break;
							}
						}
					}
				}
			}

			this._currentNode = parent;
		}
		else
		{
			// this should not happen
			this._currentNode = null;
		}
	}

	/**
	 * setUnknownElementClass
	 *
	 * @param elementClass
	 */
	public void setUnknownElementClass(Class<?> elementClass)
	{
		if (elementClass == null || INode.class.isInstance(elementClass) == false)
		{
			this._unknownElementClass = NodeBase.class;
		}
		else
		{
			this._unknownElementClass = elementClass;
		}
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
	 */
	public void startDocument() throws SAXException
	{
		this._documentNode = this.createDocumentNode();
		this._currentNode = this._documentNode;
		this._documentNode.setErrorHandler(this._errorHandler);
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String,
	 *      org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		if (this._namespace == null || this._namespace.equals(uri))
		{
			try
			{
				if (localName.indexOf('.') == -1)
				{
					this.enterObject(localName, attributes);
				}
				else
				{
					if (this._currentNode != null)
					{
						// add node to stack
						this._nodes.push(this._currentNode);
					}
		
					// update current node;
					this._currentNode = new Property();
				}
			}
			catch (Exception e)
			{
				throw new SAXException(e);
			}
		}
	}

	/**
	 * convertName
	 * 
	 * @param name
	 * @return String
	 */
	private String toClassName(String name)
	{
		StringBuffer sb = new StringBuffer();
		boolean toUpper = true;

		for (int i = 0; i < name.length(); i++)
		{
			char c = name.charAt(i);

			if (c == '-')
			{
				toUpper = true;
			}
			else
			{
				if (toUpper)
				{
					// add uppercase version of current letter
					sb.append(Character.toUpperCase(c));

					// reset flag
					toUpper = false;
				}
				else
				{
					// add current letter
					sb.append(c);
				}
			}
		}

		return sb.toString();
	}
	
	/**
	 * toMethodName
	 * 
	 * @param name
	 * @return String
	 */
	private String toMethodName(String name)
	{
		StringBuffer sb = new StringBuffer();
		boolean toUpper = true;

		// prepend "set"
		sb.append("set"); //$NON-NLS-1$

		for (int i = 0; i < name.length(); i++)
		{
			char c = name.charAt(i);

			if (c == '-')
			{
				toUpper = true;
			}
			else
			{
				if (toUpper)
				{
					// add uppercase version of current letter
					sb.append(Character.toUpperCase(c));

					// reset flag
					toUpper = false;
				}
				else
				{
					// add current letter
					sb.append(c);
				}
			}
		}

		return sb.toString();
	}
}