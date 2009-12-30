require "radrails"
require 'preview_generator'
 
command "Preview" do |cmd|
  cmd.key_binding = [ :M1, :M2, :P ]
  cmd.output = :show_as_html
  cmd.input = :selection 
  cmd.scope = "source.css"
  cmd.invoke do |context|
    preview_css(context.in.read, context.environment)
  end
end