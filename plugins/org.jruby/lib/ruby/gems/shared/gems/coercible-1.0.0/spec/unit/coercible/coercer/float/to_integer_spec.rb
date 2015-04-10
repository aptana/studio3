require 'spec_helper'

describe Coercer::Float, '.to_integer' do
  subject { object.to_integer(float) }

  let(:object) { described_class.new }
  let(:float)  { 1.0             }

  it { should be_kind_of(Integer) }

  it { should eql(1) }
end
