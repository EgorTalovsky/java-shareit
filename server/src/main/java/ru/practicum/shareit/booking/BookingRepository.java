package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllBookingsByBookerId(long bookerId, Pageable page);

    List<Booking> findAllByItemOwnerId(long ownerId, Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.status = '0'")
    List<Booking> findAllByStatusWaiting(long bookerId, Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start > :now")
    List<Booking> findAllBookingsOnFuture(long bookerId, LocalDateTime now, Pageable page);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.item.id = i.id WHERE i.owner.id = :ownerId AND b.start > :now")
    List<Booking> findAllOwnerBookingsOnFuture(long ownerId, LocalDateTime now, Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start < :now AND b.end > :now")
    List<Booking> findAllCurrentBookings(long bookerId, LocalDateTime now, Pageable page);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.item.id = i.id WHERE i.owner.id = :ownerId AND b.end < :now")
    List<Booking> findAllOwnersBookingsOnPast(long ownerId, LocalDateTime now, Pageable page);

    List<Booking> findAllBookingsByItemId(long itemId, Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.status = '2'")
    List<Booking> findAllRejectedBookingsForBooker(long bookerId, Pageable page);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.item.id = i.id WHERE i.owner.id = :ownerId AND b.status = '2'")
    List<Booking> findAllOwnersRejectedBookings(long ownerId, Pageable page);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.item.id = i.id WHERE i.owner.id = :ownerId AND b.status = '0'")
    List<Booking> findAllWaitingBookingsForOwner(long ownerId, Pageable page);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.item.id = i.id WHERE i.owner.id = :ownerId AND b.end > :now AND b.start < :now")
    List<Booking> findAllCurrentBookingsForOwner(long ownerId, LocalDateTime now, Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.end < :now")
    List<Booking> findAllPastBookingsForBooker(long bookerId, LocalDateTime now, Pageable page);
}
