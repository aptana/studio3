require 'radrails'

with_defaults :scope => "source.css" do
  # FIXME Broken because eclipse templates can't handle '!'
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
  
end
