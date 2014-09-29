require 'spec_helper'

describe Coercer::Object, '#inspect' do
  subject { object.inspect }

  let(:object) { described_class.new }

  it { should == '#<Coercible::Coercer::Object primitive=Object>'}
end
