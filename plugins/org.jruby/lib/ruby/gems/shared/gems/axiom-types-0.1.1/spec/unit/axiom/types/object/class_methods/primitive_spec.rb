# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Object, '.primitive' do
  let(:object)    { Class.new(described_class) }
  let(:primitive) { ::BasicObject              }

  context 'with no arguments' do
    subject { object.primitive }

    it { should be(primitive) }
  end

  context 'with a class' do
    subject { object.primitive(klass) }

    let(:klass) { ::String }

    it_should_behave_like 'a command method'

    it 'sets the primitive' do
      expect { subject }.to change { object.primitive.object_id }
        .from(primitive.object_id).to(klass.object_id)
    end
  end
end
