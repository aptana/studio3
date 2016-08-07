require 'spec_helper'

describe Coercer::Object, '#method_missing' do
  subject { object.send(method_name, value) }

  let(:object) { described_class.new }
  let(:value)  { Object.new }

  context "when method matches coercion method regexp" do
    let(:method_name) { :to_whatever }

    it { should be(value) }
  end

  context "when method doesn't match coercion method regexp" do
    let(:method_name) { :not_here }

    specify { expect { subject }.to raise_error(NoMethodError) }
  end
end
