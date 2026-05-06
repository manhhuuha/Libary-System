package org.example.thuvien.controller;

import org.example.thuvien.model.BorrowRecord;
import org.example.thuvien.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    // API: POST http://localhost:8080/api/borrow?userId=1&bookId=1
    @PostMapping
    public BorrowRecord borrowBook(@RequestParam Long userId, @RequestParam Long bookId) {
        return borrowService.borrowBook(userId, bookId);
    }
    // API: PUT http://localhost:8080/api/borrow/return?bookId=1
    @PutMapping("/return")
    public BorrowRecord returnBook(@RequestParam Long bookId) {
        return borrowService.returnBook(bookId);
    }
    @GetMapping("/count-book-not-return")
    public long countBookNotReturn(){
        return borrowService.countBookNotReturn();
    }
}