# encoding: utf-8

require 'spec_helper'
require 'ice_nine/support/recursion_guard'

describe IceNine::RecursionGuard::Frozen, '#guard' do
  subject { object.guard(object_arg) { return_value } }

  let(:object)       { IceNine::RecursionGuard::Frozen.new }
  let(:object_arg)   { Object.new                          }
  let(:return_value) { double('return_value')              }

  context 'when the object_arg is not frozen' do
    it 'returns the expected value' do
      should be(return_value)
    end
  end

  context 'when the object_arg is frozen' do
    before do
      object_arg.freeze
    end

    it 'returns the expected value' do
      should be(object_arg)
    end
  end
end
