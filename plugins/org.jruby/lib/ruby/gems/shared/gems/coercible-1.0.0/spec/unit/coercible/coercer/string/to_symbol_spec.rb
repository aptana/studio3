require 'spec_helper'

describe Coercer::String, '.to_symbol' do
  subject { described_class.new.to_symbol(value) }

  let(:value) { 'value' }

  it { should be(:value) }
end
