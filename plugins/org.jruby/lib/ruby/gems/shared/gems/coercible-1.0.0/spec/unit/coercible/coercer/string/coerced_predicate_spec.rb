require 'spec_helper'

describe Coercer::String, '#coerced?' do
  let(:object) { described_class.new }

  it_behaves_like 'Coercible::Coercer#coerced?' do
    let(:primitive_value)     { 'a string' }
    let(:non_primitive_value) { :a_symbol }
  end
end
