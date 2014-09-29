# encoding: utf-8

require File.expand_path('../lib/axiom/types/version', __FILE__)

Gem::Specification.new do |gem|
  gem.name        = 'axiom-types'
  gem.version     = Axiom::Types::VERSION.dup
  gem.authors     = ['Dan Kubb']
  gem.email       = 'dan.kubb@gmail.com'
  gem.summary     = 'Abstract types for logic programming'
  gem.description = 'Define types with optional constraints for use within axiom and other libraries.'
  gem.homepage    = 'https://github.com/dkubb/axiom-types'
  gem.license     = 'MIT'

  gem.require_paths    = %w[lib]
  gem.files            = `git ls-files`.split($/)
  gem.test_files       = `git ls-files -- spec/unit`.split($/)
  gem.extra_rdoc_files = %w[LICENSE README.md CONTRIBUTING.md TODO]

  gem.required_ruby_version = '>= 1.9.3'

  gem.add_runtime_dependency('descendants_tracker', '~> 0.0.4')
  gem.add_runtime_dependency('ice_nine',            '~> 0.11.0')
  gem.add_runtime_dependency('thread_safe',         '~> 0.3', '>= 0.3.1')

  gem.add_development_dependency('bundler', '~> 1.5', '>= 1.5.3')
end
