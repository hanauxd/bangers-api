package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.exceptions.CustomException;
import lk.apiit.eirlss.bangerandco.models.*;
import lk.apiit.eirlss.bangerandco.repositories.BookingRepository;
import lk.apiit.eirlss.bangerandco.repositories.BookingUtilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class BookingService {
    private BookingRepository bookingRepository;
    private UtilityService utilityService;
    private BookingUtilityRepository bookingUtilityRepository;

    @Autowired
    public BookingService(
            BookingRepository bookingRepository,
            UtilityService utilityService,
            BookingUtilityRepository bookingUtilityRepository) {
        this.bookingRepository = bookingRepository;
        this.utilityService = utilityService;
        this.bookingUtilityRepository = bookingUtilityRepository;
    }

    public Booking createBooking(Booking booking, Vehicle vehicle, List<String> utilities, User user) {
        checkVehicleAvailability(vehicle, booking.getEndDate(), booking.getStartDate());
        booking.setVehicle(vehicle);
        booking.setUser(user);
        addUtilitiesToBooking(booking, utilities);
        calculateBookingPrice(booking);
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByUser(User user) {
        List<Booking> bookings = bookingRepository.findByUser(user);
        if (bookings.isEmpty()) throw new CustomException("User does not have booking.", HttpStatus.NOT_FOUND);
        return bookings;
    }

    public Booking getBookingById(String id) {
        return bookingRepository.findById(id).orElseThrow(() -> new CustomException("Booking not found.", HttpStatus.NOT_FOUND));
    }

    public Booking updateBooking(Booking booking, Vehicle vehicle, List<String> utilities) {
        List<Booking> bookings = bookingRepository.findByVehicleAndStartDateLessThanEqualAndEndDateGreaterThanEqual(vehicle, booking.getEndDate(), booking.getStartDate());
        for (Booking persistedBooking : bookings) {
            if (!persistedBooking.getId().equals(booking.getId()))
                throw new CustomException("Vehicle is not available for the selected date range.", HttpStatus.BAD_REQUEST);
        }
        booking.setVehicle(vehicle);
        addUtilitiesToBooking(booking, utilities);
        calculateBookingPrice(booking);
        return bookingRepository.save(booking);
    }

    public Booking confirmBooking(String id, String status, boolean isLateReturn, List<String> utilities) {
        Booking booking = getBookingById(id);
        if (isLateReturn) {
            setLateReturn(booking);
        }
        booking.setStatus(status);
        addUtilitiesToBooking(booking, utilities);
        return bookingRepository.save(booking);
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

    private void setLateReturn(Booking booking) {
        if (!isNewUser(booking.getUser())) booking.setLateReturn(true);
    }

    private boolean isNewUser(User user) {
        List<Booking> bookings = bookingRepository.findByUserAndStatus(user, "CONFIRMED");
        return bookings.size() > 0;
    }

    private void checkVehicleAvailability(Vehicle vehicle, Date endDate, Date startDate) {
        List<Booking> bookings = bookingRepository.findByVehicleAndStartDateLessThanEqualAndEndDateGreaterThanEqual(vehicle, endDate, startDate);
        if (bookings.size() > 0) throw new CustomException("Vehicle is not available for the selected date range.", HttpStatus.BAD_REQUEST);
    }

    private void checkUtilityAvailability(Utility utility, Date endDate, Date startDate) {
        int count = 0;
        List<Booking> bookings = bookingRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(endDate, startDate);

        for (Booking book : bookings) {
            List<BookingUtility> bookingUtilities = book.getBookingUtilities();
            for (BookingUtility bookingUtility : bookingUtilities) {
                String type = bookingUtility.getUtility().getUtilityType();
                if (utility.getUtilityType().equals(type)) {
                    count++;
                }
            }
        }

        if (count >= utility.getQuantity())
            throw new CustomException("Utility '" + utility.getUtilityType() + "' is not available for the selected date range.", HttpStatus.BAD_REQUEST);
    }

    private void addUtilitiesToBooking(Booking booking, List<String> utilities) {
        if (booking.getId() != null) {
            removeOldUtilities(booking);
        }
        for (String utilityType : utilities) {
            Utility utility = utilityService.getUtilityByType(utilityType);
            checkUtilityAvailability(utility, booking.getEndDate(), booking.getStartDate());
            BookingUtility bookingUtility = new BookingUtility(booking, utility);
            booking.getBookingUtilities().add(bookingUtility);
            utility.getBookingUtilities().add(bookingUtility);
        }
    }

    private void removeOldUtilities(Booking booking) {
        List<BookingUtility> byBooking = bookingUtilityRepository.findByBooking(booking);
        for (BookingUtility bookingUtility : byBooking) {
            bookingUtility.getUtility().removeBookingUtility(bookingUtility);
            bookingUtility.getBooking().removeBookingUtility(bookingUtility);
            bookingUtility.setUtility(null);
            bookingUtility.setBooking(null);
            bookingUtilityRepository.delete(bookingUtility);
        }
    }

    private void calculateBookingPrice(Booking booking) {
        double unitPrice = booking.getVehicle().getPrice();
        long days = ChronoUnit.DAYS.between(booking.getStartDate().toInstant(), booking.getEndDate().toInstant());

        days++;
        double totalPrice = days * unitPrice;
        booking.setPrice(totalPrice);

        System.out.println("Unit Price: " + unitPrice);
        System.out.println("No of days: " + days);
        System.out.println("TOTAL PRICE: " + totalPrice);
    }
}
