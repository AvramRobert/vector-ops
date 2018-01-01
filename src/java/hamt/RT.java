package hamt;

import clojure.lang.ISeq;
import clojure.lang.PersistentVector;

public class RT {
    public static PersistentVector concat (PersistentVector a,
                                           PersistentVector b) {
        return HAMT.fromVector(a).concat(b).persistentVector();
    }

    public static PersistentVector concat (PersistentVector a,
                                           PersistentVector b,
                                           PersistentVector c) {
        return HAMT.fromVector(a).concat(b).concat(c).persistentVector();
    }

    public static PersistentVector concat (PersistentVector a,
                                           PersistentVector b,
                                           PersistentVector c,
                                           PersistentVector d) {
        return HAMT.fromVector(a).concat(b).concat(c).concat(d).persistentVector();
    }

    public static PersistentVector concat (PersistentVector a,
                                           PersistentVector b,
                                           PersistentVector c,
                                           PersistentVector d,
                                           PersistentVector e) {
        return HAMT.fromVector(a).concat(b).concat(c).concat(d).concat(e).persistentVector();
    }

    public static PersistentVector concatMany(ISeq vectors) {
        HAMT vector = HAMT.fromVector((PersistentVector) vectors.first());
        vectors = vectors.next();
        while(vectors != null) {
            vector = vector.concat((PersistentVector)vectors.first());
            vectors = vectors.next();
        }
        return vector.persistentVector();
    }
}
