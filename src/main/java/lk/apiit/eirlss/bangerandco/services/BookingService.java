package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.exceptions.CustomException;
import lk.apiit.eirlss.bangerandco.models.*;
import lk.apiit.eirlss.bangerandco.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BookingService {
    private BookingRepository bookingRepository;
    private UserService userService;
    private VehicleService vehicleService;
    private UtilityService utilityService;

    @Autowired
    public BookingService(
            BookingRepository bookingRepository,
            UserService userService,
            VehicleService vehicleService,
            UtilityService utilityService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.vehicleService = vehicleService;
        this.utilityService = utilityService;
    }

    public Booking createBooking(Booking booking, String vehicleId, List<String> utilities, String username) {

        User user = userService.getUserByEmail(username);
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);

        checkVehicleAvailability(vehicle, booking.getEndDate(), booking.getStartDate());

        for (String utilityType : utilities) {
            Utility utility = utilityService.getUtilityByType(utilityType);
            BookingUtility bookingUtility = new BookingUtility(booking, utility);
            booking.getBookingUtilities().add(bookingUtility);
            utility.getBookingUtilities().add(bookingUtility);
        }

        booking.setVehicle(vehicle);
        booking.setUser(user);

        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Booking booking, String vehicleId) {
        return null;
    }

    public List<Booking> getBookingsByUser(User user) {
        List<Booking> bookings = bookingRepository.findByUser(user);
        if (bookings.isEmpty()) throw new CustomException("User does not have booking.", HttpStatus.NOT_FOUND);
        return bookings;
    }

    public Booking getBookingById(String id) {
        return bookingRepository.findById(id).orElseThrow(() -> new CustomException("Booking not found.", HttpStatus.NOT_FOUND));
    }

    public void deleteBooking(String id) {
        Booking booking = bookingRepository.findById(id).orElse(null);

        if (booking == null) throw new CustomException("Booking not found.", HttpStatus.NOT_FOUND);

        booking.getVehicle().removeBooking(booking);
        booking.getUser().removeBooking(booking);
        List<BookingUtility> bookingUtilities = booking.getBookingUtilities();
        for (BookingUtility bookingUtility : bookingUtilities) {
            bookingUtility.getUtility().removeBookingUtility(bookingUtility);
        }

        bookingRepository.delete(booking);
    }

    private void checkVehicleAvailability(Vehicle vehicle, Date endDate, Date startDate) {
        Booking booking = bookingRepository.findByVehicleAndStartDateLessThanEqualAndEndDateGreaterThanEqual(vehicle, endDate, startDate);
        if (booking != null) throw new CustomException("Vehicle is not available for the selected date range.", HttpStatus.BAD_REQUEST);
    }
}
