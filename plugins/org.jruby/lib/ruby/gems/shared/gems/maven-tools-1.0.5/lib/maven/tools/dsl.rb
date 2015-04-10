require 'fileutils'
require 'maven/tools/gemspec_dependencies'
require 'maven/tools/artifact'
require 'maven/tools/jarfile'
require 'maven/tools/versions'
require 'maven/tools/gemfile_lock'

module Maven
  module Tools
    module DSL

      def tesla( &block )
        @model = Model.new
        @model.model_version = '4.0.0'
        @model.name = ::File.basename( basedir )
        @model.group_id = 'no_group_id_given'
        @model.artifact_id = model.name
        @model.version = '0.0.0'
        @context = :project
        nested_block( :project, @model, block ) if block
        if @needs_torquebox
          if ! @model.repositories.detect { |r| r.id == 'rubygems-prereleases' }  && @model.dependencies.detect { |d| d.group_id == 'rubygems' && d.version.match( /-SNAPSHOT/ ) }
            
            @current = @model
            snapshot_repository(  'rubygems-prereleases',
                                  'http://rubygems-proxy.torquebox.org/prereleases' )
            @current = nil
          end
          @needs_torquebox = nil
        end
        result = @model
        @context = nil
        @model = nil
        result
      end

      def maven( val = nil, &block )
        if @context == nil
          tesla( &block )
        else
          @current.maven = val
        end
      end
      
      def model
        @model
      end

      def eval_pom( src, reference_file )
        @source = reference_file || '.'
        eval( src, nil, ::File.expand_path( @source ) )
      ensure
        @source = nil
        @basedir = nil
      end

      def basedir( basedir = nil )
        @basedir = basedir if basedir
        if @source
          @basedir ||= ::File.directory?( @source ) ? @source : 
            ::File.dirname( ::File.expand_path( @source ) )
        end
        @basedir ||= ::File.expand_path( '.' )
      end

      def artifact( a )
        if a.is_a?( String )
          a = Maven::Tools::Artifact.from_coordinate( a )
        end
        self.send a[:type].to_sym, a
      end

      def source(*args)
        warn "ignore source #{args}" if !(args[0].to_s =~ /^https?:\/\/rubygems.org/) && args[0] != :rubygems
      end

      def ruby( *args )
        # ignore
      end

      def path( *args )
        warn 'path block not implemented'
      end

      def git( *args )
        warn 'git block not implemented'
      end

      def is_jruby_platform( *args )
        args.flatten.detect { |a| :jruby == a.to_sym }
      end
      private :is_jruby_platform

      def platforms( *args )
        if is_jruby_platform( *args )
          yield
        end
      end

      def group( *args )
        @group = args
        yield
      ensure
        @group = nil
      end

      def gemfile( name = 'Gemfile', options = {} )
        if name.is_a? Hash
          options = name
          name = 'Gemfile'
        end
        name = ::File.join( basedir, name ) unless ::File.exists?( name )
        
        @inside_gemfile = true   
        # the eval might need those options for gemspec declaration
        lockfile = ::File.expand_path( name + '.lock' )
        if File.exists? lockfile
          pr = profile :gemfile do
            activation do
              file( :missing => name + '.lock' )
            end
            
            FileUtils.cd( basedir ) do
              f = ::File.expand_path( name )
              eval( ::File.read( f ), nil, f )
            end
          end
          @inside_gemfile = :gemfile
        else
          FileUtils.cd( basedir ) do
            f = ::File.expand_path( name )
            eval( ::File.read( f ), nil, f )
          end 
          @inside_gemfile = false
        end

        if @gemspec_args
          case @gemspec_args[ 0 ]
          when Hash
            gemspec( @gemspec_args[ 0 ].merge( options ) )
          when NilClass
            gemspec( @gemspec_args[ 0 ], options )
          else
            @gemspec_args[ 1 ].merge!( options ) 
            gemspec( *@gemspec_args )
          end
        else
          setup_gem_support( options )
          
          jruby_plugin!( :gem ) do
            execute_goal :initialize, :id => 'install gems'
          end
        end

        if pr && pr.dependencies.empty?
          if @current.respond_to? :delete
            @current.profiles.delete( pr )
          else
            @current.profiles.remove( pr )
          end
        end

        if pr && !pr.dependencies.empty?
          profile :gemfile_lock do
            activation do
              file( :exists => name + '.lock' )
            end
            locked = GemfileLock.new( lockfile )
            done = add_scoped_hull( locked, pr.dependencies )
            done += add_scoped_hull( locked, pr.dependencies,
                                        done, :provided )
            add_scoped_hull( locked, pr.dependencies, done, :test )
          end
        end

        if @has_path or @has_git
          gem 'bundler', VERSIONS[ :bundler_version ], :scope => :provided unless gem? 'bundler'
          jruby_plugin! :gem do
            execute_goal( :exec,
                          :id => 'bundle install', 
                          :filename => 'bundle',
                          :args => 'install' )
          end
        end
      ensure
        @inside_gemfile = nil
        @gemspec_args = nil
        @has_path = nil
        @has_git = nil
      end

      def add_scoped_hull( locked, deps, done = [], scope = nil )
        result = {}
        scope ||= "compile runtime default"
        scope = scope.to_s
        names = deps.select do |d|
          sc = d.scope || 'default'
          scope.match /#{sc}/
        end.collect { |d| d.artifact_id }
        locked.dependency_hull( names ).each do |name, version|
          result[ name ] = version unless done.member?( name )
        end
        unless result.empty?
          scope.sub!( / .*$/, '' )
          jruby_plugin!( :gem ) do
            execute_goal( :sets, 
                          :id => "install gem sets for #{scope}",
                          :phase => :initialize,
                          :scope => scope,
                          :gems => result )
          end
        end
        result.keys
      end
      private :add_scoped_hull

      def has_gem( name )
        ( model.artifact_id == name && model.group_id == 'rubygems' ) ||
          ( @current.dependencies.detect do |d|
              d.artifact_id == name && d.group_id == 'rubygems'
            end != nil )
      end
      private :has_gem

      def setup_gem_support( options, spec = nil, config = {} )
        unless model.properties.member?( 'project.build.sourceEncoding' )
          properties( 'project.build.sourceEncoding' => 'utf-8' )
        end
        if spec.nil?
          require_path = '.'
          name = ::File.basename( ::File.expand_path( '.' ) )
        else
          require_path = spec.require_path
          name = spec.name
        end
        
        unless options[ :only_metadata ]
        
          if ( nil == model.repositories.detect { |r| r.id == 'rubygems-releases' } && options[ :no_rubygems_repo ] != true )
            
            repository( 'rubygems-releases',
                        'http://rubygems-proxy.torquebox.org/releases' )
          end
          @needs_torquebox = true

          setup_jruby_plugins_version
        end

        if options.key?( :jar ) || options.key?( 'jar' )
          jarpath = options[ :jar ] || options[ 'jar' ]
          if jarpath
            jar = ::File.basename( jarpath ).sub( /.jar$/, '' )
            output = ::File.dirname( "#{require_path}/#{jarpath}" )
            output.sub!( /\/$/, '' )
          end
        else
          jar = "#{name}"
          output = "#{require_path}"
        end
        if options.key?( :source ) || options.key?( 'source' )
          source = options[ :source ] || options[ 'source' ]
          build do
            source_directory source
          end
        end
        # TODO rename "no_rubygems_repo" to "no_jar_support"
        if  options[ :no_rubygems_repo ] != true && jar && ( source ||
                    ::File.exists?( ::File.join( basedir, 'src', 'main', 'java' ) ) )
          unless spec.nil? || spec.platform.to_s.match( /java|jruby/ )
            warn "gem is not a java platform gem but has a jar and source"
          end
 
          plugin( :jar, VERSIONS[ :jar_plugin ],
                  :outputDirectory => output,
                  :finalName => jar ) do
            execute_goals :jar, :phase => 'prepare-package'
          end
          plugin( :clean, VERSIONS[ :clean_plugin ],
                  :filesets => [ { :directory => output,
                                   :includes => [ "#{jar}.jar", '*/**/*.jar' ] } ] )
          true
        else
          false
        end
      end
      private :setup_gem_support

      def setup_jruby( jruby, jruby_scope = :provided )
        warn "deprecated: use jruby DSL directly"
        jruby ||= VERSIONS[ :jruby_version ]

        # if jruby.match( /-SNAPSHOT/ ) != nil
        #   snapshot_repository( 'http://ci.jruby.org/snapshots/maven',
        #                        :id => 'jruby-snapshots' )
        # end
        scope( jruby_scope ) do
          if ( jruby < '1.6' )
            raise 'jruby before 1.6 are not supported'
          elsif ( jruby < '1.7' )
            warn 'jruby version below 1.7 uses jruby-complete'
            jar 'org.jruby:jruby-core', jruby
          elsif ( jruby.sub( /1\.7\./, '').to_i < 5 )
            jar 'org.jruby:jruby-core', jruby
          elsif jruby =~ /-no_asm$/
            pom 'org.jruby:jruby-noasm', jruby.sub( /-no_asm$/, '' )
          else
            pom 'org.jruby:jruby', jruby
          end
        end
      end
      private :setup_jruby
      
      def jarfile( file = 'Jarfile', options = {} )
        if file.is_a? Hash 
          options = file
          file = 'Jarfile'
        end
        unless file.is_a?( Maven::Tools::Jarfile )
          file = Maven::Tools::Jarfile.new( ::File.expand_path( file ) )
        end

        if options[ :skip_locked ] or not file.exists_lock?
          dsl = file.setup_unlocked( @current )
          # TODO this setup should be partly part of Jarfile
          jarfile_dsl( dsl )
        else
          file.locked.each do |dep|
            artifact( dep )
          end
          file.populate_unlocked do |dsl|
            dsl = file.setup_locked( @current )
            # TODO this setup should be partly part of Jarfile
            jarfile_dsl( dsl )
            dsl.parent.dependencies.each do |d|
              @current.dependencies << d if d.system_path
            end
          end
        end
      end
      
      def jarfile_dsl( dsl )
        dsl.repositories.each do |r|
          repository r.merge( {:id => r[:name] } )
        end
        dsl.snapshot_repositories.each do |r|
          snapshot_repository r.merge( {:id => r[:name] } )
        end
      end
      private :jarfile_dsl

      def gemspec( name = nil, options = {} )
        if @inside_gemfile == true
          @gemspec_args = [ name, options ]
          return
        end
        if name.is_a? Hash
          options = name
          name = nil
        end
        if name
          name = ::File.join( basedir, name )
        else name
          gemspecs = Dir[ ::File.join( basedir, "*.gemspec" ) ]
          raise "more then one gemspec file found" if gemspecs.size > 1
          raise "no gemspec file found" if gemspecs.size == 0
          name = gemspecs.first
        end
        spec = nil
        f = ::File.expand_path( name )
        spec_file = ::File.read( f )
        begin
          spec = Gem::Specification.from_yaml( spec_file )
        rescue Exception
          FileUtils.cd( basedir ) do
            # TODO jruby java user.dir
            spec = eval( spec_file, nil, f )
          end
        end
        
        self.spec( spec, name, options )
      end

      def spec( spec, name = nil, options = {} )
        name ||= "#{spec.name}-#{spec.version}.gemspec"

        @gemfile_options = nil

        if @context == :project
          build.directory = '${basedir}/pkg'
          version = spec.version.to_s
          if options[ :snapshot ] && spec.version.prerelease?
            version += '-SNAPSHOT'
          end
          id "rubygems:#{spec.name}:#{version}"
          name( spec.summary || spec.name )
          description spec.description
          url spec.homepage
          if spec.homepage && spec.homepage.match( /github.com/ )
            con = spec.homepage.sub( /http:/, 'https:' ).sub( /\/?$/, ".git" )
            scm :url => spec.homepage, :connection => con
          end

          spec.licenses.each do |l|
            license( l )
          end
          authors = [ spec.authors || [] ].flatten
          emails = [ spec.email || [] ].flatten
          authors.zip( emails ).each do |d|
            developer( :name => d[0], :email => d[1] )
          end
        end

        has_jars = setup_gem_support( options, spec )

        if @context == :project and not options[ :only_metadata ]
          packaging 'gem'
          if has_jars
            extension 'de.saumya.mojo:gem-with-jar-extension:${jruby.plugins.version}'
          else
            extension 'de.saumya.mojo:gem-extension:${jruby.plugins.version}'
          end
        end

        return if options[ :only_metadata ]

        config = { :gemspec => name.sub( /^#{basedir}\/?/, '' ) }
        if options[ :include_jars ] || options[ 'include_jars' ] 
          config[ :includeDependencies ] = true
          config[ :useRepositoryLayout ] = true
        end
        jruby_plugin!( :gem, config )

        deps = nil
        if @inside_gemfile.is_a? Symbol
          profile! @inside_gemfile do
            deps = all_deps( spec )
          end
        else
          deps = all_deps( spec )
        end
        
        deps.java_dependency_artifacts.each do |a|
          _dependency a
        end
      end

      def all_deps( spec )
        deps = Maven::Tools::GemspecDependencies.new( spec )
        deps.runtime.each do |d|
          gem d
        end
        unless deps.development.empty?
          scope :test do
            deps.development.each do |d|
              gem d
            end          
          end
        end
        deps
      end
      private :all_deps

      def licenses
        yield
      end
      alias :developers :licenses
      alias :contributors :licenses
      alias :mailing_lists :licenses
      alias :notifiers :licenses
      alias :dependencies :licenses
      alias :repositories :licenses
      alias :plugin_repositories :licenses
      alias :extensions :licenses
      alias :resources :licenses
      alias :testResources :licenses
      alias :plugins :licenses

      def build( &block )
        build = @current.build ||= Build.new
        nested_block( :build, build, block ) if block
        build
      end

      def organization( *args, &block )
        if @context == :project
          args, options = args_and_options( *args )
          org = ( @current.organization ||= Organization.new )
          org.name = args[ 0 ]
          org.url = args[ 1 ]
          fill_options( org, options )
          nested_block( :organization, org, block ) if block
          org
        else
          @current.organization = args[ 0 ]
        end
      end

      def license( *args, &block )
        args, options = args_and_options( *args )
        license = License.new
        license.name = args[ 0 ]
        license.url = args[ 1 ]
        fill_options( license, options )
        nested_block( :license, license, block ) if block
        @current.licenses << license
        license
      end

      def project( *args, &block )
        raise 'mixed up hierachy' unless @current == model
        args, options = args_and_options( *args )
        @current.name = args[ 0 ]
        @current.url = args[ 1 ]
        fill_options( @current, options )
        nested_block(:project, @current, block) if block
      end

      def id( *args )
        args, options = args_and_options( *args )
        if @context == :project
          # reset version + groupId
          @current.version = nil
          @current.group_id = nil
          fill_gav( @current, *args )
          fill_options( @current, options )
          reduce_id
        else
          @current.id = args[ 0 ]
        end
      end

      def site( *args, &block )
        site = Site.new
        args, options = args_and_options( *args )
        site.id = args[ 0 ]
        site.url = args[ 1 ]
        site.name = args[ 2 ]
        fill_options( site, options )
        nested_block( :site, site, block) if block
        @current.site = site
      end

      def source_control( *args, &block )
        scm = Scm.new
        args, options = args_and_options( *args )
        scm.connection = args[ 0 ]
        scm.developer_connection = args[ 1 ]
        scm.url = args[ 2 ]
        fill_options( scm, options )
        nested_block( :scm, scm, block ) if block
        @current.scm = scm
      end
      alias :scm :source_control

      def issue_management( *args, &block )
        issues = IssueManagement.new
        args, options = args_and_options( *args )
        issues.url = args[ 0 ]
        issues.system = args[ 1 ]
        fill_options( issues, options )
        nested_block( :issue_management, issues, block ) if block
        @current.issue_management = issues
      end
      alias :issues :issue_management

      def ci_management( *args, &block )
        ci = CiManagement.new
        args, options = args_and_options( *args )
        ci.url = args[ 0 ]
        fill_options( ci, options )
        nested_block( :ci_management, ci, block ) if block
        @current.ci_management = ci
      end
      alias :ci :ci_management

      def distribution_management( *args, &block )
        di = DistributionManagement.new
        args, options = args_and_options( *args )
        di.status = args[ 0 ]
        di.download_url = args[ 1 ]
        fill_options( di, options )
        nested_block( :distribution_management, di, block ) if block
        @current.distribution_management = di
      end

      def relocation( *args, &block )
        args, options = args_and_options( *args )
        relocation = fill_gav( Relocation, args.join( ':' ) )
        fill_options( relocation, options )
        nested_block( :relocation, relocation, block ) if block
        @current.relocation = relocation
      end

      def system( *args )
        if @current && @current.respond_to?( :system )
          @current.system = args[ 0 ]
        else
          Kernel.system( *args )
        end
      end

      def notifier( *args, &block )
        n = Notifier.new
        args, options = args_and_options( *args )
        n.type = args[ 0 ]
        n.address = args[ 1 ]
        fill_options( n, options )
        nested_block( :notifier, n, block ) if block
        @current.notifiers <<  n
        n
      end

      def mailing_list( *args, &block )
        list = MailingList.new
        args, options = args_and_options( *args )
        list.name = args[ 0 ]
        fill_options( list, options )
        nested_block( :mailing_list, list, block ) if block
        @current.mailing_lists <<  list
        list
      end

      def prerequisites( *args, &block )
        pre = Prerequisites.new
        args, options = args_and_options( *args )
        fill_options( pre, options )
        nested_block( :prerequisites, pre, block ) if block
        @current.prerequisites = pre
        pre
      end

      def archives( *archives )
        @current.archive = archives.shift
        @current.other_archives = archives
      end

      def other_archives( *archives )
        @current.other_archives = archives
      end

      def developer( *args, &block )
        dev = Developer.new
        args, options = args_and_options( *args )
        dev.id = args[ 0 ]
        dev.name = args[ 1 ]
        dev.url = args[ 2 ]
        dev.email = args[ 3 ]
        fill_options( dev, options )
        nested_block( :developer, dev, block ) if block
        @current.developers << dev
        dev
      end

      def contributor( *args, &block )
        con = Contributor.new
        args, options = args_and_options( *args )
        con.name = args[ 0 ]
        con.url = args[ 1 ]
        con.email = args[ 2 ]
        fill_options( con, options )
        nested_block( :contributor, con, block ) if block
        @current.contributors << con
        con
      end
      
      def roles( *roles )
        @current.roles = roles
      end

      def property( options )
        prop = ActivationProperty.new
        prop.name = options[ :name ] || options[ 'name' ]
        prop.value = options[ :value ] || options[ 'value' ]
        @current.property = prop
      end

      def file( options )
        file = ActivationFile.new
        file.missing = options[ :missing ] || options[ 'missing' ]
        file.exists = options[ :exists ] || options[ 'exists' ]
        @current.file = file
      end

      def activation( &block )
        activation = Activation.new
        nested_block( :activation, activation, block ) if block
        @current.activation = activation
      end

      def distribution( *args, &block )
        if @context == :license
          args, options = args_and_options( *args )
          @current.distribution = args[ 0 ]
          fill_options( @current, options )
        else
          distribution_management( *args, &block )
        end
      end

      def includes( *items )
        @current.includes = items.flatten
      end

      def excludes( *items )
        @current.excludes = items.flatten
      end

      def test_resource( options = {}, &block )
        # strange behaviour when calling specs from Rakefile
        return if @current.nil?
        resource = Resource.new
        fill_options( resource, options )
        nested_block( :test_resource, resource, block ) if block
        if @context == :project
          ( @current.build ||= Build.new ).test_resources << resource
        else
          @current.test_resources << resource
        end
      end

      def resource( options = {}, &block )
        resource = Resource.new
        fill_options( resource, options )
        nested_block( :resource, resource, block ) if block
        if @context == :project
          ( @current.build ||= Build.new ).resources << resource
        else
          @current.resources << resource
        end
      end
      
      def build_method( m, val )
        m = "#{m}=".to_sym
        if @context == :project
          ( @current.build ||= Build.new ).send m, val
        else
          @current.send m, val
        end
      end
      private :build_method

      def final_name( val )
        build_method( __method__, val )
      end

      def directory( val )
        build_method( __method__, val )
      end

      def output_directory( val )
        build_method( __method__, val )
      end
      def repository( *args, &block )
        do_repository( :repository=, *args, &block )
      end

      def plugin_repository( *args, &block )
        do_repository( :plugin, *args, &block )
      end

      def set_policy( key, enable, options )
        return unless options
        if map = options[ key ] || options[ key.to_s ]
          map[ :enabled ] = enable
        else
          options[ key ] = enable
        end
      end
      private :set_policy

      def snapshot_repository( *args, &block )
        unless @current.respond_to?( :snapshot_repository= )
          args, options = args_and_options( *args )
          set_policy( :releases, false, options )
          set_policy( :snapshots, true, options )
          args << options
        end
        do_repository( :snapshot_repository=, *args, &block )
      end

      def releases( config = nil, &block )
        @current.releases = repository_policy( @current.releases,
                                               config, &block )
      end

      def snapshots( config = nil, &block)
        @current.snapshots = repository_policy( @current.snapshots,
                                                config, &block )
      end

      def repository_policy( rp, config, &block )
        rp ||= RepositoryPolicy.new
        case config
        when Hash
          rp.enabled = config[ :enabled ] unless config[ :enabled ].nil?
          rp.update_policy = config[ :update ] || config[ :update_policy ] 
          rp.checksum_policy = config[ :checksum ] || config[ :checksum_policy ]
        when TrueClass
          rp.enabled = true
        when FalseClass
          rp.enabled = false
        else
          rp.enabled = 'true' == config unless config.nil?
        end
        nested_block( :repository_policy, rp, block ) if block
        rp
      end

      def enabled( value )
        @current.enabled = ( value.to_s == 'true' )
      end

      def args_and_options( *args )
        if args.last.is_a? Hash
          [ args[0..-2], args.last ]
        else
          [ args, {} ]
        end
      end

      def fill_options( receiver, options )
        options.each do |k,v|
          receiver.send( "#{k}=".to_sym, v )
        end
      end

      def fill( receiver, method, args )
        receiver.send( "#{method}=".to_sym, args )
      rescue
        begin
          old = @current
          @current = receiver
          # assume v is an array
          send( method, *args )
        ensure
          @current = old
        end
      end

      def inherit( *args, &block )
        args, options = args_and_options( *args )
        parent = ( @current.parent = fill_gav( Parent, *args ) )
        fill_options( parent, options )
        nested_block( :parent, parent, block ) if block
        reduce_id
        parent
      end
      alias :parent :inherit

      def properties(props = {})
        props.each do |k,v|
          @current.properties[k.to_s] = v.to_s
        end
        @current.properties
      end

      def extension( *args )
        build = if @context == :build
                  @current
                else
                  @current.build ||= Build.new
                end
        args, options = args_and_options( *args )
        ext = fill_gav( Extension, args.join( ':' ) )
        fill_options( ext, options )
        build.extensions << ext
        ext
      end

      def exclusion( *gav )
        gav = gav.join( ':' )
        ex = fill_gav( Exclusion, gav )
        @current.exclusions << ex
        ex
      end

      def setup_jruby_plugins_version
        if not @current.properties.key?( 'jruby.plugins.version' ) and
           not (@context == :profile and model.properties.key?( 'jruby.plugins.version' ) )
          properties( 'jruby.plugins.version' => VERSIONS[ :jruby_plugins ] )
        end
      end

      def do_jruby_plugin( method, *gav, &block )
        gav[ 0 ] = "de.saumya.mojo:#{gav[ 0 ]}-maven-plugin"
        if gav.size == 1 || gav[ 1 ].is_a?( Hash )
          setup_jruby_plugins_version
          gav.insert( 1, '${jruby.plugins.version}' )
        end
        send( method, *gav, &block )
      end

      def jruby_plugin( *gav, &block )
        do_jruby_plugin( :plugin, *gav, &block )
      end

      def jruby_plugin!( *gav, &block )
        do_jruby_plugin( :plugin!, *gav, &block )
      end

      def plugin!( *gav, &block )
        gav, options = plugin_gav( *gav )
        ga = gav.sub( /:[^:]*$/, '' )
        pl = plugins.detect do |p|
          "#{p.group_id}:#{p.artifact_id}" == ga
        end
        if pl
          do_plugin( false, pl, options, &block )
        else
          plugin = fill_gav( @context == :reporting ? ReportPlugin : Plugin,
                             gav)

          do_plugin( true, plugin, options, &block )
        end
      end

      def plugin_gav( *gav )
        if gav.last.is_a? Hash
          options = gav.last
          gav = gav[ 0..-2 ]
        else
          options = {}
        end
        unless gav.first.match( /:/ )
          gav[ 0 ] = "org.apache.maven.plugins:maven-#{gav.first}-plugin"
        end
        [ gav.join( ':' ), options ]
      end
      private :plugin_gav

      def plugins
        if @current.respond_to? :build
          @current.build ||= Build.new
          if @context == :overrides
            @current.build.plugin_management ||= PluginManagement.new
            @current.build.plugin_management.plugins
          else
            @current.build.plugins
          end
        else
          if @context == :overrides
            @current.plugin_management ||= PluginManagement.new
            @current.plugin_management.plugins
          else
            @current.plugins
          end
        end
      end
      private :plugins

      def plugin( *gav, &block )
        gav, options = plugin_gav( *gav )
        plugin = fill_gav( @context == :reporting ? ReportPlugin : Plugin,
                           gav)

        do_plugin( true, plugin, options, &block )
      end

      def do_plugin( add_plugin, plugin, options, &block )
        set_config( plugin, options )
        plugins << plugin if add_plugin
        nested_block(:plugin, plugin, block) if block
        plugin
      end
      private :do_plugin

      def overrides(&block)
        nested_block(:overrides, @current, block) if block
      end
      alias :plugin_management :overrides
      alias :dependency_management :overrides

      def execute( id = nil, phase = nil, options = {}, &block )
        if block
          raise 'can not be inside a plugin' if @current == :plugin
          if phase.is_a? Hash
            options = phase
          else
            options[ :phase ] = phase
          end
          if id.is_a? Hash
            options = id
          else
            options[ :id ] = id
          end
          options[ :taskId ] = options[ :id ] || options[ 'id' ]
          if @source
            options[ :nativePom ] = ::File.expand_path( @source ).sub( /#{basedir}./, '' )
          end
	  
          add_execute_task( options, &block )
        else
          # just act like execute_goals
          execute_goals( id )
        end
      end

      # hook for polyglot maven to register those tasks
      def add_execute_task( options, &block )
        @model.properties[ 'tesla.version' ] = VERSIONS[ :tesla_version ]
        plugin!( 'io.tesla.polyglot:tesla-polyglot-maven-plugin',
                 '${tesla.version}' ) do
          execute_goal( :execute, options )
          
          jar!( 'io.tesla.polyglot:tesla-polyglot-ruby',
                '${tesla.version}' )
        end
      end

      def retrieve_phase( options )
        if @phase
          if options[ :phase ] || options[ 'phase' ]
            raise 'inside phase block and phase option given'
          end
          @phase
        else
          options.delete( :phase ) || options.delete( 'phase' )
        end
      end
      private :retrieve_phase

      def execute_goal( goal, options = {}, &block )
        if goal.is_a? Hash
          execute_goals( goal, &block )
        else
          execute_goals( goal, options, &block )
        end
      end

      def execute_goals( *goals, &block )
        if goals.last.is_a? Hash
          options = goals.last
          goals = goals[ 0..-2 ]
        else
          options = {}
        end
        exec = Execution.new
        # keep the original default of id
        id = options.delete( :id ) || options.delete( 'id' )
        exec.id = id if id
        exec.phase = retrieve_phase( options )
        exec.goals = goals.collect { |g| g.to_s }
        set_config( exec, options )
        @current.executions << exec
        nested_block(:execution, exec, block) if block
        exec
      end

      def _dependency( type, *args, &block )
        do_dependency( false, type, *args, &block )
      end
      alias :dependency_artifact :_dependency
      def _dependency!( type, *args, &block )
        do_dependency( true, type, *args, &block )
      end
      alias :dependency_artifact! :_dependency!

      def _dependency?( type, *args )
        find_dependency( dependency_container,
                         retrieve_dependency( type, *args ) ) != nil
      end

      def find_dependency( container, dep )
        container.detect do |d|
          dep.group_id == d.group_id && dep.artifact_id == d.artifact_id && dep.classifier == d.classifier
        end
      end
         
      def dependency_set( bang, container, dep )
        if bang
          dd = do_dependency?( container, dep )
          if index = container.index( dd )
            container[ index ] = dep
          else
            container << dep
          end
        else
          container << dep
        end
      end

      def retrieve_dependency( type, *args )
        if args.empty?
          a = type
          type = a[ :type ]
          options = a
        elsif args[ 0 ].is_a?( ::Maven::Tools::Artifact )
          a = args[ 0 ]
          type = a[ :type ]
          options = a
        else
          args, options = args_and_options( *args )
          a = ::Maven::Tools::Artifact.from( type, *args )
        end
        options ||= {}
        d = fill_gav( Dependency, 
                      a ? a.gav : args.join( ':' ) )
        d.type = type.to_s
        # TODO maybe copy everything from options ?
        d.scope = options[ :scope ] if options[ :scope ]
        d.system_path = options[ :system_path ] if options[ :system_path ]
        
        d
      end

      def dependency_container
        if @context == :overrides
          @current.dependency_management ||= DependencyManagement.new
          @current.dependency_management.dependencies
        #elsif @context == :build
        #  @current.
        else
          @current.dependencies
        end
      end

      def do_dependency( bang, type, *args, &block )
        d = retrieve_dependency( type, *args )
        container = dependency_container

        if bang
          dd = find_dependency( container, d )
          if index = container.index( dd )
            container[ index ] = d
          else
            container << d
          end
        else
          container << d
        end
        
        args, options = args_and_options( *args )

        if options || @scope
          options ||= {}
          if @scope
            if options[ :scope ] || options[ 'scope' ]
              raise "scope block and scope option given"
            end
            options[ :scope ] = @scope
          end
          exclusions = options.delete( :exclusions ) ||
            options.delete( "exclusions" )
          case exclusions
          when Array
            exclusions.each do |v|
              v, opts = args_and_options( v )
              ex = fill_gav( Exclusion, *v )
              fill_options( ex, opts )
              d.exclusions << ex
            end
          when String
            d.exclusions << fill_gav( Exclusion, exclusions )
          end

          options.each do |k,v|
            d.send( "#{k}=".to_sym, v ) unless d.send( k.to_sym )
          end
        end
        nested_block( :dependency, d, block ) if block
        d
      end

      def scope( name )
        if @context == :dependency
          @current.scope = name
        else
          @scope = name
          yield
          @scope = nil
        end
      end

      def phase( name, &block )
        if @context != :plugin && block
          @phase = name
          yield
          @phase = nil
        else
          @current.phase = name
        end
      end

      def profile!( id, &block )
        profile = @current.profiles.detect { |p| p.id.to_s == id.to_s }
        if profile
          nested_block( :profile, profile, block ) if block
          profile
        else
          profile( id, &block )
        end
      end

      def profile( *args, &block )
        profile = Profile.new
        args, options = args_and_options( *args )
        profile.id = args[ 0 ]
        fill_options( profile, options )
        @current.profiles << profile
        nested_block( :profile, profile, block ) if block
        profile
      end

      def dependency( *args, &block )
        dep = Dependency.new
        args, options = args_and_options( *args )
        dep.group_id = args[ 0 ]
        dep.artifact_id = args[ 1 ]
        dep.version = args[ 2 ]
        dep.type = :jar
        fill_options( dep, options )
        nested_block( :dependency, dep, block ) if block
        dependency_container << dep
        dep
      end

      def report_set( *reports, &block )
        set = ReportSet.new
        case reports.last
        when Hash
          options = reports.last
          reports = reports[ 0..-2 ]
          id = options.delete( :id ) || options.delete( 'id' )
          set.id = id if id
          inherited = options.delete( :inherited ) ||
            options.delete( 'inherited' )
          set.inherited = inherited if inherited
        end
        set_config( set, options )
        set.reports = reports#.to_java
        @current.report_sets << set
      end

      def reporting( &block )
        reporting = Reporting.new
        @current.reporting = reporting
        nested_block( :reporting, reporting, block ) if block
      end
      
      def gem?( name )
        @current.dependencies.detect do |d|
          d.group_id == 'rubygems' && d.artifact_id == name && d.type == :gem
        end
      end

      def jar!( *args )
        _dependency!( :jar, *args )
      end

      def gem( *args )
        do_gem( false, *args )
      end
      
      # TODO useful ?
      def gem!( *args )
        do_gem( true, *args )
      end

      def do_gem( bang, *args )
        # in some setup that gem could overload the Kernel gem
        return if @current.nil?
        unless args[ 0 ].match( /:/ )
          args[ 0 ] = "rubygems:#{args[ 0 ] }"
        end
        if args.last.is_a?(Hash)
          options = args.last
        elsif @group
          options = {}
          args << options
        end
        if options
          # on ruby-maven side we ignore the require option
          options.delete( :require )
          options.delete( 'require' )

          if options.key?( :git )
            @has_git = true
          elsif options.key?( :path )
            @has_path = true
          else
            platform = options.delete( :platform ) || options.delete( 'platform' ) || options.delete( :platforms ) || options.delete( 'platforms' )
            group = options.delete( :groups ) || options.delete( 'groups' ) ||  options.delete( :group ) || options.delete( 'group' ) || @group
            if group
              group = [ group ].flatten.each { |g| g.to_sym }
              if group.member? :development
                options[ :scope ] = :provided
              elsif group.member? :test
                options[ :scope ] = :test 
              end
            end
            if platform.nil? || is_jruby_platform( platform )
              options[ :version ] = '[0,)' if args.size == 2 && options[ :version ].nil? && options[ 'version' ].nil?
              do_dependency( bang, :gem, *args )
            end
          end
        else
          args << { :version => '[0,)' } if args.size == 1
          do_dependency( bang, :gem, *args )
        end
      end

      def local( path, options = {} )
        path = ::File.expand_path( path )
        _dependency( :jar,
                    Maven::Tools::Artifact.new_local( path, :jar, options ) )
      end

      def method_missing( method, *args, &block )
        if @context
          m = "#{method}=".to_sym
          if @current.respond_to? m
            #p @context
            #p m
            #p args
            begin

              if defined?(JRUBY_VERSION) and
                  not RUBY_VERSION =~ /1.8/ and
                  args.size > 1

                @current.send( m, args, &block )

              else
                @current.send( m, *args, &block )
              end
            rescue TypeError
              # assume single argument
              @current.send( m, args[0].to_s, &block )              
            rescue ArgumentError
              begin
                @current.send( m, args )
              rescue ArgumentError => e
                if @current.respond_to? method
                  @current.send( method, *args )
                end
              end
            end
            @current
          else
            begin
            # if ( args.size > 0 &&
            #      args[0].is_a?( String ) &&
            #      args[0] =~ /^[${}0-9a-zA-Z._-]+(:[${}0-9a-zA-Z._-]+)+$/ ) ||
            #     ( args.size == 1 && args[0].is_a?( Hash ) )
              case method.to_s[ -1 ]
              when '?'
                _dependency?( method.to_s[0..-2].to_sym, *args, &block )
              when '!'
                _dependency!( method.to_s[0..-2].to_sym, *args, &block  )
              else
                _dependency( method, *args, &block )
              end
              # elsif @current.respond_to? method
              #   @current.send( method, *args )
              #   @current
#            else
            rescue => e
              p @context
              p m
              p args
              raise e
            end
          end
        else
          super
        end
      end

      def xml( xml )
        def xml.to_xml
          self
        end
        xml
      end

      def prepare_config( receiver, options )
        return unless options
        inherited = options.delete( 'inherited' ) || options.delete( :inherited )
        receiver.inherited = inherited if inherited
      end

      def set_config( receiver, options )
        prepare_config( receiver, options )
        receiver.configuration = options
      end

      def configuration( v )
        if @context == :notifier
          @current.configuration = v
        else
          set_config( @current, v )
        end
      end

      private

      def do_repository( method, *args, &block )
        args, options = args_and_options( *args )
        if @current.respond_to?( method )
          r = DeploymentRepository.new
        else
          r = Repository.new
          c = options.delete( :snapshots )
          c = options.delete( 'snapshots' ) if c.nil?
          unless c.nil?
            r.snapshots = repository_policy( r.snapshots, c )
          end
          c = options.delete( :releases )
          c = options.delete( 'releases' ) if c.nil?
          unless c.nil?
            r.releases = repository_policy( r.releases, c )
          end
        end
        if options.size == 1 && args.size == 1
          warn "deprecated repository, use :url => '...'"
          # allow old method signature
          r.url = args[ 0 ]          
        else
          r.id = args[ 0 ]
          r.url = args[ 1 ]
          r.name = args[ 2 ]
        end
        fill_options( r, options )
        nested_block( :repository, r, block ) if block
        case method
        when :plugin
          @current.plugin_repositories << r
        else
          if @current.respond_to?( method )
            @current.send method, r
          else
            @current.repositories << r
          end
        end
      end

      def reduce_id
        if parent = @current.parent
          @current.version = nil if parent.version == @current.version
          @current.group_id = nil if parent.group_id == @current.group_id
        end
      end

      def nested_block(context, receiver, block)
        old_ctx = @context
        old = @current

        @context = context
        @current = receiver

        block.call

        @current = old
        @context = old_ctx
      end

      def fill_gav(receiver, *gav)
        if receiver.is_a? Class
          receiver = receiver.new
        end
        if gav.size > 0
          gav = gav[0].split(':') if gav.size == 1
          case gav.size
          when 0
            # do nothing - will be filled later
          when 1
            receiver.artifact_id = gav[0]
          when 2
            if gav[ 0 ] =~ /:/
              receiver.group_id, receiver.artifact_id = gav[ 0 ].split /:/
              receiver.version = gav[ 1 ]
            else
              receiver.group_id, receiver.artifact_id = gav
            end
          when 3
            receiver.group_id, receiver.artifact_id, receiver.version = gav
          when 4
            receiver.group_id, receiver.artifact_id, receiver.version, receiver.classifier = gav
          else
            raise "can not assign such an array #{gav.inspect}"
          end
        end
        receiver
      end
    end
  end
end
