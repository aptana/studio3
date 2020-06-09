package com.aptana.editor.common.text.rules;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
	private Map<String, String> others;

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

	public CommentScanner(IToken defaultToken, Map<String, String> others)
	{
		this(defaultToken, CollectionsUtil.map(TaskTag.getTaskTags(), new IMap<TaskTag, String>()
		{
			public String map(TaskTag item)
			{
				return item.getName();
			}
		}), TaskTag.isCaseSensitive(), others);
	}

	public CommentScanner(IToken defaultToken, Collection<String> tags, boolean caseSensitive)
	{
		this(defaultToken, tags, caseSensitive, null);
	}

	public CommentScanner(IToken defaultToken, Collection<String> tags, boolean caseSensitive,
			Map<String, String> others)
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
		// Other special keywords to pick up!
		this.others = others;
		if (this.others != null)
		{
			// add the keys to the list of tag names
			tagNames.addAll(others.keySet());
		}
		// Sort by descending length so that we try to match longest words/tokens first
		Collections.sort(tagNames, new Comparator<String>()
		{
			public int compare(String o1, String o2)
			{
				return o2.length() - o1.length();
			}
		});
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
				// Is this a task tag or an "other"?
				String text = matcher.group();
				if (this.others != null && this.others.containsKey(text))
				{
					queueToken(new Token(this.others.get(text)), startOffset + offset, tokenLength);
				}
				else
				{
					queueToken(taskToken, startOffset + offset, tokenLength);
				}
				currentOffset = endOffset;
			}
			// If we have space between end of last task tag and end of region, queue up default token!
			int lastMatchEnd = currentOffset + offset;
			int lastTokenLength = (length + offset) - lastMatchEnd;
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
