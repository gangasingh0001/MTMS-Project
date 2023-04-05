#!/bin/bash

# Kill the Java process running on port 8080
echo "Stopping Java server..."
kill $(lsof -t -i :8082)

# Wait for the server to stop
while lsof -i :8082 >/dev/null; do
    sleep 1
done

# Start the Java server
echo "Starting Java server..."

#!/bin/bash

# Set the source directory to compile
cd ..
SOURCE_DIR="./java"

# Find all the .java files in the source directory and its subdirectories
JAVA_FILES=$(find "$SOURCE_DIR" -name "*.java")

# Compile all the .java files using javac
javac $JAVA_FILES

# If the compilation was successful, print a success message
if [ $? -eq 0 ]; then
    echo "Compilation successful!"
else
    echo "Compilation failed."
fi



