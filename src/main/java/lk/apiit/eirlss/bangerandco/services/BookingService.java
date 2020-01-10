package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.exceptions.CustomException;
import lk.apiit.eirlss.bangerandco.models.*;
import lk.apiit.eirlss.bangerandco.repositories.BookingRepository;
import lk.apiit.eirlss.bangerandco.repositories.BookingUtilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class BookingService {
    private BookingRepository bookingRepository;
    private UtilityService utilityService;
    private BookingUtilityRepository bookingUtilityRepository;
    private BookingValidationService validationService;

    @Autowired
    public BookingService(
            BookingRepository bookingRepository,
            UtilityService utilityService,
            BookingUtilityRepository bookingUtilityRepository,
            BookingValidationService validationService
    ) {
        this.bookingRepository = bookingRepository;
        this.utilityService = utilityService;
        this.bookingUtilityRepository = bookingUtilityRepository;
        this.validationService = validationService;
    }

    public Booking createBooking(Booking booking, Vehicle vehicle, List<String> utilities, User user) {
        validationService.validateBookingAge(user, vehicle);
        Date startDate = booking.getStartDate();
        Date endDate = booking.getEndDate();
        validationService.validateBookingPeriod(startDate, endDate);
        validationService.checkVehicleAvailability(vehicle, endDate, startDate);
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
        validationService.validateBookingAge(booking.getUser(), vehicle);
        List<Booking> bookings = bookingRepository.findByVehicleAndStartDateLessThanEqualAndEndDateGreaterThanEqual(vehicle, booking.getEndDate(), booking.getStartDate());
        for (Booking persistedBooking : bookings) {
            if (!persistedBooking.getId().equals(booking.getId()))
                throw new CustomException("Vehicle is not available for the selected date range.", HttpStatus.BAD_REQUEST);
        }
        validationService.validateBookingPeriod(booking.getStartDate(), booking.getEndDate());
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

    private void addUtilitiesToBooking(Booking booking, List<String> utilities) {
        if (booking.getId() != null) {
            removeOldUtilities(booking);
        }
        for (String utilityType : utilities) {
            Utility utility = utilityService.getUtilityByType(utilityType);
            validationService.checkUtilityAvailability(utility, booking.getEndDate(), booking.getStartDate());
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
        long days = validationService.getDuration(booking.getStartDate(), booking.getEndDate(), ChronoUnit.DAYS) + 1;
        double totalPrice = days * unitPrice;
        booking.setPrice(totalPrice);
    }
}