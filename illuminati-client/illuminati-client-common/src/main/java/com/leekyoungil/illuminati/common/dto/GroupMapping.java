package com.leekyoungil.illuminati.common.dto;

import com.leekyoungil.illuminati.common.dto.enums.MappingType;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface GroupMapping {

    MappingType mappingType();
}
