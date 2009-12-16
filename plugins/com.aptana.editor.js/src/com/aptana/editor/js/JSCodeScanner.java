/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
package com.aptana.editor.js;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.RegexpRule;
import com.aptana.editor.common.WhitespaceDetector;
import com.aptana.editor.common.WordDetector;
import com.aptana.editor.common.theme.ThemeUtil;

/**
 * @author Kevin Lindsey
 * @author cwilliams
 */
public class JSCodeScanner extends RuleBasedScanner
{

	private static final String[] KEYWORD_OPERATORS = new String[] { "delete", "in", "instanceof", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"new", "typeof", "with" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private static final String[] KEYWORD_CONTROL = new String[] { "break", "case", "catch", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"continue", "default", "do", "else", "finally", "for", "goto", "if", "import", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
			"package", "return", "switch", "throw", "try", "while" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

	@SuppressWarnings("nls")
	private static final String[] STORAGE_TYPES = new String[] { "boolean", "byte", "char", "class", "double", "enum",
			"float", "function", "int", "interface", "long", "short", "var", "void" };

	@SuppressWarnings("nls")
	private static final String[] STORAGE_MODIFIERS = new String[] { "const", "export", "extends", "final",
			"implements", "native", "private", "protected", "public", "static", "synchronized", "throws", "transient",
			"volatile" };

	@SuppressWarnings("nls")
	private static final String[] SUPPORT_CLASSES = new String[] { "Anchor", "Applet", "Area", "Array", "Boolean",
			"Button", "Checkbox", "Date", "document", "event", "FileUpload", "Form", "Frame", "Function", "Hidden",
			"History", "Image", "JavaArray", "JavaClass", "JavaObject", "JavaPackage", "java", "Layer", "Link",
			"Location", "Math", "MimeType", "Number", "navigator", "netscape", "Object", "Option", "Packages",
			"Password", "Plugin", "Radio", "RegExp", "Reset", "Select", "String", "Style", "Submit", "screen", "sun",
			"Text", "Textarea", "window", "XMLHttpRequest" };

	@SuppressWarnings("nls")
	private static final String[] SUPPORT_DOM_CONSTANTS = new String[] { "ELEMENT_NODE", "ATTRIBUTE_NODE", "TEXT_NODE",
			"CDATA_SECTION_NODE", "ENTITY_REFERENCE_NODE", "ENTITY_NODE", "PROCESSING_INSTRUCTION_NODE",
			"COMMENT_NODE", "DOCUMENT_NODE", "DOCUMENT_TYPE_NODE", "DOCUMENT_FRAGMENT_NODE", "NOTATION_NODE",
			"INDEX_SIZE_ERR", "DOMSTRING_SIZE_ERR", "HIERARCHY_REQUEST_ERR", "WRONG_DOCUMENT_ERR",
			"INVALID_CHARACTER_ERR", "NO_DATA_ALLOWED_ERR", "NO_MODIFICATION_ALLOWED_ERR", "NOT_FOUND_ERR",
			"NOT_SUPPORTED_ERR", "INUSE_ATTRIBUTE_ERR" };

	/**
	 * CodeScanner
	 */
	public JSCodeScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		rules
				.add(new RegexpRule(
						"!|\\$|%|&|\\*|\\-\\-|\\-|\\+\\+|\\+|~|===|==|=|!=|!==|<=|>=|<<=|>>=|>>>=|<>|<|>|!|&&|\\|\\||\\?\\:|\\*=|(?<!\\()/=|%=|\\+=|\\-=|&=|\\^=|\\b(in|instanceof|new|delete|typeof|void)\\b", //$NON-NLS-1$
						ThemeUtil.getToken("keyword.operator.js"))); //$NON-NLS-1$
		rules.add(new RegexpRule("\\b((0(x|X)[0-9a-fA-F]+)|([0-9]+(\\.[0-9]+)?))\\b", ThemeUtil //$NON-NLS-1$
				.getToken("constant.numeric.js"))); //$NON-NLS-1$
		rules.add(new RegexpRule("\\.(warn|info|log|error|time|timeEnd|assert)\\b", ThemeUtil //$NON-NLS-1$
				.getToken("support.function.js.firebug"))); //$NON-NLS-1$

		rules
				.add(new RegexpRule(
						"\\b(s(ub(stringData|mit)|plitText|e(t(NamedItem|Attribute(Node)?)|lect))|has(ChildNodes|Feature)|namedItem|c(l(ick|o(se|neNode))|reate(C(omment|DATASection|aption)|T(Head|extNode|Foot)|DocumentFragment|ProcessingInstruction|E(ntityReference|lement)|Attribute))|tabIndex|i(nsert(Row|Before|Cell|Data)|tem)|open|delete(Row|C(ell|aption)|T(Head|Foot)|Data)|focus|write(ln)?|a(dd|ppend(Child|Data))|re(set|place(Child|Data)|move(NamedItem|Child|Attribute(Node)?)?)|get(NamedItem|Element(sBy(Name|TagName)|ById)|Attribute(Node)?)|blur)\\b(?=\\()", //$NON-NLS-1$
						ThemeUtil.getToken("support.function.dom.js"))); //$NON-NLS-1$
		rules
				.add(new RegexpRule(
						"\\b(s(h(ift|ow(Mod(elessDialog|alDialog)|Help))|croll(X|By(Pages|Lines)?|Y|To)?|t(op|rike)|i(n|zeToContent|debar|gnText)|ort|u(p|b(str(ing)?)?)|pli(ce|t)|e(nd|t(Re(sizable|questHeader)|M(i(nutes|lliseconds)|onth)|Seconds|Ho(tKeys|urs)|Year|Cursor|Time(out)?|Interval|ZOptions|Date|UTC(M(i(nutes|lliseconds)|onth)|Seconds|Hours|Date|FullYear)|FullYear|Active)|arch)|qrt|lice|avePreferences|mall)|h(ome|andleEvent)|navigate|c(har(CodeAt|At)|o(s|n(cat|textual|firm)|mpile)|eil|lear(Timeout|Interval)?|a(ptureEvents|ll)|reate(StyleSheet|Popup|EventObject))|t(o(GMTString|S(tring|ource)|U(TCString|pperCase)|Lo(caleString|werCase))|est|a(n|int(Enabled)?))|i(s(NaN|Finite)|ndexOf|talics)|d(isableExternalCapture|ump|etachEvent)|u(n(shift|taint|escape|watch)|pdateCommands)|j(oin|avaEnabled)|p(o(p|w)|ush|lugins.refresh|a(ddings|rse(Int|Float)?)|r(int|ompt|eference))|e(scape|nableExternalCapture|val|lementFromPoint|x(p|ec(Script|Command)?))|valueOf|UTC|queryCommand(State|Indeterm|Enabled|Value)|f(i(nd|le(ModifiedDate|Size|CreatedDate|UpdatedDate)|xed)|o(nt(size|color)|rward)|loor|romCharCode)|watch|l(ink|o(ad|g)|astIndexOf)|a(sin|nchor|cos|t(tachEvent|ob|an(2)?)|pply|lert|b(s|ort))|r(ou(nd|teEvents)|e(size(By|To)|calc|turnValue|place|verse|l(oad|ease(Capture|Events)))|andom)|g(o|et(ResponseHeader|M(i(nutes|lliseconds)|onth)|Se(conds|lection)|Hours|Year|Time(zoneOffset)?|Da(y|te)|UTC(M(i(nutes|lliseconds)|onth)|Seconds|Hours|Da(y|te)|FullYear)|FullYear|A(ttention|llResponseHeaders)))|m(in|ove(B(y|elow)|To(Absolute)?|Above)|ergeAttributes|a(tch|rgins|x))|b(toa|ig|o(ld|rderWidths)|link|ack))\\b(?=\\()", //$NON-NLS-1$
						ThemeUtil.getToken("support.function.js"))); //$NON-NLS-1$
		// FIXME This rule shouldn't actually match the leading period, but we have no way to capture just the rest as
		// the token
		rules
				.add(new RegexpRule(
						"\\.(s(ystemLanguage|cr(ipts|ollbars|een(X|Y|Top|Left))|t(yle(Sheets)?|atus(Text|bar)?)|ibling(Below|Above)|ource|uffixes|e(curity(Policy)?|l(ection|f)))|h(istory|ost(name)?|as(h|Focus))|y|X(MLDocument|SLDocument)|n(ext|ame(space(s|URI)|Prop))|M(IN_VALUE|AX_VALUE)|c(haracterSet|o(n(structor|trollers)|okieEnabled|lorDepth|mp(onents|lete))|urrent|puClass|l(i(p(boardData)?|entInformation)|osed|asses)|alle(e|r)|rypto)|t(o(olbar|p)|ext(Transform|Indent|Decoration|Align)|ags)|SQRT(1_2|2)|i(n(ner(Height|Width)|put)|ds|gnoreCase)|zIndex|o(scpu|n(readystatechange|Line)|uter(Height|Width)|p(sProfile|ener)|ffscreenBuffering)|NEGATIVE_INFINITY|d(i(splay|alog(Height|Top|Width|Left|Arguments)|rectories)|e(scription|fault(Status|Ch(ecked|arset)|View)))|u(ser(Profile|Language|Agent)|n(iqueID|defined)|pdateInterval)|_content|p(ixelDepth|ort|ersonalbar|kcs11|l(ugins|atform)|a(thname|dding(Right|Bottom|Top|Left)|rent(Window|Layer)?|ge(X(Offset)?|Y(Offset)?))|r(o(to(col|type)|duct(Sub)?|mpter)|e(vious|fix)))|e(n(coding|abledPlugin)|x(ternal|pando)|mbeds)|v(isibility|endor(Sub)?|Linkcolor)|URLUnencoded|P(I|OSITIVE_INFINITY)|f(ilename|o(nt(Size|Family|Weight)|rmName)|rame(s|Element)|gColor)|E|whiteSpace|l(i(stStyleType|n(eHeight|kColor))|o(ca(tion(bar)?|lName)|wsrc)|e(ngth|ft(Context)?)|a(st(M(odified|atch)|Index|Paren)|yer(s|X)|nguage))|a(pp(MinorVersion|Name|Co(deName|re)|Version)|vail(Height|Top|Width|Left)|ll|r(ity|guments)|Linkcolor|bove)|r(ight(Context)?|e(sponse(XML|Text)|adyState))|global|x|m(imeTypes|ultiline|enubar|argin(Right|Bottom|Top|Left))|L(N(10|2)|OG(10E|2E))|b(o(ttom|rder(RightWidth|BottomWidth|Style|Color|TopWidth|LeftWidth))|ufferDepth|elow|ackground(Color|Image)))\\b", //$NON-NLS-1$
						ThemeUtil.getToken("support.constant.js"))); //$NON-NLS-1$
		// FIXME This rule shouldn't actually match the leading period, but we have no way to capture just the rest as
		// the token
		rules
				.add(new RegexpRule(
						"\\.(s(hape|ystemId|c(heme|ope|rolling)|ta(ndby|rt)|ize|ummary|pecified|e(ctionRowIndex|lected(Index)?)|rc)|h(space|t(tpEquiv|mlFor)|e(ight|aders)|ref(lang)?)|n(o(Resize|tation(s|Name)|Shade|Href|de(Name|Type|Value)|Wrap)|extSibling|ame)|c(h(ildNodes|Off|ecked|arset)?|ite|o(ntent|o(kie|rds)|de(Base|Type)?|l(s|Span|or)|mpact)|ell(s|Spacing|Padding)|l(ear|assName)|aption)|t(ype|Bodies|itle|Head|ext|a(rget|gName)|Foot)|i(sMap|ndex|d|m(plementation|ages))|o(ptions|wnerDocument|bject)|d(i(sabled|r)|o(c(type|umentElement)|main)|e(clare|f(er|ault(Selected|Checked|Value)))|at(eTime|a))|useMap|p(ublicId|arentNode|r(o(file|mpt)|eviousSibling))|e(n(ctype|tities)|vent|lements)|v(space|ersion|alue(Type)?|Link|Align)|URL|f(irstChild|orm(s)?|ace|rame(Border)?)|width|l(ink(s)?|o(ngDesc|wSrc)|a(stChild|ng|bel))|a(nchors|c(ce(ssKey|pt(Charset)?)|tion)|ttributes|pplets|l(t|ign)|r(chive|eas)|xis|Link|bbr)|r(ow(s|Span|Index)|ules|e(v|ferrer|l|adOnly))|m(ultiple|e(thod|dia)|a(rgin(Height|Width)|xLength))|b(o(dy|rder)|ackground|gColor))\\b", //$NON-NLS-1$
						ThemeUtil.getToken("support.constant.dom.js"))); //$NON-NLS-1$
		rules
				.add(new RegexpRule(
						"\\bon(R(ow(s(inserted|delete)|e(nter|xit))|e(s(ize(start|end)?|et)|adystatechange))|Mouse(o(ut|ver)|down|up|move)|B(efore(cut|deactivate|u(nload|pdate)|p(aste|rint)|editfocus|activate)|lur)|S(croll|top|ubmit|elect(start|ionchange)?)|H(over|elp)|C(hange|ont(extmenu|rolselect)|ut|ellchange|l(ick|ose))|D(eactivate|ata(setc(hanged|omplete)|available)|r(op|ag(start|over|drop|en(ter|d)|leave)?)|blclick)|Unload|P(aste|ropertychange)|Error(update)?|Key(down|up|press)|Focus|Load|A(ctivate|fter(update|print)|bort))\\b", //$NON-NLS-1$
						ThemeUtil.getToken("support.function.event-handler.js"))); //$NON-NLS-1$

		// Add word rule for keywords, types, and constants.
		WordRule wordRule = new WordRule(new WordDetector(), ThemeUtil.getToken("source.js")); //$NON-NLS-1$
		addWordRules(wordRule, ThemeUtil.getToken("keyword.control.js"), KEYWORD_CONTROL); //$NON-NLS-1$
		addWordRules(wordRule, ThemeUtil.getToken("keyword.operator.js"), KEYWORD_OPERATORS); //$NON-NLS-1$
		addWordRules(wordRule, ThemeUtil.getToken("storage.type.js"), STORAGE_TYPES); //$NON-NLS-1$
		addWordRules(wordRule, ThemeUtil.getToken("storage.modifier.js"), STORAGE_MODIFIERS); //$NON-NLS-1$
		addWordRules(wordRule, ThemeUtil.getToken("support.class.js"), SUPPORT_CLASSES); //$NON-NLS-1$
		addWordRules(wordRule, ThemeUtil.getToken("support.constant.dom.js"), SUPPORT_DOM_CONSTANTS); //$NON-NLS-1$

		wordRule.addWord("true", ThemeUtil.getToken("constant.language.boolean.true.js")); //$NON-NLS-1$ //$NON-NLS-2$
		wordRule.addWord("false", ThemeUtil.getToken("constant.language.boolean.false.js")); //$NON-NLS-1$ //$NON-NLS-2$
		wordRule.addWord("null", ThemeUtil.getToken("constant.language.null.js")); //$NON-NLS-1$ //$NON-NLS-2$

		wordRule.addWord("Infinity", ThemeUtil.getToken("constant.language.js")); //$NON-NLS-1$ //$NON-NLS-2$
		wordRule.addWord("NaN", ThemeUtil.getToken("constant.language.js")); //$NON-NLS-1$ //$NON-NLS-2$
		wordRule.addWord("undefined", ThemeUtil.getToken("constant.language.js")); //$NON-NLS-1$ //$NON-NLS-2$

		wordRule.addWord("super", ThemeUtil.getToken("variable.language.js")); //$NON-NLS-1$ //$NON-NLS-2$
		wordRule.addWord("this", ThemeUtil.getToken("variable.language.js")); //$NON-NLS-1$ //$NON-NLS-2$

		wordRule.addWord("debugger", ThemeUtil.getToken("keyword.other.js")); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(wordRule);

		setRules(rules.toArray(new IRule[rules.size()]));
	}

	private void addWordRules(WordRule wordRule, IToken keywordOperators, String... words)
	{
		for (String word : words)
		{
			wordRule.addWord(word, keywordOperators);
		}
	}
}
