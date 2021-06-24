package com.robertomanfreda.springboottopostman.models.parent;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ServletFilter {
    private List<String> servletNameMappings;
    private List<String> urlPatternMappings;
    private String name;
    private String classname;
}
