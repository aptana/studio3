require "ruble"

command "MyCommand" do |cmd|
  cmd.input = :none
  cmd.output = :discard
  cmd.invoke = "echo 'hello'"
end