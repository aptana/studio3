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

/**
 * ScriptDocReader
 */
public class JSMetadataReader extends MetadataReader
{
	static final Pattern DOT_PATTERN = Pattern.compile("\\."); //$NON-NLS-1$
	static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+"); //$NON-NLS-1$
	static final Pattern PARAMETER_TYPE_DELIMITER_PATTERN = Pattern.compile("\\s*[,|]\\s*"); //$NON-NLS-1$
	static final Pattern PROPERTY_TYPE_DELIMITER_PATTERN = Pattern.compile("\\s*\\|\\s*"); //$NON-NLS-1$

	private static final String JS_METADATA_SCHEMA = "/metadata/JSMetadataSchema.xml"; //$NON-NLS-1$
	private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("[$_a-zA-Z][$_a-zA-Z0-9]*"); //$NON-NLS-1$
	private static final Pattern TYPE_PATTERN = Pattern
		.compile("[$_a-zA-Z][$_a-zA-Z0-9]*(?:\\.[$_a-zA-Z][$_a-zA-Z0-9]*)*(?:(?:<[$_a-zA-Z][$_a-zA-Z0-9]*>)|(?:\\[\\]))?"); //$NON-NLS-1$

	// state flags
	private boolean _parsingCtors;
	private TypeElement _currentClass;
	private TypeElement _currentType;
	private FunctionElement _currentFunction;
	private ParameterElement _currentParameter;
	private ReturnTypeElement _currentReturnType;
	private UserAgentElement _currentUserAgent;
	private PropertyElement _currentProperty;
	private ExceptionElement _currentException;

	private Map<String, TypeElement> _typesByName = new HashMap<String, TypeElement>();
	private List<AliasElement> _aliases = new ArrayList<AliasElement>();

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
		Map<String, String> attrs = this.attributesToMap(attributes, true);

		alias.setName(attrs.get("name")); //$NON-NLS-1$
		alias.setType(attrs.get("type")); //$NON-NLS-1$

		this._aliases.add(alias);
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
		Map<String, String> attrs = this.attributesToMap(attributes, true);

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

		this._currentUserAgent = userAgent;
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
		Map<String, String> attrs = this.attributesToMap(attributes, true);
		String typeName = attrs.get("type"); //$NON-NLS-1$

		if (this.isValidTypeIdentifier(typeName))
		{
			String[] parts = DOT_PATTERN.split(typeName);
			String accumulatedTypeName = parts[0];
			TypeElement type = this.getType(accumulatedTypeName);
			TypeElement clas = this.getType(getTypeClass(accumulatedTypeName));

			for (int i = 1; i < parts.length; i++)
			{
				// grab name part
				String propertyName = parts[i];

				// update accumulated type name
				accumulatedTypeName += "." + propertyName; //$NON-NLS-1$

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
				this._typesByName.put(type.getName(), type);
				this._typesByName.put(clas.getName(), clas);

				// create new types
				type = this.getType(accumulatedTypeName);
				clas = this.getType(getTypeClass(accumulatedTypeName));
			}

			// set optional superclass
			String superclass = attrs.get("superclass"); //$NON-NLS-1$

			if (superclass != null && superclass.length() > 0)
			{
				String[] types = WHITESPACE_PATTERN.split(superclass);

				for (String superType : types)
				{
					if (this.isValidTypeIdentifier(superType))
					{
						type.addParentType(superType);
						clas.addParentType(getTypeClass(superType));
					}
					else
					{
						String message = MessageFormat.format(Messages.JSMetadataReader_Invalid_Supertype_Name, superType, typeName);

						JSPlugin.logError(message, null);
					}
				}
			}

			// set current class
			this._currentType = type;
			this._currentClass = clas;
		}
		else
		{
			String message = MessageFormat.format(Messages.JSMetadataReader_Invalid_Type_Name, typeName);

			JSPlugin.logError(message, null);
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
		this._parsingCtors = true;
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
		Map<String, String> attrs = this.attributesToMap(attributes, true);
		String exceptionName = attrs.get("type"); //$NON-NLS-1$

		if (this.isValidIdentifier(exceptionName))
		{
			ExceptionElement exception = new ExceptionElement();

			exception.setType(exceptionName);

			this._currentException = exception;
		}
		else
		{
			JSPlugin.logError(Messages.JSMetadataReader_Invalid_Exception_Name + exceptionName, null);
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
		Map<String, String> attrs = this.attributesToMap(attributes, true);
		String mname = attrs.get("name"); //$NON-NLS-1$

		if (mname == null && this._currentType != null)
		{
			mname = this._currentType.getName();
		}

		if (this.isValidIdentifier(mname))
		{
			FunctionElement function = new FunctionElement();

			// function.setExtends(this._currentType.getExtends());
			function.setIsConstructor(this._parsingCtors); // for this xml format isCtor is always one or the other,
															// user code may vary
			function.setIsMethod(!this._parsingCtors);

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

			this._currentFunction = function;
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
		Map<String, String> attrs = this.attributesToMap(attributes, true);
		String parameterName = attrs.get("name"); //$NON-NLS-1$

		if (this.isValidIdentifier(parameterName))
		{
			// create a new parameter documentation object
			ParameterElement parameter = new ParameterElement();

			// grab and set properties
			parameter.setName(parameterName);
			
			String types = attrs.get("type"); //$NON-NLS-1$

			for (String type : PARAMETER_TYPE_DELIMITER_PATTERN.split(types))
			{
				if (this.isValidTypeIdentifier(type))
				{
					parameter.addType(type);
				}
				else
				{
					String message = MessageFormat.format(Messages.JSMetadataReader_Invalid_Parameter_Type, type, parameterName);

					JSPlugin.logError(message, null);
				}
			}

			parameter.setUsage(attrs.get("usage")); //$NON-NLS-1$

			// store parameter
			this._currentParameter = parameter;
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
		Map<String, String> attrs = this.attributesToMap(attributes, true);
		String propertyName = attrs.get("name"); //$NON-NLS-1$

		if (this.isValidIdentifier(propertyName))
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
			else
			{
				// TODO: error or warning?
			}

			// set types
			String types = attrs.get("type"); //$NON-NLS-1$

			for (String propertyType : PROPERTY_TYPE_DELIMITER_PATTERN.split(types))
			{
				if (this.isValidTypeIdentifier(propertyType))
				{
					property.addType(propertyType);
				}
				else
				{
					String message = MessageFormat.format(Messages.JSMetadataReader_Invalid_Property_Type, propertyType, propertyName);

					JSPlugin.logError(message, null);
				}
			}

			// set current property
			this._currentProperty = property;
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
		if (this._currentFunction != null)
		{
			Map<String, String> attrs = this.attributesToMap(attributes, true);

			this._currentFunction.addReference(attrs.get("name")); //$NON-NLS-1$
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
		Map<String, String> attrs = this.attributesToMap(attributes, true);
		String type = attrs.get("type"); //$NON-NLS-1$

		if (this.isValidTypeIdentifier(type))
		{
			ReturnTypeElement returnType = new ReturnTypeElement();

			// grab and set property values
			returnType.setType(type);

			this._currentReturnType = returnType;
		}
		else
		{
			JSPlugin.logError(Messages.JSMetadataReader_Invalid_Return_Type + type, null);
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
		Map<String, String> attrs = this.attributesToMap(attributes, true);

		// set name
		since.setName(attrs.get("name")); //$NON-NLS-1$

		// set version
		String version = attrs.get("version"); //$NON-NLS-1$

		if (version != null)
		{
			since.setVersion(version);
		}

		if (this._currentFunction != null)
		{
			this._currentFunction.addSince(since);
		}
		else if (this._currentProperty != null)
		{
			this._currentProperty.addSince(since);
		}
		else if (this._currentType != null)
		{
			this._currentType.addSince(since);
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
		if (this._currentUserAgent != null)
		{
			if (this._currentProperty != null)
			{
				this._currentProperty.addUserAgent(this._currentUserAgent);
			}
			else if (this._currentFunction != null)
			{
				this._currentFunction.addUserAgent(this._currentUserAgent);
			}
			else if (this._currentType != null)
			{
				this._currentType.addUserAgent(this._currentUserAgent);
			}

			// clear current class
			this._currentUserAgent = null;
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
		if (this._currentType != null)
		{
			this._typesByName.put(this._currentType.getName(), this._currentType);

			this._currentType = null;
		}

		if (this._currentClass != null)
		{
			this._typesByName.put(this._currentClass.getName(), this._currentClass);

			this._currentClass = null;
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
		this._parsingCtors = false;
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
		String description = this.normalizeText(this.getText());

		if (this._currentParameter != null)
		{
			this._currentParameter.setDescription(description);
		}
		// else if (this._currentException != false)
		// {
		// // ignore
		// this._currentException = (this._currentException == false ) ? false : true;
		// }
		else if (this._currentProperty != null)
		{
			this._currentProperty.setDescription(description);
		}
		else if (this._currentFunction != null)
		{
			if (this._currentReturnType != null)
			{
				this._currentReturnType.setDescription(description);
			}
			else
			{
				this._currentFunction.setDescription(description);
			}
		}
		else if (this._currentType != null)
		{
			this._currentType.setDescription(description);
		}
		// else if (this._currentProject != null)
		// {
		// // add description to the current method
		// this._currentProject.setDescription(description);
		// }
		else if (this._currentUserAgent != null)
		{
			// add description to the current method
			this._currentUserAgent.setDescription(description);
		}
		else
		{
			// throw error
		}
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
		String example = this.getText();

		if (this._currentProperty != null)
		{
			this._currentProperty.addExample(example);
		}
		else if (this._currentFunction != null)
		{
			this._currentFunction.addExample(example);
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
		if (this._currentException != null)
		{
			if (this._currentProperty != null)
			{
				// this doesn't make sense to me, but it is defined in the schema
			}
			else if (this._currentFunction != null)
			{
				this._currentFunction.addException(this._currentException);
			}
			else
			{
				// throw error
			}

			this._currentException = null;
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
		if (this._currentFunction != null)
		{
			if (this._currentFunction.isClassProperty())
			{
				if (this._currentClass != null)
				{
					this._currentClass.addProperty(this._currentFunction);
				}
			}
			else if (this._currentFunction.isInstanceProperty())
			{
				if (this._currentType != null)
				{
					this._currentType.addProperty(this._currentFunction);
				}
			}
			else
			{
				// TODO: warning or error about unknown method role
			}

			this._currentFunction = null;
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
		if (this._currentParameter != null)
		{
			if (this._currentFunction != null)
			{
				// add parameter to parameter list
				this._currentFunction.addParameter(this._currentParameter);
			}

			// clear current parameter
			this._currentParameter = null;
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
		if (this._currentProperty != null)
		{
			if (this._currentProperty.isClassProperty())
			{
				if (this._currentClass != null)
				{
					this._currentClass.addProperty(this._currentProperty);
				}
			}
			else if (this._currentProperty.isInstanceProperty())
			{
				if (this._currentType != null)
				{
					this._currentType.addProperty(this._currentProperty);
				}
			}
			else
			{
				// TODO: warning or error about unknown property role
			}

			this._currentProperty = null;
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
		this.getText();
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
		this.getText();
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
		if (this._currentReturnType != null)
		{
			if (this._currentFunction != null)
			{
				this._currentFunction.addReturnType(this._currentReturnType);
			}

			this._currentReturnType = null;
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
		return this._aliases.toArray(new AliasElement[this._aliases.size()]);
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
			return FileLocator.openStream(JSPlugin.getDefault().getBundle(), Path.fromPortableString(JS_METADATA_SCHEMA), false);
		}
		catch (IOException e)
		{
			return this.getClass().getResourceAsStream(JS_METADATA_SCHEMA);
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
		TypeElement result = this._typesByName.get(typeName);

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
		Collection<TypeElement> values = this._typesByName.values();
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
			Matcher m = TYPE_PATTERN.matcher(name);

			result = m.matches();
		}

		return result;
	}
}
