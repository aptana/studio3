package com.aptana.editor.html;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.aptana.editor.common.text.AbstractRegexpAutoIndentStrategy;

/**
 * @author cwilliams
 */
public class HTMLAutoIndentStrategy extends AbstractRegexpAutoIndentStrategy
{

	private static final String BLOCK_TAGS = "body|div|form|frame|head|html|menu|ol|script|style|table|ul|thead|tbody|tfoot|tr|select|fieldset|li|dl"; //$NON-NLS-1$
	private static final String INCREASE_INDENT_REGEXP = "<(" + BLOCK_TAGS + ")([^>]*)?>$"; //$NON-NLS-1$ //$NON-NLS-2$
	private static final String DECREASE_INDENT_REGEXP = "</(" + BLOCK_TAGS + ")([^>]*)?>$"; //$NON-NLS-1$ //$NON-NLS-2$

	public HTMLAutoIndentStrategy(String contentType, SourceViewerConfiguration configuration,
			ISourceViewer sourceViewer)
	{
		super(INCREASE_INDENT_REGEXP, DECREASE_INDENT_REGEXP, contentType, configuration, sourceViewer);
	}

	/**
	 * We want to push close of tag two lines down and put cursor on next line with indent.
	 * 
	 * @param contentBeforeNewline
	 * @param contentAfterNewline
	 * @return
	 */
	protected boolean indentAndPushTrailingContentAfterNewlineAndCursor(String contentBeforeNewline,
			String contentAfterNewline)
	{
		if (contentBeforeNewline == null || contentAfterNewline == null || contentBeforeNewline.trim().length() == 0
				|| contentAfterNewline.trim().length() == 0)
			return false;
		// TODO If we're in middle of matching start/end pair, return true. <html>|</html>
		return true;
	}
}
