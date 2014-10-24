# encoding: utf-8

require 'spec_helper'

describe Axiom::Types, '.infer' do
  subject { object.infer(type) }

  let(:object) { described_class }

  before do
    object.finalize
  end

  Axiom::Types::Type.descendants.each do |descendant|
    context "when the type is #{descendant}" do
      let(:type) { descendant }

      it { should be(descendant) }
    end

    if descendant.equal?(Axiom::Types::Boolean)
      context 'when the type is TrueClass' do
        let(:type) { ::TrueClass }

        it { should be(descendant) }
      end

      context 'when the type is FalseClass' do
        let(:type) { ::FalseClass }

        it { should be(descendant) }
      end
    else
      primitive = descendant.primitive

      context "when the type is #{primitive}" do
        let(:type) { primitive }

        it { should be(descendant) }
      end
    end
  end

  context 'with a custom type' do
    let(:type) do
      Axiom::Types::String.new do
        minimum_length 1
        maximum_length 100
      end
    end

    it { should be(type) }
  end
end
