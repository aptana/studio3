package com.aptana.editor.common.text.rules;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.aptana.core.IMap;
import com.aptana.core.resources.TaskTag;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;

public class CommentScanner extends QueuedTokenScanner
{

	static final String TASK_TAG_SCOPE = "keyword.other.documentation.task"; //$NON-NLS-1$

	private IToken fDefaultToken;
	private Pattern fPattern;
	private static IToken taskToken = new Token(TASK_TAG_SCOPE);

	public CommentScanner(IToken defaultToken)
	{
		this(defaultToken, CollectionsUtil.map(TaskTag.getTaskTags(), new IMap<TaskTag, String>()
		{
			public String map(TaskTag item)
			{
				return item.getName();
			}
		}), TaskTag.isCaseSensitive());
	}

	public CommentScanner(IToken defaultToken, Collection<String> tags, boolean caseSensitive)
	{
		super();
		this.fDefaultToken = defaultToken;
		List<String> tagNames = CollectionsUtil.map(tags, new IMap<String, String>()
		{
			public String map(String item)
			{
				return Pattern.quote(item);
			}
		});
		int flags = 0;
		if (!caseSensitive)
		{
			flags = flags | Pattern.CASE_INSENSITIVE;
		}
		this.fPattern = Pattern.compile(StringUtil.join("|", tagNames), flags);
	}

	public void setRange(IDocument document, int offset, int length)
	{
		super.setRange(document, offset, length);
		try
		{
			String content = document.get(offset, length);
			// TODO: Do the matching as we go via nextToken() rather than up-front?
			Matcher matcher = fPattern.matcher(content);
			// Queue up the tokens!
			int currentOffset = 0;
			while (matcher.find())
			{
				int startOffset = matcher.start();
				if (startOffset > currentOffset)
				{
					// we had space between matches, fill with default token
					queueToken(fDefaultToken, currentOffset + offset, startOffset - currentOffset);
				}
				int endOffset = matcher.end();
				int tokenLength = endOffset - startOffset;
				queueToken(taskToken, startOffset + offset, tokenLength);
				currentOffset = endOffset;
			}
			// If we have space between end of last task tag and end of region, queue up default token!
			int lastMatchEnd = currentOffset + offset;
			int lastTokenLength = length - lastMatchEnd;
			if (lastTokenLength > 0)
			{
				queueToken(fDefaultToken, lastMatchEnd, lastTokenLength);
			}
		}
		catch (BadLocationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
