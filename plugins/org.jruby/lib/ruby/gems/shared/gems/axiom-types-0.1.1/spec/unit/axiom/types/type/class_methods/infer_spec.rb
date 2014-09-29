# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Type, '.infer' do
  subject { object.infer(arg) }

  let(:object) { described_class }

  context 'when the argument is the type object' do
    let(:arg) { object }

    it { should be(object) }
  end

  Axiom::Types::Type.descendants.each do |descendant|
    context "when the argument is #{descendant}" do
      let(:arg) { descendant }

      it 'does not match any other type' do
        should be_nil
      end
    end
  end
end
