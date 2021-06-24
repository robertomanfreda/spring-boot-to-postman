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
public class Request {
    private String method;
    // TODO header
    private List<String> header;
    private Url url;
}