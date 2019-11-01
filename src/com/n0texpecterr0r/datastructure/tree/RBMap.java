package com.n0texpecterr0r.datastructure.tree;

/**
 * 红黑树实现的Map
 *
 * 红黑树比较重要的是它的规则：
 * 1.每个节点不是红色就是黑色的
 * 2.根节点总是黑色
 * 3.如果节点是红色的，则它的子节点必须是黑色的（反之不一定）
 * 4.从根节点到叶节点或空子节点的每条路径，必须包含相同数目的黑色节点（即相同的黑色高度）
 * 5.插入的新节点都是红色的（NULL节点必为黑色)
 */
public class RBMap<K, V> {
    public static final boolean RED = true;
    public static final boolean BLACK = false;

    private Entry<K, V> root;
    private int size;

    private static class Entry<K, V> {
        K key;
        V value;
        boolean color;
        Entry<K, V> left;
        Entry<K, V> right;
        Entry<K, V> parent;

        public Entry(K key, V value, Entry<K, V> parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
            this.color = RED;
        }
    }

    /**
     * 插入逻辑与 BST 基本相同，只是需要在插入结束后对红黑树进行重整
     */
    public void put(K key, V value) {
        Entry<K, V> parent = null;
        Entry<K, V> node = root;

        // 寻找插入位置
        while (node != null) {
            parent = node;
            if (key.hashCode() < node.key.hashCode()) {
                node = node.left;
            } else if (key.hashCode() > node.key.hashCode()){
                node = node.right;
            } else {
                // 找到对应位置，直接替换值，不需要再进行重整
                node.value = value;
                return;
            }
        }

        // 插入节点
        Entry<K, V> toInsert = new Entry<>(key, value, parent);
        if (parent == null) {
            // 说明还没有根节点，插入根节点
            this.root = toInsert;
            size++;
        } else if (key.hashCode() < parent.key.hashCode()) {
            // 应当插入到parent左侧
            parent.left = toInsert;
            size++;
        } else {
            // 应当插入到parent右侧
            parent.right = toInsert;
            size++;
        }
        fixupAfterPut(toInsert);
    }

    /**
     * 插入后重整红黑树，这里涉及到何时进行变色及旋转
     * 最坏情况为3->4->5
     * 有以下的情况
     * （下面基于在祖父节点左侧插入的情况，右侧则需要镜像操作）
     * 1. 若第一次插入，则为root节点，只需要将其颜色从红色变为黑色
     *
     * 2. 插入的节点父节点为黑色，则不需要进行任何操作，仍然满足红黑树的规则
     *
     * 3. 当前的节点父节点与叔叔节点（父节点的兄弟）均为红色，
     *    此时我们只需要将父节点及叔叔节点涂黑，祖父节点涂红，
     *    之后将祖父节点置为当前节点，从祖父节点继续进行重整操作。
     *   （此时祖父节点可能与其父节点仍有连续关系，需要递归向上重整）
     *
     * 4. 当前的节点父节点为红色，叔叔节点为黑色（空节点也是黑色），
     *    插入后祖父节点、父节点、新节点不在同一条线上                                            /
     *    以父节点为支点进行左旋，这时就父节点就成为了当前节点的左子节点，                          \
     *    祖父节点、父节点、新节点在同一条线上，变为了情况5
     *
     * 5. 当前的节点父节点为红色，叔叔节点为黑色，插入后祖父节点、父节点、新节点在同一条线上          /
     *    以祖父节点为支点进行右旋，同时互换父节点与祖父节点的颜色，此时父节点替代了                /
     *    祖父节点，并且祖父节点与新节点互为兄弟节点。从而继续达到平衡
     */
    private void fixupAfterPut(Entry<K, V> node) {
        while (node != null && node != root && colorOf(parentOf(node)) == RED) {
            // 祖父节点不可能为 null（否则parent不可能为红色）
            if (parentOf(node) == leftOf(parentOf(parentOf(node)))) {
                // 左侧插入
                Entry<K, V> uncle = rightOf(parentOf(parentOf(node)));
                if (colorOf(uncle) == RED) {
                    // 叔叔也是红色，变色
                    setColor(parentOf(node), BLACK);
                    setColor(uncle, BLACK);
                    setColor(parentOf(parentOf(node)), RED);
                    node = parentOf(parentOf(node));
                } else {
                    if (node == rightOf(parentOf(node))) {
                        // 情况4，需要左旋
                        node = parentOf(node);
                        leftRotate(node);
                    }
                    // 情况5，互换父节点与祖父节点的颜色，并对祖父节点进行右旋
                    setColor(parentOf(node), BLACK);
                    setColor(parentOf(parentOf(node)), RED);
                    rightRotate(parentOf(parentOf(node)));
                }
            } else {
                // 右侧插入
                Entry<K, V> uncle = leftOf(parentOf(parentOf(node)));
                if (colorOf(uncle) == RED) {
                    // 叔叔也是红色，变色
                    setColor(parentOf(node), BLACK);
                    setColor(uncle, BLACK);
                    setColor(parentOf(parentOf(node)), RED);
                    node = parentOf(parentOf(node));
                } else {
                    if (node == leftOf(parentOf(node))) {
                        // 情况4，需要右旋
                        node = parentOf(node);
                        rightRotate(node);
                    }
                    // 情况5，互换父节点与祖父节点的颜色，并对祖父节点进行左旋
                    setColor(parentOf(node), BLACK);
                    setColor(parentOf(parentOf(node)), RED);
                    leftRotate(parentOf(parentOf(node)));
                }
            }
        }
        // 每次都重新将 root 置为黑色
        setColor(root, BLACK);
    }

    /**
     * 查询操作，与BST及AVL相同
     */
    public V get(K key) {
        Entry<K, V> node = findNode(root, key);
        return node == null ? null : node.value;
    }

    /**
     * 递归在root的子树中寻找key对应的value
     */
    private Entry<K, V> findNode(Entry<K, V> root, K key) {
        if (root == null) {
            return null;
        }
        if (key.hashCode() < root.key.hashCode()) {
            return findNode(root.left, key);
        } else if (key.hashCode() > root.key.hashCode()){
            return findNode(root.right, key);
        } else {
            return root;
        }
    }

    public void remove(K key) {
        Entry<K, V> node = findNode(root, key);
        if (node != null) {
            deleteNode(node);
            size--;
        }
    }

    /**
     * 与BST的删除比较像，只是需要注意，若删除的节点是黑色，需要进行重整
     */
    private void deleteNode(Entry<K, V> node) {
        if (node.left != null && node.right != null) {
            // 左右子节点均不为null，则需要找到子树中比它大的最小值或比它小的最大值
            Entry<K, V> leftNode = node.right;
            while (leftNode.left != null) {
                leftNode = leftNode.left;
            }
            // 将该节点的值复制过来，并删除该节点
            node.key = leftNode.key;
            node.value = leftNode.value;
            node = leftNode;
        }

        Entry<K, V> replace = node.left != null ? node.left : node.right;
        // 此时子节点一定有一个为 null，用不为 null 的子节点来替代
        if (replace != null) {
            replace.parent = node.parent;
            if (node.parent == null) {
                // 如果删除的节点是根节点，用replace替代
                root = replace;
            } else if (node == node.parent.left) {
                // 替换 parent 左节点
                node.parent.left = replace;
            } else {
                // 替换 parent 右节点
                node.parent.right = replace;
            }
            node.left = null;
            node.right = null;
            node.parent = null;
            if (node.color == BLACK) {
                // 如果删除了黑色节点，需要重整
                fixupAfterRemove(replace);
            }
        } else if (node.parent == null) {
            // 删除的是根节点，则用null替代
            root = null;
        } else {
            // 删除的节点没有子节点，并且该节点不是根节点
            if (node.color == BLACK) {
                // 如果删除的是黑色节点，需要先进行重整
                fixupAfterRemove(node);
            }
            // 从父节点中删除
            if (node.parent != null) {
                if (node == node.parent.left) {
                    node.parent.left = null;
                } else if (node == node.parent.right) {
                    node.parent.right = null;
                }
                node.parent = null;
            }
        }
    }

    /**
     * 删除后若删除的节点是黑节点，会影响平衡，进行重整
     * 最坏情况为 2->5->6，共三次调整
     * 存在下列几种情况
     * 不够详细可以看这篇文章：https://segmentfault.com/a/1190000012728513
     *
     * 1. 删除的是根节点，只需用空节点替换根节点即可
     *
     * 2. 该节点的兄弟节点为红色，其余节点为黑色
     *    可以对该节点的父节点进行左旋，之后互换父节点与兄弟节点的颜色
     *    此时黑色节点数仍不满足条件，但该情况转变为了4、5、6的情况（兄弟节点为黑色）
     *
     * 3. 该节点的父节点、兄弟节点及兄弟节点的子节点都是黑色，可以将兄弟节点染成红色
     *    这样就可以使得黑色节点数一致了。不过这时通过父节点的路径比不通过父节点的路径少了一个颜色
     *    此时可以从情况1开始对父节点重新进行平衡处理
     *
     * 4. 该节点的父节点为红色，兄弟节点和兄弟节点的子节点是黑色。此时可以交换父节点和兄弟节点的颜色
     *    这样的话不会影响其他路径，又维持了平衡
     *
     * 5. 该节点为父节点的左节点，父节点颜色无所谓，兄弟节点为黑色，兄弟节点的左孩子为红色，右孩子为黑色。
     *    此时可以对兄弟节点进行右旋，并且互换它与它左孩子的颜色。此时可以交由情况6处理
     *
     * 6. 该节点为父节点左节点，父节点无所谓，兄弟节点为黑色，兄弟节点的右节点为红色。
     *    此时可以对父节点进行左旋，并交换其与兄弟节点的颜色且将兄弟节点的右子节点颜色变为黑色
     *    （对父节点进行左旋并交换其与兄弟节点的颜色，可以使得经过当前节点的路径上的黑色节点数恢复原状
     *    而对于原兄弟节点的右子节点，只需将其变为黑色即可恢复原经过兄弟节点的路径的黑色数目。）
     */
    private void fixupAfterRemove(Entry<K, V> node) {
        while (node != root && colorOf(node) != BLACK) {
            if (node == leftOf(parentOf(node))) {
                Entry<K, V> bro = rightOf(parentOf(node));
                if (colorOf(bro) == RED) {
                    // 情况2（bro为红色），结束后会变为4、5、6中一种
                    // （4、5、6中bro均为黑色）
                    setColor(bro, BLACK);
                    setColor(parentOf(node), RED);
                    leftRotate(parentOf(node));
                    bro = rightOf(parentOf(node));
                }

                if (colorOf(leftOf(bro)) == BLACK
                        && colorOf(rightOf(bro)) == BLACK) {
                    // 情况3、4（重新对父节点平衡处理）
                    // 此处对于情况4来说，虽然只是设置了兄弟节点为红色，
                    // 但下一轮循环时，由于父节点往上均是满足条件的，因此父节点在最后会被设置为黑色
                    setColor(bro, RED);
                    node = parentOf(node);
                } else {
                    if (colorOf(rightOf(bro)) == BLACK) {
                        // 情况5（说明bro的左红右黑），结束后会变为情况6
                        setColor(leftOf(bro), BLACK);
                        setColor(bro, RED);
                        rightRotate(bro);
                        bro = rightOf(parentOf(node));
                    }
                    // 情况6（说明bro右红），左旋，并且交换父节点与兄弟节点颜色
                    setColor(bro, colorOf(parentOf(node)));
                    setColor(parentOf(node), BLACK);    // 兄弟节点一定是黑色（否则已经不满足红黑树条件）
                    setColor(rightOf(bro), BLACK);      // 将兄弟节点右子节点置为黑色
                    leftRotate(parentOf(node));
                    node = root;
                }
            } else {
                Entry<K, V> bro = leftOf(parentOf(node));
                if (colorOf(bro) == RED) {
                    // 情况2
                    setColor(bro, BLACK);
                    setColor(parentOf(node), RED);
                    rightRotate(parentOf(node));
                    bro = leftOf(parentOf(node));
                }

                if (colorOf(leftOf(bro)) == BLACK
                        && colorOf(rightOf(bro)) == BLACK) {
                    // 情况3、4
                    setColor(bro, RED);
                    node = parentOf(node);
                } else {
                    if (colorOf(leftOf(bro)) == BLACK) {
                        // 情况5
                        setColor(rightOf(bro), BLACK);
                        setColor(bro, RED);
                        leftRotate(bro);
                        bro = leftOf(parentOf(node));
                    }
                    // 情况6
                    setColor(bro, colorOf(parentOf(node)));
                    setColor(parentOf(node), BLACK);
                    setColor(leftOf(bro), BLACK);
                    rightRotate(parentOf(node));
                    node = root;
                }
            }
        }
        setColor(node, BLACK);
    }

    public int size() {
        return size;
    }

    public int depth() {
        return depthRecursive(root);
    }

    private int depthRecursive(Entry<K, V> root) {
        if (root == null) {
            return 0;
        }
        return Math.max(depthRecursive(root.left), depthRecursive(root.right)) + 1;
    }

    private void setColor(Entry<K, V> node, boolean color) {
        if (node != null) {
            node.color = color;
        }
    }

    private boolean colorOf(Entry<K, V> node) {
        return node == null ? BLACK : node.color;
    }

    private Entry<K, V> parentOf(Entry<K, V> node) {
        return node == null ? null : node.parent;
    }

    private Entry<K, V> leftOf(Entry<K, V> node) {
        return node == null ? null : node.left;
    }

    private Entry<K, V> rightOf(Entry<K, V> node) {
        return node == null ? null : node.right;
    }

    /**
     * 左旋操作，将新root的left给旧root的right，旧root变为新root的left
     */
    private void leftRotate(Entry<K, V> root) {
        Entry<K, V> newRoot = root.right;
        root.right = newRoot.left;

        if (newRoot.left != null) {
            newRoot.left.parent = root;
        }
        newRoot.parent = root.parent;

        if (root.parent == null) {
            this.root = newRoot;
        } else if (root == root.parent.left) {
            root.parent.left = newRoot;
        } else {
            root.parent.right = newRoot;
        }
        newRoot.left = root;
        root.parent = newRoot;
    }

    /**
     * 右旋操作，将新root的right给旧root的left，旧root变为新root的right
     */
    private void rightRotate(Entry<K, V> root) {
        Entry<K, V> newRoot = root.left;
        root.left = newRoot.right;

        if (newRoot.right != null) {
            newRoot.right.parent = root;
        }
        newRoot.parent = root.parent;

        if (root.parent == null) {
            this.root = newRoot;
        } else if (root == root.parent.left) {
            root.parent.left = newRoot;
        } else {
            root.parent.right = newRoot;
        }
        newRoot.right = root;
        root.parent = newRoot;
    }

    public static void main(String[] args) {
        RBMap<String, Integer> map = new RBMap<>();
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
