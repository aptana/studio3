# encoding: utf-8

require 'spec_helper'
require 'ice_nine/support/recursion_guard'

describe IceNine::RecursionGuard::ObjectSet, '#guard' do
  let(:object)       { IceNine::RecursionGuard::ObjectSet.new }
  let(:object_arg)   { Object.new                             }
  let(:return_value) { double('return_value')                 }

  context 'when the block is not recursive' do
    subject { object.guard(object_arg) { return_value } }

    it 'returns the expected value' do
      should be(return_value)
    end
  end

  context 'when the block is recursive' do
    subject do
      object.guard(object_arg) do
        expect(subject).to be(object_arg)
        return_value
      end
    end

    it 'returns the expected value' do
      should be(return_value)
    end
  end
end
