package lk.apiit.eirlss.bangerandco.controllers;

import lk.apiit.eirlss.bangerandco.dto.requests.BookingRequest;
import lk.apiit.eirlss.bangerandco.models.Booking;
import lk.apiit.eirlss.bangerandco.models.User;
import lk.apiit.eirlss.bangerandco.models.Vehicle;
import lk.apiit.eirlss.bangerandco.services.BookingService;
import lk.apiit.eirlss.bangerandco.services.MapValidationErrorService;
import lk.apiit.eirlss.bangerandco.services.UserService;
import lk.apiit.eirlss.bangerandco.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/bookings")
public class BookingController {
    private BookingService bookingService;
    private UserService userService;
    private VehicleService vehicleService;
    private MapValidationErrorService mapValidationErrorService;

    @Autowired
    public BookingController(
            BookingService bookingService,
            MapValidationErrorService mapValidationErrorService,
            UserService userService,
            VehicleService vehicleService
    ) {
        this.bookingService = bookingService;
        this.mapValidationErrorService = mapValidationErrorService;
        this.userService = userService;
        this.vehicleService = vehicleService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'STAFF')")
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest dto, Authentication auth, BindingResult result) {
        if (result.hasErrors()) return mapValidationErrorService.mapValidationErrorService(result);
        User user = userService.getUserByEmail(auth.getName());
        Vehicle vehicle = vehicleService.getVehicleById(dto.getVehicleId());
        Booking booking = bookingService.createBooking(dto.transformToEntity(), vehicle, dto.getUtilities(), user);
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'STAFF')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable String id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'STAFF')")
    @GetMapping("/user")
    public ResponseEntity<?> getBookingsByUser(Authentication auth) {
        User loggedInUser = userService.getUserByEmail(auth.getName());
        List<Booking> bookings = bookingService.getBookingsByUser(loggedInUser);
        return ResponseEntity.ok(bookings);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'STAFF')")
    @PutMapping("/{id}")
    public ResponseEntity<?> extendBooking(@PathVariable String id, @RequestBody BookingRequest dto, BindingResult result) {
        if (result.hasErrors()) return mapValidationErrorService.mapValidationErrorService(result);
        Vehicle vehicle = vehicleService.getVehicleById(dto.getVehicleId());
        Booking booking = bookingService.getBookingById(id);
        dto.updateBooking(booking);
        List<String> utilities = dto.getUtilities();

        Booking updatedBooking = bookingService.updateBooking(booking, vehicle, utilities);
        return ResponseEntity.ok(updatedBooking);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping
    public ResponseEntity<?> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    @PostMapping("/update")
    public ResponseEntity<?> updateBooking(@RequestParam String id, @RequestParam String status) {
        Booking booking = bookingService.getBookingById(id);
        booking.setStatus(status);
        if ("Failed".equals(status)) {
            String userId = booking.getUser().getId();
            userService.blacklistUser(userId);
        }
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'STAFF')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable String id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok("Booking is deleted.");
    }
}
