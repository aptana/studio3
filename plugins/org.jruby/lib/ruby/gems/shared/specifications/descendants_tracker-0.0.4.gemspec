# -*- encoding: utf-8 -*-
# stub: descendants_tracker 0.0.4 ruby lib

Gem::Specification.new do |s|
  s.name = "descendants_tracker"
  s.version = "0.0.4"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["Dan Kubb", "Piotr Solnica", "Markus Schirp"]
  s.date = "2014-03-27"
  s.description = "Module that adds descendant tracking to a class"
  s.email = ["dan.kubb@gmail.com", "piotr.solnica@gmail.com", "mbj@schirp-dso.com"]
  s.extra_rdoc_files = ["LICENSE", "README.md", "CONTRIBUTING.md", "TODO"]
  s.files = ["LICENSE", "README.md", "CONTRIBUTING.md", "TODO"]
  s.homepage = "https://github.com/dkubb/descendants_tracker"
  s.licenses = ["MIT"]
  s.require_paths = ["lib"]
  s.rubygems_version = "2.1.9"
  s.summary = "Module that adds descendant tracking to a class"

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_runtime_dependency(%q<thread_safe>, [">= 0.3.1", "~> 0.3"])
      s.add_development_dependency(%q<bundler>, [">= 1.5.3", "~> 1.5"])
    else
      s.add_dependency(%q<thread_safe>, [">= 0.3.1", "~> 0.3"])
      s.add_dependency(%q<bundler>, [">= 1.5.3", "~> 1.5"])
    end
  else
    s.add_dependency(%q<thread_safe>, [">= 0.3.1", "~> 0.3"])
    s.add_dependency(%q<bundler>, [">= 1.5.3", "~> 1.5"])
  end
end
