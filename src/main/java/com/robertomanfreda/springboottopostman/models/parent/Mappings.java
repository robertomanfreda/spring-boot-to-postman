package com.robertomanfreda.springboottopostman.models.parent;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Mappings {
    private DispatcherServlets dispatcherServlets;
    private List<ServletFilter> servletFilters;
    private List<Servlets> servlets;
}