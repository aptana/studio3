# encoding: utf-8

require 'spec_helper'
require 'ice_nine/freezer'
require 'ice_nine/freezer/no_freeze'
require 'ice_nine/freezer/false_class'

describe IceNine::Freezer::FalseClass, '.deep_freeze' do
  subject { object.deep_freeze(value) }

  let(:object) { described_class }

  context 'with a false object' do
    let(:value) { false }

    it_behaves_like 'IceNine::Freezer::NoFreeze.deep_freeze'
  end
end
