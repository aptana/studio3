# -*- encoding: utf-8 -*-
lib = File.expand_path('../lib', __FILE__)
$LOAD_PATH.unshift(lib) unless $LOAD_PATH.include?(lib)
require 'coercible/version'

Gem::Specification.new do |gem|
  gem.name          = "coercible"
  gem.version       = Coercible::VERSION
  gem.authors       = ["Piotr Solnica"]
  gem.email         = ["piotr.solnica@gmail.com"]
  gem.description   = %q{Powerful, flexible and configurable coercion library. And nothing more.}
  gem.summary       = gem.description
  gem.homepage      = "https://github.com/solnic/coercible"
  gem.license       = "MIT"

  gem.files         = `git ls-files`.split($/)
  gem.executables   = gem.files.grep(%r{^bin/}).map{ |f| File.basename(f) }
  gem.test_files    = gem.files.grep(%r{^(test|spec|features)/})
  gem.require_paths = ["lib"]

  gem.add_dependency 'descendants_tracker', '~> 0.0.1'
end
