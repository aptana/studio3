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
package com.aptana.editor.js;

public class JSLanguageConstants
{
	@SuppressWarnings("nls")
	public static final String[] KEYWORD_OPERATORS = new String[] { "delete", "instanceof", "in", "new", "typeof",
			"with" };
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
	public static final String[] DOM_FUNCTIONS = new String[] { "substringData", "submit", "splitText",
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
	public static final String[] OPERATORS = { ">>>=", ">>>", "<<=", ">>=", "===", "!==", ">>", "<<", "!=", "<=",
			">=", "==", "--", "++", "&&", "||", "*=", "/=", "%=", "+=", "-=", "&=", "|=", "^=" };
	
	public static final char[] SINGLE_CHARACTER_OPERATORS = { '?', '!', '%', '&', '*', '-', '+', '~', '=', '<', '>',
	'^', '|', '/' };
	
	@SuppressWarnings("nls")
	public static final String[] KEYWORD_CONTROL = new String[] { "break", "case", "catch", "continue", "default",
			"do", "else", "finally", "for", "if", "return", "switch", "throw", "try", "while" };
	
	@SuppressWarnings("nls")
	public static final String[] SUPPORT_CLASSES = new String[] { "Anchor", "Applet", "Area", "Array", "Boolean",
			"Button", "Checkbox", "Date", "document", "event", "FileUpload", "Form", "Frame", "Function", "Hidden",
			"History", "Image", "JavaArray", "JavaClass", "JavaObject", "JavaPackage", "java", "Layer", "Link",
			"Location", "Math", "MimeType", "Number", "navigator", "netscape", "Object", "Option", "Packages",
			"Password", "Plugin", "Radio", "RegExp", "Reset", "Select", "String", "Style", "Submit", "screen", "sun",
			"Text", "Textarea", "window", "XMLHttpRequest" };
	
	@SuppressWarnings("nls")
	public static final String[] SUPPORT_DOM_CONSTANTS = new String[] { "ELEMENT_NODE", "ATTRIBUTE_NODE",
			"TEXT_NODE", "CDATA_SECTION_NODE", "ENTITY_REFERENCE_NODE", "ENTITY_NODE", "PROCESSING_INSTRUCTION_NODE",
			"COMMENT_NODE", "DOCUMENT_NODE", "DOCUMENT_TYPE_NODE", "DOCUMENT_FRAGMENT_NODE", "NOTATION_NODE",
			"INDEX_SIZE_ERR", "DOMSTRING_SIZE_ERR", "HIERARCHY_REQUEST_ERR", "WRONG_DOCUMENT_ERR",
			"INVALID_CHARACTER_ERR", "NO_DATA_ALLOWED_ERR", "NO_MODIFICATION_ALLOWED_ERR", "NOT_FOUND_ERR",
			"NOT_SUPPORTED_ERR", "INUSE_ATTRIBUTE_ERR" };
	
	@SuppressWarnings("nls")
	public static final String[] KEYWORD_CONTROL_FUTURE = new String[] { "goto", "import", "package" };
	
	@SuppressWarnings("nls")
	public static final String[] STORAGE_TYPES = new String[] { "boolean", "byte", "char", "class", "double",
			"enum", "float", "function", "int", "interface", "long", "short", "var", "void" };
	
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
	public static final String[] SUPPORT_CONSTANTS = { "systemLanguage", "scripts", "scrollbars", "screenX",
			"screenY", "screenTop", "screenLeft", "styleSheets", "style", "statusText", "statusbar", "status",
			"siblingBelow", "siblingAbove", "source", "suffixes", "securityPolicy", "security", "selection", "self",
			"history", "hostname", "host", "hash", "hasFocus", "y", "XMLDocument", "XSLDocument", "next", "namespaces",
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
	
	@SuppressWarnings("nls")
	public static String[] GRAMMAR_KEYWORDS = { "function", "var", "void", "true", "false", "null", "this" };

	/**
	 * JSLanguageConstants
	 */
	private JSLanguageConstants()
	{
	}
}
