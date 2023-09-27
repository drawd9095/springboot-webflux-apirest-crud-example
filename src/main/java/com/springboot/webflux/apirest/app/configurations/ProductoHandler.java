package com.springboot.webflux.apirest.app.configurations;

import com.mongodb.internal.connection.Server;
import com.springboot.webflux.apirest.app.models.Producto;
import com.springboot.webflux.apirest.app.services.ProductoService;
import lombok.RequiredArgsConstructor;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class ProductoHandler {

    private final ProductoService service;

    public Mono<ServerResponse> listar(ServerRequest request){
        return ServerResponse
                .ok()
                .contentType(APPLICATION_JSON)
                .body(service.findAll(), Producto.class);
    }

    public Mono<ServerResponse> ver(ServerRequest request){

        String id = request.pathVariable("id");
        return service.findById(id).flatMap(p-> ServerResponse
                .ok()
                .contentType(APPLICATION_JSON)
                .body(fromValue(p)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> crear(ServerRequest request){

        Mono<Producto> producto = request.bodyToMono(Producto.class);

        return producto.flatMap(p-> {
            if(p.getCreateAt() == null){
                p.setCreateAt(new Date());
            }
            return service.save(p);
        }).flatMap(p-> ServerResponse
                .created(URI.create("api/v2/productos/".concat(p.getId())))
                .contentType(APPLICATION_JSON)
                .body(fromValue(p)));
    }

    public Mono<ServerResponse> editar(ServerRequest request){
        Mono<Producto> producto = request.bodyToMono(Producto.class);
        String id = request.pathVariable("id");
        Mono<Producto> productoDb = service.findById(id);

        return productoDb.zipWith(producto,(db,req) ->{
           db.setNombre(req.getNombre());
           db.setPrecio(req.getPrecio());
           return db;
        }).flatMap(p -> ServerResponse
                .created(URI.create("api/v2/productos/".concat(p.getId())))
                .contentType(APPLICATION_JSON)
                .body(service.save(p),Producto.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> eliminar(ServerRequest request){
        String id = request.pathVariable("id");

        Mono<Producto> productoDb = service.findById(id);

        return productoDb.flatMap(p -> service.delete(p)
                .then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
