require 'spec_helper'

describe Coercer, '#[]' do
  subject { object[type] }

  let(:object) { described_class.new }

  context "with a known type" do
    let(:type) { ::String }

    it { should be_instance_of(Coercer::String) }
  end

  context "with an unknown type" do
    let(:type) { Object }

    it { should be_instance_of(Coercer::Object) }
  end
end
