package org.example.thuvien.controller;

import jakarta.validation.Valid;
import org.example.thuvien.dto.UserRequestDTO;
import org.example.thuvien.dto.UserResponseDTO;
import org.example.thuvien.model.BorrowRecord;
import org.example.thuvien.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public UserResponseDTO register(@Valid @RequestBody UserRequestDTO request) {
        return userService.registerUser(request);
    }

    @GetMapping("/me")
    public UserResponseDTO getCurrentUser() {
        return userService.getCurrentUserDTO();
    }

    @GetMapping("/me/borrow-history")
    public List<BorrowRecord> getMyBorrowHistory() {
        return userService.getCurrentUserBorrowHistory();
    }

    @GetMapping("/me/current-borrows")
    public List<BorrowRecord> getMyCurrentBorrows() {
        return userService.getCurrentUserCurrentBorrows();
    }

    @GetMapping("/search")
    public List<UserResponseDTO> search(@RequestParam String keyword) {
        return userService.searchUsers(keyword);
    }

    @GetMapping
    public List<UserResponseDTO> getAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/paged")
    public Page<UserResponseDTO> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userService.getUsersPaged(page, size);
    }

    @GetMapping("/{id}")
    public UserResponseDTO getById(@PathVariable Long id) {
        return userService.getUserByIdDTO(id);
    }

    @PutMapping("/{id}")
    public UserResponseDTO update(@PathVariable Long id, @Valid @RequestBody UserRequestDTO request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return "Đã xóa thành công User có ID: " + id;
    }
}
