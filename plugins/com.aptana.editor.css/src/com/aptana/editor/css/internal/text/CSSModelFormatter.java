/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.internal.text;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.aptana.core.IFilter;
import com.aptana.core.IMap;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.core.util.URIUtil;
import com.aptana.css.core.model.ElementElement;
import com.aptana.css.core.model.ICSSMetadataElement;
import com.aptana.css.core.model.PropertyElement;
import com.aptana.css.core.model.PseudoClassElement;
import com.aptana.css.core.model.PseudoElementElement;
import com.aptana.css.core.model.SpecificationElement;
import com.aptana.css.core.model.UserAgentElement;
import com.aptana.editor.common.hover.TagStripperAndTypeBolder;

/**
 * @author cwilliams
 */
public class CSSModelFormatter
{
	/**
	 * This is intentionally NOT <br />
	 * because the Additional Info popup doesn't handle that properly right now.
	 */
	private static final String HTML_NEWLINE = "<br>"; //$NON-NLS-1$

	/**
	 * For text hovers
	 */
	public static final CSSModelFormatter TEXT_HOVER = new CSSModelFormatter(true, Section.SIGNATURE,
			Section.DESCRIPTION, Section.PLATFORMS, Section.REMARK, Section.EXAMPLE, Section.SPECIFICATIONS);

	/**
	 * Used by UI label providers.
	 */
	public static final CSSModelFormatter LABEL = new CSSModelFormatter(false, Section.SIGNATURE);

	/**
	 * For "additional info" popup from highlighted CA item.
	 */
	public static final CSSModelFormatter ADDITIONAL_INFO = new CSSModelFormatter(true, Section.SIGNATURE,
			Section.DESCRIPTION, Section.PLATFORMS);

	/**
	 * The list of sections to display in our output.
	 */
	private List<Section> fSections;

	/**
	 * Use HTML tags in the output?
	 */
	private boolean useHTML;

	private CSSModelFormatter(boolean useHTML, Section... sectionsToDisplay)
	{
		this.fSections = Arrays.asList(sectionsToDisplay);
		this.useHTML = useHTML;
		for (Section s : fSections)
		{
			s.useHTML = useHTML;
		}
	}

	/**
	 * getDocumentDisplayName
	 * 
	 * @param document
	 * @return
	 */
	public static String getDocumentDisplayName(String document)
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
	 * Returns just the header, typically the signature plus optionally the locations
	 * 
	 * @param element
	 * @return
	 */
	public String getHeader(ICSSMetadataElement element)
	{
		return getHeader(CollectionsUtil.newList(element));
	}

	/**
	 * Returns just the header, typically the signature plus optionally the locations
	 * 
	 * @param elements
	 * @return
	 */
	public String getHeader(final Collection<ICSSMetadataElement> elements)
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
	public String getDocumentation(ICSSMetadataElement element)
	{
		return getDocumentation(CollectionsUtil.newList(element));
	}

	/**
	 * Returns just the documentation body.
	 * 
	 * @param elements
	 * @return
	 */
	public String getDocumentation(final Collection<ICSSMetadataElement> elements)
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

		public abstract String generate(Collection<ICSSMetadataElement> properties, URI root);

		/**
		 * Name, signature
		 */
		final static Section SIGNATURE = new Section("SIGNATURE") //$NON-NLS-1$
		{
			public boolean isHeader()
			{
				return true;
			}

			public String generate(Collection<ICSSMetadataElement> elements, URI root)
			{
				ICSSMetadataElement element = elements.iterator().next();
				return element.getName();
			}
		};

		/**
		 * Single example
		 */
		final static Section EXAMPLE = new Section("EXAMPLE") //$NON-NLS-1$
		{
			@Override
			public String generate(Collection<ICSSMetadataElement> elements, URI root)
			{
				String example = getFirstExample(elements);
				return addSection(Messages.CSSModelFormatter_ExampleSection, example);
			}

			private String getFirstExample(Collection<ICSSMetadataElement> elements)
			{
				for (ICSSMetadataElement element : elements)
				{
					String example = element.getExample();
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
			public String generate(Collection<ICSSMetadataElement> elements, URI root)
			{
				List<String> examples = new ArrayList<String>(elements.size());
				for (ICSSMetadataElement element : elements)
				{
					examples.add(element.getExample());
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
					return addSection(Messages.CSSModelFormatter_ExampleSection, examples.get(0));
				}

				List<String> builder = new ArrayList<String>();
				for (int i = 0; i < examples.size(); i++)
				{
					builder.add(addSection(MessageFormat.format(Messages.CSSModelFormatter_Example_Number, i + 1),
							examples.get(i)));
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
			public String generate(Collection<ICSSMetadataElement> elements, URI root)
			{
				String remark = getFirstRemark(elements);
				if (remark == null)
				{
					return StringUtil.EMPTY;
				}
				return addSection(Messages.CSSModelFormatter_RemarksSection, remark);
			}

			private String getFirstRemark(Collection<ICSSMetadataElement> elements)
			{
				for (ICSSMetadataElement element : elements)
				{
					String remark = null;
					if (element instanceof ElementElement)
					{
						remark = ((ElementElement) element).getRemark();
					}
					else if (element instanceof PropertyElement)
					{
						remark = ((PropertyElement) element).getRemark();
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
			public String generate(Collection<ICSSMetadataElement> elements, URI root)
			{
				Set<SpecificationElement> specs = new HashSet<SpecificationElement>();
				for (ICSSMetadataElement element : elements)
				{
					if (element instanceof PropertyElement)
					{
						specs.addAll(((PropertyElement) element).getSpecifications());
					}
					else if (element instanceof PseudoClassElement)
					{
						specs.addAll(((PseudoClassElement) element).getSpecifications());
					}
					else if (element instanceof PseudoElementElement)
					{
						specs.addAll(((PseudoElementElement) element).getSpecifications());
					}
				}
				if (CollectionsUtil.isEmpty(specs))
				{
					return StringUtil.EMPTY;
				}
				return addSection(Messages.CSSModelFormatter_SpecificationSection, getSpecificationsString(specs));
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
			public String generate(Collection<ICSSMetadataElement> elements, URI root)
			{
				Set<String> descriptions = new HashSet<String>();
				for (ICSSMetadataElement element : elements)
				{
					// strip p elements and bold any items that look like open tags with dotted local names
					stripAndBold.setUseHTML(useHTML);
					String desc = stripAndBold.searchAndReplace(element.getDescription());

					if (!StringUtil.isEmpty(desc))
					{
						descriptions.add(desc);
					}
				}

				if (CollectionsUtil.isEmpty(descriptions))
				{
					return Messages.CSSModelFormatter_NoDescription;
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
			public String generate(Collection<ICSSMetadataElement> elements, URI root)
			{
				Set<UserAgentElement> userAgents = new HashSet<UserAgentElement>();
				for (ICSSMetadataElement element : elements)
				{
					userAgents.addAll(element.getUserAgents());
				}
				return addSection(Messages.CSSModelFormatter_SupportedPlatforms, getPlatforms(userAgents));
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
