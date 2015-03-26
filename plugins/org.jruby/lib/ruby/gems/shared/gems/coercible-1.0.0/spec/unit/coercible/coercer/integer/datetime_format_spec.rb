require 'spec_helper'

describe Coercer::Integer, '#datetime_format' do
  subject { object.datetime_format }

  let(:object) { described_class.new }

  context "with Rubinius" do
    before do
      unless Coercible.rbx?
        Coercible.stub!(:rbx? => true)
      end
    end

    it { should == '%Q' }
  end

  context "with other Ruby VMs" do
    before do
      if Coercible.rbx?
        Coercible.stub!(:rbx? => false)
      end
    end

    it { should == '%s' }
  end
end
