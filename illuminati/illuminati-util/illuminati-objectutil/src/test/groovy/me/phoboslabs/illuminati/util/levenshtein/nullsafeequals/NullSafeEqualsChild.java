package me.phoboslabs.illuminati.util.levenshtein.nullsafeequals;

public class NullSafeEqualsChild {

    private final String foo = "fooValue";
    private final NullSafeEqualsGrandChild nullSafeEqualsGrandChild = new NullSafeEqualsGrandChild();
    private NullSafeEqualsGrandChild nullSafeEqualsGrandChildNull;
    private final int primitiveValue = 1;

    public String getFoo() {
        return this.foo;
    }
}
