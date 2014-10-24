# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Type, '.include?' do
  subject { object.include?(value) }

  let(:object) do
    Class.new(described_class) do
      constraint(->(object) { !object.nil? })
    end
  end

  context 'when the value matches the type constraint' do
    let(:value) { Object.new }

    it { should be(true) }
  end

  context 'when the value does not match the type constraint' do
    let(:value) { nil }

    it { should be(false) }
  end
end
