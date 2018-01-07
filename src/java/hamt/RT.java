package hamt;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.PersistentVector;

import java.util.Arrays;

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


    public static PersistentVector map (PersistentVector that, IFn f) {
        return HAMT.fromVector(that).map(f).persistentVector();
    }

    public static PersistentVector take(PersistentVector that, int n) {
        return HAMT.fromVector(that).take(n).persistentVector();
    }

    public static PersistentVector drop(PersistentVector that, int n) {
        return HAMT.fromVector(that).drop(n).persistentVector();
    }

    public static PersistentVector takeLast (PersistentVector that, int n) {
        return HAMT.fromVector(that).takeLast(n).persistentVector();
    }

    public static PersistentVector dropLast (PersistentVector that, int n) {
        return HAMT.fromVector(that).dropLast(n).persistentVector();
    }

    public static PersistentVector takeWhile(PersistentVector that, IFn p) {
        return HAMT.fromVector(that).takeWhile(p).persistentVector();
    }

    public static PersistentVector dropWhile(PersistentVector that, IFn p) {
        return HAMT.fromVector(that).dropWhile(p).persistentVector();
    }
}
