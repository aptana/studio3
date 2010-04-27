Elements = {};

Elements.Builder = {};
 (function generateBuilder() {
    var tags = ("A ABBR ACRONYM ADDRESS APPLET AREA B BASE BASEFONT BDO BIG BLOCKQUOTE BODY " +
    "BR BUTTON CAPTION CENTER CITE CODE COL COLGROUP DD DEL DFN DIR DIV DL DT EM FIELDSET " +
    "FONT FORM FRAME FRAMESET H1 H2 H3 H4 H5 H6 HEAD HR HTML I IFRAME IMG INPUT INS ISINDEX " +
    "KBD LABEL LEGEND LI LINK MAP MENU META NOFRAMES NOSCRIPT OBJECT OL OPTGROUP OPTION P " +
    "PARAM PRE Q S SAMP SCRIPT SELECT SMALL SPAN STRIKE STRONG STYLE SUB SUP TABLE TBODY TD " +
    "TEXTAREA TFOOT TH THEAD TITLE TR TT U UL VAR").split(/\s+/);
    tags.each(function(tag) {
        Elements.Builder[tag.toLowerCase()] = Elements.Builder[tag] = function() {
            var elements = [];
            var attributes = {};

            var addElement = function(arg) {
                if (Object.isString(arg)) {
                    elements.push(document.createTextNode(arg));
                } else if (Object.isElement(arg)) {
                    elements.push(arg);
                }
            };

            $A(arguments).each(function(arg) {
                if (Object.isFunction(arg)) {
                    arg = arg();
                }

                if (Object.isString(arg)) {
                    addElement(arg);
                } else if (Object.isElement(arg)) {
                    addElement(arg);
                } else if (Object.isArray(arg)) {
                    for (var i = 0; i < arg.length; i++) {
                        addElement(arg[i]);
                    }
                } else {
                    attributes = arg;
                }
            });

            var el = new Element(tag, attributes);
            elements.each(function(child) {
                el.appendChild(child);
            })
            return el;
        }
    })
})();
