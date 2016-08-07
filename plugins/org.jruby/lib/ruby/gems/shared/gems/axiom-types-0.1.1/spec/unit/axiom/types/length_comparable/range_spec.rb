# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::LengthComparable, '#range' do
  subject { object.range }

  let(:object) do
    Class.new(Axiom::Types::Type) do
      extend Axiom::Types::LengthComparable
      minimum_length 1
      maximum_length 2
    end
  end

  before do
    object.finalize
  end

  it_should_behave_like 'an idempotent method'

  it { should be_frozen }

  it { should eql(1..2) }
end
