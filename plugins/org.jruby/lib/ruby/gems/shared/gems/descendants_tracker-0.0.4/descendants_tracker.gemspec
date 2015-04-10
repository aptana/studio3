# encoding: utf-8

require File.expand_path('../lib/descendants_tracker/version', __FILE__)

Gem::Specification.new do |gem|
  gem.name        = 'descendants_tracker'
  gem.version     = DescendantsTracker::VERSION.dup
  gem.authors     = [ 'Dan Kubb', 'Piotr Solnica', 'Markus Schirp' ]
  gem.email       = %w[ dan.kubb@gmail.com piotr.solnica@gmail.com mbj@schirp-dso.com ]
  gem.description = 'Module that adds descendant tracking to a class'
  gem.summary     = gem.description
  gem.homepage    = 'https://github.com/dkubb/descendants_tracker'
  gem.license     = 'MIT'

  gem.require_paths    = %w[lib]
  gem.files            = `git ls-files`.split($/)
  gem.test_files       = `git ls-files -- spec/unit`.split($/)
  gem.extra_rdoc_files = %w[LICENSE README.md CONTRIBUTING.md TODO]

  gem.add_runtime_dependency('thread_safe', '~> 0.3', '>= 0.3.1')

  gem.add_development_dependency('bundler', '~> 1.5', '>= 1.5.3')
end
