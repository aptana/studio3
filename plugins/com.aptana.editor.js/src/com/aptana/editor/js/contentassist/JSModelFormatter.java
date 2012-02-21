/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import com.aptana.core.IMap;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.core.util.URIUtil;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.ParameterElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.SinceElement;
import com.aptana.editor.js.contentassist.model.UserAgentElement;

public class JSModelFormatter
{
	private static final String BOLD_CLOSE_TAG = "</b>"; //$NON-NLS-1$
	private static final String BOLD_OPEN_TAG = "<b>"; //$NON-NLS-1$
	private static final String COLON_SPACE = ": "; //$NON-NLS-1$
	private static final String COMMA_SPACE = ", "; //$NON-NLS-1$

	/**
	 * This is intentionally NOT <br />
	 * because the Additional Info popup doesn't handle that properly right now.
	 */
	private static final String HTML_NEWLINE = "<br>"; //$NON-NLS-1$

	private static final Map<String, Image> TYPE_IMAGE_MAP;
	// used for mixed types
	private static final Image PROPERTY = JSPlugin.getImage("/icons/js_property.png"); //$NON-NLS-1$
	private static final String PROTOTYPE_PROPERTY = "." + JSTypeConstants.PROTOTYPE_PROPERTY; //$NON-NLS-1$

	/**
	 * static initializer
	 */
	static
	{
		TYPE_IMAGE_MAP = new HashMap<String, Image>();
		TYPE_IMAGE_MAP.put(JSTypeConstants.ARRAY_TYPE, JSPlugin.getImage("/icons/array-literal.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.BOOLEAN_TYPE, JSPlugin.getImage("/icons/boolean.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.FUNCTION_TYPE, JSPlugin.getImage("/icons/js_function.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.NULL_TYPE, JSPlugin.getImage("/icons/null.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.NUMBER_TYPE, JSPlugin.getImage("/icons/number.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.OBJECT_TYPE, JSPlugin.getImage("/icons/object-literal.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.REG_EXP_TYPE, JSPlugin.getImage("/icons/regex.png")); //$NON-NLS-1$
		TYPE_IMAGE_MAP.put(JSTypeConstants.STRING_TYPE, JSPlugin.getImage("/icons/string.png")); //$NON-NLS-1$
	}

	// ----------- The available formats to use.

	/**
	 * Used by UI label providers.
	 */
	public static final JSModelFormatter LABEL = new JSModelFormatter(false, Section.SIGNATURE);

	/**
	 * For "additional info" popup from highlighted CA item.
	 */
	public static final JSModelFormatter ADDITIONAL_INFO = new JSModelFormatter(true, Section.SIGNATURE,
			Section.DESCRIPTION, Section.PLATFORMS);
	/**
	 * For text hovers, focused additional info popup.
	 */
	public static final JSModelFormatter TEXT_HOVER = new JSModelFormatter(true, Section.SIGNATURE, Section.LOCATIONS,
			Section.DESCRIPTION, Section.PLATFORMS, Section.REMARKS, Section.EXAMPLE, Section.SPECIFICATIONS);

	/**
	 * For dynamic help
	 */
	public static final JSModelFormatter DYNAMIC_HELP = new JSModelFormatter(true); // TODO As part of APSTUD-4189

	/**
	 * For context info popup.
	 */
	public static final JSModelFormatter CONTEXT_INFO = new JSModelFormatter(false, Section.SIGNATURE)
	{
		private static final char BULLET = '\u2022';

		public String getDocumentation(Collection<PropertyElement> properties)
		{
			if (CollectionsUtil.isEmpty(properties))
			{
				return null;
			}
			PropertyElement prop = properties.iterator().next();

			if (prop instanceof FunctionElement)
			{
				FunctionElement function = (FunctionElement) prop;
				List<String> result = new ArrayList<String>();
				StringBuilder buffer = new StringBuilder();

				// line 1: function name with argument names
				result.add(getHeader(function, null));

				// line 2..n: one line for each argument description
				for (ParameterElement parameter : function.getParameters())
				{
					String description = parameter.getDescription();

					buffer.setLength(0);
					buffer.append(' ').append(BULLET).append('\t').append(parameter.getName());

					if (!StringUtil.isEmpty(description))
					{
						buffer.append(':').append(FileUtil.NEW_LINE).append(" \t").append(description); //$NON-NLS-1$
					}

					result.add(buffer.toString());
				}
				return StringUtil.join(FileUtil.NEW_LINE + JSContextInformation.DESCRIPTION_DELIMITER, result);
			}
			return null;
		}
	};

	/**
	 * This list of sections to display in our output.
	 */
	private List<Section> fSections;

	/**
	 * Use HTML tags in the output?
	 */
	private boolean useHTML;

	enum Section
	{
		SIGNATURE, LOCATIONS, EXAMPLE, SPECIFICATIONS, DESCRIPTION, PLATFORMS, REMARKS, REFERENCES
	}

	private JSModelFormatter(boolean useHTML, Section... sectionsToDisplay)
	{
		this.useHTML = useHTML;
		this.fSections = Arrays.asList(sectionsToDisplay);
	}

	/**
	 * getDescription - Returns header, newline, documentation
	 * 
	 * @param property
	 * @param projectURI
	 * @return
	 */
	public String getDescription(PropertyElement property, URI projectURI)
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append(getHeader(property, projectURI));
		String docs = getDocumentation(property);
		if (!StringUtil.isEmpty(docs))
		{
			buffer.append(newline());
			buffer.append(docs);
		}
		return buffer.toString();
	}

	/**
	 * Returns just the header, typically the signature plus optionally the locations
	 * 
	 * @param property
	 * @param root
	 * @return
	 */
	public String getHeader(PropertyElement property, URI root)
	{
		return getHeader(CollectionsUtil.newList(property), root);
	}

	/**
	 * Returns just the header, typically the signature plus optionally the locations
	 * 
	 * @param properties
	 * @param root
	 * @return
	 */
	public String getHeader(List<PropertyElement> properties, URI root)
	{
		if (CollectionsUtil.isEmpty(properties))
		{
			return StringUtil.EMPTY;
		}

		List<String> stringParts = new ArrayList<String>();
		PropertyElement first = properties.get(0);
		if (useHTML)
		{
			stringParts.add(BOLD_OPEN_TAG); //$NON-NLS-1$
		}
		if (fSections.contains(Section.SIGNATURE))
		{
			stringParts.add(formatSignature(first));
		}
		if (fSections.contains(Section.LOCATIONS))
		{
			Set<String> documents = new HashSet<String>();
			for (PropertyElement pe : properties)
			{
				documents.addAll(pe.getDocuments());
			}
			String locations = formatDefiningFiles(documents, root);
			if (!StringUtil.isEmpty(locations))
			{
				stringParts.add(" - "); //$NON-NLS-1$
				stringParts.add(locations);
			}
		}
		if (useHTML)
		{
			stringParts.add(BOLD_CLOSE_TAG); //$NON-NLS-1$
		}
		return StringUtil.concat(stringParts);
	}

	/**
	 * Returns just the documentation body.
	 * 
	 * @param property
	 * @return
	 */
	public String getDocumentation(PropertyElement property)
	{
		return getDocumentation(CollectionsUtil.newList(property));
	}

	/**
	 * Returns just the documentation body.
	 * 
	 * @param properties
	 * @return
	 */
	public String getDocumentation(Collection<PropertyElement> properties)
	{
		Set<UserAgentElement> userAgents = new HashSet<UserAgentElement>();
		Set<SinceElement> sinceElements = new HashSet<SinceElement>();
		Set<String> descriptions = new HashSet<String>();
		String example = StringUtil.EMPTY;

		for (PropertyElement property : properties)
		{
			userAgents.addAll(property.getUserAgents());
			String desc = property.getDescription();
			if (!StringUtil.isEmpty(desc))
			{
				descriptions.add(desc);
			}
			sinceElements.addAll(property.getSinceList());
			if (StringUtil.isEmpty(example) && !CollectionsUtil.isEmpty(property.getExamples()))
			{
				example = property.getExamples().get(0);
			}
		}

		List<String> builder = new ArrayList<String>();
		if (fSections.contains(Section.DESCRIPTION))
		{
			String description = Messages.JSTextHover_NoDescription;
			if (!CollectionsUtil.isEmpty(descriptions))
			{
				description = StringUtil.join(COMMA_SPACE, descriptions);
			}
			builder.add(description);
		}
		if (fSections.contains(Section.PLATFORMS))
		{
			builder.add(addSection(Messages.JSTextHover_SupportedPlatforms, getPlatforms(userAgents)));
		}
		if (fSections.contains(Section.EXAMPLE))
		{
			builder.add(addSection(Messages.JSTextHover_Example, example));
		}
		if (fSections.contains(Section.SPECIFICATIONS))
		{
			builder.add(addSection(Messages.JSTextHover_Specification, getSpecificationsString(sinceElements)));
		}
		return StringUtil.concat(builder);
	}

	/**
	 * Formats the signature in the header: name, params, type.
	 * 
	 * @param prop
	 * @return
	 */
	private String formatSignature(PropertyElement prop)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(prop.getName());
		List<String> typeNames = prop.getTypeNames();
		if (prop instanceof FunctionElement)
		{
			FunctionElement fe = (FunctionElement) prop;
			builder.append('(');
			builder.append(formatParameters(fe.getParameters()));
			builder.append(')');
			typeNames = fe.getReturnTypeNames();
		}
		builder.append(COLON_SPACE);
		builder.append(formatTypes(typeNames));
		return builder.toString();
	}

	private String formatTypes(List<String> typeNames)
	{
		if (CollectionsUtil.isEmpty(typeNames))
		{
			return JSTypeConstants.NO_TYPE;
		}

		List<String> typeDisplayNames = CollectionsUtil.map(typeNames, new IMap<String, String>()
		{
			public String map(String type)
			{
				return getTypeDisplayName(type);
			}
		});

		return StringUtil.join(COMMA_SPACE, typeDisplayNames);
	}

	/**
	 * Formats {@link FunctionElement} parameters.
	 * 
	 * @param parameters
	 * @return
	 */
	private String formatParameters(List<ParameterElement> parameters)
	{
		List<String> strings = CollectionsUtil.map(parameters, new IMap<ParameterElement, String>()
		{
			public String map(ParameterElement item)
			{
				StringBuilder b = new StringBuilder();
				b.append(item.getName());
				List<String> types = item.getTypes();
				if (!CollectionsUtil.isEmpty(types))
				{
					b.append(COLON_SPACE).append(getTypeDisplayName(types.get(0)));
				}
				return b.toString();
			}
		});
		return StringUtil.join(COMMA_SPACE, strings);
	}

	/**
	 * formatDefiningFiles
	 * 
	 * @param property
	 * @param projectURI
	 */
	private String formatDefiningFiles(Collection<String> documents, final URI projectURI)
	{
		if (projectURI != null)
		{
			documents = CollectionsUtil.map(documents, new IMap<String, String>()
			{
				public String map(String item)
				{
					try
					{
						return projectURI.relativize(new URI(item)).getPath();
					}
					catch (URISyntaxException e)
					{
						return item;
					}
				}
			});
		}

		return StringUtil.join(COMMA_SPACE, documents);
	}

	private String getPlatforms(Collection<UserAgentElement> userAgents)
	{
		List<String> strings = CollectionsUtil.map(userAgents, new IMap<UserAgentElement, String>()
		{
			public String map(UserAgentElement item)
			{
				StringBuilder b = new StringBuilder();
				b.append(item.getPlatform());
				String version = item.getVersion();
				if (!StringUtil.isEmpty(version))
				{
					b.append(COLON_SPACE).append(version);
				}
				return b.toString();
			}
		});
		return StringUtil.join(COMMA_SPACE, strings);
	}

	private String getSpecificationsString(Collection<SinceElement> sinceElements)
	{
		List<String> strings = CollectionsUtil.map(sinceElements, new IMap<SinceElement, String>()
		{
			public String map(SinceElement item)
			{
				StringBuilder b = new StringBuilder();
				b.append(item.getName());
				String version = item.getVersion();
				if (!StringUtil.isEmpty(version))
				{
					b.append(COLON_SPACE).append(version);
				}
				return b.toString();
			}
		});
		return StringUtil.join(COMMA_SPACE, strings);
	}

	private String addSection(String title, String value)
	{
		StringBuilder builder = new StringBuilder();
		if (!StringUtil.isEmpty(value))
		{
			builder.append(newline()).append(newline());
			if (useHTML)
			{
				builder.append(BOLD_OPEN_TAG);
			}
			builder.append(title);
			if (useHTML)
			{
				builder.append(BOLD_CLOSE_TAG);
			}
			builder.append(newline());
			builder.append(value.trim());
		}
		return builder.toString();
	}

	private String newline()
	{
		return useHTML ? HTML_NEWLINE : FileUtil.NEW_LINE;
	}

	/**
	 * getDocumentDisplayName
	 * 
	 * @param document
	 * @return
	 */
	public String getDocumentDisplayName(String document)
	{
		String result = null;

		if (document != null)
		{
			int index = document.lastIndexOf('/');

			if (index != -1)
			{
				result = document.substring(index + 1);
			}
			else
			{
				result = document;
			}

			result = URIUtil.decodeURI(result);
		}

		return result;
	}

	/**
	 * getImage
	 * 
	 * @param property
	 * @return
	 */
	public Image getImage(PropertyElement property)
	{
		Image result = (property instanceof FunctionElement) ? TYPE_IMAGE_MAP.get(JSTypeConstants.FUNCTION_TYPE)
				: PROPERTY;

		if (property != null)
		{
			List<String> types = property.getTypeNames();

			if (types != null && types.size() == 1)
			{
				String type = types.get(0);

				if (TYPE_IMAGE_MAP.containsKey(type))
				{
					result = TYPE_IMAGE_MAP.get(type);
				}
				else if (type.startsWith(JSTypeConstants.DYNAMIC_CLASS_PREFIX))
				{
					result = TYPE_IMAGE_MAP.get(JSTypeConstants.OBJECT_TYPE);
				}
				else if (type.startsWith(JSTypeConstants.FUNCTION_TYPE))
				{
					result = TYPE_IMAGE_MAP.get(JSTypeConstants.FUNCTION_TYPE);
				}
				else if (type.endsWith(JSTypeConstants.ARRAY_LITERAL))
				{
					result = TYPE_IMAGE_MAP.get(JSTypeConstants.ARRAY_TYPE);
				}
			}
		}

		return result;
	}

	/**
	 * getDisplayTypeName
	 * 
	 * @param type
	 * @return
	 */
	public String getTypeDisplayName(String type)
	{
		String result = null;

		if (type != null)
		{
			if (type.startsWith(JSTypeConstants.GENERIC_CLASS_OPEN) && type.endsWith(JSTypeConstants.GENERIC_CLOSE))
			{
				result = type.substring(JSTypeConstants.GENERIC_CLASS_OPEN.length(), type.length() - 1);
			}
			else if (type.startsWith(JSTypeConstants.DYNAMIC_CLASS_PREFIX))
			{
				result = JSTypeConstants.USER_TYPE;
			}
			else if (type.startsWith(JSTypeConstants.GENERIC_FUNCTION_OPEN)
					&& type.endsWith(JSTypeConstants.GENERIC_CLOSE))
			{
				result = type.substring(JSTypeConstants.GENERIC_FUNCTION_OPEN.length(), type.length() - 1);
			}
			else if (type.endsWith(PROTOTYPE_PROPERTY))
			{
				result = type.substring(0, type.length() - PROTOTYPE_PROPERTY.length());
			}
			else
			{
				result = type;
			}
		}

		return result;
	}

}
