require 'radrails'

command "Execute Line Inserting Results" do |cmd|
  cmd.key_binding = "CTRL+R"
  cmd.output = :replace_line
  cmd.input = [ :selection, :line ] 
  cmd.invoke { IO.popen(STDIN.read, 'r') {|io| io.read } }
end