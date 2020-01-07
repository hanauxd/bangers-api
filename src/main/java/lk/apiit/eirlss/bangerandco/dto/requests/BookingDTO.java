package lk.apiit.eirlss.bangerandco.dto.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import lk.apiit.eirlss.bangerandco.models.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
    private String vehicleId;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date startDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date endDate;

    public Booking transformToEntity() {
        Booking booking = new Booking();
        booking.setEndDate(this.endDate);
        booking.setStartDate(this.startDate);
        return booking;
    }
}
