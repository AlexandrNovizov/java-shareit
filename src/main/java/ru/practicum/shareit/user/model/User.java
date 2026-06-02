package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;
}
