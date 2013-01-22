class Simple implements Comparable<Simple> {
    private String foo;

    public int compareTo(Simple that) {
        return com.google.common.collect.ComparisonChain.start().compare(this.foo, that.foo).result();
    }
}