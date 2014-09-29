# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Infinity, '#succ' do
  subject { object.succ }

  let(:object) { described_class.instance }

  it { should be(object) }
end
