(defn repeatall [n seq]
    (reduce concat (repeat n seq)))

(defn repeatevery [n seq]
    (reduce concat (map (fn [x] (repeat n x)) seq)))

(defn allstrings [n alphabet]
    (reduce
        (fn [acc newalph] (map str (repeatevery (count newalph) acc) (repeatall (count acc) newalph)) ) 
        alphabet
        (repeat (- n 1) alphabet)
    ))

(defn stringsdistinct [n alphabet]
    (filter 
        (fn [x] (== (count (vals (frequencies x))) n)) 
        (allstrings n alphabet) 
    ))

(println 
    (allstrings 2 (list "a" "b", "c"))
    "\n"
    (stringsdistinct 2 (list "a" "b", "c")))
