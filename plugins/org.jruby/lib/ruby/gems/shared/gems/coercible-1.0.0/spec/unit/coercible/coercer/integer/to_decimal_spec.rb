require 'spec_helper'

describe Coercer::Integer, '.to_decimal' do
  subject { object.to_decimal(fixnum) }

  let(:object) { described_class.new }
  let(:fixnum) { 1               }

  it { should be_instance_of(BigDecimal) }

  it { should eql(BigDecimal('1.0')) }
end
