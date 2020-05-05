package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.exceptions.CustomException;
import lk.apiit.eirlss.bangerandco.insurance.models.Fraud;
import lk.apiit.eirlss.bangerandco.models.*;
import lk.apiit.eirlss.bangerandco.repositories.BookingRepository;
import lk.apiit.eirlss.bangerandco.repositories.BookingUtilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UtilityService utilityService;
    private final BookingUtilityRepository bookingUtilityRepository;
    private final BookingValidationService validationService;
    private final UserService userService;
    private final FraudService fraudService;

    @Autowired
    public BookingService(
            BookingRepository bookingRepository,
            UtilityService utilityService,
            BookingUtilityRepository bookingUtilityRepository,
            BookingValidationService validationService,
            UserService userService,
            FraudService fraudService
    ) {
        this.bookingRepository = bookingRepository;
        this.utilityService = utilityService;
        this.bookingUtilityRepository = bookingUtilityRepository;
        this.validationService = validationService;
        this.userService = userService;
        this.fraudService = fraudService;
    }

    public Booking createBooking(Booking booking, Vehicle vehicle, List<String> utilities, User user) {
        validateBooking(user, booking, vehicle);
        booking.setVehicle(vehicle);
        booking.setUser(user);
        addUtilitiesToBooking(booking, utilities);
        calculateBookingPrice(booking);
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByUser(User user, Pageable pageable) {
        List<Booking> bookings = bookingRepository.findByUser(user, pageable);
        if (bookings.isEmpty()) throw new CustomException("User does not have booking.", HttpStatus.NOT_FOUND);
        return bookings;
    }

    public Booking getBookingById(String id) {
        return bookingRepository.findById(id).orElseThrow(() -> new CustomException("Booking not found.", HttpStatus.NOT_FOUND));
    }

    public List<Booking> getAllBookings(Pageable pageable) {
        Page<Booking> bookings = bookingRepository.findAll(pageable);
        return bookings.getContent();
    }

    public Booking updateUtils(Booking booking, List<String> utilities) {
        addUtilitiesToBooking(booking, utilities);
        return getBookingById(booking.getId());
    }

    public Booking extendBooking(Booking booking, Vehicle vehicle) {
        validationService.checkVehicleAvailabilityForNextDay(booking.getId(), booking.getEndDate(), vehicle);
        validationService.validateBookingPeriod(booking.getStartDate(), booking.getEndDate());
        booking.setVehicle(vehicle);
        calculateBookingPrice(booking);
        return bookingRepository.save(booking);
    }

    public Booking updateBookingStatus(String id, String status) {
        Booking booking = getBookingById(id);
        User user = booking.getUser();
        switch (status) {
            case "Failed": {
                userService.blacklistUser(user.getId(), true);
                break;
            }
            case "Collected": {
                Fraud fraud = fraudService.getFraudByNic(user.getNic());
                if (fraud != null) {
                    booking.setStatus("Refused");
                    bookingRepository.save(booking);
                    throw new CustomException(
                            "Vehicle hire is refused due to a fraudulent claim against the customer.",
                            HttpStatus.BAD_REQUEST
                    );
                }
                break;
            }
        }
        booking.setStatus(status);
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

    private boolean isNewUser(User user) {
        List<Booking> bookings = bookingRepository.findByUserAndStatus(user, "Returned");
        return bookings.size() < 1;
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
        long hours = validationService.getDuration(booking.getStartDate(), booking.getEndDate(), ChronoUnit.HOURS);
        long noOfDays = hours/24;
        long remainder = hours%24;
        double remainderPrice = remainder > 5 ? unitPrice : unitPrice / 2;
        double bookingPrice = noOfDays * unitPrice + remainderPrice;
        booking.setPrice(bookingPrice);
    }

    private void validateBooking(User user, Booking booking, Vehicle vehicle) {
        validationService.checkIfLicenseIsReported(user);
        validationService.validateDocuments(user);
        validationService.validateBookingAge(user, vehicle);
        Date startDate = booking.getStartDate();
        Date endDate = booking.getEndDate();
        validationService.validateBookingPeriod(startDate, endDate);
        validationService.checkVehicleAvailability(vehicle, endDate, startDate);
        if (booking.isLateReturn() && isNewUser(user)) {
            throw new CustomException("Late returns are not available for new users.", HttpStatus.BAD_REQUEST);
        }
    }
}