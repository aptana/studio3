require 'spec_helper'

describe Coercer::Symbol, '#coerced?' do
  let(:object) { described_class.new }

  it_behaves_like 'Coercible::Coercer#coerced?' do
    let(:primitive_value)     { :symbol }
    let(:non_primitive_value) { 'other' }
  end
end
