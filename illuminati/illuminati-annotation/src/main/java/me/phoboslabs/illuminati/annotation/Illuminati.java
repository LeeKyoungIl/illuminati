package me.phoboslabs.illuminati.annotation;

import me.phoboslabs.illuminati.annotation.enums.PackageType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 18/07/2017.
 */
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Illuminati {

    boolean ignore () default false;

    int samplingRate () default 0;

    PackageType packageType () default PackageType.DEFAULT;
}
