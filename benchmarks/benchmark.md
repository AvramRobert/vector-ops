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
| 0 | 345.229310 ns | 339.861051 ns |
| 10 | 526.583872 ns | 591.539725 ns |
| 100 | 2.207469 µs | 1.317692 µs |
| 1000 | 18.636767 µs | 10.625364 µs |
| 10000 | 187.033922 µs | 104.283332 µs |
| 100000 | 1.877405 ms | 694.706478 µs |
| 1000000 | 24.399620 ms | 8.079484 ms |
### Batch concatenation
**Note:** Concatenated 10 vectors of given `size` at once.

**Clojure:** `(vec (apply concat vs))`

**Optimised:** `(vector-ops.core/apply concatv vs)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 680.699980 ns | 727.592081 ns |
| 10 | 5.377857 µs | 2.848626 µs |
| 100 | 33.520594 µs | 8.325013 µs |
| 1000 | 313.182302 µs | 84.078660 µs |
| 10000 | 3.148500 ms | 784.801012 µs |
| 100000 | 31.899151 ms | 6.345807 ms |
| 1000000 | 748.850652 ms | 74.269136 ms |
### Dropping
**Note:** -

**Clojure:** `(vec (drop n v))`

**Optimised:** `(vector-ops.core/dropv n v)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 132.223037 ns | 375.939147 ns |
| 10 | 404.087163 ns | 409.470539 ns |
| 100 | 2.844630 µs | 483.452256 ns |
| 1000 | 27.520349 µs | 1.039916 µs |
| 10000 | 275.494455 µs | 7.162063 µs |
| 100000 | 2.777170 ms | 82.817750 µs |
| 1000000 | 29.576256 ms | 512.021192 µs |
### Dropping last
**Note:** -

**Clojure:** `(vec (drop-last n v))`

**Optimised:** `(vector-ops.core/dropv-last n v)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 175.357342 ns | 437.698358 ns |
| 10 | 896.828431 ns | 486.649462 ns |
| 100 | 7.052646 µs | 529.276734 ns |
| 1000 | 68.631365 µs | 789.191541 ns |
| 10000 | 687.124150 µs | 3.762374 µs |
| 100000 | 6.915643 ms | 46.082897 µs |
| 1000000 | 72.047499 ms | 550.540710 µs |
### Dropping last while
**Note:** -

**Clojure:** `(vec (->> (reverse v) (drop-while p) (reverse)))`

**Optimised:** `(vector-ops.core/dropv-last-while n v)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 66.726810 ns | 396.620673 ns |
| 10 | 1.129031 µs | 594.054783 ns |
| 100 | 9.144976 µs | 1.548701 µs |
| 1000 | 88.677076 µs | 10.913552 µs |
| 10000 | 891.292573 µs | 104.594932 µs |
| 100000 | 8.942677 ms | 1.152626 ms |
| 1000000 | 92.526941 ms | 11.329923 ms |
### Dropping while
**Note:** -

**Clojure:** `(vec (drop-while n v))`

**Optimised:** `(vector-ops.core/dropv-wihle n v)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 129.750360 ns | 366.233883 ns |
| 10 | 492.606611 ns | 571.779757 ns |
| 100 | 3.427911 µs | 1.633088 µs |
| 1000 | 32.988643 µs | 12.220383 µs |
| 10000 | 330.018728 µs | 119.331512 µs |
| 100000 | 3.320606 ms | 1.209765 ms |
| 1000000 | 37.018909 ms | 16.450809 ms |
### Slicing
**Note:** Like `clojure.core/subvec`, but returns a proper vector, not a subvector

**Clojure:** `(vec (->> v (take m) (drop n)))`

**Optimised:** `(vector-ops.core/slicev v n m)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 172.486548 ns | 404.239077 ns |
| 10 | 908.033727 ns | 435.668039 ns |
| 100 | 6.772123 µs | 516.028009 ns |
| 1000 | 65.365276 µs | 1.045427 µs |
| 10000 | 653.608135 µs | 7.208002 µs |
| 100000 | 6.541279 ms | 45.204940 µs |
| 1000000 | 66.623653 ms | 508.389323 µs |
### Splitting at
**Note:** -

**Clojure:** `(mapv vec (split-at n v))`

**Optimised:** `(vector-ops.core/splitv-at n v)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 599.651668 ns | 761.593406 ns |
| 10 | 1.357355 µs | 853.248136 ns |
| 100 | 7.952230 µs | 978.170149 ns |
| 1000 | 73.824995 µs | 1.772112 µs |
| 10000 | 734.441009 µs | 11.063172 µs |
| 100000 | 7.377658 ms | 128.443786 µs |
| 1000000 | 79.965784 ms | 1.325559 ms |
### Taking
**Note:** -

**Clojure:** `(vec (take n v))`

**Optimised:** `(vector-ops.core/takev n v)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 114.261699 ns | 373.606481 ns |
| 10 | 574.467594 ns | 425.956985 ns |
| 100 | 4.704670 µs | 482.331440 ns |
| 1000 | 46.059242 µs | 730.857358 ns |
| 10000 | 462.230501 µs | 3.726358 µs |
| 100000 | 4.761912 ms | 45.531588 µs |
| 1000000 | 46.933487 ms | 515.379634 µs |
### Taking last
**Note:** -

**Clojure:** `(vec (take-last n v))`

**Optimised:** `(vector-ops.core/takev-last n v)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 79.496262 ns | 421.222690 ns |
| 10 | 534.512111 ns | 453.286828 ns |
| 100 | 3.916278 µs | 527.850407 ns |
| 1000 | 37.602871 µs | 1.087716 µs |
| 10000 | 378.159922 µs | 7.222426 µs |
| 100000 | 3.831265 ms | 82.996909 µs |
| 1000000 | 41.660342 ms | 557.392265 µs |
### Taking last while
**Note:** -

**Clojure:** `(vec (->> (reverse v) (take-while p) (reverse)))`

**Optimised:** `(vector-ops.core/takev-last-while n v)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 61.263335 ns | 373.965567 ns |
| 10 | 1.060918 µs | 587.790623 ns |
| 100 | 11.000198 µs | 1.605473 µs |
| 1000 | 109.919177 µs | 11.514750 µs |
| 10000 | 1.105790 ms | 109.597844 µs |
| 100000 | 11.137508 ms | 1.115697 ms |
| 1000000 | 114.252488 ms | 12.345147 ms |
### Taking while
**Note:** -

**Clojure:** `(vec (take-while n v))`

**Optimised:** `(vector-ops.core/takev-while n v)`

| **Size** | **Clojure**   | **Optimised**   | 
| -------- | :----------------: | :------------------: | 
| 0 | 123.342048 ns | 364.198699 ns |
| 10 | 655.467758 ns | 585.539461 ns |
| 100 | 5.236549 µs | 1.652741 µs |
| 1000 | 50.687750 µs | 11.678774 µs |
| 10000 | 508.051531 µs | 114.939860 µs |
| 100000 | 5.088848 ms | 1.156994 ms |
| 1000000 | 52.200606 ms | 14.449102 ms |