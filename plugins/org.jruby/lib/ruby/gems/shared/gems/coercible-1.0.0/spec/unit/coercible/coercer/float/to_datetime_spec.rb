require 'spec_helper'

describe Coercer::Float, '#to_float' do
  subject { object.to_datetime(value) }

  let(:object) { described_class.new }
  let(:value)  { 1361036672.12 }

  specify do
    expect(subject.strftime('%Y-%m-%d %H:%M:%S.%L')).to eql('2013-02-16 17:44:32.120')
  end
end
