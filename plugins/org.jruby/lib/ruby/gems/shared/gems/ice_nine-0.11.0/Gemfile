# encoding: utf-8

source 'https://rubygems.org'

gemspec

platform :rbx do
  gem 'racc',              '~> 1.4.10'
  gem 'rubinius-coverage', '~> 2.0.3'
  gem 'rubysl-bigdecimal', '~> 2.0.2'
  gem 'rubysl-coverage',   '~> 2.0.3'
  gem 'rubysl-json',       '~> 2.0.2'
  gem 'rubysl-logger',     '~> 2.0.0'
  gem 'rubysl-singleton',  '~> 2.0.0'
end

group :development, :test do
  gem 'devtools', git: 'https://github.com/rom-rb/devtools.git'
end

eval_gemfile 'Gemfile.devtools'
