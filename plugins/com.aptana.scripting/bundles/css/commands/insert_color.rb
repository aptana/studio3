require 'radrails'
require 'radrails/ui'
require 'colors'
 
command "Insert Color..." do |cmd|
  cmd.key_binding = [ :M1, :M2, :C ]
  cmd.output = :replace_selection
  cmd.input = :selection 
  cmd.scope = "source.css"
  cmd.invoke do |context|
    colour = context.in.read    
    if colour.length > 0 and colour[0] != ?#
      colour.downcase!
      # Convert named colours to their hex values
      colour = '#' + COLOURS[colour] if COLOURS.has_key? colour
    end
    
    result = RadRails::UI.request_color(colour)
    result.nil? ? colour : result
  end
end