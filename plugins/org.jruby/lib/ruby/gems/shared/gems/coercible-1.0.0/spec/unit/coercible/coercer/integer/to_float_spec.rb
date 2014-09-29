require 'spec_helper'

describe Coercer::Integer, '.to_float' do
  subject { object.to_float(fixnum) }

  let(:object) { described_class.new }
  let(:fixnum) { 1               }

  it { should be_instance_of(Float) }

  it { should eql(1.0) }
end
