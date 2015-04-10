module Coercible
  class Coercer

    # Coerce Hash values
    class Hash < Object
      primitive ::Hash

      TIME_SEGMENTS = [ :year, :month, :day, :hour, :min, :sec ].freeze

      # Creates a Time instance from a Hash
      #
      # Valid keys are: :year, :month, :day, :hour, :min, :sec
      #
      # @param [Hash] value
      #
      # @return [Time]
      #
      # @api private
      def to_time(value)
        ::Time.local(*extract(value))
      end

      # Creates a Date instance from a Hash
      #
      # Valid keys are: :year, :month, :day, :hour
      #
      # @param [Hash] value
      #
      # @return [Date]
      #
      # @api private
      def to_date(value)
        ::Date.new(*extract(value).first(3))
      end

      # Creates a DateTime instance from a Hash
      #
      # Valid keys are: :year, :month, :day, :hour, :min, :sec
      #
      # @param [Hash] value
      #
      # @return [DateTime]
      #
      # @api private
      def to_datetime(value)
        ::DateTime.new(*extract(value))
      end

      private

      # Extracts the given args from a Hash
      #
      # If a value does not exist, it uses the value of Time.now
      #
      # @param [Hash] value
      #
      # @return [Array]
      #
      # @api private
      def extract(value)
        now = ::Time.now

        TIME_SEGMENTS.map do |segment|
          val = value.fetch(segment, now.public_send(segment))
          coercers[val.class].to_integer(val)
        end
      end

    end # class Hash

  end # class Coercer
end # module Coercible
