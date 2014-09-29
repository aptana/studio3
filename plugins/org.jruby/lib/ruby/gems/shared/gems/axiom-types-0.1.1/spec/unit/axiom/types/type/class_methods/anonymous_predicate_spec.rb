# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Type, '.anonymous?' do
  subject { object.anonymous? }

  context 'when the type is named' do
    let(:object) { described_class }

    it { should be(false) }
  end

  context 'when the type is anonymous' do
    let(:object) { Class.new(described_class) }

    it { should be(true) }
  end
end
