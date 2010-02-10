package com.aptana.editor.ruby;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.aptana.editor.common.text.AbstractRegexpAutoIndentStrategy;

/**
 * @author cwilliams
 * @deprecated No longer used now that bundles can contribute
 */
// TODO Remove now that it's not used
public class RubyAutoIndentStrategy extends AbstractRegexpAutoIndentStrategy
{
	/**
	 * Nasty regexp taken from Textmate for auto-indenting.
	 */
	private static final String INCREASE_INDENT_REGEXP = "(\\s*(module|class|def|unless|if|else|elsif|case|when|begin|rescue|ensure|for|while|until|(?=.*?\\b(do|begin|case|if|unless)\\b)(\"(\\.|[^\\\"])*+\"|''(\\.|[^\\''])*+''|[^#\"''])*(\\s(do|begin|case)|[-+=&|*/~%^<>~](?<!\\$.)\\s*+(if|unless)))\\b(?![^;]*+;.*?\bend\b)|(\"(\\.|[^\\\"])*+\"|''(\\.|[^\\''])*+''|[^#\"''])*(\\{(?![^}]*+\\})|\\[(?![^\\]]*+\\]))).*$"; //$NON-NLS-1$
	private static final String DECREASE_INDENT_REGEXP = "((^|;)\\s*+end\\s*+([#].*)?$|(^|;)\\s*+end\\..*$|^\\s*+[}\\]],?\\s*+([#].*)?$|[#].*?\\(end\\)\\s*+$|^=end)"; //$NON-NLS-1$
	
	public RubyAutoIndentStrategy(String contentType, SourceViewerConfiguration configuration,
			ISourceViewer sourceViewer)
	{
		super(INCREASE_INDENT_REGEXP, DECREASE_INDENT_REGEXP, contentType, configuration, sourceViewer);
	}

	/**
	 * Handle [], (), and {} special. We want to push close of pair two lines down and put cursor on next line with
	 * indent.
	 * 
	 * @param contentBeforeNewline
	 * @param contentAfterNewline
	 * @return
	 */
	protected boolean indentAndPushTrailingContentAfterNewlineAndCursor(String contentBeforeNewline, String contentAfterNewline)
	{
		if (contentBeforeNewline == null || contentAfterNewline == null || contentBeforeNewline.trim().length() == 0
				|| contentAfterNewline.trim().length() == 0)
			return false;
		contentBeforeNewline = contentBeforeNewline.trim();
		contentAfterNewline = contentAfterNewline.trim();
		char before = contentBeforeNewline.charAt(contentBeforeNewline.length() - 1);
		char after = contentAfterNewline.charAt(0);
		if (before == '[' && after == ']')
			return true;
		if (before == '{' && after == '}')
			return true;
		if (before == '(' && after == ')')
			return true;
		return false;
	}
}
