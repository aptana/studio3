require 'spec_helper'

describe Coercer::Numeric, '.to_decimal' do
  subject { object.to_decimal(value) }

  let(:object) { described_class.new }

  context "with an object responding to #to_d" do
    let(:value)  { Rational(2, 2)  }

    it { should eql(BigDecimal('1.0')) }
  end

  context "with an object not responding to #to_d" do
    let(:value) { Class.new { def to_s; '1'; end }.new }

    it { should eql(BigDecimal('1.0')) }
  end
end
