package hamt;

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

    public static void printArray(Object[] arr) {
        System.out.println(Arrays.deepToString(arr));
    }
}
