package hamt;

import clojure.lang.IFn;
import clojure.lang.PersistentVector;
import clojure.lang.PersistentVector.Node;
import java.util.Arrays;

public class Util {

    private static Object[] insideOut(Node node, int shift) {
        if (shift == 0) return node.array.clone();
        else {
            Object[] newNode = node.array.clone();
            for (int i = 0; i < newNode.length && node.array[i] != null ; i++) {
                newNode[i] = insideOut((Node)node.array[i], shift - 5);
            }
            return newNode;
        }
    }

    public static String asString(PersistentVector vec) {
        final Object[] tree = insideOut(vec.root, vec.shift);
        return String.format("Tail: %s :: Tree: %s", Arrays.deepToString(vec.tail), Arrays.deepToString(tree));
    }

    public static String asString(HAMT vec) {
        return asString(vec.persistentVector());
    }

    public static void println (Object... those) {
        String xs = (String) Arrays.stream(those).reduce("", (x, y) -> x.toString() + y.toString());
        System.out.println (xs);
    }

    public static void printArray(Object[] arr) {
        println(Arrays.deepToString(arr));
    }

    public static void mapArray(Object[] arr, IFn f) {
        int length = arr.length;
        for (int i = 0; i < length && arr[i] != null; i++) {
            arr[i] = f.invoke(arr[i]);
        }
    }
}
