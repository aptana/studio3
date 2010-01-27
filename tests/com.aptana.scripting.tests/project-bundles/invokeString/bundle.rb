require "ruble"

bundle "invokeString" do
  command "Test" do |cmd|
    cmd.invoke = "echo 'hello string'"
  end
end