(ns conc.task3)

(def cores 6)
(def core-chunk-size 10)

(defn parallel-filter [f col]
    (->>(partition-all (* core-chunk-size cores) col)
        (map #(partition-all core-chunk-size %))
        (map #(map (fn [core-chunk] (future (doall (filter f core-chunk)))) %))
        (flatten)
        (map deref)
        (flatten)))

(defn unit-tests []
    (assert 
        (=
            (take 100
                (parallel-filter 
                odd?
                (iterate inc 1)))
            (take 100
                (filter 
                odd?
                (iterate inc 1)))
        ))
        
    (assert 
        (=
            (take 100500
                (parallel-filter 
                odd?
                (take 50 (iterate inc 9000))))
            (take 100500
                (filter 
                odd?
                (take 50 (iterate inc 9000))))
        )))

(defn bench []
    (println "bench :: take first n, parallel/singlethreaded")
    (defn somepredicate? [x]
        (Thread/sleep 10)
        ;; (println "THREAD" (.getId (Thread/currentThread)) " solves " x)
        (odd? x))

    (time (doall
        (take 600
            (parallel-filter 
                somepredicate?
                (iterate inc 1)))))

    (time (doall
        (take 600
            (filter
                somepredicate?
                (iterate inc 1)))))


    (println "\nbench :: take 10 more than n, parallel/singlethreaded")
    (defn somepredicate-after-n? [x]
        (and (somepredicate? x) (>= x 1800)))

    (time (println
        (take 10
            (parallel-filter 
                somepredicate-after-n?
                (iterate inc 1)))))

    (time (println
        (take 10
            (filter 
                somepredicate-after-n?
                (iterate inc 1)))))
    (shutdown-agents))
    
(unit-tests)
(bench)
