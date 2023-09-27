package com.springboot.webflux.apirest.app.services;

import com.springboot.webflux.apirest.app.models.Producto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoService {

    Flux<Producto> findAll();
    Flux<Producto> findAllbyNameUpperCase();
    Flux<Producto> findAllbyNameUpperCaseRepet();
    Mono<Producto> findById(String id);
    Mono<Producto> save(Producto producto);
    Mono<Void> delete(Producto producto);
}
