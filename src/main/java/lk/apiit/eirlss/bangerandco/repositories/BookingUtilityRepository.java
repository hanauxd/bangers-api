package lk.apiit.eirlss.bangerandco.repositories;

import lk.apiit.eirlss.bangerandco.models.Booking;
import lk.apiit.eirlss.bangerandco.models.BookingUtility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingUtilityRepository extends JpaRepository<BookingUtility, String> {
    List<BookingUtility> findByBooking(Booking booking);
}
