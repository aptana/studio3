require 'spec_helper'

describe Coercer::Object, '.to_string' do
  subject { object.to_string(value) }

  let(:object) { described_class.new }
  let(:value)  { Object.new      }

  context 'when the value responds to #to_str' do
    let(:coerced) { stub('coerced') }

    before do
      value.should_receive(:to_str).with().and_return(coerced)
    end

    it { should be(coerced) }
  end

  context 'when the value does not respond to #to_str' do
    specify { expect { subject }.to raise_error(UnsupportedCoercion) }
  end
end
