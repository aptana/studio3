require 'spec_helper'

describe Coercer::Date, '.to_string' do
  subject { object.to_string(date) }

  let(:object) { described_class.new      }
  let(:date)   { Date.new(2011, 1, 1) }

  it { should be_instance_of(String) }

  it { should eql('2011-01-01') }
end
