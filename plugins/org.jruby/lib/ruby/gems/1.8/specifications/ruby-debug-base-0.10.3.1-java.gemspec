# -*- encoding: utf-8 -*-

Gem::Specification.new do |s|
  s.name = %q{ruby-debug-base}
  s.version = "0.10.3.1"
  s.platform = %q{java}

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["debug-commons team"]
  s.date = %q{2008-12-19}
  s.description = %q{Java extension to make fast ruby debugger run on JRuby. It is the same what ruby-debug-base is for native Ruby.}
  s.files = ["AUTHORS", "ChangeLog", "lib/linecache.rb", "lib/linecache-ruby.rb", "lib/ruby-debug-base.rb", "lib/ruby_debug.jar", "lib/tracelines.rb", "MIT-LICENSE", "Rakefile", "README"]
  s.has_rdoc = true
  s.homepage = %q{http://rubyforge.org/projects/debug-commons/}
  s.require_paths = ["lib"]
  s.rubyforge_project = %q{debug-commons}
  s.rubygems_version = %q{1.3.1}
  s.summary = %q{Java implementation of Fast Ruby Debugger}

  if s.respond_to? :specification_version then
    current_version = Gem::Specification::CURRENT_SPECIFICATION_VERSION
    s.specification_version = 2

    if Gem::Version.new(Gem::RubyGemsVersion) >= Gem::Version.new('1.2.0') then
    else
    end
  else
  end
end
