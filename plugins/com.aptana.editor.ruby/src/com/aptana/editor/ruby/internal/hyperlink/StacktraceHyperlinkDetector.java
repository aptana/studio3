package com.aptana.editor.ruby.internal.hyperlink;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.tm.terminal.model.IHyperlinkDetector;

import com.aptana.editor.common.text.hyperlink.EditorLineHyperlink;

public class StacktraceHyperlinkDetector implements IHyperlinkDetector
{
	private static Pattern OPEN_TRACE_LINE_PATTERN = Pattern.compile("\\s*(\\S.+*?):(\\d+):\\s+"); //$NON-NLS-1$

	@Override
	public IHyperlink[] detectHyperlinks(String contents)
	{
		Matcher m = OPEN_TRACE_LINE_PATTERN.matcher(contents);
		if (m.find())
		{
			String filepath = m.group(1);
			int lineNumber = Integer.parseInt(m.group(2));
			int start = m.start(1);
			int parenIndex = filepath.indexOf("("); //$NON-NLS-1$
			if (parenIndex != -1)
			{
				filepath = filepath.substring(parenIndex + 1);
				start += parenIndex + 1;
			}
			int length = m.end(2) - start;
			return new IHyperlink[] { new EditorLineHyperlink(new Region(start, length), filepath, lineNumber) };
		}
		return new IHyperlink[0];
	}
}
