package cl.gymtastic.checkoutservice.dto;

import lombok.Data;

@Data
public class SedeDto {
    // Coincide con la Sede en la App
    private int id;
    private String nombre;
    private String direccion; // No la usamos en el backend, pero la incluimos
    private double lat;
    private double lng;
}