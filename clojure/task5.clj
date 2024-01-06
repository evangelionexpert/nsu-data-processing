(ns conc.task5)

(defrecord fork-record [idx not-used counter])
(defn fork [idx]
    (ref (fork-record. idx true 0)))

(def stm-rest-counter 
    (atom 0))

(defn take-forks [left-fork right-fork]
    (dosync
        (swap! stm-rest-counter inc)
        (if (:not-used @left-fork)
            (if (:not-used @right-fork)
                (do
                    (ref-set left-fork
                        (fork-record. (:idx @left-fork) false (inc (:counter @left-fork))))
                    (ref-set right-fork
                        (fork-record. (:idx @right-fork) false (inc (:counter @right-fork))))
                    true)
                false)
            false)))

(defn put-forks [left-fork right-fork]
    (dosync
        (swap! stm-rest-counter inc)
        (ref-set left-fork
            (fork-record. (:idx @left-fork) true (:counter @left-fork)))
        (ref-set right-fork
            (fork-record. (:idx @right-fork) true (:counter @right-fork)))))
                    
(defn phil [idx sleep-time-millis dining-time-millis left-fork right-fork times]
    (dotimes [_ times]
        (do
            (printf "PHIL %d: goin to sleep for %d millis\n" 
                idx sleep-time-millis)

            (Thread/sleep sleep-time-millis)

            (while (not (take-forks left-fork right-fork)) 
                (do
                    (printf "PHIL %d: couldn't take forks %d and %d, gonna sleep for %d millis\n" 
                        idx (:idx @left-fork) (:idx @right-fork) sleep-time-millis)
                    (Thread/sleep sleep-time-millis)))

            (printf "PHIL %d: took forks %d and %d, eating for %d millis\n" 
                idx (:idx @left-fork) (:idx @right-fork) dining-time-millis)
            
            (Thread/sleep dining-time-millis)
            (put-forks left-fork right-fork)
            
            (printf "PHIL %d: put forks %d and %d\n" 
                idx (:idx @left-fork) (:idx @right-fork))
            (flush)))

    (printf "! PHIL %d finished\n" idx)
    (flush))

(defn main [n sleeping-time-millis dining-time-millis times]
    (def forks 
        (map fork (range n)))

    (def threads 
        (->>(range n)
            (map (fn [idx] 
                (Thread. 
                    (fn [] (phil 
                        idx 
                        sleeping-time-millis 
                        dining-time-millis 
                        (nth forks idx) 
                        (nth forks (mod (inc idx) n))
                        times)))))))

    (println "! start")
    (time (do
        (doseq [x threads]
            (.start x))
        
        (doseq [x threads]
            (.join x))
    ))
    (printf "total amount of transactions restarts is %d\n" @stm-rest-counter))

(main 6 1000 1000 6)

;; (def aa (ref 15))
;; (dosync (ref-set aa 22))
;; (println aa)
;; measure overall execution time