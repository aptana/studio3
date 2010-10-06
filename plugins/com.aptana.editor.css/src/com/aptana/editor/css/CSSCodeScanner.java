/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.ExtendedWordRule;
import com.aptana.editor.common.text.rules.SingleCharacterRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.css.internal.text.rules.AtWordDetector;
import com.aptana.editor.css.internal.text.rules.IdentifierWithPrefixDetector;
import com.aptana.editor.css.internal.text.rules.KeywordIdentifierDetector;
import com.aptana.editor.css.internal.text.rules.SpecialCharacterWordDetector;
import com.aptana.editor.css.parsing.lexer.CSSTokenType;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;

/**
 * @author Chris Williams
 */
public class CSSCodeScanner extends BufferedRuleBasedScanner
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
	private static final String[] MEDIA = { "all", "aural", "braille", "embossed", "handheld", "print", "projection",
			"screen", "tty", "tv" };

	@SuppressWarnings("nls")
	private static final String[] HTML_TAGS = { "a", "abbr", "acronym", "address", "area", "b", "base", "big",
			"blockquote", "body", "br", "button", "caption", "cite", "code", "col",
			"colgroup",
			"dd",
			"del",
			"dfn",
			// FIXME Turn "em" back on when we can add hack to determine if we're inside or outside a rule
			"div", "dl", "dt", /* "em", */"embed", "fieldset", "form", "frame", "frameset", "head", "hr", "html",
			"h1", "h2", "h3", "h4", "h5", "h6", "i", "iframe", "img", "input", "ins", "kbd", "label", "legend", "li",
			"link", "map", "meta", "noframes", "noscript", "object", "ol", "optgroup", "option", "p", "param", "pre",
			"q", "samp", "script", "select", "small", "span", "strike", "strong", "style", "sub", "sup", "table",
			"tbody", "td", "textarea", "tfoot", "th", "thead", "title", "tr", "tt", "ul", "var", "header", "nav",
			"section", "article", "footer", "aside", "audio", "video", "canvas", "hgroup" };

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
	 * CodeScanner
	 */
	public CSSCodeScanner()
	{
		List<IRule> rules = createRules();
		setRules(rules.toArray(new IRule[rules.size()]));
	}

	protected List<IRule> createRules()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		IWordDetector identifierDetector = new KeywordIdentifierDetector();
		// CSS Properties, values and measurements, HTML tags, media values, functions, color names
		WordRule wordRule = new WordRule(identifierDetector, Token.UNDEFINED);
		addWordsToRule(wordRule, getPropertyNames(), CSSTokenType.PROPERTY);
		addWordsToRule(wordRule, PROPERTY_VALUES, CSSTokenType.VALUE);
		addWordsToRule(wordRule, HTML_TAGS, CSSTokenType.ELEMENT);
		addWordsToRule(wordRule, MEDIA, CSSTokenType.MEDIA);
		addWordsToRule(wordRule, STANDARD_COLORS, CSSTokenType.COLOR);
		addWordsToRule(wordRule, DEPRECATED_COLORS, CSSTokenType.DEPRECATED_COLOR);
		rules.add(wordRule);

		// classes
		rules.add(new WordRule(new IdentifierWithPrefixDetector('.'), createToken(CSSTokenType.CLASS)));

		// keywords that start with @
		wordRule = new WordRule(new AtWordDetector(), createToken(CSSTokenType.AT_RULE));
		wordRule.addWord(KEYWORD_CHARSET, createToken(CSSTokenType.CHARSET));
		wordRule.addWord(KEYWORD_IMPORT, createToken(CSSTokenType.IMPORT));
		wordRule.addWord(KEYWORD_MEDIA, createToken(CSSTokenType.MEDIA_KEYWORD));
		wordRule.addWord(KEYWORD_PAGE, createToken(CSSTokenType.PAGE));
		rules.add(wordRule);

		// !important
		rules.add(new ExtendedWordRule(new IWordDetector()
		{

			public boolean isWordStart(char c)
			{
				return c == '!';
			}

			public boolean isWordPart(char c)
			{
				return isWordStart(c) || Character.isLetterOrDigit(c) || Character.isWhitespace(c);
			}
		}, createToken(CSSTokenType.IMPORTANT), false)
		{

			private Pattern pattern;

			@Override
			protected boolean wordOK(String word, ICharacterScanner scanner)
			{
				if (pattern == null)
				{
					pattern = Pattern.compile("!\\s*important"); //$NON-NLS-1$
				}
				return pattern.matcher(word).matches();
			}
		});

		// ignore case for font names
		wordRule = new WordRule(identifierDetector, Token.UNDEFINED, true);
		addWordsToRule(wordRule, FONT_NAMES, CSSTokenType.FONT);
		rules.add(wordRule);

		// Browser-specific property names
		rules.add(createVendorPropertyRules());

		// special character keywords
		wordRule = new WordRule(new SpecialCharacterWordDetector(), Token.UNDEFINED);
		wordRule.addWord(WORD_INCLUDES, createToken(CSSTokenType.INCLUDES));
		wordRule.addWord(WORD_DASHMATCH, createToken(CSSTokenType.DASHMATCH));
		rules.add(wordRule);

		rules.addAll(createPunctuationRules());

		// rgb values
		rules.add(createRGBRule());

		// ids
		rules.add(new WordRule(new IdentifierWithPrefixDetector('#'), createToken(CSSTokenType.ID)));

		rules.addAll(createScannerSpecificRules());

		rules.add(createNumberRule());

		// identifiers
		rules.add(new ExtendedWordRule(new KeywordIdentifierDetector(), createToken(CSSTokenType.IDENTIFIER), false)
		{

			@Override
			protected boolean wordOK(String word, ICharacterScanner scanner)
			{
				if (word.charAt(0) == '-')
				{
					return word.length() > 1;
				}
				return true;
			}
		});

		rules.add(new SingleCharacterRule('-', createToken(CSSTokenType.MINUS)));

		return rules;
	}

	private ExtendedWordRule createVendorPropertyRules()
	{
		return new ExtendedWordRule(new IdentifierWithPrefixDetector('-'), createToken(CSSTokenType.PROPERTY), true)
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
		};
	}

	private ExtendedWordRule createRGBRule()
	{
		return new ExtendedWordRule(new IdentifierWithPrefixDetector('#'), createToken(CSSTokenType.RGB), false)
		{

			@Override
			protected boolean wordOK(String word, ICharacterScanner scanner)
			{
				if (word.length() != 4 && word.length() != 7)
				{
					return false;
				}
				word = word.toLowerCase();
				for (int i = 1; i < word.length(); i++)
				{
					char c = word.charAt(i);
					if (Character.isDigit(c))
					{
						continue;
					}
					if ('a' <= c && c <= 'f') // a-f
					{
						continue;
					}
					return false;
				}
				return true;
			}
		};
	}

	@SuppressWarnings("nls")
	protected Collection<? extends IRule> createScannerSpecificRules()
	{
		List<IRule> rules = new ArrayList<IRule>();
		WordRule wordRule = new WordRule(new KeywordIdentifierDetector(), Token.UNDEFINED);
		wordRule.addWord("em", createToken(CSSTokenType.EMS));
		wordRule.addWord("ex", createToken(CSSTokenType.EXS));
		wordRule.addWord("px", createToken(CSSTokenType.LENGTH));
		wordRule.addWord("cm", createToken(CSSTokenType.LENGTH));
		wordRule.addWord("mm", createToken(CSSTokenType.LENGTH));
		wordRule.addWord("in", createToken(CSSTokenType.LENGTH));
		wordRule.addWord("pt", createToken(CSSTokenType.LENGTH));
		wordRule.addWord("pc", createToken(CSSTokenType.LENGTH));
		wordRule.addWord("deg", createToken(CSSTokenType.ANGLE));
		wordRule.addWord("rad", createToken(CSSTokenType.ANGLE));
		wordRule.addWord("grad", createToken(CSSTokenType.ANGLE));
		wordRule.addWord("ms", createToken(CSSTokenType.TIME));
		wordRule.addWord("s", createToken(CSSTokenType.TIME));
		wordRule.addWord("hz", createToken(CSSTokenType.FREQUENCY));
		wordRule.addWord("khz", createToken(CSSTokenType.FREQUENCY));
		wordRule.addWord("Hz", createToken(CSSTokenType.FREQUENCY));
		wordRule.addWord("kHz", createToken(CSSTokenType.FREQUENCY));
		addWordsToRule(wordRule, FUNCTIONS, CSSTokenType.FUNCTION);
		rules.add(wordRule);
		return rules;
	}

	protected IRule createNumberRule()
	{
		return new ExtendedWordRule(new IWordDetector()
		{

			public boolean isWordStart(char c)
			{
				return c == '-' || c == '+' || c == '.' || Character.isDigit(c);
			}

			public boolean isWordPart(char c)
			{
				return c == '.' || Character.isDigit(c);
			}
		}, createToken(CSSTokenType.NUMBER), false)
		{

			private Pattern pattern;

			@Override
			protected boolean wordOK(String word, ICharacterScanner scanner)
			{
				if (pattern == null)
				{
					pattern = Pattern.compile("(-|\\+)?\\s*[0-9]+(\\.[0-9]+)?"); //$NON-NLS-1$
				}
				return pattern.matcher(word).matches();
			}
		};
	}

	protected List<IRule> createPunctuationRules()
	{
		List<IRule> rules = new ArrayList<IRule>();
		// curly braces
		rules.add(new SingleCharacterRule('{', createToken(CSSTokenType.LCURLY)));
		rules.add(new SingleCharacterRule('}', createToken(CSSTokenType.RCURLY)));
		// colon
		rules.add(new SingleCharacterRule(':', createToken(CSSTokenType.COLON)));
		// semicolon
		rules.add(new SingleCharacterRule(';', createToken(CSSTokenType.SEMICOLON)));
		// %
		rules.add(new SingleCharacterRule('%', createToken(CSSTokenType.PERCENTAGE)));
		// comma
		rules.add(new SingleCharacterRule(',', createToken(CSSTokenType.COMMA)));
		// parens
		rules.add(new SingleCharacterRule('(', createToken(CSSTokenType.LPAREN)));
		rules.add(new SingleCharacterRule(')', createToken(CSSTokenType.RPAREN)));
		// brackets
		rules.add(new SingleCharacterRule('[', createToken(CSSTokenType.LBRACKET)));
		rules.add(new SingleCharacterRule(']', createToken(CSSTokenType.RBRACKET)));
		// plus
		rules.add(new SingleCharacterRule('+', createToken(CSSTokenType.PLUS)));
		// star
		rules.add(new SingleCharacterRule('*', createToken(CSSTokenType.STAR)));
		// greater
		rules.add(new SingleCharacterRule('>', createToken(CSSTokenType.GREATER)));
		// forward slash
		rules.add(new SingleCharacterRule('/', createToken(CSSTokenType.SLASH)));
		// equal
		rules.add(new SingleCharacterRule('=', createToken(CSSTokenType.EQUAL)));
		return rules;
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
	protected IToken createToken(CSSTokenType type)
	{
		return createToken(type.getScope());
	}

	protected IToken createToken(String scope)
	{
		return new Token(scope);
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
