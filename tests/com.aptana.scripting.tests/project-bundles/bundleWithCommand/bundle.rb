require 'ruble'

bundle do
  command "Project Command" do |cmd|
    cmd.invoke = "cd /"
  end
end