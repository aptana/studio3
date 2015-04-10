# encoding: utf-8

require 'spec_helper'
require 'ice_nine'

describe IceNine::Freezer::Struct, '.deep_freeze' do
  subject { object.deep_freeze(value) }

  let(:object) { described_class }

  context 'with a Struct' do
    let(:value) { klass.new('1') }
    let(:klass) { Struct.new(:a) }

    it_behaves_like 'IceNine::Freezer::Array.deep_freeze'
  end
end
