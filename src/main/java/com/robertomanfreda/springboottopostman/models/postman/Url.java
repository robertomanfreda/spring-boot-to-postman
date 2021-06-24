package com.robertomanfreda.springboottopostman.models.postman;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Url {
    private String raw;
    private String protocol;
    // TODO host
    private List<String> host;
    private String port;
    // TODO path
    private List<String> path;
}