package br.com.alexmdo.finantialcontrol.domain.account;

import java.math.BigDecimal;

import br.com.alexmdo.finantialcontrol.domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal initialAmount;

    @NotBlank
    private String financialInstitution;

    @NotBlank
    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull
    private AccountType accountType;

    @NotBlank
    private String color;

    @NotBlank
    private String icon;

    private boolean archived = false;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    
}
