require 'spec_helper'

describe Coercer::TimeCoercions, '.to_string' do
  subject { object.to_string(value) }

  let(:object)  { coercer.new }
  let(:coercer) { Class.new(Coercer::Object) { include Coercer::TimeCoercions } }
  let(:value)   { mock('value') }

  after do
    Coercer::Object.descendants.delete(coercer)
  end

  before do
    object.extend Coercer::TimeCoercions

    value.should_receive(:to_s).and_return('2011-01-01')
  end

  it { should be_instance_of(String) }

  it { should eql('2011-01-01') }
end
