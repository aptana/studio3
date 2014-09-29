require 'spec_helper'

describe Coercer::String, '.config' do
  subject { described_class.config }

  its(:boolean_map) { should be(described_class::BOOLEAN_MAP) }
end
