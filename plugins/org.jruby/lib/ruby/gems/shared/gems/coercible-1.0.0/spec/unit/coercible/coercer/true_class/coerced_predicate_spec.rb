require 'spec_helper'

describe Coercer::TrueClass, '#coerced?' do
  let(:object) { described_class.new }

  it_behaves_like 'Coercible::Coercer#coerced?' do
    let(:primitive_value)     { true }
    let(:non_primitive_value) { false }
  end
end
