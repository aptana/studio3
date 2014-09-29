# -*- encoding: utf-8 -*-
# stub: ruby-maven 3.1.1.0.8 ruby ruby

Gem::Specification.new do |s|
  s.name = "ruby-maven"
  s.version = "3.1.1.0.8"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["Christian Meier"]
  s.date = "2014-07-03"
  s.description = "maven support for ruby based on tesla maven. MRI needs java/javac command installed."
  s.email = ["m.kristian@web.de"]
  s.executables = ["rmvn"]
  s.files = ["bin/rmvn"]
  s.homepage = "https://github.com/tesla/tesla-polyglot/tree/master/tesla-polyglot-gem"
  s.licenses = ["EPL"]
  s.rdoc_options = ["--main", "README.md"]
  s.require_paths = ["ruby"]
  s.rubygems_version = "2.1.9"
  s.summary = "maven support for ruby projects"

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_runtime_dependency(%q<maven-tools>, ["~> 1.0.1"])
      s.add_runtime_dependency(%q<ruby-maven-libs>, ["= 3.1.1"])
      s.add_development_dependency(%q<minitest>, ["~> 5.3"])
      s.add_development_dependency(%q<rake>, ["~> 10.3"])
    else
      s.add_dependency(%q<maven-tools>, ["~> 1.0.1"])
      s.add_dependency(%q<ruby-maven-libs>, ["= 3.1.1"])
      s.add_dependency(%q<minitest>, ["~> 5.3"])
      s.add_dependency(%q<rake>, ["~> 10.3"])
    end
  else
    s.add_dependency(%q<maven-tools>, ["~> 1.0.1"])
    s.add_dependency(%q<ruby-maven-libs>, ["= 3.1.1"])
    s.add_dependency(%q<minitest>, ["~> 5.3"])
    s.add_dependency(%q<rake>, ["~> 10.3"])
  end
end
