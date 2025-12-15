package ua.kpi.banking.exception;

public class BadRequestException extends RuntimeException {
   public BadRequestException(String message) {
      super(message);
   }
}
