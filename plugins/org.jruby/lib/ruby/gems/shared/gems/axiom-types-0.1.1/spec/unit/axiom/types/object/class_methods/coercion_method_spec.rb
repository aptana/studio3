# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Object, '.coercion_method' do
  let(:object)          { Class.new(described_class) }
  let(:coercion_method) { :to_object                 }

  context 'with no arguments' do
    subject { object.coercion_method }

    it { should be(coercion_method) }
  end

  context 'with a symbol' do
    subject { object.coercion_method(symbol) }

    let(:symbol) { :to_string }

    it_should_behave_like 'a command method'

    it 'sets the coercion_method' do
      expect { subject }.to change { object.coercion_method }
        .from(coercion_method).to(symbol)
    end
  end
end
