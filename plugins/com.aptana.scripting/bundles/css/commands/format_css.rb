require 'radrails'
 
command "Format CSS" do |cmd|
  cmd.key_binding = [ :M1, :M2, :F ]
  cmd.output = :replace_selection
  cmd.input = :selection 
  cmd.scope = "source.css"
  cmd.invoke do |context|
    code = context.in.read
    property_indent = "" # Set to multiple spaces or tabs if you want properties to be indented additionally
    # TODO Figure out initial indent and then use that in substitutions to properly indent
    code.gsub!(/({|;)\s*([-\w]+:)\s*(?=\S)/im) {|match| "#{$1}\n#{property_indent}#{$2} " }
    code.gsub!(/\s*}[ \t]*\n?/im, "\n}\n") # TODO After close of rule, make sure that next selector starts at beginning of line/indent level
    code.gsub!(/\s*{[ \t]*/im, " {")
    code
  end
end