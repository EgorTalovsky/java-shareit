package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllRequestsByRequestorId(long requestorId);

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requestor.id != :userId")
    List<ItemRequest> findAllRequestsByOtherUsers(long userId, Pageable pageable);
}
