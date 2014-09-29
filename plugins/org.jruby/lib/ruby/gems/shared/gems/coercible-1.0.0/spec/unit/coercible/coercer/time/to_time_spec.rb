require 'spec_helper'

describe Coercer::Time, '.to_time' do
  subject { object.to_time(time) }

  let(:object) { described_class.new        }
  let(:time)   { Time.local(2012, 1, 1) }

  it { should equal(time) }
end
