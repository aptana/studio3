# -*- encoding: utf-8 -*-
# stub: axiom-types 0.1.1 ruby lib

Gem::Specification.new do |s|
  s.name = "axiom-types"
  s.version = "0.1.1"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["Dan Kubb"]
  s.date = "2014-03-27"
  s.description = "Define types with optional constraints for use within axiom and other libraries."
  s.email = "dan.kubb@gmail.com"
  s.extra_rdoc_files = ["LICENSE", "README.md", "CONTRIBUTING.md", "TODO"]
  s.files = ["LICENSE", "README.md", "CONTRIBUTING.md", "TODO"]
  s.homepage = "https://github.com/dkubb/axiom-types"
  s.licenses = ["MIT"]
  s.require_paths = ["lib"]
  s.required_ruby_version = Gem::Requirement.new(">= 1.9.3")
  s.rubygems_version = "2.1.9"
  s.summary = "Abstract types for logic programming"

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_runtime_dependency(%q<descendants_tracker>, ["~> 0.0.4"])
      s.add_runtime_dependency(%q<ice_nine>, ["~> 0.11.0"])
      s.add_runtime_dependency(%q<thread_safe>, [">= 0.3.1", "~> 0.3"])
      s.add_development_dependency(%q<bundler>, [">= 1.5.3", "~> 1.5"])
    else
      s.add_dependency(%q<descendants_tracker>, ["~> 0.0.4"])
      s.add_dependency(%q<ice_nine>, ["~> 0.11.0"])
      s.add_dependency(%q<thread_safe>, [">= 0.3.1", "~> 0.3"])
      s.add_dependency(%q<bundler>, [">= 1.5.3", "~> 1.5"])
    end
  else
    s.add_dependency(%q<descendants_tracker>, ["~> 0.0.4"])
    s.add_dependency(%q<ice_nine>, ["~> 0.11.0"])
    s.add_dependency(%q<thread_safe>, [">= 0.3.1", "~> 0.3"])
    s.add_dependency(%q<bundler>, [">= 1.5.3", "~> 1.5"])
  end
end
