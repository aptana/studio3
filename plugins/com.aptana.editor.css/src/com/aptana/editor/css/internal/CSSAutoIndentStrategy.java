package com.aptana.editor.css.internal;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.aptana.editor.common.text.AbstractRegexpAutoIndentStrategy;

/**
 * @deprecated
 **/
//TODO Remove now that it's not used
public class CSSAutoIndentStrategy extends AbstractRegexpAutoIndentStrategy
{

	private static final String INCREASE_INDENT_REGEXP = "/\\*\\*(?!\\*)|\\{\\s*($|/\\*(?!.*?\\*/.*\\S))"; //$NON-NLS-1$
	private static final String DECREASE_INDENT_REGEXP = "(?<!\\*)\\*\\*/|^\\s*\\}"; //$NON-NLS-1$

	public CSSAutoIndentStrategy(String contentType, SourceViewerConfiguration configuration, ISourceViewer sourceViewer)
	{
		super(INCREASE_INDENT_REGEXP, DECREASE_INDENT_REGEXP, contentType, configuration, sourceViewer);
	}

	@Override
	protected boolean indentAndPushTrailingContentAfterNewlineAndCursor(String contentBeforeNewline,
			String contentAfterNewline)
	{
		return true;
	}

}
