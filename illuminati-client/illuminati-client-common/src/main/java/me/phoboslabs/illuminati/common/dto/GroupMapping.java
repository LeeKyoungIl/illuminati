package me.phoboslabs.illuminati.common.dto;

import me.phoboslabs.illuminati.common.dto.enums.MappingType;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface GroupMapping {

    MappingType mappingType();
}
