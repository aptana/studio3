# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::ValueComparable, '#finalize' do
  subject { object.finalize }

  let(:object) do
    Class.new(Axiom::Types::Type) do
      extend Axiom::Types::ValueComparable
      minimum 1
      maximum 2
    end
  end

  it_should_behave_like 'a command method'
  it_should_behave_like 'an idempotent method'

  it { should be_frozen }

  its(:range)      { should be_frozen }
  its(:constraint) { should be_frozen }

  it 'adds a constraint that returns true for a value within range' do
    should include(1)
    should include(2)
  end

  it 'adds a constraint that returns false for a value not within range' do
    should_not include(0)
    should_not include(3)
  end
end
