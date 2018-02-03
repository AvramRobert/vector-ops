# vector-ops

Clojure collection operations optimised for vectors.

If you've ever wanted optimised vector versions of functions like `take`, `drop`, `concat` etc.,
you'll most probably find them here.

**NOTE:** Subvectors not yet supported
## Clojars

```clj
[vector-ops "0.1.0"]
```

## Usage 
```clojure 
=> (require '[vector-ops.core :as v])
nil 

=> (v/takev 3 [1 2 3 4 5])
[1 2 3] 

=> (v/dropv-last 2 [1 2 3 4 5])
[1 2 3]

...
```

And so on..
## Benchmarks
You may find here: [Benchmarks](/benchmarks/benchmark.md)

## License

Copyright © 2018 Robert M. Avram

Distributed under the Apache-2.0 License.
