package com.purrComplexity.TrabajoYa;

import com.purrComplexity.TrabajoYa.exception.EmpleadorNotFound;
import com.purrComplexity.TrabajoYa.exception.EmpleadorWithTheSameCorreo;
import com.purrComplexity.TrabajoYa.exception.EmpleadorWithTheSameRUC;
import com.purrComplexity.TrabajoYa.exception.TrabajadorNotFound;
import com.purrComplexity.TrabajoYa.exception.*;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmpleadorNotFound.class)
    public ResponseEntity<ErrorMessage> handleEmpleadorNotFoundException(EmpleadorNotFound ex){
        ErrorMessage error=new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(EmpleadorWithTheSameRUC.class)
    public ResponseEntity<ErrorMessage> handleEmpleadorWithTheSameRucException(EmpleadorWithTheSameRUC ex){
        ErrorMessage error=new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(TrabajadorWithSameCorreo.class)
    public ResponseEntity<ErrorMessage> handelPersonaWithSameCorreo(TrabajadorWithSameCorreo ex){
        ErrorMessage errorMessage=new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(TrabajadorNotFound.class)
    public ResponseEntity<ErrorMessage> handlePersonaNotFound(TrabajadorNotFound ex){
        ErrorMessage errorMessage=new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }
    @ExceptionHandler(EmpleadorWithTheSameCorreo.class)
    public ResponseEntity<ErrorMessage>handelEmpleadorWithTheSameCorreo(EmpleadorWithTheSameCorreo ex){
        ErrorMessage errorMessage=new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(EmpleoNotFound.class)
    public ResponseEntity<ErrorMessage>handleEmpleoNotFound(EmpleoNotFound ex){
        ErrorMessage errorMessage=new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(NoEmpleadorException.class)
    public ResponseEntity<?> handleNoEmpleador(NoEmpleadorException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", ex.getMessage()
        ));
    }

    @ExceptionHandler(ContratoNotFound.class)
    public ResponseEntity<?> handleContratoNotFound(ContratoNotFound ex){
        ErrorMessage errorMessage=new ErrorMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(ContratoNotInEmpleador.class)
    public ResponseEntity<?> handleContratoNotInEmpleador(ContratoNotInEmpleador ex){
        ErrorMessage errorMessage=new ErrorMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(ContratoNotInTrabajador.class)
    public ResponseEntity<?> handleContratoNotInTrabajador(ContratoNotInTrabajador ex){
        ErrorMessage errorMessage=new ErrorMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(UsuarioYaEsEmpleadorException.class)
    public ResponseEntity<?> handleUsuarioYaEsEmpleadorException(UsuarioYaEsEmpleadorException ex){
        ErrorMessage errorMessage=new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(UsuarioNoEsEmpleadorException.class)
    public ResponseEntity<?> handleUsuarioNoEsEmpleadorException(UsuarioNoEsEmpleadorException ex){
        ErrorMessage errorMessage=new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(OfertaEmpleoNoPerteneceAlEmpleadorException.class)
    public ResponseEntity<?> handleOfertaEmpleoNoPerteneceAlEmpleadorException(OfertaEmpleoNoPerteneceAlEmpleadorException ex){
        ErrorMessage errorMessage= new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(PostulanteNoEncontradoEnOfertaException.class)
    public ResponseEntity<?> handlePostulanteNoEncontradoEnOfertaException(PostulanteNoEncontradoEnOfertaException ex){
        ErrorMessage errorMessage=new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(OfertaEmpleoNotFound.class)
    public ResponseEntity<?> handleOfertaEmpleoNotFound(OfertaEmpleoNotFound ex){
        ErrorMessage errorMessage=new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(PostulacionDuplicadaException.class)
    public ResponseEntity<?> handlePostulacionDuplicadaException(PostulacionDuplicadaException ex){
        ErrorMessage errorMessage=new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(UsuarioNoEsTrabajadorException.class)
    public ResponseEntity<?> handleUsuarioNoEsTrabajadorException(UsuarioNoEsTrabajadorException ex){
        ErrorMessage errorMessage=new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(UsuarioYaEsTrabajadorException.class)
    public ResponseEntity<?> handleUsuarioYaEsTrabajadorException(UsuarioYaEsTrabajadorException ex){
        ErrorMessage errorMessage=new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(EmpleadorNoPerteneceAlUsuarioException.class)
    public ResponseEntity<?> handleEmpleadorNoPerteneceAlUsuarioException(EmpleadorNoPerteneceAlUsuarioException ex){
        ErrorMessage errorMessage=new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(TrabajadorNoPerteneceAlUsuarioException.class)
    public ResponseEntity<?> handleTrabajadorNoPerteneceAlUsuarioException(TrabajadorNoPerteneceAlUsuarioException ex){
        ErrorMessage errorMessage=new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(TrabajadorNoContratadoException.class)
    public ResponseEntity<?> hanldeTrabajadorNoContratadoException(TrabajadorNoContratadoException ex){
        ErrorMessage errorMessage=new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(NumeroPostulacionesMaximaAlcanzado.class)
    public ResponseEntity<?> handleNumeroPostulacionesMaximaAlcanzado(NumeroPostulacionesMaximaAlcanzado ex){
        ErrorMessage errorMessage=new ErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
