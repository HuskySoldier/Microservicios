package cl.gymtastic.attendanceservice.service;

import cl.gymtastic.attendanceservice.model.Attendance;
import cl.gymtastic.attendanceservice.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository repository;

    public List<Attendance> getHistory(String email) {
        return repository.findByUserEmailOrderByTimestampDesc(email);
    }

    public Attendance checkIn(String email) {
        // Opcional: Verificar si ya tiene uno abierto para no duplicar
        Optional<Attendance> open = repository.findLastOpen(email);
        if (open.isPresent()) {
            return open.get(); // Ya está adentro
        }

        Attendance newEntry = Attendance.builder()
                .userEmail(email)
                .timestamp(System.currentTimeMillis())
                .build();
        return repository.save(newEntry);
    }

    public Attendance checkOut(String email) {
        Optional<Attendance> lastOpen = repository.findLastOpen(email);
        if (lastOpen.isPresent()) {
            Attendance att = lastOpen.get();
            att.setCheckOutTimestamp(System.currentTimeMillis());
            return repository.save(att);
        }
        throw new RuntimeException("No hay sesión activa para cerrar.");
    }
}