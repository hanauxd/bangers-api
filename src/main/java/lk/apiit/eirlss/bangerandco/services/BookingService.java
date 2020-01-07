package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.exceptions.CustomException;
import lk.apiit.eirlss.bangerandco.models.Booking;
import lk.apiit.eirlss.bangerandco.models.User;
import lk.apiit.eirlss.bangerandco.models.Vehicle;
import lk.apiit.eirlss.bangerandco.repositories.BookingRepository;
import lk.apiit.eirlss.bangerandco.repositories.UserRepository;
import lk.apiit.eirlss.bangerandco.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private VehicleRepository vehicleRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, UserRepository userRepository, VehicleRepository vehicleRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public Booking createBooking(Booking booking, String vehicleId, String username) {

        User user = userRepository.findUserByEmail(username).orElse(null);
        if (user == null) throw new UsernameNotFoundException("Username '" + username + "' not found.");

        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);
        if (vehicle == null) throw new CustomException("Vehicle not found.", HttpStatus.NOT_FOUND);

        List<Booking> bookings = bookingRepository.findByVehicleAndStartDateLessThanEqualAndEndDateGreaterThanEqual(vehicle, booking.getEndDate(), booking.getStartDate());
        if (bookings.size() > 0) throw new CustomException("Vehicle is not available for the selected date range.", HttpStatus.BAD_REQUEST);

        booking.setVehicle(vehicle);
        booking.setUser(user);

        return bookingRepository.save(booking);
    }
}
