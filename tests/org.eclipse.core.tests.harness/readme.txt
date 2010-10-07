README for org.eclipse.core.tests.harness

This plug-in works as a library for core tests. It also holds the script
to launch the core automated tests after the a build.
In order to execute launch the tests, the test framework plug-ins have
to available.

Command line (from the eclipse home directory) for executing tests:

java -cp startup.jar org.eclipse.core.launcher.Main -application org.eclipse.ant.core.antRunner -file plugins\org.eclipse.core.tests.harness\test.xml run -dev bin