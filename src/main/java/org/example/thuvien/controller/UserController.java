package org.example.thuvien.controller;

import jakarta.validation.Valid;
import org.example.thuvien.dto.UserRequestDTO;
import org.example.thuvien.dto.UserResponseDTO;
import org.example.thuvien.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    // Client gửi RequestDTO -> Server trả về ResponseDTO
    @PostMapping("/register")
    public UserResponseDTO register(@Valid @RequestBody UserRequestDTO request) {
        return userService.registerUser(request);
    }

    // Server chỉ trả về ResponseDTO (đã lọc password)
    @GetMapping
    public List<UserResponseDTO> getAll() {
        return userService.getAllUsers();
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