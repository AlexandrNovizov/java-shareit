package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.projection.LastAndNextBooking;

import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = ?1 " +
            "ORDER BY b.start ASC")
    List<Booking> getAllBookingsByBookerId(Long bookerId);

    @Query("SELECT COUNT(*) > 0 FROM Booking AS b " +
            "JOIN b.booker " +
            "WHERE b.item.id = ?1 " +
            "AND b.booker.id = ?2 " +
            "AND b.status = 'APPROVED' " +
            "AND b.`end` < CURRENT_TIMESTAMP")
    Boolean hasItemBookedByUser(Long itemId, Long userId);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = ?1 " +
            "AND b.status LIKE 'ACCEPTED'" +
            "AND b.start <= CURRENT_DATE " +
            "AND b.`end` >= CURRENT_DATE " +
            "ORDER BY b.start ASC")
    List<Booking> getCurrentBookingsByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = ?1 " +
            "AND b.status LIKE 'ACCEPTED'" +
            "AND b.`end` < CURRENT_DATE " +
            "ORDER BY b.start ASC")
    List<Booking> getPastBookingsByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = ?1 " +
            "AND b.status LIKE 'ACCEPTED'" +
            "AND b.start > CURRENT_DATE " +
            "ORDER BY b.start ASC")
    List<Booking> getFutureBookingsByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = ?1 " +
            "AND b.status LIKE 'WAITING'" +
            "ORDER BY b.start ASC")
    List<Booking> getWaitingBookingsByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = ?1 " +
            "AND b.status LIKE 'REJECTED'" +
            "ORDER BY b.start ASC")
    List<Booking> getRejectedBookingsByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.item.owner AS owner " +
            "WHERE owner.id = ?1 " +
            "ORDER BY b.start ASC")
    List<Booking> getAllBookingsByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.item.owner AS owner " +
            "WHERE owner.id = ?1 " +
            "AND b.status LIKE 'ACCEPTED'" +
            "AND b.start <= CURRENT_DATE " +
            "AND b.`end` >= CURRENT_DATE " +
            "ORDER BY b.start ASC")
    List<Booking> getCurrentBookingsByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.item.owner AS owner " +
            "WHERE owner.id = ?1 " +
            "AND b.status LIKE 'ACCEPTED'" +
            "AND b.`end` < CURRENT_DATE " +
            "ORDER BY b.start ASC")
    List<Booking> getPastBookingsByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.item.owner AS owner " +
            "WHERE owner.id = ?1 " +
            "AND b.status LIKE 'ACCEPTED'" +
            "AND b.start > CURRENT_DATE " +
            "ORDER BY b.start ASC")
    List<Booking> getFutureBookingsByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.item.owner AS owner " +
            "WHERE owner.id = ?1 " +
            "AND b.status LIKE 'WAITING'" +
            "ORDER BY b.start ASC")
    List<Booking> getWaitingBookingsByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.item.owner AS owner " +
            "WHERE owner.id = ?1 " +
            "AND b.status LIKE 'REJECTED'" +
            "ORDER BY b.start ASC")
    List<Booking> getRejectedBookingsByOwnerId(Long ownerId);

    @Query("SELECT NEW ru.practicum.shareit.booking.model.projection.LastAndNextBooking(" +
            "i.id, " +
            "MAX(CASE WHEN b.start <= CURRENT_TIMESTAMP THEN b.start ELSE NULL END), " +
            "MIN(CASE WHEN b.start > CURRENT_TIMESTAMP THEN b.start ELSE NULL END))" +
            "FROM Booking AS b " +
            "JOIN b.item AS i " +
            "WHERE b.status = 'ACCEPTED' AND " +
            "i.id IN :ids " +
            "GROUP BY i.id")
    List<LastAndNextBooking> getLastAndNextBookingForIds(
            @Param("ids") Collection<Long> ids
    );
}
