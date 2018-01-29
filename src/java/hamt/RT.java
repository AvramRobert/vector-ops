package hamt;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.PersistentVector;

public class RT {
    public static PersistentVector concat (PersistentVector a,
                                           PersistentVector b) {
        if (a.count() == 0) return b;
        else return HAMT.fromVector(a).concat(b).persistentVector();
    }

    public static PersistentVector concat (PersistentVector a,
                                           PersistentVector b,
                                           PersistentVector c) {
        if (a.count() == 0) return concat(b, c);
        else return HAMT.fromVector(a).concat(b).concat(c).persistentVector();
    }

    public static PersistentVector concat (PersistentVector a,
                                           PersistentVector b,
                                           PersistentVector c,
                                           PersistentVector d) {
        if (a.count() == 0) return concat(b, c, d);
        else return HAMT.fromVector(a).concat(b).concat(c).concat(d).persistentVector();
    }

    public static PersistentVector concatMany(ISeq vectors) {
        PersistentVector vec;
        HAMT newVector = null;
        while(vectors != null) {
            vec = (PersistentVector) vectors.first();
            if (vec.count() != 0) {
                if (newVector == null) {
                    newVector = HAMT.fromVector(vec);
                } else {
                    newVector = newVector.concat(vec);
                }
            }
            vectors = vectors.next();
        }
        return newVector == null ? PersistentVector.EMPTY : newVector.persistentVector();
    }

    public static PersistentVector map (PersistentVector that, IFn f) {
        if (that.count() == 0) return that;
        else return HAMT.fromVector(that).map(f).persistentVector();
    }

    public static PersistentVector take(PersistentVector that, int n) {
        int size = that.count();
        if (n >= size) return that;
        else if (size == 0 || n <= 0) return PersistentVector.EMPTY;
        else return HAMT.fromVector(that).take(n).persistentVector();
    }

    public static PersistentVector drop(PersistentVector that, int n) {
        int size = that.count();
        if (n <= 0) return that;
        else if (size == 0 || n >= size) return PersistentVector.EMPTY;
        else return HAMT.fromVector(that).drop(n).persistentVector();
    }

    public static PersistentVector takeWhile(PersistentVector that, IFn p) {
        if (that.count() == 0) return that;
        else return HAMT.fromVector(that).takeWhile(p).persistentVector();
    }

    public static PersistentVector dropWhile(PersistentVector that, IFn p) {
        if (that.count() == 0) return that;
        else return HAMT.fromVector(that).dropWhile(p).persistentVector();
    }

    public static PersistentVector takeLastWhile(PersistentVector that, IFn p) {
        if (that.count() == 0) return that;
        else return HAMT.fromVector(that).takeLastWhile(p).persistentVector();
    }

    public static PersistentVector dropLastWhile(PersistentVector that, IFn p) {
        if (that.count() == 0) return that;
        else return HAMT.fromVector(that).dropLastWhile(p).persistentVector();
    }

    public static PersistentVector slice(PersistentVector that, int from, int until) {
        if (that.count() == 0) return that;
        else if (until <= 0 || from >= until) return PersistentVector.EMPTY;
        else return HAMT.fromVector(that).slice(from, until).persistentVector();
    }
}