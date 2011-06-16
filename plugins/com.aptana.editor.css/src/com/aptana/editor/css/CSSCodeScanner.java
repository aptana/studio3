/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.ExtendedWordRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.css.internal.text.rules.CSSHexColorRule;
import com.aptana.editor.css.internal.text.rules.CSSIdentifierRule;
import com.aptana.editor.css.internal.text.rules.CSSImportantRule;
import com.aptana.editor.css.internal.text.rules.CSSNumberRule;
import com.aptana.editor.css.internal.text.rules.EqualOperatorWordDetector;
import com.aptana.editor.css.internal.text.rules.IdentifierWithPrefixDetector;
import com.aptana.editor.css.internal.text.rules.KeywordIdentifierDetector;
import com.aptana.editor.css.parsing.lexer.CSSTokenType;

/**
 * CSSCodeScanner
 */
@SuppressWarnings("nls")
public class CSSCodeScanner extends BufferedRuleBasedScanner
{
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

	private static final String[] STANDARD_COLORS = { "aqua", "black", "blue", "fuchsia", "gray", "green", "lime",
			"maroon", "navy", "olive", "orange", "purple", "red", "silver", "teal", "white", "yellow" };

	private static final String[] MEDIA = { "all", "aural", "braille", "embossed", "handheld", "print", "projection",
			"screen", "tty", "tv" };

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

	private static final String[] FUNCTIONS = { "rgba", "rgb", "url", "attr", "counters", "counter" };

	private static final String[] PROPERTY_NAMES = { "azimuth", "background-attachment", "background-clip",
			"background-color", "background-image", "background-origin", "background-position-x",
			"background-position-y", "background-position", "background-repeat", "background-size", "background",
			"behavior", "border-bottom-color", "border-bottom-left-radius", "border-bottom-right-radius",
			"border-bottom-style", "border-bottom-width", "border-bottom", "border-collapse", "border-color",
			"border-image-source", "border-image-slice", "border-image-width", "border-image-outset",
			"border-image-repeat", "border-image", "border-left-color", "border-left-style", "border-left-width",
			"border-left", "border-radius", "border-right-color", "border-right-style", "border-right-width",
			"border-right", "border-spacing", "border-style", "border-top-color", "border-top-left-radius",
			"border-top-right-radius", "border-top-style", "border-top-width", "border-top", "border-width", "border",
			"bottom", "box-decoration-break", "box-shadow", "caption-side", "clear", "clip", "column-count",
			"column-gap", "column-rule", "column-width", "color", "content", "counter-increment", "counter-reset",
			"cue-after", "cue-before", "cue", "cursor", "direction", "display", "elevation", "empty-cells", "float",
			"font-family", "font-size-adjust", "font-size", "font-stretch", "font-style", "font-variant",
			"font-weight", "font", "height", "left", "letter-spacing", "line-height", "list-style-image",
			"list-style-position", "list-style-type", "list-style", "margin-bottom", "margin-left", "margin-right",
			"margin-top", "marker-offset", "margin", "marks", "max-aspect-ratio", "max-color-index", "max-color",
			"max-device-aspect-ratio", "max-device-height", "max-device-width", "max-height", "max-width",
			"min-aspect-ratio", "min-color-index", "min-color", "min-device-aspect-ratio", "min-device-height",
			"min-device-width", "min-height", "min-monochrome", "min-width", "monochrome", "-moz-border-radius",
			"opacity", "orientation", "orphans", "outline-color", "outline-style", "outline-width", "outline",
			"overflow-x", "overflow-y", "overflow", "padding-bottom", "padding-left", "padding-right", "padding-top",
			"padding", "page-break-after", "page-break-before", "page-break-inside", "page", "pause-after",
			"pause-before", "pause", "pitch-range", "pitch", "play-during", "position", "quotes", "resize", "richness",
			"right", "size", "speak-header", "speak-numeral", "speak-punctuation", "speech-rate", "speak", "stress",
			"table-layout", "text-align", "text-decoration", "text-indent", "text-shadow", "text-transform", "top",
			"unicode-bidi", "vertical-align", "visibility", "voice-family", "volume", "white-space", "widows", "width",
			"word-spacing", "z-index" };

	private static final String[] PROPERTY_VALUES = { "absolute", "all-scroll", "always", "armenian", "auto",
			"baseline", "below", "bidi-override", "blink", "block", "bold", "bolder", "both", "bottom", "break-all",
			"break-word", "capitalize", "center", "char", "circle", "cjk-ideographic", "col-resize", "collapse",
			"crosshair", "dashed", "decimal-leading-zero", "decimal", "default", "disabled", "disc",
			"distribute-all-lines", "distribute-letter", "distribute-space", "distribute", "dotted", "double",
			"e-resize", "ellipsis", "fixed", "georgian", "groove", "hand", "hebrew", "help", "hidden",
			"hiragana-iroha", "hiragana", "horizontal", "ideograph-alpha", "ideograph-numeric",
			"ideograph-parenthesis", "ideograph-space", "inactive", "inherit", "inline-block", "inline", "inset",
			"inside", "inter-ideograph", "inter-word", "italic", "justify", "katakana-iroha", "katakana", "keep-all",
			"landscape", "larger", "large", "left", "lighter", "line-edge", "line-through", "line", "list-item",
			"loose", "lower-alpha", "lower-greek", "lower-latin", "lower-roman", "lowercase", "lr-tb", "ltr", "medium",
			"middle", "move", "n-resize", "ne-resize", "newspaper", "no-drop", "no-repeat", "nw-resize", "none",
			"normal", "not-allowed", "nowrap", "oblique", "outset", "outside", "overline", "pointer", "portrait",
			"progress", "relative", "repeat-x", "repeat-y", "repeat", "right", "ridge", "row-resize", "rtl",
			"s-resize", "scroll", "se-resize", "separate", "small-caps", "smaller", "solid", "square", "static",
			"strict", "super", "sw-resize", "table-footer-group", "table-header-group", "tb-rl", "text-bottom",
			"text-top", "text", "thick", "thin", "top", "transparent", "underline", "upper-alpha", "upper-latin",
			"upper-roman", "uppercase", "vertical-ideographic", "vertical-text", "visible", "w-resize", "wait",
			"whitespace", "xx-large", "xx-small", "x-small", "x-large", "zero" };

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
	 * createAtWordsRule
	 * 
	 * @return
	 */
	protected WordRule createAtWordsRule()
	{
		WordRule atRule = new WordRule(new IdentifierWithPrefixDetector('@'), createToken(CSSTokenType.AT_RULE));

		atRule.addWord("@import", createToken(CSSTokenType.IMPORT));
		atRule.addWord("@page", createToken(CSSTokenType.PAGE));
		atRule.addWord("@media", createToken(CSSTokenType.MEDIA_KEYWORD));
		atRule.addWord("@charset", createToken(CSSTokenType.CHARSET));
		atRule.addWord("@font-face", createToken(CSSTokenType.FONTFACE));
		atRule.addWord("@namespace", createToken(CSSTokenType.NAMESPACE));

		return atRule;
	}

	/**
	 * createPunctuatorsRule
	 * 
	 * @return
	 */
	protected CharacterMapRule createPunctuatorsRule()
	{
		CharacterMapRule punctuatorsRule = new CharacterMapRule();

		punctuatorsRule.add(':', createToken(CSSTokenType.COLON));
		punctuatorsRule.add(';', createToken(CSSTokenType.SEMICOLON));
		punctuatorsRule.add('{', createToken(CSSTokenType.LCURLY));
		punctuatorsRule.add('}', createToken(CSSTokenType.RCURLY));
		punctuatorsRule.add('(', createToken(CSSTokenType.LPAREN));
		punctuatorsRule.add(')', createToken(CSSTokenType.RPAREN));
		punctuatorsRule.add('%', createToken(CSSTokenType.PERCENTAGE)); // ?
		punctuatorsRule.add('[', createToken(CSSTokenType.LBRACKET));
		punctuatorsRule.add(']', createToken(CSSTokenType.RBRACKET));
		punctuatorsRule.add(',', createToken(CSSTokenType.COMMA));
		punctuatorsRule.add('+', createToken(CSSTokenType.PLUS));
		punctuatorsRule.add('*', createToken(CSSTokenType.STAR));
		punctuatorsRule.add('>', createToken(CSSTokenType.GREATER));
		punctuatorsRule.add('/', createToken(CSSTokenType.SLASH));
		punctuatorsRule.add('=', createToken(CSSTokenType.EQUAL));
		punctuatorsRule.add('-', createToken(CSSTokenType.MINUS));

		return punctuatorsRule;
	}

	/**
	 * createRules
	 * 
	 * @return
	 */
	protected List<IRule> createRules()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		// CSS Properties, values and measurements, HTML tags, media values, functions, color names
		WordRule wordRule = new WordRule(new KeywordIdentifierDetector(), Token.UNDEFINED);
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
		rules.add(createAtWordsRule());

		// !important
		rules.add(new CSSImportantRule(createToken(CSSTokenType.IMPORTANT)));

		// ignore case for font names
		wordRule = new WordRule(new KeywordIdentifierDetector(), Token.UNDEFINED, true);
		addWordsToRule(wordRule, FONT_NAMES, CSSTokenType.FONT);
		rules.add(wordRule);

		// Browser-specific property names
		rules.add(createVendorPropertyRules());

		// multi-character punctuators
		WordRule punctuatorRule2 = new WordRule(new EqualOperatorWordDetector(), Token.UNDEFINED);
		punctuatorRule2.addWord("~=", createToken(CSSTokenType.INCLUDES));
		punctuatorRule2.addWord("|=", createToken(CSSTokenType.DASHMATCH));
		punctuatorRule2.addWord("^=", createToken(CSSTokenType.BEGINS_WITH));
		punctuatorRule2.addWord("$=", createToken(CSSTokenType.ENDS_WITH));
		rules.add(punctuatorRule2);

		rules.add(createPunctuatorsRule());

		// rgb values
		rules.add(new CSSHexColorRule(createToken(CSSTokenType.RGB)));

		// ids
		rules.add(new WordRule(new IdentifierWithPrefixDetector('#'), createToken(CSSTokenType.ID)));

		rules.addAll(createScannerSpecificRules());

		rules.add(new CSSNumberRule(createToken(CSSTokenType.NUMBER)));

		// identifiers
		rules.add(new CSSIdentifierRule(createToken(CSSTokenType.IDENTIFIER)));

		return rules;
	}

	/**
	 * createScannerSpecificRules
	 * 
	 * @return
	 */
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

	/**
	 * createToken
	 * 
	 * @param scope
	 * @return
	 */
	protected IToken createToken(String scope)
	{
		return new Token(scope);
	}

	/**
	 * createVendorPropertyRules
	 * 
	 * @return
	 */
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

				return word.startsWith("-moz-") || word.startsWith("-webkit-") || word.startsWith("-ms-")
						|| word.startsWith("-o-") || word.startsWith("-atsc-") || word.startsWith("-khtml-")
						|| word.startsWith("-wap-");
			}
		};
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
}
