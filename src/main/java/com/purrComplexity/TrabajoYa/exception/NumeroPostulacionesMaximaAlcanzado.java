package com.purrComplexity.TrabajoYa.exception;

public class NumeroPostulacionesMaximaAlcanzado extends RuntimeException {
    public NumeroPostulacionesMaximaAlcanzado(String message) {
        super(message);
    }
    public NumeroPostulacionesMaximaAlcanzado(){
        super("Ya no se permiten más postulaciones para esta oferta.");
    }
}
