# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Object, '.finalize' do
  subject { object.finalize }

  let(:object) do
    # Class.new { ... } does not work with this on 1.8.7
    object = Class.new(described_class)
    object.primitive(::String)
    object
  end

  it_should_behave_like 'a command method'
  it_should_behave_like 'an idempotent method'

  it { should be_frozen }

  its(:constraint) { should be_frozen }

  it 'adds a constraint that returns true for a valid primitive' do
    should include('string')
  end

  it 'adds a constraint that returns false for an invalid primitive' do
    should_not include(nil)
  end
end
