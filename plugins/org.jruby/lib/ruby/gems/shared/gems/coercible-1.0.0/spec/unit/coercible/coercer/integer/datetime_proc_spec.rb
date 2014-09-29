require 'spec_helper'

describe Coercer::Integer, '#datetime_proc' do
  subject { object.datetime_proc }

  let(:object) { described_class.new }

  context "with Rubinius" do
    before do
      unless Coercible.rbx?
        Coercible.stub!(:rbx? => true)
      end
    end

    it { should be_instance_of(Proc) }
  end

  context "with other Ruby VMs" do
    it { should be_instance_of(Proc) }
  end
end
