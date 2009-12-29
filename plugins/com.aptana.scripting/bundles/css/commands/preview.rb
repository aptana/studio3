require "radrails"
require 'css'
 
RadRails::Command.define_command("Show as HTML") do |cmd|
  cmd.key_binding = [ :M1, :M2, :P ]
  # FIXME This command expects input to be XML, so it expects source wrapped in xml tags denoting token types. We need to do some hacking of the input to manage this...
  cmd.output = :show_as_html
  cmd.input = :selection 
  cmd.scope = "source.css"
  cmd.invoke do |context|
    preview_css(context.in.read)
  end
end