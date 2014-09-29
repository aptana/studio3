# -*- encoding: utf-8 -*-
# stub: jar-dependencies 0.1.2 ruby lib

Gem::Specification.new do |s|
  s.name = "jar-dependencies"
  s.version = "0.1.2"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["christian meier"]
  s.date = "2014-09-17"
  s.description = "manage jar dependencies for gems and keep track which jar was already loaded using maven artifact coordinates. it warns on version conflicts and loads only ONE jar assuming the first one is compatible to the second one otherwise your project needs to lock down the right version."
  s.email = ["mkristian@web.de"]
  s.executables = ["bundle-with-jars"]
  s.files = ["lib/jar_install_post_install_hook.rb", "lib/jar_dependencies.rb", "lib/jar_installer.rb", "lib/jar-dependencies.rb", "lib/rubygems_plugin.rb", "bin/bundle-with-jars", "Rakefile", "Mavenfile", "Gemfile", "Readme.md", "jar-dependencies.gemspec", "MIT-LICENSE"]
  s.homepage = "https://github.com/mkristian/jar-dependencies"
  s.licenses = ["MIT"]
  s.require_paths = ["lib"]
  s.requirements = ["gem ruby-maven, ~> 3.1.1.0, :scope => :runtime"]
  s.rubygems_version = "2.1.9"
  s.summary = "manage jar dependencies for gems"

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_development_dependency(%q<minitest>, ["~> 5.3"])
      s.add_development_dependency(%q<rake>, ["~> 10.2"])
    else
      s.add_dependency(%q<minitest>, ["~> 5.3"])
      s.add_dependency(%q<rake>, ["~> 10.2"])
    end
  else
    s.add_dependency(%q<minitest>, ["~> 5.3"])
    s.add_dependency(%q<rake>, ["~> 10.2"])
  end
end
