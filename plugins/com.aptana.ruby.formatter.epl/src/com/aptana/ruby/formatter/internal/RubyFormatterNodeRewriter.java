package com.aptana.ruby.formatter.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jrubyparser.SourcePosition;
import org.jrubyparser.ast.CommentNode;
import org.jrubyparser.parser.ParserResult;

import com.aptana.formatter.FormatterTextNode;
import com.aptana.formatter.FormatterUtils;
import com.aptana.formatter.IFormatterCommentableNode;
import com.aptana.formatter.IFormatterContainerNode;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterNode;
import com.aptana.formatter.IFormatterTextNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterCommentNode;

public class RubyFormatterNodeRewriter
{

	private final IFormatterDocument document;
	private final List<CommentNode> comments = new ArrayList<CommentNode>();

	public RubyFormatterNodeRewriter(ParserResult result, IFormatterDocument document)
	{
		this.document = document;
		for (Iterator<CommentNode> i = result.getCommentNodes().iterator(); i.hasNext();)
		{
			CommentNode commentNode = i.next();
			// if (!commentNode.isBlock())
			// {
			comments.add(commentNode);
			// }
		}
	}

	public void rewrite(IFormatterContainerNode root)
	{
		mergeTextNodes(root);
		insertComments(root);
		attachComments(root);
	}

	private void mergeTextNodes(IFormatterContainerNode root)
	{
		final List<IFormatterNode> body = root.getBody();
		final List<IFormatterNode> newBody = new ArrayList<IFormatterNode>();
		final List<IFormatterNode> texts = new ArrayList<IFormatterNode>();
		for (IFormatterNode node : body)
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
		for (IFormatterNode node : body)
		{
			if (node instanceof IFormatterContainerNode)
			{
				mergeTextNodes((IFormatterContainerNode) node);
			}
		}
	}

	private void flushTextNodes(List<IFormatterNode> texts, List<IFormatterNode> newBody)
	{
		if (texts.size() > 1)
		{
			final IFormatterNode first = texts.get(0);
			final IFormatterNode last = texts.get(texts.size() - 1);
			newBody.add(new FormatterTextNode(document, first.getStartOffset(), last.getEndOffset()));
		}
		else
		{
			newBody.addAll(texts);
		}
		texts.clear();
	}

	private void insertComments(IFormatterContainerNode root)
	{
		final List<IFormatterNode> body = root.getBody();
		final List<IFormatterNode> newBody = new ArrayList<IFormatterNode>();
		boolean changes = false;
		for (IFormatterNode node : body)
		{
			if (isPlainTextNode(node))
			{
				if (hasComments(node.getStartOffset(), node.getEndOffset()))
				{
					selectValidRanges(node.getStartOffset(), node.getEndOffset(), newBody);
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
		for (IFormatterNode node : body)
		{
			if (node instanceof IFormatterContainerNode)
			{
				insertComments((IFormatterContainerNode) node);
			}
		}
	}

	private void attachComments(IFormatterContainerNode root)
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

	private boolean isPlainTextNode(final IFormatterNode node)
	{
		return node.getClass() == FormatterTextNode.class;
	}

	private boolean hasComments(int startOffset, int endOffset)
	{
		for (CommentNode comment : comments)
		{
			SourcePosition position = comment.getPosition();
			if (position.getStartOffset() < endOffset && startOffset < position.getEndOffset())
			{
				return true;
			}
		}
		return false;
	}

	private void selectValidRanges(int start, int end, List<IFormatterNode> result)
	{
		for (CommentNode comment : comments)
		{
			SourcePosition position = comment.getPosition();
			if (start <= position.getEndOffset() && position.getStartOffset() <= end)
			{
				if (start < position.getStartOffset())
				{
					int validEnd = Math.min(end, position.getStartOffset());
					result.add(new FormatterTextNode(document, start, validEnd));
					start = position.getStartOffset();
				}
				result.add(new FormatterCommentNode(document, start, Math.min(position.getEndOffset(), end)));
				start = position.getEndOffset();
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

}
