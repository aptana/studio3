require 'ruble'

command "Execute Line Inserting Results" do |cmd|
  cmd.key_binding = "CTRL+R"
  cmd.output = :replace_line
  cmd.input = [ :selection, :line ] 
  cmd.invoke { IO.popen(STDIN.read, 'r') {|io| io.read } }
end

command "Send Feedback..." do |cmd|
  cmd.input = :none
  cmd.output = :show_as_html
  cmd.invoke do |context|
    "<meta http-equiv='Refresh' content='0;URL=https://radrails.tenderapp.com/discussion/new'>"
  end
end

command "Report Bug..." do |cmd|
  cmd.input = :none
  cmd.output = :show_as_html
  cmd.invoke do |context|
    "<meta http-equiv='Refresh' content='0;URL=https://aptana.lighthouseapp.com/projects/35266-radrails/tickets/new'>"
  end
end
