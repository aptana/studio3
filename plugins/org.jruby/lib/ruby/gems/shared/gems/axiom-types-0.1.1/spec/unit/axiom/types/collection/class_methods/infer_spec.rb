# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Collection, '.infer' do
  subject { object.infer(arg) }

  before do
    object.finalize
  end

  context 'with the type' do
    let(:object) { described_class }

    context 'when the argument is the type object' do
      let(:arg) { object }

      it { should be(object) }
    end

    context 'when the argument is ::Enumerable' do
      let(:arg) { ::Enumerable }

      it { should be(object) }
    end
  end

  context 'with a base class' do
    let(:object) do
      Class.new(described_class) do
        primitive ::SortedSet

        def self.base?
          true
        end
      end
    end

    before do
      # Initialize a custom type that will be used if the class lookup does not
      # restrict matching to only types with an Object member_type
      Axiom::Types.infer(SortedSet[Float])
    end

    context 'when the argument is the type object' do
      let(:arg) { object }

      it { should be(object) }
    end

    context 'when the argument is ::SortedSet' do
      let(:arg) { ::SortedSet }

      it { should be(object) }
    end

    context 'when the argument is an empty SortedSet' do
      let(:arg) { ::SortedSet[] }

      it { should be(object) }
    end

    context 'when the argument is an SortedSet with a type' do
      let(:arg) { ::SortedSet[object] }

      its(:ancestors) { should include(object) }

      its(:primitive) { should be(object.primitive) }

      its(:member_type) { should be(object) }
    end

    context 'when the argument is an SortedSet with a primitive' do
      let(:arg) { ::SortedSet[::SortedSet] }

      its(:ancestors) { should include(object) }

      its(:primitive) { should be(object.primitive) }

      its(:member_type) { should be(object) }
    end

    context 'when the argument is nil' do
      let(:arg) { nil }

      it { should be_nil }
    end
  end

  context 'with a a non-base class' do
    let(:object) do
      Class.new(described_class) do
        primitive ::SortedSet
      end
    end

    context 'when the argument is the type object' do
      let(:arg) { object }

      it { should be(object) }
    end

    context 'when the argument is ::SortedSet' do
      let(:arg) { ::SortedSet }

      it { should be(object) }
    end

    context 'when the argument is an empty SortedSet' do
      let(:arg) { ::SortedSet[] }

      it { should be(object) }
    end

    context 'when the argument is an SortedSet with a type' do
      let(:arg) { ::SortedSet[object] }

      it { should be_nil }
    end

    context 'when the argument is an SortedSet with a primitive' do
      let(:arg) { ::SortedSet[::SortedSet] }

      it { should be_nil }
    end

    context 'when the argument is nil' do
      let(:arg) { nil }

      it { should be_nil }
    end
  end
end
