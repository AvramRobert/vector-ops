package hamt;

import clojure.lang.IFn;
import clojure.lang.IPersistentMap;
import clojure.lang.PersistentVector;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import static clojure.lang.PersistentVector.*;
import static hamt.Util.*;

class HAMT {
    private final PersistentVector vector;
    private final IPersistentMap meta;
    private int size;
    private int shift;
    private Node tree;
    private Object[] tail;

    HAMT(PersistentVector vector, IPersistentMap meta, int size, int shift, Node tree, Object[] tail) {
        this.vector = vector;
        this.meta = meta;
        this.size = size;
        this.shift = shift;
        this.tree = tree;
        this.tail = tail;
    }

    private HAMT emptyFrom(PersistentVector vec) {
        return new HAMT(vec, vec.meta(), 0, 5, nodeFrom(vec.root), new Object[]{});
    }

    private Node nodeFrom(Node that) {
        return new Node(that.edit, new Object[32]);
    }

    private Node cloneNode(Node that) {
        return new Node(that.edit, that.array.clone());
    }

    private void fillTail() {
        Object[] newTail = new Object[32];
        System.arraycopy(tail, 0, newTail, 0, tail.length);
        this.tail = newTail;
    }

    private boolean isLastIn(PersistentVector vec, int i) {
        return i >= (vec.count() - vec.tail.length);
    }

    private int tailSize() {
        return ((this.size - 1) % 32) + 1;
    }

    private boolean canContain(Object[] node) {
        return (32 - tailSize()) >= node.length;
    }

    private void trimTail() {
        int sz = tailSize();
        Object[] newTail = new Object[sz];
        System.arraycopy(tail, 0, newTail, 0, sz);
        this.tail = newTail;
    }

    private Node path (Node that, int shift) {
        if (shift == 0) return that;
        else {
            Node node = nodeFrom(that);
            node.array[0] = path(that, shift - 5);
            return node;
        }
    }

    private Node push (Node tree, Object[] that, int shift) {
        int i = ((size - 1) >>> shift) & 0x01f;
        Node newTree = cloneNode(tree);
        if (shift == 5) {
            newTree.array[i] = new Node(tree.edit, that);
        }
        else {
            Node subtree = (Node) tree.array[i];
            if (subtree == null) {
                newTree.array[i] = path(new Node(tree.edit, that), shift - 5);
            } else {
                push((Node)newTree.array[i], that, shift - 5);
            }
        }
        return newTree;
    }

    private void pushNode (Object[] node, int nodeSize) {
        if (size >>> 5 > 1 << shift) {
            Node newRoot = nodeFrom(tree);
            newRoot.array[0] = tree;
            newRoot.array[1] = path(new Node(tree.edit, tail), shift);
            this.tree = newRoot;
            this.shift = shift + 5;
        } else {
            this.tree = push(tree, tail, shift);
        }
        this.tail = node;
        this.size += nodeSize;
    }

    private void pushNodes (PersistentVector tht) {
        Object[] node;
        for (int i = 0; i < tht.count(); i += 32) {
            node = tht.arrayFor(i);
            pushNode(node, node.length);
        }
    }

    private void pushMut (Node tree, Object[] node, int shift) {
        int idx = ((this.size - 1) >>> shift) & 0x01f;
        if (shift == 5) {
            tree.array[idx] = new Node(tree.edit, node);
        }
        else {
            Node subtree = (Node) tree.array[idx];
            if (subtree == null) {
                tree.array[idx] = path(new Node(tree.edit, node), shift - 5);
            }
            else pushMut(subtree, node, shift - 5);
        }
    }

    private void pushNodeMut (Object[] node, int nodeSize) {
        if (size >>> 5 > 1 << shift) {
            Node newTree = nodeFrom(tree);
            newTree.array[0] = tree;
            newTree.array[1] = path(new Node (tree.edit, tail), shift);
            this.tree = newTree;
            this.shift += 5;
        }
        else if (size > 0) {
            pushMut(tree, tail, shift);
        }
        this.tail = node;
        this.size += nodeSize;
    }

    private void pushNodesMut(PersistentVector vec, int from, int to) {
        Object[] node;
        for (int i = from; i < to; i++) {
            node = vec.arrayFor(i*32);
            pushNodeMut(node, node.length);
        }
    }

    public PersistentVector persistentVector() {
        try {
            Constructor<PersistentVector> cons =
                    PersistentVector.class.
                            getDeclaredConstructor(
                                    IPersistentMap.class,
                                    int.class,
                                    int.class,
                                    Node.class,
                                    Object[].class);
            cons.setAccessible(true);
            return cons.newInstance(meta, size, shift, tree, tail);
        } catch (NoSuchMethodException |
                IllegalAccessException |
                InstantiationException |
                InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HAMT fromVector(PersistentVector vec) {
        return new HAMT(vec, vec.meta(), vec.count(), vec.shift, vec.root, vec.tail);
    }

    public HAMT concat(PersistentVector that) {
        if (tail.length == 32) {
            pushNodes(that);
        }
        else if (size == 0) {
            this.size = that.count();
            this.shift = that.shift;
            this.tree = that.root;
            this.tail = that.tail;
        }
        else {
            int count = that.count();
            int leftSize = tail.length;
            int rightSize;
            Object[] node;
            fillTail();
            for (int i = 0; i < count; i+=32) {
                node = that.arrayFor(i);
                if (isLastIn(that, i) && canContain(node)) {
                    System.arraycopy(node, 0, tail, leftSize, node.length);
                    this.size += node.length;
                } else {
                    Object[] newTail = new Object[32];
                    rightSize = 32 - leftSize;
                    System.arraycopy(node, 0, tail, leftSize, rightSize);
                    leftSize = node.length - rightSize;
                    System.arraycopy(node, rightSize, newTail, 0, leftSize);
                    this.size += rightSize;
                    pushNode(newTail, leftSize);
                }
            }
            trimTail();
        }
        return this;
    }

    public HAMT map (IFn f) {
        HAMT newVec = emptyFrom(vector);
        int count = vector.count();
        for (int i = 0; i < count; i += 32) {
            Object[] node = vector.arrayFor(i).clone();
            mapArray(node, f);
            newVec.pushNodeMut(node, node.length);
        }
        return newVec;
    }

    public HAMT take (int n) {
        HAMT newVec = emptyFrom(vector);
        if (n <= 0) return newVec;
        else if (n >= size) return fromVector(vector);
        else if (n < 32) {
            Object[] newTail = new Object[n];
            System.arraycopy(vector.arrayFor(0), 0, newTail, 0, n);
            newVec.tail = newTail;
            newVec.size += n;
        }
        else if ((n & 0x01f) == 0) {
            int totalNodes = n >>> 5;
            newVec.pushNodesMut(vector, 0, totalNodes);
        }
        else {
            int totalNodes = n >>> 5;
            int partial = n & 0x01f; // n % 32
            Object[] newTail = new Object[partial];
            newVec.pushNodesMut(vector, 0, totalNodes);
            System.arraycopy(vector.arrayFor(totalNodes*32), 0, newTail, 0, partial);
            newVec.pushNodeMut(newTail, partial);
        }
        return newVec;
    }

    public HAMT drop(int n) {
        HAMT newVec = emptyFrom(vector);
        if (n <= 0) return fromVector(vector);
        else if (n >= size) return newVec;
        else if ((n & 0x01f) == 0) {
            int from = n >>> 5; // n / 32
            int to = ((vector.count() - 1) >> 5) + 1;
            newVec.pushNodesMut(vector, from, to);
        } else {
            int count = vector.count();
            int from = (n >>> 5) << 5; // (floor n / 32) * 32
            Object[] node = vector.arrayFor(from);
            int leftSize = n & 0x01f; // n % 32
            int rightSize = node.length - leftSize;
            newVec.tail = new Object[32];
            newVec.size += rightSize;
            System.arraycopy(node, leftSize, newVec.tail, 0, rightSize);
            leftSize = rightSize;
            for (int i = from + 32; i < count; i += 32) {
                node = vector.arrayFor(i);
                if (isLastIn(vector, i) && newVec.canContain(node)) {
                    System.arraycopy(node, 0, newVec.tail, leftSize, node.length);
                    newVec.size += node.length;
                } else {
                    Object[] newTail = new Object[32];
                    rightSize = 32 - leftSize;
                    System.arraycopy(node, 0, newVec.tail, leftSize, rightSize);
                    leftSize = node.length - rightSize;
                    System.arraycopy(node, rightSize, newTail, 0, leftSize);
                    newVec.size += rightSize;
                    newVec.pushNodeMut(newTail, leftSize);
                }
            }
            newVec.trimTail();
        }
        return newVec;
    }

    public HAMT takeLast(int n) {
        return null;
    }

    public HAMT dropLast(int n) {
        return null;
    }

    public HAMT takeWhile(IFn p) {
        return null;
    }

    public HAMT dropWhile(IFn p) {
        return null;
    }
}