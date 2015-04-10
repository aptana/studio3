# encoding: utf-8

require 'spec_helper'
require 'ice_nine'

describe IceNine::Freezer::Hash, '.deep_freeze' do
  subject { object.deep_freeze(value) }

  let(:object) { described_class }

  context 'with a Hash object having a default proc' do
    let(:value) do
      Hash.new { }.update(Object.new => Object.new)
    end

    it_behaves_like 'IceNine::Freezer::Hash.deep_freeze'

    it 'freezes the default proc' do
      expect(subject.default_proc).to be_frozen
    end
  end

  context 'with a Hash object having a default value' do
    let(:value) do
      Hash.new('').update(Object.new => Object.new)
    end

    it_behaves_like 'IceNine::Freezer::Hash.deep_freeze'

    it 'freezes the default value' do
      expect(subject.default).to be_frozen
    end

    context 'that is a circular reference' do
      before { value.default = value }

      it_behaves_like 'IceNine::Freezer::Hash.deep_freeze'

      it 'freezes the default value' do
        expect(subject.default).to be_frozen
      end
    end
  end

  context 'with a Hash object containing itself as a key' do
    let(:value) do
      value = {}
      value[value] = '1'
      value
    end

    it_behaves_like 'IceNine::Freezer::Hash.deep_freeze'
  end

  context 'with a Hash object containing itself as a value' do
    let(:value) do
      value = {}
      value['a'] = value
      value
    end

    it_behaves_like 'IceNine::Freezer::Hash.deep_freeze'
  end
end
