# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Array, '.infer' do
  subject { object.infer(arg) }

  before do
    object.finalize
  end

  before do
    # Initialize a custom type that will be used if the class lookup does not
    # restrict matching to only types with an Object member_type
    Axiom::Types.infer(Array[Float])
  end

  context 'with Axiom::Types::Array' do
    let(:object) { described_class }

    context 'when the argument is the type object' do
      let(:arg) { object }

      it { should be(object) }
    end

    context 'when the argument is ::Array' do
      let(:arg) { ::Array }

      it { should be(object) }
    end

    context 'when the argument is an empty Array' do
      let(:arg) { ::Array[] }

      it { should be(object) }
    end

    context 'when the argument is an Array with a type' do
      let(:arg) { ::Array[object] }

      its(:ancestors) { should include(object) }

      its(:primitive) { should be(object.primitive) }

      its(:member_type) { should be(object) }
    end

    context 'when the argument is an Array with a primitive' do
      let(:arg) { ::Array[::Array] }

      its(:ancestors) { should include(object) }

      its(:primitive) { should be(object.primitive) }

      its(:member_type) { should be(object) }
    end

    context 'when the argument is nil' do
      let(:arg) { nil }

      it { should be_nil }
    end
  end

  context 'with Axiom::Types::Array subclass' do
    let(:object) { Class.new(described_class) }

    context 'when the argument is the type object' do
      let(:arg) { object }

      it { should be(object) }
    end

    context 'when the argument is ::Array' do
      let(:arg) { ::Array }

      it { should be(object) }
    end

    context 'when the argument is an empty Array' do
      let(:arg) { ::Array[] }

      it { should be(object) }
    end

    context 'when the argument is an Array with a type' do
      let(:arg) { ::Array[object] }

      it { should be_nil }
    end

    context 'when the argument is an Array with a primitive' do
      let(:arg) { ::Array[::Array] }

      it { should be_nil }
    end

    context 'when the argument is nil' do
      let(:arg) { nil }

      it { should be_nil }
    end
  end
end
