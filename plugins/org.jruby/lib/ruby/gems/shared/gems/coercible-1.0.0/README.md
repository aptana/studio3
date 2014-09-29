# Coercible

[![Build Status](https://travis-ci.org/solnic/coercible.png?branch=master)](https://travis-ci.org/solnic/coercible)
[![Code Climate](https://codeclimate.com/github/solnic/coercible.png)](https://codeclimate.com/github/solnic/coercible)
[![Dependency Status](https://gemnasium.com/solnic/coercible.png)](https://gemnasium.com/solnic/coercible)

## Installation

Add this line to your application's Gemfile:

    gem 'coercible'

And then execute:

    $ bundle

Or install it yourself as:

    $ gem install coercible

## Usage

Coercible gives you access to coercer objects where each object is responsible
for coercing only one type into other types. For example a string coercer knows
only how to coerce string objects, integer coercer knows only how to coerce integers
etc.

Here's the most basic example:

```ruby
coercer = Coercible::Coercer.new

# coerce a string to a date
coercer[String].to_date('2012/12/25') # => #<Date: 4912573/2,0,2299161>

# coerce a string to a boolean value
coercer[String].to_boolean('yes') # => true

# you got the idea :)
```

For more control you can configure your coercer like that:

``` ruby
# build coercer instance
coercer = Coercible::Coercer.new do |config|
  config.string.boolean_map = { 'yup' => true, 'nope' => false }
end

# coerce a string to boolean
coercer[String].to_boolean('yup') # => true
coercer[String].to_boolean('nope') # => false
```

Note that at the moment only Integer and String are configurable. More configurable
coercers will be added later whenever we find good usecases.

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request
