maven tools 
===========

* [![Build Status](https://secure.travis-ci.org/torquebox/maven-tools.png)](http://travis-ci.org/torquebox/maven-tools)
* [![Code Climate](https://codeclimate.com/badge.png)](https://codeclimate.com/github/torquebox/maven-tools)

Note on Ruby-1.8
----------------

ordering is important within the pom.xml since it carry info on the sequence of execution. jruby and ruby-1.9 do iterate in same order as the keys gets included, that helps to copy the order of declaration from the ruby DSL over to pom.xml. with ruby-1.8 the hash behaviour is different and since ruby-1.8 is end of life there is no support for ruby-1.8. though it might just works fine on simple setup.

Contributing
------------

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Added some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

meta-fu
-------

enjoy :) 

