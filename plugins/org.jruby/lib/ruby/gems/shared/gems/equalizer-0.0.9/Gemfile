# encoding: utf-8

source 'https://rubygems.org'

gem 'rake'

group :test do
  gem 'backports'
  gem 'coveralls', :require => false
  gem 'json',      :platforms => [:ruby_19]
  gem 'reek'
  gem 'rspec',     '~> 2.14'
  gem 'rubocop',   :platforms => [:ruby_19, :ruby_20]
  gem 'simplecov', :require => false
  gem 'yardstick'
end

platforms :jruby, :ruby_18 do
  gem 'mime-types', '~> 1.25'
end

platforms :rbx do
  gem 'racc'
  gem 'rubinius-coverage',  '~> 2.0'
  gem 'rubysl', '~> 2.0'
end

gemspec
