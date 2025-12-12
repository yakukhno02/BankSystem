package ua.kpi.banking.dto.customer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateCustomerRequest {
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
}
