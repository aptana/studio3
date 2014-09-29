require 'spec_helper'

describe Coercer::TimeCoercions, '.to_time' do
  subject { object.to_time(value) }

  let(:object)  { coercer.new }
  let(:coercer) { Class.new(Coercer::Object) { include Coercer::TimeCoercions } }
  let(:value)   { mock('value') }

  after do
    Coercer::Object.descendants.delete(coercer)
  end

  context 'when the value responds to #to_time' do
    before do
      object.extend Coercer::TimeCoercions

      value.should_receive(:to_time).and_return(Time.utc(2011, 1, 1))
    end

    it { should be_instance_of(Time) }

    it { should eql(Time.utc(2011, 1, 1)) }
  end

  context 'when the value does not respond to #to_time' do
    before do
      object.extend Coercer::TimeCoercions

      # use a string that Time.parse can handle
      value.should_receive(:to_s).and_return('Sat Jan 01 00:00:00 UTC 2011')
    end

    it { should be_instance_of(Time) }

    it { should eql(Time.utc(2011, 1, 1)) }
  end
end
