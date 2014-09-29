# encoding: utf-8

require 'spec_helper'
require 'ice_nine/freezer'
require 'ice_nine/freezer/no_freeze'
require 'ice_nine/freezer/nil_class'

describe IceNine::Freezer::NilClass, '.deep_freeze' do
  subject { object.deep_freeze(value) }

  let(:object) { described_class }

  context 'with a nil object' do
    let(:value) { nil }

    it_behaves_like 'IceNine::Freezer::NoFreeze.deep_freeze'
  end
end
