# encoding: utf-8

require 'spec_helper'

describe Axiom::Types, '.finalize' do
  subject { object.finalize }

  let(:object)      { described_class               }
  let(:descendant)  { Class.new(Axiom::Types::Type) }

  it_should_behave_like 'a command method'

  it 'finalizes the descendants' do
    expect(descendant).to receive(:finalize)
    subject
  end
end
