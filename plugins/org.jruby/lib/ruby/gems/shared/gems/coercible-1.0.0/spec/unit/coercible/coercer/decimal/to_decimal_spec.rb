require 'spec_helper'

describe Coercer::Decimal, '.to_decimal' do
  subject { described_class.new.to_decimal(value) }

  let(:value) { BigDecimal('1.0') }

  it { should be(value) }
end
