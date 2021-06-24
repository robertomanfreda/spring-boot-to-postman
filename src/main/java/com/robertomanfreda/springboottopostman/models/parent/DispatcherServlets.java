package com.robertomanfreda.springboottopostman.models.parent;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DispatcherServlets {
    private List<DispatcherServlet> dispatcherServlet;
}