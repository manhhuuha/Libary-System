package org.example.thuvien.controller;

import org.example.thuvien.model.BorrowRecord;
import org.example.thuvien.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @PostMapping
    public BorrowRecord borrowBook(@RequestParam Long userId, @RequestParam Long bookCopyId,
                                   @RequestParam(required = false) LocalDate dueDate) {
        return borrowService.borrowBook(userId, bookCopyId, dueDate);
    }

    @PutMapping("/return")
    public BorrowRecord returnBook(@RequestParam Long bookCopyId) {
        return borrowService.returnBook(bookCopyId);
    }

    @GetMapping("/due-soon")
    public List<BorrowRecord> getDueSoon() {
        return borrowService.getDueSoon();
    }

    @GetMapping("/overdue")
    public List<BorrowRecord> getOverdue() {
        return borrowService.getOverdue();
    }

    @GetMapping("/count-book-not-return")
    public long countBookNotReturn() {
        return borrowService.countBookNotReturn();
    }

    @GetMapping("/current")
    public List<BorrowRecord> getCurrentBorrows() {
        return borrowService.getAllCurrentBorrows();
    }

    @GetMapping("/history")
    public List<BorrowRecord> getBorrowHistory(@RequestParam Long userId) {
        return borrowService.getBorrowHistoryByUser(userId);
    }

    @GetMapping("/history/all")
    public List<BorrowRecord> getAllBorrowHistory() {
        return borrowService.getAllBorrowHistory();
    }

    @PostMapping("/send-reminder/{id}")
    public String sendReminder(@PathVariable Long id) {
        return borrowService.sendReminder(id);
    }
}
