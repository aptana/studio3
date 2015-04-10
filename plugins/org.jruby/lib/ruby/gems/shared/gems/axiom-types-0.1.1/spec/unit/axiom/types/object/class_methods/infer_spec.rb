# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Object, '.infer' do
  subject { object.infer(arg) }

  context 'with the type' do
    let(:object) { described_class }

    context 'when the argument is the type object' do
      let(:arg) { object }

      it { should be(object) }
    end

    context 'when the argument is ::BasicObject' do
      let(:arg) { ::BasicObject }

      it { should be(object) }
    end

    context 'when the argument is ::Object' do
      let(:arg) { ::Object }

      it { should be(object) }
    end

    Axiom::Types::Object.descendants.each do |descendant|
      primitive = descendant.primitive

      context "when the argument is #{descendant}" do
        let(:object) { descendant }
        let(:arg)    { object     }

        it { should be(object) }
      end

      context "when the argument is ::#{primitive}" do
        let(:arg) { primitive }

        it { should be(object) }
      end
    end

    context 'when the argument is an ::Object instance' do
      let(:arg) { ::Object.new }

      it { should be_nil }
    end

    context 'when the argument is nil' do
      let(:arg) { nil }

      it { should be_nil }
    end
  end

  context 'with a subclass' do
    let(:object) do
      described_class.new { primitive ::Struct }
    end

    context 'when the argument is the type object' do
      let(:arg) { object }

      it { should be(object) }
    end

    context 'when the argument is ::BasicObject' do
      let(:arg) { ::BasicObject }

      it { should be_nil }
    end

    context 'when the argument is ::Object' do
      let(:arg) { ::Object }

      it { should be_nil }
    end

    context 'when the argument is an ::Object instance' do
      let(:arg) { ::Object.new }

      it { should be_nil }
    end

    context 'when the argument is ::Struct' do
      let(:arg) { ::Struct }

      it { should be(object) }
    end

    context 'when the argument is a ::Struct instance' do
      let(:arg) { ::Struct.new(:a).new(1) }

      it { should be_nil }
    end

    context 'when the argument is nil' do
      let(:arg) { nil }

      it { should be_nil }
    end
  end
end
