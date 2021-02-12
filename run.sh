#!/bin/bash
# This script will run EJWS
set -e

java --module-path "target/classes/lib:target/EJWS-1.0.0.0.jar" -m EJWS/me.oddlyoko.ejws.EJWS module
