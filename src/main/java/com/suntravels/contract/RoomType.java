package com.suntravels.contract;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Entity
@Table(name = "room_types")
public class RoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private String name;
    @DecimalMin(value = "0.01")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerPersonPerNight;
    @Min(0)
    @Column(nullable = false)
    private int rooms;
    @Min(1)
    @Column(nullable = false)
    private int maxAdults;

    protected RoomType() {
    }

    public RoomType(String name, BigDecimal price, int rooms, int maxAdults) {
        this.name = name;
        this.pricePerPersonPerNight = price;
        this.rooms = rooms;
        this.maxAdults = maxAdults;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPricePerPersonPerNight() { return pricePerPersonPerNight; }
    public int getRooms() { return rooms; }
    public int getMaxAdults() { return maxAdults; }
}
