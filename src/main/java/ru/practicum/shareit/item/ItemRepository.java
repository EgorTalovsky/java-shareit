package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemByOwnerId(long ownerId);

    @Query("SELECT i FROM Item i WHERE i.available IS true AND (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) OR " +
        "UPPER (i.description) LIKE UPPER(CONCAT('%', ?1, '%')))")
    List<Item> search(String text);

}
