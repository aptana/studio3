# encoding: utf-8

require 'spec_helper'
require 'ice_nine'
require 'delegate'

describe IceNine, '.deep_freeze!' do
  subject { object.deep_freeze!(value) }

  let(:object) { IceNine }

  context 'with a shallowly frozen value' do
    let(:value) { %w[a b].freeze }

    it 'does not deep freeze' do
      expect(subject.select(&:frozen?)).to be_empty
    end
  end

  it_should_behave_like 'IceNine.deep_freeze'
end
