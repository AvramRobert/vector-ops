package hamt;

import clojure.lang.IPersistentMap;
import clojure.lang.PersistentVector;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import static clojure.lang.PersistentVector.*;

class HAMT {
    private final IPersistentMap meta;
    private int size;
    private int shift;
    private Node tree;
    private Object[] tail;

    HAMT(IPersistentMap meta, int size, int shift, Node tree, Object[] tail) {
        this.meta = meta;
        this.size = size;
        this.shift = shift;
        this.tree = tree;
        this.tail = tail;
    }

    public static HAMT fromVector(PersistentVector vec) {
        return new HAMT(vec.meta(), vec.count(), vec.shift, vec.root, vec.tail);
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

    private Node nodeFrom(Node that) {
        return new Node(that.edit, new Object[32]);
    }

    private Node path (Node that, int shift) {
        if (shift == 0) return that;
        else {
            Node node = nodeFrom(that);
            node.array[0] = path(that, shift - 5);
            return node;
        }
    }

    private void push (Node tree, Object[] that, int shift) {
        int i = ((size - 1) >>> shift) & 0x01f;
        if (shift == 5) {
            tree.array[i] = new Node(tree.edit, that);
        }
        else {
            Node subtree = (Node) tree.array[i];
            if (subtree == null) {
                tree.array[i] = path(new Node(tree.edit, that), shift);
            } else {
                push((Node)tree.array[i], that, shift - 5);
            }
        }
    }

    private void pushNode (Object[] node, int nodeSize) {
        if (size >>> 5 > 1 << shift) {
            Node newRoot = nodeFrom(tree);
            newRoot.array[0] = tree;
            newRoot.array[1] = path(new Node(tree.edit, tail), shift);
            this.tree = newRoot;
            this.shift = shift + 5;
        } else {
            push(tree, tail, shift);
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

    private void fillTail() {
        Object[] newTail = new Object[32];
        System.arraycopy(tail, 0, newTail, 0, tail.length);
        this.tail = newTail;
    }

    private boolean isLastIn(PersistentVector vec, int i) {
        return i >= (vec.count() - vec.tail.length);
    }

    private int tailSize() {
        return ((size - 1) & 32) + 1;
    }

    private boolean canContain(Object[] node) {
        return (32 - tailSize()) >= node.length;
    }

    private void trimTail() {
        int tailSize = tailSize();
        Object[] newTail = new Object[tailSize];
        System.arraycopy(tail, 0, newTail, 0, tailSize);
        this.tail = newTail;
    }

    public HAMT concat(PersistentVector that) {
        if (tail.length == 32) {
            pushNodes(that);
        } else {
            int leftSize = tail.length;
            int rightSize;
            Object[] node;
            fillTail();
            for (int i = 0; i < that.count(); i+=32) {
                node = that.arrayFor(i);
                if (isLastIn(that, i) && canContain(node)) {
                    System.arraycopy(node, 0, tail, 0, node.length);
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
}