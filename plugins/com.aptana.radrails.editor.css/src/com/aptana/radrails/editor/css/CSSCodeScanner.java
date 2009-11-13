package com.aptana.radrails.editor.css;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.radrails.editor.common.RegexpRule;
import com.aptana.radrails.editor.common.WhitespaceDetector;
import com.aptana.radrails.editor.common.theme.ThemeUtil;

/**
 * @author Chris Williams
 */
public class CSSCodeScanner extends BufferedRuleBasedScanner {

    private static final String[] MEASUREMENTS = new String[] { "em", "ex", "px", "cm", "mm", "in", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
            "pt", "pc", "deg", "rad", "grad", "ms", "s", "hz", "khz" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$

    /**
     * CodeScanner
     */
    public CSSCodeScanner() {
        IToken keyword = createToken("keyword.other.unit.css"); //$NON-NLS-1$
        IToken ids = createToken("entity.other.attribute-name.id.css"); //$NON-NLS-1$
        IToken classes = createToken("entity.other.attribute-name.class.css"); //$NON-NLS-1$
        IToken propertyValue = createToken("support.constant.property-value.css"); //$NON-NLS-1$
        // FIXME name of keyword should be in token!
        IToken rule = createToken("keyword.control.at-rule.media.css"); //$NON-NLS-1$
        List<IRule> rules = new ArrayList<IRule>();

        // Add generic whitespace rule.
        rules.add(new WhitespaceRule(new WhitespaceDetector()));

        // Add word rule for measurements
        WordRule wordRule = new WordRule(new WordDetector(), Token.UNDEFINED);
        for (String word : MEASUREMENTS) {
            wordRule.addWord(word, keyword);
        }
        rules.add(wordRule);
        rules.add(new RegexpRule("(#)([0-9a-fA-F]{3}|[0-9a-fA-F]{6})\\b", //$NON-NLS-1$
                createToken("constant.other.color.rgb-value.css"))); //$NON-NLS-1$
        rules.add(new RegexpRule("#[_a-zA-Z0-9-]+", ids)); //$NON-NLS-1$
        rules.add(new RegexpRule("\\.[_a-zA-Z0-9-]+", classes)); //$NON-NLS-1$
        rules.add(new RegexpRule("@[_a-zA-Z0-9-]+", rule)); //$NON-NLS-1$

        rules.add(new RegexpRule("(\\-|\\+)?\\s*[0-9]+(\\.[0-9]+)?", //$NON-NLS-1$
                createToken("constant.numeric.css"))); //$NON-NLS-1$

        rules
                .add(new RegexpRule(
                        "\\b(a|abbr|acronym|address|area|b|base|big|blockquote|body|br|button|caption|cite|code|col|colgroup|dd|del|dfn|div|dl|dt|em|fieldset|form|frame|frameset|(h[1-6])|head|hr|html|i|iframe|img|input|ins|kbd|label|legend|li|link|map|meta|noframes|noscript|object|ol|optgroup|option|p|param|pre|q|samp|script|select|small|span|strike|strong|style|sub|sup|table|tbody|td|textarea|tfoot|th|thead|title|tr|tt|ul|var)\\b", //$NON-NLS-1$
                        createToken("entity.name.tag.css"))); //$NON-NLS-1$

        rules.add(new RegexpRule(
                "\\b(all|aural|braille|embossed|handheld|print|projection|screen|tty|tv)\\b", //$NON-NLS-1$
                createToken("support.constant.media.css"))); //$NON-NLS-1$

        rules.add(new RegexpRule("%", createToken("keyword.other.unit.css"))); //$NON-NLS-1$ //$NON-NLS-2$
        rules
                .add(new RegexpRule(
                        "\\b(absolute|all-scroll|always|armenian|auto|baseline|below|bidi-override|block|bold|bolder|both|bottom|break-all|break-word|capitalize|center|char|circle|cjk-ideographic|col-resize|collapse|crosshair|dashed|decimal-leading-zero|decimal|default|disabled|disc|distribute-all-lines|distribute-letter|distribute-space|distribute|dotted|double|e-resize|ellipsis|fixed|georgian|groove|hand|hebrew|help|hidden|hiragana-iroha|hiragana|horizontal|ideograph-alpha|ideograph-numeric|ideograph-parenthesis|ideograph-space|inactive|inherit|inline-block|inline|inset|inside|inter-ideograph|inter-word|italic|justify|katakana-iroha|katakana|keep-all|left|lighter|line-edge|line-through|line|list-item|loose|lower-alpha|lower-greek|lower-latin|lower-roman|lowercase|lr-tb|ltr|medium|middle|move|n-resize|ne-resize|newspaper|no-drop|no-repeat|nw-resize|none|normal|not-allowed|nowrap|oblique|outset|outside|overline|pointer|progress|relative|repeat-x|repeat-y|repeat|right|ridge|row-resize|rtl|s-resize|scroll|se-resize|separate|small-caps|solid|square|static|strict|super|sw-resize|table-footer-group|table-header-group|tb-rl|text-bottom|text-top|text|thick|thin|top|transparent|underline|upper-alpha|upper-latin|upper-roman|uppercase|vertical-ideographic|vertical-text|visible|w-resize|wait|whitespace|zero)\\b", //$NON-NLS-1$
                        propertyValue));
        rules.add(new RegexpRule("!important", propertyValue)); //$NON-NLS-1$
        rules
                .add(new RegexpRule(
                        "\\b(azimuth|background-attachment|background-color|background-image|background-position|background-repeat|background|border-bottom-color|border-bottom-style|border-bottom-width|border-bottom|border-collapse|border-color|border-left-color|border-left-style|border-left-width|border-left|border-right-color|border-right-style|border-right-width|border-right|border-spacing|border-style|border-top-color|border-top-style|border-top-width|border-top|border-width|border|bottom|caption-side|clear|clip|color|content|counter-increment|counter-reset|cue-after|cue-before|cue|cursor|direction|display|elevation|empty-cells|float|font-family|font-size-adjust|font-size|font-stretch|font-style|font-variant|font-weight|font|height|left|letter-spacing|line-height|list-style-image|list-style-position|list-style-type|list-style|margin-bottom|margin-left|margin-right|margin-top|marker-offset|margin|marks|max-height|max-width|min-height|min-width|-moz-border-radius|opacity|orphans|outline-color|outline-style|outline-width|outline|overflow(-[xy])?|padding-bottom|padding-left|padding-right|padding-top|padding|page-break-after|page-break-before|page-break-inside|page|pause-after|pause-before|pause|pitch-range|pitch|play-during|position|quotes|richness|right|size|speak-header|speak-numeral|speak-punctuation|speech-rate|speak|stress|table-layout|text-align|text-decoration|text-indent|text-shadow|text-transform|top|unicode-bidi|vertical-align|visibility|voice-family|volume|white-space|widows|width|word-spacing|z-index)\\b", //$NON-NLS-1$
                        createToken("support.type.property-name.css"))); //$NON-NLS-1$
        rules
                .add(new RegexpRule(
                        "(\\b(?i:arial|century|comic|courier|garamond|georgia|helvetica|impact|lucida|symbol|system|tahoma|times|trebuchet|utopia|verdana|webdings|sans-serif|serif|monospace)\\b)", //$NON-NLS-1$
                        createToken("support.constant.font-name.css"))); //$NON-NLS-1$
        setRules(rules.toArray(new IRule[rules.size()]));
    }

    private IToken createToken(String string) {
        return ThemeUtil.getToken(string);
    }
}
