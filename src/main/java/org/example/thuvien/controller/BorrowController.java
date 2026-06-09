package org.example.thuvien.controller;

import org.example.thuvien.model.BorrowRecord;
import org.example.thuvien.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @PostMapping
    public BorrowRecord borrowBook(@RequestParam Long userId, @RequestParam Long bookId) {
        return borrowService.borrowBook(userId, bookId);
    }

    @PutMapping("/return")
    public BorrowRecord returnBook(@RequestParam Long bookId) {
        return borrowService.returnBook(bookId);
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
}
