# axiom-types

Define types with optional constraints for use within axiom and other libraries.

[![Gem Version](https://badge.fury.io/rb/axiom-types.png)][gem]
[![Build Status](https://secure.travis-ci.org/dkubb/axiom-types.png?branch=master)][travis]
[![Dependency Status](https://gemnasium.com/dkubb/axiom-types.png)][gemnasium]
[![Code Climate](https://codeclimate.com/github/dkubb/axiom-types.png)][codeclimate]
[![Coverage Status](https://coveralls.io/repos/dkubb/axiom-types/badge.png?branch=master)][coveralls]

[gem]: https://rubygems.org/gems/axiom-types
[travis]: https://travis-ci.org/dkubb/axiom-types
[gemnasium]: https://gemnasium.com/dkubb/axiom-types
[codeclimate]: https://codeclimate.com/github/dkubb/axiom-types
[coveralls]: https://coveralls.io/r/dkubb/axiom-types

## Examples

```ruby
# Setup constraints for all defined types
Axiom::Types.finalize

# Create Name subtype
Name = Axiom::Types::String.new do
  minimum_length 1
  maximum_length 30
end

# Test if the string is a member of the type
Name.include?('a')       # => true
Name.include?('a' * 30)  # => true
Name.include?('')        # => false
Name.include?('a' * 31)  # => false
```

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for details.

## Copyright

Copyright &copy; 2013 Dan Kubb. See LICENSE for details.
