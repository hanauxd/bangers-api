package lk.apiit.eirlss.bangerandco.dto.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import lk.apiit.eirlss.bangerandco.models.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
    private String vehicleId;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date startDate;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date endDate;
    private String status;
    private boolean lateReturn;
    private List<String> utilities = new ArrayList<>();

    public Booking transformToEntity() {
        Booking booking = new Booking();
        booking.setEndDate(this.endDate);
        booking.setStartDate(this.startDate);
        booking.setStatus(this.status);
        booking.setLateReturn(this.lateReturn);
        return booking;
    }

    public void updateBooking(Booking booking) {
        booking.setStatus(this.status);
        booking.setStartDate(this.startDate);
        booking.setEndDate(this.endDate);
        booking.setLateReturn(this.lateReturn);
    }
}
