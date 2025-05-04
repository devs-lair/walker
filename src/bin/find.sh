#!/bin/bash
for number in {1..10}
do
    time find $1 -type f -name *.xml > /dev/null
done
