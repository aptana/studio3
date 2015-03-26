# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Type, '.new' do
  let(:undefined) { Axiom::Types::Undefined }

  before do
    object.finalize
  end

  context 'with no arguments' do
    subject { object.new }

    let(:object) { described_class }

    it { should be_instance_of(Class) }

    it { should be_frozen }

    its(:ancestors) { should include(object) }

    it 'has no constraints' do
      should include(undefined)
    end
  end

  context 'with a constraint' do
    subject { object.new(proc { false }) }

    let(:object) { described_class }

    it { should be_instance_of(Class) }

    it { should be_frozen }

    its(:ancestors) { should include(object) }

    it 'has constraints' do
      should_not include(undefined)
    end
  end

  context 'with a block' do
    subject do
      object.new do
        constraint { false }
      end
    end

    let(:object) { described_class }

    it { should be_instance_of(Class) }

    it { should be_frozen }

    its(:ancestors) { should include(object) }

    it 'has constraints' do
      should_not include(undefined)
    end
  end

  context 'with a constraint and block' do
    subject do
      object.new(proc { false }) do
        constraint { false }
      end
    end

    let(:object) { described_class }

    it { should be_instance_of(Class) }

    it { should be_frozen }

    its(:ancestors) { should include(object) }

    it 'has constraints' do
      should_not include(undefined)
    end
  end
end
