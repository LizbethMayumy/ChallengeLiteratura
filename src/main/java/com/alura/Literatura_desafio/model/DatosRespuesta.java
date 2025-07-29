package com.alura.Literatura_desafio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatosRespuesta {

    private List<DatosLibro> results;

}
