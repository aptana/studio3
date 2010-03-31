/*
 *
 * This program is Copyright (C) 2009 Aptana, Inc. All Rights Reserved
 *
 */

//alert('IMPORTANT: Remove this line from json2.js before deployment.');
/*
    http://www.JSON.org/json2.js
    2009-09-29

    Public Domain.

    NO WARRANTY EXPRESSED OR IMPLIED. USE AT YOUR OWN RISK.

    See http://www.JSON.org/js.html


    This code should be minified before deployment.
    See http://javascript.crockford.com/jsmin.html

    USE YOUR OWN COPY. IT IS EXTREMELY UNWISE TO LOAD CODE FROM SERVERS YOU DO
    NOT CONTROL.


    This file creates a global JSON object containing two methods: stringify
    and parse.

        JSON.stringify(value, replacer, space)
            value       any JavaScript value, usually an object or array.

            replacer    an optional parameter that determines how object
                        values are stringified for objects. It can be a
                        function or an array of strings.

            space       an optional parameter that specifies the indentation
                        of nested structures. If it is omitted, the text will
                        be packed without extra whitespace. If it is a number,
                        it will specify the number of spaces to indent at each
                        level. If it is a string (such as '\t' or '&nbsp;'),
                        it contains the characters used to indent at each level.

            This method produces a JSON text from a JavaScript value.

            When an object value is found, if the object contains a toJSON
            method, its toJSON method will be called and the result will be
            stringified. A toJSON method does not serialize: it returns the
            value represented by the name/value pair that should be serialized,
            or undefined if nothing should be serialized. The toJSON method
            will be passed the key associated with the value, and this will be
            bound to the value

            For example, this would serialize Dates as ISO strings.

                Date.prototype.toJSON = function (key) {
                    function f(n) {
                        // Format integers to have at least two digits.
                        return n < 10 ? '0' + n : n;
                    }

                    return this.getUTCFullYear()   + '-' +
                         f(this.getUTCMonth() + 1) + '-' +
                         f(this.getUTCDate())      + 'T' +
                         f(this.getUTCHours())     + ':' +
                         f(this.getUTCMinutes())   + ':' +
                         f(this.getUTCSeconds())   + 'Z';
                };

            You can provide an optional replacer method. It will be passed the
            key and value of each member, with this bound to the containing
            object. The value that is returned from your method will be
            serialized. If your method returns undefined, then the member will
            be excluded from the serialization.

            If the replacer parameter is an array of strings, then it will be
            used to select the members to be serialized. It filters the results
            such that only members with keys listed in the replacer array are
            stringified.

            Values that do not have JSON representations, such as undefined or
            functions, will not be serialized. Such values in objects will be
            dropped; in arrays they will be replaced with null. You can use
            a replacer function to replace those with JSON values.
            JSON.stringify(undefined) returns undefined.

            The optional space parameter produces a stringification of the
            value that is filled with line breaks and indentation to make it
            easier to read.

            If the space parameter is a non-empty string, then that string will
            be used for indentation. If the space parameter is a number, then
            the indentation will be that many spaces.

            Example:

            text = JSON.stringify(['e', {pluribus: 'unum'}]);
            // text is '["e",{"pluribus":"unum"}]'


            text = JSON.stringify(['e', {pluribus: 'unum'}], null, '\t');
            // text is '[\n\t"e",\n\t{\n\t\t"pluribus": "unum"\n\t}\n]'

            text = JSON.stringify([new Date()], function (key, value) {
                return this[key] instanceof Date ?
                    'Date(' + this[key] + ')' : value;
            });
            // text is '["Date(---current time---)"]'


        JSON.parse(text, reviver)
            This method parses a JSON text to produce an object or array.
            It can throw a SyntaxError exception.

            The optional reviver parameter is a function that can filter and
            transform the results. It receives each of the keys and values,
            and its return value is used instead of the original value.
            If it returns what it received, then the structure is not modified.
            If it returns undefined then the member is deleted.

            Example:

            // Parse the text. Values that look like ISO date strings will
            // be converted to Date objects.

            myData = JSON.parse(text, function (key, value) {
                var a;
                if (typeof value === 'string') {
                    a =
/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2}(?:\.\d*)?)Z$/.exec(value);
                    if (a) {
                        return new Date(Date.UTC(+a[1], +a[2] - 1, +a[3], +a[4],
                            +a[5], +a[6]));
                    }
                }
                return value;
            });

            myData = JSON.parse('["Date(09/09/2001)"]', function (key, value) {
                var d;
                if (typeof value === 'string' &&
                        value.slice(0, 5) === 'Date(' &&
                        value.slice(-1) === ')') {
                    d = new Date(value.slice(5, -1));
                    if (d) {
                        return d;
                    }
                }
                return value;
            });


    This is a reference implementation. You are free to copy, modify, or
    redistribute.
*/

/*jslint evil: true, strict: false */

/*members "", "\b", "\t", "\n", "\f", "\r", "\"", JSON, "\\", apply,
    call, charCodeAt, getUTCDate, getUTCFullYear, getUTCHours,
    getUTCMinutes, getUTCMonth, getUTCSeconds, hasOwnProperty, join,
    lastIndex, length, parse, prototype, push, replace, slice, stringify,
    test, toJSON, toString, valueOf
*/


// Create a JSON object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.JSON) {
    this.JSON = {};
}

(function () {

    function f(n) {
        // Format integers to have at least two digits.
        return n < 10 ? '0' + n : n;
    }

    if (typeof Date.prototype.toJSON !== 'function') {

        Date.prototype.toJSON = function (key) {

            return isFinite(this.valueOf()) ?
                   this.getUTCFullYear()   + '-' +
                 f(this.getUTCMonth() + 1) + '-' +
                 f(this.getUTCDate())      + 'T' +
                 f(this.getUTCHours())     + ':' +
                 f(this.getUTCMinutes())   + ':' +
                 f(this.getUTCSeconds())   + 'Z' : null;
        };

        String.prototype.toJSON =
        Number.prototype.toJSON =
        Boolean.prototype.toJSON = function (key) {
            return this.valueOf();
        };
    }

    var cx = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        gap,
        indent,
        meta = {    // table of character substitutions
            '\b': '\\b',
            '\t': '\\t',
            '\n': '\\n',
            '\f': '\\f',
            '\r': '\\r',
            '"' : '\\"',
            '\\': '\\\\'
        },
        rep;


    function quote(string) {

// If the string contains no control characters, no quote characters, and no
// backslash characters, then we can safely slap some quotes around it.
// Otherwise we must also replace the offending characters with safe escape
// sequences.

        escapable.lastIndex = 0;
        return escapable.test(string) ?
            '"' + string.replace(escapable, function (a) {
                var c = meta[a];
                return typeof c === 'string' ? c :
                    '\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
            }) + '"' :
            '"' + string + '"';
    }


    function str(key, holder) {

// Produce a string from holder[key].

        var i,          // The loop counter.
            k,          // The member key.
            v,          // The member value.
            length,
            mind = gap,
            partial,
            value = holder[key];

// If the value has a toJSON method, call it to obtain a replacement value.

        if (value && typeof value === 'object' &&
                typeof value.toJSON === 'function') {
            value = value.toJSON(key);
        }

// If we were called with a replacer function, then call the replacer to
// obtain a replacement value.

        if (typeof rep === 'function') {
            value = rep.call(holder, key, value);
        }

// What happens next depends on the value's type.

        switch (typeof value) {
        case 'string':
            return quote(value);

        case 'number':

// JSON numbers must be finite. Encode non-finite numbers as null.

            return isFinite(value) ? String(value) : 'null';

        case 'boolean':
        case 'null':

// If the value is a boolean or null, convert it to a string. Note:
// typeof null does not produce 'null'. The case is included here in
// the remote chance that this gets fixed someday.

            return String(value);

// If the type is 'object', we might be dealing with an object or an array or
// null.

        case 'object':

// Due to a specification blunder in ECMAScript, typeof null is 'object',
// so watch out for that case.

            if (!value) {
                return 'null';
            }

// Make an array to hold the partial results of stringifying this object value.

            gap += indent;
            partial = [];

// Is the value an array?

            if (Object.prototype.toString.apply(value) === '[object Array]') {

// The value is an array. Stringify every element. Use null as a placeholder
// for non-JSON values.

                length = value.length;
                for (i = 0; i < length; i += 1) {
                    partial[i] = str(i, value) || 'null';
                }

// Join all of the elements together, separated with commas, and wrap them in
// brackets.

                v = partial.length === 0 ? '[]' :
                    gap ? '[\n' + gap +
                            partial.join(',\n' + gap) + '\n' +
                                mind + ']' :
                          '[' + partial.join(',') + ']';
                gap = mind;
                return v;
            }

// If the replacer is an array, use it to select the members to be stringified.

            if (rep && typeof rep === 'object') {
                length = rep.length;
                for (i = 0; i < length; i += 1) {
                    k = rep[i];
                    if (typeof k === 'string') {
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }
            } else {

// Otherwise, iterate through all of the keys in the object.

                for (k in value) {
                    if (Object.hasOwnProperty.call(value, k)) {
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }
            }

// Join all of the member texts together, separated with commas,
// and wrap them in braces.

            v = partial.length === 0 ? '{}' :
                gap ? '{\n' + gap + partial.join(',\n' + gap) + '\n' +
                        mind + '}' : '{' + partial.join(',') + '}';
            gap = mind;
            return v;
        }
    }

// If the JSON object does not yet have a stringify method, give it one.

    if (typeof JSON.stringify !== 'function') {
        JSON.stringify = function (value, replacer, space) {

// The stringify method takes a value and an optional replacer, and an optional
// space parameter, and returns a JSON text. The replacer can be a function
// that can replace values, or an array of strings that will select the keys.
// A default replacer method can be provided. Use of the space parameter can
// produce text that is more easily readable.

            var i;
            gap = '';
            indent = '';

// If the space parameter is a number, make an indent string containing that
// many spaces.

            if (typeof space === 'number') {
                for (i = 0; i < space; i += 1) {
                    indent += ' ';
                }

// If the space parameter is a string, it will be used as the indent string.

            } else if (typeof space === 'string') {
                indent = space;
            }

// If there is a replacer, it must be a function or an array.
// Otherwise, throw an error.

            rep = replacer;
            if (replacer && typeof replacer !== 'function' &&
                    (typeof replacer !== 'object' ||
                     typeof replacer.length !== 'number')) {
                throw new Error('JSON.stringify');
            }

// Make a fake root object containing our value under the key of ''.
// Return the result of stringifying the value.

            return str('', {'': value});
        };
    }


// If the JSON object does not yet have a parse method, give it one.

    if (typeof JSON.parse !== 'function') {
        JSON.parse = function (text, reviver) {

// The parse method takes a text and an optional reviver function, and returns
// a JavaScript value if the text is a valid JSON text.

            var j;

            function walk(holder, key) {

// The walk method is used to recursively walk the resulting structure so
// that modifications can be made.

                var k, v, value = holder[key];
                if (value && typeof value === 'object') {
                    for (k in value) {
                        if (Object.hasOwnProperty.call(value, k)) {
                            v = walk(value, k);
                            if (v !== undefined) {
                                value[k] = v;
                            } else {
                                delete value[k];
                            }
                        }
                    }
                }
                return reviver.call(holder, key, value);
            }


// Parsing happens in four stages. In the first stage, we replace certain
// Unicode characters with escape sequences. JavaScript handles many characters
// incorrectly, either silently deleting them, or treating them as line endings.

            cx.lastIndex = 0;
            if (cx.test(text)) {
                text = text.replace(cx, function (a) {
                    return '\\u' +
                        ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
                });
            }

// In the second stage, we run the text against regular expressions that look
// for non-JSON patterns. We are especially concerned with '()' and 'new'
// because they can cause invocation, and '=' because it can cause mutation.
// But just to be safe, we want to reject all unexpected forms.

// We split the second stage into 4 regexp operations in order to work around
// crippling inefficiencies in IE's and Safari's regexp engines. First we
// replace the JSON backslash pairs with '@' (a non-JSON character). Second, we
// replace all simple value tokens with ']' characters. Third, we delete all
// open brackets that follow a colon or comma or that begin the text. Finally,
// we look to see that the remaining characters are only whitespace or ']' or
// ',' or ':' or '{' or '}'. If that is so, then the text is safe for eval.

            if (/^[\],:{}\s]*$/.
test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@').
replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').
replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {

// In the third stage we use the eval function to compile the text into a
// JavaScript structure. The '{' operator is subject to a syntactic ambiguity
// in JavaScript: it can begin a block or an object literal. We wrap the text
// in parens to eliminate the ambiguity.

                j = eval('(' + text + ')');

// In the optional fourth stage, we recursively walk the new structure, passing
// each name/value pair to a reviver function for possible transformation.

                return typeof reviver === 'function' ?
                    walk({'': j}, '') : j;
            }

// If the text is not JSON parseable, then a SyntaxError is thrown.

            throw new SyntaxError('JSON.parse');
        };
    }
}());

/**
 * BrowserDetect script from http://www.quirksmode.org/js/detect.html
 */
var BrowserDetect = {
	init: function () {
		this.browser = this.searchString(this.dataBrowser) || "An unknown browser";
		this.version = this.searchVersion(navigator.userAgent)
			|| this.searchVersion(navigator.appVersion)
			|| "an unknown version";
		this.OS = this.searchString(this.dataOS) || "an unknown OS";
	},
	searchString: function (data) {
		for (var i=0;i<data.length;i++)	{
			var dataString = data[i].string;
			var dataProp = data[i].prop;
			this.versionSearchString = data[i].versionSearch || data[i].identity;
			if (dataString) {
				if (dataString.indexOf(data[i].subString) != -1)
					return data[i].identity;
			}
			else if (dataProp)
				return data[i].identity;
		}
	},
	searchVersion: function (dataString) {
		var index = dataString.indexOf(this.versionSearchString);
		if (index == -1) return;
		return parseFloat(dataString.substring(index+this.versionSearchString.length+1));
	},
	dataBrowser: [
		{
			string: navigator.userAgent,
			subString: "Chrome",
			identity: "Chrome"
		},
		{ 	string: navigator.userAgent,
			subString: "OmniWeb",
			versionSearch: "OmniWeb/",
			identity: "OmniWeb"
		},
		{
			string: navigator.vendor,
			subString: "Apple",
			identity: "Safari",
			versionSearch: "Version"
		},
		{
			prop: window.opera,
			identity: "Opera"
		},
		{
			string: navigator.vendor,
			subString: "iCab",
			identity: "iCab"
		},
		{
			string: navigator.vendor,
			subString: "KDE",
			identity: "Konqueror"
		},
		{
			string: navigator.userAgent,
			subString: "Firefox",
			identity: "Firefox"
		},
		{
			string: navigator.vendor,
			subString: "Camino",
			identity: "Camino"
		},
		{		// for newer Netscapes (6+)
			string: navigator.userAgent,
			subString: "Netscape",
			identity: "Netscape"
		},
		{
			string: navigator.userAgent,
			subString: "MSIE",
			identity: "Explorer",
			versionSearch: "MSIE"
		},
		{
			string: navigator.userAgent,
			subString: "Gecko",
			identity: "Mozilla",
			versionSearch: "rv"
		},
		{ 		// for older Netscapes (4-)
			string: navigator.userAgent,
			subString: "Mozilla",
			identity: "Netscape",
			versionSearch: "Mozilla"
		}
	],
	dataOS : [
		{
			string: navigator.platform,
			subString: "Win",
			identity: "Windows"
		},
		{
			string: navigator.platform,
			subString: "Mac",
			identity: "Mac"
		},
		{
			   string: navigator.userAgent,
			   subString: "iPhone",
			   identity: "iPhone/iPod"
	    },
		{
			string: navigator.platform,
			subString: "Linux",
			identity: "Linux"
		}
	]

};
BrowserDetect.init();


/**
 * Clamp the specified value to fit within the close interval specified by min
 * and max
 * 
 * @alias window.clamp
 * @param {Number} value
 * @param {Number} min
 * @param {Number} max
 */
function clamp(value, min, max)
{
	var result = value;
	
	if (isNumber(value) && isNumber(min) && isNumber(max))
	{
		result = Math.min(max, Math.max(min, value));
	}
	
	return result;
}

/**
 * Create an XMLHttpRequest object
 * 
 * @alias window.createXHR
 */
function createXHR()
{
	var req;
	
	if (window.XMLHttpRequest) 
    {
        req = new XMLHttpRequest();
    }
    else 
    {
        req = new ActiveXObject("Microsoft.XMLHTTP");
    }    
    
    if (req.overrideMimeType)
    {
    	req.overrideMimeType("text/plain");
    }
	
	return req;
}

/**
 * Build a URL from the given address and collection of entries (key/value pairs)
 * 
 * @param {String} url
 * @param {Object} entries
 */
function createURL(url, entries)
{
	var result = url;
	var query = [];
	
	if (isDefined(entries))
	{
		for (var k in entries)
		{
			if (entries.hasOwnProperty(k))
			{
				query.push(k + "=" + entries[k]);
			}
		}
	}
	
	if (query.length > 0)
	{
		result += "?" + query.join("&");
	}
	
	return result;
}

function getCSSRule(name)
{
	var rule = null;
	
	var styleSheet = document.styleSheets[0];
	var rules;
	
	if (styleSheet.cssRules)
	{
		rules = styleSheet.cssRules;
	}
	else if (styleSheet.rules)
	{
		rules = styleSheet.rules
	}
	
	for (var i = 0; i < rules.length; i++)
	{
		if (rules[i].selectorText == name)
		{
			rule = rules[i];
			break;
		}
	}	
	
	return rule;
}

/**
 * Calculate the width of the HTML window
 *
 * @return {Number}
 */
function getWindowWidth()
{
	var result = 0;
	
	if (window.innerWidth)
	{
		result = window.innerWidth;
	}
	else if (document.documentElement && document.documentElement.clientWidth)
	{
		result = document.documentElement.clientWidth;
	}
	else if (document.body && document.body.clientWidth)
	{
		result = document.body.clientWidth;
	}
	
	return result;
}

/**
 * Calculate the height of the HTML window
 *
 * @return {Number}
 */
function getWindowHeight()
{
	var result = 0;

	if (window.innerHeight)
	{
		result = window.innerHeight;
	}
	else if (document.documentElement && document.documentElement.clientHeight)
	{
		result = document.documentElement.clientHeight;
	}
	else if (document.body && document.body.clientHeight)
	{
		result = document.body.clientHeight;
	}
	
	return result;
}	

/**
 * getQuery
 */
function getQuery()
{
	var query = window.location.search.substring(1);
	var keypairs = query.split("&");
	var result = {};
	
	for (var i = 0; i < keypairs.length; i++)
	{
		var parts = keypairs[i].split("=");
		var key = parts[0];
		var value = parts[1];
		
		result[key] = value;
	}
	
	return result;
}

/**
 * Test that the specified value is a boolean value
 * 
 * @alias window.isBoolean
 * @param {Boolean} b
 */
function isBoolean(b)
{
	return b !== null && b !== undefined && b.constructor === Boolean;
}

/**
 * Test that the specified value is a String with at least one character
 * 
 * @alias window.isCharacter
 * @param {String} ch
 */
function isCharacter(ch)
{
	return ch !== null && ch !== undefined && ch.constructor === String && ch.length > 0;
}

/**
 * Test that the specified value is defined
 * 
 * @alias window.isDefined
 * @param {Object} o
 */
function isDefined(o)
{
	return o !== null && o !== undefined;
}

/**
 * Test that the specified value is a Function
 * 
 * @param {Function} f
 */
function isFunction(f)
{
	return f !== null && f !== undefined && f.constructor === Function;
}

/**
 * Test that the specified value is a Number
 * 
 * @alias window.isNumber
 * @param {Number} n
 */
function isNumber(n)
{
	return n !== null && n !== undefined && n.constructor === Number;
}

/**
 * Test that the specified value is a String
 * 
 * @alias window.isString
 * @param {String} s
 */
function isString(s)
{
	return s !== null && s !== undefined && s.constructor === String;
}

/**
 * Creates a new object whose private prototype (the one used when looking up
 * property values) will be set to the object passed into this function. This
 * allows for the resulting clone object to add new properties and to redefine
 * property values without affecting the master object. If access to the master
 * object is required, the cloned object contains a '$parent' property which
 * can be used for that purpose.
 * 
 * @alais window.protectedClone
 * @param {Object} master
 * @return {Object}
 * 		Returns a new object that effectively inherits all properties of the
 * 		passed in object via JavaScript's prototype inheritance mechanism.
 */
function protectedClone(master)
{
	// anonymous object creator
	var f = function() {};
	
	// attach original object to prototype
	f.prototype = master;
	
	// create a new object whose [proto] now points to the original object
	var result = new f();
	
	// make a local reference to the master in cases where we need to unroll
	// the prototype chain
	result.$parent = master;
	
	// return result;
	return result;
};

/**
 * @classDescription {Dragger} A "part" that implements dragging support. A
 * callback mechanism is provided so any code using this class can update as the
 * user drags over an item
 */

function dragger(node, callback)
{
	// state variables
	var startX = null, startY = null;
	var endX, endY;
	var lastX = null, lastY = null;
	
	// event helpers
	var findLocation = function(obj)
	{
		var curleft = curtop = 0;
		
		if (obj.offsetParent)
		{
			do
			{
				curleft += obj.offsetLeft;
				curtop += obj.offsetTop;
			} while (obj = obj.offsetParent);
		}

		return [curleft,curtop];
	};		
		
	var fireCallback = function(e)
	{
		if (isFunction(callback))
		{
			var offsets = findLocation(node);
			var offsetX = offsets[0];
			var offsetY = offsets[1];
			var fire = false;
			
			// save starting position, first time only
			if (startX === null || startY === null)
			{
				startX = e.clientX - offsetX;
				startY = e.clientY - offsetY;
				fire = true;
			}			
			
			// always update ending position
			endX = e.clientX - offsetX;
			endY = e.clientY - offsetY;
			
			// fire the callback
			if (fire || lastX != endX || lastY != endY)
			{
				callback(startX, startY, endX, endY);
				
				lastX = endX;
				lastY = endY;
			}
		}		
		
		// cancel this event
		return stopEvent(e);
	};	
	
	var addEventListener = function(type, func)
	{
		if (node.addEventListener)
		{
			node.addEventListener(type, func, false);
		}		
		else if (node.attachEvent)
		{
			node.attachEvent("on" + type, func)
		}
	};	
	
	var removeEventListener = function(type, func)
	{
		if (node.removeEventListener)
		{
			node.removeEventListener(type, func, false);
		}		
		else if (node.detachEvent)
		{
			node.detachEvent("on" + type, func);
		}
	};
	
	var stopEvent = function(e)
	{
		if (e) 
		{
			e.cancelBubble = true;
			if (e.stopPropagtion) e.stopPropagation();
			if (e.preventDefault) e.preventDefault();
		}
		
		return false;
	};	
	
	var suppressEvent = function(e)
	{
		if (e) 
		{
			e.cancelBubble = true;
			if (e.stopPropagtion) e.stopPropagation();
		}
		
		return true;
	};	
	
	// mouse handlers
	var mousedown = function(e)
	{
		// only want left-button, cancel the rest
		if (e.button != 0 && e.button != 1)
		{
			return stopEvent(e);
		}
		
		// HACK: This will activate the view/editor in Eclipse
		window.focus();
		
		// update registered events
		removeEventListener("mousedown", mousedown);
	  	addEventListener("mousemove", mousemove);
	  	addEventListener("mouseup", mouseup);
	  	//addEventListener("mouseout", mouseup);
		
		// let the callback know what's going on
		return fireCallback(e);
	};
	
	var mousemove = function(e)
	{
		// let the callback know what's going on
		return fireCallback(e);
	};
	
	var mouseup = function(e)
	{
		// update register events
		removeEventListener("mousemove", mousemove);
		removeEventListener("mouseup", mouseup);
		//removeEventListener("mouseout", mouseup);
		addEventListener("mousedown", mousedown);
		
		// let the callback know what's going on
		var result = fireCallback(e);
		
		// clear cached locations
		startX = startY = endX = endY = lastX = lastY = null;
		
		return result;
	};
	
	if (isDefined(node))
	{
		addEventListener("mousedown", mousedown);
	}
}

/**
 * @classDescription {FontInfo} FontInfo caches character size info
 */

FontInfo.MONOSPACE = "monospace";

/**
 * FontInfo
 *
 * @param {String} id
 */
function FontInfo(id)
{
	id = (id) ? id : "fontInfo";
	
	// TODO: either create or pass into this ctor
	this._rootNode = document.getElementById(id);
	
	if (this._rootNode)
	{
		this._rootNode.className = "fontInfo";
	
		// create pre element and add to root
		this._termNode = document.createElement("pre");
		this._rootNode.appendChild(this._termNode);
	}

	// initialize size cache
	this.reset();

	// initialize flag indicating if we should take tracking into account
	// when reporting character sizes
	this._useTracking;
}

/**
 * Set the reported width and height for all characters. This will also tag this
 * font info as being monospaced.
 *
 * @param {Number} width
 * @param {Number} height
 */
FontInfo.prototype.forceSize = function(width, height)
{
	this._characterSizes[FontInfo.MONOSPACE] = {
		normal: [width, height],
		bold: [width, height]
	};
};

/**
 * getCharacterHeight
 *
 * @param {Character} c
 * @param {String} style
 * @return {Number}
 */
FontInfo.prototype.getCharacterHeight = function(c, style)
{
	var size = this.getCharacterSize(c, style);

	return size[1];
}

/**
 * getCharacterSize
 *
 * @param {Character} c
 * @param {String} style
 * @return {Array}
 */
FontInfo.prototype.getCharacterSize = function(c, style)
{
	var characterSizes = this._characterSizes;

	style = (this._useTracking) ? "normal" : style;

	if (this.isMonospaced())
	{
		c = FontInfo.MONOSPACE;
	}
	else if (characterSizes.hasOwnProperty(c) === false)
	{
		var node = this._termNode;
		var getSize = function(style)
		{
			node.innerHTML = (c == ' ') ? "&nbsp;" : c;
			node.style.fontWeight = style;

			var result = [
				node.clientWidth || node.offsetWidth,
				node.clientHeight || node.offsetHeight
			];

			node.innerHTML = "";

			return result;
		}

		characterSizes[c] = {
			normal: getSize("normal"),
			bold: getSize("bolder")
		};

		//node.innerHTML = "<pre>" + JSON.stringify(characterSizes, null, "\t") + "</pre>";
	}

	return characterSizes[c][style || "normal"];
};

/**
 * getCharacterWidth
 *
 * @param {Character} c
 * @param {String} style
 * @return {Number}
 */
FontInfo.prototype.getCharacterWidth = function(c, style)
{
	var size = this.getCharacterSize(c, style);

	return size[0];
};

/**
 * Return the tracking amount needed to make bold characters take up the same
 * width as normal characters. This value is calculated only if this is a fixed
 * sized font
 *
 * @return {String}
 */
FontInfo.prototype.getTracking = function()
{
	var result = 0;

	if (this.isMonospaced)
	{
		var M = this._characterSizes[FontInfo.MONOSPACE];

		result = M.normal[0] - M.bold[0];
	}

	return result + "px";
}

/**
 * isMonospaced
 *
 * @return {Boolean}
 */
FontInfo.prototype.isMonospaced = function()
{
	return this._characterSizes.hasOwnProperty(FontInfo.MONOSPACE);
};

/**
 * reset
 */
FontInfo.prototype.reset = function()
{
	this._characterSizes = {};

	if (this._rootNode)
	{
		// determine if this is monospaced
		var space = this.getCharacterSize(" ");
		var i = this.getCharacterSize("i");
		var M = this.getCharacterSize("M");
	
		if (space[0] == i[0] && i[0] == M[0])
		{
			this._characterSizes[FontInfo.MONOSPACE] = {
				normal: M,
				bold: this.getCharacterSize("M", "bold")
			};
		}
	}
};

/**
 * Set the flag indicating if FontInfo should take tracking into account. When
 * this is set to true, bold characters will be reported as having normal size
 *
 * @param {Boolean} value
 */
FontInfo.prototype.useTracking = function(value)
{
	this._useTracking = value;
};

/**
 * @classDescription {Attribute} The Attribute class holds text styling
 * information for a single character in a terminal. By default, an attribute
 * has a white background with black text. Styles can be applied by setting the
 * following propreties to true: bold, italic, underline, inverse,
 * strikethrough, and blink. Note that CSS styling is used to apply underline,
 * strikethrough, and blink. These all use the same text-decoration property and
 * as such, only one of these styles can exist at a time. The order that they
 * will be applied, by highest precedence first is underline, strikethrough, and
 * then blink. 
 */

Attribute.DEFAULT_BACKGROUND = "b";
Attribute.DEFAULT_FOREGROUND = "f";

/**
 * Create a new Attribute instance using default values
 * 
 * @constructor
 * @alias Attribute
 */
function Attribute()
{
	this.reset();
}

/**
 * This method makes a new copy of the attribute. The new Attribute will return
 * true when compared with the original with the equals method, but it is
 * completely independent of the original. This allows a new attribute to be
 * created based on another that in turn can be changed without altering the
 * original
 * 
 * @alias Attribute.prototype.copy
 * @return {Attribute}
 */
Attribute.prototype.copy = function()
{
	var result = new Attribute();
	
	result.foreground = this.foreground;
	result.background = this.background;
	result.bold = this.bold;
	result.italic = this.italic;
	result.underline = this.underline;
	result.inverse = this.inverse;
	result.strikethrough = this.strikethrough;
	result.blink = this.blink;
	result.selected = this.selected;
	
	return result;
};

/**
 * Compares this attribute to another. This method will return true if the two
 * objects are actually the same object or if all properties on the two objects
 * match.
 * 
 * @alias Attribute.prototype.equals
 * @param {Attribute} attr
 * @return {Boolean}
 */
Attribute.prototype.equals = function(attr)
{
	var result = false;
	
	if (attr instanceof Attribute)
	{
		result =
			this === attr ||
			(
				this.foreground    == attr.foreground    &&
				this.background    == attr.background    &&
				this.bold          == attr.bold          &&
				this.italic        == attr.italic        &&
				this.underline     == attr.underline     &&
				this.inverse       == attr.inverse       &&
				this.strikethrough == attr.strikethrough &&
				this.blink         == attr.blink         &&
				this.selected      == attr.selected
			);
	}
	
	return result;
};

/**
 * This method generates a new span element with its class attribute set
 * according to the values of this attribute. Note that the element is unclosed
 * to allow multiple characters in the terminal to use the same style, as
 * needed. In order to close the element, getEndingHTML will need to be called.
 * 
 * @alias Attribute.prototype.getStartingHTML
 * @return {String}
 */
Attribute.prototype.getStartingHTML = function()
{
	var buffer = [];
	
	// NOTE: The background and foreground properties are possible injection
	// sites since we use their values directly. XTermHandler only sets these as
	// numbers, but being overly paranoid, we make sure these are numbers and if
	// not, we use default values instead
	var background = (isNumber(this.background)) ? this.background : Attribute.DEFAULT_BACKGROUND;
	var foreground = (isNumber(this.foreground)) ? this.foreground : Attribute.DEFAULT_FOREGROUND;
	
	if (this.inverse)
	{
		buffer.push("f" + background);
		buffer.push("b" + ((this.selected) ? "s" : foreground));
	}
	else
	{
		buffer.push("f" + foreground);
		buffer.push("b" + ((this.selected) ? "s" : background));
	}
	if (this.bold)
	{
		buffer.push("b");
	}
	if (this.italic)
	{
		buffer.push("i");
	}
	
	// NOTE: can't have underline, blink and strikethrough, so first one wins
	if (this.underline)
	{
		buffer.push("u");
	}
	else if (this.strikethrough)
	{
		buffer.push("lt");
	}
	else if (this.blink)
	{
		buffer.push("bl");
	}
	
	return "<span class=\"" + buffer.join(" ") + "\">";
};

/**
 * This method returns the markup needed to turn of the style being applied by
 * this attribute
 * 
 * @alias Attribute.prototype.getEndingHTML
 * @return {String}
 */
Attribute.prototype.getEndingHTML = function()
{
	return "</span>";
};

/**
 * This method resets all properties to their default values
 * 
 * @alias Attribute.prototype.reset
 */
Attribute.prototype.reset = function()
{
	this.resetBackground();
	this.resetForeground();
	this.bold = false;
	this.italic = false;
	this.underline = false;
	this.inverse = false;
	this.strikethrough = false;
	this.blink = false;
	this.selected = false;
};

/**
 * This method resets the background color to its default value
 * 
 * @alias Attribute.prototype.resetBackground
 */
Attribute.prototype.resetBackground = function()
{
	this.background = Attribute.DEFAULT_BACKGROUND;
};

/**
 * This method resets the foreground color to its default value
 * 
 * @alias Attribute.prototype.resetForeground
 */
Attribute.prototype.resetForeground = function()
{
	this.foreground = Attribute.DEFAULT_FOREGROUND;
};

/**
 * @classDescription {Range} A range represents a continuous run of characters.
 * These are used by Term to specify and report selections within the terminal's
 * display
 */

/**
 * Create a new instance of a Range. Note that the inputs will be normalized so
 * that the starting offset is less then or equal to the ending offset.
 *
 * @constructor
 * @alias Range
 * @param {Number} startingOffset
 * @param {Number} endingOffset
 */
function Range(startingOffset, endingOffset)
{
	if (isNumber(startingOffset) === false)
	{
		startingOffset = 0;
	}	
	if (isNumber(endingOffset) === false)
	{
		endingOffset = 0;
	}
	this.startingOffset = Math.min(startingOffset, endingOffset);
	this.endingOffset = Math.max(startingOffset, endingOffset);
}

/**
 * Clamp this range so that it fits within the specified range
 *
 * @alias {Range.prototype.clamp}
 * @param {Range} that
 * @return {Range}
 */
Range.prototype.clamp = function(that)
{
	var result;
	
	if (this.isOverlapping(that))
	{
		result = new Range(
			Math.max(this.startingOffset, that.startingOffset),
			Math.min(this.endingOffset, that.endingOffset)
		);
	}	
	else
	{
		result = new Range(0, 0);
	}	
	
	return result;
};

/**
 * Determine if the specified offset is contained by this range
 *
 * @alias {Range.prototype.contains}
 * @param {Number} offset
 * @return {Boolean}
 */
Range.prototype.contains = function(offset)
{
	return this.startingOffset <= offset && offset < this.endingOffset;
};

/**
 * Determine if this range is empty
 *
 * @alias {Range.prototype.isEmpty}
 * @return {Boolean}
 */
Range.prototype.isEmpty = function()
{
	return this.startingOffset === this.endingOffset;
};

/**
 * Determine if this range and the specified range are touching or overlapping
 *
 * @alias {Range.prototype.isOverlapping}
 * @param {Range} that
 * @return {Boolean}
 */
Range.prototype.isOverlapping = function(that)
{
	var start1 = this.startingOffset;
	var start2 = that.startingOffset;
	var end1 = this.endingOffset - 1;
	var end2 = that.endingOffset - 1;
	
	return (
			start2 <= start1 && start1 <= end2
		||	start2 <= end1   && end1   <= end2
		||	start1 <= start2 && start2 <= end1
		||	start1 <= end2   && end2   <= end1
	);
};

/**
 * Combines this range with another range so the resulting range covers the
 * entire range of both. Note that it is assumed that the two ranges are
 * overlapping or contiguous. If they are not, then the gap between the two will
 * be included in the resulting range. If this behavior is not desired, then
 * test with isOverlapping before calling this method.
 *
 * @param {Range} that
 * @return {Range}
 */
Range.prototype.merge = function(that)
{
	return new Range(
		Math.min(this.startingOffset, that.startingOffset),
		Math.max(this.endingOffset, that.endingOffset)
	);
};

/**
 * Create a new range that has been shifted by the specified delta
 *
 * @alias {Range.prototype.move}
 * @param {Number} delta
 * @return {Range}
 */
Range.prototype.move = function(delta)
{
	return new Range(
		this.startingOffset + delta,
		this.endingOffset + delta
	)
};

/**
 * @classDescription {Line} A Line represents a single line in a terminal. Basic
 * manipulation of the line's content can be performed (clearning, inserting,
 * deleting). Lines are used to generate the HTML used to display the content of
 * a terminal
 */

Line.DEFAULT_WIDTH = 80;
Line.MIN_WIDTH = 20;
Line.MAX_WIDTH = 512;

/**
 * Create a new instance of a Line.
 * 
 * @constructor
 * @alias Line
 * @param {Number} [width]
 * 		The number of characters (or columns) in this line. If the value is not
 * 		specified, a default value of 80 will be used. Note that the value is
 * 		clamped to the closed interval [20,512],
 * @param {FontInfo} terminal
 */
function Line(width, fontInfo)
{
	if (isNumber(width))
	{
		width = clamp(width, Line.MIN_WIDTH, Line.MAX_WIDTH);
	}
	else
	{
		width = Line.DEFAULT_WIDTH;
	}
	
	this._fontInfo = fontInfo; // TODO: type check
	this._chars = new Array(width);
	this._attributes = new Array(width);
	this.clear();
	
	// cache the last HTML we generated, when possible
	this._lastInfo = null;
	this._lastCursorOffset = null;
}

/**
 * Replace all characters in this line with spaces or with the specified
 * character
 * 
 * @alias Line.prototype.clear
 * @param {String} [ch]
 * 		If this optional parameter is specified, the first character of the
 * 		string value will be used as the replacement character when filling this
 * 		line.
 */
Line.prototype.clear = function(ch)
{
	ch = (isCharacter(ch)) ? ch.charAt(0) : ' ';
	
	for (var i = 0; i < this._chars.length; i++)
	{
		this._chars[i] = ch;
		this._attributes[i] = new Attribute();
	}	
	
	this.clearCache();
};

/**
 * clearCache
 *
 * @private
 */
Line.prototype.clearCache = function()
{
	// clear any cached HTML we may have already generated
	this._lastInfo = null;
	this._lastCursorOffset = null;
};

/**
 * Clear all characters starting from the beginning of the line up to and
 * including the specified offset. If the offset is outside the region of this
 * line, no action is performed
 * 
 * @alias Line.prototype.clearLeft
 * @param {Number} offset
 */
Line.prototype.clearLeft = function(offset)
{
	if (isNumber(offset) && 0 <= offset && offset < this._chars.length) 
	{
		for (var i = 0; i <= offset; i++) 
		{
			this._chars[i] = ' ';
			this._attributes[i] = new Attribute();
		}		
		
		this.clearCache();
	}
};

/**
 * Clear all characters starting from the specified offset to the end of the
 * line. If the offset is outside the region of this line, no action is
 * performed
 * 
 * @alias Line.prototype.clearRight
 * @param {Number} offset
 */
Line.prototype.clearRight = function(offset)
{
	if (isNumber(offset) && 0 <= offset && offset < this._chars.length) 
	{
		for (var i = offset; i < this._chars.length; i++) 
		{
			this._chars[i] = ' ';
			this._attributes[i] = new Attribute();
		}		
		
		this.clearCache();
	}
};

/**
 * Clear all selection attributes in this line
 */
Line.prototype.clearSelection = function()
{
	var attrs = this._attributes;
	var length = attrs.length;
	var hadSelection = false;
	
	for (var i = 0; i < length; i++)
	{
		var attr = attrs[i];
		
		if (attr.selected)
		{
			hadSelection = true;
		}		
		
		attr.selected = false;
	}	
	
	if (hadSelection)
	{
		this.clearCache();
	}
};

/**
 * Remove one or more characters from the specified offset. The equivalent
 * number of spaces will be added to the end of the line. Each new character's
 * attribute will be set to the default style.
 * 
 * @alias Line.prototype.deleteCharacter
 * @param {Number} offset
 * 		The offset within this line where characters should be deleted
 * @param {Number} [count]
 * 		An optional number of characters to delete. When this parameter is not
 * 		specified, a default value of 1 will be used
 */
Line.prototype.deleteCharacter = function(offset, count)
{
	if (isNumber(offset))
	{
		var length = this._chars.length;
		
		count = (isNumber(count)) ? count : 1;
		
		if (count > 0 && 0 <= offset && offset < length)
		{
			// clamp count so we don't go past the end of the line
			if (offset + count > length)
			{
				count = length - offset;
			}
			
			// delete character and attribute
			this._chars.splice(offset, count);
			this._attributes.splice(offset, count);
			
			// add new character and attribute
			for (var i = 0; i < count; i++)
			{
				this._chars.push(' ');
				this._attributes.push(new Attribute());
			}			
			
			this.clearCache();
		}
	}
};

/**
 * Deselect the characters in the specified range
 
 * @param {Range} range
 */
Line.prototype.deselect = function(range)
{
	var targetRange = new Range(0, this._chars.length);
	var range = range.clamp(targetRange);
	var attrs = this._attributes;
	var endingOffset = range.endingOffset;
	var deselected = false;
	
	for (var i = range.startingOffset; i < endingOffset; i++)
	{
		var attribute = attrs[i];
		
		if (attribute.selected)
		{
			// make a copy in case this attribute is used in multiple locations
			attribute.copy();
			
			// turn off the selection flag
			attribute.selected = false;
			
			// store the new attribute
			attrs[i] = attribute;
			
			// indicate that the selection has changed
			deselected = true;
		}
	}	
	
	// clear cache if selection has changed
	if (deselected)
	{
		this.clearCache();
	}
};

/**
 * Convert this line to HTML including any style changes that are needed both to
 * represent changes in attributes and to represent the current cursor position
 * 
 * @alias Line.prototype.getHTMLInfo
 * @param {Attribute} currentAttribute
 * 		The current attribute that is in effect at the beginning of this line
 * @param {Number} [cursorOffset]
 * 		When this optional parameter is specified, it indicates the offset
 * 		within this line where the cursor should appear.
 * @return {Object}
 * 		"html" - Contains the HTML markup for this line
 * 		"attribute" - The attribute that is in effect at the end of this line
 */
Line.prototype.getHTMLInfo = function(currentAttribute, cursorOffset)
{
	// TEMP: disable caching until we resolve the scrolling issue
	this.clearCache();
	
	if (this._lastInfo === null || this._lastCursorOffset !== cursorOffset)
	{
		var buffer = [];
		
		for (var i = 0; i < this._chars.length; i++)
		{
			var ch = this._chars[i];
			var attr = this._attributes[i];
			
			if (attr && attr.equals(currentAttribute) == false)
			{
				if (currentAttribute !== null)
				{
					buffer.push(currentAttribute.getEndingHTML());
				}
	
				buffer.push(attr.getStartingHTML());
							
				currentAttribute = attr;
			}
			
			if (i === cursorOffset)
			{
				buffer.push("<span class=\"cursor\">");
			}
			
			switch (ch)
			{
				case '&':
					buffer.push("&amp;");
					break
					
				case '<':
					buffer.push("&lt;");
					break;
					
				case '>':
					buffer.push("&gt;");
					break;
					
				case ' ':
					buffer.push("&nbsp;");
					break;
					
				default:
					buffer.push(ch);
					break;
			}
			
			if (i === cursorOffset)
			{
				buffer.push("</span>");
			}
			
		}
		
		this._lastInfo = {
			html: buffer.join(""),
			attribute: currentAttribute
		};		
		this._lastCursorOffset = cursorOffset;
	}	
	
	return this._lastInfo;
}

/**
 * Find the offset of the last non-white character in this line
 *
 * @private
 * @return {Number}
 */
Line.prototype.getLastNonWhiteOffset = function()
{
	var offset = 0;
	var length = this._chars.length;
	
	for (var i = length - 1; i >= 0; i--)
	{
		if (this._chars[i].match(/\S/))
		{
			offset = i + 1;
			break;
		}
	}	
	
	return offset;
};

/**
 * getLineHeight
 *
 * @return {Number}
 */
Line.prototype.getLineHeight = function()
{
	// NOTE: This is probably overly paranoid that some characters may be taller
	// than others. Really we should only need to return the line height being
	// used in CSS or the height of a single character.
	var chars = this._chars;
	var attrs = this._attributes;
	var length = chars.length;
	var result = 0;
	
	for (var i = 0; i < length; i++)
	{
		var ch = chars[i];
		var attr = attrs[i];
		var style = (attr.bold) ? "bold" : "normal";
		var charHeight = this._fontInfo.getCharacterHeight(ch, style);
		
		result = Math.max(result, charHeight);
	}
	
	return result;
};

/**
 * getOffsetFromPosition
 *
 * @param {Number} x
 * @return {Number}
 */
Line.prototype.getOffsetFromPosition = function(x)
{
	var startingOffset = 0;
	var result;
	
	if (this._fontInfo.isMonospaced())
	{
		var width = this._fontInfo.getCharacterWidth('M');
		
		result = Math.floor(x / width);
	}	
	else
	{
		var chars = this._chars;
		var attrs = this._attributes;
		var length = chars.length;
		
		for (var i = 0; i < length; i++)
		{
			var ch = chars[i];
			var attr = attrs[i];
			var style = (attr.bold) ? "bold" : "normal";
			var endingOffset = startingOffset + this._fontInfo.getCharacterWidth(ch, style);
			
			if (startingOffset <= x && x < endingOffset)
			{
				result = i;
				break;
			}		
			else
			{
				startingOffset = endingOffset;
			}
		}
	}
	
	return result;
};

/**
 * getSelectedText
 *
 * @alias {Line.prototype.getSelectedText}
 * @return {String}
 */
Line.prototype.getSelectedText = function()
{
	var chars = this._chars;
	var attrs = this._attributes;
	var length = Math.min(this.getLastNonWhiteOffset(), attrs.length);
	var result = null;
	
	for (var i = 0; i < length; i++)
	{
		// NOTE: not handling possible discontiguous selections.
		if (attrs[i].selected)
		{
			if (result === null)
			{
				result = [];
			}			
			
			result.push(chars[i]);
		}
	}
	
	return (result !== null) ? result.join("") : null;
};

/**
 * Return the number of columns in this line
 * 
 * @alias Line.prototype.getWidth
 * @return {Number}
 */
Line.prototype.getWidth = function()
{
	return this._chars.length;
};

/**
 * Insert a character one or more times into this line at the specified offset.
 * All characters shifted off of the end of the line are lost. All new
 * characters will use default styling.
 * 
 * @alias Line.prototype.insertCharacter
 * @param {String} ch
 * @param {Number} offset
 * @param {Number} [count]
 */
Line.prototype.insertCharacter = function(ch, offset, count)
{
	if (isCharacter(ch) && isNumber(offset))
	{
		var length = this._chars.length;
		
		count = (isNumber(count)) ? count : 1;
		
		if (count > 0 && 0 <= offset && offset < length)
		{
			ch = ch.charAt(0);
			
			// clamp count so we don't go past the end of the line
			if (offset + count > length)
			{
				count = length - offset;
			}
			
			// remove trailing characters and attributes
			this._chars.splice(length - count, count);
			this._attributes.splice(length - count, count);
			
			// add new characters and attributes
			var chars = new Array(count);
			var attrs = new Array(count);
			
			for (var i = 0; i < count; i++)
			{
				this._chars.splice(offset + i, 0, ch);
				this._attributes.splice(offset + i, 0, new Attribute());
			}			
			
			this.clearCache();
		}
	}
};

/**
 * Overwrite the character in this line at the specified offset with the newly
 * specificed character and attribute
 * 
 * @alias Line.prototype.putCharacter
 * @param {String} ch
 * 		The character to put into this line
 * @param {Attribute} attr
 * 		The attribute for the new character
 * @param {Number} offset
 * 		The offset where to place the new character and attribute
 */
Line.prototype.putCharacter = function(ch, attr, offset)
{
	if (isCharacter(ch) && isDefined(attr) && attr.constructor == Attribute && isNumber(offset))
	{
		if (0 <= offset && offset < this._chars.length) 
		{
			this._chars[offset] = ch.charAt(0);
			this._attributes[offset] = attr;
			
			this.clearCache();
		}
	}
};

/**
 * Resize this line to the newly specified value. If the new value is below
 * Line.MIN_WIDTH, above Line.MAX_WIDTH, or is the same as the current width,
 * then no action is performed
 * 
 * @alias Line.prototype.resize
 * @param {Number} width
 * 		The new line width
 */
Line.prototype.resize = function(width)
{
	if (isNumber(width))
	{
		var length = this._chars.length;
		
		if (Line.MIN_WIDTH <= width && width <= Line.MAX_WIDTH && length != width) 
		{
			this._chars.length = width;
			
			if (width > length) 
			{
				for (var i = length; i < width; i++) 
				{
					this._chars[i] = ' ';
					this._attributes[i] = new Attribute();
				}				
			}
			
			this.clearCache();
		}
	}
};

/**
 * Set the selected characters on this line
 *
 * @alias {Line.prototype.select}
 * @param {Range} range
 * @param {Boolean} selectionContinues
 * @return {Boolean}
 *		returns true if a selection was made on this line
 */
Line.prototype.select = function(range, selectionContinues)
{
	var endingOffset = (selectionContinues) ? this._chars.length : this.getLastNonWhiteOffset();
	var targetRange = new Range(0, endingOffset);
	var range = range.clamp(targetRange);
	var attrs = this._attributes;
	var endingOffset = range.endingOffset;
	var selected = false;
	
	for (var i = range.startingOffset; i < endingOffset; i++)
	{
		var attribute = attrs[i];
		
		if (attribute.selected === false)
		{
			// make a copy in case this attribute is used in multiple locations
			attribute = attribute.copy();
			
			// turn on the selection flag
			attribute.selected = true;
			
			// store the new attribute
			attrs[i] = attribute;
			
			// indicate that the selection has changed
			selected = true;
		}
	}
	
	// clear cache if selection has changed
	if (selected)
	{
		this.clearCache();
	}
	
	return selected;
};

/**
 * Return this content of this line as a string
 * 
 * @alias Line.prototype.toString
 */
Line.prototype.toString = function()
{
	return this._chars.join("");
};

/**
 * @classDescription {KeyHandler} KeyHandler is responsible for monitoring key
 * events for a terminal. Key presses are collected and sent in batches with
 * each interation with the server.
 */

// Character sequences for specific keys
KeyHandler.BACKSPACE = "\x7F";
KeyHandler.DELETE = "\x1B[3~";
KeyHandler.ESCAPE = "\x1B";
KeyHandler.F1 = "\x1B[[A";
KeyHandler.F2 = "\x1B[[B";
KeyHandler.F3 = "\x1B[[C";
KeyHandler.F4 = "\x1B[[D";
KeyHandler.F5 = "\x1B[[E";
KeyHandler.F6 = "\x1B[17~";
KeyHandler.F7 = "\x1B[18~";
KeyHandler.F8 = "\x1B[19~";
KeyHandler.F9 = "\x1B[20~";
KeyHandler.F10 = "\x1B[21~";
KeyHandler.F11 = "\x1B[23~";
KeyHandler.F12 = "\x1B[24~";
KeyHandler.F13 = "\x1B[25~";
KeyHandler.F14 = "\x1B[26~";
KeyHandler.F15 = "\x1B[28~";
KeyHandler.F16 = "\x1B[29~";
KeyHandler.F17 = "\x1B[31~";
KeyHandler.F18 = "\x1B[32~";
KeyHandler.F19 = "\x1B[33~";
KeyHandler.F20 = "\x1B[34~";
KeyHandler.INSERT = "\x1B[2~";
KeyHandler.TAB = "\t";

// cursor movement sequences
KeyHandler.APP_UP = "\x1BOA";
KeyHandler.APP_DOWN = "\x1BOB";
KeyHandler.APP_RIGHT = "\x1BOC";
KeyHandler.APP_LEFT = "\x1BOD";
KeyHandler.APP_HOME = "\x1BOH";
KeyHandler.APP_END = "\x1BOF";

KeyHandler.UP = "\x1B[A";
KeyHandler.DOWN = "\x1B[B";
KeyHandler.RIGHT = "\x1B[C";
KeyHandler.LEFT = "\x1B[D";
KeyHandler.HOME = "\x1B[H";	// "\x1B[1~";
KeyHandler.END = "\x1B[F"; 	// "\x1B[4~";

KeyHandler.PAGE_UP = "\x1B[5~";
KeyHandler.PAGE_DOWN = "\x1B[6~";

// playback states
KeyHandler.KEY_DOWN = "keydown";
KeyHandler.KEY_PRESS = "keypress";
KeyHandler.RECORDING = "recording";
KeyHandler.PLAYING = "playing";
KeyHandler.STOPPED = "stopped";

/**
 * Create a new instance of KeyHandler. The constructor will register event
 * handlers for the keypress and keydown events. Those events will fire methods
 * on the KeyHandler instance. So, theoretically, we should be able to have
 * multiple KeyHandlers active within the same document.
 * 
 * @constructor
 * @alias KeyHandler
 */
function KeyHandler()
{
    var self = this;
    
	// a queue of all keys that have been collected so far
    this._queue = [];
	
	// a flag indicating which sequences to use for arrow keys
	this._applicationKeys = false;
	
	// initialize playback state
	this._playbackState = KeyHandler.STOPPED;
	this.clearEvents();
	this._playbackID = null;
    
	// setup the keypress and keydown events
    document.onkeypress = function(e)
    {
        return self.processKeyPress(e);
    };
    document.onkeydown = function(e)
    {
        return self.processKeyDown(e);
    };
}

/**
 * This method is used during the key event recording process to make
 * a record of the specified event. The event type is used to identify
 * the event type during playback and also controls which properties
 * on the specified event will be recorded.
 *
 * @private
 * @alias KeyHandler.prototype.addEvent
 * @param {String} type
 * @param {KeyEvent} e
 */
KeyHandler.prototype.addEvent = function(type, e)
{
	// don't record empty events
	if (e)
	{
		var event = {};
		
		switch (type)
		{
			case KeyHandler.KEY_DOWN:
				event.keyCode = e.keyCode;
				event.ctrlKey = e.ctrlKey;
				event.altKey = e.altKey;
				event.shiftKey = e.shiftKey;
				break;
				
			case KeyHandler.KEY_PRESS:
				event.keyCode = e.keyCode;
				event.which = e.which;
				event.ctrlKey = e.ctrlKey;
				event.altKey = e.altKey;
				event.metaKey = e.metaKey;
				break;
				
			default:
				// don't recognize this event type, so return
				return;
		}
		
		this._events.keys.push({
			time: new Date().getTime(),
			type: type,
			event: event
		});
	}
};

/**
 * Add the specified characters to the key handler queue
 * 
 * @alias KeyHandler.prototype.addKeys
 * @param {String} keys
 */
KeyHandler.prototype.addKeys = function(keys)
{
    this._queue.push(keys);
	
	if (isDefined(this.callback))
	{
		this.callback(true);
	}
};

/**
 * Clear any events we may have recorded previously
 *
 */
KeyHandler.prototype.clearEvents = function()
{
	// clear out our last recording session
	this._events = {
		user_agent: {
			browser: BrowserDetect.browser,
			version: BrowserDetect.version,
			os: BrowserDetect.OS
		},
		keys: []
	};
};

/**
 * Remove all key sequences from the queue as a single string value. The queue
 * is reset by this method
 * 
 * @alias KeyHandler.prototype.dequeueAll
 * @return {String}
 */
KeyHandler.prototype.dequeueAll = function()
{
    var result = this._queue.join("");
    
    this._queue.length = 0;
    
    return result;
};

/**
 * Get the current application keys setting
 *
 * @return {Boolean}
 */
KeyHandler.prototype.getApplicationKeys = function()
{
	return this._applicationKeys;
};

/**
 * Test if there are any keys in the key handler queue
 * 
 * @alias KeyHandler.prototype.hasContent
 * @return {Boolean}
 */
KeyHandler.prototype.hasContent = function()
{
    return this._queue.length > 0;
};

/**
 * Play back a sequence of key events

 * @alias KeyHandler.prototype.play
 * @param {Array} events
 */
KeyHandler.prototype.play = function(events)
{
	if (this._playbackState != KeyHandler.PLAYING)
	{
		this._playbackState = KeyHandler.PLAYING
		
		events = events || this._events.keys;
		
		var self = this;
		var i = 0;
		var processEvents = function()
		{
			var info = events[i++];
			
			switch(info.type)
			{
				case KeyHandler.KEY_DOWN:
					self.processKeyDown(info.event);
					break;
					
				case KeyHandler.KEY_PRESS:
					self.processKeyPress(info.event);
					break;
					
				default:
					break;
			}
			
			if (self._playbackState == KeyHandler.PLAYING && i < events.length)
			{
				var delay = clamp(events[i].time - info.time, 0, 1000);
				
				this._playbackID = window.setTimeout(processEvents, delay);
			}
		}
		
		// start processing
		processEvents();
	}
};

/**
 * Process a key down event
 * 
 * @alias KeyHandler.prototype.processKeyDown
 * @param {KeyEvent} e
 */
KeyHandler.prototype.processKeyDown = function(e)
{
	if (!e) 
	{
		e = window.event;
	}
	
	// NOTE: I was going to get fancy and do a kind of AOP approach to
	// support recording without altering this method, but this should
	// have so little overhead compared to the rest of the key handling,
	// I decided to include this inline
	if (this._playbackState == KeyHandler.RECORDING)
	{
		this.addEvent(KeyHandler.KEY_DOWN, e);
	}
	
	var keyCode = e.keyCode;
	var keys = null;
	var useAppKeys = this._applicationKeys;
	
	// Handle special keys.  We do this here because IE doesn't send
	// keypress events for these (or at least some versions of IE don't for
	// at least many of them).  This is unfortunate as it means that the
	// cursor keys don't auto-repeat, even in browsers where that would be
	// possible.  That could be improved.

	if (BrowserDetect.browser == "Firefox" && (e.keyCode == 8 || (37 <= e.keyCode && e.keyCode <= 40)))
	{
		// this will be handled in processKeyPress; otherwise, we'll send to of these events when the
		// user first presses on of these keys.
	}
	else
	{
		switch (keyCode)
		{
			case 8:		keys = KeyHandler.BACKSPACE; break;
			case 9:		keys = KeyHandler.TAB; break;
			case 27:	keys = KeyHandler.ESCAPE; break;
			case 33:	keys = KeyHandler.PAGE_UP; break;
			case 34:	keys = KeyHandler.PAGE_DOWN; break;
			case 35:	keys = (useAppKeys) ? KeyHandler.APP_END : KeyHandler.END; break;
			case 36:	keys = (useAppKeys) ? KeyHandler.APP_HOME : KeyHandler.HOME; break;
			case 37:	keys = (useAppKeys) ? KeyHandler.APP_LEFT : KeyHandler.LEFT; break;
			case 38:	keys = (useAppKeys) ? KeyHandler.APP_UP : KeyHandler.UP; break;
			case 39:	keys = (useAppKeys) ? KeyHandler.APP_RIGHT : KeyHandler.RIGHT; break;
			case 40:	keys = (useAppKeys) ? KeyHandler.APP_DOWN : KeyHandler.DOWN; break;
			case 45:	keys = KeyHandler.INSERT; break;
			case 46:	keys = KeyHandler.DELETE; break;
			case 112:	keys = e.shiftKey ? KeyHandler.F13 : KeyHandler.F1; break;
			case 113:	keys = e.shiftKey ? KeyHandler.F14 : KeyHandler.F2; break;
			case 114:	keys = e.shiftKey ? KeyHandler.F15 : KeyHandler.F3; break;
			case 115:	keys = e.shiftKey ? KeyHandler.F16 : KeyHandler.F4; break;
			case 116:	keys = e.shiftKey ? KeyHandler.F17 : KeyHandler.F5; break;
			case 117:	keys = e.shiftKey ? KeyHandler.F18 : KeyHandler.F6; break;
			case 118:	keys = e.shiftKey ? KeyHandler.F19 : KeyHandler.F7; break;
			case 119:	keys = e.shiftKey ? KeyHandler.F20 : KeyHandler.F8; break;
			case 120:	keys = KeyHandler.F9; break;
			case 121:	keys = KeyHandler.F10; break;
			case 122:	keys = KeyHandler.F11; break;
			case 123:	keys = KeyHandler.F12; break;
	
			default:
				// For most keys we'll stop now and let the subsequent keypress event
				// process the key.  This has the advantage that auto-repeat will work.
				// But we'll carry on here for control keys.
				// Note that when altgr is pressed, the event reports ctrl and alt being
				// pressed because it doesn't have a separate field for altgr.  We'll
				// handle altgr in the keypress handler.
				if (!e.ctrlKey					// ctrl not pressed
					|| (e.ctrlKey && e.altKey)	// altgr pressed
					|| (e.keyCode == 17))		// I think that if you press shift-control,
				{                  				// you'll get an event with !ctrlKey && keyCode==17.
					// do nothing
				}
				else
				{
					// OK, so now we're handling a ctrl key combination.
					
					// There are some assumptions below about whether these symbols are shifted
					// or not; does this work with different keyboards?
					if (e.shiftKey)
					{
						switch (keyCode)
						{
							case 50:  keys = String.fromCharCode(0);  break;	// Ctrl-@
							case 54:  keys = String.fromCharCode(30); break;	// Ctrl-^, doesn't work
							case 94:  keys = String.fromCharCode(30); break;	// Ctrl-^, doesn't work
							case 109: keys = String.fromCharCode(31); break;	// Ctrl-_
							default:
								break;
						}
					}
					else
					{
						switch (keyCode)
						{
							case 32:  keys = String.fromCharCode(0);  break;	// Ctrl-space sends 0, like ctrl-@.
							case 190: keys = String.fromCharCode(30); break;	// Since ctrl-^ doesn't work, map ctrl-. to its code.
							case 219: keys = String.fromCharCode(27); break;	// Ctrl-[
							case 220: keys = String.fromCharCode(28); break;	// Ctrl-\
							case 221: keys = String.fromCharCode(29); break;	// Ctrl-]
							default:
								if (65 <= keyCode && keyCode <= 90)
								{
									keys = String.fromCharCode(keyCode - 64); // Ctrl-A..Z
								}
								break;
						}
					}
				}
				break;
	  	}
  	}
	
	if (keys !== null)
	{
		this.addKeys(keys);
		
		return this.stopEvent(e);
	}
	else
	{
		// exit with true so keypress handler will be invoked
		return this.suppressEvent(e);
	}
};

/**
 * Process a keypress event - adapted from anyterm.js#keypress
 * 
 * @alias KeyHandler.prototype.processKeyPress
 * @param {KeyEvent} e
 */
KeyHandler.prototype.processKeyPress = function(e)
{
    if (!e) 
	{
		e = window.event;
	}
    
    if (!e || e.metaKey) 
    {
        // let the browser get meta-key combinations
        return true;
    }

	if (this._playbackState == KeyHandler.RECORDING)
	{
		this.addEvent(KeyHandler.KEY_PRESS, e);
	}
    
    if (BrowserDetect.browser == "Firefox" && (e.keyCode == 8 || (37 <= e.keyCode && e.keyCode <= 40)))
	{
		var useAppKeys = this._applicationKeys;
		var keys = null;
		
		switch (e.keyCode)
		{
			case 8:		keys = KeyHandler.BACKSPACE; break;
			case 37:	keys = (useAppKeys) ? KeyHandler.APP_LEFT : KeyHandler.LEFT; break;
			case 38:	keys = (useAppKeys) ? KeyHandler.APP_UP : KeyHandler.UP; break;
			case 39:	keys = (useAppKeys) ? KeyHandler.APP_RIGHT : KeyHandler.RIGHT; break;
			case 40:	keys = (useAppKeys) ? KeyHandler.APP_DOWN : KeyHandler.DOWN; break;
		}
		
		if (keys !== null)
		{
			this.addKeys(keys);
		}
	}
    // Only handle "safe" characters here.  Anything unusual is ignored; it would
    // have been handled earlier by the keydown function
    else if ((e.ctrlKey && !e.altKey)	// Ctrl is pressed (but not altgr, which is reported
 									// as ctrl+alt in at least some browsers).
		|| (e.which == 0)			// there's no key in the event; maybe a shift key?
 									// (Mozilla sends which==0 && keyCode==0 when you press
									// the 'windows logo' key.)
		|| (e.keyCode == 8)			// backspace
 		|| (e.keyCode == 16)) 
	{								// shift; Opera sends this.
		// do nothing
	}
	else 
	{
		var code;
		
		if (e.keyCode) 
		{
			code = e.keyCode;
		}
		if (e.which) 
		{
			code = e.which;
		}
	
		// When a key is pressed with ALT, we send ESC followed by the key's normal
		// code.  But we don't want to do this when ALT-GR is pressed.
		if (e.altKey && !e.ctrlKey) 
		{
			this.addKeys(KeyHandler.ESCAPE);
		}
		
		this.addKeys(String.fromCharCode(code));
	}
	
	return this.stopEvent(e);
};

/**
 * Start recording keydown and keypress events
 *
 * @alias KeyHandler.prototype.record
 */
KeyHandler.prototype.record = function()
{
	if (this._playbackState != KeyHandler.RECORDING)
	{
		// stop any playback that is currently running
		if (this._playbackState == KeyHandler.PLAYING)
		{
			this.stop();
		}
		
		// clear out our last recording
		this.clearEvents();
		
		// start recording
		this._playbackState = KeyHandler.RECORDING;
	}
};

/**
 * Set the application keys flag. If the value is not a boolean value, then no
 * action will occur.
 * 
 * @alias KeyHandler.prototype.setApplicationKeys
 * @param {Boolean} value
 */
KeyHandler.prototype.setApplicationKeys = function(value)
{
	if (isBoolean(value))
	{
		this._applicationKeys = value;
	}
};

/**
 * Stop playback or any recording that is currently running
 *
 * @alias KeyHandler.prototype.stop
 */
KeyHandler.prototype.stop = function()
{
	if (this._playbackState != KeyHandler.STOPPED)
	{
		this._playbackState = KeyHandler.STOPPED;
		
		if (this._playbackID !== null)
		{
			window.clearTimeout(this._playbackID);
			this._playbackID = null;
		}
	}
	
	return this._events;
};

/**
 * A convenience function used to prevent a key event from continuing to
 * propagate or from being processed by the browser
 * 
 * @alias KeyHandler.prototype.stopEvent
 * @param {KeyEvent} e
 */
KeyHandler.prototype.stopEvent = function(e)
{
	if (e) 
	{
		e.cancelBubble = true;
		if (e.stopPropagtion) e.stopPropagation();
		if (e.preventDefault) e.preventDefault();
		try
		{
			e.keyCode = 0;
		}
		catch (e)
		{
		}
	}
	
	return false;
};

/**
 * A convenience function used to prevent a key event from continuing to
 * propagate
 * 
 * @alias KeyHandler.prototype.supressEvent
 * @param {KeyEvent} e
 */
KeyHandler.prototype.suppressEvent = function(e)
{
	if (e) 
	{
		e.cancelBubble = true;
		if (e.stopPropagtion) e.stopPropagation();
	}
	
	return true;
};

/**
 * !!!!!WARNING!!!!!
 *
 * Do not edit this file. It was computer generated by TermGen
 *
 * !!!!!WARNING!!!!!
 */

var XTermTables = {
	format: "rle",
	version: 1,
	actions: [
        [ "<error>" ],
        [ "ANSI", 2, 0 ],
        [ "ANSI_SYS" ],
        [ "APC" ],
        [ "APP_CTRL", 2, 2 ],
        [ "BEL" ],
        [ "BS" ],
        [ "CBT", 2, 1 ],
        [ "CHA", 2, 1 ],
        [ "CHT", 2, 1 ],
        [ "CNL", 2, 1 ],
        [ "CPL", 2, 1 ],
        [ "CR" ],
        [ "CSI" ],
        [ "CUB", 2, 1 ],
        [ "CUD", 2, 1 ],
        [ "CUF", 2, 1 ],
        [ "CUP", 2, 1 ],
        [ "CURSOR_LOWER_LEFT" ],
        [ "CUU", 2, 1 ],
        [ "DA1", 2, 1 ],
        [ "DA2", 3, 1 ],
        [ "DCH", 2, 1 ],
        [ "DCS" ],
        [ "DECALN" ],
        [ "DECCARA", 2, 2 ],
        [ "DECCRA", 2, 2 ],
        [ "DECDHL_BH" ],
        [ "DECDHL_TH" ],
        [ "DECDWL" ],
        [ "DECEFR", 2, 2 ],
        [ "DECELR", 2, 2 ],
        [ "DECERA", 2, 2 ],
        [ "DECFRA", 2, 2 ],
        [ "DECID" ],
        [ "DECPAM" ],
        [ "DECPNM" ],
        [ "DECRARA", 2, 2 ],
        [ "DECRC" ],
        [ "DECREQTPARM_OR_DECSACE", 2, 1 ],
        [ "DECRQLP", 2, 2 ],
        [ "DECRQSS", 4, 2 ],
        [ "DECRST", 3, 1 ],
        [ "DECSC" ],
        [ "DECSCA", 2, 2 ],
        [ "DECSCL", 2, 2 ],
        [ "DECSED", 3, 1 ],
        [ "DECSEL", 3, 1 ],
        [ "DECSERA", 2, 2 ],
        [ "DECSET", 3, 1 ],
        [ "DECSLE", 2, 2 ],
        [ "DECSTBM", 2, 1 ],
        [ "DECSTR" ],
        [ "DECSWL" ],
        [ "DECUDK", 2, 2 ],
        [ "DEFAULT_CHARSET" ],
        [ "DL", 2, 1 ],
        [ "DSR", 2, 1 ],
        [ "DSR2", 3, 1 ],
        [ "ECH", 2, 1 ],
        [ "ED", 2, 1 ],
        [ "EL", 2, 1 ],
        [ "ENQ" ],
        [ "EPA" ],
        [ "FF" ],
        [ "G0_CHARSET", 2, 0 ],
        [ "G1_CHARSET", 2, 0 ],
        [ "G2_CHARSET", 2, 0 ],
        [ "G3_CHARSET", 2, 0 ],
        [ "HPA", 2, 1 ],
        [ "HTS" ],
        [ "HVP", 2, 1 ],
        [ "ICH", 2, 1 ],
        [ "IL", 2, 1 ],
        [ "IND" ],
        [ "LF" ],
        [ "LS1R" ],
        [ "LS2" ],
        [ "LS2R" ],
        [ "LS3" ],
        [ "LS3R" ],
        [ "MC", 2, 1 ],
        [ "MC2", 3, 1 ],
        [ "MEM_LOCK" ],
        [ "MEM_UNLOCK" ],
        [ "MOUSE_TRACKING", 2, 1 ],
        [ "NEL" ],
        [ "OSC" ],
        [ "PM" ],
        [ "PRI_MSG", 2, 2 ],
        [ "REP", 2, 1 ],
        [ "RESTORE_MODE", 3, 1 ],
        [ "RI" ],
        [ "RIS" ],
        [ "RM", 2, 1 ],
        [ "S7C1T" ],
        [ "S8C1T" ],
        [ "SAVE_MODE", 3, 1 ],
        [ "SD", 2, 1 ],
        [ "SET_TEXT_PARAMS", 2, 1 ],
        [ "SET_TEXT_PARAMS2", 2, 2 ],
        [ "SGR", 2, 1 ],
        [ "SI" ],
        [ "SM", 2, 1 ],
        [ "SO" ],
        [ "SOS" ],
        [ "SPA" ],
        [ "SS2" ],
        [ "SS3" ],
        [ "ST" ],
        [ "SU", 2, 1 ],
        [ "TAB" ],
        [ "TBC", 2, 1 ],
        [ "UTF8_CHARSET" ],
        [ "VPA", 2, 1 ],
        [ "VT" ]
    ],
	nodes: [
        [ [-5,256,-1,512,768,1024,1280,1536,1792,2048,2304,2560,-11,2816,-104,3072,3328,-2,3584,-4,3840,4096,4352,4608,-5,4864,5120,5376,-1,5632,5888,6144,6400,6656,6912,-96], -1 ],
        [ [-256], 62 ],
        [ [-256], 5 ],
        [ [-256], 6 ],
        [ [-256], 111 ],
        [ [-256], 75 ],
        [ [-256], 115 ],
        [ [-256], 64 ],
        [ [-256], 12 ],
        [ [-256], 104 ],
        [ [-256], 102 ],
        [ [-32,7168,-2,7424,-1,7680,-2,7936,8192,8448,8704,-11,8960,9216,-4,9472,9728,-5,9984,10240,10496,-1,10752,-4,11008,11264,11520,11776,-5,12032,12288,12544,-1,12800,13056,13312,13568,13824,14080,-3,14336,-8,14592,14848,15104,15360,-12,15616,15872,16128,-129], -1 ],
        [ [-256], 74 ],
        [ [-256], 86 ],
        [ [-256], 70 ],
        [ [-256], 92 ],
        [ [-256], 107 ],
        [ [-256], 108 ],
        [ [-256], 23 ],
        [ [-256], 106 ],
        [ [-256], 63 ],
        [ [-256], 105 ],
        [ [-256], 34 ],
        [ [-256], 13 ],
        [ [-256], 109 ],
        [ [-256], 87 ],
        [ [-256], 88 ],
        [ [-256], 3 ],
        [ [16393,-1,16385,-1,16439,16640,16896,16567], -1 ],
        [ [-51,17152,17408,17664,17920,-1,18176,-199], -1 ],
        [ [-64,18432,-6,18688,-184], -1 ],
        [ [18953,-1,18945,-1,19185], -1 ],
        [ [19209,-1,19201,-1,19441], -1 ],
        [ [19465,-1,19457,-1,19697], -1 ],
        [ [19721,-1,19713,-1,19953], -1 ],
        [ [-256], 43 ],
        [ [-256], 38 ],
        [ [-256], 35 ],
        [ [-256], 36 ],
        [ [-256], 74 ],
        [ [-256], 86 ],
        [ [-256], 18 ],
        [ [-256], 70 ],
        [ [-256], 92 ],
        [ [-256], 107 ],
        [ [-256], 108 ],
        [ [-36,19968,-11,20233,-1,20480,-196], 23 ],
        [ [-256], 106 ],
        [ [-256], 63 ],
        [ [-256], 105 ],
        [ [-256], 34 ],
        [ [-33,20736,20992,-13,21257,-1,21504,-2,21760,22016,22272,22528,22784,23040,23296,23552,23808,24064,24320,24576,24832,25088,25344,25600,-2,25856,-2,26112,26368,-3,26624,-1,26880,-5,27136,-1,27392,27648,27904,-2,28160,28416,28672,-2,28928,29184,29440,-4,29696,-1,29952,-2,30208,-135], 13 ],
        [ [-256], 109 ],
        [ [-48,30473,-1,30720,-196], 87 ],
        [ [31002,31232,31203], 88 ],
        [ [31514,31744,31715], 3 ],
        [ [-256], 93 ],
        [ [-256], 83 ],
        [ [-256], 84 ],
        [ [-256], 77 ],
        [ [-256], 79 ],
        [ [-256], 80 ],
        [ [-256], 78 ],
        [ [-256], 76 ],
        [ [-256], 1 ],
        [ [-256], 95 ],
        [ [-256], 96 ],
        [ [-256], 28 ],
        [ [-256], 27 ],
        [ [-256], 53 ],
        [ [-256], 29 ],
        [ [-256], 24 ],
        [ [-256], 55 ],
        [ [-256], 113 ],
        [ [-256], 65 ],
        [ [-256], 66 ],
        [ [-256], 67 ],
        [ [-256], 68 ],
        [ [-113,32000,-142], -1 ],
        [ [-48,20233,-1,20480,-196], -1 ],
        [ [-48,32265,-66,32512,-131], -1 ],
        [ [-112,32768,-143], -1 ],
        [ [-113,33024,-142], -1 ],
        [ [-34,20992,-13,21257,-1,21504,-4,22272,22528,22784,23040,23296,23552,23808,24064,-1,24576,24832,25088,25344,25600,-2,25856,-2,26112,26368,-3,26624,-1,26880,-5,27136,-1,27392,27648,27904,-2,28160,28416,28672,-2,28928,29184,29440,-9,30208,-135], -1 ],
        [ [-34,33280,-4,33536,-8,33801,-1,34048,-12,34304,-23,34560,-5,34816,-1,28416,28672,-2,28928,29184,-4,35072,-141], -1 ],
        [ [-48,35337,-41,35584,-156], -1 ],
        [ [-48,35849,-1,36096,-14,36352,36608,-28,36864,37120,-2,37376,-1,37632,-3,37888,38144,-140], -1 ],
        [ [-256], 72 ],
        [ [-256], 19 ],
        [ [-256], 15 ],
        [ [-256], 16 ],
        [ [-256], 14 ],
        [ [-256], 10 ],
        [ [-256], 11 ],
        [ [-256], 8 ],
        [ [-256], 17 ],
        [ [-256], 9 ],
        [ [-256], 60 ],
        [ [-256], 61 ],
        [ [-256], 73 ],
        [ [-256], 56 ],
        [ [-256], 22 ],
        [ [-256], 110 ],
        [ [-256], 98 ],
        [ [-256], 59 ],
        [ [-256], 7 ],
        [ [-123,38400,38656,-131], 69 ],
        [ [-256], 90 ],
        [ [-256], 20 ],
        [ [-256], 114 ],
        [ [-256], 112 ],
        [ [-256], 103 ],
        [ [-256], 81 ],
        [ [-256], 94 ],
        [ [-256], 101 ],
        [ [-256], 57 ],
        [ [-256], 2 ],
        [ [-256], 2 ],
        [ [-256], 39 ],
        [ [-48,30473,-1,30720,-196], -1 ],
        [ [38918,39168,38930,39424,39139], -1 ],
        [ [31002,31232,31203], -1 ],
        [ [-92,39680,-163], -1 ],
        [ [31514,31744,31715], -1 ],
        [ [-92,39936,-163], -1 ],
        [ [40218,40448,40419], -1 ],
        [ [-48,32265,-66,32512,-131], -1 ],
        [ [40730,40960,40931], -1 ],
        [ [-256], 52 ],
        [ [-256], 44 ],
        [ [-112,41216,-143], -1 ],
        [ [-122,41472,-133], -1 ],
        [ [-34,33280,-4,33536,-8,33801,-1,34048,-12,34304,-23,34560,-5,34816,-1,28416,28672,-2,28928,29184,-4,35072,-141], -1 ],
        [ [-48,41737,-1,41984,-36,34560,-7,28416,28672,-2,28928,29184,-146], -1 ],
        [ [-256], 17 ],
        [ [-123,38400,-132], -1 ],
        [ [-256], 71 ],
        [ [-256], 51 ],
        [ [-48,35337,-41,35584,-156], -1 ],
        [ [-256], 21 ],
        [ [-48,35849,-1,36096,-14,36352,36608,-28,36864,37120,-2,37376,-1,37632,-3,37888,38144,-140], -1 ],
        [ [-48,42249,-1,36096,-44,36864,37120,-2,37376,-5,37888,38144,-140], -1 ],
        [ [-256], 46 ],
        [ [-256], 47 ],
        [ [-256], 49 ],
        [ [-256], 82 ],
        [ [-256], 42 ],
        [ [-256], 58 ],
        [ [-256], 91 ],
        [ [-256], 97 ],
        [ [-256], 50 ],
        [ [-256], 40 ],
        [ [38918,39168,38930,39424,39139], -1 ],
        [ [42522,42752,42723], 99 ],
        [ [43014,43264,43091,43520,43170], -1 ],
        [ [-256], 89 ],
        [ [-256], 4 ],
        [ [40218,40448,40419], -1 ],
        [ [-92,43776,-163], -1 ],
        [ [40730,40960,40931], -1 ],
        [ [-92,44032,-163], -1 ],
        [ [-256], 45 ],
        [ [-256], 31 ],
        [ [-48,41737,-1,41984,-36,34560,-7,28416,28672,-2,28928,29184,-146], -1 ],
        [ [-36,44288,-2,44544,-8,44809,-1,45056,-36,34560,-7,28416,28672,-2,28928,29184,-146], -1 ],
        [ [-48,42249,-1,36096,-44,36864,37120,-2,37376,-5,37888,38144,-140], -1 ],
        [ [42522,42752,42723], -1 ],
        [ [-92,45312,-163], -1 ],
        [ [43014,43264,43255], -1 ],
        [ [-256], 99 ],
        [ [43014,43264,43255], 100 ],
        [ [-256], 41 ],
        [ [-256], 54 ],
        [ [-122,45568,45824,-132], -1 ],
        [ [-119,46080,-136], -1 ],
        [ [-36,44288,-2,44544,-8,44809,-1,45056,-36,34560,-7,28416,28672,-2,28928,29184,-146], -1 ],
        [ [-36,46336,-11,46601,-1,46848,-24,47104,-11,34560,-7,28416,28672,-2,28928,29184,-146], -1 ],
        [ [-256], 100 ],
        [ [-256], 32 ],
        [ [-256], 48 ],
        [ [-256], 30 ],
        [ [-114,47360,-1,47616,-3,47872,-135], -1 ],
        [ [-36,46336,-11,46601,-1,46848,-24,47104,-11,34560,-7,28416,28672,-2,28928,29184,-146], -1 ],
        [ [-48,48137,-1,48384,-36,34560,-7,28416,28672,-2,28928,29184,-146], -1 ],
        [ [-256], 85 ],
        [ [-256], 25 ],
        [ [-256], 37 ],
        [ [-256], 33 ],
        [ [-48,48137,-1,48384,-36,34560,-7,28416,28672,-2,28928,29184,-146], -1 ],
        [ [-48,48649,-1,48896,-36,34560,-7,28416,28672,-2,28928,29184,-146], -1 ],
        [ [-48,48649,-1,48896,-36,34560,-7,28416,28672,-2,28928,29184,-146], -1 ],
        [ [-36,49152,-11,49417,-1,49664,-36,34560,-7,28416,28672,-2,28928,29184,-146], -1 ],
        [ [-118,49920,-137], -1 ],
        [ [-36,49152,-11,49417,-1,49664,-36,34560,-7,28416,28672,-2,28928,29184,-146], -1 ],
        [ [-48,50185,-1,49664,-36,34560,-7,28416,28672,-2,28928,29184,-146], -1 ],
        [ [-256], 26 ],
        [ [-48,50185,-1,49664,-36,34560,-7,28416,28672,-2,28928,29184,-146], -1 ]
    ]
};

/**
 * @classDescription {XTermHandler} This class is a TermParser handler for xterm
 * key sequences. The parser will generate events and fire  like-name methods on
 * this class passing in the event name and the event's parameters string. This
 * class with then call the appropriate methods on a Term to implement the given
 * event's functionality
 */

/**
 * Create a new instance of XTermHandler
 * 
 * @constructor
 * @alias XTermHandler
 * @param {Term} term
 * 		The terminal object to control as events are recognized
 */
function XTermHandler(term)
{
	// NOTE: throw if term is null. This will remove the need
	// for null checks in each of the handlers which should
	// speed things up a bit
	this._term = term;
	this._insertMode = false;
	this._missingCommands = {};
}

/**
 * BEL
 * 
 * @alias XTermHandler.prototype.BEL
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.BEL = function(eventName, paramsString)
{
	// nothing for now
};

/**
 * BS
 * 
 * @alias XTermHandler.prototype.BS
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.BS = function(eventName, paramsString)
{
	var col = this._term.getColumn() - 1;
	
	col = Math.max(0, col);
	
	this._term.setColumn(col);
};

/**
 * CHA
 * 
 * @alias XTermHandler.prototype.CHA
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.CHA = function(eventName, paramsString)
{
	var column = 0;
	
	if (paramsString.length > 0)
	{
		column = paramsString - 1;
	}
	
	this._term.setColumn(column);
};

/**
 * CR
 * 
 * @alias XTermHandler.prototype.CR
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.CR = function(eventName, paramsString)
{
	this._term.setColumn(0);
};

/**
 * CUB
 * 
 * @alias XTermHandler.prototype.CUB
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.CUB = function(eventName, paramsString)
{
	// NOTE: vt100.net doesn't mention if we should take the left margin
	// into account like we take the bottom margin into account with CUD/CUU
	var count = 1;
	
	if (paramsString.length > 0) 
	{
		count = paramsString - 0;
		
		if (count == 0)
		{
			count = 1;
		}
	}
	
	var col = this._term.getColumn() - count;
	
	col = Math.max(0, col);
	
	this._term.setColumn(col);
};

/**
 * CUD
 * 
 * @alias XTermHandler.prototype.CUD
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.CUD = function(eventName, paramsString)
{
	var count = 1;
	
	if (paramsString.length > 0) 
	{
		count = paramsString - 0;
		
		if (count == 0)
		{
			count = 1;
		}
	}
	
	var currentRow = this._term.getRow();
	var bottomMargin = this._term.getScrollRegion().bottom;
	var newRow;
	
	if (currentRow <= bottomMargin)
	{
		// we're above or at the bottom margin
		newRow = Math.min(currentRow + count, bottomMargin);
	}
	else
	{
		// we're below the bottom margin
		newRow = Math.min(currentRow + count, this._term.getHeight() - 1);
	}
	
	this._term.setRow(newRow);
};

/**
 * CUF
 * 
 * @alias XTermHandler.prototype.CUF
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.CUF = function(eventName, paramsString)
{
	// NOTE: vt100.net doesn't mention if we should take the left margin
	// into account like we take the bottom margin into account with CUD/CUU
	var count = 1;
	
	if (paramsString.length > 0) 
	{
		count = paramsString - 0;
		
		if (count == 0)
		{
			count = 1;
		}
	}
	
	var col = this._term.getColumn() + count;
	
	col = Math.min(col, this._term.getWidth() - 1);
	
	this._term.setColumn(col);
};

/**
 * CUP
 * 
 * @alias XTermHandler.prototype.CUP
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.CUP = function(eventName, paramsString)
{
	var row = 0;
	var col = 0;
	var height = this._term.getHeight();
	
	if (paramsString.length > 0) 
	{
		// NOTE: Need to take DECOM origin setting into account
		var params = paramsString.split(/;/);
		var row = params[0] - 1;
		var col = params[1] - 1;
	}
	
	if (row >= height)
	{
		var count = height - row;
		
		row = height - 1;
		
		this._term.scrollUp(count);
	}

	this._term.setPosition(row, col);
};

/**
 * CUU
 * 
 * @alias XTermHandler.prototype.CUU
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.CUU = function(eventName, paramsString)
{
	var count = 1;
	
	if (paramsString.length > 0) 
	{
		count = paramsString - 0;
		
		if (count == 0)
		{
			count = 1;
		}
	}
	
	var currentRow = this._term.getRow();
	var topMargin = this._term.getScrollRegion().top;
	var newRow;
	
	if (topMargin <= currentRow)
	{
		// we're below or at the top margin
		newRow = Math.max(topMargin, currentRow - count);
	}
	else
	{
		// we're below the bottom margin
		newRow = Math.max(0, currentRow - count);
	}
	
	this._term.setRow(newRow);
};

/**
 * DCH
 * 
 * @alias XTermHandler.prototype.DCH
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.DCH = function(eventName, paramsString)
{
	var count = paramsString - 0;
	
	this._term.deleteCharacter(count);
};

/**
 * DECALN
 * 
 * @alias XTermHandler.prototype.DECALN
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.DECALN = function(eventName, paramsString)
{
	this._term.clear('E');
};

/**
 * DECRC
 * 
 * @alias XTermHandler.prototype.DECRC
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.DECRC = function(eventName, paramsString)
{
	this._term.popPosition();
};

/**
 * DECPAM
 * 
 * @alias XTermHandler.prototype.DECPAM
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.DECPAM = function(eventName, paramsString)
{
	this._term.setApplicationKeys(true);
};

/**
 * DECPNM
 * 
 * @alias XTermHandler.prototype.DECPNM
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.DECPNM = function(eventName, paramsString)
{
	this._term.setApplicationKeys(false);
};

/**
 * DECRST
 * 
 * @alias XTermHandler.prototype.DECRST
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.DECRST = function(eventName, paramsString)
{
	var mode = paramsString - 0;
	
	switch (mode)
	{
		case 1: // Normal Cursor Keys (DECCKM)
			this._term.setApplicationKeys(false);
			break;
			
		case 3: // 80 Column Mode (DECCOLM)
			this._term.setWidth(80);
			break;
			
		case 25: // Hide Cursor (DECTCEM)
			this._term.setCursorVisible(false);
			break;
		
		case 47: // Use Normal Screen Buffer
			this._term.popBuffer();
			break;
			
		case 1049:	// Use Normal Screen Buffer and restore cursor as in DECRC (unless disabled by
					// the titeInhibit resource). This combines the effects of the 1047 and 1048
					// modes. Use this with terminfo-based applications rather than the 47 mode.
			this._term.popPosition();
			this._term.popBuffer();
			break;
			
		/*
		case 2: // Designate VT52 mode (DECANM).
		case 4: // Jump (Fast) Scroll (DECSCLM)
		case 5: // Normal Video (DECSCNM)
		case 6: // Normal Cursor Mode (DECOM)
		case 7: // No Wraparound Mode (DECAWM)
		case 8: // No Auto-repeat Keys (DECARM)
		case 9: // Don’t send Mouse X & Y on button press
		case 10: // Hide toolbar (rxvt)
		case 12: // Stop Blinking Cursor (att610)
		case 18: // Don’t print form feed (DECPFF)
		case 19: // Limit print to scrolling region (DECPEX)
		case 30: // Don’t show scrollbar (rxvt).
		case 35: // Disable font-shifting functions (rxvt).
		case 40: // Disallow 80 → 132 Mode
		case 41: // No more(1) fix (see curses resource)
		case 42: // Disable Nation Replacement Character sets (DECNRCM)
		case 44: // Turn Off Margin Bell
		case 45: // No Reverse-wraparound Mode
		case 46: // Stop Logging (normally disabled by a compile-time option)
		case 66: // Numeric keypad (DECNKM)
		case 67: // Backarrow key sends delete (DECBKM)
		case 1000: // Don’t send Mouse X & Y on button press and release. See the section Mouse Tracking.
		case 1001: // Don’t use Hilite Mouse Tracking.
		case 1002: // Don’t use Cell Motion Mouse Tracking.
		case 1003: // Don’t use All Motion Mouse Tracking.
		case 1004: // Don’t send FocusIn/FocusOut events.
		case 1010: // Don’t scroll to bottom on tty output (rxvt).
		case 1011: // Don’t scroll to bottom on key press (rxvt).
		case 1034: // Don’t interpret "meta" key (disables the eightBitInput resource).
		case 1035: // Disable special modifiers for Alt and NumLock keys (disables the numLock resource).
		case 1036: // Don’t send ESC when Meta modifies a key (disables the metaSendsEscape resource).
		case 1037: // Send VT220 Remove from the editing-keypad Delete key
		case 1039: // Don’t send ESC when Alt modifies a key (disables the altSendsEscape resource).
		case 1040: // Do not keep selection when not highlighted (disables the keepSelection resource).
		case 1041: // Use the PRIMARY selection. (disables the selectToClipboard resource).
		case 1042: // Disable Urgency window manager hint when Control-G is received (disables the bellIsUrgent resource).
		case 1043: // Disable raising of the window when Control-G is received (disables the popOnBell resource).
		case 1047: // Use Normal Screen Buffer, clearing screen first if in the Alternate Screen (unless disabled by the titeInhibit resource)
		case 1048: // Restore cursor as in DECRC (unless disabled by the titeInhibit resource)
		case 1050: // Reset terminfo/termcap function-key mode.
		case 1051: // Reset Sun function-key mode.
		case 1052: // Reset HP function-key mode.
		case 1053: // Reset SCO function-key mode.
		case 1060: // Reset legacy keyboard emulation (X11R6).
		case 1061: // Reset keyboard emulation to Sun/PC style.
		case 2004: // Reset bracketed paste mode.
			// fall through
		*/
		
		default:
			// report as unsupported
			this.genericHandler(eventName, paramsString);
			break;
	}
};

/**
 * DECSC
 * 
 * @alias XTermHandler.prototype.DECSC
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.DECSC = function(eventName, paramsString)
{
	this._term.pushPosition();
};

/**
 * DECSET
 * 
 * @alias XTermHandler.prototype.DECSET
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.DECSET = function(eventName, paramsString)
{
	var mode = paramsString - 0;
	
	switch (mode)
	{
		case 1: // Application Cursor Keys (DECCKM)
			this._term.setApplicationKeys(true);
			break;
			
		case 3: // 132 Column Mode (DECCOLM)
			this._term.setWidth(132);
			break;
			
		case 25: // Show Cursor (DECTCEM)
			this._term.setCursorVisible(true);
			break;
			
		case 47: // Use Alternate Screen Buffer (unless disabled by the titeInhibit resource)
			this._term.pushBuffer();
			break;
			
		case 1049:	// Save cursor as in DECSC and use Alternate Screen Buffer, clearing it first
					// (unless disabled by the titeInhibit resource). This combines the effects of
					// the 1047 and 1048 modes. Use this with terminfo-based applications rather
					// than the 47 mode.
			this._term.pushPosition();
			this._term.pushBuffer();
			break;
			
		/*
		case 2: // Designate USASCII for character sets G0-G3 (DECANM), and set VT100 mode.
		case 4: // Smooth (Slow) Scroll (DECSCLM)
		case 5: // Reverse Video (DECSCNM)
		case 6: // Origin Mode (DECOM)
		case 7: // Wraparound Mode (DECAWM)
		case 8: // Auto-repeat Keys (DECARM)
		case 9: // Send Mouse X & Y on button press. See the section Mouse Tracking.
		case 10: // Show toolbar (rxvt)
		case 12: // Start Blinking Cursor (att610)
		case 18: // Print form feed (DECPFF)
		case 19: // Set print extent to full screen (DECPEX)
		case 30: // Show scrollbar (rxvt).
		case 35: // Enable font-shifting functions (rxvt).
		case 38: // Enter Tektronix Mode (DECTEK)
		case 40: // Allow 80 → 132 Mode
		case 41: // more(1) fix (see curses resource)
		case 42: // Enable Nation Replacement Character sets (DECNRCM)
		case 44: // Turn On Margin Bell
		case 45: // Reverse-wraparound Mode
		case 46: // Start Logging (normally disabled by a compile-time option)
		case 66: // Application keypad (DECNKM)
		case 67: // Backarrow key sends backspace (DECBKM)
		case 1000: // Send Mouse X & Y on button press and release. See the section Mouse Tracking.
		case 1001: // Use Hilite Mouse Tracking.
		case 1002: // Use Cell Motion Mouse Tracking.
		case 1003: // Use All Motion Mouse Tracking.
		case 1004: // Send FocusIn/FocusOut events.
		case 1010: // Scroll to bottom on tty output (rxvt).
		case 1011: // Scroll to bottom on key press (rxvt).
		case 1034: // Interpret "meta" key, sets eighth bit. (enables the eightBitInput resource).
		case 1035: // Enable special modifiers for Alt and NumLock keys (enables the numLock resource).
		case 1036: // Send ESC when Meta modifies a key (enables the metaSendsEscape resource).
		case 1037: // Send DEL from the editing-keypad Delete key
		case 1039: // Send ESC when Alt modifies a key (enables the altSendsEscape resource).
		case 1040: // Keep selection even if not highlighted (enables the keepSelection resource).
		case 1041: // Use the CLIPBOARD selection (enables the selectToClipboard resource).
		case 1042: // Enable Urgency window manager hint when Control-G is received (enables the bellIsUrgent resource).
		case 1043: // Enable raising of the window when Control-G is received (enables the popOnBell resource).
		case 1047: // Use Alternate Screen Buffer (unless disabled by the titeInhibit resource)
		case 1048: // Save cursor as in DECSC (unless disabled by the titeInhibit resource)
		case 1050: // Set terminfo/termcap function-key mode.
		case 1051: // Set Sun function-key mode.
		case 1052: // Set HP function-key mode.
		case 1053: // Set SCO function-key mode.
		case 1060: // Set legacy keyboard emulation (X11R6).
		case 1061: // Set VT220 keyboard emulation.
		case 2004: // Set bracketed paste mode.
			// fall through
		*/
			
		default:
			// report as unsupported
			this.genericHandler(eventName, paramsString);
			break;
	}
};

/**
 * DECSTBM
 * 
 * @alias XTermHandler.prototype.DECSTBM
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.DECSTBM = function(eventName, paramsString)
{
	var params = paramsString.split(/;/);
	var top = params[0] - 1;
	var bottom = params[1] - 1;
	
	this._term.setScrollRegion(top, 0, bottom, this._term.getWidth() - 1);
};

/**
 * DL
 * 
 * @alias XTermHandler.prototype.DL
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.DL = function(eventName, paramsString)
{
	var count = 1;
	
	if (paramsString.length > 0)
	{
		count = paramsString - 0;
		
		// NOTE: assuming a zero value works the same as the CU* actions
		if (count == 0)
		{
			count = 1;
		}
	}
	
	this._term.deleteLine(count);
};

/**
 * ED
 * 
 * @alias XTermHandler.prototype.ED
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.ED = function(eventName, paramsString)
{
	var param = paramsString - 0;
	
	switch (param)
	{
		case 0:
			this._term.clearAfter();
			break;
			
		case 1:
			this._term.clearBefore();
			break;
			
		case 2:
			this._term.clear();
			break;
		
		default:
			this.genericHandler(eventName + ":" + paramsString, "");
			break;
	}
};

/**
 * EL
 * 
 * @alias XTermHandler.prototype.EL
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.EL = function(eventName, paramsString)
{
	var param = paramsString - 0;
	
	switch (param)
	{
		case 0:
			this._term.clearRight();
			break;
			
		case 1:
			this._term.clearLeft();
			break;
			
		case 2:
			this._term.clearLine();
			break;
			
		default:
			this.genericHandler(eventName + ":" + paramsString, "");
			break;
	}
};

/**
 * genericHandler
 * 
 * @alias XTermHandler.prototype.genericHandler
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.genericHandler = function(eventName, paramsString)
{
	if (this._missingCommands.hasOwnProperty(eventName) === false)
	{
		this._missingCommands[eventName] = 0;
	}
	
	// increment count
	this._missingCommands[eventName]++;
};

/**
 * getMissingCommands
 * 
 * @alias XTermHandler.prototype.getMissingCommands
 * @return {Object}
 */
XTermHandler.prototype.getMissingCommands = function()
{
	return this._missingCommands;
};

/**
 * HVP
 * 
 * @alias XTermHandler.prototype.HVP
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.HVP = XTermHandler.prototype.CUP;

/**
 * ICH
 * 
 * @alias XTermHandler.prototype.ICH
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.ICH = function(eventName, paramsString)
{
	var count = paramsString - 0;
	
	this._term.insertCharacter(' ', count);
};

/**
 * IL
 * 
 * @alias XTermHandler.prototype.IL
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.IL = function(eventName, paramsString)
{
	var count = 1;
	
	if (paramsString.length > 0)
	{
		count = paramsString - 0;
		
		// NOTE: assuming a zero value works the same as the CU* actions
		if (count == 0)
		{
			count = 1;
		}
	}
	
	this._term.insertLine(count);
};

/**
 * IND
 * 
 * @alias XTermHandler.prototype.IND
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.IND = function(eventName, paramsString)
{
	var currentRow = this._term.getRow();
	var bottomMargin = this._term.getScrollRegion().bottom;
	var newRow = currentRow + 1;
	
	if (currentRow <= bottomMargin)
	{
		this._term.setRow(newRow);
	}
	else
	{
		this._term.scrollUp(1);
		this._term.setRow(bottomMargin);
	}
};

/**
 * LF
 * 
 * @alias XTermHandler.prototype.LF
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.LF = function(eventName, paramsString)
{
	var term = this._term;
	var row = term.getRow() + 1;
	var height = term.getScrollRegion().bottom;
	
	if (row > height)
	{
		term.scrollUp();
		
		row = height;
	}
	
	// NOTE: not sure we should be setting the column here, but menu.txt fails
	// without this. It's not clear if this is a mode set by DECRST or DECSET
	if (this._term.getApplicationKeys() === false)
	{
		term.setPosition(row, 0);
	}
	else
	{
		term.setRow(row);
	}
};

/**
 * NEL
 * 
 * @alias XTermHandler.prototype.NEL
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.NEL = XTermHandler.prototype.LF;

/**
 * processCharacter
 * 
 * @alias XTermHandler.prototype.processCharacter
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.processCharacter = function(eventName, paramsString)
{
	if (this._insertMode)
	{
		this._term.insertCharacter(' ', 1);
	}
	
	this._term.displayCharacters(paramsString);
};

/**
 * RI
 * 
 * @alias XTermHandler.prototype.RI
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.RI = function(eventName, paramsString)
{
	var currentRow = this._term.getRow();
	var topMargin = this._term.getScrollRegion().top;
	var newRow = currentRow - 1;
	
	if (topMargin <= newRow)
	{
		this._term.setRow(newRow);
	}
	else
	{
		this._term.scrollDown(1);
		this._term.setRow(topMargin);
	}
};

/**
 * RM
 * 
 * @alias XTermHandler.prototype.RM
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.RM = function(eventName, paramsString)
{
	var mode = paramsString - 0;
	
	switch (mode)
	{
		case 4:
			this._insertMode = false;
			break;
			
		case 2:
		case 12:
		case 20:
		// fall through
		
		default:
			// report as unsupported
			this.genericHandler(eventName, paramsString);
			break;
	}
};

/**
 * SD
 * 
 * @alias XTermHandler.prototype.SD
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.SD = function(eventName, paramsString)
{
	var count = 1;
	
	if (paramsString.length > 0)
	{
		count = paramsString - 0;
	}
	
	var currentRow = this._term.getRow();
	var topMargin = this._term.getScrollRegion().top;
	var newRow = currentRow - count;
	
	if (topMargin <= newRow)
	{
		this._term.setRow(newRow);
	}
	else
	{
		this._term.scrollDown(count);
		this._term.setRow(topMargin);
	}
};

/**
 * SET_TEXT_PARAMS
 * 
 * @alias XTermHandler.prototype.SET_TEXT_PARAMS
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.SET_TEXT_PARAMS = function(eventName, paramsString)
{
	var params = paramsString.split(/;/);
	var code = params[0] - 0;
	var text = params[1];
	
	if (code == 0)
	{
		this._term.setTitle(text);
	}
	else
	{
		this.genericHandler(eventName + ":" + paramsString, "");
	}
};

/**
 * SET_TEXT_PARAMS2
 * 
 * @alias XTermHandler.prototype.SET_TEXT_PARAMS2
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.SET_TEXT_PARAMS2 = XTermHandler.prototype.SET_TEXT_PARAMS;

/**
 * SGR
 * 
 * @alias XTermHandler.prototype.SGR
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.SGR = function(eventName, paramsString)
{
	var attr = this._term.getCurrentAttribute();
	var params = paramsString.split(/;/);
	
	for (var i = 0; i < params.length; i++)
	{
		var param = params[i] - 0;
		
		if (param < 50)
		{
			var tens = Math.floor(param / 10);
			var ones = param % 10;
			
			switch (tens)
			{
				case 0:
					switch (ones)
					{
						case 0:
							attr.reset();
							break;
							
						case 1:
							attr.bold = true;
							break;
							
						case 3:
							attr.italic = true;
							break;
							
						case 4:
							attr.underline = true;
							break;
							
						case 7:
							attr.inverse = true;
							break;
							
						case 9:
							attr.strikethrough = true;
							break;
						
						default:
							this.genericHandler(eventName + ":" + paramsString, "");
							break;
					}
					break;
				
				case 2:
					switch (ones)
					{
						case 2:
							attr.bold = false;
							break;
							
						case 3:
							attr.italic = false;
							break;
							
						case 4:
							attr.underline = false;
							break;
							
						case 7:
							attr.inverse = false;
							break;
							
						case 9:
							attr.strikethough = false;
							break;
						
						default:
							this.genericHandler(eventName + ":" + paramsString, "");
							break;
					}
					break;
				
				case 3:
					switch (ones)
					{
						case 0:
						case 1:
						case 2:
						case 3:
						case 4:
						case 5:
						case 6:
						case 7:
							attr.foreground = ones;
							break;
						
						case 9:
							attr.resetForeground();
							break;
						
						default:
							this.genericHandler(eventName + ":" + paramsString, "");
							break;
					}
					break;
				
				case 4:
					switch (ones)
					{
						case 0:
						case 1:
						case 2:
						case 3:
						case 4:
						case 5:
						case 6:
						case 7:
							attr.background = ones;
							break;
						
						case 9:
							attr.resetBackground();
							break;
						
						default:
							this.genericHandler(eventName + ":" + paramsString, "");
							break;
					}
					break;
				
				default:
					this.genericHandler(eventName + ":" + paramsString, "");
					break;
			}
		}
		else
		{
			this.genericHandler(eventName + ":" + paramsString, "");
		}
	}
	
	this._term.setCurrentAttribute(attr);
};

/**
 * SM
 * 
 * @alias XTermHandler.prototype.SM
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.SM = function(eventName, paramsString)
{
	var mode = paramsString - 0;
	
	switch (mode)
	{
		case 4:
			this._insertMode = true;
			break;
			
		case 2:
		case 12:
		case 20:
		// fall through
		
		default:
			// report as unsupported
			this.genericHandler(eventName, paramsString);
			break;
	}
};

/**
 * SU
 * 
 * @alias XTermHandler.prototype.SU
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.SU = function(eventName, paramsString)
{
	var count = 1;
	
	if (paramsString.length > 0)
	{
		count = paramsString - 0;
	}
	
	var currentRow = this._term.getRow();
	var bottomMargin = this._term.getScrollRegion().bottom;
	var newRow = currentRow + count;
	
	if (newRow <= bottomMargin)
	{
		this._term.setRow(newRow);
	}
	else
	{
		this._term.scrollUp(count);
		this._term.setRow(bottomMargin);
	}
};

/**
 * TAB
 * 
 * @param {Object} eventName
 * @param {Object} paramsString
 */
XTermHandler.prototype.TAB = function(eventName, paramsString)
{
	var currentColumn = this._term.getColumn();
	var spaces = 8 - (currentColumn % 8);
	
	this._term.displayCharacters(new Array(spaces + 1).join(" "));
};

/**
 * VPA
 * 
 * @alias XTermHandler.prototype.VPA
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.VPA = function(eventName, paramsString)
{
	var row = 0;
	
	if (paramsString.length > 0)
	{
		row = paramsString - 1;
	}
	
	this._term.setRow(row);
};

/**
 * VT
 * 
 * @alias XTermHandler.prototype.VT
 * @param {String} eventName
 * @param {String} paramsString
 */
XTermHandler.prototype.VT = XTermHandler.prototype.LF;

/**
 * @classDescription {BroadcastHandler} This class is a type of TermParser
 * handler. It allows multiple handlers to be associated with it. When an event
 * is fired on a BroadcastHandler instance, it will simply forward that event
 * along with the associated parameters string to all of its children. This
 * allows TermParser to have more than one handler associated with it.
 */

/**
 * Create a new instance of BroadcastHandler
 * 
 * @constructor
 * @alias BroadcastHandler
 */
function BroadcastHandler()
{
	this._handlers = [];
}

/**
 * Add a handler to the list of handlers
 * 
 * @alias BroadcastHandler.prototype.addHandler
 * @param {IHandler} handler
 */
BroadcastHandler.prototype.addHandler = function(handler)
{
	this._handlers.push(handler);
};

/**
 * A generic event handler. This method is responsible for firing the
 * appropriate event on each handler registered with this class. If the method
 * does not exist on a given handler, then we try to call that handler's
 * genericHandler method
 * 
 * @alias BroadcastHandler.prototype.genericHandler
 * @param {String} eventName
 * @param {String} paramsString
 */
BroadcastHandler.prototype.genericHandler = function(eventName, paramsString)
{
	for (var i = 0; i < this._handlers.length; i++)
	{
		var handler = this._handlers[i];
		
		if (handler[eventName] && handler[eventName] instanceof Function)
		{
			handler[eventName](eventName, paramsString);
		}
		else if (handler.genericHandler && handler.genericHandler instanceof Function)
		{
			handler.genericHandler(eventName, paramsString);
		}
	}
};

/**
 * This method is reponsible for firing the processCharacter event on each
 * handler associated with this class.
 * 
 * @alias BroadcastHandler.prototype.processCharacter
 * @param {String} eventName
 * @param {String} paramsString
 */
BroadcastHandler.prototype.processCharacter = function(eventName, paramsString)
{
	for (var i = 0; i < this._handlers.length; i++)
	{
		var handler = this._handlers[i];
		
		if (handler.processCharacter && handler.processCharacter instanceof Function) 
		{
			handler.processCharacter(eventName, paramsString);
		};
	}
};

/**
 * Remove the specified handler from the handler list
 * 
 * @alias BroadcastHandler.prototype.removeHandler
 * @param {IHandler} handler
 */
BroadcastHandler.prototype.removeHandler = function(handler)
{
	for (var i = 0; i < this._handlers.length; i++)
	{
		if (handler === this._handlers[i])
		{
			this._handlers.splice(i, 1);
			break;
		}
	}
};

/**
 * @classDescription {DebugHandler} A DebugHandler keeps track of events that
 * are fired on it. Multiple aspects of each event are maintained: 1) an ordered
 * list showing each event with its parameters in the order they fired, 2) a
 * hash of all event names that were encountered along with frequency counts,
 * and 3) a hash of all event names plus their parameters string along with
 * frequency counts 
 */

/**
 * Create a new instance of DebugHandler
 * 
 * @constructor
 * @alias DebugHandler
 */
function DebugHandler()
{
	this.clear();
}

/**
 * Clear all tracking information
 * 
 * @alias DebugHandler.prototype.clear
 */
DebugHandler.prototype.clear = function()
{
	this._commands = {};
	this._commandsWithParams = {};
	this._actions = [];
	this._text = [];
};

/**
 * This method is responsible for tracking information about the given event
 * and its parameters string.
 * 
 * @alias DebugHandler.prototype.genericHandler
 * @param {String} eventName
 * @param {String} paramsString
 */
DebugHandler.prototype.genericHandler = function(eventName, paramsString)
{
	// register command
	if (this._commands.hasOwnProperty(eventName) == false) 
	{
		this._commands[eventName] = 1;
	}
	else 
	{
		this._commands[eventName]++;
	}
	
	// register command and parameters
	var key = eventName + ":" + paramsString;
	
	if (this._commandsWithParams.hasOwnProperty(key) == false)
	{
		this._commandsWithParams[key] = 1;
	}
	else
	{
		this._commandsWithParams[key]++;
	}
	
	// push accumulated text, if any
	if (this._text.length > 0)
	{
		this._actions.push("text: " + this._text.join(""));
		this._text = [];
	}
	
	// add entry to action list
	this._actions.push(eventName + "(" + paramsString + ")");
};

/**
 * Return a list of all actions that have occurred since the last reset. Actions
 * will be shown in the order in which they were received along with its
 * parameters string
 * 
 * @alias DebugHandler.prototype.getActions
 * @return {Array}
 */
DebugHandler.prototype.getActions = function()
{
	return this._actions;
};

/**
 * Return the hash of all event names that have been processed since the last
 * reset
 * 
 * @alias DebugHandler.prototype.getCommands
 * @return {Object}
 */
DebugHandler.prototype.getCommands = function()
{
	return this._commands;
};

/**
 * Return the hash of all event names and associated parameters string that have
 * been processed since the last reset
 * 
 * @alias DebugHandler.prototype.getCommandsWithParams
 * @return {Object}
 */
DebugHandler.prototype.getCommandsWithParams = function()
{
	return this._commandsWithParams;
};

/**
 * Process the specified character. This method will collect all characters to
 * be displayed until the next action occurs. The accumulated text will be
 * emitted as a "text" event into the actions list.
 *
 * @alias DebugHandler.prototype.processCharacter
 * @param {String} eventName
 * @param {String} paramsString
 */
DebugHandler.prototype.processCharacter = function(eventName, paramsString)
{
    function toHex(d)
    {
        var hex = d.toString(16).toUpperCase();
        
        if (hex.length < 2) 
        {
            hex = "0" + hex;
        }
        
        return hex;
    }
	
    if (paramsString.match(/[\x20-\x7e]/)) 
    {
        this._text.push(paramsString);
    }
    else 
    {
    	this._text.push(toHex(paramsString.charAt(0)));
    }
};

/**
 * @classDescription {TermParser} TermParser is a class used to parse the input
 * stream of a terminal. The parser is table-driven and knows nothing of what it
 * is parsing. This potentially allows this class to support multiple terminal
 * types simply by generating the appropriate actions and DFA tables for that
 * terminal.
 */

/**
 * Create a new instance of TermParser
 * 
 * @constructor
 * @alias TermParser
 * @param {Object} tables
 *         "format" - This property specifies the format in which the actions and
 *             and transitions tables have been written.
 *         "version" - This property indicates the format version allowing for
 *             future changes to a given format.
 *         "actions" - This property is an ordered list of event names possibly
 *             including beginning and ending trim offsets. The offsets are used to
 *             extractthe given event's parameters.
 *        "nodes" - This property is an ordered list of DFA nodes. Each node
 *            has a table of transitions keyed by ASCII character code. Each value
 *            indicates to which new node we should transition on the given input
 *            character. A node also has an accept state. Values that are not -1
 *            indicate that we have found a successful match. The value indicates
 *            the event that has matched and is the index into the actions list
 *            for that match
 * @param {IHandler} [handler]
 *         When defined, this object should contain zero of more function
 *         properties matching the names in the actions list in the tables
 *         parameter. When a given action has been recognized, the same-named
 *         method on this object will be invoked passing in the event name and any
 *         parameter text that matched. Note for code streamlining, any missing
 *         event methods are added to this object; however, that is done in such a
 *         way as not to alter the original object passed in here
 */
function TermParser(tables, handler)
{
    if (tables === null || tables === undefined)
    {
        throw new Error("Parsing tables must be defined when creating a new TermParser");
    }
    
    // verify version and perform any needed pre-processing
    this._processTables(tables);
    
    // extract only what we need
    this._actions = tables.actions
    this._nodes = tables.nodes;
    
    // we might have to perform some magic on the handler. Assign via the setter
    // so we can do that
    this.setHandler(handler);
	
	// the following flag, when true, will cause parse to return once it recognizes
	// an escape sequence. This is used for debugging purposes
	this.singleStep = false;
	this.offset = -1;
}

/**
 * Return the handler associated with this parser
 * 
 * @alias TermParser.prototype.getHandler
 * @return {IHandler}
 */
TermParser.prototype.getHandler = function()
{
    return this._handler;
};

/**
 * Process the entire string passed into this method. As terminal sequences are
 * recognized, events will be fired on the associated handler. If a given
 * character in the string fails to match an action sequence, it is considered
 * a character. That character is sent to the handler's processCharacter method,
 * if it exists.
 * 
 * @alias TermParser.prototype.parse
 * @param {String} source
 */
TermParser.prototype.parse = function(source)
{
    var offset = 0;
    var length = isString(source) ? source.length : 0;

    while (offset < length)
    {
        // BEGIN: inlining match function
        var currentState = 0;
        var acceptState = this._nodes[currentState][1];
        var lastAcceptOffset = (acceptState == -1) ? -2 : offset;
    
        for (var i = offset; i < length; i++)
        {
            var nextNode = this._nodes[currentState];
            
            if (nextNode) 
            {
	            var index = source.charCodeAt(i);
	            
	            // get next state, passing through all codes >= 256 as characters
                var nextState = (index < 256) ? nextNode[0][index] : -1;
                
                if (nextState != -1) 
                {
                    currentState = nextState;
                    
                    var candidateState = this._nodes[currentState][1];
                    
                    if (candidateState != -1) 
                    {
                        lastAcceptOffset = i;
                        acceptState = candidateState;
                    }
                }
                else
                {
                    break;
                }
            }
        }
        // END: inlining match function

		if (acceptState == -1)
        {
            // process current character
            if (this._handler != null)
            {
                if (this._handler.processCharacter) 
                {
                    this._handler.processCharacter("processCharacter", source.charAt(offset));
                }
            }
            
            // advance
            offset++
        }
        else
        {
            var endingOffset = lastAcceptOffset + 1;
            
            if (this._handler != null) 
            {
                var info = this._actions[acceptState];
                var eventName = info[0];
                var params = "";
                
                if (info.length >= 3 && info[1] != -1 && info[2] != -1)
                {
                    params = source.substring(offset + info[1], endingOffset - info[2]);
                }
                
                this._handler[eventName](eventName, params);
            }

			// move past matched text
            offset = endingOffset;
			
			if (this.singleStep)
			{
				this.offset = offset;
				break;
			}
        }
    }
};

/**
 * match
 * 
 * @param {String} source
 * @param {Number} startingOffset
 *
TermParser.prototype.match = function(source, startingOffset)
{
    var currentState = 0;
    var acceptState = this._nodes[currentState].accept;
    var lastAcceptOffset = (acceptState == -1) ? -2 : startingOffset;
    var length = source.length;

    for (var i = startingOffset; i < length; i++)
    {
        var index = source.charCodeAt(i);
        var nextNode = this._nodes[currentState];
        
        if (nextNode) 
        {
            var nextState = nextNode.transitions[index];
            
            if (nextState != -1) 
            {
                currentState = nextState;
                
                var candidateState = this._nodes[currentState].accept;
                
                if (candidateState != -1) 
                {
                    lastAcceptOffset = i;
                    acceptState = candidateState;
                }
            }
            else
            {
                break;
            }
        }
    }

    return {
        acceptState: acceptState,
        endingOffset: lastAcceptOffset + 1
    };
};
*/

/**
 * This private method is used to verify that the table being associated with
 * the parser is of a valid format and version. It is possible (and likely) that
 * further processing will need to be performed on the table before it can be
 * used by the parser. This method is responsible for performing any necessary
 * post-processing. For example, the "morle" format stores the DFA in a
 * compressed format. This method expands the DFA tables for use by the parse
 * method.
 * 
 * @private
 * @alias TermParser.prototype._processTables
 * @param {Object} tables
 */
TermParser.prototype._processTables = function(tables)
{
    if (tables.hasOwnProperty("processed") == false || tables.processed == false)
    {
        switch (tables.format)
        {
            case "expanded":
                break;
                
//            case "morle":
//                // build arrays of -1s
//                var mos = new Array(256);
//                
//                for (var i = 0; i < mos.length; i++)
//                {
//                    mos[i] = -1;
//                }
//                
//                // process nodes
//                var nodes = tables.nodes;
//                
//                for (var i = 0; i < nodes.length; i++)
//                {
//                    var trans = nodes[i].transitions;
//                    var newTrans = [];
//                    
//                    for (var j = 0; j < trans.length; j++)
//                    {
//                        var value = trans[j];
//                        
//                        if (value < 0)
//                        {
//                            newTrans = newTrans.concat(mos.slice(0, -value));
//                        }
//                        else
//                        {
//                            newTrans.push(value);
//                        }
//                    }
//                    
//                    //if (newTrans.length != 256)
//                    //{
//                    //    alert(newTrans.length + " at index " + i + "\n" + trans[i] + "\n" + newTrans[i]);
//                    //}
//                    
//                    nodes[i].transitions = newTrans;
//                }
//                break;
                
            case "rle":
                // build arrays of -1s
                var mos = new Array(256);
                
                for (var i = 0; i < mos.length; i++)
                {
                    mos[i] = -1;
                }
                
                // process nodes
                var nodes = tables.nodes;
                
                for (var i = 0; i < nodes.length; i++)
                {
                    var trans = nodes[i][0];
                    var newTrans = [];
                    
                    for (var j = 0; j < trans.length; j++)
                    {
                        var value = trans[j];
                        
                        if (value < 0)
                        {
                            newTrans = newTrans.concat(mos.slice(0, -value));
                        }
                        else
                        {
                            var actual = value >> 8;
                            var count = (value & 0xFF) + 1;
                            
                            for (var k = 0; k < count; k++)
                            { 
                                newTrans.push(actual);
                            }
                        }
                    }
                    
//                    if (once && newTrans.length != 256)
//                    {
//                        alert([value, actual, count]);
//                        alert(newTrans.length + " at index " + i + "\n" + trans[i] + "\n" + newTrans[i]);
//                        once = false;
//                    }
                    
                    nodes[i][0] = newTrans;
                }
                break;
                
            default:
                break;
        }
        
        tables.processed = true;
    }
}

/**
 * Associate a handler with this parser. This method will compare all action
 * names to the properties on the handler. Any missing handlers will
 * automatically be redirected to the method named "genericHandler", if it
 * exists. If "genericHandler" does not exist, then an empty function is used.
 * Modifications to the handler are done on a "protected clone" so the original
 * object remains unchanged.
 * 
 * @alias TermParser.prototype.setHandler
 * @param {IHandler} handler
 */
TermParser.prototype.setHandler = function(handler)
{
    var clone = null;
    
    if (handler)
    {
        var genericHandler = null;
        var emptyFunction = function(eventName, paramsString) {};
        
        for (var i = 0; i < this._actions.length; i++)
        {
            var action = this._actions[i];
            var eventName = action[0];
            
            if (!handler[eventName])
            {
                if (clone == null)
                {
                    clone = protectedClone(handler);
                    
                    if (!handler.genericHandler)
                    {
                        // empty function function
                        genericHandler = emptyFunction;
                    }
                    else
                    {
                        genericHandler = handler.genericHandler;
                    }
                }
                
                clone[eventName] = genericHandler;
            }
        }
    }
    
    // NOTE: if handler is null, we'll end up assigning a null value to our
    // private property. That is what we want to do here in that case.
    if (clone == null) 
    {
        this._handler = handler;
    }
    else
    {
        this._handler = clone;
    }
};

TermComm.POLLING_INTERVAL_MIN = 125;
TermComm.POLLING_INTERVAL_MAX = 2000;
TermComm.POLLING_GROWTH_RATE = 2.0;
TermComm.DEFAULT_REQUEST_URL = "/stream";
TermComm.DEFAULT_GET_UNIQUE_ID_URL = "/id";

/**
 * This class is responsible for communicating with the server, sending
 * key presses and retrieving terminal output
 *  
 * @constructor
 * @alias {TermComm}
 * @param {Term} terminal
 * @param {Object} [config]
 */
function TermComm(terminal, config)
{
	var self = this;
	
	this.terminal = terminal;
	this.keyHandler = terminal.getKeyHandler();
	this.keyHandler.callback = function()
	{
		self.sendKeys();
	}
	
	// set default values
	this.minInterval = 125;
	this.maxInterval = 2000;
	this.growthRate = 2;
	this.timeoutInterval = 5000;
	this.requestURL = TermComm.DEFAULT_REQUEST_URL;
	this.getUniqueIdURL = TermComm.DEFAULT_GET_UNIQUE_ID_URL;
	
	// then process optional configuration, if defined
	if (isDefined(config))
	{
		if (config.hasOwnProperty("minInterval") && isNumber(config.minInterval))
		{
			this.minInterval = config.minInterval;
		}
		if (config.hasOwnProperty("maxInterval") && isNumber(config.maxInterval))
		{
			this.maxInterval = config.maxInterval;
		}
		if (config.hasOwnProperty("growthRate") && isNumber(config.growthRate))
		{
			this.growthRate = config.growthRate;
		}
		if (config.hasOwnProperty("timeoutInterval") && isNumber(config.timeoutInterval))
		{
			this.timeoutInterval = config.timeoutInterval;
		}
		if (config.hasOwnProperty("requestURL") && isString(config.requestURL) && config.requestURL.length > 0)
		{
			this.requestURL = config.requestURL;
		}
		if (config.hasOwnProperty("getUniqueIdURL") && isString(config.getUniqueIdURL) && config.getUniqueIdURL.length > 0)
		{
			this.getUniqueIdURL = config.getUniqueIdURL;
		}
	}
	
	this.pollingInterval = this.minInterval;
	this.watchdogID = null;
	this.requestID = null;
	this.running = false;
	this.gettingInput = false;
	this.updateQueued = false;
	this.sendingKeys = false;
	this.cacheBusterID = 0;
	
	this.ie = (window.ActiveXObject) ? true : false;
}

/**
 * Get a unique identifier. This identifier is used by a terminal to indicate
 * which instance the server is talking to when it receives XHRs from this
 * client.
 * 
 * @alias {TermComm.prototype.getUniqueID}
 * @return {String}
 */
TermComm.prototype.getUniqueID = function()
{
	var req = createXHR();
					
	req.open("GET", this.getUniqueIdURL, false);
	req.send("");
	
	return req.responseText;
};

/**
 * Determine if we are actively communicating with the server at this time
 * 
 * @alias {TermComm.prototype.isRunning}
 * @return {Boolean}
 */
TermComm.prototype.isRunning = function()
{
	return this.running;
}

/**
 * getInput
 * 
 * @alias {TermComm.prototype.getInput}
 */
TermComm.prototype.getInput = function()
{
	if (this.watchdogID === null)
	{
		var self = this;
		var req = createXHR();
		var entries = {
			id : this.terminal.getId(),
			cb : new Date().getTime() + ":" + this.cacheBusterID++
		};
					
	    req.open("GET", createURL(this.requestURL, entries), true);
		
		if (this.ie)
		{
			req.setRequestHeader("If-Modified-Since", "Sat, 1 Jan 2000 00:00:00 GMT");
		}
		
		req.onreadystatechange = function()
		{
			if (req.readyState == 4)
			{
				// clear watchdog
				if (self.watchdogID !== null)
				{
					window.clearTimeout(self.watchdogID);
					self.watchdogID = null;
				}
				
				// grab response and process
				var text = req.responseText;
				
				if (isString(text) && text.length > 0)
				{
					// TODO: This might need to be called with a 0ms timeout so as not to block
					// this function while processing large blocks of text
	                self.terminal.processCharacters(text);
					self.pollingInterval = self.minInterval;
				}
				else
				{
					self.pollingInterval *= self.growthRate;
					
					if (self.pollingInterval > self.maxInterval)
					{
						self.pollingInterval = self.maxInterval;
					}
				}
				
				self.requestID = window.setTimeout(
					function() { self.update(); },
					(this.updateQueued) ? 0 : self.pollingInterval
				);

				// clear any pending updates				
				this.updateQueued = false;
			}
		};
		
		this.watchdogID = window.setTimeout(
			function() { self.timeout(); },
			this.timeoutInterval
		);
		
	    req.send("");
	}
	else
	{
		this.updateQueued = true;
	}
};

/**
 * Send any keys in the key handler buffer
 * 
 * @alias {TermComm.prototype.sendKeys}
 */
TermComm.prototype.sendKeys = function()
{
	var id = this.terminal.getId();
	
	if (isDefined(this.keyHandler) && id !== null)
	{
		if (this.keyHandler.hasContent() && this.sendingKeys === false)
		{
			this.sendingKeys = true;
			
			var self = this;
			var req = createXHR();
			var entries = { id : id };
					
			req.open("POST", createURL(this.requestURL, entries), true);
			
			req.onreadystatechange = function()
			{
				if (req.readyState == 4)
				{
					self.sendingKeys = false;
					
					// fetch any resulting messages from this key press
					// and send any other keys that may have been queued
					// during this last send
					self.update(true);
				}
			};
			
			req.send(this.keyHandler.dequeueAll());
		}
	}
};

/**
 * Timeout
 * 
 * @private
 * @alias {TermComm.prototype.timeout}
 */
TermComm.prototype.timeout = function()
{
	//alert("Connection timeout");
};

/**
 * Toggle the current run state. If the run state is turned on, we force an
 * update and send any keys that are waiting to be sent to the server
 * 
 * @alias {TermComm.prototype.toggleRunState}
 */
TermComm.prototype.toggleRunState = function()
{
	this.running = !this.running;
	
	if (this.running)
	{
		this.update(true);
	}
};

/**
 * This method performs all communications with the server
 * 
 * @alias {TermComm.prototype.update}
 * @param {Boolean} processKeys
 */
TermComm.prototype.update = function(processKeys)
{
	if (this.running && this.terminal.getId() !== null)
	{
		// send any accumulated keys, if asked to
		if (isBoolean(processKeys))
		{
			// our request timer calls update with processKeys undefined, so if
			// it is defined, then we may need to clear out any pending updates
			// from our last update
			if (this.requestID !== null)
			{
				window.clearTimeout(this.requestID);
				this.requestID = null;
			}
			
			if (processKeys)
			{
				this.sendKeys();
				
				// reset polling interval so we can get a quick response
				this.pollingInterval = this.minInterval;
			}
		}
		
		this.getInput();
	}
};

/**
 * @classDescription {Term} A Term is the display for a remote terminal.
 */

Term.DEFAULT_ID = "terminal";
Term.DEFAULT_HEIGHT = 24;
Term.MIN_HEIGHT = 5;
Term.MAX_HEIGHT = 512;

/**
 * Create a new instance of Term.
 * 
 * @constructor
 * @alias Term
 * @param {String} [id]
 * 		This optional parameter specifies the div element to which this terminal
 * 		will be attached. If the id is not specified, then a default value of
 * 		'terminal' will be used. An element using the given id must be present;
 * 		otherwise, this constructor will throw an exception and no terminal will
 * 		be created.
 * @param {Number} [width]
 * 		This optional parameter specifies the width of the terminal. The value
 * 		is clamped to the closed interval [20,512]
 * @param {Number} [height]
 * 		This optional parameter specifies the height of the terminal. The value
 * 		is clamped to the closed interval [5,512]
 * @param {Object} [config]
 * 		This optional parameter is used to override default configurations used
 * 		by the terminal. Zero or more of the following properties can be defined
 * 		on the object:
 * 		"autoStart" - This is a boolean which determines if the communication
 * 			handler should be started as soon as this terminal has been created.
 * 			If this value is not present, autoStart defaults to true
 * 		"commHandler" - This is the communications handler responsible for
 * 			communicating with the server. When this value is not present,
 * 			TermComm will be used
 * 		"handler" - This is the event hander to be used by the parser. When this
 * 			property is not present, XTermHandler will be used.
 * 		"keyHandler" - This is the object used to process typing on the client.
 * 			If this property is not present, KeyHandler will be used.
 * 		"onTitleChange" - This is a callback that will fire whenever the title
 * 			of the terminal changes. The default value is null.
 * 		"parser" - This is the parser to use when processing incoming text and
 * 			command sequences. If this property is not present, TermParser will
 * 			be used.
 * 		"sendResizeSequence" - This is a boolean used to determine if the
 * 			dtterm escape sequence should be sent when the terminal changes
 * 			size. When this value is not present, sendResizeSequence defaults to
 * 			true.
 * 		"showTitle" - This is a boolean used to determine if the terminal title
 * 			bar should be displayed or not. When this value is not present,
 * 			showTitle will default to true. Note that you can also toggle this
 * 			value using the showTitle() method.
 * 		"tables" - This is an object containing the parse tables to be used by
 * 			the parser. When this property is not present, XTermTables will be
 * 			used.
 */
function Term(id, width, height, config)
{
	// make sure we have an id
	if (isString(id) === false || id.length === 0)
	{
		id = "terminal";
	}
	
	// A ID to uniquely identify this terminal
	this._id = (config && config.hasOwnProperty("id")) ? config.id : null;
	
	// a buffer used to keep track of input that hasn't been processed
	this._remainingText = "";
	
	// grab the root element from the id
	this._rootNode = document.getElementById(id);
	
	// make sure we have a node
	if (this._rootNode)
	{
		// make sure we're using our styles
		this._rootNode.className = "webterm";
		
		// create pre element and add to root
		this._termNode = document.createElement("pre");
		this._rootNode.appendChild(this._termNode);
		
		// clamp width and height
		this._width = (isNumber(width))
			?	clamp(width, Line.MIN_WIDTH, Line.MAX_WIDTH)
			:	Line.DEFAULT_WIDTH;
		
		this._height = (isNumber(height))
			?	clamp(height, Term.MIN_HEIGHT, Term.MAX_HEIGHT)
			:	Term.DEFAULT_HEIGHT;
		
		this._title = "Aptana WebTerm";
		this._row = 0;
		this._column = 0;
		this._scrollRegion = {
			top: 0,
			left: 0,
			bottom: this._height - 1,
			right : this._width - 1
		};
		this._cursorVisible = true;
		this._buffers = [];
		this._positions = [];
		this._currentAttribute = new Attribute();
		this._sendResizeSequence = (config && config.hasOwnProperty("sendResizeSequence")) ? config.sendResizeSequence : true;
		this._showTitle = (config && config.hasOwnProperty("showTitle")) ? config.showTitle : true;
		this._onTitleChange = (config && config.hasOwnProperty("onTitleChange")) ? config.onTitleChange : null;
		
		// create selection-related properties
		this._useNativeCopy = (config && config.hasOwnProperty("useNativeCopy") ? config.useNativeCopy : true);
		
		if (this._useNativeCopy === false)
		{
			this._hasSelection = false;
			this._lastStartingOffset = null;
			this._lastEndingOffset = null;
			
			// add selection support
			var self = this;
			
			dragger(
				this._rootNode,
				function(sx, sy, ex, ey)
				{
					self.updateSelection(sx, sy, ex, ey);
				}
			);			
			
			this._fontInfo = new FontInfo("fontInfo");
		}
		else
		{
			// TODO: We're forcing state within FontInfo by giving it an invalid
			// element id. There should be a cleaner way. Changes here will
			// affect Term.sizeToWindow
			this._fontInfo = new FontInfo("");
		}
		
		// create parser, handlers, etc. either from config or as needed
		var handler     = (config && config.hasOwnProperty("handler"))     ? config.handler     : new XTermHandler(this);
		var tables      = (config && config.hasOwnProperty("tables"))      ? config.tables      : XTermTables;
		var parser      = (config && config.hasOwnProperty("parser"))      ? config.parser      : new TermParser(tables, handler);
		var keyHandler  = (config && config.hasOwnProperty("keyHandler"))  ? config.keyHandler  : new KeyHandler();
		this._parser = parser;
		this._keyHandler = keyHandler;
		
		// NOTE: TermComm grabs the terminal's KeyHandler, so we need to process
		// the comm object after we've setup the key handler 
		var commHandler = (config && config.hasOwnProperty("commHandler")) ? config.commHandler : new TermComm(this, config);
		var autoStart   = (config && config.hasOwnProperty("autoStart"))   ? config.autoStart   : true;
		this._commHandler = commHandler;
		
		// create buffer
		this.createBuffer();	// defines this._lines -> Array<Line>
		
		// draw what we have
		this.refresh();
		
		// possibly start polling
		if (autoStart)
		{
			this.toggleRunState();
		}
	}
	else
	{
		// we have to have somewhere to put the terminal
		throw new Error("Unable to create a new Term because there is no element named '" + id + "'");
	}
}

/**
 * Replace all characters in this terminal with spaces or with the specified
 * character
 * 
 * @alias Term.prototype.clear
 * @param {String} [ch]
 * 		If this optional parameter is specified, the first character of the
 * 		string value will be used as the replacement character when filling the
 * 		terminal.
 */
Term.prototype.clear = function(ch)
{
	for (var i = 0; i < this._lines.length; i++)
	{
		this._lines[i].clear(ch);
	}
	
	this._row = 0;
	this._column = 0;
};

/**
 * Clear all characters starting from the current position to the end of the
 * terminal.
 * 
 * @alias Term.prototype.clearAfter
 */
Term.prototype.clearAfter = function()
{
	this._lines[this._row].clearRight(this._column);
	
	for (var i = this._row + 1; i < this._lines.length; i++)
	{
		this._lines[i].clear();
	}
};

/**
 * Clear all characters starting from the top-left of terminal up to and
 * including the current position in the terminal.
 * 
 * @alias Term.prototype.clearBefore
 */
Term.prototype.clearBefore = function()
{
	this._lines[this._row].clearLeft(this._column);
	
	for (var i = this._row - 1; i >= 0; i--)
	{
		this._lines[i].clear();
	}
};

/**
 * clearCharacterSizes
 */
Term.prototype.clearCharacterSizes = function()
{
	this._characterSizes = {};
};

/**
 * Clear all characters starting from the beginning of the current line up to
 * and including the current offset.
 * 
 * @alias Term.prototype.clearLeft
 */
Term.prototype.clearLeft = function()
{
	this._lines[this._row].clearLeft(this._column);
};

/**
 * Clear all characters in the current line
 * 
 * @alias Term.prototype.clearLine
 */
Term.prototype.clearLine = function()
{
	this._lines[this._row].clear();
};

/**
 * Clear all characters starting from the current offset in the current line to
 * the end of the line.
 * 
 * @alias Term.prototype.clearRight
 */
Term.prototype.clearRight = function()
{
	this._lines[this._row].clearRight(this._column);
};

/**
 * Creates a new buffer matching the current screen size. Note that this will
 * overwrite the current buffer. If you need to save and later restore the
 * current buffer, then use pushBuffer/popBuffer instead.
 * 
 * @alias Term.prototype.createBuffer
 */
Term.prototype.createBuffer = function()
{
	// create a new buffer
	var lines = new Array(this._height);
	
	for (var i = 0; i < lines.length; i++)
	{
		lines[i] = this.createLine();
	}
	
	// and activate it
	this._lines = lines;
};

/**
 * @private
 */
Term.prototype.createLine = function()
{
	return new Line(this._width, this._fontInfo);
};

/**
 * Clear all selection attributes in the current display
 *
 * @alias {Term.prototype.clearSelection}
 * @param {Boolean} [refresh]
 */
Term.prototype.clearSelection = function(refresh)
{
	var lines = this._lines;
	var length = lines.length;
	
	// clear all lines of any selections they have
	for (var i = 0; i < length; i++)
	{
		lines[i].clearSelection();
	}	
	
	// reset select flag
	this._hasSelection = false;
	
	// clear selection offset cache
	this._lastStartingOffset = null;
	this._lastEndingOffset = null;

	// refresh as necessary
	if ((isBoolean(refresh)) ? refresh : true)
	{
		this.refresh();
	}
};

/**
 * Remove one or more characters from the current position.
 * 
 * @alias Term.prototype.deleteCharacter
 * @param {Number} [count]
 * 		An optional number of characters to delete. When this parameter is not
 * 		specified, a default value of 1 will be used
 */
Term.prototype.deleteCharacter = function(count)
{
	this._lines[this._row].deleteCharacter(this._column, count);
};

/**
 * Remove the entire line at the current position. Any lines below this line
 * will be shifted up. New lines will be created at the bottom of the terminal
 * which will be filled with spaces and will use default styling.
 * 
 * @alias Term.prototype.deleteLine
 * @param {Number} [count]
 * 		An optional number of lines to delete from and including the current
 * 		line. If this parameter is not specified, a default value of 1 will be
 * 		used.
 */
Term.prototype.deleteLine = function(count)
{
	// default to 1, if needed
	count = (count === undefined) ? 1 : count;
	
	if (count > 0) 
	{
		var scrollRegion = this._scrollRegion;
		
		if (scrollRegion.left == 0 && scrollRegion.right == this._width - 1) 
		{
			// clamp count, if needed
			if (this._row + count > scrollRegion.bottom)
			{
				count = scrollRegion.bottom - this._row + 1;
			}
			
			if (count == this._height)
			{
				this.clear();
			}
			else
			{
				// remove current line
				var lines = this._lines.splice(this._row, count);
				
				// reset lines
				for (var i = 0; i < count; i++) 
				{
					lines[i].clear();
				}
				
				if (scrollRegion.bottom + 1 == this.height) 
				{
					this._lines = this._lines.concat(lines);
				}
				else 
				{
					for (var i = 0; i < count; i++) 
					{
						// put it back onto the bottom of the scroll region
						this._lines.splice(scrollRegion.bottom - count + i + 1, 0, lines[i]);
					}
				}
			}
		}
		else 
		{
			// scroll sub-regions
		}
	}
};

/**
 * @private
 */
Term.prototype.deselect = function(s, e)
{
	var width = this.getWidth();
	var height = this.getHeight();
	var range = new Range(s, e).clamp(new Range(0, width*height)); // clamp range to viewable area
	
	// process if we have a non-empty range
	if (range.isEmpty() === false)
	{
		var startingLine = Math.floor(range.startingOffset / width);
		var endingLine = Math.ceil(range.endingOffset / width);
		var startingOffset = startingLine * width;
		
		// process all lines for possible deselection
		for (var i = startingLine; i <= endingLine; i++)
		{
			var endingOffset = startingOffset + width;
			var lineRange = new Range(startingOffset, endingOffset).clamp(range);
			
			if (lineRange.isEmpty() === false)
			{
				var normalizedRange = lineRange.move(-startingOffset);
				var line = this._lines[i];
				
				line.deselect(normalizedRange);
			}
			
			startingOffset = endingOffset;
		}
	}
};

/**
 * Print all characters in the specified string to the screen at the current
 * position. It is assumed that the string contains printable text only. All
 * characters outside of the closed interval [\x20-\x7F] will be displayed as
 * spaces.
 * 
 * If there are more characters than will fit in the current line, then content
 * that would overflow the right-most column will be printed in the last column.
 * 
 * @alias Term.prototype.displayCharacters
 * @param {String} chars
 */
Term.prototype.displayCharacters = function(chars)
{
	if (isString(chars))
	{
		for (var i = 0; i < chars.length; i++)
		{
			var ch = chars.charAt(i);
		
			// replace non-printable characters with a space	
			if (/[\x00-\x1F]+/.test(ch))
			{
				ch = ' ';
			}
			
			// NOTE: not sure if we should wrap to next line or leave at last
			// column.
			if (this._column >= this._width)
			{
				// back up to the last column
				this._column = 0;
				
				// possibly scroll
				this.getParser().getHandler().SU("SU", 1);
			}
			
			// put character
			var line = this._lines[this._row];
			
			line.putCharacter(ch, this._currentAttribute, this._column);
			
			// advance to next column
			this._column++;
		}
	}
};

/**
 * Get the flag indicating if application keys are on or off.
 * 
 * @alias Term.prototype.getApplicationKeys
 */
Term.prototype.getApplicationKeys = function()
{
	return this._keyHandler.getApplicationKeys();
};

/**
 * Return the column of the current position. All column values are zero-based.
 * 
 * @alias Term.prototype.getColumn
 * @return {Number}
 */
Term.prototype.getColumn = function()
{
	return this._column;
};

/**
 * Return the object responsible for communicating with the server
 * 
 * @alias {Term.prototype.getCommunicationHandler}
 * @return {TermComm}
 */
Term.prototype.getCommunicationHandler = function()
{
	return this._commHandler;
};

/**
 * Return a copy of the current attribute. A copy is made so that its properties
 * can be altered and then used as a new attribute without changing all places
 * the attribute is already being used.
 * 
 * @alias Term.prototype.getCurrentAttribute
 * @return {Attribute}
 */
Term.prototype.getCurrentAttribute = function()
{
	// NOTE: Since it is most likely we are grabbing the current attribute to
	// change it, we go ahead and make a copy so changes to the attribute won't
	// end up changing all the locations where it is already used.
	return this._currentAttribute.copy();
};

/**
 * Return the font info object associated with this terminal
 *
 * @return {FontInfo}
 */
Term.prototype.getFontInfo = function()
{
	return this._fontInfo;
};

/**
 * Return the height of the terminal
 * 
 * @alias Term.prototype.getHeight
 * @return {Number}
 */
Term.prototype.getHeight = function()
{
	return this._height;
};

/**
 * Return the unique id for this terminal instance
 * 
 * @return {String}
 */
Term.prototype.getId = function()
{
	return this._id;
};

/**
 * Return the key handler being used by this terminal
 * 
 * @alias Term.prototype.getKeyHandler
 * @return {KeyHandler}
 */
Term.prototype.getKeyHandler = function()
{
	return this._keyHandler;
};

/**
 * Return the parse being used by this terminal
 * 
 * @alias Term.prototype.getParser
 * @return {TermParser}
 */
Term.prototype.getParser = function()
{
	return this._parser;
};

/**
 * Return the row of the current position. All row values are zero-based.
 * 
 * @alias Term.prototype.getRow
 * @return {Number}
 */
Term.prototype.getRow = function()
{
	return this._row;
};

/**
 * Return a protected clone of the current scroll region. A scroll area defines
 * the rectangular region within the terminal that will scroll when scroll
 * commands are initiated on this terminal.
 * 
 * It is safe for code to change values on the returned object as it will not
 * affect the one being used by the terminal itself
 * 
 * @alias Term.prototype.getScrollRegion
 * @return {Object}
 * 		"top" - the top-most row of the scroll area
 * 		"left" - The left-most column of the scroll area
 * 		"bottom" - The bottom-most row of the scroll area
 * 		"right" - The right-most column of the scroll area
 */
Term.prototype.getScrollRegion = function()
{
	// NOTE: Not sure if this is overly paranoid, but we return a protected
	// clone so any changes done to the return value will not affect our local
	// scrollRegion object
	return protectedClone(this._scrollRegion);
};

/**
 * getSelectedText
 *
 * @alias {Term.prototype.getSelectedText}
 * @return {String}
 */
Term.prototype.getSelectedText = function()
{
	var lineEnding = (BrowserDetect.OS == "Windows") ? "\r\n" : "\n";
	var result = null;
	
	if (this._useNativeCopy === false)
	{
		if (this.hasSelection())
		{
			var lines = this._lines;
			var length = lines.length;
			
			for (var i = 0; i < length; i++)
			{
				var lineResult = lines[i].getSelectedText();
				
				if (lineResult !== null)
				{
					if (result === null)
					{
						result = [];
					}		
					
					result.push(lineResult);
				}
			}
		}
	}	
	else
	{
		if (window.getSelection)
		{
			// safari && ff
			result = window.getSelection().toString();
			
			// convert into a list of lines
			result = result.split(/\r\n|\r|\n/);
		}		
		else if (document.selection)
		{
			// ie
			result = document.selection.createRange().text;
			
			// convert into a list of lines
			var index = 0;
			var lines = [];
			
			for (var i = 0; i < this._height && index < result.length; i++, index += this._width)
			{
				lines.push(result.substring(index, index + this._width));
			}			
			
			result = lines;
		}		
		
		// remove trailing whitespace on each line
		if (result !== null)
		{
			for (var i = 0; i < result.length; i++)
			{
				result[i] = result[i].replace(/\s+$/, "");
			}
		}
	}
	
	return (result !== null) ? result.join(lineEnding) : null;
};

/**
 * Return the current title of the terminal
 * 
 * @alias Term.prototype.getTitle
 * @return {String}
 */
Term.prototype.getTitle = function()
{
	return this._title;
};

/**
 * Return the width of the terminal
 * 
 * @alias Term.prototype.getWidth
 * @return {Number}
 */
Term.prototype.getWidth = function()
{
	return this._width;
};

/**
 * hasSelection
 */
Term.prototype.hasSelection = function()
{
	return this._hasSelection;
};

/**
 * Insert a character one or more times into the current position in the
 * terminal. All characters shifted off of the end of the current line are lost.
 * All new characters will use default styling.
 * 
 * @alias Term.prototype.insertCharacter
 * @param {String} ch
 * 		The character to insert at the current position. Only the first
 * 		character in the string will be inserted
 * @param {Number} [count]
 * 		An optional number of times to insert the specified character. If this
 * 		parameter does not exist, a default value of 1 will be used.
 */
Term.prototype.insertCharacter = function(ch, count)
{
	this._lines[this._row].insertCharacter(ch, this._column, count);
};

/**
 * Insert a new line at the current position. Any lines below this line will be
 * shifted down. New lines will be created at the current line in the terminal.
 * Each new line will be filled with spaces and will use default styling.
 * 
 * @alias Term.prototype.insertLine
 * @param {Number} [count]
 * 		An optional number of lines to insert at the current line. If this
 * 		parameter is not specified, a default value of 1 will be used.
 */
Term.prototype.insertLine = function(count)
{
	// default to 1, if needed
	count = (count === undefined) ? 1 : count;
	
	if (count > 0) 
	{
		var scrollRegion = this._scrollRegion;
		
		if (scrollRegion.left == 0 && scrollRegion.right == this._width - 1) 
		{
			// clamp count, if needed
			if (this._row + count > scrollRegion.bottom)
			{
				count = scrollRegion.bottom - this._row + 1;
			}
			
			if (count == this._height)
			{
				this.clear();
			}
			else
			{
				// remove bottom lines
				var lines = this._lines.splice(scrollRegion.bottom - count + 1, count);
				
				// reset lines
				for (var i = 0; i < count; i++) 
				{
					lines[i].clear();
				}
				
				// put lines back onto the top of the scroll region
				if (this._row == 0) 
				{
					this._lines = lines.concat(this._lines);
				}
				else 
				{
					for (var i = 0; i < count; i++) 
					{
						this._lines.splice(this._row + i, 0, lines[i]);
					}
				}
			}
		}
		else 
		{
			// scroll sub-regions
		}
	}
};

/**
 * Restore the last pushed buffer, if any. If there are no buffers to restore,
 * then this method does nothing.
 * 
 * @alias Term.prototype.popBuffer
 */
Term.prototype.popBuffer = function()
{
	if (this._buffers.length > 0)
	{
		this._lines = this._buffers.pop();
		
		// remember our current height and width
		var width = this.getWidth(),
			height = this.getHeight();
		
		// set width and height to the restored buffer size
		this._width = this._lines[0].getWidth();
		this._height = this._lines.length;
		
		// potentially resize the restored buffer to the correct size
		this.setSize(width, height);
	}
};

/**
 * Restore the last saved position. If no positions have been saved, then this
 * method does nothing
 * 
 * @alias Term.prototype.popPosition
 */
Term.prototype.popPosition = function()
{
	if (this._positions.length > 0) 
	{
		var position = this._positions.pop();
		
		this._row = position[0];
		this._column = position[1];
	}
};

/**
 * Process each of character in the specified string. The string can include
 * control sequences which will be parsed by the terminal's parser. In turn, the
 * parser will fire events on the handler, which finally will invoke methods on
 * this terminal.
 * 
 * @alias Term.prototype.processCharacters
 * @param {String} text
 */
Term.prototype.processCharacters = function(text)
{
	if (isString(text) && text.length > 0)
	{
		// process incoming text
		this._parser.parse(text);
		
		// clear selection (also redraws the screen)
		this.clearSelection();
	}
};

/**
 * Save all of the lines in the current buffer and create a new empty buffer.
 * The new buffer takes over the screen and becomes the active display area. The
 * original buffer can be restored by calling popBuffer
 * 
 * @alias Term.prototype.pushBuffer
 */
Term.prototype.pushBuffer = function()
{
	// push current buffer
	this._buffers.push(this._lines);
	
	// create a new one
	this.createBuffer();
};

/**
 * Save the current position. The position can be restore later by calling
 * popPosition.
 * 
 * @alias Term.prototype.pushPosition
 */
Term.prototype.pushPosition = function()
{
	this._positions.push([this._row, this._column]);
};

/**
 * Apply the content of the current buffer to the screen. This will be called
 * automatically by processCharacters, so it should not need to be invoked
 * unless the terminal is being manipulated outside of that method.
 * 
 * @alias Term.prototype.refresh
 */
Term.prototype.refresh = function()
{
	var buffer = [];
	var attr = null;
	
	// emit title
	var extendedTitle = this._title + " — " + this._width + "x" + this._height; 
	var title = "<div class='title'>" + extendedTitle + "</div>";
	
	// emit console
	for (var row = 0; row < this._height; row++)
	{
		var line = this._lines[row];
		var cursorOffset = (this._cursorVisible) ? (row == this._row) ? this._column : -1 : -1;
		var htmlInfo = line.getHTMLInfo(attr, cursorOffset);
		
		attr = htmlInfo.attribute;
		
		buffer.push(htmlInfo.html);
	}
	
	// turn off style, if any is in use
	if (attr != null)
	{
		buffer[buffer.length - 1] += attr.getEndingHTML();
	}
	
	if (this._showTitle)
	{
		this._termNode.innerHTML = title + buffer.join("<br />");
	}
	else
	{
		this._termNode.innerHTML = buffer.join("<br />");
	}
};

/**
 * Shift the scroll region down one line. All lines shifted off of the bottom
 * will be lost. A new line will be created at the top of the scroll region
 * which will be filled with spaces using the default style.
 * 
 * @alias Term.prototype.scrollDown
 * @param {Number} [count]
 * 		An optional number of lines to scroll. If this parameter is not defined,
 * 		the a default value of 1 will be used.
 */
Term.prototype.scrollDown = function(count)
{
	// default to 1, if needed
	count = (count === undefined) ? 1 : count;
	
	if (count > 0) 
	{
		var scrollRegion = this._scrollRegion;
		
		if (scrollRegion.left == 0 && scrollRegion.right == this._width - 1) 
		{
			var lineCount = scrollRegion.bottom - scrollRegion.top + 1;
			
			// clamp count, if needed
			if (count >= lineCount)
			{
				this.clear();
			}
			else
			{
				// remove bottom lines
				var lines = this._lines.splice(scrollRegion.bottom - count + 1, count);
				
				// reset lines
				for (var i = 0; i < count; i++) 
				{
					lines[i].clear();
				}
				
				// put lines back onto the top of the scroll region
				if (scrollRegion.top == 0) 
				{
					this._lines = lines.concat(this._lines);
				}
				else 
				{
					for (var i = 0; i < count; i++) 
					{
						this._lines.splice(scrollRegion.top + i, 0, lines[i]);
					}
				}
			}
		}
		else 
		{
			// scroll sub-regions
		}
	}
};

/**
 * Shift the scroll region up one line. All lines shifted off of the top will be
 * lost. A new line will be created at the bottom of the scroll region which
 * will be filled with spaces using the default style.
 * 
 * @alias Term.prototype.scrollUp
 * @param {Number} [count]
 * 		An optional number of lines to scroll. If this parameter is not defined,
 * 		the a default value of 1 will be used.
 */
Term.prototype.scrollUp = function(count)
{
	// default to 1, if needed
	count = (count === undefined) ? 1 : count;
	
	if (count > 0) 
	{
		var scrollRegion = this._scrollRegion;
		
		if (scrollRegion.left == 0 && scrollRegion.right == this._width - 1) 
		{
			var lineCount = scrollRegion.bottom - scrollRegion.top + 1;
			
			// clamp count, if needed
			if (count >= lineCount)
			{
				this.clear();
			}
			else
			{
				// remove current line
				var lines = this._lines.splice(scrollRegion.top, count);
				
				// reset lines
				for (var i = 0; i < count; i++) 
				{
					lines[i].clear();
				}
				
				if (scrollRegion.bottom + 1 == this.height) 
				{
					this._lines = this._lines.concat(lines);
				}
				else 
				{
					for (var i = 0; i < count; i++) 
					{
						// put it back onto the bottom of the scroll region
						this._lines.splice(scrollRegion.bottom - count + i + 1, 0, lines[i]);
					}
				}
			}
		}
		else 
		{
			// scroll sub-regions
		}
	}
};

/**
 * Set the current selection
 *
 * @alias {Term.prototype.select}
 * @param {Number} start
 * @param {Number} end
 * @param {Boolean} [preserveSelection]
 */
Term.prototype.select = function(s, e, preserveSelection)
{
	var width = this.getWidth();
	var height = this.getHeight();
	var range = new Range(s, e).clamp(new Range(0, width*height)); // clamp range to viewable area
	var hadSelection = this.hasSelection() && !preserveSelection;
	var selected = false;
	
	// clear out any previous selection we might have had
	if (hadSelection)
	{
		this.clearSelection(false);
	}	
	
	// process if we have a non-empty range
	if (range.isEmpty() === false)
	{
		var startingLine = Math.floor(range.startingOffset / width);
		var endingLine = Math.ceil(range.endingOffset / width);
		var startingOffset = startingLine * width;
		
		// process all lines for possible selection
		for (var i = startingLine; i <= endingLine; i++)
		{
			var endingOffset = startingOffset + width;
			var lineRange = new Range(startingOffset, endingOffset).clamp(range);
			
			if (lineRange.isEmpty() === false)
			{
				var selectionContinues = range.endingOffset > lineRange.endingOffset;
				var normalizedRange = lineRange.move(-startingOffset);
				var line = this._lines[i];
				
				if (line.select(normalizedRange, selectionContinues))
				{
					selected = true;
				}
			}
			
			startingOffset = endingOffset;
		}
	}
	
	// save selection state for future queries
	this._hasSelection = selected;
	
	// refresh display, if necessary
	if (hadSelection || selected)
	{
		this.refresh();
	}
};

/**
 * Select all of the visible text in the Term's display
 */
Term.prototype.selectAll = function()
{
	if (this._useNativeCopy === false)
	{
		this.select(0, this._width * this._height);
	}
	else
	{
		var node = this._termNode;
		var allText = (node.innerText) ? node.innerText : node.textContent;
		var startingOffset = 0;
		var endingOffset = allText.length;
		
		if (window.getSelection && document.createRange)
		{
			// ff & safari
			var selection = window.getSelection();
			var range = document.createRange();
			
			range.selectNodeContents(node);
			selection.removeAllRanges();
			selection.addRange(range);
		}
		else if (document.selection && document.selection.createRange)
		{
			alert("document.selection.createRange");
			
			var range = document.selection.createRange();
			
			range.moveToElementText(node);
			range.select();
		}
		else
		{
			alert("Didn't see a way to set the selection natively");
		}
	}
};

/**
 * Set a flag indicating if application keys should be on or off.
 * 
 * @alias Term.prototype.setApplicationKeys
 * @param {Boolean} value
 * 		The new application keys value. Non-boolean values are ignored
 */
Term.prototype.setApplicationKeys = function(value)
{
	if (isBoolean(value))
	{
		this._keyHandler.setApplicationKeys(value);
	}
};

/**
 * Set the column of the current position. All column values are zero-based.
 * Non-number values and values outside of the range of the terminal width will
 * be ignored
 * 
 * @alias Term.prototype.setColumn
 * @param {Number} column
 */
Term.prototype.setColumn = function(column)
{
	if (isNumber(column) && 0 <= column && column < this._width)
	{
		this._column = column;
	}
};

/**
 * Set the current attribute to be in affect as new characters are placed onto
 * the terminal via displayCharacters. Non-attribute instances will be ignored
 * 
 * @alias Term.prototype.setCurrentAttribute
 * @param {Attribute} attr
 */
Term.prototype.setCurrentAttribute = function(attr)
{
	if (isDefined(attr) && attr.constructor === Attribute)
	{
		this._currentAttribute = attr;
	}
};

/**
 * Set a flag indicating whether the cursor should be visible or not.
 * Non-boolean values have no affect on this setting.
 * 
 * @alias Term.prototype.setCursorVisible
 * @param {Boolean} value
 */
Term.prototype.setCursorVisible = function(value)
{
	if (isBoolean(value))
	{
		this._cursorVisible = value;
	}
};

/**
 * Set the height of the terminal. If the new height is smaller than the current
 * height, then lines will be removed from the bottom of the terminal until the
 * new size is met. If the new height is larger than the current height, new
 * lines will be added to bottom of the terminal each containing spaces and
 * using default styling. Non-number values, values outside the range of
 * Term.MIN_HEIGHT and term.MAX_HEIGHT, and values matching the current height
 * will be ignored.
 * 
 * @alias Term.prototype.setHeight
 * @param {Number} height
 */
Term.prototype.setHeight = function(height)
{
	this.setSize(this._width, height);
};

/**
 * Set the current position within the terminal. Non-numeric values and values
 * outside the range of the terminal will be ignored
 * 
 * @alias Term.prototype.setPosition
 * @param {Number} row
 * @param {Number} column
 */
Term.prototype.setPosition = function(row, column)
{
	if (isNumber(row) && 0 <= row && row < this._height)
	{
		this._row = row;
	}
	if (isNumber(column) && 0 <= column && column < this._width)
	{
		this._column = column;
	}
};

/**
 * Set the row of the current position. All row values are zero-based.
 * Non-number values and values outside of the range of the terminal height will
 * be ignored
 * 
 * @alias Term.prototype.setRow
 * @param {Number} row
 */
Term.prototype.setRow = function(row)
{
	if (0 <= row && row < this._height)
	{
		this._row = row;
	}
};

/**
 * Set the scroll region for the terminal. In order for the scroll region to be
 * valid, all parameters must be numbers, top must be less than bottom, and left
 * must be less than right. See getScrollRegion for more details on what these
 * properties mean.
 * 
 * @alias Term.prototype.setScrollRegion
 * @param {Number} top
 * @param {Number} left
 * @param {Number} bottom
 * @param {Number} right
 */
Term.prototype.setScrollRegion = function(top, left, bottom, right)
{
	if (isNumber(top) && isNumber(left) && isNumber(bottom) && isNumber(right))
	{
		if (top < bottom && left < right)
		{
			var topIsValid = (0 <= top && top < this._height);
			var leftIsValid = (0 <= left && left < this._width);
			var bottomIsValid = (0 <= bottom && bottom < this._height);
			var rightIsValid = (0 <= right && right < this._width);
			
			if (topIsValid && leftIsValid && bottomIsValid && rightIsValid) 
			{
				this._scrollRegion = {
					top: top,
					left: left,
					bottom: bottom,
					right: right
				};
			}
		}
	}
};

/**
 * setSize
 * 
 * @param {Number} width
 * @param {Number} height
 */
Term.prototype.setSize = function(width, height)
{
	var changed = false;
	
	if (isNumber(width) && Line.MIN_WIDTH <= width && width <= Line.MAX_WIDTH && this._width != width)
	{
		for (var i = 0; i < this._height; i++)
		{
			this._lines[i].resize(width);
		}
		
		this._width = width;
		this._column = Math.min(this._width - 1, this._column);
		
		changed = true;
	}
	
	if (isNumber(height) && Term.MIN_HEIGHT <= height && height <= Term.MAX_HEIGHT && this._height != height)
	{
		if (height > this._height)
		{
			for (var i = this._height; i < height; i++)
			{
				this._lines.push(this.createLine());
			}
		}
		else
		{
			this._lines = this._lines.splice(this._height - height, height);
		}
		
		this._height = height;
		this._row = Math.min(this._height - 1, this._row);
		
		changed = true;
	}
	
	if (changed)
	{
		// NOTE: not sure if we should reset scroll region on resizes, but this resolves issues during auto-resizing
		
		this.setScrollRegion(0, 0, this._height - 1, this._width - 1);
		
		// possibly send resize escape sequence
		if (this._sendResizeSequence)
		{
			var ESC = String.fromCharCode(0x1B);
			var CSI = ESC + "[";
			
			this._keyHandler.addKeys(CSI + [8,this._height,this._width].join(";") + "t");
		}
		
		// refresh
		this.refresh();
	}
};

/**
 * Set the title that is displayed for this terminal
 * 
 * @alias Term.prototype.setTitle
 * @param {String} title
 */
Term.prototype.setTitle = function(title)
{
	this._title = title;
	
	if (isFunction(this._onTitleChange))
	{
		this._onTitleChange(title);
	}
};

/**
 * showTitle
 * 
 * @param {Boolean} value
 */
Term.prototype.showTitle = function(value)
{
	if (isBoolean(value))
	{
		this._showTitle = value;
		this.refresh();
	}
};

/**
 * Resize this terminal to take up as much space in the current window as
 * possible. This will take the current font into account when determining the
 * number of characters to use for the term's width and height properties.
 */
Term.prototype.sizeToWindow = function()
{
	// try to calculate character size once only
	if (this._useNativeCopy && this._fontInfo.isMonospaced() === false)
	{
		var div = this._rootNode;
					
		// save current font-weight and then set to bold
		var oldWeight = div.style.fontWeight;
		div.style.fontWeight = "bold";
		
		// get div's width and height
		var divWidth = div.offsetWidth;
		var divHeight = div.offsetHeight;
		
		// restore original font-weight
		div.style.fontWeight = oldWeight;
		
		// calculate average width/height of a character
		var width = divWidth / this.getWidth();
		var height = divHeight / this.getHeight();
		
		this._fontInfo.forceSize(width, height);
	}	
	
	// get dimensions of the letter 'M'
	var m = this._fontInfo.getCharacterSize("M");
	var characterWidth = m[0];
	var characterHeight = m[1];
	
	// calculate new height and width
	var width = Math.floor(getWindowWidth() / characterWidth) - 1;
	var height = Math.floor(getWindowHeight() / characterHeight);
	
	// apply new settings
	this.setSize(width, height);
};

/**
 * Start or stop communication between this terminal and the its server
 */
Term.prototype.toggleRunState = function()
{
	if (this._commHandler !== null)
	{
		if (this._id === null && this._commHandler.isRunning() == false)
		{
			this._id = this._commHandler.getUniqueID();
		}
		
		this._commHandler.toggleRunState();
	}
};

/**
 * Set the width of the terminal. If the new width is smaller than the current
 * width, then columns will be removed from each line in the terminal until the
 * new size is met. If the new width is larger than the current width, new
 * columns will be added to each line in the terminal each containing spaces and
 * using default styling. Non-number values, values outside the range of
 * Line.MIN_WIDTH and Line.MAX_WIDTH, and values matching the current width will
 * be ignored.
 * 
 * @alias Term.prototype.setWidth
 * @param {Number} width
 */
Term.prototype.setWidth = function(width)
{
	this.setSize(width, this._height);
};

/**
 * Return the content of the terminal as a string. Each line will be delimited
 * by a newline, except for the last line
 * 
 * @alias Term.prototype.toString
 * @return {String}
 */
Term.prototype.toString = function()
{
	var buffer = [];
	
	for (var i = 0; i < this._lines.length; i++)
	{
		buffer.push(this._lines[i].toString());
	}
	
	return buffer.join("\n");
};

/**
 * @private
 * @param {Number} startX
 * @param {Number} startY
 * @param {Number} endX
 * @param {Number} endY
 */
Term.prototype.updateSelection = function(startX, startY, endX, endY)
{
	if (isNumber(startX) && isNumber(startY) && isNumber(endX) && isNumber(endY))
	{
		var lines = this._lines;
		// The following is an optimzation that assumes the character height
		// is constant for all characters in the current font
		var lineHeight = this._fontInfo.getCharacterHeight("M");
		var getLine = function(y)
		{
			var length = lines.length;
			var startingHeight = 0;
			var result = null;
			
			for (var i = 0; i < length; i++)
			{
				var line = lines[i];
				var endingHeight = startingHeight + lineHeight;
				
				if (startingHeight <= y && y < endingHeight)
				{
					result = i;
					break;
				}
				else
				{
					startingHeight = endingHeight;
				}
			}
			
			return result;
		}
		
		var startLine = getLine(startY);
		var endLine = getLine(endY);
		
		if (startLine !== null && endLine !== null)
		{
			var startColumn = lines[startLine].getOffsetFromPosition(startX);
			var endColumn = lines[endLine].getOffsetFromPosition(endX)
			
			var startingOffset = startLine * this.getWidth() + startColumn;
			var endingOffset = endLine * this.getWidth() + endColumn;
			
			
			// only update selection if it has changed
			if (this._lastStartingOffset !== startingOffset || this._lastEndingOffset !== endingOffset)
			{
				this.select(startingOffset, endingOffset);
				
				this._lastStartingOffset = startingOffset;
				this._lastEndingOffset = endingOffset;
			}
		}	
	}
};
