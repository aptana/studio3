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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;

import com.aptana.core.IFilter;
import com.aptana.core.IMap;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.hover.TagStripperAndTypeBolder;
import com.aptana.editor.js.JSPlugin;
import com.aptana.js.core.JSTypeConstants;
import com.aptana.js.core.model.BaseElement;
import com.aptana.js.core.model.FunctionElement;
import com.aptana.js.core.model.ParameterElement;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.SinceElement;
import com.aptana.js.core.model.TypeElement;
import com.aptana.js.core.model.UserAgentElement;

public class JSModelFormatter
{

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
	private static final ImageDescriptor STATIC_OVERLAY = JSPlugin.getImageDescriptor("icons/overlays/static.png"); //$NON-NLS-1$
	private static final ImageDescriptor DEPRECATED_OVERLAY = JSPlugin
			.getImageDescriptor("icons/overlays/deprecated.gif"); //$NON-NLS-1$

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
			Section.DESCRIPTION, Section.PLATFORMS, Section.EXAMPLE, Section.SPECIFICATIONS);

	/**
	 * For dynamic help
	 */
	public static final JSModelFormatter DYNAMIC_HELP = new JSModelFormatter(true, Section.DESCRIPTION,
			Section.PARAMETERS, Section.RETURNS, Section.EXAMPLES, Section.PLATFORMS, Section.SPECIFICATIONS);

	/**
	 * For context info popup.
	 */
	public static final JSModelFormatter CONTEXT_INFO = new JSModelFormatter(false, Section.SIGNATURE)
	{
		private static final String BULLET = "\u2022"; //$NON-NLS-1$
		private TagStripperAndTypeBolder stripAndBold = new TagStripperAndTypeBolder();

		public String getDocumentation(Collection<? extends BaseElement> properties)
		{
			if (CollectionsUtil.isEmpty(properties))
			{
				return null;
			}
			BaseElement prop = properties.iterator().next();

			if (prop instanceof FunctionElement)
			{
				FunctionElement function = (FunctionElement) prop;
				List<String> result = new ArrayList<String>();

				// line 1: function name with argument names
				result.add(getHeader(function, null));

				// create buffer once
				List<String> buffer = new ArrayList<String>();

				// line 2..n: one line for each argument description
				for (ParameterElement parameter : function.getParameters())
				{
					// make sure buffer is empty
					buffer.clear();

					CollectionsUtil.addToList(buffer, " ", BULLET, "\t", parameter.getName()); //$NON-NLS-1$ //$NON-NLS-2$

					// add, possibly cleaned up, description, if it exists
					String description = parameter.getDescription();

					if (!StringUtil.isEmpty(description))
					{
						// strip html tags and preserve types that look like tags
						description = stripAndBold.searchAndReplace(description);

						CollectionsUtil.addToList(buffer, ":", FileUtil.NEW_LINE, " \t", description); //$NON-NLS-1$ //$NON-NLS-2$
					}

					result.add(StringUtil.concat(buffer));
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

	private JSModelFormatter(boolean useHTML, Section... sectionsToDisplay)
	{
		this.fSections = Arrays.asList(sectionsToDisplay);
		this.useHTML = useHTML;
		for (Section s : fSections)
		{
			s.useHTML = useHTML;
		}
	}

	/**
	 * getDescription - Returns header, newline, documentation
	 * 
	 * @param property
	 * @param projectURI
	 * @return
	 */
	public String getDescription(BaseElement property, URI projectURI)
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
	public String getHeader(BaseElement property, URI root)
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
	public String getHeader(final Collection<? extends BaseElement> properties, URI root)
	{
		if (CollectionsUtil.isEmpty(properties))
		{
			return StringUtil.EMPTY;
		}

		List<String> stringParts = new ArrayList<String>();
		if (useHTML)
		{
			stringParts.add(TagStripperAndTypeBolder.BOLD_OPEN_TAG);
		}

		List<Section> headerSections = CollectionsUtil.filter(fSections, new IFilter<Section>()
		{
			public boolean include(Section item)
			{
				return item.isHeader();
			}
		});
		stringParts.addAll(CollectionsUtil.map(headerSections, new IMap<Section, String>()
		{
			public String map(Section s)
			{
				return s.generate(properties, null);
			}
		}));

		if (useHTML)
		{
			stringParts.add(TagStripperAndTypeBolder.BOLD_CLOSE_TAG);
		}
		return StringUtil.concat(stringParts);
	}

	/**
	 * Returns just the documentation body.
	 * 
	 * @param property
	 * @return
	 */
	public String getDocumentation(BaseElement property)
	{
		return getDocumentation(CollectionsUtil.newList(property));
	}

	/**
	 * Returns just the documentation body.
	 * 
	 * @param properties
	 * @return
	 */
	public String getDocumentation(final Collection<? extends BaseElement> properties)
	{
		List<Section> docSections = CollectionsUtil.filter(fSections, new IFilter<Section>()
		{
			public boolean include(Section s)
			{
				return !s.isHeader();
			}
		});

		List<String> sectionStrings = CollectionsUtil.map(docSections, new IMap<Section, String>()
		{
			public String map(Section s)
			{
				return s.generate(properties, null);
			}
		});
		return StringUtil.concat(sectionStrings);
	}

	/**
	 * getImage
	 * 
	 * @param property
	 * @return
	 */
	public Image getImage(PropertyElement property)
	{
		String key = "property"; //$NON-NLS-1$
		Image result = PROPERTY;
		if (property instanceof FunctionElement)
		{
			key = "function"; //$NON-NLS-1$
			result = TYPE_IMAGE_MAP.get(JSTypeConstants.FUNCTION_TYPE);
		}

		if (property != null)
		{
			List<String> types = property.getTypeNames();

			if (types != null && types.size() == 1)
			{
				String type = types.get(0);

				if (TYPE_IMAGE_MAP.containsKey(type))
				{
					result = TYPE_IMAGE_MAP.get(type);
					key = type;
				}
				else if (type.startsWith(JSTypeConstants.DYNAMIC_CLASS_PREFIX))
				{
					result = TYPE_IMAGE_MAP.get(JSTypeConstants.OBJECT_TYPE);
					key = "object"; //$NON-NLS-1$
				}
				else if (type.startsWith(JSTypeConstants.FUNCTION_TYPE))
				{
					result = TYPE_IMAGE_MAP.get(JSTypeConstants.FUNCTION_TYPE);
					key = "function"; //$NON-NLS-1$
				}
				else if (type.endsWith(JSTypeConstants.ARRAY_LITERAL))
				{
					result = TYPE_IMAGE_MAP.get(JSTypeConstants.ARRAY_TYPE);
					key = "array"; //$NON-NLS-1$
				}
			}
			if (property.isClassProperty())
			{
				key += ".static"; //$NON-NLS-1$
				result = addOverlay(result, STATIC_OVERLAY, IDecoration.TOP_RIGHT, key);
			}
			if (property.isDeprecated())
			{
				key += ".deprecated"; //$NON-NLS-1$
				result = addOverlay(result, DEPRECATED_OVERLAY, IDecoration.TOP_LEFT, key);
			}
		}
		return result;
	}

	private Image addOverlay(Image base, ImageDescriptor overlay, int location, String key)
	{
		ImageRegistry reg = getImageRegistry();
		Image cached = reg.get(key);
		if (cached != null)
		{
			return cached;
		}

		DecorationOverlayIcon decorator = new DecorationOverlayIcon(base, overlay, location);
		Image result = decorator.createImage();
		reg.put(key, result);
		return result;
	}

	protected ImageRegistry getImageRegistry()
	{
		return JSPlugin.getDefault().getImageRegistry();
	}

	/**
	 * getDisplayTypeName
	 * 
	 * @param type
	 * @return
	 */
	public static String getTypeDisplayName(String type)
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

	protected String newline()
	{
		return useHTML ? HTML_NEWLINE : FileUtil.NEW_LINE;
	}

	private abstract static class Section
	{
		protected boolean useHTML;

		public boolean isHeader()
		{
			return false;
		}

		private String newline()
		{
			return useHTML ? HTML_NEWLINE : FileUtil.NEW_LINE;
		}

		protected String addSection(String title, String value)
		{
			StringBuilder builder = new StringBuilder();
			if (!StringUtil.isEmpty(value))
			{
				builder.append(newline()).append(newline());
				if (useHTML)
				{
					builder.append(TagStripperAndTypeBolder.BOLD_OPEN_TAG);
				}
				builder.append(title);
				if (useHTML)
				{
					builder.append(TagStripperAndTypeBolder.BOLD_CLOSE_TAG);
				}
				builder.append(newline());
				builder.append(value.trim());
			}
			return builder.toString();
		}

		public abstract String generate(Collection<? extends BaseElement> properties, URI root);

		protected String formatTypes(List<String> typeNames)
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
		 * Name, type, parameters of property/function all combined
		 */
		final static Section SIGNATURE = new Section()
		{
			public boolean isHeader()
			{
				return true;
			}

			public String generate(Collection<? extends BaseElement> properties, URI root)
			{
				BaseElement base = properties.iterator().next();
				List<String> builder = new ArrayList<String>();
				builder.add(base.getName());
				if (base instanceof PropertyElement)
				{
					PropertyElement prop = (PropertyElement) base;
					List<String> typeNames = prop.getTypeNames();
					if (prop instanceof FunctionElement)
					{
						FunctionElement fe = (FunctionElement) prop;
						builder.add("("); //$NON-NLS-1$
						builder.add(formatParameters(fe.getParameters()));
						builder.add(")"); //$NON-NLS-1$
						typeNames = fe.getReturnTypeNames();
					}
					builder.add(COLON_SPACE);
					builder.add(formatTypes(typeNames));
				}
				return StringUtil.concat(builder);
			}

			/**
			 * Formats {@link FunctionElement} parameters.
			 * 
			 * @param parameters
			 * @return
			 */
			private String formatParameters(Collection<ParameterElement> parameters)
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
		};

		/**
		 * Documents containing the property/function
		 */
		final static Section LOCATIONS = new Section()
		{
			public boolean isHeader()
			{
				return true;
			}

			@Override
			public String generate(Collection<? extends BaseElement> properties, URI root)
			{
				Set<String> documents = new LinkedHashSet<String>(); // linked hash set to preserve add order

				// collect all documents
				for (BaseElement pe : properties)
				{
					documents.addAll(pe.getDocuments());
				}

				// convert document list to text
				if (!documents.isEmpty())
				{
					List<String> parts = new ArrayList<String>(3); // concatenation container
					String first = documents.iterator().next();

					if (root != null)
					{
						try
						{
							first = root.relativize(new URI(first)).getPath();
						}
						catch (URISyntaxException e)
						{
							// ignore and use the default value set in the "first" declaration
						}
					}

					parts.add(" - "); //$NON-NLS-1$
					parts.add(first);

					if (documents.size() > 1)
					{
						parts.add(", ..."); //$NON-NLS-1$
					}

					return StringUtil.concat(parts);
				}

				return StringUtil.EMPTY;
			}
		};

		/**
		 * Single example
		 */
		final static Section EXAMPLE = new Section()
		{
			@Override
			public String generate(Collection<? extends BaseElement> properties, URI root)
			{
				String example = getFirstExample(properties);
				return addSection(Messages.JSTextHover_Example, example);
			}

			private String getFirstExample(Collection<? extends BaseElement> properties)
			{
				for (BaseElement prop : properties)
				{
					List<String> examples = getExamples(prop);
					for (String example : examples)
					{
						if (!StringUtil.isEmpty(example))
						{
							return example;
						}
					}
				}
				return StringUtil.EMPTY;
			}
		};

		/**
		 * Multiple examples
		 */
		final static Section EXAMPLES = new Section()
		{
			@Override
			public String generate(Collection<? extends BaseElement> properties, URI root)
			{
				List<String> examples = new ArrayList<String>();
				for (BaseElement prop : properties)
				{
					examples.addAll(getExamples(prop));
				}
				examples = CollectionsUtil.filter(examples, new IFilter<String>()
				{
					public boolean include(String item)
					{
						return !StringUtil.isEmpty(item);
					}
				});
				if (examples.size() == 1)
				{
					return addSection(Messages.JSTextHover_Example, examples.get(0));
				}

				List<String> builder = new ArrayList<String>();
				for (int i = 0; i < examples.size(); i++)
				{
					builder.add(addSection(Messages.JSTextHover_Example + " " + (i + 1), examples.get(i))); //$NON-NLS-1$
				}
				return StringUtil.concat(builder);
			}
		};

		/**
		 * Defining specs
		 */
		final static Section SPECIFICATIONS = new Section()
		{
			@Override
			public String generate(Collection<? extends BaseElement> properties, URI root)
			{
				Set<SinceElement> sinceElements = new HashSet<SinceElement>();
				for (BaseElement property : properties)
				{
					sinceElements.addAll(property.getSinceList());
				}
				return addSection(Messages.JSTextHover_Specification, getSpecificationsString(sinceElements));
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
		};

		/**
		 *
		 */
		final static Section DESCRIPTION = new Section()
		{
			private TagStripperAndTypeBolder stripAndBold = new TagStripperAndTypeBolder();

			@Override
			public String generate(Collection<? extends BaseElement> properties, URI root)
			{
				Set<String> descriptions = new HashSet<String>();
				for (BaseElement property : properties)
				{
					// strip p elements and bold any items that look like open tags with dotted local names
					stripAndBold.setUseHTML(useHTML);
					String desc = property.getDescription();
					if (property.isDeprecated())
					{
						desc = "<b>Deprecated</b><br>" + desc; //$NON-NLS-1$
					}
					desc = stripAndBold.searchAndReplace(desc);

					if (!StringUtil.isEmpty(desc))
					{
						descriptions.add(desc);
					}
				}

				if (CollectionsUtil.isEmpty(descriptions))
				{
					return Messages.JSTextHover_NoDescription;
				}
				return StringUtil.join(COMMA_SPACE, descriptions);
			}
		};

		/**
		 * User agents and versions
		 */
		final static Section PLATFORMS = new Section()
		{
			@Override
			public String generate(Collection<? extends BaseElement> properties, URI root)
			{
				Set<UserAgentElement> userAgents = new HashSet<UserAgentElement>();
				for (BaseElement property : properties)
				{
					userAgents.addAll(property.getUserAgents());
				}
				return addSection(Messages.JSTextHover_SupportedPlatforms, getPlatforms(userAgents));
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
		};

		/**
		 * Separated return value section
		 */
		final static Section RETURNS = new Section()
		{
			@Override
			public String generate(Collection<? extends BaseElement> properties, URI root)
			{
				List<String> returnTypeNames = new ArrayList<String>();
				for (BaseElement property : properties)
				{
					if (property instanceof FunctionElement)
					{
						FunctionElement function = (FunctionElement) property;
						returnTypeNames = function.getReturnTypeNames();
					}
				}
				return addSection(Messages.JSModelFormatter_Returns, formatTypes(returnTypeNames));
			}
		};

		/**
		 * Parameter listing in long-form (outside the signature). Includes names, types, and descriptions.
		 */
		final static Section PARAMETERS = new Section()
		{
			@Override
			public String generate(Collection<? extends BaseElement> properties, URI root)
			{
				List<ParameterElement> parameters = new ArrayList<ParameterElement>();

				for (BaseElement property : properties)
				{
					if (property instanceof FunctionElement)
					{
						FunctionElement function = (FunctionElement) property;
						parameters = function.getParameters();
					}
				}
				return addSection(Messages.JSModelFormatter_Parameters, getLongformParameters(parameters));
			}

			private String getLongformParameters(List<ParameterElement> parameters)
			{
				List<String> strings = CollectionsUtil.map(parameters, new IMap<ParameterElement, String>()
				{
					public String map(ParameterElement item)
					{
						List<String> b = new ArrayList<String>();
						b.add(item.getName());
						List<String> types = item.getTypes();
						if (!CollectionsUtil.isEmpty(types))
						{
							b.add(" (");//$NON-NLS-1$
							b.add(getTypeDisplayName(types.get(0)));
							b.add(")"); //$NON-NLS-1$
						}
						String desc = item.getDescription();
						if (!StringUtil.isEmpty(desc))
						{
							b.add(COLON_SPACE);
							b.add(desc);
						}
						return StringUtil.concat(b);
					}
				});
				return StringUtil.join(COMMA_SPACE, strings);
			}
		};

		protected static List<String> getExamples(BaseElement prop)
		{
			if (prop instanceof TypeElement)
			{
				TypeElement te = (TypeElement) prop;
				return te.getExamples();
			}
			if (prop instanceof PropertyElement)
			{
				PropertyElement pe = (PropertyElement) prop;
				return pe.getExamples();
			}
			return Collections.emptyList();
		}
	}
}
