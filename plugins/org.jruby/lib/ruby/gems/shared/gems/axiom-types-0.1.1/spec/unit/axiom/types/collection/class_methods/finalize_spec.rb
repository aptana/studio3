# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Collection, '.finalize' do
  subject { object.finalize }

  let(:object) { Class.new(Axiom::Types::Collection) }

  context 'with the default member constraints' do
    it_should_behave_like 'a command method'
    it_should_behave_like 'an idempotent method'

    it { should be_frozen }

    its(:constraint) { should be_frozen }

    it 'adds a constraint that returns true for a collection' do
      should include([Object.new])
    end

    it 'adds a constraint that returns false for a non-collection' do
      should_not include(Object.new)
    end
  end

  context 'with custom member constraints' do
    let(:member) { :name }

    before do
      object.member_type Axiom::Types::Symbol
    end

    it_should_behave_like 'a command method'
    it_should_behave_like 'an idempotent method'

    it { should be_frozen }

    its(:constraint) { should be_frozen }

    it 'adds a constraint that returns true for a valid member' do
      should include([member])
    end

    it 'adds a constraint that returns false for an invalid member' do
      should_not include([member.to_s])
    end
  end
end
