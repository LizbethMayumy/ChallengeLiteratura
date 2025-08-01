package com.alura.Literatura_desafio;

import com.alura.Literatura_desafio.principal.Principal;
import com.alura.Literatura_desafio.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraturaDesafioApplication implements CommandLineRunner{
	@Autowired
	//private LibroRepository repository;
	private Principal principal;

	public static void main(String[] args) {

		SpringApplication.run(LiteraturaDesafioApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//Principal principal = new Principal(repository,);
		principal.muestraElMenu();
	}

}
