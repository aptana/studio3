/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.validator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.jaxen.JaxenException;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.buildpath.core.BuildPathCorePlugin;
import com.aptana.core.IFilter;
import com.aptana.core.IMap;
import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.build.Problem;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.contentassist.HTMLIndexQueryHelper;
import com.aptana.editor.html.contentassist.model.AttributeElement;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.EntityElement;
import com.aptana.editor.html.contentassist.model.EventElement;
import com.aptana.editor.html.contentassist.model.ValueElement;
import com.aptana.editor.html.core.IHTMLConstants;
import com.aptana.editor.html.parsing.HTMLDocumentTypes;
import com.aptana.editor.html.parsing.HTMLDocumentTypes.Type;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.ast.HTMLCommentNode;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLNode;
import com.aptana.editor.html.parsing.ast.HTMLTextNode;
import com.aptana.editor.html.parsing.ast.IHTMLNodeTypes;
import com.aptana.index.core.build.BuildContext;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseNodeAttribute;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.util.ParseUtil;
import com.aptana.parsing.xpath.ParseNodeNavigator;
import com.aptana.parsing.xpath.ParseNodeXPath;

/**
 * Custom replacement for JTidy. Allows us to re-use the AST we already generate, which gives us speed gains of about 4x
 * running tidy as a separate lib/process.
 * 
 * @author cwilliams
 */
public class HTMLTidyValidator extends AbstractBuildParticipant
{
	private static final EnumSet<Type> XHTML_TYPES = EnumSet.of(Type.XHTML_1_0_FRAMESET, Type.XHTML_1_0_STRICT,
			Type.XHTML_1_0_TRANSITIONAL, Type.XHTML_1_1_STRICT);

	public static final String ID = "com.aptana.editor.html.validator.TidyValidator"; //$NON-NLS-1$

	private static final String BOOLEAN_TYPE = "Boolean"; //$NON-NLS-1$

	private static final Pattern DOCTYPE_PATTERN = Pattern
			.compile(
					"<!(DOCTYPE)\\s+HTML(\\s+((SYSTEM)\\s+[\"']about:legacy-compat[\"']|(PUBLIC)\\s+[\"']\\-//(W3C)//(DTD)\\s+[^/]+//(EN)[\"'](\\s+[\"'].+[\"'])?))?>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE); //$NON-NLS-1$

	private static final Pattern ENTITY_PATTERN = Pattern.compile("&(\\S+)?"); //$NON-NLS-1$

	/**
	 * XPath expressions we use to jump to lists of nodes.
	 */
	private static ParseNodeXPath FRAMESET_TAG;
	private static ParseNodeXPath BODY_TAG;
	private static ParseNodeXPath NOFRAMES_TAG;
	private static ParseNodeXPath HTML_CHILDREN;

	public enum ProblemCategory
	{
		// @formatter:off
		Doctype("!DOCTYPE"),
		Entities("Entities"),
		Elements("Elements"),
		Attributes("Attributes");
		// @formatter:on

		private String label;

		ProblemCategory(String label)
		{
			this.label = label;
		}

		public String label()
		{
			return label;
		}
	}

	public enum ProblemType
	{
		// @formatter:off
		DeprecatedAttribute(ProblemCategory.Attributes, 256, "Deprecated Attributes"),
		DeprecatedElement(ProblemCategory.Elements, 257, "Deprecated Elements"),
		DoctypeAfterElements(ProblemCategory.Doctype, 258, Messages.HTMLTidyValidator_DoctypeAfterElements),
		ElementNotEmptyOrClosed(ProblemCategory.Elements, 259, "Unclosed elements"),
		ElementNotInsideNoFrames(ProblemCategory.Elements, 260, "Elements outside <noframes>"),
		ElementNotRecognized(ProblemCategory.Elements, 261, "Unrecognized elements"),
		EntityMissingSemicolon(ProblemCategory.Entities, 262, "Entity missing trailing semicolon"),
		IdNameAttributeMismatch(ProblemCategory.Attributes, 263, Messages.HTMLTidyValidator_IdNameAttributeMismatch),
		InsertImplicitNoFrames(ProblemCategory.Elements, 264, Messages.HTMLTidyValidator_InsertImplicitNoFrames),
		InsertMissingTitle(ProblemCategory.Elements, 265, "Missing <title> element"),
		InvalidAttributeValue(ProblemCategory.Attributes, 266, "Invalid attribute values"),
		MalformedDoctype(ProblemCategory.Doctype, 267, "Malformed !DOCTYPE"),
		MissingCloseTag(ProblemCategory.Elements, 268, "Missing close tags"),
		MissingDoctype(ProblemCategory.Doctype, 269, Messages.HTMLTidyValidator_MissingDoctype),
		MissingNoFrames(ProblemCategory.Elements, 270, Messages.HTMLTidyValidator_MissingNoFrames),
		NonUniqueIdValue(ProblemCategory.Attributes, 271, "Non-unique id attribute value"),
		ProprietaryAttribute(ProblemCategory.Attributes, 272, "Proprietary attributes"),
		RepeatedFrameset(ProblemCategory.Elements, 273, Messages.HTMLTidyValidator_RepeatedFrameset),
		TrimEmptyElement(ProblemCategory.Elements, 274, "Trim empty elements"),
		UnescapedAmpersand(ProblemCategory.Entities, 275, "Unescaped ampersand (&& which should be &&amp;)"),
		UnknownEntity(ProblemCategory.Entities, 276, "Unescaped or unknown entity"),
		UppercaseDoctype(ProblemCategory.Doctype, 277, Messages.HTMLTidyValidator_UppercaseDoctype);
		// @formatter:on

		private int id;
		private String description;
		private ProblemCategory category;

		ProblemType(ProblemCategory category, int id, String description)
		{
			this.category = category;
			this.id = id;
			this.description = description;
		}

		public ProblemCategory category()
		{
			return this.category;
		}

		public String description()
		{
			return description;
		}

		public int getId()
		{
			return id;
		}

		public String getPrefKey()
		{
			return "problem_" + getId(); //$NON-NLS-1$
		}
	}

	static
	{
		try
		{
			ParseNodeNavigator caseInsensitive = new ParseNodeNavigator(true);
			BODY_TAG = new ParseNodeXPath("/html/body", caseInsensitive); //$NON-NLS-1$
			FRAMESET_TAG = new ParseNodeXPath("/html/frameset", caseInsensitive); //$NON-NLS-1$
			NOFRAMES_TAG = new ParseNodeXPath("/html/frameset/noframes", caseInsensitive); //$NON-NLS-1$
			HTML_CHILDREN = new ParseNodeXPath("/html/*", caseInsensitive); //$NON-NLS-1$
		}
		catch (JaxenException e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), e);
		}
	}

	private HTMLIndexQueryHelper fQueryHelper;

	/**
	 * Cache from element names to the element metadata.
	 */
	private Map<String, ElementElement> fElementsMap;
	/**
	 * Cache from attribute names (possibly prepended by specific element name and then underscore) to the attribute
	 * metadata.
	 */
	private Map<String, AttributeElement> fAttributesMap;
	/**
	 * Cache from entity names to the entity metadata.
	 */
	private Map<String, EntityElement> fEntityMap;
	/**
	 * Cache from event names to the event metadata.
	 */
	private Map<String, EventElement> fEventMap;

	/**
	 * Set of unique id values from elements
	 */
	private Set<String> fIds;

	private boolean foundTitle;

	private String sourcePath;

	private Document doc;

	private Type docType;

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		context.removeProblems(IHTMLConstants.TIDY_PROBLEM);
	}

	public void buildFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		fQueryHelper = new HTMLIndexQueryHelper();
		fIds = new HashSet<String>();

		try
		{
			sourcePath = context.getURI().toString();
			Collection<IProblem> problems = new ArrayList<IProblem>();
			try
			{

				String source = context.getContents();
				if (!StringUtil.isEmpty(source))
				{
					docType = HTMLDocumentTypes.getType(source);
					// TODO Can't we ask the context for line number of an offset so we don't duplicate the source? Or
					// maybe just ask for IDocument?
					doc = new Document(source);
					problems.addAll(validateDoctype());

					IParseRootNode ast = context.getAST();
					if (ast != null)
					{
						foundTitle = false;
						problems.addAll(validateFrames(ast));
						problems.addAll(validateAST(ast));
						if (!foundTitle)
						{
							try
							{
								problems.add(createProblem(ProblemType.InsertMissingTitle,
										Messages.HTMLTidyValidator_InsertMissingTitle, 0, 0));
							}
							catch (BadLocationException e)
							{
								IdeLog.logError(HTMLPlugin.getDefault(), e);
							}
						}
					}
				}
			}
			catch (CoreException e)
			{
				IdeLog.logError(HTMLPlugin.getDefault(),
						MessageFormat.format("Failed to validate {0} using HTML Tidy validator", sourcePath), e); //$NON-NLS-1$
			}

			final List<String> filters = getFilters();
			problems = CollectionsUtil.filter(problems, new IFilter<IProblem>()
			{
				public boolean include(IProblem item)
				{
					return item != null && !isIgnored(item.getMessage(), filters);
				}
			});
			context.putProblems(IHTMLConstants.TIDY_PROBLEM, problems);
		}
		finally
		{
			// clean up caches!
			doc = null;
			sourcePath = null;
			fElementsMap = null;
			fAttributesMap = null;
			fEntityMap = null;
			fIds = null;
			fQueryHelper = null;
		}
	}

	/**
	 * We check links to see if the id and name attribute values match.
	 * 
	 * @param ast
	 * @param doc
	 * @param sourcePath
	 * @return
	 */
	private Collection<IProblem> checkLink(HTMLElementNode aNode)
	{
		String id = aNode.getAttributeValue("id"); //$NON-NLS-1$
		String name = aNode.getAttributeValue("name"); //$NON-NLS-1$
		if (id != null && name != null && !name.equals(id))
		{
			try
			{
				int offset = aNode.getStartingOffset();
				int length = aNode.getLength();
				return CollectionsUtil.newList(createProblem(ProblemType.IdNameAttributeMismatch,
						Messages.HTMLTidyValidator_IdNameAttributeMismatch, offset, length));
			}
			catch (BadLocationException e)
			{
				IdeLog.logError(HTMLPlugin.getDefault(), e);
			}
		}
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	private Collection<IProblem> validateFrames(IParseRootNode ast)
	{
		Collection<IProblem> problems = new ArrayList<IProblem>();

		try
		{
			List<HTMLElementNode> framesetNodes = (List<HTMLElementNode>) FRAMESET_TAG.evaluate(ast);
			if (!CollectionsUtil.isEmpty(framesetNodes))
			{
				// verify only one FRAMESET child of HTML
				for (int i = 0; i < framesetNodes.size(); i++)
				{
					// Skip the first one
					if (i != 0)
					{
						// We want to verify we have at most one frameset child of html
						HTMLElementNode framesetNode = framesetNodes.get(i);
						IRange range = framesetNode.getNameNode().getNameRange();
						int offset = range.getStartingOffset();
						int length = range.getLength();
						problems.add(createProblem(ProblemType.RepeatedFrameset,
								Messages.HTMLTidyValidator_RepeatedFrameset, offset, length));
					}
				}

				// Check NOFRAMES
				List<HTMLElementNode> noFramesNodes = (List<HTMLElementNode>) NOFRAMES_TAG.evaluate(ast);
				HTMLElementNode noFrames = null;
				if (!CollectionsUtil.isEmpty(noFramesNodes))
				{
					noFrames = noFramesNodes.get(0);
				}
				if (noFrames == null)
				{
					// If there's an html/body, add warning to insert implicit noFrames
					List<HTMLElementNode> bodyNode = (List<HTMLElementNode>) BODY_TAG.evaluate(ast);
					if (!CollectionsUtil.isEmpty(bodyNode))
					{
						IRange range = bodyNode.iterator().next().getNameNode().getNameRange();
						int offset = range.getStartingOffset();
						problems.add(createProblem(ProblemType.InsertImplicitNoFrames,
								Messages.HTMLTidyValidator_InsertImplicitNoFrames, offset, range.getLength()));
					}
					else
					{
						HTMLElementNode invalidContentNode = invalidContentNode(ast);
						if (invalidContentNode != null)
						{
							IRange range = invalidContentNode.getNameNode().getNameRange();
							problems.add(createProblem(ProblemType.MissingNoFrames,
									Messages.HTMLTidyValidator_MissingNoFrames, range.getStartingOffset(),
									range.getLength()));
						}
					}
				}
				else
				{
					HTMLElementNode invalidContentNode = invalidContentNode(ast);
					if (invalidContentNode != null)
					{
						IRange range = invalidContentNode.getNameNode().getNameRange();
						problems.add(createProblem(ProblemType.ElementNotInsideNoFrames, MessageFormat.format(
								Messages.HTMLTidyValidator_ElementNotInsideNoFrames,
								invalidContentNode.getElementName()), range.getStartingOffset(), range.getLength()));
					}
				}
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), e);
		}
		return problems;
	}

	/**
	 * We try to find a child element of html that isn't "body", "head" or "frameset". For frames, we'll mark this node
	 * as needing to be in a "noframes" element.
	 * 
	 * @param ast
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HTMLElementNode invalidContentNode(IParseRootNode ast)
	{
		try
		{
			List<HTMLNode> htmlChildren = (List<HTMLNode>) HTML_CHILDREN.evaluate(ast);
			if (!CollectionsUtil.isEmpty(htmlChildren))
			{
				Set<String> validTags = CollectionsUtil.newSet("body", "head", "frameset"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				for (HTMLNode htmlChild : htmlChildren)
				{
					if (!(htmlChild instanceof HTMLElementNode))
					{
						continue;
					}
					HTMLElementNode element = (HTMLElementNode) htmlChild;
					String tagName = element.getElementName().toLowerCase();
					if (!validTags.contains(tagName))
					{
						return element;
					}
				}
			}
		}
		catch (JaxenException e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), e);
		}
		return null;
	}

	private Collection<IProblem> validateAST(IParseRootNode root)
	{
		final Collection<IProblem> problems = new ArrayList<IProblem>();

		ParseUtil.treeApply(root, new IFilter<IParseNode>()
		{
			public boolean include(IParseNode node)
			{
				problems.addAll(handleNode(node));
				return true;
			}
		});

		return problems;
	}

	private Collection<IProblem> handleNode(IParseNode node)
	{
		if (!(node instanceof HTMLNode))
		{
			return Collections.emptyList();
		}
		switch (node.getNodeType())
		{
			case IHTMLNodeTypes.ELEMENT:
			case IHTMLNodeTypes.SPECIAL:
				return validateElement((HTMLElementNode) node);

			case IHTMLNodeTypes.COMMENT:
				return validateComment((HTMLCommentNode) node);

			case IHTMLNodeTypes.TEXT:
				return validateTextContent((HTMLTextNode) node);

			default:
				return Collections.emptyList();
		}
	}

	private Collection<IProblem> validateElement(HTMLElementNode element)
	{
		List<IProblem> problems = new ArrayList<IProblem>(2);
		try
		{
			String tagName = element.getName().toLowerCase();
			if (!foundTitle && "title".equals(tagName)) //$NON-NLS-1$
			{
				foundTitle = true;
			}

			// Search if tag is unknown element!
			ElementElement ee = getElement(tagName);
			if (ee == null)
			{
				// This is an unknown tag!
				int offset = element.getStartingOffset();
				int length = element.getLength();
				problems.add(createProblem(ProblemType.ElementNotRecognized,
						MessageFormat.format(Messages.HTMLTidyValidator_ElementNotRecognized, tagName), offset, length));
			}
			else
			{
				// Known tag
				problems.addAll(validateAttributes(element));

				// Check if it is deprecated
				String deprecated = ee.getDeprecated();
				if (!StringUtil.isEmpty(deprecated))
				{
					int offset = element.getStartingOffset();
					problems.add(createProblem(ProblemType.DeprecatedElement,
							MessageFormat.format(Messages.HTMLTidyValidator_DeprecatedElement, tagName, deprecated),
							offset, element.getLength()));
				}

				// Check for empty tags that shouldn't be
				if (!HTMLParseState.isEmptyTagType(tagName) && ArrayUtil.isEmpty(element.getChildren()))
				{
					// If it's a script tag with a src attribute, don't mark it
					if (!"script".equals(tagName) || StringUtil.isEmpty(element.getAttributeValue("src"))) //$NON-NLS-1$ //$NON-NLS-2$
					{
						int offset = element.getStartingOffset();
						problems.add(createProblem(ProblemType.TrimEmptyElement,
								MessageFormat.format(Messages.HTMLTidyValidator_TrimEmptyElement, tagName), offset,
								element.getLength()));
					}
				}

				// Check for tags that should be empty that aren't
				if (HTMLParseState.isEmptyTagType(tagName) && !ArrayUtil.isEmpty(element.getChildren()))
				{
					int offset = element.getStartingOffset();
					problems.add(createProblem(ProblemType.ElementNotEmptyOrClosed,
							MessageFormat.format(Messages.HTMLTidyValidator_ElementNotEmptyOrClosed, tagName), offset,
							element.getLength()));
				}

				// We can't "find" missing end tags, because our parser fixes this inline.
			}
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), e);
		}

		return problems;
	}

	/**
	 * Validate the attributes for an element. We verify that the attribute is a known one and that if we have a
	 * sepcific set of values in our metadata that the value matches one of them.
	 * 
	 * @param element
	 * @param doc
	 * @param sourcePath
	 * @return
	 * @throws BadLocationException
	 */
	private Collection<IProblem> validateAttributes(HTMLElementNode element) throws BadLocationException
	{
		IParseNodeAttribute[] attributes = element.getAttributes();
		if (ArrayUtil.isEmpty(attributes))
		{
			return Collections.emptyList();
		}

		Collection<IProblem> problems = new ArrayList<IProblem>(attributes.length);
		String tagName = element.getElementName();

		if ("a".equalsIgnoreCase(tagName)) //$NON-NLS-1$
		{
			problems.addAll(checkLink(element));
		}

		for (IParseNodeAttribute attr : attributes)
		{
			String attrName = attr.getName();
			String attrValue = attr.getValue();
			// Check for uniqueness of id values.
			if ("id".equalsIgnoreCase(attrName)) //$NON-NLS-1$
			{
				if (fIds.contains(attrValue))
				{
					int offset = element.getStartingOffset();
					problems.add(createProblem(ProblemType.NonUniqueIdValue,
							MessageFormat.format(Messages.HTMLTidyValidator_NonUniqueIdValue, tagName, attrValue),
							offset, element.getNameNode().getNameRange().getLength()));
				}
				else
				{
					fIds.add(attrValue);
				}
			}

			AttributeElement ae = getAttribute(tagName, attrName);
			if (ae == null)
			{
				EventElement event = getEvent(attrName);
				if (event == null)
				{
					if (!XHTML_TYPES.contains(docType))
					{
						// Unrecognized attribute!
						int offset = element.getStartingOffset();
						problems.add(createProblem(ProblemType.ProprietaryAttribute, MessageFormat.format(
								Messages.HTMLTidyValidator_ProprietaryAttribute, tagName, attrName, attrValue), offset,
								element.getNameNode().getNameRange().getLength()));
					}
				}
				continue;
			}

			// Check if attribute is deprecated
			String deprecated = ae.getDeprecated();
			if (!StringUtil.isEmpty(deprecated))
			{
				int offset = element.getStartingOffset();
				problems.add(createProblem(ProblemType.DeprecatedAttribute, MessageFormat.format(
						Messages.HTMLTidyValidator_DeprecatedAttribute, tagName, attrName, attrValue), offset, element
						.getNameNode().getNameRange().getLength()));
			}

			// verify the value for the attribute
			List<ValueElement> values = ae.getValues();
			if (!CollectionsUtil.isEmpty(values))
			{
				boolean validAttribute = false;
				// If attribute is a boolean one, valid values are empty string, no value, or case-insensitive match
				// with
				// the attribute name.
				if (BOOLEAN_TYPE.equalsIgnoreCase(ae.getType()))
				{
					validAttribute = (StringUtil.isEmpty(attrValue) || attrName.equalsIgnoreCase(attrValue));
				}
				else
				{
					for (ValueElement value : values)
					{
						String valueName = value.getName();
						if (valueName.equals(attrValue) || "*".equals(valueName)) //$NON-NLS-1$
						{
							validAttribute = true;
							break;
						}
					}
				}
				if (!validAttribute)
				{
					int offset = element.getStartingOffset();
					problems.add(createProblem(ProblemType.InvalidAttributeValue, MessageFormat.format(
							Messages.HTMLTidyValidator_InvalidAttributeValue, tagName, attrName, attrValue), offset,
							element.getNameNode().getNameRange().getLength()));
				}
			}
		}
		return problems;
	}

	private Collection<IProblem> validateComment(HTMLCommentNode comment)
	{
		// String text = comment.getText();
		// TODO malformed_comment=Warning: adjacent hyphens within comment
		// TODO bad_comment_chars=Warning: expecting -- or >
		// TODO bad_xml_comment=Warning: XML comments can't contain --

		return Collections.emptyList();
	}

	private Collection<IProblem> validateTextContent(HTMLTextNode textNode)
	{
		String text = textNode.getText();
		if (StringUtil.isEmpty(text) || text.indexOf('&') == -1)
		{
			return Collections.emptyList();
		}

		Collection<IProblem> problems = new ArrayList<IProblem>(2);
		Matcher m = ENTITY_PATTERN.matcher(text);
		while (m.find())
		{
			try
			{
				int offset = textNode.getStartingOffset() + m.start();
				String entity = m.group(1);
				if (entity == null)
				{
					problems.add(createProblem(ProblemType.UnescapedAmpersand,
							Messages.HTMLTidyValidator_UnescapedAmpersand, offset, 1));
				}
				else if (!entity.endsWith(";")) //$NON-NLS-1$
				{
					EntityElement entityEl = getEntity('&' + entity + ';');
					String msg;
					ProblemType type;
					if (entityEl != null)
					{
						type = ProblemType.EntityMissingSemicolon;
						msg = MessageFormat.format(Messages.HTMLTidyValidator_EntityMissingSemicolon, entity);
					}
					else
					{
						type = ProblemType.UnknownEntity;
						msg = MessageFormat.format(Messages.HTMLTidyValidator_UnknownEntity, entity);
					}
					problems.add(createProblem(type, msg, offset, entity.length() + 1));
				}
			}
			catch (BadLocationException e)
			{
				IdeLog.logError(HTMLPlugin.getDefault(), e);
			}
		}

		return problems;
	}

	private EntityElement getEntity(String entityName)
	{
		if (fEntityMap == null)
		{
			List<EntityElement> entities = fQueryHelper.getEntities();
			fEntityMap = CollectionsUtil.mapFromValues(entities, new IMap<EntityElement, String>()
			{
				public String map(EntityElement item)
				{
					return item.getName();
				}
			});
		}
		return fEntityMap.get(entityName);
	}

	private EventElement getEvent(String eventName)
	{
		if (fEventMap == null)
		{
			List<EventElement> events = fQueryHelper.getEvents();
			fEventMap = CollectionsUtil.mapFromValues(events, new IMap<EventElement, String>()
			{
				public String map(EventElement item)
				{
					return item.getName();
				}
			});
		}
		return fEventMap.get(eventName);
	}

	private ElementElement getElement(String tagName)
	{
		if (fElementsMap == null)
		{
			List<ElementElement> elements = fQueryHelper.getElements();
			fElementsMap = CollectionsUtil.mapFromValues(elements, new IMap<ElementElement, String>()
			{
				public String map(ElementElement item)
				{
					return item.getName();
				}
			});
		}
		return fElementsMap.get(tagName);
	}

	private AttributeElement getAttribute(String tagName, String attributeName)
	{
		if (fAttributesMap == null)
		{
			List<AttributeElement> attrs = fQueryHelper.getAttributes();
			fAttributesMap = CollectionsUtil.mapFromValues(attrs, new IMap<AttributeElement, String>()
			{
				public String map(AttributeElement item)
				{
					String element = item.getElement();
					if (StringUtil.isEmpty(element))
					{
						return item.getName();
					}
					return element + "_" + item.getName(); //$NON-NLS-1$
				}
			});
		}

		AttributeElement ae = fAttributesMap.get(tagName.toLowerCase() + "_" + attributeName); //$NON-NLS-1$
		if (ae != null)
		{
			return ae;
		}
		return fAttributesMap.get(attributeName);
	}

	/**
	 * Validates the DOCTYPE declaration.
	 * 
	 * @return
	 */
	private Collection<IProblem> validateDoctype()
	{
		String source = doc.get();
		Collection<IProblem> problems = new ArrayList<IProblem>(2);
		try
		{
			int doctypeIndex = source.indexOf("<!DOCTYPE"); //$NON-NLS-1$
			if (doctypeIndex == -1)
			{
				doctypeIndex = source.indexOf("<!doctype"); //$NON-NLS-1$
			}
			if (doctypeIndex == -1)
			{
				return CollectionsUtil.newList(createProblem(ProblemType.MissingDoctype,
						Messages.HTMLTidyValidator_MissingDoctype, 0, 0));
			}

			Matcher m = DOCTYPE_PATTERN.matcher(source);
			if (m.find())
			{
				String rest = m.group(2);
				if (!StringUtil.isEmpty(rest))
				{
					String systemStr = m.group(4);
					if (systemStr != null)
					{
						int offsetUppercase = m.start(4);
						if (!systemStr.equals("SYSTEM")) //$NON-NLS-1$
						{
							problems.add(createProblem(ProblemType.UppercaseDoctype,
									Messages.HTMLTidyValidator_UppercaseDoctype, offsetUppercase, 6));
						}
					}

					String publicStr = m.group(5);
					if (publicStr != null)
					{
						int offsetUppercase = m.start(5);
						if (!publicStr.equals("PUBLIC")) //$NON-NLS-1$
						{
							problems.add(createProblem(ProblemType.UppercaseDoctype,
									Messages.HTMLTidyValidator_UppercaseDoctype, offsetUppercase, 6));
						}

						String w3cStr = m.group(6);
						offsetUppercase = m.start(6);
						if (!w3cStr.equals("W3C")) //$NON-NLS-1$
						{
							problems.add(createProblem(ProblemType.UppercaseDoctype,
									Messages.HTMLTidyValidator_UppercaseDoctype, offsetUppercase, 3));
						}

						String dtdStr = m.group(7);
						offsetUppercase = m.start(7);
						if (!dtdStr.equals("DTD")) //$NON-NLS-1$
						{
							problems.add(createProblem(ProblemType.UppercaseDoctype,
									Messages.HTMLTidyValidator_UppercaseDoctype, offsetUppercase, 3));
						}

						String enStr = m.group(8);
						offsetUppercase = m.start(8);
						if (!enStr.equals("EN")) //$NON-NLS-1$
						{
							problems.add(createProblem(ProblemType.UppercaseDoctype,
									Messages.HTMLTidyValidator_UppercaseDoctype, offsetUppercase, 2));
						}
					}
				}
			}
			else
			{
				// Malformed Doctype
				problems.add(createProblem(ProblemType.MalformedDoctype, Messages.HTMLTidyValidator_MalformedDoctype,
						doctypeIndex, 9));
			}

			// Check if doctype is after any tags...
			String before = source.substring(0, doctypeIndex);
			if (!StringUtil.isEmpty(before) && !XHTML_TYPES.contains(docType))
			{
				problems.add(createProblem(ProblemType.DoctypeAfterElements,
						Messages.HTMLTidyValidator_DoctypeAfterElements, doctypeIndex, 9));
			}
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), e);
		}
		return problems;
	}

	/**
	 * Creates a problem of the given type, unless the user has set to ignore the given type. Looks up the severity
	 * based on the user's preferences. Defaults to {@link IProblem.Severity#WARNING}
	 * 
	 * @param type
	 * @param message
	 * @param offset
	 * @param length
	 * @return
	 * @throws BadLocationException
	 */
	private IProblem createProblem(ProblemType type, String message, int offset, int length)
			throws BadLocationException
	{
		IProblem.Severity severity = getSeverity(type);
		if (severity == IProblem.Severity.IGNORE)
		{
			return null;
		}
		int lineNumber = doc.getLineOfOffset(offset) + 1;
		IProblem problem = new Problem(severity.intValue(), message, offset, length, lineNumber, sourcePath);
		problem.setAttribute(ID, type.id);
		return problem;
	}

	private IProblem.Severity getSeverity(ProblemType type)
	{
		int num = getPreferenceInt(type.getPrefKey(), IProblem.Severity.WARNING.intValue());
		return IProblem.Severity.create(num);
	}

	@Override
	public void restoreDefaults()
	{
		// Wipe the user prefs for the problem severities
		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode(getPreferenceNode());
		for (ProblemType type : ProblemType.values())
		{
			prefs.remove(type.getPrefKey());
		}
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(BuildPathCorePlugin.getDefault(), e);
		}
		// Let super class handle cleaning up enablement and filters
		super.restoreDefaults();
	}
}
