#!/bin/bash

# Find the current working directory
cwd=$(pwd)

# Save the address the script is running from
sdir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Go to the directory the script is running in
cd "$sdir"

chmod 777 ./videvox/*.sh

if [ "`java -version`" == "*1.8*" ]
then
	java -jar ./videvox/"jarfile.jar"
else
	/usr/lib/jvm/jre1.8/bin/java -jar ./videvox/"jarfile.jar"
fi

# Go back to the original working directory
cd "$cwd"
echo "Thankyou for using VIDEVOX"
