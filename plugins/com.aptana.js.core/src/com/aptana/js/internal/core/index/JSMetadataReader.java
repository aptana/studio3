/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.index;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.MetadataReader;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.JSTypeConstants;
import com.aptana.js.core.inferencing.JSTypeMapper;
import com.aptana.js.core.model.AliasElement;
import com.aptana.js.core.model.ExceptionElement;
import com.aptana.js.core.model.FunctionElement;
import com.aptana.js.core.model.ParameterElement;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.ReturnTypeElement;
import com.aptana.js.core.model.SinceElement;
import com.aptana.js.core.model.TypeElement;
import com.aptana.js.core.model.UserAgentElement;
import com.aptana.js.internal.core.parsing.sdoc.SDocParser;
import com.aptana.js.internal.core.parsing.sdoc.model.Type;

/**
 * ScriptDocReader
 */
public class JSMetadataReader extends MetadataReader
{
	private enum Element
	{
		JAVASCRIPT("javascript"), //$NON-NLS-1$
		VALUE("value"), //$NON-NLS-1$
		TYPE_MAP("type-map"), //$NON-NLS-1$
		SPECIFICATION("specification"), //$NON-NLS-1$
		RETURN_TYPE("return-type"), //$NON-NLS-1$
		REFERENCE("reference"), //$NON-NLS-1$
		PROPERTY("property"), //$NON-NLS-1$
		PARAMETER("parameter"), //$NON-NLS-1$
		MIXINS("mixins"), //$NON-NLS-1$
		MIXIN("mixin"), //$NON-NLS-1$
		METHOD("method"), //$NON-NLS-1$
		CONSTRUCTOR("constructor"), //$NON-NLS-1$
		EXCEPTION("exception"), //$NON-NLS-1$
		RETURN_DESCRIPTION("return-description"), //$NON-NLS-1$
		REMARKS("remarks"), //$NON-NLS-1$
		EXAMPLE("example"), //$NON-NLS-1$
		DESCRIPTION("description"), //$NON-NLS-1$
		DEPRECATED("deprecated"), //$NON-NLS-1$
		CONSTRUCTORS("constructors"), //$NON-NLS-1$
		CLASS("class"), //$NON-NLS-1$
		BROWSER("browser"), //$NON-NLS-1$
		ALIAS("alias"), //$NON-NLS-1$
		// Ignored elements
		BROWSERS("browsers"), //$NON-NLS-1$
		AVAILABILITY("availability"), //$NON-NLS-1$
		REFERENCES("references"), //$NON-NLS-1$
		METHODS("methods"), //$NON-NLS-1$
		PROPERTIES("properties"), //$NON-NLS-1$
		RETURN_TYPES("return-types"), //$NON-NLS-1$
		ALIASES("aliases"), //$NON-NLS-1$
		EXAMPLES("examples"), //$NON-NLS-1$
		EXCEPTIONS("exceptions"), //$NON-NLS-1$
		INTERFACE("interface"), //$NON-NLS-1$
		INTERFACES("interfaces"), //$NON-NLS-1$
		OVERVIEW("overview"), //$NON-NLS-1$
		PARAMETERS("parameters"), //$NON-NLS-1$
		TYPE_MAPS("type-maps"), //$NON-NLS-1$
		VALUES("values"), //$NON-NLS-1$
		// Undefined
		UNDEFINED(null);

		private String name;

		private Element(String name)
		{
			this.name = name;
		}

		private static Element fromString(String name)
		{
			if (name != null)
			{
				for (Element b : Element.values())
				{
					if (name.equals(b.name))
					{
						return b;
					}
				}
			}
			return UNDEFINED;
		}
	}

	private static final Pattern DOT_PATTERN = Pattern.compile("\\."); //$NON-NLS-1$
	private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+"); //$NON-NLS-1$
	private static final Pattern PROPERTY_TYPE_DELIMITER_PATTERN = Pattern.compile("\\s*\\|\\s*"); //$NON-NLS-1$

	private static final String JS_METADATA_SCHEMA = "/metadata/JSMetadataSchema.xml"; //$NON-NLS-1$
	private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("[$_a-zA-Z][$_a-zA-Z0-9]*"); //$NON-NLS-1$

	// state flags
	private boolean parsingCtors;
	private TypeElement currentClass;
	private TypeElement currentType;
	private FunctionElement currentFunction;
	private ParameterElement currentParameter;
	private ReturnTypeElement currentReturnType;
	private UserAgentElement currentUserAgent;
	private PropertyElement currentProperty;
	private ExceptionElement currentException;

	private SDocParser parser = new SDocParser();
	private Map<String, TypeElement> typesByName = new HashMap<String, TypeElement>();
	private List<AliasElement> aliases = new ArrayList<AliasElement>();

	/**
	 * Create a new instance of CoreLoader
	 */
	public JSMetadataReader()
	{
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attributes)
			throws SAXException
	{
		super.startElement(namespaceURI, localName, qualifiedName, attributes);

		switch (Element.fromString(localName))
		{
			case ALIAS:
				enterAlias(namespaceURI, localName, qualifiedName, attributes);
				break;

			case BROWSER:
				enterBrowser(namespaceURI, localName, qualifiedName, attributes);
				break;

			case CLASS:
				enterClass(namespaceURI, localName, qualifiedName, attributes);
				break;

			case CONSTRUCTORS:
				enterConstructors(namespaceURI, localName, qualifiedName, attributes);
				break;

			case DEPRECATED:
			case DESCRIPTION:
			case EXAMPLE:
			case REMARKS:
			case RETURN_DESCRIPTION:
				startTextBuffer(namespaceURI, localName, qualifiedName, attributes);
				break;

			case EXCEPTION:
				enterException(namespaceURI, localName, qualifiedName, attributes);
				break;

			case CONSTRUCTOR:
			case METHOD:
				enterMethod(namespaceURI, localName, qualifiedName, attributes);
				break;

			case MIXIN:
				enterMixin(namespaceURI, localName, qualifiedName, attributes);
				break;

			case MIXINS:
				enterMixins(namespaceURI, localName, qualifiedName, attributes);
				break;

			case PARAMETER:
				enterParameter(namespaceURI, localName, qualifiedName, attributes);
				break;

			case PROPERTY:
				enterProperty(namespaceURI, localName, qualifiedName, attributes);
				break;

			case REFERENCE:
				enterReference(namespaceURI, localName, qualifiedName, attributes);
				break;

			case RETURN_TYPE:
				enterReturnType(namespaceURI, localName, qualifiedName, attributes);
				break;

			case SPECIFICATION:
				enterSpecification(namespaceURI, localName, qualifiedName, attributes);
				break;

			case TYPE_MAP:
				enterTypeMap(namespaceURI, localName, qualifiedName, attributes);
				break;

			case VALUE:
				enterValue(namespaceURI, localName, qualifiedName, attributes);
				break;

			case UNDEFINED:
				IdeLog.logWarning(JSCorePlugin.getDefault(),
						MessageFormat.format("Unable to convert element with name {0} to enum value", localName)); //$NON-NLS-1$
				break;

			default:
				// do nothing
				break;
		}
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException
	{
		switch (Element.fromString(localName))
		{
			case BROWSER:
				exitBrowser(namespaceURI, localName, qualifiedName);
				break;

			case CLASS:
				exitClass(namespaceURI, localName, qualifiedName);
				break;

			case CONSTRUCTORS:
				exitConstructors(namespaceURI, localName, qualifiedName);
				break;

			case DEPRECATED:
				exitDeprecated(namespaceURI, localName, qualifiedName);
				break;

			case DESCRIPTION:
				exitDescription(namespaceURI, localName, qualifiedName);
				break;

			case EXAMPLE:
				exitExample(namespaceURI, localName, qualifiedName);
				break;

			case EXCEPTION:
				exitException(namespaceURI, localName, qualifiedName);
				break;

			case JAVASCRIPT:
				exitJavaScript(namespaceURI, localName, qualifiedName);
				break;

			case METHOD:
			case CONSTRUCTOR:
				exitMethod(namespaceURI, localName, qualifiedName);
				break;

			case PARAMETER:
				exitParameter(namespaceURI, localName, qualifiedName);
				break;

			case PROPERTY:
				exitProperty(namespaceURI, localName, qualifiedName);
				break;

			case REMARKS:
				exitRemarks(namespaceURI, localName, qualifiedName);
				break;

			case RETURN_DESCRIPTION:
				exitReturnDescription(namespaceURI, localName, qualifiedName);
				break;

			case RETURN_TYPE:
				exitReturnType(namespaceURI, localName, qualifiedName);
				break;

			case VALUE:
				exitValue(namespaceURI, localName, qualifiedName);
				break;
			case UNDEFINED:
				IdeLog.logWarning(JSCorePlugin.getDefault(),
						MessageFormat.format("Unable to convert element with name {0} to enum value", localName)); //$NON-NLS-1$
				break;

			default:
				// do nothing
				break;
		}
		super.endElement(namespaceURI, localName, qualifiedName);
	}

	/**
	 * start processing an alias element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	private void enterAlias(String ns, String name, String qname, Attributes attributes)
	{
		AliasElement alias = new AliasElement();
		Map<String, String> attrs = attributesToMap(attributes, true);

		alias.setName(attrs.get("name")); //$NON-NLS-1$
		alias.setType(attrs.get("type")); //$NON-NLS-1$

		aliases.add(alias);
	}

	/**
	 * start processing a browser element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	private void enterBrowser(String ns, String name, String qname, Attributes attributes)
	{
		// create a new item documentation object
		Map<String, String> attrs = attributesToMap(attributes, true);

		// set platform
		String platform = attrs.get("platform"); //$NON-NLS-1$
		String version = attrs.get("version"); //$NON-NLS-1$
		String os = attrs.get("os"); //$NON-NLS-1$
		String osVersion = attrs.get("osVersion"); //$NON-NLS-1$

		currentUserAgent = UserAgentElement.createUserAgentElement(platform, version, os, osVersion, StringUtil.EMPTY);
	}

	/**
	 * start processing a class element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	private void enterClass(String ns, String name, String qname, Attributes attributes)
	{
		Map<String, String> attrs = attributesToMap(attributes, true);
		String typeName = attrs.get("type"); //$NON-NLS-1$

		if (isValidTypeIdentifier(typeName))
		{
			String[] parts = DOT_PATTERN.split(typeName);
			String accumulatedTypeName = parts[0];
			TypeElement type = getType(accumulatedTypeName);
			TypeElement clas = getType(getTypeClass(accumulatedTypeName));

			for (int i = 1; i < parts.length; i++)
			{
				// grab name part
				String propertyName = parts[i];

				// update accumulated type name
				accumulatedTypeName += "." + propertyName; //$NON-NLS-1$ // $codepro.audit.disable stringConcatenationInLoop

				// try to grab the property off of the current type
				PropertyElement property = type.getProperty(propertyName);

				// create property, if we didn't have one
				if (property == null)
				{
					property = new PropertyElement();

					property.setName(propertyName);
					property.setIsClassProperty(true);
					property.addType(accumulatedTypeName);

					type.addProperty(property);
					// clas.addProperty(property);
				}

				// make sure to save last type we visited
				typesByName.put(type.getName(), type);
				typesByName.put(clas.getName(), clas);

				// create new types
				type = getType(accumulatedTypeName);
				clas = getType(getTypeClass(accumulatedTypeName));
			}

			// set optional superclass
			String superclass = attrs.get("superclass"); //$NON-NLS-1$

			if (superclass != null && superclass.length() > 0)
			{
				String[] types = WHITESPACE_PATTERN.split(superclass);

				for (String superType : types)
				{
					if (isValidTypeIdentifier(superType))
					{
						type.addParentType(superType);
						clas.addParentType(getTypeClass(superType));
					}
					else
					{
						String message = MessageFormat.format(Messages.JSMetadataReader_Invalid_Base_Type, superType,
								typeName);

						logError(message);
					}
				}
			}

			// set current class
			currentType = type;
			currentClass = clas;
		}
		else
		{
			String message = MessageFormat.format(Messages.JSMetadataReader_Invalid_Type_Name, typeName);

			logError(message);
		}
	}

	/**
	 * enterConstructors
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	private void enterConstructors(String ns, String name, String qname, Attributes attributes)
	{
		parsingCtors = true;
	}

	/**
	 * start processing an exception element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	private void enterException(String ns, String name, String qname, Attributes attributes)
	{
		Map<String, String> attrs = attributesToMap(attributes, true);
		String exceptionName = attrs.get("type"); //$NON-NLS-1$

		if (isValidIdentifier(exceptionName))
		{
			ExceptionElement exception = new ExceptionElement();

			exception.setType(exceptionName);

			currentException = exception;
		}
		else
		{
			logError(Messages.JSMetadataReader_Invalid_Exception_Name + exceptionName);
		}
	}

	/**
	 * Start processing a method element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	private void enterMethod(String ns, String name, String qname, Attributes attributes)
	{
		Map<String, String> attrs = attributesToMap(attributes, true);
		String mname = attrs.get("name"); //$NON-NLS-1$

		if (mname == null && currentType != null)
		{
			mname = currentType.getName();
		}

		if (isValidIdentifier(mname))
		{
			FunctionElement function = new FunctionElement();

			// function.setExtends(_currentType.getExtends());
			function.setIsConstructor(parsingCtors); // for this xml format isCtor is always one or the other,
														// user code may vary
			function.setIsMethod(!parsingCtors);

			// determine and set method name
			function.setName(mname);

			// set scope
			String scope = attrs.get("scope"); //$NON-NLS-1$

			if (scope == null || scope.length() == 0 || scope.equals("instance")) //$NON-NLS-1$
			{
				function.setIsInstanceProperty(true);
			}
			else if (scope.equals("static")) //$NON-NLS-1$
			{
				function.setIsClassProperty(true);
			}

			// set visibility
			String visibility = attrs.get("visibility"); //$NON-NLS-1$

			if (visibility != null && visibility.equals("internal")) //$NON-NLS-1$
			{
				function.setIsInternal(true);
			}

			currentFunction = function;
		}
	}

	/**
	 * enterMixin
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	private void enterMixin(String ns, String name, String qname, Attributes attributes)
	{
	}

	/**
	 * enterMixins
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	private void enterMixins(String ns, String name, String qname, Attributes attributes)
	{
	}

	/**
	 * Start processing a parameter element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	private void enterParameter(String ns, String name, String qname, Attributes attributes)
	{
		Map<String, String> attrs = attributesToMap(attributes, true);
		String parameterName = attrs.get("name"); //$NON-NLS-1$

		if (isValidIdentifier(parameterName))
		{
			// create a new parameter documentation object
			ParameterElement parameter = new ParameterElement();

			// grab and set properties
			parameter.setName(parameterName);

			String typespec = attrs.get("type"); //$NON-NLS-1$
			List<Type> types = parseTypes(typespec);

			if (types != null)
			{
				for (Type type : types)
				{
					parameter.addType(type.toSource());
				}
			}
			else
			{
				String message = MessageFormat.format(Messages.JSMetadataReader_Invalid_Parameter_Type, typespec,
						parameterName);

				logError(message);
			}

			parameter.setUsage(attrs.get("usage")); //$NON-NLS-1$

			// store parameter
			currentParameter = parameter;
		}
	}

	/**
	 * Start processing a property element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	private void enterProperty(String ns, String name, String qname, Attributes attributes)
	{
		Map<String, String> attrs = attributesToMap(attributes, true);
		String propertyName = attrs.get("name"); //$NON-NLS-1$

		if (isValidIdentifier(propertyName))
		{
			// create a new property documentation object
			PropertyElement property = new PropertyElement();

			// grab and set property values
			property.setName(propertyName);

			// set scope
			String scope = attrs.get("scope"); //$NON-NLS-1$

			if (scope == null || scope.length() == 0 || scope.equals("instance")) //$NON-NLS-1$
			{
				property.setIsInstanceProperty(true);
			}
			else if (scope.equals("static")) //$NON-NLS-1$
			{
				property.setIsClassProperty(true);
			}
			// TODO: else error or warning?

			// set types
			String types = attrs.get("type"); //$NON-NLS-1$

			for (String propertyType : PROPERTY_TYPE_DELIMITER_PATTERN.split(types))
			{
				if (isValidTypeIdentifier(propertyType))
				{
					property.addType(propertyType);
				}
				else
				{
					String message = MessageFormat.format(Messages.JSMetadataReader_Invalid_Property_Type,
							propertyType, propertyName);

					logError(message);
				}
			}

			// set current property
			currentProperty = property;
		}
	}

	/**
	 * Exit a reference element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	private void enterReference(String ns, String name, String qname, Attributes attributes)
	{
		if (currentFunction != null)
		{
			Map<String, String> attrs = attributesToMap(attributes, true);

			currentFunction.addReference(attrs.get("name")); //$NON-NLS-1$
		}
	}

	/**
	 * Exit a return-type element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	private void enterReturnType(String ns, String name, String qname, Attributes attributes)
	{
		Map<String, String> attrs = attributesToMap(attributes, true);
		String type = attrs.get("type"); //$NON-NLS-1$

		if (isValidTypeIdentifier(type))
		{
			ReturnTypeElement returnType = new ReturnTypeElement();

			// grab and set property values
			returnType.setType(type);

			currentReturnType = returnType;
		}
		else
		{
			logError(Messages.JSMetadataReader_Invalid_Return_Type + type);
		}
	}

	/**
	 * start processing a specification element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	private void enterSpecification(String ns, String name, String qname, Attributes attributes)
	{
		SinceElement since = new SinceElement();
		Map<String, String> attrs = attributesToMap(attributes, true);

		// set name
		since.setName(attrs.get("name")); //$NON-NLS-1$

		// set version
		String version = attrs.get("version"); //$NON-NLS-1$

		if (version != null)
		{
			since.setVersion(version);
		}

		if (currentFunction != null)
		{
			currentFunction.addSince(since);
		}
		else if (currentProperty != null)
		{
			currentProperty.addSince(since);
		}
		else if (currentType != null)
		{
			currentType.addSince(since);
		}
	}

	/**
	 * Processing a type map
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	private void enterTypeMap(String ns, String name, String qname, Attributes attributes)
	{
		Map<String, String> attrs = attributesToMap(attributes, true);
		String sourceType = attrs.get("source-type"); //$NON-NLS-1$
		String destinationType = attrs.get("destination-type"); //$NON-NLS-1$

		if (!StringUtil.isEmpty(sourceType) && !StringUtil.isEmpty(destinationType))
		{
			JSTypeMapper.getInstance().addTypeMapping(sourceType, destinationType);
		}
	}

	/**
	 * start processing a value element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	private void enterValue(String ns, String name, String qname, Attributes attributes)
	{
	}

	/**
	 * Exit a browser element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	private void exitBrowser(String ns, String name, String qname)
	{
		if (currentUserAgent != null)
		{
			if (currentProperty != null)
			{
				currentProperty.addUserAgent(currentUserAgent);
			}
			else if (currentFunction != null)
			{
				currentFunction.addUserAgent(currentUserAgent);
			}
			else if (currentType != null)
			{
				currentType.addUserAgent(currentUserAgent);
			}

			// clear current class
			currentUserAgent = null;
		}
	}

	/**
	 * Exit a class element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	private void exitClass(String ns, String name, String qname)
	{
		if (currentType != null)
		{
			typesByName.put(currentType.getName(), currentType);

			currentType = null;
		}

		if (currentClass != null)
		{
			typesByName.put(currentClass.getName(), currentClass);

			currentClass = null;
		}
	}

	/**
	 * Exit a constructors element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	private void exitConstructors(String ns, String name, String qname)
	{
		parsingCtors = false;
	}

	/**
	 * Exit a deprecated element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	private void exitDeprecated(String ns, String name, String qname)
	{
		// TODO Get text and save that as some sort of deprecation comment!
		boolean deprecated = true;

		if (currentProperty != null)
		{
			currentProperty.setIsDeprecated(deprecated);
		}
		else if (currentFunction != null)
		{
			currentFunction.setIsDeprecated(deprecated);
		}
		else if (currentType != null)
		{
			currentType.setIsDeprecated(deprecated);
		}
	}

	/**
	 * Exit a description element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	private void exitDescription(String ns, String name, String qname)
	{
		String description = normalizeText(getText());

		if (currentParameter != null)
		{
			currentParameter.setDescription(description);
		}
		// else if (_currentException != false)
		// {
		// // ignore
		// _currentException = (_currentException == false ) ? false : true;
		// }
		else if (currentProperty != null)
		{
			currentProperty.setDescription(description);
		}
		else if (currentFunction != null)
		{
			if (currentReturnType != null)
			{
				currentReturnType.setDescription(description);
			}
			else
			{
				currentFunction.setDescription(description);
			}
		}
		else if (currentType != null)
		{
			currentType.setDescription(description);
		}
		// else if (_currentProject != null)
		// {
		// // add description to the current method
		// _currentProject.setDescription(description);
		// }
		else if (currentUserAgent != null)
		{
			// add description to the current method
			currentUserAgent.setDescription(description);
		}
		// TODO: else throw error
	}

	/**
	 * Exit a example element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	private void exitExample(String ns, String name, String qname)
	{
		String example = getText();

		if (currentProperty != null)
		{
			currentProperty.addExample(example);
		}
		else if (currentFunction != null)
		{
			currentFunction.addExample(example);
		}

		// TODO: The schema allows these on classes as well
	}

	/**
	 * Exit a exception element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	private void exitException(String ns, String name, String qname)
	{
		if (currentException != null)
		{
			if (currentProperty != null)
			{ // $codepro.audit.disable emptyIfStatement
				// this doesn't make sense to me, but it is defined in the schema. Ignore for now
			}
			else if (currentFunction != null)
			{
				currentFunction.addException(currentException);
			}
			// TODO: else throw error

			currentException = null;
		}
	}

	/**
	 * Exit a javascript element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	private void exitJavaScript(String ns, String name, String qname)
	{
	}

	/**
	 * Exit a method element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	private void exitMethod(String ns, String name, String qname)
	{
		if (currentFunction != null)
		{
			if (currentFunction.isClassProperty())
			{
				if (currentClass != null)
				{
					currentClass.addProperty(currentFunction);
				}
			}
			else if (currentFunction.isInstanceProperty())
			{
				if (currentType != null)
				{
					currentType.addProperty(currentFunction);
				}
			}
			// TODO: else warning or error about unknown method role

			currentFunction = null;
		}
	}

	/**
	 * Exit a parameter element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	private void exitParameter(String ns, String name, String qname)
	{
		if (currentParameter != null)
		{
			if (currentFunction != null)
			{
				// add parameter to parameter list
				currentFunction.addParameter(currentParameter);
			}

			// clear current parameter
			currentParameter = null;
		}
	}

	/**
	 * Exit a property element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	private void exitProperty(String ns, String name, String qname)
	{
		if (currentProperty != null)
		{
			if (currentProperty.isClassProperty())
			{
				if (currentClass != null)
				{
					currentClass.addProperty(currentProperty);
				}
			}
			else if (currentProperty.isInstanceProperty())
			{
				if (currentType != null)
				{
					currentType.addProperty(currentProperty);
				}
			}
			// TODO: else warning or error about unknown property role

			currentProperty = null;
		}
	}

	/**
	 * Exit a remarks element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	private void exitRemarks(String ns, String name, String qname)
	{
		getText();
	}

	/**
	 * Exit a description element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	private void exitReturnDescription(String ns, String name, String qname)
	{
		getText();
	}

	/**
	 * Exit a return-type element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	private void exitReturnType(String ns, String name, String qname)
	{
		if (currentReturnType != null)
		{
			if (currentFunction != null)
			{
				currentFunction.addReturnType(currentReturnType);
			}

			currentReturnType = null;
		}
	}

	/**
	 * Exit a field element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	private void exitValue(String ns, String name, String qname)
	{
	}

	/**
	 * getAliases
	 * 
	 * @return
	 */
	public AliasElement[] getAliases()
	{
		return aliases.toArray(new AliasElement[aliases.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataReader#getSchemaStream()
	 */
	@Override
	protected InputStream getSchemaStream()
	{
		try
		{
			return FileLocator.openStream(JSCorePlugin.getDefault().getBundle(),
					Path.fromPortableString(JS_METADATA_SCHEMA), false);
		}
		catch (IOException e)
		{
			return getClass().getResourceAsStream(JS_METADATA_SCHEMA);
		}
	}

	/**
	 * getType
	 * 
	 * @param typeName
	 * @return
	 */
	private TypeElement getType(String typeName)
	{
		TypeElement result = typesByName.get(typeName);

		if (result == null)
		{
			result = new TypeElement();

			result.setName(typeName);

			// NOTE: type will be added in exitClass
		}

		return result;
	}

	private String getTypeClass(String accumulatedTypeName)
	{
		return JSTypeConstants.GENERIC_CLASS_OPEN + accumulatedTypeName + JSTypeConstants.GENERIC_CLOSE;
	}

	/**
	 * getTypes
	 * 
	 * @return
	 */
	public TypeElement[] getTypes()
	{
		Collection<TypeElement> values = typesByName.values();
		TypeElement[] types = new TypeElement[values.size()];

		return values.toArray(types);
	}

	/**
	 * isValidIdentifier
	 * 
	 * @param name
	 * @return
	 */
	private boolean isValidIdentifier(String name)
	{
		boolean result = false;

		if (name != null)
		{
			Matcher m = IDENTIFIER_PATTERN.matcher(name);

			result = m.matches();
		}

		return result;
	}

	/**
	 * isValidTypeIdentifier
	 * 
	 * @param name
	 * @return
	 */
	private boolean isValidTypeIdentifier(String name)
	{
		boolean result = false;

		if (name != null)
		{
			List<Type> types = parseTypes(name);

			result = (types != null && types.size() == 1);
		}

		return result;
	}

	/**
	 * parseTypes
	 * 
	 * @param typeSpec
	 * @return
	 */
	private List<Type> parseTypes(String typeSpec)
	{
		List<Type> types = null;

		try
		{
			types = parser.parseType(typeSpec);
		}
		catch (Exception e) // $codepro.audit.disable emptyCatchClause
		{
			// we return null if the typeSpec is invalid
		}

		return types;
	}
}
