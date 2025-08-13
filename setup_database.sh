#!/bin/bash

echo "Setting up Smart Items Database..."
echo

# Check if MySQL is available
if ! command -v mysql &> /dev/null; then
    echo "Error: MySQL is not installed or not in PATH"
    echo "Please install MySQL and add it to your PATH"
    exit 1
fi

echo "Creating database and tables..."
mysql -u root -p < src/main/resources/schema.sql

echo
echo "Running migration to add image support..."
mysql -u root -p smartitemsdb < src/main/resources/migration.sql

echo
echo "Adding sample data..."
mysql -u root -p smartitemsdb < src/main/resources/sample_data.sql

echo
echo "Database setup completed!"
echo "You can now run the application."
