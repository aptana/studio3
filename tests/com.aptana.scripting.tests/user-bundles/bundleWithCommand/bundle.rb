require 'radrails'

bundle "bundleWithCommand" do
  command "MyCommand" do |cmd|
    cmd.invoke = "cd .."
  end
end