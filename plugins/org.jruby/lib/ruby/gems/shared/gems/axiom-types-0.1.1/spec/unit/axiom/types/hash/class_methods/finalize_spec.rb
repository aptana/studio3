# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Hash, '.finalize' do
  subject { object.finalize }

  let(:object) { Class.new(Axiom::Types::Hash) }

  context 'with the default key and value constraints' do
    it_should_behave_like 'a command method'
    it_should_behave_like 'an idempotent method'

    it { should be_frozen }

    its(:constraint) { should be_frozen }

    it 'adds a constraint that returns true for a Hash' do
      should include(Object.new => Object.new)
    end

    it 'adds a constraint that returns false for a non-Hash' do
      should_not include(Object.new)
    end
  end

  context 'with custom key and value constraints' do
    let(:key)   { :name      }
    let(:value) { 'Dan Kubb' }

    before do
      object.key_type   Axiom::Types::Symbol
      object.value_type Axiom::Types::String
    end

    it_should_behave_like 'a command method'
    it_should_behave_like 'an idempotent method'

    it { should be_frozen }

    its(:constraint) { should be_frozen }

    it 'adds a constraint that returns true for valid keys and values' do
      should include(key => value)
    end

    it 'adds a constraint that returns false for an invalid key' do
      should_not include(key.to_s => value)
    end

    it 'adds a constraint that returns false for an invalid value' do
      should_not include(key => value.to_sym)
    end
  end
end
