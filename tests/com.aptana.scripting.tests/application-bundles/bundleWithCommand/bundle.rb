require 'ruble'

bundle do
  command "Application Command" do |cmd|
    cmd.invoke = "cd"
  end
end