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

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
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
	// No opts: 3208ms avg
	// Regexp opt: 2476ms avg
	// Regexp + special char opt: 2295ms avg
	// regexp + special char + word: 160-190ms avg (biggest gain was converting operators from regexp to words)

	@SuppressWarnings("nls")
	private static final String[] KEYWORD_OPERATORS = new String[] { "delete", "in", "instanceof", "new", "typeof",
			"with" };

	@SuppressWarnings("nls")
	private static final String[] KEYWORD_CONTROL = new String[] { "break", "case", "catch", "continue", "default",
			"do", "else", "finally", "for", "goto", "if", "import", "package", "return", "switch", "throw", "try",
			"while" };

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

	@SuppressWarnings("nls")
	private static final String[] SUPPORT_FUNCTIONS = new String[] { "small", "savePreferences", "slice", "sqrt",
			"shift", "showModelessDialog", "showModalDialog", "showHelp", "scrollX", "scrollByPages", "scrollByLines",
			"scrollBy", "scrollY", "scrollTo", "scroll", "stop", "strike", "sin", "sidebar", "signText",
			"sizeToContent", "sort", "sup", "substring", "substr", "sub", "splice", "split", "send", "search",
			"setResizable", "setRequestHeader", "setMinutes", "setMilliseconds", "setMonth", "setSeconds",
			"setHotKeys", "setHours", "setYear", "setCursor", "setTimeout", "setTime", "setInterval", "setZOptions",
			"setDate", "setUTCMinutes", "setUTCMilliseconds", "setUTCMonth", "setUTCSeconds", "setUTCHours",
			"setUTCDate", "setUTCFullYear", "setFullYear", "setActive", "home", "handleEvent", "navigate",
			"charCodeAt", "charAt", "compile", "cos", "concat", "contextual", "confirm", "ceil", "clearTimeout",
			"clearInterval", "clear", "captureEvents", "call", "createStyleSheet", "createPopup", "createEventObject",
			"toGMTString", "toString", "toSource", "toUTCString", "toUpperCase", "toLocaleString", "toLowerCase",
			"test", "tan", "taintEnabled", "taint", "isNaN", "isFinite", "indexOf", "italics",
			"disableExternalCapture", "dump", "detachEvent", "unshift", "untaint", "unescape", "unwatch",
			"updateCommands", "join", "javaEnabled", "push", "plugins.refresh", "paddings", "parseInt", "parseFloat",
			"parse", "pop", "pow", "print", "prompt", "preference", "escape", "enableExternalCapture", "eval",
			"elementFromPoint", "exp", "execScript", "execCommand", "exec", "valueOf", "UTC", "queryCommandState",
			"queryCommandIndeterm", "queryCommandEnabled", "queryCommandValue", "find", "fixed", "fileModifiedDate",
			"fileSize", "fileCreatedDate", "fileUpdatedDate", "fontsize", "fontcolor", "forward", "floor",
			"fromCharCode", "watch", "link", "lastIndexOf", "load", "log", "asin", "anchor", "acos", "apply", "alert",
			"abs", "abort", "attachEvent", "atob", "atan2", "atan", "round", "routeEvents", "resizeBy", "resizeTo",
			"recalc", "returnValue", "replace", "reverse", "reload", "releaseCapture", "releaseEvents", "random", "go",
			"getResponseHeader", "getMinutes", "getMilliseconds", "getMonth", "getSeconds", "getSelection", "getHours",
			"getYear", "getTimezoneOffset", "getTime", "getDay", "getDate", "getUTCMinutes", "getUTCMilliseconds",
			"getUTCMonth", "getUTCSeconds", "getUTCHours", "getUTCDay", "getUTCDate", "getUTCFullYear", "getFullYear",
			"getAttention", "getAllResponseHeaders", "min", "mergeAttributes", "match", "margins", "max", "moveBy",
			"moveBelow", "moveToAbsolute", "moveTo", "moveAbove", "btoa", "big", "blink", "bold", "borderWidths",
			"back" };

	@SuppressWarnings("nls")
	private static final String[] EVENT_HANDLER_FUNCTIONS = new String[] { "onRowsinserted", "onRowsdelete",
			"onRowenter", "onRowexit", "onResizestart", "onResizeend", "onResize", "onReset", "onReadystatechange",
			"onMouseout", "onMouseover", "onMousedown", "onMouseup", "onMousemove", "onBeforecut",
			"onBeforedeactivate", "onBeforeunload", "onBeforeupdate", "onBeforepaste", "onBeforeprint",
			"onBeforeeditfocus", "onBeforeactivate", "onBlur", "onScroll", "onStop", "onSelectstart",
			"onSelectionchange", "onSelect", "onSubmit", "onHover", "onHelp", "onChange", "onContextmenu",
			"onControlselect", "onCut", "onCellchange", "onClick", "onClose", "onDeactivate", "onDatasetcomplete",
			"onDatasetchanged", "onDataavailable", "onDrop", "onDragover", "onDragdrop", "onDragenter", "onDragend",
			"onDragstart", "onDragleave", "onDrag", "onDblclick", "onUnload", "onPaste", "onPropertychange",
			"onErrorupdate", "onError", "onKeydown", "onKeyup", "onKeypress", "onFocus", "onLoad", "onActivate",
			"onAbort", "onAfterupdate", "onAfterprint" };

	@SuppressWarnings("nls")
	private static final String[] DOM_FUNCTIONS = new String[] { "substringData", "submit", "splitText",
			"setNamedItem", "setAttributeNode", "setAttribute", "select", "hasChildNodes", "hasFeature", "namedItem",
			"click", "close", "cloneNode", "createComment", "createCDATASection", "createCaption", "createTHead",
			"createTextNode", "createTFoot", "createDocumentFragment", "createProcessingInstruction",
			"createEntityReference", "createElement", "createAttribute", "tabIndex", "insertRow", "insertBefore",
			"insertCell", "insertData", "item", "open", "deleteRow", "deleteCell", "deleteCaption", "deleteTHead",
			"deleteTFoot", "deleteData", "focus", "writeln", "write", "add", "appendChild", "appendData", "reset",
			"replaceChild", "replaceData", "removeNamedItem", "removeChild", "removeAttributeNode", "removeAttribute",
			"remove", "getNamedItem", "getElementsByName", "getElementsByTagName", "getElementById",
			"getAttributeNode", "getAttribute", "blur" };

	@SuppressWarnings("nls")
	private static final String[] FIREBUG_FUNCTIONS = new String[] { ".warn", ".info", ".log", ".error", ".time",
			".timeEnd", ".assert" };

	@SuppressWarnings("nls")
	private static final String[] KEYWORD_OPERATOR_WORDS = new String[] { "in", "instanceof", "new", "delete",
			"typeof", "void" };

	@SuppressWarnings("nls")
	private static final String[] OPERATORS = { ">>>=", "<<=", ">>=", "===", "!==", "!!!", "!=", "<>", "<=", ">=",
			"==", "--", "++", "&&", "||", "?:", "*=", "/=", "%=", "+=", "-=", "&=", "^=", "!!", "!", "$", "%", "&",
			"*", "-", "+", "~", "=", "<", ">", "!", "/" };

	private static final boolean OPTIMIZE_REGEXP_RULES = true;
	private static final boolean USE_WORD_RULES = true;

	/**
	 * CodeScanner
	 */
	public JSCodeScanner()
	{
		// Please note that ordering of rules is important! the last word rule will end up assigning a token type to any
		// words that don't match the list. So we'll only have non-word source left to match against (like braces or
		// numbers)
		// Also, we try to make the fastest rules run first rather than have slow regexp rules continually getting
		// called. We want them called the least so we should try all faster rules first.
		List<IRule> rules = new ArrayList<IRule>();
		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		if (USE_WORD_RULES)
		{
			// Converted word rules
			WordRule wordRule = new WordRule(new LettersAndDigitsWordDetector(), Token.UNDEFINED);
			addWordRules(wordRule, createToken("keyword.operator.js"), KEYWORD_OPERATOR_WORDS); //$NON-NLS-1$
			addWordRules(wordRule, createToken("support.function.js"), SUPPORT_FUNCTIONS); //$NON-NLS-1$
			addWordRules(wordRule, createToken("support.function.event-handler.js"), EVENT_HANDLER_FUNCTIONS); //$NON-NLS-1$
			addWordRules(wordRule, createToken("support.function.dom.js"), DOM_FUNCTIONS); //$NON-NLS-1$
			rules.add(wordRule);

			// Functions where we need period to begin it
			wordRule = new WordRule(new FunctionCallDetector(), Token.UNDEFINED);
			addWordRules(wordRule, createToken("support.function.js.firebug"), FIREBUG_FUNCTIONS); //$NON-NLS-1$
			rules.add(wordRule);

			// Operators
			wordRule = new WordRule(new OperatorDetector(), Token.UNDEFINED);
			addWordRules(wordRule, createToken("keyword.operator.js"), OPERATORS); //$NON-NLS-1$
			rules.add(wordRule);
		}
		else
		{
			// Original regexp rules
			rules
					.add(new RegexpRule(
							"!|\\$|%|&|\\*|\\-\\-|\\-|\\+\\+|\\+|~|===|==|=|!=|!==|<=|>=|<<=|>>=|>>>=|<>|<|>|!|&&|\\|\\||\\?\\:|\\*=|(?<!\\()/=|%=|\\+=|\\-=|&=|\\^=|\\b(in|instanceof|new|delete|typeof|void)\\b", //$NON-NLS-1$
							createToken("keyword.operator.js"))); //$NON-NLS-1$
			rules.add(new RegexpRule("\\.(warn|info|log|error|time|timeEnd|assert)\\b", //$NON-NLS-1$
					createToken("support.function.js.firebug"), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$
			rules
					.add(new RegexpRule(
							"\\b(s(ub(stringData|mit)|plitText|e(t(NamedItem|Attribute(Node)?)|lect))|has(ChildNodes|Feature)|namedItem|c(l(ick|o(se|neNode))|reate(C(omment|DATASection|aption)|T(Head|extNode|Foot)|DocumentFragment|ProcessingInstruction|E(ntityReference|lement)|Attribute))|tabIndex|i(nsert(Row|Before|Cell|Data)|tem)|open|delete(Row|C(ell|aption)|T(Head|Foot)|Data)|focus|write(ln)?|a(dd|ppend(Child|Data))|re(set|place(Child|Data)|move(NamedItem|Child|Attribute(Node)?)?)|get(NamedItem|Element(sBy(Name|TagName)|ById)|Attribute(Node)?)|blur)\\b(?=\\()", //$NON-NLS-1$
							createToken("support.function.dom.js"))); //$NON-NLS-1$
			rules
					.add(new RegexpRule(
							"\\b(s(h(ift|ow(Mod(elessDialog|alDialog)|Help))|croll(X|By(Pages|Lines)?|Y|To)?|t(op|rike)|i(n|zeToContent|debar|gnText)|ort|u(p|b(str(ing)?)?)|pli(ce|t)|e(nd|t(Re(sizable|questHeader)|M(i(nutes|lliseconds)|onth)|Seconds|Ho(tKeys|urs)|Year|Cursor|Time(out)?|Interval|ZOptions|Date|UTC(M(i(nutes|lliseconds)|onth)|Seconds|Hours|Date|FullYear)|FullYear|Active)|arch)|qrt|lice|avePreferences|mall)|h(ome|andleEvent)|navigate|c(har(CodeAt|At)|o(s|n(cat|textual|firm)|mpile)|eil|lear(Timeout|Interval)?|a(ptureEvents|ll)|reate(StyleSheet|Popup|EventObject))|t(o(GMTString|S(tring|ource)|U(TCString|pperCase)|Lo(caleString|werCase))|est|a(n|int(Enabled)?))|i(s(NaN|Finite)|ndexOf|talics)|d(isableExternalCapture|ump|etachEvent)|u(n(shift|taint|escape|watch)|pdateCommands)|j(oin|avaEnabled)|p(o(p|w)|ush|lugins.refresh|a(ddings|rse(Int|Float)?)|r(int|ompt|eference))|e(scape|nableExternalCapture|val|lementFromPoint|x(p|ec(Script|Command)?))|valueOf|UTC|queryCommand(State|Indeterm|Enabled|Value)|f(i(nd|le(ModifiedDate|Size|CreatedDate|UpdatedDate)|xed)|o(nt(size|color)|rward)|loor|romCharCode)|watch|l(ink|o(ad|g)|astIndexOf)|a(sin|nchor|cos|t(tachEvent|ob|an(2)?)|pply|lert|b(s|ort))|r(ou(nd|teEvents)|e(size(By|To)|calc|turnValue|place|verse|l(oad|ease(Capture|Events)))|andom)|g(o|et(ResponseHeader|M(i(nutes|lliseconds)|onth)|Se(conds|lection)|Hours|Year|Time(zoneOffset)?|Da(y|te)|UTC(M(i(nutes|lliseconds)|onth)|Seconds|Hours|Da(y|te)|FullYear)|FullYear|A(ttention|llResponseHeaders)))|m(in|ove(B(y|elow)|To(Absolute)?|Above)|ergeAttributes|a(tch|rgins|x))|b(toa|ig|o(ld|rderWidths)|link|ack))\\b(?=\\()", //$NON-NLS-1$
							createToken("support.function.js"))); //$NON-NLS-1$
			rules
					.add(new RegexpRule(
							"\\bon(R(ow(s(inserted|delete)|e(nter|xit))|e(s(ize(start|end)?|et)|adystatechange))|Mouse(o(ut|ver)|down|up|move)|B(efore(cut|deactivate|u(nload|pdate)|p(aste|rint)|editfocus|activate)|lur)|S(croll|top|ubmit|elect(start|ionchange)?)|H(over|elp)|C(hange|ont(extmenu|rolselect)|ut|ellchange|l(ick|ose))|D(eactivate|ata(setc(hanged|omplete)|available)|r(op|ag(start|over|drop|en(ter|d)|leave)?)|blclick)|Unload|P(aste|ropertychange)|Error(update)?|Key(down|up|press)|Focus|Load|A(ctivate|fter(update|print)|bort))\\b", //$NON-NLS-1$
							createToken("support.function.event-handler.js"))); //$NON-NLS-1$
		}

		// TODO Turn these next two rules into word rules using the FunctionCallDetector word rule list
		// FIXME This rule shouldn't actually match the leading period, but we have no way to capture just the rest as
		// the token
		rules
				.add(new RegexpRule(
						"\\.(s(ystemLanguage|cr(ipts|ollbars|een(X|Y|Top|Left))|t(yle(Sheets)?|atus(Text|bar)?)|ibling(Below|Above)|ource|uffixes|e(curity(Policy)?|l(ection|f)))|h(istory|ost(name)?|as(h|Focus))|y|X(MLDocument|SLDocument)|n(ext|ame(space(s|URI)|Prop))|M(IN_VALUE|AX_VALUE)|c(haracterSet|o(n(structor|trollers)|okieEnabled|lorDepth|mp(onents|lete))|urrent|puClass|l(i(p(boardData)?|entInformation)|osed|asses)|alle(e|r)|rypto)|t(o(olbar|p)|ext(Transform|Indent|Decoration|Align)|ags)|SQRT(1_2|2)|i(n(ner(Height|Width)|put)|ds|gnoreCase)|zIndex|o(scpu|n(readystatechange|Line)|uter(Height|Width)|p(sProfile|ener)|ffscreenBuffering)|NEGATIVE_INFINITY|d(i(splay|alog(Height|Top|Width|Left|Arguments)|rectories)|e(scription|fault(Status|Ch(ecked|arset)|View)))|u(ser(Profile|Language|Agent)|n(iqueID|defined)|pdateInterval)|_content|p(ixelDepth|ort|ersonalbar|kcs11|l(ugins|atform)|a(thname|dding(Right|Bottom|Top|Left)|rent(Window|Layer)?|ge(X(Offset)?|Y(Offset)?))|r(o(to(col|type)|duct(Sub)?|mpter)|e(vious|fix)))|e(n(coding|abledPlugin)|x(ternal|pando)|mbeds)|v(isibility|endor(Sub)?|Linkcolor)|URLUnencoded|P(I|OSITIVE_INFINITY)|f(ilename|o(nt(Size|Family|Weight)|rmName)|rame(s|Element)|gColor)|E|whiteSpace|l(i(stStyleType|n(eHeight|kColor))|o(ca(tion(bar)?|lName)|wsrc)|e(ngth|ft(Context)?)|a(st(M(odified|atch)|Index|Paren)|yer(s|X)|nguage))|a(pp(MinorVersion|Name|Co(deName|re)|Version)|vail(Height|Top|Width|Left)|ll|r(ity|guments)|Linkcolor|bove)|r(ight(Context)?|e(sponse(XML|Text)|adyState))|global|x|m(imeTypes|ultiline|enubar|argin(Right|Bottom|Top|Left))|L(N(10|2)|OG(10E|2E))|b(o(ttom|rder(RightWidth|BottomWidth|Style|Color|TopWidth|LeftWidth))|ufferDepth|elow|ackground(Color|Image)))\\b", //$NON-NLS-1$
						createToken("support.constant.js"), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$
		// FIXME This rule shouldn't actually match the leading period, but we have no way to capture just the rest as
		// the token
		rules
				.add(new RegexpRule(
						"\\.(s(hape|ystemId|c(heme|ope|rolling)|ta(ndby|rt)|ize|ummary|pecified|e(ctionRowIndex|lected(Index)?)|rc)|h(space|t(tpEquiv|mlFor)|e(ight|aders)|ref(lang)?)|n(o(Resize|tation(s|Name)|Shade|Href|de(Name|Type|Value)|Wrap)|extSibling|ame)|c(h(ildNodes|Off|ecked|arset)?|ite|o(ntent|o(kie|rds)|de(Base|Type)?|l(s|Span|or)|mpact)|ell(s|Spacing|Padding)|l(ear|assName)|aption)|t(ype|Bodies|itle|Head|ext|a(rget|gName)|Foot)|i(sMap|ndex|d|m(plementation|ages))|o(ptions|wnerDocument|bject)|d(i(sabled|r)|o(c(type|umentElement)|main)|e(clare|f(er|ault(Selected|Checked|Value)))|at(eTime|a))|useMap|p(ublicId|arentNode|r(o(file|mpt)|eviousSibling))|e(n(ctype|tities)|vent|lements)|v(space|ersion|alue(Type)?|Link|Align)|URL|f(irstChild|orm(s)?|ace|rame(Border)?)|width|l(ink(s)?|o(ngDesc|wSrc)|a(stChild|ng|bel))|a(nchors|c(ce(ssKey|pt(Charset)?)|tion)|ttributes|pplets|l(t|ign)|r(chive|eas)|xis|Link|bbr)|r(ow(s|Span|Index)|ules|e(v|ferrer|l|adOnly))|m(ultiple|e(thod|dia)|a(rgin(Height|Width)|xLength))|b(o(dy|rder)|ackground|gColor))\\b", //$NON-NLS-1$
						createToken("support.constant.dom.js"), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$

		// Add word rule for keywords, types, and constants.
		WordRule wordRule = new WordRule(new WordDetector(), createToken("source.js")); //$NON-NLS-1$
		addWordRules(wordRule, createToken("keyword.control.js"), KEYWORD_CONTROL); //$NON-NLS-1$
		addWordRules(wordRule, createToken("keyword.operator.js"), KEYWORD_OPERATORS); //$NON-NLS-1$
		addWordRules(wordRule, createToken("storage.type.js"), STORAGE_TYPES); //$NON-NLS-1$
		addWordRules(wordRule, createToken("storage.modifier.js"), STORAGE_MODIFIERS); //$NON-NLS-1$
		addWordRules(wordRule, createToken("support.class.js"), SUPPORT_CLASSES); //$NON-NLS-1$
		addWordRules(wordRule, createToken("support.constant.dom.js"), SUPPORT_DOM_CONSTANTS); //$NON-NLS-1$
		wordRule.addWord("true", createToken("constant.language.boolean.true.js")); //$NON-NLS-1$ //$NON-NLS-2$
		wordRule.addWord("false", createToken("constant.language.boolean.false.js")); //$NON-NLS-1$ //$NON-NLS-2$
		wordRule.addWord("null", createToken("constant.language.null.js")); //$NON-NLS-1$ //$NON-NLS-2$
		wordRule.addWord("Infinity", createToken("constant.language.js")); //$NON-NLS-1$ //$NON-NLS-2$
		wordRule.addWord("NaN", createToken("constant.language.js")); //$NON-NLS-1$ //$NON-NLS-2$
		wordRule.addWord("undefined", createToken("constant.language.js")); //$NON-NLS-1$ //$NON-NLS-2$
		wordRule.addWord("super", createToken("variable.language.js")); //$NON-NLS-1$ //$NON-NLS-2$
		wordRule.addWord("this", createToken("variable.language.js")); //$NON-NLS-1$ //$NON-NLS-2$
		wordRule.addWord("debugger", createToken("keyword.other.js")); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(wordRule);

		// Punctuation
		rules.add(new SingleCharacterRule(';', createToken("punctuation.terminator.statement.js"))); //$NON-NLS-1$
		rules.add(new SingleCharacterRule('(', createToken("meta.brace.round.js"))); //$NON-NLS-1$
		rules.add(new SingleCharacterRule(')', createToken("meta.brace.round.js"))); //$NON-NLS-1$
		rules.add(new SingleCharacterRule('[', createToken("meta.brace.square.js"))); //$NON-NLS-1$
		rules.add(new SingleCharacterRule(']', createToken("meta.brace.square.js"))); //$NON-NLS-1$
		rules.add(new SingleCharacterRule('{', createToken("meta.brace.curly.js"))); //$NON-NLS-1$
		rules.add(new SingleCharacterRule('}', createToken("meta.brace.curly.js"))); //$NON-NLS-1$
		rules.add(new SingleCharacterRule(',', createToken("meta.delimiter.object.comma.js"))); //$NON-NLS-1$

		// Numbers
		rules.add(new RegexpRule("\\b((0(x|X)[0-9a-fA-F]+)|([0-9]+(\\.[0-9]+)?))\\b", //$NON-NLS-1$
				createToken("constant.numeric.js"))); //$NON-NLS-1$

		setRules(rules.toArray(new IRule[rules.size()]));
	}

	protected IToken createToken(String string)
	{
		return ThemeUtil.getToken(string);
	}

	private void addWordRules(WordRule wordRule, IToken keywordOperators, String... words)
	{
		for (String word : words)
		{
			wordRule.addWord(word, keywordOperators);
		}
	}

	/**
	 * Optimized rule to match a single character. Faster than a RegexpRule for one char.
	 * 
	 * @author cwilliams
	 */
	private static class SingleCharacterRule implements IPredicateRule
	{

		private IToken successToken;
		private char c;

		SingleCharacterRule(char c, IToken successToken)
		{
			this.c = c;
			this.successToken = successToken;
		}

		@Override
		public IToken evaluate(ICharacterScanner scanner, boolean resume)
		{
			if (c == (char) scanner.read())
			{
				return getSuccessToken();
			}
			scanner.unread();
			return Token.UNDEFINED;
		}

		@Override
		public IToken getSuccessToken()
		{
			return successToken;
		}

		@Override
		public IToken evaluate(ICharacterScanner scanner)
		{
			return evaluate(scanner, false);
		}
	}

	/**
	 * Special "word" detector for finding JS operators.
	 * 
	 * @author cwilliams
	 */
	private static final class OperatorDetector implements IWordDetector
	{

		@Override
		public boolean isWordPart(char c)
		{
			return isWordStart(c) || c == ':';
		}

		@Override
		public boolean isWordStart(char c)
		{
			switch (c)
			{
				case '!':
				case '$':
				case '%':
				case '&':
				case '*':
				case '-':
				case '+':
				case '~':
				case '=':
				case '<':
				case '>':
				case '|':
				case '?':
				case '/':
				case '^':
					return true;
				default:
					return false;
			}
		}
	}

	/**
	 * Word Detector for function names.
	 * 
	 * @author cwilliams
	 */
	private static final class LettersAndDigitsWordDetector implements IWordDetector
	{
		@Override
		public boolean isWordPart(char c)
		{
			return isWordStart(c) || Character.isDigit(c);
		}

		@Override
		public boolean isWordStart(char c)
		{
			return Character.isLetter(c);
		}
	}

	/**
	 * Special word detector to detect calls to functions (leading period plus function name)
	 * 
	 * @author cwilliams
	 */
	private static final class FunctionCallDetector implements IWordDetector
	{

		@Override
		public boolean isWordPart(char c)
		{
			return Character.isLetter(c);
		}

		@Override
		public boolean isWordStart(char c)
		{
			return c == '.';
		}
	}
}
