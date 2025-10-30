package com.erp.controller;

import com.erp.repository.CrmNoteRepository;
import com.erp.repository.CustomerRepository;
import com.erp.repository.UserRepository;
import entity.CrmNote;
import entity.Customer;
import entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/crm-notes")
@CrossOrigin
public class CrmNoteController {

    @Autowired private CrmNoteRepository noteRepo;
    @Autowired private CustomerRepository customerRepo;
    @Autowired private UserRepository userRepo;

    // ✅ List all notes
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(noteRepo.findAll());
    }

    // ✅ Notes by customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getByCustomer(@PathVariable Long customerId) {
        if (!customerRepo.existsById(customerId)) {
            return ResponseEntity.status(404)
                .body(Map.of("error", "Customer not found"));
        }
        return ResponseEntity.ok(
            noteRepo.findByCustomer_IdOrderByCreatedAtDesc(customerId)
        );
    }

    // ✅ Add note
    @PostMapping
    public ResponseEntity<?> addNote(@RequestBody Map<String, Object> data) {
        Long customerId = Long.valueOf(data.get("customerId").toString());
        String noteText = data.get("note").toString();
        Long userId = data.containsKey("userId") 
                        ? Long.valueOf(data.get("userId").toString()) 
                        : null;

        Customer customer = customerRepo.findById(customerId).orElse(null);
        if (customer == null) {
            return ResponseEntity.status(404)
                .body(Map.of("error", "Customer not found"));
        }

        User user = null;
        if (userId != null) {
            user = userRepo.findById(userId).orElse(null);
        }

        CrmNote note = new CrmNote();
        note.setCustomer(customer);
        note.setUser(user);
        note.setNote(noteText);
        note.setCreatedAt(Instant.now());

        return ResponseEntity.ok(noteRepo.save(note));
    }

    // ✅ Update note
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        CrmNote note = noteRepo.findById(id).orElse(null);
        if (note == null) {
            return ResponseEntity.status(404)
                .body(Map.of("error", "Note not found"));
        }

        if (data.containsKey("note")) {
            note.setNote(data.get("note").toString());
        }

        return ResponseEntity.ok(noteRepo.save(note));
    }

    // ✅ Delete note
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!noteRepo.existsById(id)) {
            return ResponseEntity.status(404)
                .body(Map.of("error", "Note not found"));
        }
        noteRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Note deleted"));
    }
}
