require 'ruble'

bundle do
  command "MyCommand" do |cmd|
    cmd.invoke = "cd .."
  end
end