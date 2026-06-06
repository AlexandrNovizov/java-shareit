package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings")
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_timestamp", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_timestamp", nullable = false)
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "booker_id", referencedColumnName = "id", nullable = false)
    private User booker;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
