package com.n0texpecterr0r.datastructure.linear;

/**
 * 基于双向循环链表实现的LinkedList
 */
public class LinkedList<T> {
    private Entry<T> head;
    private int length;

    private static class Entry<T> {
        T data;
        Entry<T> prev;
        Entry<T> next;

        public Entry(T data) {
            this.data = data;
        }
    }

    public void add(T data) {
        length++;
        Entry<T> insert = new Entry<>(data);
        if (head == null) {
            head = insert;
            head.next = head;
            head.prev = head;
        } else {
            Entry<T> tail = head.prev;
            head.prev = insert;
            insert.next = head;
            insert.prev = tail;
            tail.next = insert;
        }
    }

    public void add(int index, T data) {
        checkBounds(index);
        length++;
        Entry<T> insert = new Entry<>(data);

        if (index == 0 && head == null) {
            head = insert;
            head.next = head;
            head.prev = head;
        } else {
            Entry<T> node = head;
            if (index == 0) {
                head = insert;
            }

            for (int i = 0; i < index; i++) {
                node = node.next;
            }
            insert.prev = node.prev;
            node.prev.next = insert;
            node.prev = insert;
            insert.next = node;
        }
    }

    public void addAll(LinkedList<T> list) {
        for (int i = 0; i < list.length; i++) {
            add(list.get(i));
        }
    }

    public void remove(T data) {
        if (head == null) {
            return;
        }
        Entry<T> node = head;
        boolean found = false;
        do {
            if (node.data == data) {
                found = true;
                break;
            }
            node = node.next;
        } while (node != head);

        if (found) {
            if (node == head) {
                head = node.next;
            }
            node.prev.next = node.next;
            node.next.prev = node.prev;
            node.prev = null;
            node.next = null;
            node = null;
            length--;
        }
    }

    public T get(int index) {
        checkBounds(index);
        // 根据位置从尾端或头端进行获取
        if (index >= length / 2) {
            Entry<T> node = head;
            for (int i = 0; i < index; i++) {
               node = node.next;
            }
            return node.data;
        } else {
            Entry<T> node = head.prev;
            for (int i = 0; i < length - index - 1; i++) {
                node = node.prev;
            }
            return node.data;
        }
    }

    public boolean contains(T data) {
        return indexOf(data) > 0;
    }

    public int indexOf(T data) {
        Entry<T> node = head;
        int index = 0;
        do {
            if (node.data == data) {
                return index;
            }
            index++;
            node = node.next;
        } while (node != head);
        return -1;
    }

    public int size() {
        return length;
    }

    private void checkBounds(int index) {
        if (index > length || index < 0) {
            throw new IndexOutOfBoundsException("index: " + index + " length: " + length);
        }
    }

    public static void main(String[] args) {
        LinkedList<Integer> list = new LinkedList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i + 1);
        }
        System.out.println("index of 25: " + list.indexOf(25));
        list.remove(15);
        list.add(10, 260);
        System.out.println("index of 25: " + list.indexOf(25));
        System.out.println(list.contains(16));

        for (int i = 0; i < list.size(); i++) {
            System.out.print(list.get(i) + " ");
        }
    }
}
