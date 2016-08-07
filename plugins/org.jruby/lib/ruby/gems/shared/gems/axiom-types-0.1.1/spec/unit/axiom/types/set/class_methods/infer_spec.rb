# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Set, '.infer' do
  subject { object.infer(arg) }

  before do
    object.finalize
  end

  before do
    # Initialize a custom type that will be used if the class lookup does not
    # restrict matching to only types with an Object member_type
    Axiom::Types.infer(Set[Float])
  end

  context 'with Axiom::Types::Set' do
    let(:object) { described_class }

    context 'when the argument is the type object' do
      let(:arg) { object }

      it { should be(object) }
    end

    context 'when the argument is ::Set' do
      let(:arg) { ::Set }

      it { should be(object) }
    end

    context 'when the argument is an empty Set' do
      let(:arg) { ::Set[] }

      it { should be(object) }
    end

    context 'when the argument is an Set with a type' do
      let(:arg) { ::Set[object] }

      its(:ancestors) { should include(object) }

      its(:primitive) { should be(object.primitive) }

      its(:member_type) { should be(object) }
    end

    context 'when the argument is an Set with a primitive' do
      let(:arg) { ::Set[::Set] }

      its(:ancestors) { should include(object) }

      its(:primitive) { should be(object.primitive) }

      its(:member_type) { should be(object) }
    end

    context 'when the argument is nil' do
      let(:arg) { nil }

      it { should be_nil }
    end
  end

  context 'with Axiom::Types::Set subclass' do
    let(:object) { Class.new(described_class) }

    context 'when the argument is the type object' do
      let(:arg) { object }

      it { should be(object) }
    end

    context 'when the argument is ::Set' do
      let(:arg) { ::Set }

      it { should be(object) }
    end

    context 'when the argument is an empty Set' do
      let(:arg) { ::Set[] }

      it { should be(object) }
    end

    context 'when the argument is an Set with a type' do
      let(:arg) { ::Set[object] }

      it { should be_nil }
    end

    context 'when the argument is an Set with a primitive' do
      let(:arg) { ::Set[::Set] }

      it { should be_nil }
    end

    context 'when the argument is nil' do
      let(:arg) { nil }

      it { should be_nil }
    end
  end
end
