# VIDEVOX - Install Guide

The following is instructions on how to run VIDEVOX on a new system. For actual usage information please see the Manual or click the help menu from within the application.

## Setup:
VIDEVOX relies on a couple of script files for handling video compilation. Please look in the folder that contains the .jar file (and probably this file) and check that all .sh files have executable permissions.  
Next, run the jar from the command line using JRE8. If Java 8 is the default on your system then

```Shell
$ java -jar <jar_name_with_path.jar>
```

should work. On the UG4 computers, the java 8 jre is installed but not the default so

```Shell
$ /usr/lib/jvm/jre1.8/bin/java -jar <jar_name_with_path.jar>
```

should be used.