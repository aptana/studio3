require 'spec_helper'

describe Coercer::FalseClass, '.to_string' do
  subject { object.to_string(false_class) }

  let(:object)      { described_class.new }
  let(:false_class) { false           }

  it { should be_instance_of(String) }

  it { should eql('false') }
end
