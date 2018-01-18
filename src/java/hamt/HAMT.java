package hamt;

import clojure.lang.*;
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

    private HAMT(PersistentVector vector, IPersistentMap meta, int size, int shift, Node tree, Object[] tail) {
        this.vector = vector;
        this.meta = meta;
        this.size = size;
        this.shift = shift;
        this.tree = tree;
        this.tail = tail;
    }

    private HAMT empty() {
        return new HAMT(vector, meta, 0, 5, nodeFrom(vector.root), new Object[]{});
    }

    private Node nodeFrom(Node that) {
        return new Node(that.edit, new Object[32]);
    }

    private Node cloneNode(Node that) {
        return new Node(that.edit, that.array.clone());
    }

    private boolean invokePred(IFn p, Object a) {
        Object ret = p.invoke(a);
        if (ret instanceof Boolean) return (Boolean) ret;
        else return ret != null;
    }

    private void fillTail() {
        Object[] newTail = new Object[32];
        System.arraycopy(tail, 0, newTail, 0, tail.length);
        this.tail = newTail;
    }

    private int tailSize() {
        return ((this.size - 1) & 0x01f) + 1;
    }

    private void trimTail() {
        int sz = tailSize();
        Object[] newTail = new Object[sz];
        System.arraycopy(tail, 0, newTail, 0, sz);
        this.tail = newTail;
    }

    private boolean canContain(int amount) {
        return (32 - tailSize()) >= amount;
    }

    // use only when copying nodes that don't start from the node's beginning (0 or multiples of 32)
    private void chunkedCopy(HAMT newVec, int from, int to, int leftSize) {
        Object[] node = nodeAt(from);
        int rightSize;
        if (node.length + from > to) { // both in the same node
            rightSize = (to & 0x01f) - leftSize;
        } else {
            rightSize = node.length - leftSize;
        }
        newVec.tail = new Object[32];
        newVec.size += rightSize;
        System.arraycopy(node, leftSize, newVec.tail, 0, rightSize);
        leftSize = rightSize;
        for (int i = from + 32; i < to; i += 32) {
            node = nodeAt(i);
            int next = i + 32;
            int nodeSize = to - i;
            if (next > to && newVec.canContain(nodeSize)) {
                System.arraycopy(node, 0, newVec.tail, leftSize, nodeSize);
                newVec.size += nodeSize;
            } else {
                Object[] newTail = new Object[32];
                rightSize = 32 - leftSize;
                System.arraycopy(node, 0, newVec.tail, leftSize, rightSize);
                newVec.size += rightSize;
                leftSize = node.length - rightSize;
                if (next > to) leftSize = (to & 0x01f) - rightSize;
                System.arraycopy(node, rightSize, newTail, 0, leftSize);
                newVec.pushNodeMut(newTail, leftSize);
            }
        }
        newVec.trimTail();
    }

    private int countWhile(IFn p) {
        Object[] node;
        int i = 0;
        int y = 0;
        boolean go = true;
        while (go && i < size) {
            node = nodeAt(i);
            int nodeLength = node.length;
            while (y < nodeLength) {
                if (invokePred(p, node[y])) i++;
                else {
                    go = false;
                    break;
                }
                y++;
            }
            y = 0;
        }
        return i;
    }

    private int reverseCountWhile(IFn p) {
        Object[] node;
        int y;
        int i = size;
        boolean go = true;
        while (go && i >= 0) {
            node = nodeAt(i - 1);
            y = node.length - 1;
            while (y >= 0) {
                if (invokePred(p, node[y])) i--;
                else {
                    go = false;
                    break;
                }
                y--;
            }
        }
        return i;
    }

    private Node path(Node that, int shift) {
        if (shift == 0) return that;
        else {
            Node node = nodeFrom(that);
            node.array[0] = path(that, shift - 5);
            return node;
        }
    }

    private Node push(Node tree, Object[] that, int shift) {
        int i = ((size - 1) >>> shift) & 0x01f;
        Node newTree = cloneNode(tree);
        if (shift == 5) {
            newTree.array[i] = new Node(tree.edit, that);
        } else {
            Node subtree = (Node) tree.array[i];
            if (subtree == null) {
                newTree.array[i] = path(new Node(tree.edit, that), shift - 5);
            } else {
                push((Node) newTree.array[i], that, shift - 5);
            }
        }
        return newTree;
    }

    private void pushNode(Object[] node, int nodeSize) {
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

    private void pushNodes(PersistentVector tht) {
        Object[] node;
        for (int i = 0; i < tht.count(); i += 32) {
            node = tht.arrayFor(i);
            pushNode(node, node.length);
        }
    }

    private void pushMut(Node tree, Object[] node, int shift) {
        int idx = ((this.size - 1) >>> shift) & 0x01f;
        if (shift == 5) {
            tree.array[idx] = new Node(tree.edit, node);
        } else {
            Node subtree = (Node) tree.array[idx];
            if (subtree == null) {
                tree.array[idx] = path(new Node(tree.edit, node), shift - 5);
            } else pushMut(subtree, node, shift - 5);
        }
    }

    private void pushNodeMut(Object[] node, int nodeSize) {
        if (size >>> 5 > 1 << shift) {
            Node newTree = nodeFrom(tree);
            newTree.array[0] = tree;
            newTree.array[1] = path(new Node(tree.edit, tail), shift);
            this.tree = newTree;
            this.shift += 5;
        } else if (size > 0) {
            pushMut(tree, tail, shift);
        }
        this.tail = node;
        this.size += nodeSize;
    }

    private void pushNodesMut(PersistentVector vec, int from, int to) {
        Object[] node;
        for (int i = from; i < to; i++) {
            node = vec.arrayFor(i << 5); // i * 32
            pushNodeMut(node, node.length);
        }
    }

    private Object[] nodeAt(int idx) {
        if (idx >= (size - tailSize())) return tail;
        else {
            Object[] node = tree.array;
            for (int s = shift; s > 0; s -= 5) {
                int i = (idx >>> s) & 0x01f;
                node = ((Node) node[i]).array;
            }
            return node;
        }
    }

    private Object lookup(int idx) {
        return nodeAt(idx)[idx & 0x01f];
    }

    public static HAMT fromVector(PersistentVector vec) {
        return new HAMT(vec, vec.meta(), vec.count(), vec.shift, vec.root, vec.tail);
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

    public HAMT concat(PersistentVector that) {
        if (tail.length == 32) {
            pushNodes(that);
        } else if (size == 0) {
            this.size = that.count();
            this.shift = that.shift;
            this.tree = that.root;
            this.tail = that.tail;
        } else {
            int count = that.count();
            int leftSize = tail.length;
            int rightSize;
            Object[] node;
            fillTail();
            for (int i = 0; i < count; i += 32) {
                node = that.arrayFor(i);
                int next = i + 32;
                int nodeLength = node.length;
                if (next > count && canContain(nodeLength)) {
                    System.arraycopy(node, 0, tail, leftSize, nodeLength);
                    this.size += nodeLength;
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

    public HAMT map(IFn f) {
        HAMT newVec = empty();
        for (int i = 0; i < size; i += 32) {
            Object[] node = nodeAt(i).clone();
            mapArray(node, f);
            newVec.pushNodeMut(node, node.length);
        }
        return newVec;
    }

    public HAMT take(int n) {
        HAMT newVec = empty();
        if (n <= 0) return newVec;
        else if (n >= size) return fromVector(vector);
        else if (n < 32) {
            Object[] newTail = new Object[n];
            System.arraycopy(nodeAt(0), 0, newTail, 0, n);
            newVec.tail = newTail;
            newVec.size += n;
        } else if ((n & 0x01f) == 0) {
            int totalNodes = n >>> 5;
            newVec.pushNodesMut(vector, 0, totalNodes);
        } else {
            int totalNodes = n >>> 5;
            int partial = n & 0x01f; // n % 32
            Object[] newTail = new Object[partial];
            newVec.pushNodesMut(vector, 0, totalNodes);
            System.arraycopy(nodeAt(totalNodes << 5), 0, newTail, 0, partial); // totalNodes * 32
            newVec.pushNodeMut(newTail, partial);
        }
        return newVec;
    }

    public HAMT drop(int n) {
        HAMT newVec = empty();
        if (n <= 0) return this;
        else if (n >= size) return newVec;
        else if ((n & 0x01f) == 0) {
            int from = n >>> 5; // n / 32
            int to = ((size - 1) >> 5) + 1;
            newVec.pushNodesMut(vector, from, to);
        } else {
            int from = (n >>> 5) << 5; // (floor n / 32) * 32
            int leftSize = n & 0x01f; // n % 32
            chunkedCopy(newVec, from, size, leftSize);
        }
        return newVec;
    }

    public HAMT takeWhile(IFn p) {
        return take(countWhile(p));
    }

    public HAMT dropWhile(IFn p) {
        return drop(countWhile(p));
    }

    public HAMT takeLastWhile(IFn p) {
        if (size == 0) return fromVector(vector);
        else if (!invokePred(p, lookup(size - 1))) return empty();
        else return drop(reverseCountWhile(p));
    }

    public HAMT dropLastWhile(IFn p) {
        if (size == 0 || !invokePred(p, lookup(size - 1))) return fromVector(vector);
        else return take(reverseCountWhile(p));
    }

    public HAMT slice(int from, int to) {
        HAMT newVec = empty();
        if (to <= 0 || from >= to) return newVec;
        else if (from < 0) return slice(0, to);
        else if ((from & 0x01f) == 0) {
            if (from + 32 < to) {
                int totalNodes = to >>> 5; // to / 32
                int partial = to & 0x01f; // to % 32
                newVec.pushNodesMut(vector, from >>> 5, totalNodes);
                Object[] arr = new Object[partial];
                System.arraycopy(nodeAt(totalNodes << 5), 0, arr, 0, partial); // totalNodes * 32
                newVec.pushNodeMut(arr, partial);
            } else chunkedCopy(newVec, from, to, from & 0x01f);
        } else {
            int start = (from >>> 5) << 5;
            chunkedCopy(newVec, start, to, from & 0x01f);
        }
        return newVec;
    }
}