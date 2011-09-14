/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.index;

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

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.contentassist.MetadataReader;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.model.AliasElement;
import com.aptana.editor.js.contentassist.model.ExceptionElement;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.ParameterElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.ReturnTypeElement;
import com.aptana.editor.js.contentassist.model.SinceElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.contentassist.model.UserAgentElement;
import com.aptana.editor.js.inferencing.JSTypeMapper;
import com.aptana.editor.js.sdoc.model.Type;
import com.aptana.editor.js.sdoc.parsing.SDocParser;

/**
 * ScriptDocReader
 */
public class JSMetadataReader extends MetadataReader
{
	static final Pattern DOT_PATTERN = Pattern.compile("\\."); //$NON-NLS-1$
	static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+"); //$NON-NLS-1$
	static final Pattern PROPERTY_TYPE_DELIMITER_PATTERN = Pattern.compile("\\s*\\|\\s*"); //$NON-NLS-1$

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

	/**
	 * start processing an alias element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterAlias(String ns, String name, String qname, Attributes attributes)
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
	public void enterBrowser(String ns, String name, String qname, Attributes attributes)
	{
		// create a new item documentation object
		UserAgentElement userAgent = new UserAgentElement();
		Map<String, String> attrs = attributesToMap(attributes, true);

		// set platform
		userAgent.setPlatform(attrs.get("platform")); //$NON-NLS-1$

		// set version
		String version = attrs.get("version"); //$NON-NLS-1$

		if (version != null)
		{
			userAgent.setVersion(version);
		}

		// set OS
		String os = attrs.get("os"); //$NON-NLS-1$

		if (os != null)
		{
			userAgent.setOS(os);
		}

		// set OS version
		String osVersion = attrs.get("osVersion"); //$NON-NLS-1$

		if (osVersion != null)
		{
			userAgent.setOSVersion(osVersion);
		}

		currentUserAgent = userAgent;
	}

	/**
	 * start processing a class element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterClass(String ns, String name, String qname, Attributes attributes)
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
	public void enterConstructors(String ns, String name, String qname, Attributes attributes)
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
	public void enterException(String ns, String name, String qname, Attributes attributes)
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
	public void enterMethod(String ns, String name, String qname, Attributes attributes)
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
	public void enterMixin(String ns, String name, String qname, Attributes attributes)
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
	public void enterMixins(String ns, String name, String qname, Attributes attributes)
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
	public void enterParameter(String ns, String name, String qname, Attributes attributes)
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
	public void enterProperty(String ns, String name, String qname, Attributes attributes)
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
	public void enterReference(String ns, String name, String qname, Attributes attributes)
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
	public void enterReturnType(String ns, String name, String qname, Attributes attributes)
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
	public void enterSpecification(String ns, String name, String qname, Attributes attributes)
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
	public void enterTypeMap(String ns, String name, String qname, Attributes attributes)
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
	public void enterValue(String ns, String name, String qname, Attributes attributes)
	{
	}

	/**
	 * Exit a browser element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitBrowser(String ns, String name, String qname)
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
	public void exitClass(String ns, String name, String qname)
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
	public void exitConstructors(String ns, String name, String qname)
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
	public void exitDeprecated(String ns, String name, String qname)
	{
	}

	/**
	 * Exit a description element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitDescription(String ns, String name, String qname)
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
	public void exitExample(String ns, String name, String qname)
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
	public void exitException(String ns, String name, String qname)
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
	public void exitJavaScript(String ns, String name, String qname)
	{
	}

	/**
	 * Exit a method element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitMethod(String ns, String name, String qname)
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
	public void exitParameter(String ns, String name, String qname)
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
	public void exitProperty(String ns, String name, String qname)
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
	public void exitRemarks(String ns, String name, String qname)
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
	public void exitReturnDescription(String ns, String name, String qname)
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
	public void exitReturnType(String ns, String name, String qname)
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
	public void exitValue(String ns, String name, String qname)
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
			return FileLocator.openStream(JSPlugin.getDefault().getBundle(),
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
	protected boolean isValidIdentifier(String name)
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
	protected boolean isValidTypeIdentifier(String name)
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
	protected List<Type> parseTypes(String typeSpec)
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
