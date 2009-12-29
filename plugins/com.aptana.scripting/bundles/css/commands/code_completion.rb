require "radrails"
require "radrails/ui"
# FIXME If I just refer to the lib css file relatively, it doesn't work right
#require 'css'
require '/Users/cwilliams/Documents/RadRails Bundles/css/lib/css'

RadRails::Command.define_command("CodeCompletion CSS Properties") do |cmd|
  cmd.key_binding = [ "~" ] # FIXME Keybinding is incorrect
  cmd.output = :insert_as_snippet
  #cmd.input = [:selection, :line]
  cmd.input = :selection
  cmd.scope = "source.css"
  cmd.invoke do |context|
    options = CSS_PROPERTIES.keys # TODO Actual property list in Textmate is slightly different
    prefix = context.in.read
    options = options.select {|word| word.index(prefix) == 0 } if !prefix.nil? and prefix.size > 0
    index = RadRails::UI.menu(options)
    options[index]
  end
end
