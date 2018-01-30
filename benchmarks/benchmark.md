# Benchmarks
  These benchmarks have been done using `criterium`. You may find the code in `vector_ops.benchmarks.bench`. 

  As with any benchmarks, these don't project a perfect view of reality and I don't claim they do as such. 
  They solely deem as an indicator of the degree of improvement these optimisations might have over the Clojure alternatives. 

  
  Benchmarks have been run on: 

  **Operating System**: Ubuntu 16.04 (64 bit) 

  **CPU**: Intel i7-6700HQ CPU (6MB cache, 2.60 - 3.50 GHz x 4 cores) 

  **Memory**: 16 GB DDR4 
### Concatenation
**Note:** -

**Clojure:** `(vec (concat v1 v2))`

**Optimised:** `(vector-ops.core/concatv v1 v2)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 361.810252 ns | 7.196989 ns |
| 10 | 558.245696 ns | 398.796734 ns |
| 100 | 2.240790 µs | 846.869074 ns |
| 1000 | 19.031484 µs | 7.450031 µs |
| 10000 | 189.564562 µs | 71.827575 µs |
| 100000 | 1.927494 ms | 900.493711 µs |
| 1000000 | 23.703857 ms | 12.814612 ms |
### Batch concatenation
**Note:** Concatenated 10 vectors of given `size` at once.

**Clojure:** `(vec (apply concat vs))`

**Optimised:** `(vector-ops.core/apply concatv vs)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 756.808988 ns | 393.664749 ns |
| 10 | 5.480622 µs | 2.163517 µs |
| 100 | 34.627662 µs | 6.678997 µs |
| 1000 | 318.344501 µs | 80.677171 µs |
| 10000 | 3.214963 ms | 1.030669 ms |
| 100000 | 45.329293 ms | 10.913499 ms |
| 1000000 | 893.050373 ms | 162.112949 ms |
### Dropping
**Note:** -

**Clojure:** `(vec (drop n v))`

**Optimised:** `(vector-ops.core/dropv n v)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 154.673231 ns | 18.564517 ns |
| 10 | 461.134753 ns | 444.081258 ns |
| 100 | 3.125726 µs | 517.383259 ns |
| 1000 | 29.222027 µs | 1.110373 µs |
| 10000 | 347.917215 µs | 8.011953 µs |
| 100000 | 3.791349 ms | 95.015811 µs |
| 1000000 | 32.418843 ms | 695.911450 µs |
### Dropping last
**Note:** -

**Clojure:** `(vec (drop-last n v))`

**Optimised:** `(vector-ops.core/dropv-last n v)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 180.366026 ns | 53.649340 ns |
| 10 | 903.780801 ns | 445.236752 ns |
| 100 | 7.046274 µs | 481.406993 ns |
| 1000 | 68.173964 µs | 722.727751 ns |
| 10000 | 686.190882 µs | 3.796002 µs |
| 100000 | 7.932386 ms | 46.238683 µs |
| 1000000 | 75.833936 ms | 517.512106 µs |
### Dropping last while
**Note:** -

**Clojure:** `(vec (->> (reverse v) (drop-while p) (reverse)))`

**Optimised:** `(vector-ops.core/dropv-last-while n v)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 68.505042 ns | 8.913599 ns |
| 10 | 1.167079 µs | 544.383748 ns |
| 100 | 9.456833 µs | 1.522609 µs |
| 1000 | 90.773551 µs | 11.015628 µs |
| 10000 | 900.016235 µs | 105.808669 µs |
| 100000 | 9.058089 ms | 1.067737 ms |
| 1000000 | 93.121276 ms | 11.336951 ms |
### Dropping while
**Note:** -

**Clojure:** `(vec (drop-while n v))`

**Optimised:** `(vector-ops.core/dropv-wihle n v)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 131.099668 ns | 9.535428 ns |
| 10 | 500.303606 ns | 613.369386 ns |
| 100 | 3.462573 µs | 1.614100 µs |
| 1000 | 33.210243 µs | 11.893156 µs |
| 10000 | 334.205152 µs | 116.636202 µs |
| 100000 | 3.334074 ms | 1.225122 ms |
| 1000000 | 36.378544 ms | 16.444744 ms |
### Mapping
**Note:** -

**Clojure:** `(mapv f vs)`

**Optimised:** `(vector-ops.core/mapv f vs)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 181.796290 ns | 7.399992 ns |
| 10 | 454.735210 ns | 675.667162 ns |
| 100 | 3.040831 µs | 2.270018 µs |
| 1000 | 29.574956 µs | 18.165056 µs |
| 10000 | 292.305066 µs | 196.545751 µs |
| 100000 | 2.959955 ms | 1.815747 ms |
| 1000000 | 33.691650 ms | 21.845780 ms |
### Slicing
**Note:** Like `clojure.core/subvec`, but returns a proper vector, not a subvector

**Clojure:** `(vec (->> v (take m) (drop n)))`

**Optimised:** `(vector-ops.core/slicev v n m)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 176.707260 ns | 42.609155 ns |
| 10 | 923.916195 ns | 536.723375 ns |
| 100 | 6.906388 µs | 621.573771 ns |
| 1000 | 67.022146 µs | 1.253480 µs |
| 10000 | 667.791529 µs | 8.570844 µs |
| 100000 | 6.780595 ms | 46.800204 µs |
| 1000000 | 71.247426 ms | 914.541700 µs |
### Splitting at
**Note:** -

**Clojure:** `(mapv vec (split-at n v))`

**Optimised:** `(vector-ops.core/splitv-at n v)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 611.454342 ns | 47.140462 ns |
| 10 | 1.410785 µs | 807.516807 ns |
| 100 | 8.159660 µs | 931.020481 ns |
| 1000 | 75.594338 µs | 1.731148 µs |
| 10000 | 752.465039 µs | 11.221048 µs |
| 100000 | 7.625630 ms | 132.211750 µs |
| 1000000 | 84.335322 ms | 1.310956 ms |
### Taking
**Note:** -

**Clojure:** `(vec (take n v))`

**Optimised:** `(vector-ops.core/takev n v)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 121.605930 ns | 19.252959 ns |
| 10 | 575.795633 ns | 387.547367 ns |
| 100 | 4.717715 µs | 428.134181 ns |
| 1000 | 45.774762 µs | 660.637803 ns |
| 10000 | 457.472861 µs | 3.668902 µs |
| 100000 | 4.612023 ms | 49.418577 µs |
| 1000000 | 47.498904 ms | 510.973851 µs |
### Taking last
**Note:** -

**Clojure:** `(vec (take-last n v))`

**Optimised:** `(vector-ops.core/takev-last n v)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 81.464990 ns | 53.871355 ns |
| 10 | 550.774035 ns | 458.354913 ns |
| 100 | 3.929223 µs | 535.625752 ns |
| 1000 | 37.912209 µs | 1.116335 µs |
| 10000 | 384.062047 µs | 7.406457 µs |
| 100000 | 3.869972 ms | 85.214376 µs |
| 1000000 | 41.998881 ms | 510.553739 µs |
### Taking last while
**Note:** -

**Clojure:** `(vec (->> (reverse v) (take-while p) (reverse)))`

**Optimised:** `(vector-ops.core/takev-last-while n v)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 62.448233 ns | 8.927027 ns |
| 10 | 1.085941 µs | 575.353576 ns |
| 100 | 11.152364 µs | 1.588850 µs |
| 1000 | 111.231348 µs | 11.537438 µs |
| 10000 | 1.115974 ms | 111.004553 µs |
| 100000 | 11.322195 ms | 1.125774 ms |
| 1000000 | 115.946913 ms | 12.238548 ms |
### Taking while
**Note:** -

**Clojure:** `(vec (take-while n v))`

**Optimised:** `(vector-ops.core/takev-while n v)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 123.973344 ns | 9.025158 ns |
| 10 | 674.887858 ns | 547.634108 ns |
| 100 | 5.387527 µs | 1.537828 µs |
| 1000 | 52.125426 µs | 11.015713 µs |
| 10000 | 523.327036 µs | 105.406068 µs |
| 100000 | 5.242220 ms | 1.080573 ms |
| 1000000 | 53.885682 ms | 13.177093 ms |