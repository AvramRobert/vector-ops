#!/bin/bash

while IFS='' read -r line || [[ -n "$line" ]]; do
	cd /home/robert/Repositories/vector-ops
    lein run $line
done < "$1"