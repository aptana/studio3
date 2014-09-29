# encoding: utf-8

require 'spec_helper'

describe Axiom::Types::Object, '.inspect' do
  subject { object.inspect }

  let(:object) { described_class }

  it { should eql("Axiom::Types::Object (#{object.primitive})") }
end
