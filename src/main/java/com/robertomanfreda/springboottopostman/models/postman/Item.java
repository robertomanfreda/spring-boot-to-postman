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
public class Item {
    private String name;
    private Request request;
    private List<String> response;
}
