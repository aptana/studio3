require 'spec_helper'

describe Coercer::TimeCoercions, '.to_date' do
  subject { object.to_date(value) }

  let(:object)  { coercer.new }
  let(:coercer) { Class.new(Coercer::Object) { include Coercer::TimeCoercions } }
  let(:value)   { mock('value') }

  after do
    Coercer::Object.descendants.delete(coercer)
  end

  context 'when the value responds to #to_date' do
    before do
      value.should_receive(:to_date).and_return(Date.new(2011, 1, 1))
    end

    it { should be_instance_of(Date) }

    it { should eql(Date.new(2011, 1, 1)) }
  end

  context 'when the value does not respond to #to_date' do
    before do
      # use a string that Date.parse can handle
      value.should_receive(:to_s).and_return('2011-01-01')
    end

    it { should be_instance_of(Date) }

    it { should eql(Date.new(2011, 1, 1)) }
  end
end
