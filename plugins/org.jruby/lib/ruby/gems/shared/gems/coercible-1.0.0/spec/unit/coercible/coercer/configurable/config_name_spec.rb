require 'spec_helper'

describe Coercer::Configurable, '.config_name' do
  subject { object.config_name }

  let(:object)  {
    Class.new {
      extend Coercer::Configurable, Options
      config_keys [ :one, :two ]

      def self.name
        "Some::Class::Test"
      end
    }
  }

  it { should be(:test) }
end
