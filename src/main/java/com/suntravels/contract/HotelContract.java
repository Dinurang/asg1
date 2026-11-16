package com.suntravels.contract;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hotel_contracts")
public class HotelContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private String hotelName;
    @NotNull
    @Column(nullable = false)
    private LocalDate validFrom;
    @NotNull
    @Column(nullable = false)
    private LocalDate validTo;
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal markupPercent;
    @Valid
    @NotEmpty
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomType> roomTypes = new ArrayList<>();

    protected HotelContract() {
    }

    public HotelContract(String hotelName, LocalDate validFrom, LocalDate validTo,
                         BigDecimal markupPercent, List<RoomType> roomTypes) {
        this.hotelName = hotelName;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.markupPercent = markupPercent;
        this.roomTypes = new ArrayList<>(roomTypes);
    }

    public Long getId() { return id; }
    public String getHotelName() { return hotelName; }
    public LocalDate getValidFrom() { return validFrom; }
    public LocalDate getValidTo() { return validTo; }
    public BigDecimal getMarkupPercent() { return markupPercent; }
    public List<RoomType> getRoomTypes() { return roomTypes; }

    public void update(String hotelName, LocalDate validFrom, LocalDate validTo,
                       BigDecimal markupPercent, List<RoomType> roomTypes) {
        this.hotelName = hotelName;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.markupPercent = markupPercent;
        this.roomTypes.clear();
        this.roomTypes.addAll(roomTypes);
    }
}
