require 'spec_helper'

describe Coercer::Float, '.to_string' do
  subject { object.to_string(float) }

  let(:object) { described_class.new }
  let(:float)  { 1.0             }

  it { should be_instance_of(String) }

  it { should eql('1.0') }
end
