#!/bin/bash

cd ../

lein run clj-concat
lein run opt-concat
lein run clj-map
lein run opt-map
lein run clj-take
lein run opt-take
lein run clj-drop
lein run opt-drop
lein run clj-take-while
lein run opt-take-while
lein run clj-drop-while
lein run opt-drop-while
lein run clj-take-last-while
lein run opt-take-last-while
lein run clj-drop-last-while
lein run opt-drop-last-while
lein run clj-split
lein run opt-split
lein run clj-slice
lein run opt-slice
lein run clj-concat-many
lein run opt-concat-many
