require 'spec_helper'

describe Coercer::FalseClass, '#coerced?' do
  let(:object) { described_class.new }

  it_behaves_like 'Coercible::Coercer#coerced?' do
    let(:primitive_value)     { false }
    let(:non_primitive_value) { 'other' }
  end
end
