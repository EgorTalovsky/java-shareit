package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllBookingsByBookerId(long bookerId);

    List<Booking> findAllByItemOwnerId(long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.status = '0'")
    List<Booking> findAllByStatusWaiting(long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start > :now")
    List<Booking> findAllBookingsOnFuture(long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.item.id = i.id WHERE i.owner.id = :ownerId AND b.start > :now")
    List<Booking> findAllOwnerBookingsOnFuture(long ownerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start < :now AND b.end > :now")
    List<Booking> findAllCurrentBookings(long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.item.id = i.id WHERE i.owner.id = :ownerId AND b.end < :now")
    List<Booking> findAllOwnersBookingsOnPast(long ownerId, LocalDateTime now);
    //1 approved 2 rejected

    List<Booking> findAllBookingsByItemId(long itemId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.status = '2'")
    List<Booking> findAllRejectedBookingsForBooker(long bookerId);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.item.id = i.id WHERE i.owner.id = :ownerId AND b.status = '2'")
    List<Booking> findAllOwnersRejectedBookings(long ownerId);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.item.id = i.id WHERE i.owner.id = :ownerId AND b.status = '0'")
    List<Booking> findAllWaitingBookingsForOwner(long ownerId);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.item.id = i.id WHERE i.owner.id = :ownerId AND b.end > :now AND b.start < :now")
    List<Booking> findAllCurrentBookingsForOwner(long ownerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.end < :now")
    List<Booking> findAllPastBookingsForBooker(long bookerId, LocalDateTime now);
}
