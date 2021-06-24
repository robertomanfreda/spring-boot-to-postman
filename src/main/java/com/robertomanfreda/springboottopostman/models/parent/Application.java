package com.robertomanfreda.springboottopostman.models.parent;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Application {
    private Mappings mappings;
    private String parentId;
}
