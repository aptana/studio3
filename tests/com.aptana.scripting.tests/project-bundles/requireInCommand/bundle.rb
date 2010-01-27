require 'ruble'

bundle "requireInCommand" do
  command "MyCommand" do |cmd|
    cmd.invoke do |context|
    	require 'thing'
    	
    	t = Thing.new("My Thing Name")
    	t.name
    end
  end
end