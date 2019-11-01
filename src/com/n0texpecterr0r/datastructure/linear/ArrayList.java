package com.n0texpecterr0r.datastructure.linear;

public class ArrayList<T> {
    public static final int INIT_CAPACITY = 10;

    private Object[] datas;
    private int capacity;
    private int length;

    public ArrayList() {
        this.capacity = INIT_CAPACITY;
        this.datas = new Object[capacity];
    }

    public ArrayList(int capacity) {
        this.capacity = capacity;
        this.datas = new Object[capacity];
    }

    public void add(T data) {
        ensureCapacityEnough(length + 1);
        datas[length++] = data;
    }

    public void addAll(ArrayList<T> list) {
        ensureCapacityEnough(length + list.length);
        for (Object data : list.toArray()) {
            datas[length++] = data;
        }
    }

    public void remove(T data) {
        int index = indexOf(data);
        if (index < 0)
            return;
        System.arraycopy(datas, index + 1, datas, index, length - index - 1);
        length -= 1;
    }

    public T get(int index) {
        if (index > length || index < 0) {
            throw new IndexOutOfBoundsException("index: " + index + " length: " + length);
        }
        return (T) datas[index];
    }

    public boolean contains(T data) {
        return indexOf(data) > 0;
    }

    public int indexOf(T data) {
        for (int i = 0; i < datas.length; i++) {
            if (datas[i] == data) {
                return i;
            }
        }
        return -1;
    }

    public int size() {
        return length;
    }

    public T[] toArray() {
        return (T[]) datas;
    }

    private void ensureCapacityEnough(int len) {
        if (len >= capacity) {
            resize();
        }
    }

    private void resize() {
        if (capacity >= Integer.MAX_VALUE >> 1) {
            throw new OutOfMemoryError("array too large");
        }
        Object[] old = datas;
        capacity = capacity << 1;
        datas = new Object[capacity];
        System.arraycopy(old, 0, datas, 0, old.length);
    }

    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i + 1);
        }
        System.out.println("index of 25: " + list.indexOf(25));
        list.remove(15);
        System.out.println("index of 25: " + list.indexOf(25));
        System.out.println(list.contains(16));

        for (int i = 0; i < list.size(); i++) {
            System.out.print(list.get(i) + " ");
        }
    }
}
