require 'spec_helper'

describe Coercer::Integer, '#coerced?' do
  let(:object) { described_class.new }

  it_behaves_like 'Coercible::Coercer#coerced?' do
    let(:primitive_value)     { 1 }
    let(:non_primitive_value) { 1.0 }
  end
end
