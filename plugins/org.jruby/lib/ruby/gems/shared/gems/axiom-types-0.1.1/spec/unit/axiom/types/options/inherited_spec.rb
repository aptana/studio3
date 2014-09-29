# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Options, '#inherited' do
  subject { descendant }

  let(:object) do
    Class.new(ancestor) do
      extend Options, DescendantsTracker
      accept_options :primitive, :coerce_method
      primitive ::String
    end
  end

  let(:ancestor) do
    Class.new
  end

  let(:descendant) do
    Class.new(object)
  end

  it 'delegates to the ancestor' do
    expect(ancestor).to receive(:inherited).twice
    subject
  end

  it 'adds the accepted option to the descendant' do
    subject
    expect(descendant).to respond_to(:primitive, :coerce_method)
  end

  it 'sets the default value for the descendant' do
    subject
    expect(descendant.primitive).to be(::String)
  end
end
