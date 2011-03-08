/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.index;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.aptana.json.SchemaObject;

/**
 * JSCAHandler
 */
public class JSCAHandler implements IContextHandler
{
	private static enum PropertyName
	{
		UNDEFINED(""),
		VERSION("version"),
		ALIASES("aliases"),
		TYPES("types"),
		NAME("name"),
		DESCRIPTION("description"),
		TYPE("type"),
		DEPRECATED("deprecated"),
		USER_AGENTS("userAgents"),
		SINCE("since"),
		INHERITS("inherits"),
		PROPERTIES("properties"),
		FUNCTIONS("functions"),
		EVENTS("events"),
		REMARKS("remarks"),
		PLATFORM("platform"),
		OS("os"),
		OS_VERSION("osVersion"),
		IS_INSTANCE_PROPERTY("isInstanceProperty"),
		IS_CLASS_PROPERTY("isClassProperty"),
		IS_INTERNAL("isInternal"),
		EXAMPLES("examples"),
		PARAMETERS("parameters"),
		REFERENCES("references"),
		EXCEPTIONS("exceptions"),
		RETURN_TYPES("returnTypes"),
		IS_CONSTRUCTOR("isConstructor"),
		IS_METHOD("isMethod"),
		CODE("code"),
		USAGE("usage");

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

	private List<TypeElement> _types;
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
		TYPE_MAP.put("array", "Array");
		TYPE_MAP.put("bool", "Boolean");
		TYPE_MAP.put("boolean", "Boolean");
		TYPE_MAP.put("date", "Date");
		TYPE_MAP.put("double", "Number");
		TYPE_MAP.put("float", "Number");
		TYPE_MAP.put("function", "Function");
		TYPE_MAP.put("int", "Number");
		TYPE_MAP.put("long", "Number");
		TYPE_MAP.put("number", "Number");
		TYPE_MAP.put("object", "Object");
		TYPE_MAP.put("string", "String");
	}

	/**
	 * JSCAHandler
	 */
	public JSCAHandler()
	{
		this._types = new ArrayList<TypeElement>();
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
				this._types.add(this._currentType);
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
	 * getMappedType
	 * 
	 * @param type
	 * @return
	 */
	protected String getMappedType(String type)
	{
		String result = type;

		// map types
		if (TYPE_MAP.containsKey(type))
		{
			result = TYPE_MAP.get(this._currentString);
		}
		else
		{
			System.out.println(type);
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
		return this._types.toArray(new TypeElement[this._types.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IContextHandler#setProperty(java.lang.String, java.lang.String, com.aptana.json.IState)
	 */
	public void setProperty(SchemaObject owningType, String propertyName, String propertyTypeName, IState propertyType)
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
				// TODO: add ExampleElement to support for name+code
				if (this._currentSince != null)
				{
					this._currentSince.setName(this._currentString);
				}
				else if (this._currentEventProperty != null)
				{
					this._currentEventProperty.setName(this._currentString);
				}
				else if (this._currentEvent != null)
				{
					this._currentEvent.setName(this._currentString);
				}
				else if (this._currentProperty != null)
				{
					this._currentProperty.setName(this._currentString);
				}
				else if (this._currentParameter != null)
				{
					this._currentParameter.setName(this._currentString);
				}
				else if (this._currentFunction != null)
				{
					this._currentFunction.setName(this._currentString);
				}
				else if (this._currentAlias != null)
				{
					this._currentAlias.setName(this._currentString);
				}
				else if (this._currentType != null)
				{
					this._currentType.setName(this._currentString);
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
				this._currentString = this.getMappedType(this._currentString);

				if (this._currentException != null)
				{
					this._currentException.setType(this._currentString);
				}
				if (this._currentReturnType != null)
				{
					this._currentReturnType.setType(this._currentString);
				}
				else if (this._currentEventProperty != null)
				{
					this._currentEventProperty.setType(this._currentString);
				}
				else if (this._currentParameter != null)
				{
					this._currentParameter.addType(this._currentString);
				}
				else if (this._currentProperty != null)
				{
					this._currentProperty.addType(this._currentString);
				}
				else if (this._currentAlias != null)
				{
					this._currentAlias.setType(this._currentString);
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
				System.out.println("unrecognized property: " + propertyName);
				// warn
		}
	}
}
