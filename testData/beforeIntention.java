import java.lang.Comparable;

public class Simple implements Comparable<Simple> {
    private boolean foo;

    public int compareTo(Simple that) {
        return com.google.common.collect.ComparisonChain.start().com<caret>pare(this.foo, that.foo).result();
    }
}