require "ruble"

bundle "invokeBlock" do
  command "Test" do |cmd|
    cmd.invoke do
      "hello"
    end
  end
end