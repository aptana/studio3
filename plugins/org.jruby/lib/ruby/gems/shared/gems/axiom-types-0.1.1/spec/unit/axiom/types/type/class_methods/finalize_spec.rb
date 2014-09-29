# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Type, '.finalize' do
  subject { object.finalize }

  let(:object) do
    Class.new(described_class) do
      constraint Tautology
    end
  end

  it_should_behave_like 'a command method'
  it_should_behave_like 'an idempotent method'

  it { should be_frozen }

  its(:constraint) { should be_frozen }
end
