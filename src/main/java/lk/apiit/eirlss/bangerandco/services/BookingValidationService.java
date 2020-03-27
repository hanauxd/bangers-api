package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.exceptions.CustomException;
import lk.apiit.eirlss.bangerandco.models.*;
import lk.apiit.eirlss.bangerandco.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class BookingValidationService {
    private BookingRepository bookingRepository;

    @Autowired
    public BookingValidationService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public void checkVehicleAvailability(Vehicle vehicle, Date endDate, Date startDate) {
        List<Booking> bookings = bookingRepository.findByVehicleAndStartDateLessThanEqualAndEndDateGreaterThanEqual(vehicle, endDate, startDate);
        if (bookings.size() > 0) throw new CustomException("Vehicle is not available for the selected date range.", HttpStatus.BAD_REQUEST);
    }

    public void checkVehicleAvailabilityForNextDay(String bookingId, Date returnDate, Vehicle vehicle) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(returnDate);
        calendar.add(Calendar.DATE, 1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.set(year, month, day, 0, 0, 0);
        Date newStartDate = calendar.getTime();

        calendar.set(year, month, day, 23, 59, 59);
        Date newEndDate = calendar.getTime();

        List<Booking> bookings = bookingRepository.findByVehicleAndStartDateLessThanEqualAndEndDateGreaterThanEqual(vehicle, newEndDate, newStartDate);

        for (Booking persistedBooking : bookings) {
            if (!persistedBooking.getId().equals(bookingId))
                throw new CustomException("Vehicle is not available for the selected date range.", HttpStatus.BAD_REQUEST);
        }
    }

    public void checkUtilityAvailability(Utility utility, Date endDate, Date startDate) {
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

    public void validateBookingPeriod(Date startDate, Date endDate) {
        long hours = getDuration(startDate, endDate, ChronoUnit.HOURS);
        if (hours < 5) throw new CustomException("Minimum booking period is 5 hours.", HttpStatus.BAD_REQUEST);

        long days = getDuration(startDate, endDate, ChronoUnit.DAYS) + 1;
        if (days > 14) throw new CustomException("Maximum booking period is 14 days.", HttpStatus.BAD_REQUEST);
    }

    public void validateBookingAge(User user, Vehicle vehicle) {
        boolean isAge = user.getAge() < 25;
        boolean isTownCar = vehicle.getCategory().toLowerCase().equals("town car");
        boolean isSmall = vehicle.getSize().toLowerCase().equals("small");
        if (isAge && !isTownCar && !isSmall) {
            throw new CustomException("Customers under 25 years are allowed to book only Small Town Cars.", HttpStatus.BAD_REQUEST);
        }
    }

    public long getDuration(Date startDate, Date endDate, ChronoUnit timeUnit) {
        return timeUnit.between(startDate.toInstant(), endDate.toInstant());
    }
}
