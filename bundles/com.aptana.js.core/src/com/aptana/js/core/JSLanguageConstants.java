/**
 * Aptana Studio
 * Copyright (c) 2005-2017 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core;

import java.util.Set;

import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;

public class JSLanguageConstants
{
	public static final String AWAIT = "await"; //$NON-NLS-1$
	public static final String CONST = "const"; //$NON-NLS-1$
	public static final String LET = "let"; //$NON-NLS-1$
	public static final String AS = "as"; //$NON-NLS-1$
	public static final String FROM = "from"; //$NON-NLS-1$
	public static final String YIELD = "yield"; //$NON-NLS-1$
	public static final String IMPORT = "import"; //$NON-NLS-1$
	public static final String EXPORT = "export"; //$NON-NLS-1$
	public static final String CLASS = "class"; //$NON-NLS-1$
	public static final String EXTENDS = "extends"; //$NON-NLS-1$
	public static final String SUPER = "super"; //$NON-NLS-1$
	public static final String GET = "get"; //$NON-NLS-1$
	public static final String SET = "set"; //$NON-NLS-1$
	public static final String TRUE = "true"; //$NON-NLS-1$
	public static final String FALSE = "false"; //$NON-NLS-1$
	public static final String THIS = "this"; //$NON-NLS-1$
	public static final String NULL = "null"; //$NON-NLS-1$
	public static final String GREATER_EQUAL = ">="; //$NON-NLS-1$
	public static final String CATCH = "catch"; //$NON-NLS-1$
	public static final String FUNCTION = "function"; //$NON-NLS-1$
	public static final String INSTANCEOF = "instanceof"; //$NON-NLS-1$
	public static final String PLUS = "+"; //$NON-NLS-1$
	public static final String TRY = "try"; //$NON-NLS-1$
	public static final String DELETE = "delete"; //$NON-NLS-1$
	public static final String VAR = "var"; //$NON-NLS-1$
	public static final String AMPERSAND_AMPERSAND = "&&"; //$NON-NLS-1$
	public static final String EQUAL = "="; //$NON-NLS-1$
	public static final String CARET_EQUAL = "^="; //$NON-NLS-1$
	public static final String PLUS_EQUAL = "+="; //$NON-NLS-1$
	public static final String MINUS = "-"; //$NON-NLS-1$
	public static final String COLON = ":"; //$NON-NLS-1$
	public static final String WHILE = "while"; //$NON-NLS-1$
	public static final String PIPE_EQUAL = "|="; //$NON-NLS-1$
	public static final String TYPEOF = "typeof"; //$NON-NLS-1$
	public static final String SWITCH = "switch"; //$NON-NLS-1$
	public static final String RBRACKET = "]"; //$NON-NLS-1$
	public static final String FINALLY = "finally"; //$NON-NLS-1$
	public static final String LPAREN = "("; //$NON-NLS-1$
	public static final String RCURLY = "}"; //$NON-NLS-1$
	public static final String TILDE = "~"; //$NON-NLS-1$
	public static final String DOT = "."; //$NON-NLS-1$
	public static final String DOT_DOT_DOT = "..."; //$NON-NLS-1$
	public static final String LESS_EQUAL = "<="; //$NON-NLS-1$
	public static final String FORWARD_SLASH_EQUAL = "/="; //$NON-NLS-1$
	public static final String CASE = "case"; //$NON-NLS-1$
	public static final String TARGET = "target"; //$NON-NLS-1$
	public static final String EXCLAMATION = "!"; //$NON-NLS-1$
	public static final String PLUS_PLUS = "++"; //$NON-NLS-1$
	public static final String CARET = "^"; //$NON-NLS-1$
	public static final String LBRACKET = "["; //$NON-NLS-1$
	public static final String EQUAL_EQUAL = "=="; //$NON-NLS-1$
	public static final String DO = "do"; //$NON-NLS-1$
	public static final String RETURN = "return"; //$NON-NLS-1$
	public static final String QUESTION = "?"; //$NON-NLS-1$
	public static final String PERCENT = "%"; //$NON-NLS-1$
	public static final String EXCLAMATION_EQUAL = "!="; //$NON-NLS-1$
	public static final String MINUS_MINUS = "--"; //$NON-NLS-1$
	public static final String IF = "if"; //$NON-NLS-1$
	public static final String OF = "of"; //$NON-NLS-1$
	public static final String DEFAULT = "default"; //$NON-NLS-1$
	public static final String NEW = "new"; //$NON-NLS-1$
	public static final String PERCENT_EQUAL = "%="; //$NON-NLS-1$
	public static final String EXCLAMATION_EQUAL_EQUAL = "!=="; //$NON-NLS-1$
	public static final String WITH = "with"; //$NON-NLS-1$
	public static final String AMPERSAND = "&"; //$NON-NLS-1$
	public static final String SEMICOLON = ";"; //$NON-NLS-1$
	public static final String AMPERSAND_EQUAL = "&="; //$NON-NLS-1$
	public static final String LCURLY = "{"; //$NON-NLS-1$
	public static final String GREATER = ">"; //$NON-NLS-1$
	public static final String PIPE = "|"; //$NON-NLS-1$
	public static final String LESS = "<"; //$NON-NLS-1$
	public static final String LESS_LESS_EQUAL = "<<="; //$NON-NLS-1$
	public static final String CONTINUE = "continue"; //$NON-NLS-1$
	public static final String COMMA = "comma"; //$NON-NLS-1$
	public static final String VOID = "void"; //$NON-NLS-1$
	public static final String ARROW = "=>"; //$NON-NLS-1$
	public static final String EQUAL_EQUAL_EQUAL = "==="; //$NON-NLS-1$
	public static final String ELSE = "else"; //$NON-NLS-1$
	public static final String STAR_EQUAL = "*="; //$NON-NLS-1$
	public static final String PIPE_PIPE = "||"; //$NON-NLS-1$
	public static final String THROW = "throw"; //$NON-NLS-1$
	public static final String BREAK = "break"; //$NON-NLS-1$
	public static final String LESS_LESS = "<<"; //$NON-NLS-1$
	public static final String STAR = "*"; //$NON-NLS-1$
	public static final String STAR_STAR = "**"; //$NON-NLS-1$
	public static final String STAR_STAR_EQUAL = "**="; //$NON-NLS-1$
	public static final String MINUS_EQUAL = "-="; //$NON-NLS-1$
	public static final String RPAREN = ")"; //$NON-NLS-1$
	public static final String GREATER_GREATER = ">>"; //$NON-NLS-1$
	public static final String GREATER_GREATER_GREATER_EQUAL = ">>>="; //$NON-NLS-1$
	public static final String FORWARD_SLASH = "/"; //$NON-NLS-1$
	public static final String GREATER_GREATER_GREATER = ">>>"; //$NON-NLS-1$
	public static final String FOR = "for"; //$NON-NLS-1$
	public static final String IN = "in"; //$NON-NLS-1$
	public static final String GREATER_GREATER_EQUAL = ">>="; //$NON-NLS-1$

	public static final String REQUIRE = "require"; //$NON-NLS-1$

	public static final String[] KEYWORD_OPERATORS = new String[] { DELETE, INSTANCEOF, IN, NEW, TYPEOF, WITH, FROM, AS };
	@SuppressWarnings("nls")
	public static final String[] SUPPORT_FUNCTIONS = new String[] { "small", "savePreferences", "slice", "sqrt",
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
	public static final String[] EVENT_HANDLER_FUNCTIONS = new String[] { "onRowsinserted", "onRowsdelete",
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
	public static final String[] DOM_FUNCTIONS = new String[] { "substringData", "submit", "splitText", "setNamedItem",
			"setAttributeNode", "setAttribute", "select", "hasChildNodes", "hasFeature", "namedItem", "click", "close",
			"cloneNode", "createComment", "createCDATASection", "createCaption", "createTHead", "createTextNode",
			"createTFoot", "createDocumentFragment", "createProcessingInstruction", "createEntityReference",
			"createElement", "createAttribute", "tabIndex", "insertRow", "insertBefore", "insertCell", "insertData",
			"item", "open", "deleteRow", "deleteCell", "deleteCaption", "deleteTHead", "deleteTFoot", "deleteData",
			"focus", "writeln", "write", "add", "appendChild", "appendData", "reset", "replaceChild", "replaceData",
			"removeNamedItem", "removeChild", "removeAttributeNode", "removeAttribute", "remove", "getNamedItem",
			"getElementsByName", "getElementsByTagName", "getElementById", "getAttributeNode", "getAttribute", "blur" };

	public static final String[] OPERATORS = { GREATER_GREATER_GREATER_EQUAL, GREATER_GREATER_GREATER, LESS_LESS_EQUAL,
			GREATER_GREATER_EQUAL, EQUAL_EQUAL_EQUAL, EXCLAMATION_EQUAL_EQUAL, GREATER_GREATER, LESS_LESS,
			EXCLAMATION_EQUAL, LESS_EQUAL, GREATER_EQUAL, EQUAL_EQUAL, MINUS_MINUS, PLUS_PLUS, AMPERSAND_AMPERSAND,
			PIPE_PIPE, STAR_EQUAL, FORWARD_SLASH_EQUAL, PERCENT_EQUAL, PLUS_EQUAL, MINUS_EQUAL, AMPERSAND_EQUAL,
			PIPE_EQUAL, CARET_EQUAL };

	public static final char[] SINGLE_CHARACTER_OPERATORS = { '?', '!', '%', '&', '*', '-', '+', '~', '=', '<', '>',
			'^', '|', '/' };

	public static final String[] KEYWORD_CONTROL = new String[] { BREAK, CASE, CATCH, CONTINUE, DEFAULT, DO, ELSE,
			FINALLY, FOR, IF, RETURN, SWITCH, THROW, TRY, WHILE };

	@SuppressWarnings("nls")
	public static final String[] SUPPORT_CLASSES = new String[] { "Anchor", "Applet", "Area", "Array", "Boolean",
			"Button", "Checkbox", "Date", "document", "Error", "event", "FileUpload", "Form", "Frame", "Function",
			"Hidden", "History", "Image", "JavaArray", "JavaClass", "JavaObject", "JavaPackage", "java", "Layer",
			"Link", "Location", "Math", "MimeType", "Number", "navigator", "netscape", "Object", "Option", "Packages",
			"Password", "Plugin", "Radio", "RegExp", "Reset", "Select", "String", "Style", "Submit", "screen", "sun",
			"Text", "Textarea", "window", "XMLHttpRequest" };

	@SuppressWarnings("nls")
	public static final String[] SUPPORT_DOM_CONSTANTS = new String[] { "ELEMENT_NODE", "ATTRIBUTE_NODE", "TEXT_NODE",
			"CDATA_SECTION_NODE", "ENTITY_REFERENCE_NODE", "ENTITY_NODE", "PROCESSING_INSTRUCTION_NODE",
			"COMMENT_NODE", "DOCUMENT_NODE", "DOCUMENT_TYPE_NODE", "DOCUMENT_FRAGMENT_NODE", "NOTATION_NODE",
			"INDEX_SIZE_ERR", "DOMSTRING_SIZE_ERR", "HIERARCHY_REQUEST_ERR", "WRONG_DOCUMENT_ERR",
			"INVALID_CHARACTER_ERR", "NO_DATA_ALLOWED_ERR", "NO_MODIFICATION_ALLOWED_ERR", "NOT_FOUND_ERR",
			"NOT_SUPPORTED_ERR", "INUSE_ATTRIBUTE_ERR" };

	@SuppressWarnings("nls")
	public static final String[] KEYWORD_CONTROL_FUTURE = new String[] { "goto", "import", "package" };

	@SuppressWarnings("nls")
	public static final String[] STORAGE_TYPES = new String[] { "boolean", "byte", "char", "class", "double", "enum",
			"float", "function", "int", "interface", "long", "short", "var", "void" };

	@SuppressWarnings("nls")
	public static final String[] STORAGE_MODIFIERS = new String[] { "const", "export", "extends", "final",
			"implements", "native", "private", "protected", "public", "static", "synchronized", "throws", "transient",
			"volatile" };

	@SuppressWarnings("nls")
	public static final String[] FIREBUG_FUNCTIONS = new String[] { ".warn", ".info", ".log", ".error", ".time",
			".timeEnd", ".assert" };

	@SuppressWarnings("nls")
	public static final String[] DOM_CONSTANTS = { "shape", "src", "systemId", "scheme", "scope", "scrolling",
			"standby", "start", "size", "summary", "specified", "sectionRowIndex", "selectedIndex", "selected",
			"hspace", "httpEquiv", "htmlFor", "height", "headers", "hreflang", "href", "noResize", "notations",
			"notationName", "noWrap", "noShade", "noHref", "nodeName", "nodeType", "nodeValue", "nextSibling", "name",
			"childNodes", "chOff", "checked", "charset", "ch", "cite", "content", "cookie", "coords", "codeBase",
			"codeType", "code", "cols", "colSpan", "color", "compact", "cells", "cellSpacing", "cellPadding", "clear",
			"className", "caption", "type", "tBodies", "title", "tHead", "text", "target", "tagName", "tFoot", "isMap",
			"index", "id", "implementation", "images", "options", "ownerDocument", "object", "disabled", "dir",
			"doctype", "documentElement", "domain", "declare", "defer", "defaultSelected", "defaultChecked",
			"defaultValue", "dateTime", "data", "useMap", "publicId", "parentNode", "profile", "prompt",
			"peviousSibling", "enctype", "entities", "event", "elements", "vspace", "version", "valueType", "value",
			"vLink", "vAlign", "URL", "firstChild", "forms", "form", "face", "frameBorder", "frame", "width", "links",
			"link", "longDesc", "lowSrc", "lastChild", "lang", "label", "anchors", "attributes", "applets",
			"accessKey", "acceptCharset", "accept", "action", "alt", "align", "archive", "areas", "axis", "aLink",
			"abbr", "rows", "rowSpan", "rowIndex", "rules", "rev", "referrer", "rel", "readOnly", "multiple", "method",
			"media", "marginHeight", "marginWidth", "maxLength", "body", "border", "background", "bgColor" };

	@SuppressWarnings("nls")
	public static final String[] SUPPORT_CONSTANTS = { "systemLanguage", "scripts", "scrollbars", "screenX", "screenY",
			"screenTop", "screenLeft", "styleSheets", "style", "statusText", "statusbar", "status", "siblingBelow",
			"siblingAbove", "source", "suffixes", "securityPolicy", "security", "selection", "self", "history",
			"hostname", "host", "hash", "hasFocus", "y", "XMLDocument", "XSLDocument", "next", "namespaces",
			"namespaceURI", "nameProp", "MIN_VALUE", "MAX_VALUE", "characterSet", "constructor", "controllers",
			"cookieEnabled", "colorDepth", "components", "complete", "current", "cpuClass", "clipboardData", "clip",
			"clientInformation", "closed", "classes", "callee", "caller", "crypto", "toolbar", "top", "textTransform",
			"textIndent", "textDecoration", "textAlign", "tags", "SQRT1_2", "SQRT2", "innerHeight", "innerWidth",
			"input", "ids", "ignoreCase", "zIndex", "oscpu", "onreadystatechange", "onLine", "outerHeight",
			"outerWidth", "opsProfile", "opener", "offscreenBuffering", "NEGATIVE_INFINITY", "display", "dialogHeight",
			"dialogTop", "dialogWidth", "dialogLeft", "dialogArguments", "directories", "description", "defaultStatus",
			"defaultChecked", "defaultCharset", "defaultView", "userProfile", "userLanguage", "userAgent", "undefined",
			"uniqueID", "updateInterval", "_content", "pixelDepth", "port", "personalbar", "pkcs11", "plugins",
			"platform", "pathname", "paddingRight", "paddingBottom", "paddingTop", "paddingLeft", "parentWindow",
			"parentLayer", "parent", "pageXOffset", "pageX", "pageYOffset", "pageY", "protocol", "prototype",
			"product", "productSub", "prompter", "previous", "prefix", "encoding", "enabledPlugin", "external",
			"expando", "embeds", "visibility", "vendorSub", "vendor", "vLinkcolor", "URLUnencoded", "PI",
			"POSITIVE_INFINITY", "filename", "fontSize", "fontFamily", "fontWeight", "formName", "frames",
			"frameElement", "fgColor", "E", "whiteSpace", "listStyleType", "lineHeight", "linkColor", "locationbar",
			"location", "localName", "lowsrc", "length", "leftContext", "left", "lastModified", "lastMatch",
			"lastIndex", "lastParen", "layers", "layerX", "language", "appMinorVersion", "appName", "appCodeName",
			"appCore", "appVersion", "availHeight", "availTop", "availWidth", "availLeft", "all", "arity", "arguments",
			"aLinkcolor", "above", "rightContext", "right", "responseXML", "responseext", "readyState", "global", "x",
			"mimeTypes", "multiline", "menubar", "marginRight", "marginBottom", "marginTop", "marginLeft", "LN10",
			"LN2", "LOG10E", "LOG2E", "bottom", "borderRightWidth", "borderBottomWidth", "borderStyle", "borderColor",
			"borderTopWidth", "borderLeftWidth", "bufferDepth", "below", "backgroundColor", "backgroundImage", };

	private static String[] GRAMMAR_KEYWORDS = { AWAIT, FUNCTION, VAR, LET, CONST, CLASS, EXTENDS, SUPER, VOID, TRUE, FALSE, NULL, THIS };

	/**
	 * All keywords
	 */
	public static final Set<String> KEYWORDS = CollectionsUtil.newSet(ArrayUtil.flatten(
			JSLanguageConstants.KEYWORD_OPERATORS, JSLanguageConstants.GRAMMAR_KEYWORDS,
			JSLanguageConstants.KEYWORD_CONTROL));

	/**
	 * JSLanguageConstants
	 */
	private JSLanguageConstants()
	{
	}

	public static boolean isKeyword(String string)
	{
		return KEYWORDS.contains(string);
	}
}