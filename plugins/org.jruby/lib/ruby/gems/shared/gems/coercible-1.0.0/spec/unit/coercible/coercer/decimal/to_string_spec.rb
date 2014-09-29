require 'spec_helper'

describe Coercer::Decimal, '.to_string' do
  subject { object.to_string(big_decimal) }

  let(:object)      { described_class.new   }
  let(:big_decimal) { BigDecimal('1.0') }

  it { should be_instance_of(String) }

  it { should eql('1.0') }
end
