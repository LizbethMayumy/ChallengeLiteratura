package com.alura.Literatura_desafio.service;

public interface IConvierteDatos {
    <T> T obtenerDatos(String json, Class<T> clase);

}
