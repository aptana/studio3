#!/usr/bin/env ruby

# encoding: utf-8

# benchmark speed of deep freeze

$LOAD_PATH.unshift File.expand_path('../../lib', __FILE__)

require 'rbench'
require 'ice_nine'

# @return [Hash]
def self.nested(depth, width, array_length)
  hash = {}

  1.upto(width) do |n|
    hash[n.to_s] = n.to_s
  end

  unless depth == 1
    hash[(width - 1).to_s] = array_length.times.map { nested(depth - 1, width, array_length) }
    hash[width.to_s] = nested(depth - 1, width, array_length)
  end

  hash
end

hash = nested(3, 5, 500)
hash2 = nested(3, 5, 500)

RBench.run do
  report('deep_freeze')  { IceNine.deep_freeze(hash)   }
  report('deep_freeze!') { IceNine.deep_freeze!(hash2) }
end
