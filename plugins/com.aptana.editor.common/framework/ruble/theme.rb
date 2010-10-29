require 'java'
require 'ruble'

module Ruble
  class Theme
    # This method will add a theme to the IDE from a passed in Hash.
    # expected keys are:
    # :foreground => #abcdef (CSS style 6 or 8 char hex color value)
    # :background =>  #abcdef (CSS style 6 or 8 char hex color value)
    # :selection => #abcdef (CSS style 6 or 8 char hex color value)
    # :caret => #abcdef (CSS style 6 or 8 char hex color value)
    # :lineHighlight => #abcdef (CSS style 6 or 8 char hex color value)
    # All other keys are token scopes, typically strings with words joined by periods (i.e. 'string.quoted.single.css'). 
    # Values are an array of colors and font styles. first color is fg, second (optional) is bg. 
    # 'italic', 'bold' and 'underline' are optional font styles that will get applied.
    # e.g. 'string.quoted.single.css' => ['#ff0000', '#00ff00', 'bold', 'italic'] will produce a red fg, green bg with bold and italic font.
    def self.add(hash = {})
      plugin = com.aptana.theme.ThemePlugin.getDefault
      return nil unless plugin
      color_manager = plugin.getColorManager
      theme_manager = plugin.getThemeManager
      # TODO Merge the hash with default values for fg, bg, selection, etc.
      props = java.util.Properties.new
      hash.each {|key, value| props.setProperty(key.to_s.tr("_", "."), value.to_s) }
      theme = com.aptana.theme.Theme.new(color_manager, props)
      theme_manager.addTheme(theme)
      return theme
    end
  end
end