# descendants_tracker

[![Gem Version](https://badge.fury.io/rb/descendants_tracker.png)][gem]
[![Build Status](https://secure.travis-ci.org/dkubb/descendants_tracker.png?branch=master)][travis]
[![Dependency Status](https://gemnasium.com/dkubb/descendants_tracker.png)][gemnasium]
[![Code Climate](https://codeclimate.com/github/dkubb/descendants_tracker.png)][codeclimate]
[![Coverage Status](https://coveralls.io/repos/dkubb/descendants_tracker/badge.png?branch=master)][coveralls]

[gem]: https://rubygems.org/gems/descendants_tracker
[travis]: https://travis-ci.org/dkubb/descendants_tracker
[gemnasium]: https://gemnasium.com/dkubb/descendants_tracker
[codeclimate]: https://codeclimate.com/github/dkubb/descendants_tracker
[coveralls]: https://coveralls.io/r/dkubb/descendants_tracker

Small module to track descendants in an unobtrusive way.

## Examples

``` ruby
class Foo
  extend DescendantsTracker
end

class Bar < Foo
end

Foo.descendants # => [Bar]
```

## Credits

* Dan Kubb ([dkubb](https://github.com/dkubb))
* Piotr Solnica ([solnic](https://github.com/solnic))
* Markus Schirp ([mbj](https://github.com/mbj))

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for details.

## Copyright

Copyright &copy; 2012-2013 Dan Kubb (author)
Copyright &copy; 2011-2012 Piotr Solnica (source maintainer)
Copyright &copy; 2012 Markus Schirp (packaging)

See LICENSE for details.
