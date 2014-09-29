require 'spec_helper'

describe Coercer::Numeric, '.to_float' do
  subject { object.to_float(numeric) }

  let(:object)  { described_class.new }
  let(:numeric) { Rational(2, 2)  }

  it { should eql(1.0) }
end
