# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Type, '.base' do
  subject { object.base }

  context 'when the type is named' do
    let(:object) { described_class }

    it { should be(object) }
  end

  context 'when the type is anonymous' do
    let(:object) { Class.new(Class.new(described_class)) }

    it { should be(described_class) }
  end
end
