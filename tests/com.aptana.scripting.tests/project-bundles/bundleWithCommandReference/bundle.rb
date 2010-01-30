require 'ruble'

bundle "bundleWithCommand" do
  command "One More Application Command" do |cmd|
    cmd.invoke = "cd .."
  end
end