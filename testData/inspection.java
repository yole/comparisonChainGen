class Simple implements Comparable<Simple> {
    private String foo;

    public boolean equals(Object other) {
        return other instanceof Simple && ((Simple)other).foo.equals(foo);
    }

    public int <warning descr="The compareTo() method does not reference 'foo' which is referenced from equals(); inconsistency may result">compareTo</warning>(Simple other) {
        return 0;
    }
}