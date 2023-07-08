package br.com.alexmdo.finantialcontrol.domain.category;

import br.com.alexmdo.finantialcontrol.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categories")
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(unique = true, nullable = false)
    private String name;

    private String color;

    private String icon;

    @NotNull(message = "Type is required")
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public enum Type {
        INCOME,
        EXPENSE
    }

}