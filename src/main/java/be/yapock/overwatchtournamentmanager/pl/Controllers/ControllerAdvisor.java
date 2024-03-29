package be.yapock.overwatchtournamentmanager.pl.Controllers;

import be.yapock.overwatchtournamentmanager.pl.models.error.Error;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ControllerAdvisor {
    @ExceptionHandler({UsernameNotFoundException.class, EntityNotFoundException.class})
    public ResponseEntity<Error> handleUsernameNotFoundException(RuntimeException e, HttpServletRequest request){
        Error error = Error.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .requestMadeAt(LocalDateTime.now())
                .URI(request.getRequestURI())
                .build();
        return ResponseEntity.status(error.getStatus())
                .body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> handleArgumentException(RuntimeException e, HttpServletRequest request){
        Error error = Error.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage())
                .requestMadeAt(LocalDateTime.now())
                .URI(request.getRequestURI())
                .build();
        return ResponseEntity.status(error.getStatus())
                .body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Error> handleUnauthorizeException(RuntimeException e, HttpServletRequest request){
        Error error = Error.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(e.getMessage())
                .requestMadeAt(LocalDateTime.now())
                .URI(request.getRequestURI())
                .build();
        return ResponseEntity.status(error.getStatus())
                .body(error);
    }
}
