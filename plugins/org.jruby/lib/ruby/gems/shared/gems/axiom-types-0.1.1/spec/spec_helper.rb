# encoding: utf-8

if ENV['COVERAGE'] == 'true'
  require 'simplecov'
  require 'coveralls'

  SimpleCov.formatter = SimpleCov::Formatter::MultiFormatter[
    SimpleCov::Formatter::HTMLFormatter,
    Coveralls::SimpleCov::Formatter
  ]

  SimpleCov.start do
    command_name 'spec:unit'

    add_filter 'config'
    add_filter 'spec'
    add_filter 'vendor'

    minimum_coverage 100
  end
end

require 'devtools/spec_helper'
require 'axiom-types'

include Axiom::Types

RSpec.configure do |config|
  config.expect_with :rspec do |expect_with|
    expect_with.syntax = :expect
  end

  # Record the original Type descendants
  config.before do
    @original_descendants = Axiom::Types::Type.descendants.dup
  end

  # Reset the Type descendants
  config.after do
    Axiom::Types::Type.descendants.replace(@original_descendants)
    Axiom::Types.instance_variable_get(:@inference_cache).clear
  end
end
