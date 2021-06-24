package com.robertomanfreda.springboottopostman.models.parent;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DispatcherServlet {
    private String handler;
    private String predicate;
    private Details details;
}
