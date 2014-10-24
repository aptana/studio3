# encoding: utf-8

if ENV['COVERAGE'] == 'true'
  require 'simplecov'
  require 'coveralls'

  SimpleCov.formatter = SimpleCov::Formatter::MultiFormatter[
    SimpleCov::Formatter::HTMLFormatter,
    Coveralls::SimpleCov::Formatter,
  ]

  SimpleCov.start do
    command_name 'spec:unit'

    add_filter 'config'
    add_filter 'spec'
    add_filter 'vendor'

    minimum_coverage 100
  end
end

require 'equalizer'

# TODO: FIXME!
# Cache correct freezer in ice_nine before
# rspec2 infects the world...
Equalizer.new

RSpec.configure do |config|
  config.expect_with :rspec do |expect_with|
    expect_with.syntax = :expect
  end
end
