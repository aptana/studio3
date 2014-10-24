require 'spec_helper'

describe Coercer::DateTime, '.to_datetime' do
  subject { object.to_datetime(date_time) }

  let(:object)    { described_class.new          }
  let(:date_time) { DateTime.new(2012, 1, 1) }

  it { should equal(date_time) }
end
