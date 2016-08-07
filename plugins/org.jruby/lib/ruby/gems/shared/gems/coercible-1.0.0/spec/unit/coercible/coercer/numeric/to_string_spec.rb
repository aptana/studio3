require 'spec_helper'

describe Coercer::Numeric, '.to_string' do
  subject { object.to_string(numeric) }

  let(:object)  { described_class.new }
  let(:numeric) { Rational(2, 2)  }

  let(:coerced_value) { RUBY_VERSION < '1.9' ? '1' : '1/1' }

  it { should eql(coerced_value) }
end
