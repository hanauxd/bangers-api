package lk.apiit.eirlss.bangerandco.controllers;

import lk.apiit.eirlss.bangerandco.dto.requests.BookingDTO;
import lk.apiit.eirlss.bangerandco.models.Booking;
import lk.apiit.eirlss.bangerandco.models.User;
import lk.apiit.eirlss.bangerandco.services.BookingService;
import lk.apiit.eirlss.bangerandco.services.MapValidationErrorService;
import lk.apiit.eirlss.bangerandco.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private BookingService bookingService;
    private MapValidationErrorService mapValidationErrorService;
    private UserService userService;

    @Autowired
    public BookingController(
            BookingService bookingService,
            MapValidationErrorService mapValidationErrorService,
            UserService userService
    ) {
        this.bookingService = bookingService;
        this.mapValidationErrorService = mapValidationErrorService;
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'STAFF')")
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingDTO dto, Authentication auth, BindingResult result) {

        if (result.hasErrors()) return mapValidationErrorService.mapValidationErrorService(result);

        Booking booking = bookingService.createBooking(dto.transformToEntity(), dto.getVehicleId(), dto.getUtilities(), auth.getName());
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
    public ResponseEntity<?> updateBooking(@PathVariable String id, @RequestBody BookingDTO dto, Authentication auth, BindingResult result) {
        deleteBooking(id);
        return createBooking(dto, auth, result);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'STAFF')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable String id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok("Booking is deleted.");
    }
}
