require 'ruble'

bundle do
  with_defaults :scope => "source.ruby", :input => :none, :output => :discard do
    command "Test 1" do |cmd|
      cmd.invoke = "cd"
    end
    with_defaults :scope => "text.html" do
      command "Test 2" do |cmd|
        cmd.invoke = "cd .."
      end
    end
    command "Test 3" do |cmd|
      cmd.invoke = "cls"
    end
  end
end