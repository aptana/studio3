package com.aptana.editor.css;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.ExtendedWordRule;
import com.aptana.editor.common.text.rules.RegexpRule;
import com.aptana.editor.common.text.rules.SingleCharacterRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.css.parsing.lexer.CSSTokenType;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;

/**
 * @author Chris Williams
 */
public class CSSCodeScanner extends BufferedRuleBasedScanner
{
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

	@SuppressWarnings("nls")
	private static final String[] MEASUREMENTS = new String[] { "em", "ex", "px", "cm", "mm", "in", "pt", "pc", "deg",
			"rad", "grad", "ms", "s", "hz", "khz" };

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
	private static final String[] MEDIA = { "all", "aural", "braille", "embossed", "handheld", "print", "projection",
			"screen", "tty", "tv" };

	@SuppressWarnings("nls")
	private static final String[] HTML_TAGS = { "a", "abbr", "acronym", "address", "area", "b", "base", "big",
			"blockquote", "body", "br", "button", "caption", "cite", "code", "col", "colgroup", "dd", "del", "dfn",
			"div", "dl", "dt", "em", "embed", "fieldset", "form", "frame", "frameset", "head", "hr", "html", "h1",
			"h2", "h3", "h4", "h5", "h6", "i", "iframe", "img", "input", "ins", "kbd", "label", "legend", "li", "link",
			"map", "meta", "noframes", "noscript", "object", "ol", "optgroup", "option", "p", "param", "pre", "q",
			"samp", "script", "select", "small", "span", "strike", "strong", "style", "sub", "sup", "table", "tbody",
			"td", "textarea", "tfoot", "th", "thead", "title", "tr", "tt", "ul", "var", "header", "nav", "section",
			"article", "footer", "aside", "audio", "video", "canvas", "hgroup" };

	@SuppressWarnings("nls")
	private static final String[] FUNCTIONS = { "rgb", "url", "attr", "counters", "counter" };

	@SuppressWarnings("nls")
	private static final String[] PROPERTY_NAMES = { "azimuth", "background-attachment", "background-clip",
			"background-color", "background-image", "background-origin", "background-position-x",
			"background-position-y", "background-position", "background-repeat", "background-size", "background",
			"border-bottom-color", "border-bottom-style", "border-bottom-width", "border-bottom", "border-collapse",
			"border-color", "border-image-source", "border-image-slice", "border-image-width", "border-image-outset",
			"border-image-repeat", "border-image", "border-left-color", "border-left-style", "border-left-width",
			"border-left", "border-radius", "border-right-color", "border-right-style", "border-right-width",
			"border-right", "border-spacing", "border-style", "border-top-color", "border-top-style",
			"border-top-width", "border-top", "border-width", "border", "bottom", "box-decoration-break", "box-shadow",
			"caption-side", "clear", "clip", "color", "content", "counter-increment", "counter-reset", "cue-after",
			"cue-before", "cue", "cursor", "direction", "display", "elevation", "empty-cells", "float", "font-family",
			"font-size-adjust", "font-size", "font-stretch", "font-style", "font-variant", "font-weight", "font",
			"height", "left", "letter-spacing", "line-height", "list-style-image", "list-style-position",
			"list-style-type", "list-style", "margin-bottom", "margin-left", "margin-right", "margin-top",
			"marker-offset", "margin", "marks", "max-height", "max-width", "min-height", "min-width",
			"-moz-border-radius", "opacity", "orphans", "outline-color", "outline-style", "outline-width", "outline",
			"overflow-x", "overflow-y", "overflow", "padding-bottom", "padding-left", "padding-right", "padding-top",
			"padding", "page-break-after", "page-break-before", "page-break-inside", "page", "pause-after",
			"pause-before", "pause", "pitch-range", "pitch", "play-during", "position", "quotes", "richness", "right",
			"size", "speak-header", "speak-numeral", "speak-punctuation", "speech-rate", "speak", "stress",
			"table-layout", "text-align", "text-decoration", "text-indent", "text-shadow", "text-transform", "top",
			"unicode-bidi", "vertical-align", "visibility", "voice-family", "volume", "white-space", "widows", "width",
			"word-spacing", "z-index" };

	@SuppressWarnings("nls")
	private static final String[] PROPERTY_VALUES = { "absolute", "all-scroll", "always", "armenian", "auto",
			"baseline", "below", "bidi-override", "blink", "block", "bold", "bolder", "both", "bottom", "break-all",
			"break-word", "capitalize", "center", "char", "circle", "cjk-ideographic", "col-resize", "collapse",
			"crosshair", "dashed", "decimal-leading-zero", "decimal", "default", "disabled", "disc",
			"distribute-all-lines", "distribute-letter", "distribute-space", "distribute", "dotted", "double",
			"e-resize", "ellipsis", "fixed", "georgian", "groove", "hand", "hebrew", "help", "hidden",
			"hiragana-iroha", "hiragana", "horizontal", "ideograph-alpha", "ideograph-numeric",
			"ideograph-parenthesis", "ideograph-space", "inactive", "inherit", "inline-block", "inline", "inset",
			"inside", "inter-ideograph", "inter-word", "italic", "justify", "katakana-iroha", "katakana", "keep-all",
			"larger", "large", "left", "lighter", "line-edge", "line-through", "line", "list-item", "loose",
			"lower-alpha", "lower-greek", "lower-latin", "lower-roman", "lowercase", "lr-tb", "ltr", "medium",
			"middle", "move", "n-resize", "ne-resize", "newspaper", "no-drop", "no-repeat", "nw-resize", "none",
			"normal", "not-allowed", "nowrap", "oblique", "outset", "outside", "overline", "pointer", "progress",
			"relative", "repeat-x", "repeat-y", "repeat", "right", "ridge", "row-resize", "rtl", "s-resize", "scroll",
			"se-resize", "separate", "small-caps", "smaller", "solid", "square", "static", "strict", "super",
			"sw-resize", "table-footer-group", "table-header-group", "tb-rl", "text-bottom", "text-top", "text",
			"thick", "thin", "top", "transparent", "underline", "upper-alpha", "upper-latin", "upper-roman",
			"uppercase", "vertical-ideographic", "vertical-text", "visible", "w-resize", "wait", "whitespace",
			"xx-large", "xx-small", "x-small", "x-large", "zero" };

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
	public CSSCodeScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		IWordDetector identifierDetector = new KeywordIdentifierDetector();
		WordRule wordRule = new WordRule(identifierDetector, Token.UNDEFINED);
		addWordsToRule(wordRule, getPropertyNames(), CSSTokenType.PROPERTY);
		addWordsToRule(wordRule, PROPERTY_VALUES, CSSTokenType.VALUE);
		addWordsToRule(wordRule, MEASUREMENTS, CSSTokenType.UNIT);
		rules.add(wordRule);

		// normal words
		wordRule = new WordRule(identifierDetector, Token.UNDEFINED);
		addWordsToRule(wordRule, HTML_TAGS, CSSTokenType.ELEMENT);
		addWordsToRule(wordRule, MEDIA, CSSTokenType.MEDIA);
		addWordsToRule(wordRule, FUNCTIONS, CSSTokenType.FUNCTION);
		addWordsToRule(wordRule, STANDARD_COLORS, CSSTokenType.COLOR);
		addWordsToRule(wordRule, DEPRECATED_COLORS, CSSTokenType.DEPRECATED_COLOR);
		rules.add(wordRule);

		// ignore case
		wordRule = new WordRule(identifierDetector, Token.UNDEFINED, true);
		addWordsToRule(wordRule, FONT_NAMES, CSSTokenType.FONT);
		rules.add(wordRule);

		// Browser-specific property names
		IWordDetector browserSpecificProperties = new KeywordIdentifierDetector()
		{
			@Override
			public boolean isWordStart(char c)
			{
				return c == '-';
			}
		};
		rules.add(new ExtendedWordRule(browserSpecificProperties, createToken(CSSTokenType.PROPERTY), true)
				{
					@Override
					protected boolean wordOK(String word, ICharacterScanner scanner)
					{
						// Table 1. Vendor Extension Prefixes
						// Prefix Organisation
						// -ms- Microsoft
						// mso- Microsoft Office
						// -moz- Mozilla Foundation (Gecko-based browsers)
						// -o- Opera Software
						// -atsc- Advanced Television Standards Committee
						// -wap- The WAP Forum
						// -webkit- Safari (and other WebKit-based browsers)
						// -khtml-

						return word.startsWith("-moz-") || word.startsWith("-webkit-") || word.startsWith("-ms-") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								|| word.startsWith("-o-") || word.startsWith("-atsc-") || word.startsWith("-khtml-") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								|| word.startsWith("-wap-"); //$NON-NLS-1$
					}
				});

		// curly braces
		rules.add(new SingleCharacterRule('{', createToken(CSSTokenType.CURLY_BRACE)));
		rules.add(new SingleCharacterRule('}', createToken(CSSTokenType.CURLY_BRACE)));
		// colon
		rules.add(new SingleCharacterRule(':', createToken(CSSTokenType.COLON)));
		// semicolon
		rules.add(new SingleCharacterRule(';', createToken(CSSTokenType.SEMICOLON)));
		// parens
		// HACK these rules are more correct, but for now we eat up the stuff inside parens too, to avoid weirder
		// coloring
		//		rules.add(new SingleCharacterRule('(', createToken("punctuation.section.function.css")));
		//		rules.add(new SingleCharacterRule(')', createToken("punctuation.section.function.css")));
		rules.add(new RegexpRule("\\([^)]*?\\)", createToken(CSSTokenType.ARGS), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$

		// Now onto to more expensive regexp rules
		// rgb values
		rules.add(new RegexpRule("#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})\\b", //$NON-NLS-1$
				createToken(CSSTokenType.RGB), OPTIMIZE_REGEXP_RULES));
		// ids
		rules.add(new RegexpRule("#[_a-zA-Z0-9-]+", createToken(CSSTokenType.ID), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$
		// classes
		rules.add(new RegexpRule("\\.[_a-zA-Z0-9-]+", createToken(CSSTokenType.CLASS), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$

		// numbers
		rules.add(new RegexpRule("(\\-|\\+)?\\s*[0-9]+(\\.[0-9]+)?", //$NON-NLS-1$
				createToken(CSSTokenType.NUMBER)));

		// %
		rules.add(new SingleCharacterRule('%', createToken(CSSTokenType.UNIT)));
		// !important
		rules.add(new RegexpRule("!important", createToken(CSSTokenType.VALUE), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$
		// FIXME name of keyword should be in token!
		// @ rules
		rules.add(new RegexpRule("@[_a-zA-Z0-9-]+", createToken(CSSTokenType.AT_RULE), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$

		// identifiers
		rules.add(new RegexpRule("[_a-zA-Z0-9-]+", createToken(CSSTokenType.IDENTIFIER), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$

		setRules(rules.toArray(new IRule[rules.size()]));
	}

	/**
	 * addWordsToRule
	 * 
	 * @param wordRule
	 * @param words
	 * @param tokenType
	 */
	private void addWordsToRule(WordRule wordRule, String[] words, CSSTokenType tokenType)
	{
		IToken token = createToken(tokenType);

		for (String word : words)
		{
			wordRule.addWord(word, token);
		}
	}

	/**
	 * createToken
	 * 
	 * @param type
	 * @return
	 */
	private IToken createToken(CSSTokenType type)
	{
		return this.createToken(type.getScope());
	}

	/**
	 * createToken
	 * 
	 * @param string
	 * @return
	 */
	protected IToken createToken(String string)
	{
		return getThemeManager().getToken(string);
	}

	/**
	 * getPropertyNames
	 * 
	 * @return
	 */
	protected String[] getPropertyNames()
	{
		return PROPERTY_NAMES;
	}

	/**
	 * getThemeManager
	 * 
	 * @return
	 */
	protected IThemeManager getThemeManager()
	{
		return ThemePlugin.getDefault().getThemeManager();
	}
}
