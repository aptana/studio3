require 'ruble'

bundle "bundleWithCommand" do
  command "Another Application Command" do |cmd|
    cmd.invoke = "cd /"
  end
end