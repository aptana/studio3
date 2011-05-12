/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;

import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.contentassist.model.AliasElement;
import com.aptana.editor.js.contentassist.model.EventElement;
import com.aptana.editor.js.contentassist.model.EventPropertyElement;
import com.aptana.editor.js.contentassist.model.ExceptionElement;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.ParameterElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.ReturnTypeElement;
import com.aptana.editor.js.contentassist.model.SinceElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.contentassist.model.UserAgentElement;
import com.aptana.json.IContextHandler;
import com.aptana.json.IState;

/**
 * JSCAHandler
 */
public class JSCAHandler implements IContextHandler
{
	private static enum PropertyName
	{
		UNDEFINED(""), //$NON-NLS-1$
		VERSION("version"), //$NON-NLS-1$
		ALIASES("aliases"), //$NON-NLS-1$
		TYPES("types"), //$NON-NLS-1$
		NAME("name"), //$NON-NLS-1$
		DESCRIPTION("description"), //$NON-NLS-1$
		TYPE("type"), //$NON-NLS-1$
		DEPRECATED("deprecated"), //$NON-NLS-1$
		USER_AGENTS("userAgents"), //$NON-NLS-1$
		SINCE("since"), //$NON-NLS-1$
		INHERITS("inherits"), //$NON-NLS-1$
		PROPERTIES("properties"), //$NON-NLS-1$
		FUNCTIONS("functions"), //$NON-NLS-1$
		EVENTS("events"), //$NON-NLS-1$
		REMARKS("remarks"), //$NON-NLS-1$
		PLATFORM("platform"), //$NON-NLS-1$
		OS("os"), //$NON-NLS-1$
		OS_VERSION("osVersion"), //$NON-NLS-1$
		IS_INSTANCE_PROPERTY("isInstanceProperty"), //$NON-NLS-1$
		IS_CLASS_PROPERTY("isClassProperty"), //$NON-NLS-1$
		IS_INTERNAL("isInternal"), //$NON-NLS-1$
		EXAMPLES("examples"), //$NON-NLS-1$
		PARAMETERS("parameters"), //$NON-NLS-1$
		REFERENCES("references"), //$NON-NLS-1$
		EXCEPTIONS("exceptions"), //$NON-NLS-1$
		RETURN_TYPES("returnTypes"), //$NON-NLS-1$
		IS_CONSTRUCTOR("isConstructor"), //$NON-NLS-1$
		IS_METHOD("isMethod"), //$NON-NLS-1$
		CODE("code"), //$NON-NLS-1$
		USAGE("usage"); //$NON-NLS-1$

		private static Map<String, PropertyName> NAME_MAP;
		private String _name;

		static
		{
			NAME_MAP = new HashMap<String, PropertyName>();

			for (PropertyName property : EnumSet.allOf(PropertyName.class))
			{
				NAME_MAP.put(property.getName(), property);
			}
		}

		public static PropertyName get(String name)
		{
			PropertyName result = UNDEFINED;

			if (NAME_MAP.containsKey(name))
			{
				result = NAME_MAP.get(name);
			}

			return result;
		}

		private PropertyName(String name)
		{
			this._name = name;
		}

		public String getName()
		{
			return this._name;
		}
	}

	private static enum TypeName
	{
		UNDEFINED(""), //$NON-NLS-1$
		JSMETADATA("JSMetadata"), //$NON-NLS-1$
		ALIAS("Alias"), //$NON-NLS-1$
		TYPE("Type"), //$NON-NLS-1$
		USER_AGENT("UserAgent"), //$NON-NLS-1$
		SINCE("Since"), //$NON-NLS-1$
		PROPERTY("Property"), //$NON-NLS-1$
		FUNCTION("Function"), //$NON-NLS-1$
		EVENT("Event"), //$NON-NLS-1$
		EVENT_PROPERTY("EventProperty"), //$NON-NLS-1$
		RETURN_TYPE("ReturnType"), //$NON-NLS-1$
		EXAMPLE("Example"), //$NON-NLS-1$
		PARAMETER("Parameter"), //$NON-NLS-1$
		EXCEPTION("Exception"), //$NON-NLS-1$
		STRING("String"), //$NON-NLS-1$
		BOOLEAN("Boolean"); //$NON-NLS-1$

		private static Map<String, TypeName> NAME_MAP;
		private String _name;

		static
		{
			NAME_MAP = new HashMap<String, TypeName>();

			for (TypeName type : EnumSet.allOf(TypeName.class))
			{
				NAME_MAP.put(type.getName(), type);
			}
		}

		public static TypeName get(String name)
		{
			TypeName result = UNDEFINED;

			if (NAME_MAP.containsKey(name))
			{
				result = NAME_MAP.get(name);
			}

			return result;
		}

		private TypeName(String name)
		{
			this._name = name;
		}

		public String getName()
		{
			return this._name;
		}
	}

	private static final Map<String, String> TYPE_MAP;
	private static final Pattern TYPE_DELIMITER = Pattern.compile("\\s*[,|]\\s*"); //$NON-NLS-1$
	private static final Pattern DOT_PATTERN = Pattern.compile("\\."); //$NON-NLS-1$
	private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("[$_a-zA-Z][$_a-zA-Z0-9]*"); //$NON-NLS-1$
	private static final Pattern EVENT_IDENTIFIER_PATTERN = Pattern
			.compile("[$_a-zA-Z][$_a-zA-Z0-9]*(?::[$_a-zA-Z][$_a-zA-Z0-9]*)?"); //$NON-NLS-1$
	private static final Pattern TYPE_PATTERN = Pattern
			.compile("[$_a-zA-Z][$_a-zA-Z0-9]*(?:\\.[$_a-zA-Z][$_a-zA-Z0-9]*)*(?:(?:<[$_a-zA-Z][$_a-zA-Z0-9]*>)|(?:\\[\\]))?"); //$NON-NLS-1$

	private Map<String, TypeElement> _typesByName;
	private List<AliasElement> _aliases;

	private AliasElement _currentAlias;
	private TypeElement _currentType;
	private UserAgentElement _currentUserAgent;
	private SinceElement _currentSince;
	private PropertyElement _currentProperty;
	private FunctionElement _currentFunction;
	private EventElement _currentEvent;
	private EventPropertyElement _currentEventProperty;
	private ReturnTypeElement _currentReturnType;
	private String _currentExample;
	private ParameterElement _currentParameter;
	private ExceptionElement _currentException;
	private String _currentString;
	private Boolean _currentBoolean;

	static
	{
		TYPE_MAP = new HashMap<String, String>();
		TYPE_MAP.put("array", "Array"); //$NON-NLS-1$ //$NON-NLS-2$
		TYPE_MAP.put("bool", "Boolean"); //$NON-NLS-1$ //$NON-NLS-2$
		TYPE_MAP.put("boolean", "Boolean"); //$NON-NLS-1$ //$NON-NLS-2$
		TYPE_MAP.put("date", "Date"); //$NON-NLS-1$ //$NON-NLS-2$
		TYPE_MAP.put("double", "Number"); //$NON-NLS-1$ //$NON-NLS-2$
		TYPE_MAP.put("float", "Number"); //$NON-NLS-1$ //$NON-NLS-2$
		TYPE_MAP.put("function", "Function"); //$NON-NLS-1$ //$NON-NLS-2$
		TYPE_MAP.put("int", "Number"); //$NON-NLS-1$ //$NON-NLS-2$
		TYPE_MAP.put("long", "Number"); //$NON-NLS-1$ //$NON-NLS-2$
		TYPE_MAP.put("number", "Number"); //$NON-NLS-1$ //$NON-NLS-2$
		TYPE_MAP.put("object", "Object"); //$NON-NLS-1$ //$NON-NLS-2$
		TYPE_MAP.put("string", "String"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * JSCAHandler
	 */
	public JSCAHandler()
	{
		this._typesByName = new HashMap<String, TypeElement>();
		this._aliases = new ArrayList<AliasElement>();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IContextHandler#addElement(java.lang.String, com.aptana.json.IState)
	 */
	public void addElement(String elementTypeName, IState elementType)
	{
		TypeName type = TypeName.get(elementTypeName);

		switch (type)
		{
			case ALIAS:
				this._aliases.add(this._currentAlias);
				this._currentAlias = null;
				break;

			case TYPE:
				// NOTE: Setting name on type already puts it into the typesByName hash, so we don't have
				// to do anything here
				this._currentType = null;
				break;

			case USER_AGENT:
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
				this._currentUserAgent = null;
				break;

			case SINCE:
				if (this._currentProperty != null)
				{
					this._currentProperty.addSince(this._currentSince);
				}
				else if (this._currentFunction != null)
				{
					this._currentFunction.addSince(this._currentSince);
				}
				else if (this._currentType != null)
				{
					this._currentType.addSince(this._currentSince);
				}
				this._currentSince = null;
				break;

			case PROPERTY:
				if (this._currentType != null)
				{
					this._currentType.addProperty(this._currentProperty);
				}
				this._currentProperty = null;
				break;

			case FUNCTION:
				if (this._currentType != null)
				{
					this._currentType.addProperty(this._currentFunction);
				}
				this._currentFunction = null;
				break;

			case EVENT:
				if (this._currentType != null)
				{
					this._currentType.addEvent(this._currentEvent);
				}
				this._currentEvent = null;
				break;

			case STRING:
				if (this._currentFunction != null)
				{
					this._currentFunction.addReference(this._currentString);
				}
				else if (this._currentType != null)
				{
					this._currentType.addRemark(this._currentString);
				}
				this._currentString = null;
				break;

			case EVENT_PROPERTY:
				if (this._currentEvent != null)
				{
					this._currentEvent.addProperty(this._currentEventProperty);
				}
				this._currentEventProperty = null;
				break;

			case EXAMPLE:
				if (this._currentProperty != null)
				{
					this._currentProperty.addExample(this._currentExample);
				}
				else if (this._currentFunction != null)
				{
					this._currentFunction.addExample(this._currentExample);
				}
				else if (this._currentType != null)
				{
					this._currentType.addExample(this._currentExample);
				}
				this._currentExample = null;
				break;

			case PARAMETER:
				if (this._currentFunction != null)
				{
					this._currentFunction.addParameter(this._currentParameter);
				}
				this._currentParameter = null;
				break;

			case EXCEPTION:
				if (this._currentFunction != null)
				{
					this._currentFunction.addException(this._currentException);
				}
				this._currentException = null;
				break;

			case RETURN_TYPE:
				if (this._currentFunction != null)
				{
					this._currentFunction.addReturnType(this._currentReturnType);
				}
				this._currentReturnType = null;
				break;

			default:
				// TODO: warn
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IContextHandler#createType(java.lang.String, com.aptana.json.IState, java.lang.Object)
	 */
	public void createType(String typeName, IState type, Object value)
	{
		TypeName t = TypeName.get(typeName);

		switch (t)
		{
			case ALIAS:
				this._currentAlias = new AliasElement();
				break;

			case TYPE:
				this._currentType = new TypeElement();
				break;

			case USER_AGENT:
				this._currentUserAgent = new UserAgentElement();
				break;

			case SINCE:
				this._currentSince = new SinceElement();
				break;

			case PROPERTY:
				this._currentProperty = new PropertyElement();
				break;

			case FUNCTION:
				this._currentFunction = new FunctionElement();
				break;

			case EVENT:
				this._currentEvent = new EventElement();
				break;

			case EVENT_PROPERTY:
				this._currentEventProperty = new EventPropertyElement();
				break;

			case RETURN_TYPE:
				this._currentReturnType = new ReturnTypeElement();
				break;

			case EXAMPLE:
				// TODO: create ExampleElement. Treating as string right now
				break;

			case PARAMETER:
				this._currentParameter = new ParameterElement();
				break;

			case EXCEPTION:
				this._currentException = new ExceptionElement();
				break;

			case STRING:
				this._currentString = (String) value;
				break;

			case BOOLEAN:
				this._currentBoolean = (Boolean) value;
				break;

			default:
				// TODO: warn
		}
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

	/**
	 * getMappedTypes
	 * 
	 * @param typeSpec
	 * @return
	 */
	protected List<String> getMappedTypes(String typeSpec)
	{
		List<String> result = new ArrayList<String>();

		if (typeSpec != null && typeSpec.length() > 0)
		{
			String[] types = TYPE_DELIMITER.split(typeSpec);

			for (String type : types)
			{
				if (isValidTypeIdentifier(type))
				{
					// map types
					if (TYPE_MAP.containsKey(type))
					{
						result.add(TYPE_MAP.get(this._currentString));
					}
					else
					{
						result.add(type);

						// TODO: Collect unmatched types in a set, remove built-ins, remove types in jsca file.
						// Possibly warn on remaining types as possibly missing types
					}
				}
				else
				{
					JSPlugin.logError(Messages.JSCAHandler_Invalid_Type_Name + type, null);
				}
			}
		}

		return result;
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

			// NOTE: type will be added in addElement
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
		Collection<TypeElement> types = this._typesByName.values();

		return types.toArray(new TypeElement[types.size()]);
	}

	/**
	 * isValidEventIdentifier
	 * 
	 * @param name
	 * @return
	 */
	protected boolean isValidEventIdentifier(String name)
	{
		boolean result = false;

		if (name != null)
		{
			Matcher m = EVENT_IDENTIFIER_PATTERN.matcher(name);

			result = m.matches();
		}

		return result;
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

	/**
	 * log
	 * 
	 * @param message
	 */
	protected void log(String message)
	{
		if (Platform.inDevelopmentMode())
		{
			System.out.println(message);
		}
		else
		{
			JSPlugin.logError(message, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IContextHandler#setProperty(java.lang.String, java.lang.String, com.aptana.json.IState)
	 */
	public void setProperty(String propertyName, String propertyTypeName, IState propertyType)
	{
		PropertyName p = PropertyName.get(propertyName);

		switch (p)
		{
			case VERSION:
				// NOTE: Ignoring JSMetadata properties
				if (this._currentUserAgent != null)
				{
					this._currentUserAgent.setVersion(this._currentString);
				}
				else if (this._currentSince != null)
				{
					this._currentSince.setVersion(this._currentString);
				}
				this._currentString = null;
				break;

			case NAME:
				if (this._currentSince != null)
				{
					this._currentSince.setName(this._currentString);
				}
				else if (this._currentExample != null)
				{
					// TODO: add ExampleElement to support for name+code
				}
				else if (this._currentEventProperty != null)
				{
					if (isValidIdentifier(this._currentString))
					{
						this._currentEventProperty.setName(this._currentString);
					}
					else
					{
						this.log(Messages.JSCAHandler_Invalid_Event_Property_Name + this._currentString);
					}
				}
				else if (this._currentEvent != null)
				{
					if (isValidEventIdentifier(this._currentString))
					{
						this._currentEvent.setName(this._currentString);
					}
					else
					{
						this.log(Messages.JSCAHandler_Invalid_Event_Name + this._currentString);
					}
				}
				else if (this._currentProperty != null)
				{
					if (isValidIdentifier(this._currentString))
					{
						this._currentProperty.setName(this._currentString);
					}
					else
					{
						this.log(Messages.JSCAHandler_Invalid_Property_Name + this._currentString);
					}
				}
				else if (this._currentParameter != null)
				{
					if (isValidIdentifier(this._currentString))
					{
						this._currentParameter.setName(this._currentString);
					}
					else
					{
						this.log(Messages.JSCAHandler_Invalid_Parameter_Name + this._currentString);
					}
				}
				else if (this._currentFunction != null)
				{
					if (isValidIdentifier(this._currentString))
					{
						this._currentFunction.setName(this._currentString);
					}
					else
					{
						this.log(Messages.JSCAHandler_Invalid_Function_Name + this._currentString);
					}
				}
				else if (this._currentAlias != null)
				{
					if (isValidIdentifier(this._currentString))
					{
						this._currentAlias.setName(this._currentString);
					}
					else
					{
						this.log(Messages.JSCAHandler_Invalid_Alias + this._currentString);
					}
				}
				else if (this._currentType != null)
				{
					if (isValidTypeIdentifier(this._currentString))
					{
						this.createType();
					}
					else
					{
						this.log(Messages.JSCAHandler_Invalid_Type_Name + this._currentString);
					}
				}
				else
				{
					this.log(Messages.JSCAHandler_Unable_To_Set_Name_Property);
				}

				this._currentString = null;
				break;

			case DESCRIPTION:
				if (this._currentUserAgent != null)
				{
					this._currentUserAgent.setDescription(this._currentString);
				}
				else if (this._currentException != null)
				{
					this._currentException.setDescription(this._currentString);
				}
				else if (this._currentEventProperty != null)
				{
					this._currentEventProperty.setDescription(this._currentString);
				}
				else if (this._currentEvent != null)
				{
					this._currentEvent.setDescription(this._currentString);
				}
				else if (this._currentProperty != null)
				{
					this._currentProperty.setDescription(this._currentString);
				}
				else if (this._currentParameter != null)
				{
					this._currentParameter.setDescription(this._currentString);
				}
				else if (this._currentReturnType != null)
				{
					this._currentReturnType.setDescription(this._currentString);
				}
				else if (this._currentFunction != null)
				{
					this._currentFunction.setDescription(this._currentString);
				}
				else if (this._currentAlias != null)
				{
					this._currentAlias.setDescription(this._currentString);
				}
				else if (this._currentType != null)
				{
					this._currentType.setDescription(this._currentString);
				}
				this._currentString = null;
				break;

			case TYPE:
				List<String> types = this.getMappedTypes(this._currentString);

				for (String type : types)
				{
					if (this._currentException != null)
					{
						// last wins
						this._currentException.setType(type);
					}
					if (this._currentReturnType != null)
					{
						// last wins
						this._currentReturnType.setType(type);
					}
					else if (this._currentEventProperty != null)
					{
						// last wins, but may want to support multiple types here
						this._currentEventProperty.setType(type);
					}
					else if (this._currentParameter != null)
					{
						this._currentParameter.addType(type);
					}
					else if (this._currentProperty != null)
					{
						this._currentProperty.addType(type);
					}
					else if (this._currentAlias != null)
					{
						// last wins
						this._currentAlias.setType(type);
					}
				}
				this._currentString = null;
				break;

			case DEPRECATED:
				if (this._currentType != null)
				{
					this._currentType.setIsDeprecated(this._currentBoolean);
				}
				this._currentBoolean = null;
				break;

			case INHERITS:
				if (this._currentType != null)
				{
					this._currentType.addParentType(this._currentString);
				}
				this._currentString = null;
				break;

			case PLATFORM:
				if (this._currentUserAgent != null)
				{
					this._currentUserAgent.setPlatform(this._currentString);
				}
				this._currentString = null;
				break;

			case OS:
				if (this._currentUserAgent != null)
				{
					this._currentUserAgent.setOS(this._currentString);
				}
				this._currentString = null;
				break;

			case OS_VERSION:
				if (this._currentUserAgent != null)
				{
					this._currentUserAgent.setOSVersion(this._currentString);
				}
				this._currentString = null;
				break;

			case IS_INSTANCE_PROPERTY:
				if (this._currentProperty != null)
				{
					this._currentProperty.setIsInstanceProperty(this._currentBoolean);
				}
				else if (this._currentFunction != null)
				{
					this._currentFunction.setIsInstanceProperty(this._currentBoolean);
				}
				this._currentBoolean = null;
				break;

			case IS_CLASS_PROPERTY:
				if (this._currentProperty != null)
				{
					this._currentProperty.setIsClassProperty(this._currentBoolean);
				}
				else if (this._currentFunction != null)
				{
					this._currentFunction.setIsClassProperty(this._currentBoolean);
				}
				this._currentBoolean = null;
				break;

			case IS_INTERNAL:
				if (this._currentProperty != null)
				{
					this._currentProperty.setIsInternal(this._currentBoolean);
				}
				else if (this._currentFunction != null)
				{
					this._currentFunction.setIsInternal(this._currentBoolean);
				}
				this._currentBoolean = null;
				break;

			case IS_CONSTRUCTOR:
				if (this._currentFunction != null)
				{
					this._currentFunction.setIsConstructor(this._currentBoolean);
				}
				this._currentBoolean = null;
				break;

			case IS_METHOD:
				if (this._currentFunction != null)
				{
					this._currentFunction.setIsMethod(this._currentBoolean);
				}
				this._currentBoolean = null;
				break;

			case CODE:
				this._currentExample = this._currentString;
				this._currentString = null;
				break;

			case USAGE:
				if (this._currentParameter != null)
				{
					this._currentParameter.setUsage(this._currentString);
				}
				this._currentString = null;
				break;

			case ALIASES:
			case TYPES:
			case EXAMPLES:
			case PARAMETERS:
			case REFERENCES:
			case EXCEPTIONS:
			case RETURN_TYPES:
			case USER_AGENTS:
			case SINCE:
			case PROPERTIES:
			case FUNCTIONS:
			case EVENTS:
			case REMARKS:
				// implicitly handled in addElement
				break;

			default:
				this.log(Messages.JSCAHandler_Unrecognized_Property_Name + propertyName);
				break;
		}
	}

	protected void createType()
	{
		// NOTE: It is assumed that the "name" property is always the first property on a type. This allows us to do the
		// following.

		if (this._typesByName.containsKey(this._currentString))
		{
			// Use existing type if we've already created one for the current name
			this._currentType = this._typesByName.get(this._currentString);
		}
		else
		{
			// Otherwise, use the current empty type, set it's name, and store it in the type map
			this._currentType.setName(this._currentString);
			this._typesByName.put(this._currentString, this._currentType);
		}

		String[] parts = DOT_PATTERN.split(this._currentString);

		if (parts.length > 1)
		{
			String accumulatedName = parts[0];
			TypeElement type = this.getType(accumulatedName);

			for (int i = 1; i < parts.length; i++)
			{
				// grab name part
				String pName = parts[i];

				// update accumulated type name
				accumulatedName += "." + pName; //$NON-NLS-1$

				// try to grab the property off of the current type
				PropertyElement property = type.getProperty(pName);

				// create property, if we didn't have one
				if (property == null)
				{
					property = new PropertyElement();

					property.setName(pName);
					property.setIsClassProperty(true);
					property.addType(accumulatedName);

					type.addProperty(property);
				}

				// make sure to save last type we visited
				this._typesByName.put(type.getName(), type);

				type = this.getType(accumulatedName);
			}
		}
	}
}
