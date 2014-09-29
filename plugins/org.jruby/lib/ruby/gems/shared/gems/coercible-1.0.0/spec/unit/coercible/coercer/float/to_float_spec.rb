require 'spec_helper'

describe Coercer::Float, '.to_float' do
  subject { described_class.new.to_float(value) }

  let(:value) { 1.0 }

  it { should be(value) }
end
