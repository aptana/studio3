require 'spec_helper'

describe Coercer::String, '.to_date' do
  subject { object.to_date(string) }

  let(:object) { described_class.new }

  context 'with a valid date string' do
    let(:string) { 'July, 22th, 2011' }

    it { should be_instance_of(Date) }

    its(:year)  { should == 2011 }
    its(:month) { should == 7    }
    its(:day)   { should == 22   }
  end

  context 'with an invalid date string' do
    let(:string) { 'non-date' }

    specify do
      expect { subject }.to raise_error(UnsupportedCoercion)
    end
  end
end
