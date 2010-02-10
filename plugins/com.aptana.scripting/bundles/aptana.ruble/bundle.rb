require 'ruble'

bundle 'Aptana' do |bundle|
  bundle.author = "Sandip Chitale, Andrew Shebanow"
  bundle.copyright = "© Copyright 2010 Aptana Inc. Distributed under GPLv3 and Aptana Source license."
  bundle.description = "Aptana bundle."

  bundle.menu "Aptana" do |support_menu|
    support_menu.command "Report Bug..."
    support_menu.command "Send Feedback..."
  end
end
