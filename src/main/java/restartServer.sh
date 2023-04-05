##!/bin/bash
#
## Set the path to the Java executable
#export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_202.jdk/Contents/Home

# Start the first program in debug mode in a new terminal window
#gnome-terminal --tab --title="Program 1" --working-directory="" --command="bash -c 'export JAVA_OPTS=\"-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005\"; mvn spring-boot:run'"

# shellcheck disable=SC1128
!/bin/bash

# set the path to your java program and debug port
program_path="/Users/gangasingh/Desktop/COMP6231/MTBS-Project/src/main/java/Replicas/Server"
debug_port="5005"

# open multiple terminal windows and run the program in debug mode
osascript <<END_SCRIPT
tell application "Terminal"
    do script "cd \"$program_path\"; export JAVA_TOOL_OPTIONS=\"-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=$debug_port\"; java ServerInstance"
end tell
END_SCRIPT

#osascript <<END_SCRIPT
#tell application "Terminal"
#    do script "cd \"$program_path\"; export JAVA_TOOL_OPTIONS=\"-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=$debug_port\"; mvn clean compile exec:java"
#end tell
#END_SCRIPT

# add more terminal windows as needed

