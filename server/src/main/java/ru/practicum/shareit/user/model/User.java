package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.common.DisplayName;

@Data
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@DisplayName("Пользователь")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;
}
