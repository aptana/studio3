require 'spec_helper'

describe Coercer, '.new' do
  subject { described_class.new(&block) }

  let(:block) { Proc.new {} }

  it { should be_instance_of(Coercer) }

  its(:config) { should be_instance_of(Coercible::Configuration) }
  its(:config) { should respond_to(:string) }
  its(:config) { should respond_to(:string=) }
end
