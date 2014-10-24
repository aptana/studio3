# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Hash, '.infer' do
  subject { object.infer(arg) }

  before do
    object.finalize
  end

  before do
    # Initialize custom types that will be used if the class lookup does not
    # restrict matching to only types with an Object key and value types
    Axiom::Types.infer(Hash[Object => Float])
    Axiom::Types.infer(Hash[Float  => Object])
  end

  context 'with Axiom::Types::Hash' do
    let(:object) { described_class }

    context 'with a type object' do
      let(:arg) { object }

      it { should be(object) }
    end

    context 'with a ::Hash' do
      let(:arg) { ::Hash }

      it { should be(object) }
    end

    context 'with an empty Hash' do
      let(:arg) { ::Hash[] }

      it { should be(object) }
    end

    context 'with an Hash with a key type and nil value' do
      let(:arg) { ::Hash[object => nil] }

      its(:ancestors) { should include(object) }

      its(:primitive) { should be(object.primitive) }

      its(:key_type) { should be(object) }

      its(:value_type) { should be(Axiom::Types::Object) }
    end

    context 'with an Hash with a nil key and value type' do
      let(:arg) { ::Hash[nil => object] }

      its(:ancestors) { should include(object) }

      its(:primitive) { should be(object.primitive) }

      its(:key_type) { should be(Axiom::Types::Object) }

      its(:value_type) { should be(object) }
    end

    context 'with an Hash with key and value types' do
      let(:arg) { ::Hash[object => object] }

      its(:ancestors) { should include(object) }

      its(:primitive) { should be(object.primitive) }

      its(:key_type) { should be(object) }

      its(:value_type) { should be(object) }
    end

    context 'with an Hash with a key primitive and nil value' do
      let(:arg) { ::Hash[::Hash => nil] }

      its(:ancestors) { should include(object) }

      its(:primitive) { should be(object.primitive) }

      its(:key_type) { should be(object) }

      its(:value_type) { should be(Axiom::Types::Object) }
    end

    context 'with an Hash with a nil key and value primitive' do
      let(:arg) { ::Hash[nil => ::Hash] }

      its(:ancestors) { should include(object) }

      its(:primitive) { should be(object.primitive) }

      its(:key_type) { should be(Axiom::Types::Object) }

      its(:value_type) { should be(object) }
    end

    context 'with an Hash with key and value primitives' do
      let(:arg) { ::Hash[::Hash => ::Hash] }

      its(:ancestors) { should include(object) }

      its(:primitive) { should be(object.primitive) }

      its(:key_type) { should be(object) }

      its(:value_type) { should be(object) }
    end

    context 'when the argument is nil' do
      let(:arg) { nil }

      it { should be_nil }
    end
  end

  context 'with Axiom::Types::Hash subclass' do
    let(:object) { Class.new(described_class) }

    context 'when the argument is the type object' do
      let(:arg) { object }

      it { should be(object) }
    end

    context 'when the argument is ::Hash' do
      let(:arg) { ::Hash }

      it { should be(object) }
    end

    context 'with an empty Hash' do
      let(:arg) { ::Hash[] }

      it { should be(object) }
    end

    context 'with an Hash with a key type and nil value' do
      let(:arg) { ::Hash[object => nil] }

      it { should be_nil }
    end

    context 'with an Hash with a nil key and value type' do
      let(:arg) { ::Hash[nil => object] }

      it { should be_nil }
    end

    context 'with an Hash with key and value types' do
      let(:arg) { ::Hash[object => object] }

      it { should be_nil }
    end

    context 'with an Hash with a key primitive and nil value' do
      let(:arg) { ::Hash[::Hash => nil] }

      it { should be_nil }
    end

    context 'with an Hash with a nil key and value primitive' do
      let(:arg) { ::Hash[nil => ::Hash] }

      it { should be_nil }
    end

    context 'with an Hash with key and value primitives' do
      let(:arg) { ::Hash[::Hash => ::Hash] }

      it { should be_nil }
    end

    context 'with a nil' do
      let(:arg) { nil }

      it { should be_nil }
    end
  end
end
