package com.robertomanfreda.springboottopostman.models.postman;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Info {
    private String _postman_id;
    private String name;
    private String schema;
}
