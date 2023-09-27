package com.springboot.webflux.apirest.app;

import com.springboot.webflux.apirest.app.models.Producto;
import com.springboot.webflux.apirest.app.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class SpringbootWebfluxApirestApplication implements CommandLineRunner {

	private final ProductoRepository productoRepository;
	private final ReactiveMongoTemplate mongoTemplate;

	public static void main(String[] args) {
		SpringApplication.run(SpringbootWebfluxApirestApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		mongoTemplate.dropCollection("productos").subscribe();
		Flux.just(
						new Producto("Tv",654.23),
						new Producto("Radio",654.23),
						new Producto("Cerro",987.23),
						new Producto("Lavadora",211.23),
						new Producto("Refri",985.23),
						new Producto("Cocina",156.23),
						new Producto("Mesa",367.23),
						new Producto("Silla",126.23),
						new Producto("Florero",963.23),
						new Producto("Espejo",269.23),
						new Producto("Cama",963.23)
				).flatMap(producto -> {
					producto.setCreateAt(new Date());
					return productoRepository.save(producto);
				})
				.subscribe(producto -> log.info("Insert: " + producto.getId() + " " + producto.getNombre()));
	}

}
