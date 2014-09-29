require 'spec_helper'

describe Coercer::Float, '#coerced?' do
  let(:object) { described_class.new }

  it_behaves_like 'Coercible::Coercer#coerced?' do
    let(:primitive_value)     { 1.2 }
    let(:non_primitive_value) { 'other' }
  end
end
