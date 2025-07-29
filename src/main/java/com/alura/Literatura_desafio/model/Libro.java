package com.alura.Literatura_desafio.model;

import jakarta.persistence.*;
import lombok.*;




import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
@Setter
@Getter
@Entity
@Table(name= "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String idioma;
    private int descargas;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "autor_id")
    private Autor autor;

    public Libro() {
    }

    public Libro(DatosLibro datosLibro){
        this.titulo = datosLibro.titulo();
        //this.idiomas = datosLibro.idiomas();
        this.idioma = Optional.ofNullable(datosLibro.idioma())
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .orElse("Desconocido");

        this.descargas =datosLibro.numeroDescargas();
        if (!datosLibro.autores().isEmpty()) {
            this.autor = new Autor(datosLibro.autores().get(0));
        }
        //this.autores = datosLibro.autores();
        //this.idioma = String.join(", ", datosLibro.idioma());
        //this.titulo = (datosLibro.titulo() != null && !datosLibro.titulo().isEmpty())
               // ? datosLibro.titulo()
                //: "Título no disponible";

    }
    @Override
    public String toString() {
        return
                "TITULO='" + titulo + '\'' +
                ", IDIOMA='" + idioma + '\'' +
                ", AUTOR=" + autor + '\'' +
                ", DESCARGAS=" + descargas;

    }

}
