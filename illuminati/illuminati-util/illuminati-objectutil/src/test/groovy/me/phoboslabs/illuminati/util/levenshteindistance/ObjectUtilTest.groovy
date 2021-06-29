package me.phoboslabs.illuminati.util.levenshteindistance

import me.phoboslabs.illuminati.util.objectutil.nullsafeequals.NullSafeEqualsParent
import spock.lang.Shared
import spock.lang.Specification

class ObjectUtilTest extends Specification {

    @Shared NullSafeEqualsParent nullSafeEqualsParent = new NullSafeEqualsParent();

    def "Object Util Test" () {
        expect:
        ObjectUtil.nullSafeEquals(nullSafeEqualsParent, targetPath, value) == result

        where:
        targetPath | value | result
        "foo" | "parentFoo" | true
        "nullSafeEqualsChildPrivate.foo" | "fooValue" | true
        "nullSafeEqualsChildPrivate.nullSafeEqualsGrandChild.var" | "varValue" | true
        "nullSafeEqualsChildPrivate.foo" | "foo1Value" | false
        "nullSafeEqualsChildPrivate.nullSafeEqualsGrandChild.var" | "var1Value" | false
        "nullSafeEqualsChildPrivate.primitiveValue" | 1 | true
        "nullSafeEqualsChildPrivate.primitiveValue" | 12 | false
        "nullSafeEqualsChildPublic" | null | true
        "nullSafeEqualsChildPrivate.nullSafeEqualsGrandChildNull" | null | true
        "nullSafeEqualsChildPrivate.nullSafeEqualsGrandChildNull.var" | "varValue" | false
        "STATIC_FINAL_FOO" | "foo" | true
        "STATIC_FOO" | "foo" | true
        "NullSafeEqualsParent.PUBLIC_STATIC_FINAL_FOO" | "foo" | true
        "NullSafeEqualsParent.STATIC_FINAL_FOO" | "foo" | true
        "NullSafeEqualsParent.PUBLIC_STATIC_FINAL_FOO" | null | false
        "NullSafeEqualsParent.PUBLIC_STATIC_FINAL_FOO" | "bar" | false
        "NullSafeEqualsParent.PUBLIC_STATIC_NULL_FOO" | "foo" | false
        "NullSafeEqualsParent.PUBLIC_STATIC_NULL_FOO" | null | true
    }
}