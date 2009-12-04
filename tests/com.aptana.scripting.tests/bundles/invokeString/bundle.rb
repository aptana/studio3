require "radrails"

bundle "invokeString" do |b|
  command "Test" do |cmd|
    cmd.invoke = "echo hello"
  end
end