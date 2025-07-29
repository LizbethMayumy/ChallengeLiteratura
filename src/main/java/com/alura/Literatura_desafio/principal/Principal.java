package com.alura.Literatura_desafio.principal;

import com.alura.Literatura_desafio.model.Autor;
import com.alura.Literatura_desafio.model.DatosLibro;
import com.alura.Literatura_desafio.model.DatosRespuesta;
import com.alura.Literatura_desafio.model.Libro;
import com.alura.Literatura_desafio.repository.AutorRepository;
import com.alura.Literatura_desafio.repository.LibroRepository;
import com.alura.Literatura_desafio.service.ConsumoApi;
import com.alura.Literatura_desafio.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
@Component
public class Principal {
        private Scanner teclado = new Scanner(System.in);
        private ConsumoApi consumoApi = new ConsumoApi();
        private final String URL_BASE = "https://gutendex.com/books/";
        private ConvierteDatos conversor = new ConvierteDatos();



        private List<DatosLibro> datosLibros = new ArrayList<>();

        private LibroRepository repositorio;//para guardar en la BD

        private AutorRepository autorRepositorio;

        private List <Libro> series;

        private Optional<Libro> serieBuscada;


        @Autowired
        public Principal(LibroRepository repository, AutorRepository autorRepositorio) {
            this.repositorio = repository;
            this.autorRepositorio = autorRepositorio;
        }

        public void muestraElMenu() {
            var opcion = -1;
            while (opcion != 0) {
                var menu = """
                    1 - Buscar libro por título
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma

                    0 - Salir
                    """;
                System.out.println(menu);
                opcion = teclado.nextInt();
                teclado.nextLine();

                switch (opcion) {
                    case 1:
                        buscarLibro();
                        break;
                    case 2:
                        ListarLibros();
                        break;
                    case 3:
                        ListarAutores();
                        break;
                    case 4:
                        ListarAutoresVivos();
                        break;
                    case 5:
                        ListarLibrosIdioma();
                        break;

                    case 0:
                        System.out.println("Cerrando la aplicación...");
                        break;
                    default:
                        System.out.println("Opción inválida, ingresa otra opcion entre el 1 al 5.");
                }
            }

        }



        private List<DatosLibro> getDatosLibro() {
            System.out.println("Escribe el nombre del libro que deseas buscar: ");
            var nombreLibro = teclado.nextLine();

            var json = consumoApi.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ", "+"));

            var respuesta = conversor.obtenerDatos(json, DatosRespuesta.class);
            return respuesta.getResults();
        }


    private void buscarLibro() {
        List<DatosLibro> resultados = getDatosLibro();

        if (resultados == null || resultados.isEmpty()) {
            System.out.println("No se encontraron libros para esa búsqueda.");
            return;
        }

        for (int i = 0; i <Math.min(resultados.size(), 10); i++) {
            DatosLibro libro = resultados.get(i);
            System.out.println("\n[" + (i + 1) + "] Título: " + libro.titulo());
            System.out.println("Autor(a): " + libro.autores().stream()
                    .map(a -> a.nombre())
                    .collect(Collectors.joining(", ")));
            System.out.println(" Idioma: " + String.join(", ", libro.idioma()));
            System.out.println(" Descargas: " + libro.numeroDescargas());
        }

        System.out.println("\nSelecciona el número del libro que deseas guardar (0 para cancelar): ");
        int opcion = teclado.nextInt();
        teclado.nextLine();

        if (opcion > 0 && opcion <= resultados.size()) {
            DatosLibro seleccionado = resultados.get(opcion - 1);
            if (repositorio.existsByTituloIgnoreCase(seleccionado.titulo())) {
                System.out.println(" El libro '" + seleccionado.titulo() + "' ya está registrado en la base de datos.");
                return;
            }
            Libro libro = new Libro(seleccionado);
            repositorio.save(libro);
            System.out.println(" Libro guardado exitosamente: " + libro.getTitulo());
        } else if (opcion == 0) {
            System.out.println(" Operación cancelada.");
        } else {
            System.out.println(" Opción inválida.");
        }
    }

    private void ListarLibros() {

        List<Libro> libros = repositorio.findAll();
        System.out.println("\n LISTA DE LIBROS REGISTRADOS:\n");
        libros.stream()

                .sorted(Comparator.comparing(Libro::getTitulo))
                .forEach(libro -> {
                    System.out.println(" TITULO: " + libro.getTitulo());
                    System.out.println(" AUTOR(a): " + libro.getAutor().getNombre());
                    System.out.println(" IDIOMA: " + libro.getIdioma());
                    System.out.println(" DESCARGAS: " + libro.getDescargas());
                    System.out.println("---------------------------------");
                });

    }

    private void ListarAutores() {
        //List<Autor> autores = autorRepositorio.findAll();
        List<Autor> autores = autorRepositorio.findAllAutoresWithLibros();

        if (autores.isEmpty()) {
            System.out.println(" No hay autores guardados en la base de datos.");
        } else {
            System.out.println("\n LISTA DE AUTORES REGISTRADOS:\n");
            autores.stream()
                    .sorted(Comparator.comparing(Autor::getNombre))
                    .forEach(autor -> {
                        System.out.println(" AUTOR: " + autor.getNombre());
                        System.out.println(" FECHA DE NACIMIENTO: " + autor.getNacimiento());
                        System.out.println(" FECHA DE FALLECIMIENTO: " + autor.getFallecimiento());
                        //System.out.println(" LIBROS: " + autor.getLibros());
                        List<Libro> librosDelAutor = autor.getLibros();
                        if (librosDelAutor != null && !librosDelAutor.isEmpty()) {
                            System.out.println(" LIBROS:");
                            librosDelAutor.forEach(libro ->
                                    System.out.println(" - " + libro.getTitulo()));
                        } else {
                            System.out.println(" LIBROS: No tiene libros registrados.");
                        }
                        System.out.println("---------------------------------");
                    });
        }
    }

    private void ListarAutoresVivos() {
        System.out.print("Ingresa el año para verificar autores vivos: ");
        int anio = teclado.nextInt();
        teclado.nextLine(); //

        List<Autor> autores = autorRepositorio.findAutoresVivosEnAnio(anio);

        if (autores.isEmpty()) {
            System.out.println("No se encontraron autores vivos en el año " + anio);
        } else {
            System.out.println("\n AUTORES VIVOS EN EL AÑO:\n");

            autores.stream()
                    .sorted(Comparator.comparing(Autor::getNombre))
                    .forEach(autor -> {
                        System.out.println(" AUTOR: " + autor.getNombre());
                        System.out.println(" FECHA DE NACIMIENTO: " + autor.getNacimiento());
                        System.out.println(" FECHA DE FALLECIMIENTO: " + autor.getFallecimiento());
                        System.out.println("---------------------------------");
                    });
        }
    }

    private void ListarLibrosIdioma() {
        List<Libro> libros = repositorio.findAll();
        System.out.print("Ingrese el idioma por ejemplo:\n 'en: Ingles' \n 'es: Español'\n 'fr: Frances' \n 'pt: Portugues' ");
        String idioma = teclado.nextLine().trim();

        long cantidad = repositorio.countByIdiomaIgnoreCase(idioma);

        if (cantidad > 0) {
            System.out.println("Cantidad de libros en el idiomaelegido '" + idioma + "': " + cantidad);
            System.out.println("\n Cantidad de libros en el idioma elegido '" + idioma + "': " + cantidad+ "\n");

            libros.stream()
                    .sorted(Comparator.comparing(Libro::getTitulo))
                    .forEach(libro -> {
                        System.out.println(" TITULO: " + libro.getTitulo());
                        System.out.println(" AUTOR(a): " + libro.getAutor().getNombre());
                        System.out.println(" IDIOMA: " + libro.getIdioma());
                        System.out.println(" DESCARGAS: " + libro.getDescargas());
                        System.out.println("---------------------------------");
                    });

        } else {
            System.out.println("No se encontraron libros en ese idioma.");
        }
    }











}


