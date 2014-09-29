ice_nine
========

Deep freeze ruby objects

[![Gem Version](https://badge.fury.io/rb/ice_nine.png)][gem]
[![Build Status](https://secure.travis-ci.org/dkubb/ice_nine.png?branch=master)][travis]
[![Dependency Status](https://gemnasium.com/dkubb/ice_nine.png)][gemnasium]
[![Code Climate](https://codeclimate.com/github/dkubb/ice_nine.png)][codeclimate]
[![Coverage Status](https://coveralls.io/repos/dkubb/ice_nine/badge.png?branch=master)][coveralls]

[gem]: https://rubygems.org/gems/ice_nine
[travis]: https://travis-ci.org/dkubb/ice_nine
[gemnasium]: https://gemnasium.com/dkubb/ice_nine
[codeclimate]: https://codeclimate.com/github/dkubb/ice_nine
[coveralls]: https://coveralls.io/r/dkubb/ice_nine

Examples
--------

```ruby
# Deep freezes most kinds of objects
hash   = IceNine.deep_freeze('a' => '1')
array  = IceNine.deep_freeze([ 'a', 'b', 'c' ])
range  = IceNine.deep_freeze('a'..'z')
struct = IceNine.deep_freeze(Struct.new(:a, :b).new('a', 'b'))
object = IceNine.deep_freeze(Object.new)
user   = IceNine.deep_freeze(User.new(name: 'dkubb'))

# Add core extension for Object#deep_freeze (not required by default)
require 'ice_nine/core_ext/object'

object = Object.new
object.deep_freeze

# Faster deep freeze that skips walking frozen objects
object = IceNine.deep_freeze!(Object.new)
```

Contributing
------------

See [CONTRIBUTING.md](CONTRIBUTING.md) for details.

Copyright
---------

Copyright &copy; 2012-2013 Dan Kubb. See LICENSE for details.
