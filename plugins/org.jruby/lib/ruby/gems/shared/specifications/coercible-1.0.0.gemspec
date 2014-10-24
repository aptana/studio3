# -*- encoding: utf-8 -*-
# stub: coercible 1.0.0 ruby lib

Gem::Specification.new do |s|
  s.name = "coercible"
  s.version = "1.0.0"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["Piotr Solnica"]
  s.date = "2013-12-10"
  s.description = "Powerful, flexible and configurable coercion library. And nothing more."
  s.email = ["piotr.solnica@gmail.com"]
  s.homepage = "https://github.com/solnic/coercible"
  s.licenses = ["MIT"]
  s.require_paths = ["lib"]
  s.rubygems_version = "2.1.9"
  s.summary = "Powerful, flexible and configurable coercion library. And nothing more."

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_runtime_dependency(%q<descendants_tracker>, ["~> 0.0.1"])
    else
      s.add_dependency(%q<descendants_tracker>, ["~> 0.0.1"])
    end
  else
    s.add_dependency(%q<descendants_tracker>, ["~> 0.0.1"])
  end
end
