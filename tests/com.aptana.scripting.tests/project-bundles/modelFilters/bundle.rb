require 'ruble'

bundle "modelFilters" do |b|

  command "Ruby" do |cmd|
    cmd.scope = "source.ruby"
  end
  
  command "HTML" do |cmd|
    cmd.scope = "source.html"
    cmd.trigger = "html"
  end
  
  command "JS" do |cmd|
    cmd.scope = "source.js"
    cmd.trigger = "js"
  end
  
end