# encoding: utf-8

require 'spec_helper'
require 'ice_nine'

describe IceNine, '.deep_freeze!' do
  subject { object.deep_freeze!(value) }

  let(:object) { IceNine    }
  let(:value)  { Object.new }

  context 'when the object is not frozen' do
    it_behaves_like 'IceNine::Freezer::Object.deep_freeze'
  end

  context 'when the object is frozen' do
    before do
      value.instance_eval { @a = '1' }
      value.freeze
    end

    it_behaves_like 'IceNine::Freezer::NoFreeze.deep_freeze'
  end
end
