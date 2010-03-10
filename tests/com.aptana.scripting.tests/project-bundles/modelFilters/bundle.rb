require 'ruble'

bundle "modelFilters" do |b|

  command "Ruby" do |cmd|
    cmd.scope = "source.ruby"
    cmd.invoke = "cd ."
  end

  command "HTML" do |cmd|
    cmd.scope = "source.html"
    cmd.trigger = "html"
    cmd.invoke = "cd ."
  end

  command "JS" do |cmd|
    cmd.scope = "source.js"
    cmd.trigger = "js"
    cmd.invoke = "cd ."
  end

  command "CSS String" do |cmd|
    cmd.scope = "source.css string.double.quoted"
    cmd.trigger = "css"
    cmd.invoke = "cd ."
  end

end