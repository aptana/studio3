/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.editor.js.contentassist.index;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.xml.sax.Attributes;

import com.aptana.editor.common.contentassist.MetadataReader;
import com.aptana.editor.js.Activator;
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
	private static final String JS_METADATA_SCHEMA = "/metadata/JSMetadataSchema.xml"; //$NON-NLS-1$
	private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("[$_a-zA-Z][$_a-zA-Z0-9]*"); //$NON-NLS-1$
	private static final Pattern TYPE_PATTERN = Pattern
			.compile("[$_a-zA-Z][$_a-zA-Z0-9]*(?:(?:<[$_a-zA-Z][$_a-zA-Z0-9]*>)|(?:\\[\\]))?"); //$NON-NLS-1$

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

		alias.setName(attributes.getValue("name")); //$NON-NLS-1$
		alias.setType(attributes.getValue("type")); //$NON-NLS-1$

		// add somewhere?
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

		// set platform
		userAgent.setPlatform(attributes.getValue("platform")); //$NON-NLS-1$

		// set version
		String version = attributes.getValue("version"); //$NON-NLS-1$

		if (version != null)
		{
			userAgent.setVersion(version);
		}

		// set OS
		String os = attributes.getValue("os"); //$NON-NLS-1$

		if (os != null)
		{
			userAgent.setOS(os);
		}

		// set OS version
		String osVersion = attributes.getValue("osVersion"); //$NON-NLS-1$

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
		String typeName = attributes.getValue("type"); //$NON-NLS-1$

		if (this.isValidIdentifier(typeName))
		{
			String className = "Class<" + typeName + ">"; //$NON-NLS-1$ //$NON-NLS-2$

			// create a new class documentation object
			TypeElement type = this.getType(typeName);
			TypeElement clas = this.getType(className);

			// set optional superclass
			String superclass = attributes.getValue("superclass"); //$NON-NLS-1$

			if (superclass != null && superclass.length() > 0)
			{
				String[] types = superclass.split("\\s+"); //$NON-NLS-1$

				for (String superType : types)
				{
					if (this.isValidTypeIdentifier(superType))
					{
						type.addParentType(superType);
						clas.addParentType("Class<" + superType + ">"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					else
					{
						String message = MessageFormat.format(Messages.JSMetadataReader_Invalid_Supertype_Name,
								superType, typeName);

						Activator.logError(message, null);
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

			Activator.logError(message, null);
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
		String exceptionName = attributes.getValue("type"); //$NON-NLS-1$

		if (this.isValidIdentifier(exceptionName))
		{
			ExceptionElement exception = new ExceptionElement();

			exception.setType(exceptionName);

			this._currentException = exception;
		}
		else
		{
			Activator.logError(Messages.JSMetadataReader_Invalid_Exception_Name + exceptionName, null);
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
		String mname = attributes.getValue("name"); //$NON-NLS-1$

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
			String scope = attributes.getValue("scope"); //$NON-NLS-1$

			if (scope == null || scope.length() == 0 || scope.equals("instance")) //$NON-NLS-1$
			{
				function.setIsInstanceProperty(true);
			}
			else if (scope.equals("static")) //$NON-NLS-1$
			{
				function.setIsClassProperty(true);
			}

			// set visibility
			String visibility = attributes.getValue("visibility"); //$NON-NLS-1$

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
		String parameterName = attributes.getValue("name"); //$NON-NLS-1$

		if (this.isValidIdentifier(parameterName))
		{
			// create a new parameter documentation object
			ParameterElement parameter = new ParameterElement();

			// grab and set properties
			parameter.setName(parameterName);

			for (String type : attributes.getValue("type").split("\\s*[,|]\\s*")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				if (this.isValidTypeIdentifier(type))
				{
					parameter.addType(type);
				}
				else
				{
					String message = MessageFormat.format(Messages.JSMetadataReader_Invalid_Parameter_Type, type,
							parameterName);

					Activator.logError(message, null);
				}
			}

			parameter.setUsage(attributes.getValue("usage")); //$NON-NLS-1$

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
		String propertyName = attributes.getValue("name"); //$NON-NLS-1$

		if (this.isValidIdentifier(propertyName))
		{
			// create a new property documentation object
			PropertyElement property = new PropertyElement();

			// grab and set property values
			property.setName(propertyName);

			// set scope
			String scope = attributes.getValue("scope"); //$NON-NLS-1$

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
			String type = attributes.getValue("type"); //$NON-NLS-1$
			String[] types = type.split("\\s*\\|\\s*"); //$NON-NLS-1$

			for (String propertyType : types)
			{
				if (this.isValidTypeIdentifier(propertyType))
				{
					ReturnTypeElement returnType = new ReturnTypeElement();

					returnType.setType(propertyType);

					property.addType(returnType);
				}
				else
				{
					String message = MessageFormat.format(Messages.JSMetadataReader_Invalid_Property_Type,
							propertyType, propertyName);

					Activator.logError(message, null);
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
			this._currentFunction.addReference(attributes.getValue("name")); //$NON-NLS-1$
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
		String type = attributes.getValue("type"); //$NON-NLS-1$

		if (this.isValidTypeIdentifier(type))
		{
			ReturnTypeElement returnType = new ReturnTypeElement();

			// grab and set property values
			returnType.setType(type); //$NON-NLS-1$

			this._currentReturnType = returnType;
		}
		else
		{
			Activator.logError(Messages.JSMetadataReader_Invalid_Return_Type + type, null);
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

		// set name
		since.setName(attributes.getValue("name")); //$NON-NLS-1$

		// set version
		String version = attributes.getValue("version"); //$NON-NLS-1$

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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.MetadataReader#getSchemaStream()
	 */
	@Override
	protected InputStream getSchemaStream()
	{
		try
		{
			return FileLocator.openStream(Activator.getDefault().getBundle(),
					Path.fromPortableString(JS_METADATA_SCHEMA), false);
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
