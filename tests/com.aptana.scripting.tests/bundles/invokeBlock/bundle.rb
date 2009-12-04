require "radrails"

bundle "invokeBlock" do |b|
  command "Test" do |cmd|
    cmd.invoke do
      "hello"
    end
  end
end