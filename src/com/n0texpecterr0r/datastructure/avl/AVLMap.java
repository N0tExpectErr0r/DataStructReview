package com.n0texpecterr0r.datastructure.avl;

/**
 * 基于 AVL 树实现的Map
 */
public class AVLMap<K, V> {
    private Entry<K, V> root;
    private int size;

    private static class Entry<K, V> {
        K key;
        V value;
        int depth;
        Entry<K, V> left;
        Entry<K, V> right;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.depth = 1;
        }
    }

    public void put(K key, V value) {
        root = putRecursive(root, key, value);
    }

    /**
     * 在root的子树中递归插入节点
     */
    private Entry<K, V> putRecursive(Entry<K, V> root, K key, V value) {
        if (root == null) {
            size++;
            return new Entry<>(key, value);
        }
        if (key.hashCode() < root.key.hashCode()) {
            root.left = putRecursive(root.left, key, value);
        } else if (key.hashCode() > root.key.hashCode()){
            root.right = putRecursive(root.right, key, value);
        } else {
            root.value = value;
            return root;
        }
        // 在递归返回时，对于插入节点的每个祖先节点进行重平衡并重新计算深度
        root.depth = Math.max(depth(root.left), depth(root.right)) + 1;
        return balance(root, key);
    }

    public void remove(K key) {
        root = removeRecursive(root, key);
    }

    /**
     * 删除节点，与二叉搜索树思路差不多，只是需要将删除的节点的所有祖先节点重新进行平衡
     */
    private Entry<K, V> removeRecursive(Entry<K, V> root, K key) {
        if (root == null) {
            return null;
        }
        if (key.hashCode() < root.key.hashCode()) {
            root.left = removeRecursive(root.left, key);
        } else if (key.hashCode() > root.key.hashCode()) {
            root.right = removeRecursive(root.right, key);
        } else {
            // key 的 hashCode 相同，找到了对应的节点
            if (root.left == null) {
                size--;
                // 节点无左子树，只需要让 root 变为其 right 节点
                root = root.right;
            } else if (root.right == null) {
                size--;
                // 节点无右子树，只需要让 root 变为其 left 节点
                root = root.left;
            } else {
                size--;
                // 节点有左右子树，则需要找到比其大的最小值或比其小的最大值
                Entry<K, V> node = root.right;
                while (node.left != null) {
                    node = node.left;
                }
                // 将该位置的节点的数据复制到当前删除的节点
                root.key = node.key;
                root.value = node.value;
                // 将原先该位置的节点递归删除
                root.right = removeRecursive(root.right, key);
            }
        }
        if (root == null) {
            return null;
        }
        // 在递归返回时，对于插入节点的每个祖先节点进行重平衡并重新计算深度
        root.depth = Math.max(depth(root.left), depth(root.right)) + 1;
        return balance(root, key);
    }

    /**
     * 不平衡的状态下共有四种情况
     * 1.       a           2.        a            3.       a           4.      a
     *         /                     /                       \                   \
     *        b                     b                         b                   b
     *       /                       \                       /                     \
     *      *                         *                     *                       *
     * 其中 * 代表新插入的节点，可以看到，对于 1,4 两种情况，只需要对节点a分别进行一次右旋/左旋，即可使得树变回平衡
     * 而对于 2,3两种情况，可以先对于中间节点b分别进行一次左旋/右旋，
     * 之后就变成了 1,4 的状态，只需要继续对节点 a 进行一次右旋/左旋即可了
     */
    private Entry<K, V> balance(Entry<K, V> root, K key) {
        int balanceFactor = depth(root.left) - depth(root.right);
        // 情况1，左边更深，且插入在了最左节点的左节点，直接右旋
        if (balanceFactor > 1 && key.hashCode() < root.left.key.hashCode()) {
            // 对根节点 a 进行右旋
            return rightRotate(root);
        }
        // 情况4，右边更深，且插入在了最右节点的右节点，直接左旋
        if (balanceFactor < -1 && key.hashCode() > root.right.key.hashCode()) {
            // 对根节点 a 进行左旋
            return leftRotate(root);
        }
        // 情况2，左边更深，但插入在了最左节点的右节点，需要先左旋回到情况1，之后进行右旋
        if (balanceFactor > 1 && key.hashCode() > root.left.key.hashCode()) {
            // 对中间节点 b 进行左旋
            root.left = leftRotate(root.left);
            // 再对根节点 a 进行右旋
            return rightRotate(root);
        }
        // 情况3，右边更深，但插入在了最右节点的左节点，需要先右旋回到情况4，之后进行左旋
        if (balanceFactor < -1 && key.hashCode() < root.right.key.hashCode()) {
            // 对中间节点 b 进行右旋
            root.right = rightRotate(root.right);
            // 再对根节点 a 进行左旋
            return leftRotate(root);
        }
        return root;
    }

    /**
     * 左旋，原root的right变为新root
     * 新root的left变为原root的right
     */
    private Entry<K, V> leftRotate(Entry<K, V> root) {
        Entry<K, V> newRoot = root.right;
        Entry<K, V> newRight = newRoot.left;
        newRoot.left = root;
        root.right = newRight;
        // 这样不会改变left right节点的深度
        caculateDepth(root);
        caculateDepth(newRoot);
        return newRoot;
    }

    /**
     * 右旋，原root的left变为新root
     * 新root的right变为原root的left
     */
    private Entry<K, V> rightRotate(Entry<K, V> root) {
        Entry<K, V> newRoot = root.left;
        Entry<K, V> newLeft = newRoot.right;
        newRoot.right = root;
        root.left = newLeft;
        // 这样不会改变left right节点的深度
        caculateDepth(root);
        caculateDepth(newRoot);
        return newRoot;
    }

    public V get(K key) {
        return getRecursive(root, key);
    }

    /**
     * 递归在root的子树中寻找key对于的value
     */
    private V getRecursive(Entry<K, V> root, K key) {
        if (root == null) {
            return null;
        }
        if (key.hashCode() < root.key.hashCode()) {
            return getRecursive(root.left, key);
        } else if (key.hashCode() > root.key.hashCode()){
            return getRecursive(root.right, key);
        } else {
            return root.value;
        }
    }

    public int size() {
        return size;
    }

    public int depth() {
        return depth(root);
    }

    private void caculateDepth(Entry<K, V> root) {
        root.depth = Math.max(depth(root.left), depth(root.right)) + 1;
    }

    private int depth(Entry<K, V> root) {
        return root == null ? 0 : root.depth;
    }

    public static void main(String[] args) {
        AVLMap<String, Integer> map = new AVLMap<>();
        map.put("haha", 1532);
        map.put("hehe", 25432);
        map.put("test", 3141);
        map.put("N0tExpectErr0r", 10000);
        map.put("Test", 10002);
        map.put("AAA", 12345);
        map.put("BBB", 14134);
        map.put("CCC", 131312);

        System.out.println(map.get("hehe"));
        System.out.println("size:" + map.size());
        System.out.println("depth:" + map.depth());
        map.remove("test");
        map.remove("BBB");
        map.remove("ADASD");
        System.out.println(map.get("hehe"));
        System.out.println("size:" + map.size());
        System.out.println("depth:" + map.depth());
    }
}
