require 'spec_helper'

describe Configuration, '.build' do
  subject { described_class.build(keys) }

  let(:keys) { [ :foo, :bar ] }

  it { should be_instance_of(described_class) }

  it { should respond_to(:foo) }
  it { should respond_to(:foo=) }

  it { should respond_to(:bar) }
  it { should respond_to(:bar=) }
end
