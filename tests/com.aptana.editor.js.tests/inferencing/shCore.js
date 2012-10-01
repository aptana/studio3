/* SyntaxHighlighter 3.0.83 (C) 2004-2010 Alex Gorbatchev http://alexgorbatchev.com/SyntaxHighlighter */
var SyntaxHighlighter = function() {
	function b(a, b) {
		return a.className.indexOf(b) != -1
	}

	function c(a, c) {
		b(a, c) || (a.className += " " + c)
	}

	function d(a, b) {
		a.className = a.className.replace(b, "")
	}

	function e(a) {
		var b = [];
		for (var c = 0; c < a.length; c++)
			b.push(a[c]);
		return b
	}

	function f(a) {
		return a.split("\n")
	}

	function g(a) {
		var b = "highlighter_";
		return a.indexOf(b) == 0 ? a : b + a
	}

	function h(b) {
		return a.vars.highlighters[g(b)]
	}

	function i(a) {
		return document.getElementById(g(a))
	}

	function j(b) {
		a.vars.highlighters[g(b.id)] = b
	}

	function k(a, b, c) {
		if (a == null)
			return null;
		var d = c != 1 ? a.childNodes : [a.parentNode], e = {"#":"id",".":"className"}[b.substr(0, 1)] || "nodeName", f, g;
		f = e != "nodeName" ? b.substr(1) : b.toUpperCase();
		if ((a[e] || "").indexOf(f) != -1)
			return a;
		for (var h = 0; d && h < d.length && g == null; h++)
			g = k(d[h], b, c);
		return g
	}

	function l(a, b) {
		return k(a, b, !0)
	}

	function m(a, b, c) {
		c = Math.max(c || 0, 0);
		for (var d = c; d < a.length; d++)
			if (a[d] == b)
				return d;
		return -1
	}

	function n(a) {
		return (a || "") + Math.round(Math.random() * 1e6).toString()
	}

	function o(a, b) {
		var c = {}, d;
		for (d in a)
		c[d] = a[d];
		for (d in b)
		c[d] = b[d];
		return c
	}

	function p(a) {
		var b = {"true":!0,"false":!1}[a];
		return b == null ? a : b
	}

	function q(a, b, c, d, e) {
		var f = (screen.width - c) / 2, g = (screen.height - d) / 2;
		e += ", left=" + f + ", top=" + g + ", width=" + c + ", height=" + d, e = e.replace(/^,/, "");
		var h = window.open(a, b, e);
		return h.focus(), h
	}

	function r(a, b, c, d) {
		function e(a) {
			a = a || window.event, a.target || (a.target = a.srcElement, a.preventDefault = function() {
				this.returnValue = !1
			}), c.call(d || window, a)
		}
		a.attachEvent ? a.attachEvent("on" + b, e) : a.addEventListener(b, e, !1)
	}

	function s(b) {
		window.alert(a.config.strings.alert + b)
	}

	function t(b, c) {
		var d = a.vars.discoveredBrushes, e = null;
		if (d == null) {
			d = {};
			for (var f in a.brushes) {
				var g = a.brushes[f], h = g.aliases;
				if (h == null)
					continue;
				g.brushName = f.toLowerCase();
				for (var i = 0; i < h.length; i++)
					d[h[i]] = f
			}
			a.vars.discoveredBrushes = d
		}
		return e = a.brushes[d[b]], e == null && c != 0 && s(a.config.strings.noBrush + b), e
	}

	function u(a, b) {
		var c = f(a);
		for (var d = 0; d < c.length; d++)
			c[d] = b(c[d], d);
		return c.join("\n")
	}

	function v(a) {
		return a.replace(/^[ ]*[\n]+|[\n]*[ ]*$/g, "")
	}

	function w(a) {
		var b, c = {}, d = new XRegExp("^\\[(?<values>(.*?))\\]$"), e = new XRegExp("(?<name>[\\w-]+)\\s*:\\s*(?<value>[\\w-%#]+|\\[.*?\\]|\".*?\"|'.*?')\\s*;?", "g");
		while (( b = e.exec(a)) != null) {
			var f = b.value.replace(/^['"]|['"]$/g, "");
			if (f != null && d.test(f)) {
				var g = d.exec(f);
				f = g.values.length > 0 ? g.values.split(/\s*,\s*/) : []
			}
			c[b.name] = f
		}
		return c
	}

	function x(b, c) {
		return b == null || b.length == 0 || b == "\n" ? b : ( b = b.replace(/</g, "&lt;"), b = b.replace(/ {2,}/g, function(b) {
			var c = "";
			for (var d = 0; d < b.length - 1; d++)
				c += a.config.space;
			return c + " "
		}), c != null && ( b = u(b, function(a) {
			if (a.length == 0)
				return "";
			var b = "";
			return a = a.replace(/^(&nbsp;| )+/, function(a) {
				return b = a, ""
			}), a.length == 0 ? b : b + '<code class="' + c + '">' + a + "</code>"
		})), b)
	}

	function y(a, b) {
		var c = a.toString();
		while (c.length < b)
		c = "0" + c;
		return c
	}

	function z(a, b) {
		var c = "";
		for (var d = 0; d < b; d++)
			c += " ";
		return a.replace(/\t/g, c)
	}

	function A(a, b) {
		function h(a, b, c) {
			return a.substr(0, b) + e.substr(0, c) + a.substr(b + 1, a.length)
		}

		var c = f(a), d = "\t", e = "";
		for (var g = 0; g < 50; g++)
			e += "                    ";
		return a = u(a, function(a) {
			if (a.indexOf(d) == -1)
				return a;
			var c = 0;
			while (( c = a.indexOf(d)) != -1) {
				var e = b - c % b;
				a = h(a, c, e)
			}
			return a
		}), a
	}

	function B(b) {
		var c = /<br\s*\/?>|&lt;br\s*\/?&gt;/gi;
		return a.config.bloggerMode == 1 && ( b = b.replace(c, "\n")), a.config.stripBrs == 1 && ( b = b.replace(c, "")), b
	}

	function C(a) {
		return a.replace(/^\s+|\s+$/g, "")
	}

	function D(a) {
		var b = f(B(a)), c = new Array, d = /^\s*/, e = 1e3;
		for (var g = 0; g < b.length && e > 0; g++) {
			var h = b[g];
			if (C(h).length == 0)
				continue;
			var i = d.exec(h);
			if (i == null)
				return a;
			e = Math.min(i[0].length, e)
		}
		if (e > 0)
			for (var g = 0; g < b.length; g++)
				b[g] = b[g].substr(e);
		return b.join("\n")
	}

	function E(a, b) {
		return a.index < b.index ? -1 : a.index > b.index ? 1 : a.length < b.length ? -1 : a.length > b.length ? 1 : 0
	}

	function F(b, c) {
		function d(a, b) {
			return a[0]
		}

		var e = 0, f = null, g = [], h = c.func ? c.func : d;
		while (( f = c.regex.exec(b)) != null) {
			var i = h(f, c);
			typeof i == "string" && ( i = [new a.Match(i, f.index, c.css)]), g = g.concat(i)
		}
		return g
	}

	function G(b) {
		var c = /(.*)((&gt;|&lt;).*)/;
		return b.replace(a.regexLib.url, function(a) {
			var b = "", d = null;
			if ( d = c.exec(a))
				a = d[1], b = d[2];
			return '<a href="' + a + '">' + a + "</a>" + b
		})
	}

	function H() {
		var a = document.getElementsByTagName("script"), b = [];
		for (var c = 0; c < a.length; c++)
			a[c].type == "syntaxhighlighter" && b.push(a[c]);
		return b
	}

	function I(a) {
		var b = "<![CDATA[", c = "]]>", d = C(a), e = !1, f = b.length, g = c.length;
		d.indexOf(b) == 0 && ( d = d.substring(f), e = !0);
		var h = d.length;
		return d.indexOf(c) == h - g && ( d = d.substring(0, h - g), e = !0), e ? d : a
	}

	function J(a) {
		var b = a.target, e = l(b, ".syntaxhighlighter"), f = l(b, ".container"), g = document.createElement("textarea"), i;
		if (!f || !e || k(f, "textarea"))
			return;
		i = h(e.id), c(e, "source");
		var j = f.childNodes, m = [];
		for (var n = 0; n < j.length; n++)
			m.push(j[n].innerText || j[n].textContent);
		m = m.join("\r"), g.appendChild(document.createTextNode(m)), f.appendChild(g), g.focus(), g.select(), r(g, "blur", function(a) {
			g.parentNode.removeChild(g), d(e, "source")
		})
	}
	typeof require != "undefined" && typeof XRegExp == "undefined" && ( XRegExp = require("XRegExp").XRegExp);
	var a = {
		defaults : {
			"class-name" : "",
			"first-line" : 1,
			"pad-line-numbers" : !1,
			highlight : null,
			title : null,
			"smart-tabs" : !0,
			"tab-size" : 4,
			gutter : !0,
			toolbar : !0,
			"quick-code" : !0,
			collapse : !1,
			"auto-links" : !0,
			light : !1,
			"html-script" : !1
		},
		config : {
			space : "&nbsp;",
			useScriptTags : !0,
			bloggerMode : !1,
			stripBrs : !1,
			tagName : "pre",
			strings : {
				expandSource : "expand source",
				help : "?",
				alert : "SyntaxHighlighter\n\n",
				noBrush : "Can't find brush for: ",
				brushNotHtmlScript : "Brush wasn't configured for html-script option: ",
				aboutDialog : '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"><html xmlns="http://www.w3.org/1999/xhtml"><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8" /><title>About SyntaxHighlighter</title></head><body style="font-family:Geneva,Arial,Helvetica,sans-serif;background-color:#fff;color:#000;font-size:1em;text-align:center;"><div style="text-align:center;margin-top:1.5em;"><div style="font-size:xx-large;">SyntaxHighlighter</div><div style="font-size:.75em;margin-bottom:3em;"><div>version 3.0.83 (July 02 2010)</div><div><a href="http://alexgorbatchev.com/SyntaxHighlighter" target="_blank" style="color:#005896">http://alexgorbatchev.com/SyntaxHighlighter</a></div><div>JavaScript code syntax highlighter.</div><div>Copyright 2004-2010 Alex Gorbatchev.</div></div><div>If you like this script, please <a href="https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=2930402" style="color:#005896">donate</a> to <br/>keep development active!</div></div></body></html>'
			}
		},
		vars : {
			discoveredBrushes : null,
			highlighters : {}
		},
		brushes : {},
		regexLib : {
			multiLineCComments : /\/\*[\s\S]*?\*\//gm,
			singleLineCComments : /\/\/.*$/gm,
			singleLinePerlComments : /#.*$/gm,
			doubleQuotedString : /"([^\\"\n]|\\.)*"/g,
			singleQuotedString : /'([^\\'\n]|\\.)*'/g,
			multiLineDoubleQuotedString : new XRegExp('"([^\\\\"]|\\\\.)*"', "gs"),
			multiLineSingleQuotedString : new XRegExp("'([^\\\\']|\\\\.)*'", "gs"),
			xmlComments : /(&lt;|<)!--[\s\S]*?--(&gt;|>)/gm,
			url : /\w+:\/\/[\w-.\/?%&=:@;]*/g,
			phpScriptTags : {
				left : /(&lt;|<)\?=?/g,
				right : /\?(&gt;|>)/g
			},
			aspScriptTags : {
				left : /(&lt;|<)%=?/g,
				right : /%(&gt;|>)/g
			},
			scriptScriptTags : {
				left : /(&lt;|<)\s*script.*?(&gt;|>)/gi,
				right : /(&lt;|<)\/\s*script\s*(&gt;|>)/gi
			}
		},
		toolbar : {
			getHtml : function(b) {
				function f(b, c) {
					return a.toolbar.getButtonHtml(b, c, a.config.strings[c])
				}

				var c = '<div class="toolbar">', d = a.toolbar.items, e = d.list;
				for (var g = 0; g < e.length; g++)
					c += (d[e[g]].getHtml || f)(b, e[g]);
				return c += "</div>", c
			},
			getButtonHtml : function(a, b, c) {
				return '<span><a href="#" class="toolbar_item command_' + b + " " + b + '">' + c + "</a></span>"
			},
			handler : function(b) {
				function e(a) {
					var b = new RegExp(a + "_(\\w+)"), c = b.exec(d);
					return c ? c[1] : null
				}

				var c = b.target, d = c.className || "", f = h(l(c, ".syntaxhighlighter").id), g = e("command");
				f && g && a.toolbar.items[g].execute(f), b.preventDefault()
			},
			items : {
				list : ["expandSource", "help"],
				expandSource : {
					getHtml : function(b) {
						if (b.getParam("collapse") != 1)
							return "";
						var c = b.getParam("title");
						return a.toolbar.getButtonHtml(b, "expandSource", c ? c : a.config.strings.expandSource)
					},
					execute : function(a) {
						var b = i(a.id);
						d(b, "collapsed")
					}
				},
				help : {
					execute : function(b) {
						var c = q("", "_blank", 500, 250, "scrollbars=0"), d = c.document;
						d.write(a.config.strings.aboutDialog), d.close(), c.focus()
					}
				}
			}
		},
		findElements : function(b, c) {
			var d = c ? [c] : e(document.getElementsByTagName(a.config.tagName)), f = a.config, g = [];
			f.useScriptTags && ( d = d.concat(H()));
			if (d.length === 0)
				return g;
			for (var h = 0; h < d.length; h++) {
				var i = {
					target : d[h],
					params : o(b, w(d[h].className))
				};
				if (i.params["brush"] == null)
					continue;
				g.push(i)
			}
			return g
		},
		highlight : function(b, c) {
			var d = this.findElements(b, c), e = "innerHTML", f = null, g = a.config;
			if (d.length === 0)
				return;
			for (var h = 0; h < d.length; h++) {
				var c = d[h], i = c.target, j = c.params, k = j.brush, l;
				if (k == null)
					continue;
				if (j["html-script"] == "true" || a.defaults["html-script"] == 1)
					f = new a.HtmlScript(k), k = "htmlscript";
				else {
					var m = t(k);
					if (m)
						f = new m;
					else
						continue
				}
				l = i[e], g.useScriptTags && ( l = I(l)), (i.title || "") != "" && (j.title = i.title), j.brush = k, f.init(j), c = f.getDiv(l), (i.id || "") != "" && (c.id = i.id), i.parentNode.replaceChild(c, i)
			}
		},
		all : function(b) {
			r(window, "load", function() {
				a.highlight(b)
			})
		}
	};
	return a.all = a.all, a.highlight = a.highlight, a.Match = function(a, b, c) {
		this.value = a, this.index = b, this.length = a.length, this.css = c, this.brushName = null
	}, a.Match.prototype.toString = function() {
		return this.value
	}, a.HtmlScript = function(b) {
		function j(a, b) {
			for (var c = 0; c < a.length; c++)
				a[c].index += b
		}

		function k(a, b) {
			var e = a.code, f = [], g = d.regexList, h = a.index + a.left.length, i = d.htmlScript, k;
			for (var l = 0; l < g.length; l++)
				k = F(e, g[l]), j(k, h), f = f.concat(k);
			i.left != null && a.left != null && ( k = F(a.left, i.left), j(k, a.index), f = f.concat(k)), i.right != null && a.right != null && ( k = F(a.right, i.right), j(k, a.index + a[0].lastIndexOf(a.right)), f = f.concat(k));
			for (var m = 0; m < f.length; m++)
				f[m].brushName = c.brushName;
			return f
		}

		var c = t(b), d, e = new a.brushes.Xml, f = null, g = this, h = "getDiv getHtml init".split(" ");
		if (c == null)
			return;
		d = new c;
		for (var i = 0; i < h.length; i++)
			(function() {
				var a = h[i];
				g[a] = function() {
					return e[a].apply(e, arguments)
				}
			})();
		if (d.htmlScript == null) {
			s(a.config.strings.brushNotHtmlScript + b);
			return
		}
		e.regexList.push({
			regex : d.htmlScript.code,
			func : k
		})
	}, a.Highlighter = function() {
	}, a.Highlighter.prototype = {
		getParam : function(a, b) {
			var c = this.params[a];
			return p(c == null ? b : c)
		},
		create : function(a) {
			return document.createElement(a)
		},
		findMatches : function(a, b) {
			var c = [];
			if (a != null)
				for (var d = 0; d < a.length; d++)
					typeof a[d] == "object" && ( c = c.concat(F(b, a[d])));
			return this.removeNestedMatches(c.sort(E))
		},
		removeNestedMatches : function(a) {
			for (var b = 0; b < a.length; b++) {
				if (a[b] === null)
					continue;
				var c = a[b], d = c.index + c.length;
				for (var e = b + 1; e < a.length && a[b] !== null; e++) {
					var f = a[e];
					if (f === null)
						continue;
					if (f.index > d)
						break;
					f.index == c.index && f.length > c.length ? a[b] = null : f.index >= c.index && f.index < d && (a[e] = null)
				}
			}
			return a
		},
		figureOutLineNumbers : function(a) {
			var b = [], c = parseInt(this.getParam("first-line"));
			return u(a, function(a, d) {
				b.push(d + c)
			}), b
		},
		isLineHighlighted : function(a) {
			var b = this.getParam("highlight", []);
			return typeof b != "object" && b.push == null && ( b = [b]), m(b, a.toString()) != -1
		},
		getLineHtml : function(a, b, c) {
			var d = ["line", "number" + b, "index" + a, "alt" + (b % 2 == 0 ? 1 : 2).toString()];
			return this.isLineHighlighted(b) && d.push("highlighted"), b == 0 && d.push("break"), '<div class="' + d.join(" ") + '">' + c + "</div>"
		},
		getLineNumbersHtml : function(b, c) {
			var d = "", e = f(b).length, g = parseInt(this.getParam("first-line")), h = this.getParam("pad-line-numbers");
			h == 1 ? h = (g + e - 1).toString().length : isNaN(h) == 1 && ( h = 0);
			for (var i = 0; i < e; i++) {
				var j = c ? c[i] : g + i, b = j == 0 ? a.config.space : y(j, h);
				d += this.getLineHtml(i, j, b)
			}
			return d
		},
		getCodeLinesHtml : function(b, c) {
			b = C(b);
			var d = f(b), e = this.getParam("pad-line-numbers"), g = parseInt(this.getParam("first-line")), b = "", h = this.getParam("brush");
			for (var i = 0; i < d.length; i++) {
				var j = d[i], k = /^(&nbsp;|\s)+/.exec(j), l = null, m = c ? c[i] : g + i;
				k != null && ( l = k[0].toString(), j = j.substr(l.length), l = l.replace(" ", a.config.space)), j = C(j), j.length == 0 && ( j = a.config.space), b += this.getLineHtml(i, m, (l != null ? '<code class="' + h + ' spaces">' + l + "</code>" : "") + j)
			}
			return b
		},
		getTitleHtml : function(a) {
			return a ? "<caption>" + a + "</caption>" : ""
		},
		getMatchesHtml : function(a, b) {
			function f(a) {
				var b = a ? a.brushName || e : e;
				return b ? b + " " : ""
			}

			var c = 0, d = "", e = this.getParam("brush", "");
			for (var g = 0; g < b.length; g++) {
				var h = b[g], i;
				if (h === null || h.length === 0)
					continue;
				i = f(h), d += x(a.substr(c, h.index - c), i + "plain") + x(h.value, i + h.css), c = h.index + h.length + (h.offset || 0)
			}
			return d += x(a.substr(c), f() + "plain"), d
		},
		getHtml : function(b) {
			var c = "", d = ["syntaxhighlighter"], e, f, h;
			return this.getParam("light") == 1 && (this.params.toolbar = this.params.gutter = !1), className = "syntaxhighlighter", this.getParam("collapse") == 1 && d.push("collapsed"), ( gutter = this.getParam("gutter")) == 0 && d.push("nogutter"), d.push(this.getParam("class-name")), d.push(this.getParam("brush")), b = v(b).replace(/\r/g, " "), e = this.getParam("tab-size"), b = this.getParam("smart-tabs") == 1 ? A(b, e) : z(b, e), b = D(b), gutter && ( h = this.figureOutLineNumbers(b)), f = this.findMatches(this.regexList, b), c = this.getMatchesHtml(b, f), c = this.getCodeLinesHtml(c, h), this.getParam("auto-links") && ( c = G(c)), typeof navigator != "undefined" && navigator.userAgent && navigator.userAgent.match(/MSIE/) && d.push("ie"), c = '<div id="' + g(this.id) + '" class="' + d.join(" ") + '">' + (this.getParam("toolbar") ? a.toolbar.getHtml(this) : "") + '<table border="0" cellpadding="0" cellspacing="0">' + this.getTitleHtml(this.getParam("title")) + "<tbody>" + "<tr>" + ( gutter ? '<td class="gutter">' + this.getLineNumbersHtml(b) + "</td>" : "") + '<td class="code">' + '<div class="container">' + c + "</div>" + "</td>" + "</tr>" + "</tbody>" + "</table>" + "</div>", c
		},
		getDiv : function(b) {
			b === null && ( b = ""), this.code = b;
			var c = this.create("div");
			return c.innerHTML = this.getHtml(b), this.getParam("toolbar") && r(k(c, ".toolbar"), "click", a.toolbar.handler), this.getParam("quick-code") && r(k(c, ".code"), "dblclick", J), c
		},
		init : function(b) {
			this.id = n(), j(this), this.params = o(a.defaults, b || {}), this.getParam("light") == 1 && (this.params.toolbar = this.params.gutter = !1)
		},
		getKeywords : function(a) {
			return a = a.replace(/^\s+|\s+$/g, "").replace(/\s+/g, "|"), "\\b(?:" + a + ")\\b"
		},
		forHtmlScript : function(a) {
			this.htmlScript = {
				left : {
					regex : a.left,
					css : "script"
				},
				right : {
					regex : a.right,
					css : "script"
				},
				code : new XRegExp("(?<left>" + a.left.source + ")" + "(?<code>.*?)" + "(?<right>" + a.right.source + ")", "sgi")
			}
		}
	}, a
}();
typeof exports != "undefined" ? exports.SyntaxHighlighter = SyntaxHighlighter : null;
