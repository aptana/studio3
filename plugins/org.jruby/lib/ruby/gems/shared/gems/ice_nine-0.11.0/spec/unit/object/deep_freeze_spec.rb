# encoding: utf-8

require 'spec_helper'
require 'ice_nine'
require 'ice_nine/core_ext/object'

describe Object, '#deep_freeze' do
  subject { value.deep_freeze }

  let(:value) { described_class.new }

  it_behaves_like 'IceNine::Freezer::Object.deep_freeze'
end
