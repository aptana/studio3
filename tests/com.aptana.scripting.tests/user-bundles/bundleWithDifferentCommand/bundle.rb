require 'ruble'

bundle "bundleWithCommand" do
  command "MyOtherCommand" do |cmd|
    cmd.invoke = "cd"
  end
end