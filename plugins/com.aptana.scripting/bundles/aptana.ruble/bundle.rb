require 'ruble'

# its ruby, so this just adds commands/snippets in bundle (or replaces those with same name)
# many ruby files could add to a single bundle
bundle 'Aptana' do |bundle|
  bundle.author = "Sandip Chitale"
  bundle.copyright = <<END
© Copyright 2009 Aptana Inc. Distributed under GPLv3 and Aptana Source license.
END

  bundle.description = <<END
Aptana bundle.
END

  bundle.repository = "git://github.com/aptana/red_core.git"


end