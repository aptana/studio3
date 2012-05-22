/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.formatter.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.aptana.core.util.StringUtil;
import com.aptana.formatter.FormatterUtils;
import com.aptana.formatter.IFormatterDocument;

public abstract class FormatterNodeRewriter
{
	protected final List<CommentInfo> comments = new ArrayList<CommentInfo>();

	/**
	 * @param root
	 */
	public void rewrite(IFormatterContainerNode root)
	{
		mergeTextNodes(root);
		insertComments(root);
	}

	protected void mergeTextNodes(IFormatterContainerNode root)
	{
		final List<IFormatterNode> body = root.getBody();
		final List<IFormatterNode> newBody = new ArrayList<IFormatterNode>();
		final List<IFormatterNode> texts = new ArrayList<IFormatterNode>();
		for (final IFormatterNode node : body)
		{
			if (isPlainTextNode(node))
			{
				if (!texts.isEmpty()
						&& ((IFormatterTextNode) texts.get(texts.size() - 1)).getEndOffset() != node.getStartOffset())
				{
					flushTextNodes(texts, newBody);
				}
				texts.add(node);
			}
			else
			{
				if (!texts.isEmpty())
				{
					flushTextNodes(texts, newBody);
				}
				newBody.add(node);
			}
		}
		if (!texts.isEmpty())
		{
			flushTextNodes(texts, newBody);
		}
		if (body.size() != newBody.size())
		{
			body.clear();
			body.addAll(newBody);
		}
		for (final IFormatterNode node : body)
		{
			if (node instanceof IFormatterContainerNode)
			{
				mergeTextNodes((IFormatterContainerNode) node);
			}
		}
	}

	protected void attachComments(IFormatterContainerNode root)
	{
		final List<IFormatterNode> commentNodes = new ArrayList<IFormatterNode>();
		final List<IFormatterNode> comments = new ArrayList<IFormatterNode>();
		final List<IFormatterNode> body = root.getBody();
		for (IFormatterNode node : body)
		{
			if (node instanceof FormatterCommentNode)
			{
				comments.add(node);
			}
			else if (FormatterUtils.isNewLine(node) && !comments.isEmpty()
					&& comments.get(comments.size() - 1) instanceof FormatterCommentNode)
			{
				comments.add(node);
			}
			else if (!comments.isEmpty())
			{
				if (node instanceof IFormatterCommentableNode)
				{
					((IFormatterCommentableNode) node).insertBefore(comments);
					commentNodes.addAll(comments);
				}
				comments.clear();
			}
		}
		body.removeAll(commentNodes);
		for (Object node : body)
		{
			if (node instanceof IFormatterContainerNode)
			{
				attachComments((IFormatterContainerNode) node);
			}
		}
	}

	private void flushTextNodes(List<IFormatterNode> texts, List<IFormatterNode> newBody)
	{
		if (texts.size() > 1)
		{
			final IFormatterNode first = texts.get(0);
			final IFormatterNode last = texts.get(texts.size() - 1);
			newBody.add(new FormatterTextNode(first.getDocument(), first.getStartOffset(), last.getEndOffset()));
		}
		else
		{
			newBody.addAll(texts);
		}
		texts.clear();
	}

	protected boolean isPlainTextNode(final IFormatterNode node)
	{
		return node.getClass() == FormatterTextNode.class;
	}

	private static class CommentInfo
	{
		final int startOffset;
		final int endOffset;
		final Object object;

		public CommentInfo(int startOffset, int endOffset, Object object)
		{
			this.startOffset = startOffset;
			this.endOffset = endOffset;
			this.object = object;
		}

	}

	protected void addComment(int startOffset, int endOffset, Object object)
	{
		comments.add(new CommentInfo(startOffset, endOffset, object));
	}

	protected void insertComments(IFormatterContainerNode root)
	{
		final List<IFormatterNode> body = root.getBody();
		final List<IFormatterNode> newBody = new ArrayList<IFormatterNode>();
		boolean changes = false;
		for (final IFormatterNode node : body)
		{
			if (isPlainTextNode(node))
			{
				if (hasComments(node.getStartOffset(), node.getEndOffset()))
				{
					selectValidRanges(root.getDocument(), node.getStartOffset(), node.getEndOffset(), newBody);
					changes = true;
				}
				else
				{
					newBody.add(node);
				}
			}
			else
			{
				newBody.add(node);
			}
		}
		if (changes)
		{
			body.clear();
			body.addAll(newBody);
		}
		for (final IFormatterNode node : body)
		{
			if (node instanceof IFormatterContainerNode)
			{
				insertComments((IFormatterContainerNode) node);
			}
		}
	}

	private boolean hasComments(int startOffset, int endOffset)
	{
		for (final CommentInfo commentNode : comments)
		{
			if (commentNode.startOffset < endOffset && startOffset < commentNode.endOffset)
			{
				return true;
			}
		}
		return false;
	}

	private void selectValidRanges(IFormatterDocument document, int start, int end, List<IFormatterNode> result)
	{
		for (final CommentInfo comment : comments)
		{
			if (start <= comment.endOffset && comment.startOffset <= end)
			{
				if (start < comment.startOffset)
				{
					int validEnd = Math.min(end, comment.startOffset);
					// Check if the comment has a prefix of only tabs and spaces.
					String preCommentContent = document.get(start, validEnd);
					Pattern TABS_OR_SPACES = Pattern.compile("\\t| "); //$NON-NLS-1$
					if (TABS_OR_SPACES.matcher(preCommentContent).replaceAll(StringUtil.EMPTY).length() > 0)
					{
						// Not all characters are spaces and tabs, so we add all the range as a text node.
						result.add(new FormatterTextNode(document, start, validEnd));
					}
					else
					{
						// In case all characters in the range are spaces and tabs, we are adding *one* space/tab.
						if (preCommentContent.length() > 0 && !result.isEmpty()
								&& result.get(result.size() - 1).getSpacesCountAfter() == 0)
						{
							result.add(new FormatterTextNode(document, validEnd - 1, validEnd));
						}
					}

					start = comment.startOffset;
				}
				result.add(createCommentNode(document, start, Math.min(comment.endOffset, end), comment.object));
				start = comment.endOffset;
				if (start > end)
				{
					break;
				}
			}
		}
		if (start < end)
		{
			result.add(new FormatterTextNode(document, start, end));
		}
	}

	protected abstract IFormatterNode createCommentNode(IFormatterDocument document, int startOffset, int endOffset,
			Object object);

}
