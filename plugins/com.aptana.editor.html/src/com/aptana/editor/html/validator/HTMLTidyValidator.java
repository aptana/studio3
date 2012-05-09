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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.jaxen.JaxenException;

import com.aptana.core.IFilter;
import com.aptana.core.IMap;
import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.IHTMLConstants;
import com.aptana.editor.html.contentassist.HTMLIndexQueryHelper;
import com.aptana.editor.html.contentassist.model.AttributeElement;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.EntityElement;
import com.aptana.editor.html.contentassist.model.ValueElement;
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
	public static final String ID = "com.aptana.editor.html.validator.TidyValidator"; //$NON-NLS-1$

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
	 * Set of unique id values from elements
	 */
	private Set<String> fIds;

	private boolean foundTitle;

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
			String sourcePath = context.getURI().toString();
			Collection<IProblem> problems = new ArrayList<IProblem>();
			try
			{

				String source = context.getContents();
				if (!StringUtil.isEmpty(source))
				{
					// TODO Can't we ask the context for line number of an offset so we don't duplicate the source? Or
					// maybe just ask for IDocument?
					IDocument doc = new Document(source);
					problems.addAll(validateDoctype(sourcePath, doc));

					IParseRootNode ast = context.getAST();
					if (ast != null)
					{
						foundTitle = false;
						problems.addAll(validateFrames(ast, doc, sourcePath));
						problems.addAll(validateAST(ast, doc, sourcePath));
						if (!foundTitle)
						{
							problems.add(createWarning(Messages.HTMLTidyValidator_InsertMissingTitle, 1, 0, 0,
									sourcePath));
						}
					}
				}
			}
			catch (CoreException e)
			{
				IdeLog.logError(HTMLPlugin.getDefault(),
						MessageFormat.format("Failed to validate {0} using HTML Tidy validator", sourcePath), e); //$NON-NLS-1$
			}

			context.putProblems(IHTMLConstants.TIDY_PROBLEM, problems);
		}
		finally
		{
			// clean up caches!
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
	private Collection<IProblem> checkLink(HTMLElementNode aNode, IDocument doc, String sourcePath)
	{
		String id = aNode.getAttributeValue("id"); //$NON-NLS-1$
		String name = aNode.getAttributeValue("name"); //$NON-NLS-1$
		if (id != null && name != null && !name.equals(id))
		{
			try
			{
				int offset = aNode.getStartingOffset();
				int length = aNode.getLength();
				return CollectionsUtil.newList(createError(Messages.HTMLTidyValidator_IdNameAttributeMismatch,
						doc.getLineOfOffset(offset) + 1, offset, length, sourcePath));
			}
			catch (BadLocationException e)
			{
				IdeLog.logError(HTMLPlugin.getDefault(), e);
			}
		}
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	private Collection<IProblem> validateFrames(IParseRootNode ast, IDocument doc, String sourcePath)
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
						problems.add(createWarning(Messages.HTMLTidyValidator_RepeatedFrameset,
								doc.getLineOfOffset(offset) + 1, offset, length, sourcePath));
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
						problems.add(createWarning(Messages.HTMLTidyValidator_InsertImplicitNoFrames,
								doc.getLineOfOffset(offset) + 1, offset, range.getLength(), sourcePath));
					}
					else
					{
						HTMLElementNode invalidContentNode = invalidContentNode(ast);
						if (invalidContentNode != null)
						{
							IRange range = invalidContentNode.getNameNode().getNameRange();
							problems.add(createWarning(Messages.HTMLTidyValidator_MissingNoFrames,
									doc.getLineOfOffset(range.getStartingOffset()) + 1, range.getStartingOffset(),
									range.getLength(), sourcePath));
						}
					}
				}
				else
				{
					HTMLElementNode invalidContentNode = invalidContentNode(ast);
					if (invalidContentNode != null)
					{
						IRange range = invalidContentNode.getNameNode().getNameRange();
						problems.add(createWarning(MessageFormat.format(
								Messages.HTMLTidyValidator_ElementNotInsideNoFrames,
								invalidContentNode.getElementName()),
								doc.getLineOfOffset(range.getStartingOffset()) + 1, range.getStartingOffset(), range
										.getLength(), sourcePath));
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

	private Collection<IProblem> validateAST(IParseRootNode root, final IDocument doc, final String sourcePath)
	{
		final Collection<IProblem> problems = new ArrayList<IProblem>();

		ParseUtil.treeApply(root, new IFilter<IParseNode>()
		{
			public boolean include(IParseNode node)
			{
				problems.addAll(handleNode(node, doc, sourcePath));
				return true;
			}
		});

		return problems;
	}

	private Collection<IProblem> handleNode(IParseNode node, IDocument doc, String sourcePath)
	{
		if (!(node instanceof HTMLNode))
		{
			return Collections.emptyList();
		}
		switch (node.getNodeType())
		{
			case IHTMLNodeTypes.ELEMENT:
			case IHTMLNodeTypes.SPECIAL:
				return validateElement((HTMLElementNode) node, doc, sourcePath);

			case IHTMLNodeTypes.COMMENT:
				return validateComment((HTMLCommentNode) node, doc, sourcePath);

			case IHTMLNodeTypes.TEXT:
				return validateTextContent((HTMLTextNode) node, doc, sourcePath);

			default:
				return Collections.emptyList();
		}
	}

	private Collection<IProblem> validateElement(HTMLElementNode element, IDocument doc, String sourcePath)
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
				problems.add(createWarning(
						MessageFormat.format(Messages.HTMLTidyValidator_ElementNotRecognized, tagName),
						doc.getLineOfOffset(offset) + 1, offset, length, sourcePath));
			}
			else
			{
				// Known tag
				problems.addAll(validateAttributes(element, doc, sourcePath));

				// Check if it is deprecated
				String deprecated = ee.getDeprecated();
				if (!StringUtil.isEmpty(deprecated))
				{
					int offset = element.getStartingOffset();
					problems.add(createWarning(
							MessageFormat.format(Messages.HTMLTidyValidator_DeprecatedElement, tagName, deprecated),
							doc.getLineOfOffset(offset) + 1, offset, element.getLength(), sourcePath));
				}

				// Check for empty tags that shouldn't be
				if (!HTMLParseState.isEmptyTagType(tagName) && ArrayUtil.isEmpty(element.getChildren()))
				{
					int offset = element.getStartingOffset();
					problems.add(createWarning(
							MessageFormat.format(Messages.HTMLTidyValidator_TrimEmptyElement, tagName),
							doc.getLineOfOffset(offset) + 1, offset, element.getLength(), sourcePath));
				}

				// Check for tags that should be empty that aren't
				if (HTMLParseState.isEmptyTagType(tagName) && !ArrayUtil.isEmpty(element.getChildren()))
				{
					int offset = element.getStartingOffset();
					problems.add(createWarning(
							MessageFormat.format(Messages.HTMLTidyValidator_ElementNotEmptyOrClosed, tagName),
							doc.getLineOfOffset(offset) + 1, offset, element.getLength(), sourcePath));
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
	private Collection<IProblem> validateAttributes(HTMLElementNode element, IDocument doc, String sourcePath)
			throws BadLocationException
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
			problems.addAll(checkLink(element, doc, sourcePath));
		}

		for (IParseNodeAttribute attr : attributes)
		{
			String attrName = attr.getName();
			// Check for uniqueness of id values.
			if ("id".equalsIgnoreCase(attrName)) //$NON-NLS-1$
			{
				String id = attr.getValue();
				if (fIds.contains(id))
				{
					int offset = element.getStartingOffset();
					problems.add(createWarning(
							MessageFormat.format(Messages.HTMLTidyValidator_NonUniqueIdValue, tagName, attr.getValue()),
							doc.getLineOfOffset(offset) + 1, offset, element.getNameNode().getNameRange().getLength(),
							sourcePath));
				}
				else
				{
					fIds.add(id);
				}
			}

			AttributeElement ae = getAttribute(tagName, attrName);
			if (ae == null)
			{
				// Unrecognized attribute!
				int offset = element.getStartingOffset();
				problems.add(createWarning(
						MessageFormat.format(Messages.HTMLTidyValidator_ProprietaryAttribute, tagName, attrName,
								attr.getValue()), doc.getLineOfOffset(offset) + 1, offset, element.getNameNode()
								.getNameRange().getLength(), sourcePath));
				continue;
			}

			// Check if attribute is deprecated
			String deprecated = ae.getDeprecated();
			if (!StringUtil.isEmpty(deprecated))
			{
				int offset = element.getStartingOffset();
				problems.add(createWarning(
						MessageFormat.format(Messages.HTMLTidyValidator_DeprecatedAttribute, tagName, attrName,
								attr.getValue()), doc.getLineOfOffset(offset) + 1, offset, element.getNameNode()
								.getNameRange().getLength(), sourcePath));
			}

			// verify the value for the attribute
			List<ValueElement> values = ae.getValues();
			if (!CollectionsUtil.isEmpty(values))
			{
				boolean matchingValue = false;
				for (ValueElement value : values)
				{
					String valueName = value.getName();
					if (valueName.equals(attr.getValue()) || "*".equals(valueName)) //$NON-NLS-1$
					{
						matchingValue = true;
						break;
					}
				}
				if (!matchingValue)
				{
					int offset = element.getStartingOffset();
					problems.add(createWarning(MessageFormat.format(Messages.HTMLTidyValidator_InvalidAttributeValue,
							tagName, attrName, attr.getValue()), doc.getLineOfOffset(offset) + 1, offset, element
							.getNameNode().getNameRange().getLength(), sourcePath));
				}
			}
		}
		return problems;
	}

	private Collection<IProblem> validateComment(HTMLCommentNode comment, IDocument doc, String sourcePath)
	{
		// String text = comment.getText();
		// TODO malformed_comment=Warning: adjacent hyphens within comment
		// TODO bad_comment_chars=Warning: expecting -- or >
		// TODO bad_xml_comment=Warning: XML comments can't contain --

		return Collections.emptyList();
	}

	private Collection<IProblem> validateTextContent(HTMLTextNode textNode, IDocument doc, String sourcePath)
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
					problems.add(createWarning(Messages.HTMLTidyValidator_UnescapedAmpersand,
							doc.getLineOfOffset(offset) + 1, offset, 1, sourcePath));
				}
				else if (!entity.endsWith(";")) //$NON-NLS-1$
				{
					EntityElement entityEl = getEntity('&' + entity + ';');
					String msg;
					if (entityEl != null)
					{
						msg = MessageFormat.format(Messages.HTMLTidyValidator_EntityMissingSemicolon, entity);
					}
					else
					{
						msg = MessageFormat.format(Messages.HTMLTidyValidator_UnknownEntity, entity);
					}
					problems.add(createWarning(msg, doc.getLineOfOffset(offset) + 1, offset, entity.length() + 1,
							sourcePath));
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
	 * @param sourcePath
	 * @param doc
	 * @return
	 */
	private Collection<IProblem> validateDoctype(String sourcePath, IDocument doc)
	{
		String source = doc.get();
		int doctypeIndex = source.indexOf("<!DOCTYPE"); //$NON-NLS-1$
		if (doctypeIndex == -1)
		{
			return CollectionsUtil
					.newList(createWarning(Messages.HTMLTidyValidator_MissingDoctype, 1, 0, 0, sourcePath));
		}

		Collection<IProblem> problems = new ArrayList<IProblem>(2);
		try
		{
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
							problems.add(createWarning(Messages.HTMLTidyValidator_UppercaseDoctype,
									doc.getLineOfOffset(offsetUppercase) + 1, offsetUppercase, 6, sourcePath));
						}
					}

					String publicStr = m.group(5);
					if (publicStr != null)
					{
						int offsetUppercase = m.start(5);
						if (!publicStr.equals("PUBLIC")) //$NON-NLS-1$
						{
							problems.add(createWarning(Messages.HTMLTidyValidator_UppercaseDoctype,
									doc.getLineOfOffset(offsetUppercase) + 1, offsetUppercase, 6, sourcePath));
						}

						String w3cStr = m.group(6);
						offsetUppercase = m.start(6);
						if (!w3cStr.equals("W3C")) //$NON-NLS-1$
						{
							problems.add(createWarning(Messages.HTMLTidyValidator_UppercaseDoctype,
									doc.getLineOfOffset(offsetUppercase) + 1, offsetUppercase, 3, sourcePath));
						}

						String dtdStr = m.group(7);
						offsetUppercase = m.start(7);
						if (!dtdStr.equals("DTD")) //$NON-NLS-1$
						{
							problems.add(createWarning(Messages.HTMLTidyValidator_UppercaseDoctype,
									doc.getLineOfOffset(offsetUppercase) + 1, offsetUppercase, 3, sourcePath));
						}

						String enStr = m.group(8);
						offsetUppercase = m.start(8);
						if (!enStr.equals("EN")) //$NON-NLS-1$
						{
							problems.add(createWarning(Messages.HTMLTidyValidator_UppercaseDoctype,
									doc.getLineOfOffset(offsetUppercase) + 1, offsetUppercase, 2, sourcePath));
						}
					}
				}
			}
			else
			{
				// Malformed Doctype
				problems.add(createWarning(Messages.HTMLTidyValidator_MalformedDoctype,
						doc.getLineOfOffset(doctypeIndex) + 1, doctypeIndex, 9, sourcePath));
			}

			// Check if doctype is after any tags...
			String before = source.substring(0, doctypeIndex);
			if (!StringUtil.isEmpty(before))
			{
				problems.add(createWarning(Messages.HTMLTidyValidator_DoctypeAfterElements,
						doc.getLineOfOffset(doctypeIndex) + 1, doctypeIndex, 9, sourcePath));
			}
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), e);
		}
		return problems;
	}
}
