require 'ruble'

with_defaults :scope => "source.ruby", :input => :none, :output => :discard do
  command "Test" do |cmd|
    cmd.invoke = "cd"
  end
end