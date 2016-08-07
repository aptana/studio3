# encoding: utf-8

require 'spec_helper'
require 'ice_nine'

describe IceNine::Freezer::Array, '.deep_freeze' do
  subject { object.deep_freeze(value) }

  let(:object) { described_class }

  context 'with an Array object' do
    let(:value) { %w[a] }

    context 'without a circular reference' do
      it_behaves_like 'IceNine::Freezer::Array.deep_freeze'
    end

    context 'with a circular reference' do
      before { value << value }

      it_behaves_like 'IceNine::Freezer::Array.deep_freeze'
    end
  end
end
