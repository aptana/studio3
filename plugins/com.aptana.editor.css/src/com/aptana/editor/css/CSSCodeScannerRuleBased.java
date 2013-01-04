/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.css.core.parsing.CSSTokenType;
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

/**
 * CSSCodeScannerRuleBased Note: this class is deprecated but still kept around because SassCodeScanner uses it (but
 * {@link CSSCodeScannerFlex} should be used instead if possible as it's considerably faster).
 */
@Deprecated
@SuppressWarnings("nls")
public class CSSCodeScannerRuleBased extends BufferedRuleBasedScanner
{
	/* default */static final class VendorPropertyWordRule extends ExtendedWordRule
	{
		private VendorPropertyWordRule(IWordDetector detector, IToken defaultToken, boolean ignoreCase)
		{
			super(detector, defaultToken, ignoreCase);
		}

		@Override
		public boolean wordOK(String word, ICharacterScanner scanner)
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
	}

	public static final String KEYWORD_MEDIA = "@media";

	public static final String[] DEPRECATED_COLORS = new String[] { "aliceblue", "antiquewhite", "aquamarine", "azure",
			"beige", "bisque", "blanchedalmond", "blueviolet", "brown", "burlywood", "cadetblue", "chartreuse",
			"chocolate", "coral", "cornflowerblue", "cornsilk", "crimson", "cyan", "darkblue", "darkcyan",
			"darkgoldenrod", "darkgray", "darkgreen", "darkgrey", "darkkhaki", "darkmagenta", "darkolivegreen",
			"darkorange", "darkorchid", "darkred", "darksalmon", "darkseagreen", "darkslateblue", "darkslategray",
			"darkslategrey", "darkturquoise", "darkviolet", "deeppink", "deepskyblue", "dimgray", "dimgrey",
			"dodgerblue", "firebrick", "floralwhite", "forestgreen", "gainsboro", "ghostwhite", "gold", "goldenrod",
			"greenyellow", "grey", "honeydew", "hotpink", "indianred", "indigo", "ivory", "khaki", "lavender",
			"lavenderblush", "lawngreen", "lemonchiffon", "lightblue", "lightcoral", "lightcyan",
			"lightgoldenrodyellow", "lightgray", "lightgreen", "lightgrey", "lightpink", "lightsalmon",
			"lightseagreen", "lightskyblue", "lightslategray", "lightslategrey", "lightsteelblue", "lightyellow",
			"limegreen", "linen", "magenta", "mediumaquamarine", "mediumblue", "mediumorchid", "mediumpurple",
			"mediumseagreen", "mediumslateblue", "mediumspringgreen", "mediumturquoise", "mediumvioletred",
			"midnightblue", "mintcream", "mistyrose", "moccasin", "navajowhite", "oldlace", "olivedrab", "orangered",
			"orchid", "palegoldenrod", "palegreen", "paleturquoise", "palevioletred", "papayawhip", "peachpuff",
			"peru", "pink", "plum", "powderblue", "rosybrown", "royalblue", "saddlebrown", "salmon", "sandybrown",
			"seagreen", "seashell", "sienna", "skyblue", "slateblue", "slategray", "slategrey", "snow", "springgreen",
			"steelblue", "tan", "thistle", "tomato", "turquoise", "violet", "wheat", "whitesmoke", "yellowgreen" };

	public static final String[] STANDARD_COLORS = { "aqua", "black", "blue", "fuchsia", "gray", "green", "lime",
			"maroon", "navy", "olive", "orange", "purple", "red", "silver", "teal", "white", "yellow" };

	public static final String[] MEDIA = { "all", "aural", "braille", "embossed", "handheld", "print", "projection",
			"screen", "tty", "tv" };

	// FIXME Turn "em" back on when we can add hack to determine if we're inside or outside a rule
	public static final String[] HTML_TAGS = { "a", "abbr", "acronym", "address", "area", "b", "base", "big",
			"blockquote", "body", "br", "button", "caption", "cite", "code", "col", "colgroup", "dd", "del", "details",
			"dfn", "div", "dl", "dt", /* "em", */"embed", "fieldset", "figcaption", "figure", "form", "frame",
			"frameset", "head", "hr", "html", "h1", "h2", "h3", "h4", "h5", "h6", "i", "iframe", "img", "input", "ins",
			"kbd", "label", "legend", "li", "link", "map", "mark", "menu", "meta", "noframes", "noscript", "object",
			"ol", "optgroup", "option", "p", "param", "pre", "q", "samp", "script", "select", "small", "span",
			"strike", "strong", "style", "sub", "summary", "sup", "table", "tbody", "td", "textarea", "tfoot", "th",
			"thead", "time", "title", "tr", "tt", "ul", "var", "header", "nav", "section", "article", "footer",
			"aside", "audio", "video", "canvas", "hgroup" };

	public static final String[] FUNCTIONS = { "rgba", "rgb", "url", "attr", "counters", "counter", "linear-gradient" };

	public static final String[] PROPERTY_NAMES = { "alignment-adjust", "alignment-baseline", "azimuth",
			"background-attachment", "background-clip", "background-color", "background-image", "background-origin",
			"background-position-x", "background-position-y", "background-position", "background-quantity",
			"background-repeat", "background-size", "background-spacing", "background", "behavior",
			"border-bottom-color", "border-bottom-left-radius", "border-bottom-right-radius", "border-bottom-style",
			"border-bottom-width", "border-bottom", "border-collapse", "border-color", "border-image-source",
			"border-image-slice", "border-image-width", "border-image-outset", "border-image-repeat", "border-image",
			"border-left-color", "border-left-style", "border-left-width", "border-left", "border-radius",
			"border-right-color", "border-right-style", "border-right-width", "border-right", "border-spacing",
			"border-style", "border-top-color", "border-top-left-radius", "border-top-right-radius",
			"border-top-style", "border-top-width", "border-top", "border-width", "border", "bottom",
			"box-decoration-break", "box-shadow", "box-sizing", "caption-side", "clear", "clip", "column-count",
			"column-gap", "column-rule", "column-width", "color", "content", "counter-increment", "counter-reset",
			"cue-after", "cue-before", "cue", "cursor", "direction", "display", "elevation", "empty-cells", "filter",
			"fit", "fit-position", "float", "font-family", "font-size-adjust", "font-size", "font-stretch",
			"font-style", "font-variant", "font-weight", "font", "height", "left", "letter-spacing", "line-height",
			"list-style-image", "list-style-position", "list-style-type", "list-style", "margin-bottom", "margin-left",
			"margin-right", "margin-top", "marker-offset", "margin", "marks", "max-aspect-ratio", "max-color-index",
			"max-color", "max-device-aspect-ratio", "max-device-height", "max-device-width", "max-height", "max-width",
			"min-aspect-ratio", "min-color-index", "min-color", "min-device-aspect-ratio", "min-device-height",
			"min-device-width", "min-height", "min-monochrome", "min-width", "monochrome", "-moz-border-radius",
			"offx", "offy", "opacity", "orientation", "orphans", "outline-color", "outline-offset", "outline-style",
			"outline-width", "outline", "overflow-x", "overflow-y", "overflow", "padding-bottom", "padding-left",
			"padding-right", "padding-top", "padding", "page-break-after", "page-break-before", "page-break-inside",
			"page", "pause-after", "pause-before", "pause", "pitch-range", "pitch", "play-during", "position",
			"quotes", "resize", "richness", "right", "size", "speak-header", "speak-numeral", "speak-punctuation",
			"speech-rate", "speak", "src", "stress", "table-layout", "text-align", "text-decoration", "text-indent",
			"text-overflow", "text-shadow", "text-transform", "top", "transform", "transition", "unicode-bidi",
			"user-select", "vertical-align", "visibility", "voice-family", "volume", "weight", "white-space", "widows",
			"width", "word-break", "word-spacing", "word-wrap", "z-index", "zoom" };

	public static final String[] PROPERTY_VALUES = { "absolute", "all-scroll", "always", "armenian", "auto",
			"baseline", "below", "bidi-override", "blink", "block", "bold", "bolder", "border-box", "both", "bottom",
			"break-all", "break-word", "capitalize", "center", "char", "circle", "cjk-ideographic", "col-resize",
			"collapse", "content-box", "crosshair", "dashed", "decimal-leading-zero", "decimal", "default", "disabled",
			"disc", "distribute-all-lines", "distribute-letter", "distribute-space", "distribute", "dotted", "double",
			"e-resize", "ease-in-out", "ease-in", "ease-out", "ease", "ellipsis", "fixed", "georgian", "groove",
			"hand", "hebrew", "help", "hidden", "hiragana-iroha", "hiragana", "horizontal", "ideograph-alpha",
			"ideograph-numeric", "ideograph-parenthesis", "ideograph-space", "inactive", "inherit", "inline-block",
			"inline", "inset", "inside", "inter-ideograph", "inter-word", "italic", "justify", "katakana-iroha",
			"katakana", "keep-all", "landscape", "larger", "large", "left", "lighter", "line-edge", "line-through",
			"linear", "line", "list-item", "loose", "lower-alpha", "lower-greek", "lower-latin", "lower-roman",
			"lowercase", "lr-tb", "ltr", "medium", "middle", "move", "n-resize", "ne-resize", "newspaper", "no-drop",
			"no-repeat", "nw-resize", "none", "normal", "not-allowed", "nowrap", "oblique", "outset", "outside",
			"overline", "pointer", "portrait", "progress", "relative", "repeat-x", "repeat-y", "repeat", "right",
			"ridge", "row-resize", "rtl", "s-resize", "scroll", "se-resize", "separate", "small-caps", "smaller",
			"solid", "square", "static", "strict", "super", "sw-resize", "table-footer-group", "table-header-group",
			"tb-rl", "text-bottom", "text-top", "text", "thick", "thin", "top", "transparent", "underline",
			"upper-alpha", "upper-latin", "upper-roman", "uppercase", "vertical-ideographic", "vertical-text",
			"vertical", "visible", "w-resize", "wait", "whitespace", "xx-large", "xx-small", "x-small", "x-large",
			"zero" };

	public static final String[] FONT_NAMES = { "arial", "clean", "century", "comic", "courier", "garamond", "geneva",
			"georgia", "helvetica", "impact", "lucida", "monaco", "symbol", "system", "tahoma", "times", "trebuchet",
			"utopia", "verdana", "webdings", "sans-serif", "serif", "monospace" };

	public static final Pattern CURLY_MEDIA_PATTERN = Pattern.compile("([{}]|" + KEYWORD_MEDIA + ")");

	/**
	 * Keep the level of curlies...
	 */
	private int fCurlyState;
	private boolean fInMedia;
	protected boolean fInPropertyValue;
	protected boolean fInSelector;

	/**
	 * CodeScanner
	 */
	public CSSCodeScannerRuleBased()
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
		atRule.addWord(KEYWORD_MEDIA, createToken(CSSTokenType.MEDIA_KEYWORD));
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
		return new Token(type);
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

	public static final VendorPropertyWordRule VENDOR_WORD_RULE = new VendorPropertyWordRule(
			new IdentifierWithPrefixDetector('-'), new Token(CSSTokenType.PROPERTY), true);

	/**
	 * createVendorPropertyRules
	 * 
	 * @return
	 */
	private ExtendedWordRule createVendorPropertyRules()
	{
		return VENDOR_WORD_RULE;
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

	@Override
	public IToken nextToken()
	{
		// FIXME We're also not generating the meta scopes for properties and selectors!
		// "meta.property-name.css", "meta.property-value.css", and "meta.selector.css"
		IToken token = super.nextToken();
		if (token.isEOF())
		{
			return token;
		}
		Object tokenData = token.getData();
		if (CSSTokenType.MEDIA_KEYWORD == tokenData)
		{
			this.fInMedia = true;
			this.fCurlyState = 0;
		}
		else if (CSSTokenType.LCURLY == tokenData)
		{
			// Use a different punctuation scope if opening @media
			if (insideMedia() && this.fCurlyState == 0)
			{
				token = createToken(CSSTokenType.LCURLY_MEDIA);
			}
			this.fCurlyState++;
		}
		else if (CSSTokenType.RCURLY == tokenData)
		{
			this.fInPropertyValue = false;
		}
		else if (CSSTokenType.PROPERTY == tokenData)
		{
			this.fInSelector = false;
		}
		else if (CSSTokenType.COLON == tokenData)
		{
			this.fInPropertyValue = true;
		}
		else if (CSSTokenType.CLASS == tokenData || CSSTokenType.ID == tokenData || CSSTokenType.STAR == tokenData
				|| CSSTokenType.ELEMENT == tokenData)
		{
			this.fInSelector = true;
		}

		StringBuilder builder = new StringBuilder();
		// Media META scope
		if (insideMedia())
		{
			builder.append(CSSTokenType.META_MEDIA.getScope()).append(' ');
		}
		// Ruleset META scope
		if (insideRule())
		{
			builder.append(CSSTokenType.META_RULE.getScope()).append(' ');
		}
		// Selector META scope
		else if (this.fInSelector)
		{
			builder.append(CSSTokenType.META_SELECTOR.getScope()).append(' ');
		}
		// Property value META scope
		if (insidePropertyValue())
		{
			builder.append(CSSTokenType.META_PROPERTY_VALUE.getScope()).append(' ');
		}

		// Constant property value, like "top" or "left", but not inside property value. Assume it's a property name!
		if (!this.fInPropertyValue && CSSTokenType.VALUE == tokenData)
		{
			token = createToken(CSSTokenType.PROPERTY);
		}
		// left curly ends selector meta scope
		else if (CSSTokenType.LCURLY == tokenData)
		{
			this.fInSelector = false;
		}
		// right curly might end media/rule meta scopes
		else if (CSSTokenType.RCURLY == tokenData)
		{
			this.fCurlyState--;
			if (this.fCurlyState <= 0 && insideMedia())
			{
				token = createToken(CSSTokenType.RCURLY_MEDIA);
				this.fInMedia = false;
			}
		}
		// semicolon ends property value meta scope
		else if (CSSTokenType.SEMICOLON == tokenData)
		{
			this.fInPropertyValue = false;
		}

		if (token.isOther())
		{
			// Grab data again, because we may have changed the token above...
			tokenData = token.getData();
			if (tokenData != null)
			{
				if (tokenData instanceof CSSTokenType)
				{
					builder.append(((CSSTokenType) tokenData).getScope());
				}
				else if (tokenData instanceof String)
				{
					builder.append((String) tokenData);
				}
			}
			else
			{
				if (builder.length() > 0)
				{
					// remove the trailing space
					builder.deleteCharAt(builder.length() - 1);
				}
			}
		}
		else if (token.isWhitespace())
		{
			if (builder.length() > 0)
			{
				// remove the trailing space
				builder.deleteCharAt(builder.length() - 1);
			}
			else
			{
				// return whitespace token unchanged...
				return token;
			}
		}
		return createToken(builder.toString());
	}

	private boolean insidePropertyValue()
	{
		return this.fInPropertyValue;
	}

	@Override
	public void setRange(IDocument document, int offset, int length)
	{
		super.setRange(document, offset, length);

		this.fCurlyState = 0;
		this.fInMedia = false;
		this.fInPropertyValue = false;
		this.fInSelector = false;
		if (offset > 0)
		{
			String previous = null;
			try
			{
				ITypedRegion[] partitions = fDocument.computePartitioning(0, offset);
				for (ITypedRegion region : partitions)
				{
					// skip strings and comments
					if (CSSSourceConfiguration.MULTILINE_COMMENT.equals(region.getType())
							|| CSSSourceConfiguration.STRING_DOUBLE.equals(region.getType())
							|| CSSSourceConfiguration.STRING_SINGLE.equals(region.getType()))
					{
						continue;
					}
					previous = fDocument.get(region.getOffset(), region.getLength());
					// Calculate curly nesting level and whether we're inside media
					Matcher m = CURLY_MEDIA_PATTERN.matcher(previous);
					while (m.find())
					{
						String found = m.group();
						if ("{".equals(found))
						{
							this.fCurlyState++;
						}
						else if ("}".equals(found))
						{
							this.fCurlyState--;
							if (this.fCurlyState <= 0 && insideMedia())
							{
								this.fInMedia = false;
							}
						}
						else if (KEYWORD_MEDIA.equals(found))
						{
							this.fInMedia = true;
							this.fCurlyState = 0;
						}
					}
				}
			}
			catch (BadLocationException e)
			{
				// ignore
			}
		}
	}

	private boolean insideRule()
	{
		if (insideMedia())
		{
			// media adds a curly nesting level!
			return this.fCurlyState > 1;
		}
		return this.fCurlyState > 0;
	}

	private boolean insideMedia()
	{
		return this.fInMedia;
	}

}
