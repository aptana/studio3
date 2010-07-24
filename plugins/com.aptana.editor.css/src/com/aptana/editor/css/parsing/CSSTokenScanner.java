package com.aptana.editor.css.parsing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.RegexpRule;
import com.aptana.editor.common.text.rules.SingleCharacterRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.css.parsing.lexer.CSSTokens;

/**
 * @author Chris Williams
 */
public class CSSTokenScanner extends BufferedRuleBasedScanner
{

	private static final String KEYWORD_IMPORT = "@import"; //$NON-NLS-1$
	private static final String KEYWORD_PAGE = "@page"; //$NON-NLS-1$
	private static final String KEYWORD_MEDIA = "@media"; //$NON-NLS-1$
	private static final String KEYWORD_CHARSET = "@charset"; //$NON-NLS-1$
	private static final String WORD_INCLUDES = "~="; //$NON-NLS-1$
	private static final String WORD_DASHMATCH = "|="; //$NON-NLS-1$

	@SuppressWarnings("nls")
	private static final String[] DEPRECATED_COLORS = new String[] { "aliceblue", "antiquewhite", "aquamarine",
			"azure", "beige", "bisque", "blanchedalmond", "blueviolet", "brown", "burlywood", "cadetblue",
			"chartreuse", "chocolate", "coral", "cornflowerblue", "cornsilk", "crimson", "cyan", "darkblue",
			"darkcyan", "darkgoldenrod", "darkgray", "darkgreen", "darkgrey", "darkkhaki", "darkmagenta",
			"darkolivegreen", "darkorange", "darkorchid", "darkred", "darksalmon", "darkseagreen", "darkslateblue",
			"darkslategray", "darkslategrey", "darkturquoise", "darkviolet", "deeppink", "deepskyblue", "dimgray",
			"dimgrey", "dodgerblue", "firebrick", "floralwhite", "forestgreen", "gainsboro", "ghostwhite", "gold",
			"goldenrod", "greenyellow", "grey", "honeydew", "hotpink", "indianred", "indigo", "ivory", "khaki",
			"lavender", "lavenderblush", "lawngreen", "lemonchiffon", "lightblue", "lightcoral", "lightcyan",
			"lightgoldenrodyellow", "lightgray", "lightgreen", "lightgrey", "lightpink", "lightsalmon",
			"lightseagreen", "lightskyblue", "lightslategray", "lightslategrey", "lightsteelblue", "lightyellow",
			"limegreen", "linen", "magenta", "mediumaquamarine", "mediumblue", "mediumorchid", "mediumpurple",
			"mediumseagreen", "mediumslateblue", "mediumspringgreen", "mediumturquoise", "mediumvioletred",
			"midnightblue", "mintcream", "mistyrose", "moccasin", "navajowhite", "oldlace", "olivedrab", "orangered",
			"orchid", "palegoldenrod", "palegreen", "paleturquoise", "palevioletred", "papayawhip", "peachpuff",
			"peru", "pink", "plum", "powderblue", "rosybrown", "royalblue", "saddlebrown", "salmon", "sandybrown",
			"seagreen", "seashell", "sienna", "skyblue", "slateblue", "slategray", "slategrey", "snow", "springgreen",
			"steelblue", "tan", "thistle", "tomato", "turquoise", "violet", "wheat", "whitesmoke", "yellowgreen" };

	@SuppressWarnings("nls")
	private static final String[] STANDARD_COLORS = { "aqua", "black", "blue", "fuchsia", "gray", "green", "lime",
			"maroon", "navy", "olive", "orange", "purple", "red", "silver", "teal", "white", "yellow" };

	@SuppressWarnings("nls")
	private static final String[] HTML_TAGS = { "a", "abbr", "acronym", "address", "area", "b", "base", "big",
			"blockquote", "body", "br", "button", "caption", "cite", "code", "col", "colgroup", "dd", "del", "dfn",
			"div", "dl", "dt", "em", "embed", "fieldset", "form", "frame", "frameset", "head", "hr", "html", "h1",
			"h2", "h3", "h4", "h5", "h6", "i", "iframe", "img", "input", "ins", "kbd", "label", "legend", "li", "link",
			"map", "meta", "noframes", "noscript", "object", "ol", "optgroup", "option", "p", "param", "pre", "q",
			"samp", "script", "select", "small", "span", "strike", "strong", "style", "sub", "sup", "tbody", "td",
			"textarea", "tfoot", "th", "thead", "title", "tr", "tt", "ul", "var", "header", "nav", "section",
			"article", "footer", "aside", "audio", "video", "canvas", "hgroup" };

	@SuppressWarnings("nls")
	private static final String[] MEDIAS = { "all", "aural", "braille", "embossed", "handheld", "print", "projection",
			"screen", "tty", "tv" };

	@SuppressWarnings("nls")
	private static final String[] FUNCTIONS = { "rgb", "attr", "counters", "counter" };

	@SuppressWarnings("nls")
	private static final String[] PROPERTY_NAMES = { "azimuth", "background-attachment", "background-color",
			"background-image", "background-position-x", "background-position-y", "background-position",
			"background-repeat", "background", "border-bottom-color", "border-bottom-style", "border-bottom-width",
			"border-bottom", "border-collapse", "border-color", "border-left-color", "border-left-style",
			"border-left-width", "border-left", "border-right-color", "border-right-style", "border-right-width",
			"border-right", "border-spacing", "border-style", "border-top-color", "border-top-style",
			"border-top-width", "border-top", "border-width", "border", "bottom", "caption-side", "clear", "clip",
			"color", "content", "counter-increment", "counter-reset", "cue-after", "cue-before", "cue", "cursor",
			"direction", "display", "elevation", "empty-cells", "float", "font-family", "font-size-adjust",
			"font-size", "font-stretch", "font-style", "font-variant", "font-weight", "font", "height", "left",
			"letter-spacing", "line-height", "list-style-image", "list-style-position", "list-style-type",
			"list-style", "margin-bottom", "margin-left", "margin-right", "margin-top", "marker-offset", "margin",
			"marks", "max-height", "max-width", "min-height", "min-width", "-moz-border-radius", "opacity", "orphans",
			"outline-color", "outline-style", "outline-width", "outline", "overflow-x", "overflow-y", "overflow",
			"padding-bottom", "padding-left", "padding-right", "padding-top", "padding", "page-break-after",
			"page-break-before", "page-break-inside", "page", "pause-after", "pause-before", "pause", "pitch-range",
			"pitch", "play-during", "position", "quotes", "richness", "right", "size", "speak-header", "speak-numeral",
			"speak-punctuation", "speech-rate", "speak", "stress", "table-layout", "text-align", "text-decoration",
			"text-indent", "text-shadow", "text-transform", "top", "unicode-bidi", "vertical-align", "visibility",
			"voice-family", "volume", "white-space", "widows", "width", "word-spacing", "z-index" };

	@SuppressWarnings("nls")
	private static final String[] FONT_NAMES = { "arial", "century", "comic", "courier", "garamond", "geneva",
			"georgia", "helvetica", "impact", "lucida", "monaco", "symbol", "system", "tahoma", "times", "trebuchet",
			"utopia", "verdana", "webdings", "sans-serif", "serif", "monospace" };

	/**
	 * A flag to turn on or off the optimization of eligible regexp rules. Seems to make a measurable difference on
	 * large files.
	 */
	private static final boolean OPTIMIZE_REGEXP_RULES = true;

	/**
	 * CodeScanner
	 */
	public CSSTokenScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// generic whitespace rule
		rules.add(new WhitespaceRule(new WhitespaceDetector()));
		// comments
		rules.add(new MultiLineRule("/*", "*/", new Token(getTokenName(CSSTokens.COMMENT)), (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$
		// quoted strings
		IToken token = new Token(getTokenName(CSSTokens.STRING));
		rules.add(new SingleLineRule("\"", "\"", token, '\\')); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new SingleLineRule("\'", "\'", token, '\\')); //$NON-NLS-1$ //$NON-NLS-2$

		// normal words
		IWordDetector identifierDetector = new KeywordIdentifierDetector();
		WordRule wordRule = new WordRule(identifierDetector, Token.UNDEFINED);
		String identifier = getTokenName(CSSTokens.IDENTIFIER);
		addWordsToRule(wordRule, HTML_TAGS, identifier);
		addWordsToRule(wordRule, MEDIAS, identifier);
		addWordsToRule(wordRule, FUNCTIONS, identifier);
		rules.add(wordRule);

		// keywords that start with @
		wordRule = new WordRule(new AtWordDetector(), Token.UNDEFINED);
		wordRule.addWord(KEYWORD_CHARSET, createToken(getTokenName(CSSTokens.CHARSET)));
		wordRule.addWord(KEYWORD_IMPORT, createToken(getTokenName(CSSTokens.IMPORT)));
		wordRule.addWord(KEYWORD_MEDIA, createToken(getTokenName(CSSTokens.MEDIA)));
		wordRule.addWord(KEYWORD_PAGE, createToken(getTokenName(CSSTokens.PAGE)));
		rules.add(wordRule);

		// property words and colors
		wordRule = new WordRule(identifierDetector, Token.UNDEFINED);
		addWordsToRule(wordRule, PROPERTY_NAMES, identifier);
		addWordsToRule(wordRule, STANDARD_COLORS, getTokenName(CSSTokens.COLOR));
		addWordsToRule(wordRule, DEPRECATED_COLORS, getTokenName(CSSTokens.COLOR));
		rules.add(wordRule);

		// ignore case
		wordRule = new WordRule(identifierDetector, Token.UNDEFINED, true);
		addWordsToRule(wordRule, FONT_NAMES, identifier);
		rules.add(wordRule);

		// special character keywords
		wordRule = new WordRule(new SpecialCharacterWordDetector(), Token.UNDEFINED);
		wordRule.addWord(WORD_INCLUDES, createToken(getTokenName(CSSTokens.INCLUDES)));
		wordRule.addWord(WORD_DASHMATCH, createToken(getTokenName(CSSTokens.DASHMATCH)));
		rules.add(wordRule);

		// curly braces
		rules.add(new SingleCharacterRule('{', createToken(getTokenName(CSSTokens.LCURLY))));
		rules.add(new SingleCharacterRule('}', createToken(getTokenName(CSSTokens.RCURLY))));
		// colon
		rules.add(new SingleCharacterRule(':', createToken(getTokenName(CSSTokens.COLON))));
		// semicolon
		rules.add(new SingleCharacterRule(';', createToken(getTokenName(CSSTokens.SEMICOLON))));
		// comma
		rules.add(new SingleCharacterRule(',', createToken(getTokenName(CSSTokens.COMMA))));
		// parens
		rules.add(new SingleCharacterRule('(', createToken(getTokenName(CSSTokens.FUNCTION))));
		rules.add(new SingleCharacterRule(')', createToken(getTokenName(CSSTokens.RPAREN))));
		// brackets
		rules.add(new SingleCharacterRule('[', createToken(getTokenName(CSSTokens.LBRACKET))));
		rules.add(new SingleCharacterRule(']', createToken(getTokenName(CSSTokens.RBRACKET))));
		// plus
		rules.add(new SingleCharacterRule('+', createToken(getTokenName(CSSTokens.PLUS))));
		// star
		rules.add(new SingleCharacterRule('*', createToken(getTokenName(CSSTokens.STAR))));
		// greater
		rules.add(new SingleCharacterRule('>', createToken(getTokenName(CSSTokens.GREATER))));
		// forward slash
		rules.add(new SingleCharacterRule('/', createToken(getTokenName(CSSTokens.FORWARD_SLASH))));
		// equal
		rules.add(new SingleCharacterRule('=', createToken(getTokenName(CSSTokens.EQUAL))));

		// Now onto to more expensive regexp rules
		// rgb values
		rules.add(new RegexpRule("#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})\\b", createToken(getTokenName(CSSTokens.COLOR)), //$NON-NLS-1$
				OPTIMIZE_REGEXP_RULES));

		// url
		rules.add(new RegexpRule("url\\([^\\)]*\\)", createToken(getTokenName(CSSTokens.URL)), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$
		// em
		rules
				.add(new RegexpRule(
						"(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)em", createToken(getTokenName(CSSTokens.EMS)))); //$NON-NLS-1$
		// length
		rules
				.add(new RegexpRule(
						"(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)(px|cm|mm|in|pt|pc)", createToken(getTokenName(CSSTokens.LENGTH)))); //$NON-NLS-1$
		// percentage
		rules.add(new RegexpRule(
				"(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)%", createToken(getTokenName(CSSTokens.PERCENTAGE)))); //$NON-NLS-1$
		// angle
		rules.add(new RegexpRule(
				"(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)(deg|rad|grad)", createToken(getTokenName(CSSTokens.ANGLE)))); //$NON-NLS-1$
		// ex
		rules
				.add(new RegexpRule(
						"(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)ex", createToken(getTokenName(CSSTokens.EXS)))); //$NON-NLS-1$
		// frequency
		rules
				.add(new RegexpRule(
						"(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)([Hh]z|k[Hh]z)", createToken(getTokenName(CSSTokens.FREQUENCY)))); //$NON-NLS-1$
		// time
		rules.add(new RegexpRule(
				"(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)(ms|s)", createToken(getTokenName(CSSTokens.TIME)))); //$NON-NLS-1$
		// numbers
		rules.add(new RegexpRule(
				"(\\-|\\+)?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)", createToken(getTokenName(CSSTokens.NUMBER)))); //$NON-NLS-1$

		// ids
		rules.add(new RegexpRule("#[_a-zA-Z0-9-]+", createToken(getTokenName(CSSTokens.HASH)), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$
		// classes
		rules
				.add(new RegexpRule(
						"\\.[_a-zA-Z0-9-]+", createToken(getTokenName(CSSTokens.CLASS)), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$

		// minus
		rules
				.add(new RegexpRule(
						"-(?=\\s*[0-9\\.])", createToken(getTokenName(CSSTokens.MINUS)), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$

		// !important
		rules.add(new RegexpRule(
				"!\\s*important", createToken(getTokenName(CSSTokens.IMPORTANT)), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$
		// @ rules
		rules.add(new RegexpRule(
				"@[_a-zA-Z0-9-]+", createToken(getTokenName(CSSTokens.AT_KEYWORD)), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$

		// identifiers
		rules.add(new RegexpRule("[_a-zA-Z0-9-]+", createToken(identifier), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$

		setRules(rules.toArray(new IRule[rules.size()]));
	}

	private void addWordsToRule(WordRule wordRule, String[] words, String tokenType)
	{
		IToken token = createToken(tokenType);
		for (String word : words)
		{
			wordRule.addWord(word, token);
		}
	}

	protected IToken createToken(String string)
	{
		return new Token(string);
	}

	private static String getTokenName(short token)
	{
		return CSSTokens.getTokenName(token);
	}

	/**
	 * Detects words consisting only of letters, digits, '-', and '_'. Must start with letter
	 * 
	 * @author cwilliams
	 */
	protected static class KeywordIdentifierDetector implements IWordDetector
	{
		@Override
		public boolean isWordPart(char c)
		{
			return Character.isLetterOrDigit(c) || c == '-' || c == '_';
		}

		@Override
		public boolean isWordStart(char c)
		{
			return Character.isLetter(c);
		}
	}

	private static final class AtWordDetector implements IWordDetector
	{

		@Override
		public boolean isWordPart(char c)
		{
			return Character.isLetter(c) || c == '@';
		}

		@Override
		public boolean isWordStart(char c)
		{
			return c == '@';
		}
	}

	private static final class SpecialCharacterWordDetector implements IWordDetector
	{

		@Override
		public boolean isWordPart(char c)
		{
			return !Character.isLetterOrDigit(c);
		}

		@Override
		public boolean isWordStart(char c)
		{
			return !Character.isLetterOrDigit(c);
		}
	}
}
