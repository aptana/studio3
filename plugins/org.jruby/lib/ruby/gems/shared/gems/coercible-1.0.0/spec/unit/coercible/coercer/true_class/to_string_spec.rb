require 'spec_helper'

describe Coercer::TrueClass, '.to_string' do
  subject { object.to_string(true_class) }

  let(:object)     { described_class.new }
  let(:true_class) { true            }

  it { should be_instance_of(String) }

  it { should eql('true') }
end
