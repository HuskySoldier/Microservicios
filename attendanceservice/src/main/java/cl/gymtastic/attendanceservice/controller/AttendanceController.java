package cl.gymtastic.attendanceservice.controller;

import cl.gymtastic.attendanceservice.model.Attendance;
import cl.gymtastic.attendanceservice.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attendance")
@Tag(name = "Attendance Service")
@CrossOrigin
public class AttendanceController {

    @Autowired
    private AttendanceService service;

    @Operation(summary = "Obtener historial de un usuario")
    @GetMapping("/history/{email}")
    public ResponseEntity<List<Attendance>> getHistory(@PathVariable String email) {
        return ResponseEntity.ok(service.getHistory(email));
    }

    @Operation(summary = "Registrar entrada (Check-In)")
    @PostMapping("/check-in")
    public ResponseEntity<Attendance> checkIn(@RequestParam String email) {
        return ResponseEntity.ok(service.checkIn(email));
    }

    @Operation(summary = "Registrar salida (Check-Out)")
    @PostMapping("/check-out")
    public ResponseEntity<?> checkOut(@RequestParam String email) {
        try {
            return ResponseEntity.ok(service.checkOut(email));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}