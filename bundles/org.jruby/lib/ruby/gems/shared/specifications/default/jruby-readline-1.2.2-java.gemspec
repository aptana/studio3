# -*- encoding: utf-8 -*-
# stub: jruby-readline 1.2.2 java lib

Gem::Specification.new do |s|
  s.name = "jruby-readline"
  s.version = "1.2.2"
  s.platform = "java"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.require_paths = ["lib"]
  s.authors = ["JRuby contributors"]
  s.date = "2018-02-14"
  s.description = "readline extension for JRuby"
  s.email = "dev@jruby.org"
  s.files = ["License.txt", "Mavenfile", "README.md", "lib/readline.jar", "lib/readline.rb", "lib/readline/version.rb"]
  s.homepage = "https://github.com/jruby/jruby"
  s.licenses = ["EPL-1.0", "GPL-2.0", "LGPL-2.1"]
  s.requirements = ["jar jline:jline, 2.11"]
  s.rubygems_version = "2.4.8"
  s.summary = "JRuby Readline"
end
