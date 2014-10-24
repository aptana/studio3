require 'spec_helper'

describe Coercer::String, '.to_constant' do
  subject { object.to_constant(string) }

  let(:object) { described_class.new }

  context 'with a non-namespaced name' do
    let(:string) { 'String' }

    it { should be(String) }
  end

  context 'with a non-namespaced qualified name' do
    let(:string) { '::String' }

    it { should be(String) }
  end

  context 'with a namespaced name' do
    let(:string) { 'Coercible::Coercer::String' }

    it { should be(Coercer::String) }
  end

  context 'with a namespaced qualified name' do
    let(:string) { '::Coercible::Coercer::String' }

    it { should be(Coercer::String) }
  end

  context 'with a name outside of the namespace' do
    let(:string) { 'Virtus::Object' }

    specify { expect { subject }.to raise_error(NameError) }
  end

  context 'when the name is unknown' do
    let(:string) { 'Unknown' }

    specify { expect { subject }.to raise_error(NameError) }
  end

  context 'when the name is invalid' do
    let(:string) { 'invalid' }

    specify { expect { subject }.to raise_error(NameError) }
  end
end
