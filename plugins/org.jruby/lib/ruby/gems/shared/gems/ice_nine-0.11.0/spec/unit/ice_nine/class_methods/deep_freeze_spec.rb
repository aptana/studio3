# encoding: utf-8

require 'spec_helper'
require 'ice_nine'

describe IceNine, '.deep_freeze' do
  subject { object.deep_freeze(value) }

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

    it 'returns the object' do
      should be(value)
    end

    it 'leaves the object frozen' do
      expect { subject }.not_to change(value, :frozen?).from(true)
    end

    it 'freezes instance variables' do
      expect(subject.instance_variable_get(:@a)).to be_frozen
    end
  end
end
