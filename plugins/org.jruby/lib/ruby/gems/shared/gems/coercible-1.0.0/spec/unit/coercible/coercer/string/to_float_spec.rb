require 'spec_helper'

describe Coercer::String, '.to_float' do
  subject { described_class.new.to_float(string) }

  {
    '1'       => 1.0,
    '+1'      => 1.0,
    '-1'      => -1.0,
    '1.0'     => 1.0,
    '1.0e+1'  => 10.0,
    '1.0e-1'  => 0.1,
    '1.0E+1'  => 10.0,
    '1.0E-1'  => 0.1,
    '+1.0'    => 1.0,
    '+1.0e+1' => 10.0,
    '+1.0e-1' => 0.1,
    '+1.0E+1' => 10.0,
    '+1.0E-1' => 0.1,
    '-1.0'    => -1.0,
    '-1.0e+1' => -10.0,
    '-1.0e-1' => -0.1,
    '-1.0E+1' => -10.0,
    '-1.0E-1' => -0.1,
    '.1'      => 0.1,
    '.1e+1'   => 1.0,
    '.1e-1'   => 0.01,
    '.1E+1'   => 1.0,
    '.1E-1'   => 0.01,
    '1e1'     => 10.0,
    '1E+1'    => 10.0,
    '+1e-1'   => 0.1,
    '-1E1'    => -10.0,
    '-1e-1'   => -0.1,
  }.each do |value, expected|
    context "with #{value.inspect}" do
      let(:string) { value }

      it { should be_instance_of(Float) }

      it { should eql(expected) }
    end
  end

  context 'with an invalid float string' do
    let(:string) { 'non-float' }

    specify { expect { subject }.to raise_error(UnsupportedCoercion) }
  end

  context 'string starts with e' do
    let(:string) { 'e1' }

    specify { expect { subject }.to raise_error(UnsupportedCoercion) }
  end
end
