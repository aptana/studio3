# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::LengthComparable, '#finalize' do
  subject { object.finalize }

  let(:object) do
    Class.new(Axiom::Types::Type) do
      extend Axiom::Types::LengthComparable
      minimum_length 1
      maximum_length 2
    end
  end

  it_should_behave_like 'a command method'
  it_should_behave_like 'an idempotent method'

  it { should be_frozen }

  its(:range)      { should be_frozen }
  its(:constraint) { should be_frozen }

  it 'adds a constraint that returns true for a length within range' do
    should include('a')
    should include('ab')
  end

  it 'adds a constraint that returns false for a length not within range' do
    should_not include('')
    should_not include('abc')
  end
end
