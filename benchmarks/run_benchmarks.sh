#!/bin/bash

cd ../

lein run benchmark clj-concat
lein run benchmark opt-concat
lein run benchmark clj-map
lein run benchmark opt-map
lein run benchmark clj-take
lein run benchmark opt-take
lein run benchmark clj-drop
lein run benchmark opt-drop
lein run benchmark clj-take-last
lein run benchmark opt-take-last
lein run benchmark clj-drop-last
lein run benchmark opt-drop-last
lein run benchmark clj-take-while
lein run benchmark opt-take-while
lein run benchmark clj-drop-while
lein run benchmark opt-drop-while
lein run benchmark clj-take-last-while
lein run benchmark opt-take-last-while
lein run benchmark clj-drop-last-while
lein run benchmark opt-drop-last-while
lein run benchmark clj-split
lein run benchmark opt-split
lein run benchmark clj-slice
lein run benchmark opt-slice
lein run benchmark clj-concat-many
lein run benchmark opt-concat-many
lein run make-page
