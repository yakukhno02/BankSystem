package ua.kpi.banking.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateCustomerRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 32)
    private String name;

    @NotBlank(message = "Surname is required")
    @Size(min = 2, max = 32)
    private String surname;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 10, max = 20)
    private String phoneNumber;
}
