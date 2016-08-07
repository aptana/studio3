require 'spec_helper'

describe Coercer::Integer, '.to_boolean' do
  subject { object.to_boolean(fixnum) }

  let(:object) { described_class.new }

  context 'when the fixnum is 1' do
    let(:fixnum) { 1 }

    it { should be(true) }
  end

  context 'when the fixnum is 0' do
    let(:fixnum) { 0 }

    it { should be(false) }
  end

  context 'when the fixnum is not 1 or 0' do
    let(:fixnum) { -1 }

    specify do
      expect { subject }.to raise_error(UnsupportedCoercion)
    end
  end
end
