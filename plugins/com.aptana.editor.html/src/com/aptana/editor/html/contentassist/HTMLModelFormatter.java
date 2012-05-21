/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.aptana.core.IFilter;
import com.aptana.core.IMap;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.hover.TagStripperAndTypeBolder;
import com.aptana.editor.html.contentassist.model.AttributeElement;
import com.aptana.editor.html.contentassist.model.BaseElement;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.SpecificationElement;
import com.aptana.editor.html.contentassist.model.UserAgentElement;

/**
 * @author cwilliams
 */
public class HTMLModelFormatter
{
	/**
	 * This is intentionally NOT <br />
	 * because the Additional Info popup doesn't handle that properly right now.
	 */
	private static final String HTML_NEWLINE = "<br>"; //$NON-NLS-1$

	/**
	 * For text hovers
	 */
	public static final HTMLModelFormatter TEXT_HOVER = new HTMLModelFormatter(true, Section.NAME,
			Section.DISPLAY_NAME, Section.DESCRIPTION, Section.REMARK, Section.EXAMPLE, Section.SPECIFICATIONS);

	/**
	 * This list of sections to display in our output.
	 */
	private List<Section> fSections;

	/**
	 * Use HTML tags in the output?
	 */
	private boolean useHTML;

	private HTMLModelFormatter(boolean useHTML, Section... sectionsToDisplay)
	{
		this.fSections = Arrays.asList(sectionsToDisplay);
		this.useHTML = useHTML;
		for (Section s : fSections)
		{
			s.useHTML = useHTML;
		}
	}

	/**
	 * Returns just the header, typically the signature plus optionally the locations
	 * 
	 * @param property
	 * @return
	 */
	public String getHeader(BaseElement element)
	{
		return getHeader(CollectionsUtil.newList(element));
	}

	/**
	 * Returns just the header, typically the signature plus optionally the locations
	 * 
	 * @param elements
	 * @return
	 */
	public String getHeader(final Collection<BaseElement> elements)
	{
		if (CollectionsUtil.isEmpty(elements))
		{
			return StringUtil.EMPTY;
		}

		List<String> stringParts = new ArrayList<String>(fSections.size() + 2);
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
				return s.generate(elements, null);
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
	 * @param element
	 * @return
	 */
	public String getDocumentation(BaseElement element)
	{
		return getDocumentation(CollectionsUtil.newList(element));
	}

	/**
	 * Returns just the documentation body.
	 * 
	 * @param elements
	 * @return
	 */
	public String getDocumentation(final Collection<BaseElement> elements)
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
				return s.generate(elements, null);
			}
		});
		return StringUtil.concat(sectionStrings);
	}

	protected String newline()
	{
		return useHTML ? HTML_NEWLINE : FileUtil.NEW_LINE;
	}

	private abstract static class Section
	{
		protected boolean useHTML;
		private String name;

		private Section(String name)
		{
			this.name = name;
		}

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

		@Override
		public String toString()
		{
			return name;
		}

		public abstract String generate(Collection<BaseElement> properties, URI root);

		/**
		 * Name
		 */
		final static Section NAME = new Section("NAME") //$NON-NLS-1$
		{
			public boolean isHeader()
			{
				return true;
			}

			public String generate(Collection<BaseElement> elements, URI root)
			{
				BaseElement prop = elements.iterator().next();
				return prop.getName();
			}
		};

		/**
		 * display name combined
		 */
		final static Section DISPLAY_NAME = new Section("DISPLAY_NAME") //$NON-NLS-1$
		{
			public boolean isHeader()
			{
				return true;
			}

			public String generate(Collection<BaseElement> elements, URI root)
			{
				BaseElement prop = elements.iterator().next();
				if (!(prop instanceof ElementElement))
				{
					return StringUtil.EMPTY;
				}
				return MessageFormat.format(Messages.HTMLModelFormatter_DisplayName,
						((ElementElement) prop).getDisplayName());
			}
		};

		/**
		 * Single example
		 */
		final static Section EXAMPLE = new Section("EXAMPLE") //$NON-NLS-1$
		{
			@Override
			public String generate(Collection<BaseElement> elements, URI root)
			{
				String example = getFirstExample(elements);
				return addSection(Messages.HTMLModelFormatter_ExampleSection, example);
			}

			private String getFirstExample(Collection<BaseElement> elements)
			{
				for (BaseElement prop : elements)
				{
					if (!(prop instanceof ElementElement))
					{
						continue;
					}
					ElementElement el = (ElementElement) prop;
					String example = el.getExample();
					if (!StringUtil.isEmpty(example))
					{
						return example;
					}
				}
				return StringUtil.EMPTY;
			}
		};

		/**
		 * Multiple examples
		 */
		final static Section EXAMPLES = new Section("EXAMPLES") //$NON-NLS-1$
		{
			@Override
			public String generate(Collection<BaseElement> elements, URI root)
			{
				List<String> examples = new ArrayList<String>();
				for (BaseElement prop : elements)
				{
					if (!(prop instanceof ElementElement))
					{
						continue;
					}
					ElementElement el = (ElementElement) prop;
					examples.add(el.getExample());
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
					return addSection(Messages.HTMLModelFormatter_ExampleSection, examples.get(0));
				}

				List<String> builder = new ArrayList<String>();
				for (int i = 0; i < examples.size(); i++)
				{
					builder.add(addSection(MessageFormat.format("Example {0}", i + 1), examples.get(i))); //$NON-NLS-1$
				}
				return StringUtil.concat(builder);
			}
		};

		/**
		 * Single remark
		 */
		final static Section REMARK = new Section("REMARK") //$NON-NLS-1$
		{
			@Override
			public String generate(Collection<BaseElement> elements, URI root)
			{
				String remark = getFirstRemark(elements);
				return addSection(Messages.HTMLModelFormatter_RemarksSection, remark);
			}

			private String getFirstRemark(Collection<BaseElement> elements)
			{
				for (BaseElement prop : elements)
				{
					String remark = null;
					if (prop instanceof ElementElement)
					{
						ElementElement el = (ElementElement) prop;
						remark = el.getRemark();
					}
					else if (prop instanceof AttributeElement)
					{
						AttributeElement at = (AttributeElement) prop;
						remark = at.getRemark();
					}
					if (!StringUtil.isEmpty(remark))
					{
						return remark;
					}
				}
				return StringUtil.EMPTY;
			}
		};

		/**
		 * Defining specs
		 */
		final static Section SPECIFICATIONS = new Section("SPECIFICATIONS") //$NON-NLS-1$
		{
			@Override
			public String generate(Collection<BaseElement> properties, URI root)
			{
				Set<SpecificationElement> specs = new HashSet<SpecificationElement>();
				for (BaseElement property : properties)
				{
					List<SpecificationElement> someSpecs = Collections.emptyList();
					if (property instanceof ElementElement)
					{
						ElementElement el = (ElementElement) property;
						someSpecs = el.getSpecifications();
					}
					else if (property instanceof AttributeElement)
					{
						AttributeElement at = (AttributeElement) property;
						someSpecs = at.getSpecifications();
					}
					specs.addAll(someSpecs);
				}
				return addSection(Messages.HTMLModelFormatter_SpecificationSection, getSpecificationsString(specs));
			}

			private String getSpecificationsString(Collection<SpecificationElement> specs)
			{
				List<String> strings = CollectionsUtil.map(specs, new IMap<SpecificationElement, String>()
				{
					public String map(SpecificationElement item)
					{
						StringBuilder b = new StringBuilder();
						b.append(item.getName());
						String version = item.getVersion();
						if (!StringUtil.isEmpty(version))
						{
							b.append(": ").append(version); //$NON-NLS-1$
						}
						return b.toString();
					}
				});
				return StringUtil.join(", ", strings); //$NON-NLS-1$
			}
		};

		/**
		 *
		 */
		final static Section DESCRIPTION = new Section("DESCRIPTION") //$NON-NLS-1$
		{
			private TagStripperAndTypeBolder stripAndBold = new TagStripperAndTypeBolder();

			@Override
			public String generate(Collection<BaseElement> properties, URI root)
			{
				Set<String> descriptions = new HashSet<String>();
				for (BaseElement property : properties)
				{
					// strip p elements and bold any items that look like open tags with dotted local names
					stripAndBold.setUseHTML(useHTML);
					String desc = stripAndBold.searchAndReplace(property.getDescription());

					if (!StringUtil.isEmpty(desc))
					{
						descriptions.add(desc);
					}
				}

				if (CollectionsUtil.isEmpty(descriptions))
				{
					return Messages.HTMLModelFormatter_NoDescription;
				}
				return StringUtil.join(", ", descriptions); //$NON-NLS-1$
			}
		};

		/**
		 * User agents and versions
		 */
		final static Section PLATFORMS = new Section("PLATFORMS") //$NON-NLS-1$
		{
			@Override
			public String generate(Collection<BaseElement> elements, URI root)
			{
				Set<UserAgentElement> userAgents = new HashSet<UserAgentElement>();
				for (BaseElement property : elements)
				{
					List<UserAgentElement> ua = Collections.emptyList();
					if (property instanceof ElementElement)
					{
						ElementElement element = (ElementElement) property;
						ua = element.getUserAgents();
					}
					else if (property instanceof AttributeElement)
					{
						AttributeElement at = (AttributeElement) property;
						ua = at.getUserAgents();
					}
					userAgents.addAll(ua);
				}
				return addSection(Messages.HTMLModelFormatter_SupportedPlatforms, getPlatforms(userAgents));
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
							b.append(": ").append(version); //$NON-NLS-1$
						}
						return b.toString();
					}
				});
				return StringUtil.join(", ", strings); //$NON-NLS-1$
			}
		};
	}
}
