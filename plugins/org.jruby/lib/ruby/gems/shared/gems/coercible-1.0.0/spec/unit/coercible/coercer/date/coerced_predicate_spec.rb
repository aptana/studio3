require 'spec_helper'

describe Coercer::Date, '#coerced?' do
  let(:object) { described_class.new }

  it_behaves_like 'Coercible::Coercer#coerced?' do
    let(:primitive_value)     { Date.new }
    let(:non_primitive_value) { 'other' }
  end
end
