/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.JSTypeConstants;
import com.aptana.js.core.model.AliasElement;
import com.aptana.js.core.model.EventElement;
import com.aptana.js.core.model.EventPropertyElement;
import com.aptana.js.core.model.ExceptionElement;
import com.aptana.js.core.model.FunctionElement;
import com.aptana.js.core.model.ParameterElement;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.ReturnTypeElement;
import com.aptana.js.core.model.SinceElement;
import com.aptana.js.core.model.TypeElement;
import com.aptana.js.core.model.UserAgentElement;
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
		private String name;

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
			this.name = name;
		}

		public String getName()
		{
			return name;
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
		private String name;

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
			this.name = name;
		}

		public String getName()
		{
			return name;
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

	private Map<String, TypeElement> typesByName;
	private List<AliasElement> aliases;

	private AliasElement currentAlias;
	private TypeElement currentType;
	private UserAgentElement currentUserAgent;
	private SinceElement currentSince;
	private PropertyElement currentProperty;
	private FunctionElement currentFunction;
	private EventElement currentEvent;
	private EventPropertyElement currentEventProperty;
	private ReturnTypeElement currentReturnType;
	private String currentExample;
	private ParameterElement currentParameter;
	private ExceptionElement currentException;
	private String currentString;
	private Boolean currentBoolean;

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
		typesByName = new HashMap<String, TypeElement>();
		aliases = new ArrayList<AliasElement>();
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
				aliases.add(currentAlias);
				currentAlias = null;
				break;

			case TYPE:
				// grab namespace
				String typeName = currentType.getName();
				String namespace = getNamespace(typeName);

				// hide property
				setIsInternal(typeName, currentType.isInternal());

				// potentially hide all segments up to this one
				hideNamespace(namespace);

				// transfer user agents
				TypeElement namespaceType = getType(namespace);

				if (namespaceType != null)
				{
					String propertyName = typeName.substring(namespace.length() + 1);
					PropertyElement property = namespaceType.getProperty(propertyName);

					if (property != null)
					{
						List<UserAgentElement> userAgents = currentType.getUserAgents();

						if (!CollectionsUtil.isEmpty(userAgents))
						{
							for (UserAgentElement userAgent : userAgents)
							{
								property.addUserAgent(userAgent);
							}
						}
					}
				}

				// NOTE: Setting name on type already puts it into the typesByName hash, so we don't have
				// to do anything else here
				currentType = null;
				break;

			case USER_AGENT:
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
				currentUserAgent = null;
				break;

			case SINCE:
				if (currentProperty != null)
				{
					currentProperty.addSince(currentSince);
				}
				else if (currentFunction != null)
				{
					currentFunction.addSince(currentSince);
				}
				else if (currentType != null)
				{
					currentType.addSince(currentSince);
				}
				currentSince = null;
				break;

			case PROPERTY:
				if (currentType != null)
				{
					currentType.addProperty(currentProperty);
				}
				currentProperty = null;
				break;

			case FUNCTION:
				if (currentType != null)
				{
					currentType.addProperty(currentFunction);
				}
				currentFunction = null;
				break;

			case EVENT:
				if (currentType != null)
				{
					currentType.addEvent(currentEvent);
				}
				currentEvent = null;
				break;

			case STRING:
				if (currentFunction != null)
				{
					currentFunction.addReference(currentString);
				}
				else if (currentType != null)
				{
					currentType.addRemark(currentString);
				}
				currentString = null;
				break;

			case EVENT_PROPERTY:
				if (currentEvent != null)
				{
					currentEvent.addProperty(currentEventProperty);
				}
				currentEventProperty = null;
				break;

			case EXAMPLE:
				if (currentProperty != null)
				{
					currentProperty.addExample(currentExample);
				}
				else if (currentFunction != null)
				{
					currentFunction.addExample(currentExample);
				}
				else if (currentType != null)
				{
					currentType.addExample(currentExample);
				}
				currentExample = null;
				break;

			case PARAMETER:
				if (currentFunction != null)
				{
					currentFunction.addParameter(currentParameter);
				}
				currentParameter = null;
				break;

			case EXCEPTION:
				if (currentFunction != null)
				{
					currentFunction.addException(currentException);
				}
				currentException = null;
				break;

			case RETURN_TYPE:
				if (currentFunction != null)
				{
					currentFunction.addReturnType(currentReturnType);
				}
				currentReturnType = null;
				break;

			default:
				log("Unrecognized element type name in JSCAHandler#addElement: " + elementTypeName); //$NON-NLS-1$
		}
	}

	protected void createType()
	{
		// NOTE: It is assumed that the "name" property is always the first property on a type. This allows us to do the
		// following.

		if (typesByName.containsKey(currentString))
		{
			// Use existing type if we've already created one for the current name
			currentType = typesByName.get(currentString);
		}
		else
		{
			// Otherwise, use the current empty type, set its name, and store it in the type map
			currentType.setName(currentString);
			typesByName.put(currentString, currentType);
		}

		String[] parts = DOT_PATTERN.split(currentString);

		if (parts.length > 1)
		{
			String accumulatedName = parts[0];
			TypeElement type = getType(accumulatedName);

			for (int i = 1; i < parts.length; i++)
			{
				// grab name part
				String pName = parts[i];

				// update accumulated type name
				accumulatedName += "." + pName; //$NON-NLS-1$ // $codepro.audit.disable stringConcatenationInLoop

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

				// make sure to save last visited type
				typesByName.put(type.getName(), type);

				type = getType(accumulatedName);
			}
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
				currentAlias = new AliasElement();
				break;

			case TYPE:
				currentType = new TypeElement();
				break;

			case USER_AGENT:
				currentUserAgent = new UserAgentElement();
				break;

			case SINCE:
				currentSince = new SinceElement();
				break;

			case PROPERTY:
				currentProperty = new PropertyElement();
				break;

			case FUNCTION:
				currentFunction = new FunctionElement();
				break;

			case EVENT:
				currentEvent = new EventElement();
				break;

			case EVENT_PROPERTY:
				currentEventProperty = new EventPropertyElement();
				break;

			case RETURN_TYPE:
				currentReturnType = new ReturnTypeElement();
				break;

			case EXAMPLE:
				// TODO: create ExampleElement. Treating as string right now
				break;

			case PARAMETER:
				currentParameter = new ParameterElement();
				break;

			case EXCEPTION:
				currentException = new ExceptionElement();
				break;

			case STRING:
				currentString = (String) value;
				break;

			case BOOLEAN:
				currentBoolean = (Boolean) value;
				break;

			case JSMETADATA:
				// no-op, occurs at beginning of JSCA file only
				break;

			default:
				if (typeName != null && !typeName.startsWith(JSTypeConstants.GENERIC_ARRAY_OPEN))
				{
					log("Unrecognized type name in JSCAHandler#createType: " + typeName); //$NON-NLS-1$
				}
		}
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
						result.add(TYPE_MAP.get(currentString));
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
					log("Invalid type name: " + type); //$NON-NLS-1$
				}
			}
		}

		return result;
	}

	/**
	 * getNamespace
	 * 
	 * @param typeName
	 * @return
	 */
	private String getNamespace(String typeName)
	{
		int index = typeName.lastIndexOf('.');

		return (index != -1) ? typeName.substring(0, index) : StringUtil.EMPTY;
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
		Collection<TypeElement> types = typesByName.values();

		return types.toArray(new TypeElement[types.size()]);
	}

	/**
	 * Possibly hide/show the specified namespace by visiting its properties. If all properties are internal, then the
	 * namespace will become internal. Note that this method both hides and shows namespaces and all parent namespaces
	 * are visited as well
	 * 
	 * @param namespace
	 *            The namespace to process
	 */
	protected void hideNamespace(String namespace)
	{
		while (!StringUtil.isEmpty(namespace))
		{
			TypeElement type = typesByName.get(namespace);

			if (type != null)
			{
				boolean isInternal = true;

				for (PropertyElement property : type.getProperties())
				{
					if (!property.isInternal())
					{
						isInternal = false;
						break;
					}
				}

				setIsInternal(namespace, isInternal);
			}
			else
			{
				log("Unrecognized namespace in JSCAHandler#hideNamespace: " + namespace); //$NON-NLS-1$
			}

			// move back one more segment
			namespace = getNamespace(namespace);
		}
	}

	/**
	 * Set the isInternal flag for the specified type. If the type name includes a namespace, then property for that
	 * type on the namespace will have its flag set. Otherwise, the type itself will have its flag set
	 * 
	 * @param typeName
	 *            The name of the type to process
	 * @param isInternal
	 *            The value to use when setting the type's isInternal flag
	 */
	protected void setIsInternal(String typeName, boolean isInternal)
	{
		String namespace = getNamespace(typeName);

		if (!StringUtil.isEmpty(namespace))
		{
			TypeElement namespaceType = typesByName.get(namespace);

			if (namespaceType != null)
			{
				String name = typeName.substring(namespace.length() + 1);

				// get property for type name
				PropertyElement property = namespaceType.getProperty(name);

				if (property != null)
				{
					// tag property as internal
					property.setIsInternal(isInternal);
				}
			}
		}
		else
		{
			TypeElement type = typesByName.get(typeName);

			type.setIsInternal(isInternal);
		}
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
			System.out.println(message); // $codepro.audit.disable debuggingCode
		}
		else
		{
			IdeLog.logError(JSCorePlugin.getDefault(), message);
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
				if (currentUserAgent != null)
				{
					currentUserAgent.setVersion(currentString);
				}
				else if (currentSince != null)
				{
					currentSince.setVersion(currentString);
				}
				currentString = null;
				break;

			case NAME:
				if (currentSince != null)
				{
					currentSince.setName(currentString);
				}
				else if (currentExample != null)
				{ // $codepro.audit.disable emptyIfStatement
					// TODO: add ExampleElement to support for name+code
				}
				else if (currentEventProperty != null)
				{
					if (isValidIdentifier(currentString))
					{
						currentEventProperty.setName(currentString);
					}
					else
					{
						log("Invalid event property name: " + currentString); //$NON-NLS-1$
					}
				}
				else if (currentEvent != null)
				{
					if (isValidEventIdentifier(currentString))
					{
						currentEvent.setName(currentString);
					}
					else
					{
						log("Invalid event name: " + currentString); //$NON-NLS-1$
					}
				}
				else if (currentProperty != null)
				{
					if (isValidIdentifier(currentString))
					{
						currentProperty.setName(currentString);
					}
					else
					{
						log("Invalid property name: " + currentString); //$NON-NLS-1$
					}
				}
				else if (currentParameter != null)
				{
					if (isValidIdentifier(currentString))
					{
						currentParameter.setName(currentString);
					}
					else
					{
						log("Invalid parameter name: " + currentString); //$NON-NLS-1$
					}
				}
				else if (currentFunction != null)
				{
					if (isValidIdentifier(currentString))
					{
						currentFunction.setName(currentString);
					}
					else
					{
						log("Invalid function name: " + currentString); //$NON-NLS-1$
					}
				}
				else if (currentAlias != null)
				{
					if (isValidIdentifier(currentString))
					{
						currentAlias.setName(currentString);
					}
					else
					{
						log("Invalid alias: " + currentString); //$NON-NLS-1$
					}
				}
				else if (currentType != null)
				{
					if (isValidTypeIdentifier(currentString))
					{
						createType();
					}
					else
					{
						log("Invalid type name: " + currentString); //$NON-NLS-1$
					}
				}
				else
				{
					log("Unable to set a name property"); //$NON-NLS-1$
				}

				currentString = null;
				break;

			case DESCRIPTION:
				if (currentUserAgent != null)
				{
					currentUserAgent.setDescription(currentString);
				}
				else if (currentException != null)
				{
					currentException.setDescription(currentString);
				}
				else if (currentEventProperty != null)
				{
					currentEventProperty.setDescription(currentString);
				}
				else if (currentEvent != null)
				{
					currentEvent.setDescription(currentString);
				}
				else if (currentProperty != null)
				{
					currentProperty.setDescription(currentString);
				}
				else if (currentParameter != null)
				{
					currentParameter.setDescription(currentString);
				}
				else if (currentReturnType != null)
				{
					currentReturnType.setDescription(currentString);
				}
				else if (currentFunction != null)
				{
					currentFunction.setDescription(currentString);
				}
				else if (currentAlias != null)
				{
					currentAlias.setDescription(currentString);
				}
				else if (currentType != null)
				{
					currentType.setDescription(currentString);
				}
				currentString = null;
				break;

			case TYPE:
				List<String> types = getMappedTypes(currentString);

				for (String type : types)
				{
					if (currentException != null)
					{
						// last wins
						currentException.setType(type);
					}
					if (currentReturnType != null)
					{
						// last wins
						currentReturnType.setType(type);
					}
					else if (currentEventProperty != null)
					{
						// last wins, but may want to support multiple types here
						currentEventProperty.setType(type);
					}
					else if (currentParameter != null)
					{
						currentParameter.addType(type);
					}
					else if (currentProperty != null)
					{
						currentProperty.addType(type);
					}
					else if (currentAlias != null)
					{
						// last wins
						currentAlias.setType(type);
					}
				}
				currentString = null;
				break;

			case DEPRECATED:
				if (currentEventProperty != null)
				{
					currentEventProperty.setIsDeprecated(currentBoolean);
				}
				else if (currentEvent != null)
				{
					currentEvent.setIsDeprecated(currentBoolean);
				}
				else if (currentProperty != null)
				{
					currentProperty.setIsDeprecated(currentBoolean);
				}
				else if (currentFunction != null)
				{
					currentFunction.setIsDeprecated(currentBoolean);
				}
				else if (currentType != null)
				{
					currentType.setIsDeprecated(currentBoolean);
				}
				currentBoolean = null;
				break;

			case INHERITS:
				if (currentType != null)
				{
					currentType.addParentType(currentString);
				}
				currentString = null;
				break;

			case PLATFORM:
				if (currentUserAgent != null)
				{
					currentUserAgent.setPlatform(currentString);
				}
				currentString = null;
				break;

			case OS:
				if (currentUserAgent != null)
				{
					currentUserAgent.setOS(currentString);
				}
				currentString = null;
				break;

			case OS_VERSION:
				if (currentUserAgent != null)
				{
					currentUserAgent.setOSVersion(currentString);
				}
				currentString = null;
				break;

			case IS_INSTANCE_PROPERTY:
				if (currentProperty != null)
				{
					currentProperty.setIsInstanceProperty(currentBoolean);
				}
				else if (currentFunction != null)
				{
					currentFunction.setIsInstanceProperty(currentBoolean);
				}
				currentBoolean = null;
				break;

			case IS_CLASS_PROPERTY:
				if (currentProperty != null)
				{
					currentProperty.setIsClassProperty(currentBoolean);
				}
				else if (currentFunction != null)
				{
					currentFunction.setIsClassProperty(currentBoolean);
				}
				currentBoolean = null;
				break;

			case IS_INTERNAL:
				if (currentProperty != null)
				{
					currentProperty.setIsInternal(currentBoolean);
				}
				else if (currentFunction != null)
				{
					currentFunction.setIsInternal(currentBoolean);
				}
				else if (currentType != null)
				{
					currentType.setIsInternal(currentBoolean);
				}
				currentBoolean = null;
				break;

			case IS_CONSTRUCTOR:
				if (currentFunction != null)
				{
					currentFunction.setIsConstructor(currentBoolean);
				}
				currentBoolean = null;
				break;

			case IS_METHOD:
				if (currentFunction != null)
				{
					currentFunction.setIsMethod(currentBoolean);
				}
				currentBoolean = null;
				break;

			case CODE:
				currentExample = currentString;
				currentString = null;
				break;

			case USAGE:
				if (currentParameter != null)
				{
					currentParameter.setUsage(currentString);
				}
				currentString = null;
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
				log("Unrecognized property name: " + propertyName); //$NON-NLS-1$
				break;
		}
	}
}
