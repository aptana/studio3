# -*- encoding: utf-8 -*-
# stub: krypt-core 0.0.2 universal-java lib

Gem::Specification.new do |s|
  s.name = "krypt-core"
  s.version = "0.0.2"
  s.platform = "universal-java"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["Hiroshi Nakamura, Martin Bosslet"]
  s.date = "2014-06-22"
  s.description = "Java implementation of the krypt-core API"
  s.email = "Martin.Bosslet@gmail.com"
  s.files = ["Rakefile", "LICENSE", "README.rdoc", "Manifest.txt", "lib/kryptcore.jar", "lib/krypt/core.rb", "lib/krypt/core/version.rb"]
  s.homepage = "https://github.com/krypt/krypt-core-java"
  s.licenses = ["MIT"]
  s.require_paths = ["lib"]
  s.required_ruby_version = Gem::Requirement.new(">= 1.9.3")
  s.rubygems_version = "2.1.9"
  s.summary = "krypt-core API for JRuby"

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_runtime_dependency(%q<krypt-provider-jdk>, ["= 0.0.2"])
    else
      s.add_dependency(%q<krypt-provider-jdk>, ["= 0.0.2"])
    end
  else
    s.add_dependency(%q<krypt-provider-jdk>, ["= 0.0.2"])
  end
end
