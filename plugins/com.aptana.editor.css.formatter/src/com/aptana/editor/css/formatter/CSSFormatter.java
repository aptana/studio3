package com.aptana.editor.css.formatter;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import com.aptana.formatter.AbstractScriptFormatter;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.FormatterIndentDetector;
import com.aptana.formatter.FormatterWriter;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.ui.CodeFormatterConstants;
import com.aptana.formatter.ui.FormatterException;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.formatter.ui.IScriptFormatter;
import com.aptana.formatter.util.DumpContentException;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.ui.preferences.IPreferenceDelegate;

/**
 * CSS code formatter.
 */
public class CSSFormatter extends AbstractScriptFormatter implements IScriptFormatter
{

	/**
	 * Blank lines constants
	 */
	protected static final String[] BLANK_LINES = { CSSFormatterConstants.LINES_AFTER_ELEMENTS,
			CSSFormatterConstants.LINES_BEFORE_NON_CSS_ELEMENTS, CSSFormatterConstants.LINES_AFTER_NON_CSS_ELEMENTS };

	private String lineSeparator;

	/**
	 * Constructor.
	 * 
	 * @param preferences
	 */
	protected CSSFormatter(String lineSeparator, Map<String, ? extends Object> preferences, String mainContentType)
	{
		super(preferences, mainContentType);
		this.lineSeparator = lineSeparator;
	}

	/**
	 * Detects the indentation level.
	 */
	public int detectIndentationLevel(IDocument document, int offset)
	{
		IParser parser = getParser();
		IParseState parseState = new ParseState();
		String source = document.get();
		parseState.setEditState(source, null, 0, 0);
		int indent = 0;
		try
		{
			IParseRootNode parseResult = parser.parse(parseState);
			if (parseResult != null)
			{
				final CSSFormatterNodeBuilder builder = new CSSFormatterNodeBuilder();
				final FormatterDocument formatterDocument = createFormatterDocument(source);
				IFormatterContainerNode root = builder.build(parseResult, formatterDocument);
				new CSSFormatterNodeRewriter(parseResult, formatterDocument).rewrite(root);
				IFormatterContext context = new CSSFormatterContext(0);
				FormatterIndentDetector detector = new FormatterIndentDetector(offset);
				try
				{
					root.accept(context, detector);
					return detector.getLevel();
				}
				catch (Exception e)
				{
					// ignore
				}
			}
		}
		catch (Throwable t)
		{
			indent = alternativeDetectIndentationLevel(document, offset);
		}
		return indent;
	}

	/**
	 * Returns the indentation level by looking at the previous line and the formatter settings for the tabs and spaces.
	 * This is an alternative way that is invoked if the parser fails to parse the CSS content.
	 * 
	 * @param document
	 * @param offset
	 * @return
	 */
	private int alternativeDetectIndentationLevel(IDocument document, int offset)
	{
		try
		{
			int lineNumber = document.getLineOfOffset(offset);
			if (lineNumber > 0)
			{
				IRegion previousLineRegion = document.getLineInformation(lineNumber - 1);
				String text = document.get(previousLineRegion.getOffset(), previousLineRegion.getLength());
				// grab the empty string at the beginning of the text.
				int spaceChars = 0;
				int tabChars = 0;
				for (int i = 0; i < text.length(); i++)
				{
					char c = text.charAt(i);
					if (!Character.isWhitespace(c))
					{
						break;
					}
					if (c == '\n' || c == '\r')
					{
						// ignore it
						continue;
					}
					if (c == ' ')
					{
						spaceChars++;
					}
					else if (c == '\t')
					{
						tabChars++;
					}
				}
				String indentType = getIndentType();
				int indentSize = getIndentSize();
				int tabSize = getTabSize();
				if (CodeFormatterConstants.TAB.equals(indentType))
				{
					// treat the whitespace-chars as tabs
					return (spaceChars / tabSize) + tabChars + 1;
				}
				else if (CodeFormatterConstants.SPACE.equals(indentType))
				{
					// treat the tabs as spaces
					return (spaceChars + (tabSize * tabChars)) / indentSize + 1;
				}
				else
				{
					// it's Mixed
					return (spaceChars + tabChars) / indentSize + 1;
				}

			}
		}
		catch (BadLocationException e)
		{
			FormatterPlugin.logError(e);
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#format(java.lang.String, int, int, int)
	 */
	public TextEdit format(String source, int offset, int length, int indentationLevel) throws FormatterException
	{
		String input = source.substring(offset, offset + length);
		IParser parser = getParser();
		IParseState parseState = new ParseState();
		parseState.setEditState(input, null, 0, 0);
		try
		{
			IParseRootNode parseResult = parser.parse(parseState);
			if (parseResult != null)
			{
				final String output = format(input, parseResult, indentationLevel);
				if (output != null)
				{
					if (!input.equals(output))
					{
						
						//TODO need to compare the contents of the AST like comments and declarations
						parseState.setEditState(output, null, 0, 0);
						IParseRootNode outputParseResult = parser.parse(parseState);
						if (parseResult.equals(outputParseResult))
						{
							return new ReplaceEdit(offset, length, output);
						}
						else
						{
							FormatterPlugin.log(new Status(IStatus.ERROR, CSSFormatterPlugin.PLUGIN_ID, IStatus.OK,
									FormatterMessages.Formatter_formatterError, new DumpContentException(input
											+ "\n=========================\n" + output))); //$NON-NLS-1$
						}
					}
					else
					{
						return new MultiTextEdit(); // NOP
					}
				}
			}
		}
		catch (Exception e)
		{
			FormatterPlugin.logError(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentSize()
	 */
	public int getIndentSize()
	{
		return getInt(CSSFormatterConstants.FORMATTER_INDENTATION_SIZE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentType()
	 */
	public String getIndentType()
	{
		return getString(CSSFormatterConstants.FORMATTER_TAB_CHAR);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getTabSize()
	 */
	public int getTabSize()
	{
		return getInt(CSSFormatterConstants.FORMATTER_TAB_SIZE);
	}

	/**
	 * Do the actual formatting of the CSS.
	 * 
	 * @param input
	 *            The String input
	 * @param parseResult
	 *            An CSS parser result - {@link IParseNode}
	 * @param indentationLevel
	 *            The indentation level to start from
	 * @return A formatted string
	 */
	private String format(String input, IParseRootNode parseResult, int indentationLevel)
	{
		final CSSFormatterNodeBuilder builder = new CSSFormatterNodeBuilder();
		final FormatterDocument document = createFormatterDocument(input);
		IFormatterContainerNode root = builder.build(parseResult, document);
		new CSSFormatterNodeRewriter(parseResult, document).rewrite(root);
		IFormatterContext context = new CSSFormatterContext(indentationLevel);
		FormatterWriter writer = new FormatterWriter(document, lineSeparator, createIndentGenerator());
		writer.setWrapLength(getInt(CSSFormatterConstants.WRAP_COMMENTS_LENGTH));
		writer.setLinesPreserve(getInt(CSSFormatterConstants.PRESERVED_LINES));
		try
		{
			root.accept(context, writer);
			writer.flush(context);
			return writer.getOutput();
		}
		catch (Exception e)
		{
			FormatterPlugin.logError(e);
			return null;
		}
	}

	private FormatterDocument createFormatterDocument(String input)
	{
		FormatterDocument document = new FormatterDocument(input);
		document.setInt(CSSFormatterConstants.FORMATTER_TAB_SIZE, getInt(CSSFormatterConstants.FORMATTER_TAB_SIZE));
		document.setBoolean(CSSFormatterConstants.WRAP_COMMENTS, getBoolean(CSSFormatterConstants.WRAP_COMMENTS));
		document.setSet(CSSFormatterConstants.INDENT_EXCLUDED_TAGS,
				getSet(CSSFormatterConstants.INDENT_EXCLUDED_TAGS, IPreferenceDelegate.PREFERECE_DELIMITER));
		document.setSet(CSSFormatterConstants.NEW_LINES_EXCLUDED_TAGS,
				getSet(CSSFormatterConstants.NEW_LINES_EXCLUDED_TAGS, IPreferenceDelegate.PREFERECE_DELIMITER));
		for (int i = 0; i < BLANK_LINES.length; i++)
		{
			document.setInt(BLANK_LINES[i], getInt(BLANK_LINES[i]));
		}
		return document;
	}

}
