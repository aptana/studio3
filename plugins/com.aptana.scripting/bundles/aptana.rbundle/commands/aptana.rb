require 'radrails'
require "radrails/progress"

command "Execute Line Inserting Results" do |cmd|
  cmd.key_binding = "CTRL+R"
  cmd.output = :replace_line
  cmd.input = [ :selection, :line ] 
  cmd.invoke do |context|
    command_to_execute = context.in.read
    result = ""
    RadRails.call_with_progress(:title => "Execute Line Inserting Results", :message => "Executing #{command_to_execute}") do
      result = IO.popen(command_to_execute, 'r') {|io| io.read }
    end
    result
  end
end