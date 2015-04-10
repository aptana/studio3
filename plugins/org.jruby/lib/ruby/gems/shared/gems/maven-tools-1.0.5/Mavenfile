#-*- mode: ruby -*-

gemspec

jruby_plugin( :minitest, :minispecDirectory =>"spec/*spec.rb" ) do
  execute_goals(:spec)
end

snapshot_repository :jruby, 'http://ci.jruby.org/snapshots/maven'

# (jruby-1.6.7 produces a lot of yaml errors parsing gemspecs)
properties( 'jruby.versions' => ['1.7.13','9000.dev-SNAPSHOT'].join(','),
            'jruby.modes' => ['1.9', '2.0','2.1'].join(','),
            # just lock the versions
            'jruby.version' => '1.7.13',
            'tesla.dump.pom' => 'pom.xml',
            'tesla.dump.readonly' => true )

# vim: syntax=Ruby
