#!/bin/bash
# This script will export lib folder from EJWS and create module folder
set -e

cd target/
unzip EJWS-*.jar "lib/*"
mkdir "module"
cp "/home/odoo/Desktop/Test/target/Test-1.0.jar" "module/"
