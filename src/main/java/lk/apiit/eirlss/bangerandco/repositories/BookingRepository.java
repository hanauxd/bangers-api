package lk.apiit.eirlss.bangerandco.repositories;

import lk.apiit.eirlss.bangerandco.models.Booking;
import lk.apiit.eirlss.bangerandco.models.User;
import lk.apiit.eirlss.bangerandco.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    Booking findByVehicleAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Vehicle vehicle, Date endDate, Date startDate);
    List<Booking> findByUser(User user);
}
