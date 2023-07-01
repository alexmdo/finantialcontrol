package br.com.alexmdo.finantialcontrol.domain.creditcard;

import br.com.alexmdo.finantialcontrol.domain.account.Account;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "credit_cards")
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Limit must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Limit must be greater than zero")
    private BigDecimal creditCardLimit;

    @NotBlank(message = "Description must not be blank")
    private String description;

    @NotNull(message = "Brand must not be null")
    @Enumerated(EnumType.STRING)
    private CreditCardBrand brand;

    @NotNull(message = "Closing day must not be null")
    @Min(value = 1, message = "Closing day must be greater than or equal to 1")
    @Max(value = 30, message = "Closing day must be less than or equal to 31")
    private Integer closingDay;

    @NotNull(message = "Due date must not be null")
    @Min(value = 1, message = "Due date must be greater than or equal to 1")
    @Max(value = 30, message = "Due date must be less than or equal to 31")
    private Integer dueDate;

    @NotNull(message = "Account must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

}
