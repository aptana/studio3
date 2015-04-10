# encoding: utf-8

require 'spec_helper'
require 'ice_nine/freezer/no_freeze'

describe IceNine::Freezer::NoFreeze, '.deep_freeze' do
  subject { object.deep_freeze(value) }

  let(:object) { described_class }
  let(:value)  { double('value') }

  it_behaves_like 'IceNine::Freezer::NoFreeze.deep_freeze'
end
