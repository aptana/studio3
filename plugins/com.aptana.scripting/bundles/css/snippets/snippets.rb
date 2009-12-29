require 'radrails'

with_defaults :scope => "source.css" do
  
  snippet "!important CSS" do |s|
    s.trigger = "!"
    s.expansion = "${1:!important}"
  end
  
  snippet "background-attachment: scroll/fixed" do |s|
    s.trigger = "background"
    s.expansion = "background-attachment: ${1:scroll/fixed};$0"
  end
  
  snippet "background-color: hex" do |s|
    s.trigger = "background"
    s.expansion = "background-color: \#${1:DDD};$0"
  end
  
  snippet "background-color: name" do |s|
    s.trigger = "background"
    s.expansion = "background-color: ${1:red};$0"
  end
  
  snippet "background-color: rgb" do |s|
    s.trigger = "background"
    s.expansion = "background-color: rgb(${1:255},${2:255},${3:255});$0"
  end
  
  snippet "background-color: transparent" do |s|
    s.trigger = "background"
    s.expansion = "background-color: transparent;$0"
  end
  
  snippet "background-image: none" do |s|
    s.trigger = "background"
    s.expansion = "background-image: none;$0"
  end
  
  snippet "background-image: url" do |s|
    s.trigger = "background"
    s.expansion = "background-image: url($1);$0"
  end
  
  snippet "background-position: position" do |s|
    s.trigger = "background"
    s.expansion = "background-position: ${1:top left/top center/top right/center left/center center/center right/bottom left/bottom center/bottom right/x-% y-%/x-pos y-pos};$0"
  end
  
  snippet "background-repeat: r/r-x/r-y/n-r" do |s|
    s.trigger = "background"
    s.expansion = "background-repeat: ${1:repeat/repeat-x/repeat-y/no-repeat};$0"
  end
  
  snippet "background: color image repeat attachment position" do |s|
    s.trigger = "background"
    s.expansion = "background:${6: \#${1:DDD}} url($2) ${3:repeat/repeat-x/repeat-y/no-repeat} ${4:scroll/fixed} ${5:top left/top center/top right/center left/center center/center right/bottom left/bottom center/bottom right/x-% y-%/x-pos y-pos};$0"
  end
  
  snippet "border-bottom-color: color" do |s|
    s.trigger = "border"
    s.expansion = "border-bottom-color: \#${1:999};$0"
  end
  
  snippet "border-bottom-style: style" do |s|
    s.trigger = "border"
    s.expansion = "border-bottom-style: ${1:none/hidden/dotted/dashed/solid/double/groove/ridge/inset/outset};$0"
  end

  snippet "border-bottom-width: size" do |s|
    s.trigger = "border"
    s.expansion = 'border-bottom-width: ${1:1}px ${2:solid} #${3:999};$0'
  end
  
  snippet "border-bottom: size style color" do |s|
    s.trigger = "border"
    s.expansion = 'border-bottom: ${1:1}px ${2:solid} #${3:999};$0'
  end
  
  snippet "border-color: color" do |s|
    s.trigger = "border"
    s.expansion = 'border-color: ${1:999};$0'
  end
  
  snippet "border-left-color: color" do |s|
    s.trigger = "border"
    s.expansion = 'border-right-color: #${1:999};$0'
  end
  
  snippet "border-left-style: style" do |s|
    s.trigger = "border"
    s.expansion = 'border-left-style: ${1:none/hidden/dotted/dashed/solid/double/groove/ridge/inset/outset};$0'
  end
  
  snippet "border-left-width: size" do |s|
    s.trigger = "border"
    s.expansion = 'border-left-width: ${1:1}px'
  end
  
  snippet "border-left: size style color" do |s|
    s.trigger = "border"
    s.expansion = 'border-left: ${1:1}px ${2:solid} #${3:999};$0'
  end
  
  snippet "border-right-color: color" do |s|
    s.trigger = "border"
    s.expansion = 'border-right-color: #${1:999};$0'
  end
  
  snippet "border-right-style: style" do |s|
    s.trigger = "border"
    s.expansion = 'border-right-style: ${1:none/hidden/dotted/dashed/solid/double/groove/ridge/inset/outset};$0'
  end
  
  snippet "border-right-width: size" do |s|
    s.trigger = "border"
    s.expansion = 'border-right-width: ${1:1}px'
  end
  
  snippet "border-right: size style color" do |s|
    s.trigger = "border"
    s.expansion = 'border-right: ${1:1}px ${2:solid} #${3:999};$0'
  end
  
  snippet "border-style: style" do |s|
    s.trigger = "border"
    s.expansion = 'border-style: ${1:none/hidden/dotted/dashed/solid/double/groove/ridge/inset/outset};$0'
  end
  
  snippet "border-top-color: color" do |s|
    s.trigger = "border"
    s.expansion = 'border-top-color: #${1:999};$0'
  end
  
  snippet "border-top-style: style" do |s|
    s.trigger = "border"
    s.expansion = 'border-top-style: ${1:none/hidden/dotted/dashed/solid/double/groove/ridge/inset/outset};$0'
  end
  
  snippet "border-top-width: size" do |s|
    s.trigger = "border"
    s.expansion = 'border-top-width: ${1:1}px'
  end
  
  snippet "border-top: size style color" do |s|
    s.trigger = "border"
    s.expansion = 'border-top: ${1:1}px ${2:solid} #${3:999};$0'
  end
  
  snippet "border-width: size" do |s|
    s.trigger = "border"
    s.expansion = 'border-width: ${1:1px};$0'
  end
  
  snippet "border: size style color" do |s|
    s.trigger = "border"
    s.expansion = 'border: ${1:1px} ${2:solid} #${3:999};$0'
  end
  
  snippet "clear: value" do |s|
    s.trigger = "clear"
    s.expansion = 'clear: ${1:left/right/both/none};$0'
  end
  
  snippet "color: hex" do |s|
    s.trigger = "color"
    s.expansion = 'color: #${1:DDD};$0'
  end
  
  snippet "color: name" do |s|
    s.trigger = "color"
    s.expansion = 'color: ${1:red};$0'
  end
  
  snippet "color: rgb" do |s|
    s.trigger = "color"
    s.expansion = 'color: rgb(${1:255},${2:255},${3:255});$0'
  end
  
  snippet "cursor: type" do |s|
    s.trigger = "cursor"
    s.expansion = 'cursor: ${1:default/auto/crosshair/pointer/move/*-resize/text/wait/help};$0'
  end
  
  snippet "cursor: url" do |s|
    s.trigger = "cursor"
    s.expansion = 'cursor: url($1);$0'
  end
  
  snippet "direction: ltr/rtl" do |s|
    s.trigger = "direction"
    s.expansion = 'direction: ${1:ltr/rtl};$0'
  end
  
  snippet "display: block" do |s|
    s.trigger = "display"
    s.expansion = 'display: block;$0'
  end
  
  snippet "display: common-types" do |s|
    s.trigger = "display"
    s.expansion = 'display: ${1:none/inline/block/list-item/run-in/compact/marker};$0'
  end
  
  snippet "display: inline" do |s|
    s.trigger = "display"
    s.expansion = 'display: inline;$0'
  end
  
  snippet "display: table-types" do |s|
    s.trigger = "display"
    s.expansion = 'display: ${1:table/inline-table/table-row-group/table-header-group/table-footer-group/table-row/table-column-group/table-column/table-cell/table-caption};$0'
  end
  # FIXME This is broken
  snippet "filter: AlphaImageLoader [for IE PNGs]" do |s|
    s.trigger = "background"
    s.expansion = '${3:background-image: none;
  }filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src=\'${1:${TM_SELECTED_TEXT:/images/transparent.png}}\', sizingMethod=\'${2:image/scale/crop}\');'
  end
  
  snippet "float: left/right/none" do |s|
    s.trigger = "float"
    s.expansion = 'float: ${1:left/right/none};$0'
  end
  # FIXME Tab stop ordering is not used correctly
  snippet "Fixed Position Bottom 100% wide IE6" do |s|
    s.trigger = "fixed"
    s.expansion = '${2:bottom: auto;}
top: expression(eval(document.documentElement.scrollTop+document.documentElement.clientHeight-${1:THE HEIGHT OF THIS THING IN PIXELS}));
${3:left: expression(eval(document.documentElement.scrollLeft));
}${4:width: expression(eval(document.documentElement.clientWidth));}$0'
  end
  
  snippet "font-family: family" do |s|
    s.trigger = "font"
    s.expansion = 'font-family: ${1:Arial, "MS Trebuchet"}, ${2:sans-}serif;$0'
  end
  
  snippet "font-size: size" do |s|
    s.trigger = "font"
    s.expansion = 'font-size: ${1:100%};$0'
  end
  
  snippet "font-style: normal/italic/oblique" do |s|
    s.trigger = "font"
    s.expansion = 'font-style: ${1:normal/italic/oblique};$0'
  end
  
  snippet "font-variant: normal/small-caps" do |s|
    s.trigger = "font"
    s.expansion = 'font-variant: ${1:normal/small-caps};$0'
  end
  
  snippet "font-weight: weight" do |s|
    s.trigger = "font"
    s.expansion = 'font-weight: ${1:normal/bold};$0'
  end
  
  snippet "font: style variant weight size/line-height font-family" do |s|
    s.trigger = "font"
    s.expansion = 'font: ${1:normal/italic/oblique} ${2:normal/small-caps} ${3:normal/bold} ${4:1em/1.5em} ${5:Arial}, ${6:sans-}serif;$0'
  end

  snippet "font: size font" do |s|
    s.trigger = "font"
    s.expansion = 'font: ${1:75%} ${2:"Lucida Grande", "Trebuchet MS", Verdana,} ${3:sans-}serif;$0'
  end
  
  snippet "letter-spacing: em" do |s|
    s.trigger = "letter"
    s.expansion = 'letter-spacing: $1em;$0'
  end
  
  snippet "letter-spacing: px" do |s|
    s.trigger = "letter"
    s.expansion = 'letter-spacing: $1px;$0'
  end
  
  snippet "list-style-image: url" do |s|
    s.trigger = "list"
    s.expansion = 'list-style-image: url($1);$0'
  end
  
  snippet "list-style-position: pos" do |s|
    s.trigger = "list"
    s.expansion = 'list-style-position: ${1:inside/outside};$0'
  end
  
  snippet "list-style-type: asian" do |s|
    s.trigger = "list"
    s.expansion = 'list-style-type: ${1:cjk-ideographic/hiragana/katakana/hiragana-iroha/katakana-iroha};$0'
  end
  
  snippet "list-style-type: marker" do |s|
    s.trigger = "list"
    s.expansion = 'list-style-type: ${1:none/disc/circle/square};$0'
  end
  
  snippet "list-style-type: numeric" do |s|
    s.trigger = "list"
    s.expansion = 'list-style-type: ${1:decimal/decimal-leading-zero/zero};$0'
  end
  
  snippet "list-style-type: other" do |s|
    s.trigger = "list"
    s.expansion = 'list-style-type: ${1:hebrew/armenian/georgian};$0'
  end
  
  snippet "list-style-type: roman-alpha-greek" do |s|
    s.trigger = "list"
    s.expansion = 'list-style-type: ${1:lower-roman/upper-roman/lower-alpha/upper-alpha/lower-greek/lower-latin/upper-latin};$0'
  end
  
  snippet "list-style: type position image" do |s|
    s.trigger = "list"
    s.expansion = 'list-style: ${1:none/disc/circle/square/decimal/zero} ${2:inside/outside} url($3);$0'
  end
  
  snippet "margin-bottom: length" do |s|
    s.trigger = "margin"
    s.expansion = 'margin-bottom: ${1:20px};$0'
  end
  
  snippet "margin-left: length" do |s|
    s.trigger = "margin"
    s.expansion = 'margin-left: ${1:20px};$0'
  end
  
  snippet "margin-right: length" do |s|
    s.trigger = "margin"
    s.expansion = 'margin-right: ${1:20px};$0'
  end
  
  snippet "margin-top: length" do |s|
    s.trigger = "margin"
    s.expansion = 'margin-top: ${1:20px};$0'
  end
  
  snippet "margin: all" do |s|
    s.trigger = "margin"
    s.expansion = 'margin: ${1:20px};$0'
  end
  
  snippet "margin: T R B L" do |s|
    s.trigger = "margin"
    s.expansion = 'margin: ${1:20px} ${2:0px} ${3:40px} ${4:0px};$0'
  end
  
  snippet "margin: V H" do |s|
    s.trigger = "margin"
    s.expansion = 'margin: ${1:20px} ${2:0px};$0'
  end
  
  snippet "marker-offset: auto" do |s|
    s.trigger = "marker"
    s.expansion = 'marker-offset: auto;$0'
  end
  
  snippet "marker-offset: length" do |s|
    s.trigger = "marker"
    s.expansion = 'marker-offset: ${1:10px};$0'
  end
  # FIXME Doesn't work
  snippet "opacity: [for Safari, FF & IE]" do |s|
    s.trigger = "opacity"
    s.expansion = 'opacity: ${1:0.5};${100:
  }-moz-opacity: ${1:0.5};${100:
  }filter:alpha(opacity=${2:${1/(1?)0?\.(.*)/$1$2/}${1/^\d*\.\d\d+$|^\d*$|(^\d\.\d$)/(?1:0)/}});$0'
  end
  
  snippet "overflow: type" do |s|
    s.trigger = "overflow"
    s.expansion = 'overflow: ${1:visible/hidden/scroll/auto};$0'
  end
  
  snippet "padding-bottom: length" do |s|
    s.trigger = "padding"
    s.expansion = 'padding-bottom: ${1:20px};$0'
  end
  
  snippet "padding-left: length" do |s|
    s.trigger = "padding"
    s.expansion = 'padding-left: ${1:20px};$0'
  end
  
  snippet "padding-right: length" do |s|
    s.trigger = "padding"
    s.expansion = 'padding-right: ${1:20px};$0'
  end
  
  snippet "padding-top: length" do |s|
    s.trigger = "padding"
    s.expansion = 'padding-top: ${1:20px};$0'
  end
  
  snippet "padding: all" do |s|
    s.trigger = "padding"
    s.expansion = 'padding: ${1:20px};$0'
  end
  
  snippet "padding: T R B L" do |s|
    s.trigger = "padding"
    s.expansion = 'padding: ${1:20px} ${2:0px} ${3:40px} ${4:0px};$0'
  end
  
  snippet "padding: V H" do |s|
    s.trigger = "padding"
    s.expansion = 'padding: ${1:20px} ${2:0px};$0'
  end
  
  snippet "position: type" do |s|
    s.trigger = "position"
    s.expansion = 'position: ${1:static/relative/absolute/fixed};$0'
  end
  
  snippet "properties { } ( } )" do |s|
    s.trigger = "{"
    s.expansion = '{
    /* $1 */
    $0
  '
  end
  # FIXME Doesn't work
  snippet "scrollbar" do |s|
    s.trigger = "scrollbar"
    s.expansion = 'scrollbar-base-color:       ${1:#CCCCCC};${2:
scrollbar-arrow-color:      ${3:#000000};
scrollbar-track-color:      ${4:#999999};
scrollbar-3dlight-color:    ${5:#EEEEEE};
scrollbar-highlight-color:  ${6:#FFFFFF};
scrollbar-face-color:       ${7:#CCCCCC};
scrollbar-shadow-color:     ${9:#999999};
scrollbar-darkshadow-color: ${8:#666666};}'
  end
  
  snippet "selection" do |s|
    s.trigger = "selection"
    s.expansion = '$1::-moz-selection,
$1::selection {
  color: ${2:inherit};
  background: ${3:inherit};
}'
  end
  
  snippet "text-align: left/center/right" do |s|
    s.trigger = "text"
    s.expansion = 'text-align: ${1:left/right/center/justify};$0'
  end
  
  snippet "text-decoration: none/underline/overline/line-through/blink" do |s|
    s.trigger = "text"
    s.expansion = 'text-decoration: ${1:none/underline/overline/line-through/blink};$0'
  end
  
  snippet "text-indent: length" do |s|
    s.trigger = "text"
    s.expansion = 'text-indent: ${1:10}px;$0'
  end
  
  snippet "text-shadow: color-hex x y blur" do |s|
    s.trigger = "text"
    s.expansion = 'text-shadow: #${1:DDD} ${2:10px} ${3:10px} ${4:2px};$0'
  end
  
  snippet "text-shadow: color-rgb x y blur" do |s|
    s.trigger = "text"
    s.expansion = 'text-shadow: rgb(${1:255},${2:255},${3:255}) ${4:10px} ${5:10px} ${6:2px};$0'
  end
  
  snippet "text-shadow: none" do |s|
    s.trigger = "text"
    s.expansion = 'text-shadow: none;$0'
  end
  
  snippet "text-transform: capitalize/upper/lower" do |s|
    s.trigger = "text"
    s.expansion = 'text-transform: ${1:capitalize/uppercase/lowercase};$0'
  end
  
  snippet "text-transform: none" do |s|
    s.trigger = "text"
    s.expansion = 'text-transform: none;$0'
  end
  
  snippet "vertical-align: type" do |s|
    s.trigger = "vertical"
    s.expansion = 'vertical-align: ${1:baseline/sub/super/top/text-top/middle/bottom/text-bottom/length/%};$0'
  end
  
  snippet "visibility: type" do |s|
    s.trigger = "visibility"
    s.expansion = 'visibility: ${1:visible/hidden/collapse};$0'
  end
  
  snippet "white-space: normal/pre/nowrap" do |s|
    s.trigger = "white"
    s.expansion = 'white-space: ${1:normal/pre/nowrap};$0'
  end
  
  snippet "word-spacing: length" do |s|
    s.trigger = "word"
    s.expansion = 'word-spacing: ${1:10px};$0'
  end
  
  snippet "word-spacing: normal" do |s|
    s.trigger = "word"
    s.expansion = 'word-spacing: normal;$0'
  end
  
  snippet "z-index: index" do |s|
    s.trigger = "z"
    s.expansion = 'z-index: $1;$0'
  end

end
