require "ruble"

bundle "bundleWithPlatformSpecifierCommands" do

  command "implicitAllPlatformString" do |cmd|
    cmd.scope = "foo"
    cmd.invoke = "echo 'implicitAllPlatformString'"
  end

  command "explicitAllPlatformString" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.all= "echo 'explicitAllPlatformString'"
  end

  command "macAndImplicitAllPlatformString" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.mac = "echo 'macAndImplicitAllPlatformString'"
    cmd.invoke = "echo 'macAndImplicitAllPlatformString'"
  end

  command "macAndExplicitAllPlatformString" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.mac = "echo 'macAndExplicitAllPlatformString'"
    cmd.invoke = "echo 'macAndExplicitAllPlatformString'"
  end

  command "macOnlyPlatformString" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.mac = "echo 'macOnlyPlatformString'"
  end

  command "windowsAndImplicitAllPlatformString" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.windows = "echo 'windowsAndImplicitAllPlatformString'"
    cmd.invoke = "echo 'windowsAndImplicitAllPlatformString'"
  end

  command "windowsAndExplicitAllPlatformString" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.windows = "echo 'windowsAndExplicitAllPlatformString'"
    cmd.invoke = "echo 'windowsAndExplicitAllPlatformString'"
  end

  command "windowsOnlyPlatformString" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.windows = "echo 'windowsOnlyPlatformString'"
  end

  command "linuxAndImplicitAllPlatformString" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.linux = "echo 'linuxAndImplicitAllPlatformString'"
    cmd.invoke = "echo 'linuxAndImplicitAllPlatformString'"
  end

  command "linuxAndExplicitAllPlatformString" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.linux = "echo 'linuxAndExplicitAllPlatformString'"
    cmd.invoke = "echo 'linuxAndExplicitAllPlatformString'"
  end

  command "linuxOnlyPlatformString" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.linux = "echo 'linuxOnlyPlatformString'"
  end

  command "unixAndImplicitAllPlatformString" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.unix = "echo 'unixAndImplicitAllPlatformString'"
    cmd.invoke = "echo 'unixAndImplicitAllPlatformString'"
  end

  command "unixAndExplicitAllPlatformString" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.unix = "echo 'unixAndExplicitAllPlatformString'"
    cmd.invoke = "echo 'unixAndExplicitAllPlatformString'"
  end

  command "unixOnlyPlatformString" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.unix = "echo 'unixOnlyPlatformString'"
  end

  command "implicitAllPlatformBlock" do |cmd|
    cmd.scope = "foo"
    cmd.invoke do 'implicitAllPlatformBlock' end
  end

  command "explicitAllPlatformBlock" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.all do 'explicitAllPlatformBlock' end
  end

  command "macAndImplicitAllPlatformBlock" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.mac  do 'macAndImplicitAllPlatformBlock' end
    cmd.invoke do 'macAndImplicitAllPlatformBlock' end
  end

  command "macAndExplicitAllPlatformBlock" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.mac  do 'macAndExplicitAllPlatformBlock' end
    cmd.invoke do 'macAndExplicitAllPlatformBlock' end
  end

  command "macOnlyPlatformBlock" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.mac do 'macOnlyPlatformBlock' end
  end

  command "windowsAndImplicitAllPlatformBlock" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.windows do 'windowsAndImplicitAllPlatformBlock' end
    cmd.invoke do 'windowsAndImplicitAllPlatformBlock' end
  end

  command "windowsAndExplicitAllPlatformBlock" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.windows  do 'windowsAndExplicitAllPlatformBlock' end
    cmd.invoke do 'windowsAndExplicitAllPlatformBlock' end
  end

  command "windowsOnlyPlatformBlock" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.windows  do 'windowsOnlyPlatformBlock' end
  end

  command "linuxAndImplicitAllPlatformBlock" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.linux  do 'linuxAndImplicitAllPlatformBlock' end
    cmd.invoke do 'linuxAndImplicitAllPlatformBlock' end
  end

  command "linuxAndExplicitAllPlatformBlock" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.linux  do 'linuxAndExplicitAllPlatformBlock' end
    cmd.invoke do 'linuxAndExplicitAllPlatformBlock' end
  end

  command "linuxOnlyPlatformBlock" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.linux  do 'linuxOnlyPlatformBlock' end
  end

  command "unixAndImplicitAllPlatformBlock" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.unix  do 'unixAndImplicitAllPlatformBlock' end
    cmd.invoke do 'unixAndImplicitAllPlatformBlock' end
  end

  command "unixAndExplicitAllPlatformBlock" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.unix  do 'unixAndExplicitAllPlatformBlock' end
    cmd.invoke do 'unixAndExplicitAllPlatformBlock' end
  end

  command "unixOnlyPlatformBlock" do |cmd|
    cmd.scope = "foo"
    cmd.invoke.unix  do 'unixOnlyPlatformBlock' end
  end

end