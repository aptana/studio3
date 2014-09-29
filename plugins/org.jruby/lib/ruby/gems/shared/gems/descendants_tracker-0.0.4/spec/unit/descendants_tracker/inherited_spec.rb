# encoding: utf-8

require 'spec_helper'

describe DescendantsTracker, '#inherited' do
  subject { Class.new(object) }

  let!(:object)    { Class.new(superklass).extend(self.class.described_class) }
  let(:superklass) { Class.new                                                }

  it 'delegates to the superclass #inherited method' do
    superklass.should_receive(:inherited) do |descendant|
      expect(descendant).to be_instance_of(Class)
      expect(descendant.ancestors).to include(object)
    end
    subject
  end

  it 'adds the descendant' do
    expect(object.descendants).to include(subject)
  end

  it 'sets up descendants in the child class' do
    expect(subject.descendants).to eql([])
  end
end
