package in.mjunth.resumebuilderjava.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidationException(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error->{
            String fieldName = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName,message);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("message","Validation Failed");
        response.put("error",errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }

    @ExceptionHandler(ResourceExistsException.class)
    public ResponseEntity<Map<String,Object>> handleResourceExistsException(ResourceExistsException ex){
        Map<String,Object> resposne = new HashMap<>();
        resposne.put("message","Validation Failed");
        resposne.put("error",ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(resposne);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleGenericException(Exception ex){
        Map<String,Object> resposne = new HashMap<>();
        resposne.put("message","Something went WRONG, contact System Administrator");
        resposne.put("error",ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(resposne);
    }
}
