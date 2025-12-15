package ua.kpi.banking.dto.customer;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
}
