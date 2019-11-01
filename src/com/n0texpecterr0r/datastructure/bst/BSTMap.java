package com.n0texpecterr0r.datastructure.bst;

/**
 * 二叉搜索树实现的 Map
 */
public class BSTMap<K, V>  {
    private Entry<K, V> root;
    private int size;

    private static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> left;
        Entry<K, V> right;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public void put(K key, V value) {
        if (root == null) {
            root = new Entry<>(key, value);
            size++;
            return;
        }
        Entry<K, V> parent = null;
        Entry<K, V> node = root;

        while (node != null) {
            parent = node;
            if (key.hashCode() < node.key.hashCode()) {
                node = node.left;
            } else if (key.hashCode() > node.key.hashCode()) {
                node = node.right;
            } else {
                node.value = value;
                return;
            }
        }
        if (key.hashCode() < parent.key.hashCode()) {
            size++;
            parent.left = new Entry<>(key, value);
        } else {
            size++;
            parent.right = new Entry<>(key, value);
        }
    }

    public V get(K key) {
        Entry<K, V> node = root;
        while (node != null) {
            if (key.hashCode() == node.key.hashCode()) {
                return node.value;
            } else if (key.hashCode() < node.key.hashCode()) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return null;
    }

    public void remove(K key) {
        Entry<K, V> parent = null;
        Entry<K, V> node = root;
        while(node != null) {
            if (key.hashCode() == node.key.hashCode()) {
                deleteNode(parent, node);
                size--;
                return;
            } else if (key.hashCode() < node.key.hashCode()) {
                parent = node;
                node = node.left;
            } else {
                parent = node;
                node = node.right;
            }
        }
    }

    private void deleteNode(Entry<K, V> parent, Entry<K, V> node) {
         if (node.left == null) {
             // 节点无左子树，只需要让 parent 指向其 right
             if (node.key.hashCode() <  parent.key.hashCode()) {
                 parent.left = node.right;
             } else {
                 parent.right = node.right;
             }
             return;
         } else if (node.right == null) {
             // 节点无右子树，只需要让 parent 指向其 left
             if (node.key.hashCode() <  parent.key.hashCode()) {
                 parent.left = node.left;
             } else {
                 parent.right = node.left;
             }
             return;
         } else {
             // 节点左右均存在，找到比其大的最小值或者比其小的最大值（这里找比其大的最小值）
             Entry<K, V> leftParent = node;
             Entry<K, V> leftNode = node.right;
             while (leftNode.left != null) {
                 leftParent = leftNode;
                 leftNode = leftNode.left;
             }
             node.value = leftNode.value;
             deleteNode(leftParent, leftNode);
         }
    }

    public static void main(String[] args) {
        BSTMap<String, Integer> map = new BSTMap<>();
        map.put("haha", 1532);
        map.put("hehe", 25432);
        map.put("test", 3141);
        map.put("N0tExpectErr0r", 10000);

        System.out.println(map.get("hehe"));
        System.out.println("size:" + map.size);
        map.remove("hehe");
        System.out.println(map.get("hehe"));
        System.out.println("size:" + map.size);
    }
}
