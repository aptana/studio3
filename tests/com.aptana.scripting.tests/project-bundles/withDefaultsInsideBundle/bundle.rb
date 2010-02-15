require 'ruble'

bundle "Bundle Reference" do
  with_defaults :scope => "source.ruby", :input => :none, :output => :discard do
    command "Test" do |cmd|
      cmd.invoke = "cd"
    end
  end
end