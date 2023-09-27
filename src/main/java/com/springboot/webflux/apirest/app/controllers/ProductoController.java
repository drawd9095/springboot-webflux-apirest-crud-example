package com.springboot.webflux.apirest.app.controllers;

import com.springboot.webflux.apirest.app.models.Producto;
import com.springboot.webflux.apirest.app.services.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<Producto>>> listar() {
        return Mono.just(ResponseEntity.ok()
                .contentType(APPLICATION_JSON)
                .body(service.findAll()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Producto>> verDetalle(@PathVariable String id) {
        return service.findById(id).map(p -> ResponseEntity.ok()
                        .contentType(APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Producto>> creaProducto(@RequestBody Producto producto) {

        if (producto.getCreateAt() == null) {
            producto.setCreateAt(new Date());
        }

        return service.save(producto).map(p -> ResponseEntity
                .created(URI.create("/api/productos/".concat(p.getId())))
                .contentType(APPLICATION_JSON)
                .body(p));
    }


    @PutMapping("/{id}")
    public Mono<ResponseEntity<Producto>> actualizar(
            @PathVariable String id,
            @RequestBody Producto producto) {
        return service.findById(id).flatMap(p -> {

            p.setNombre(producto.getNombre());
            p.setPrecio(producto.getPrecio());

            return service.save(p).map(pr -> ResponseEntity
                            .created(URI.create("/api/productos/".concat(pr.getId())))
                            .contentType(APPLICATION_JSON)
                            .body(pr))
                    .defaultIfEmpty(ResponseEntity.notFound().build());
        });
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> elminar(@PathVariable String id) {
        return service.findById(id).flatMap(p -> service.delete(p))
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND)));

    }

}
