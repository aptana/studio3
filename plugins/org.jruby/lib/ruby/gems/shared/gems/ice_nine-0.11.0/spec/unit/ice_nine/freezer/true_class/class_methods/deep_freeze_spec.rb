# encoding: utf-8

require 'spec_helper'
require 'ice_nine/freezer'
require 'ice_nine/freezer/object'
require 'ice_nine/freezer/no_freeze'
require 'ice_nine/freezer/true_class'

describe IceNine::Freezer::TrueClass, '.deep_freeze' do
  subject { object.deep_freeze(value) }

  let(:object) { described_class }

  context 'with a true object' do
    let(:value) { true }

    it_behaves_like 'IceNine::Freezer::NoFreeze.deep_freeze'
  end
end
