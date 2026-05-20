package org.example.thuvien.service;

import org.example.thuvien.dto.UserRequestDTO;
import org.example.thuvien.dto.UserResponseDTO;
import org.example.thuvien.mapper.UserMapper;
import org.example.thuvien.model.Book;
import org.example.thuvien.model.User;
import org.example.thuvien.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    // Lấy toàn bộ người dùng
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        // Dùng MapStruct để convert cả List cực gọn
        return userMapper.toResponseDTOList(users);
    }

    public User saveUser(User user) {
        // Mã hóa mật khẩu trước khi lưu vào DB
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + id));
    }

    public void deleteUser(Long id) {
        User user = getUserById(id); // Kiểm tra tồn tại
        userRepository.delete(user);
    }

    public UserResponseDTO registerUser(UserRequestDTO request) {
        // 1. Chuyển DTO sang Entity để làm việc với DB
        User user = userMapper.toEntity(request);

        // 2. Mã hóa mật khẩu (Nếu có)
        if (request.password() != null && !request.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        // 3. Lưu vào DB
        User savedUser = userRepository.save(user);

        // 4. Trả về DTO để Controller phản hồi cho Client
        return userMapper.toResponseDTO(savedUser);
    }

    // Cập nhật thông tin người dùng
    public UserResponseDTO updateUser(Long id, UserRequestDTO request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + id));

        // Cập nhật các trường thông tin
        existingUser.setFullName(request.fullName());
        existingUser.setEmail(request.email());
        existingUser.setPhoneNumber(request.phoneNumber());
        existingUser.setIdentityCard(request.identityCard());

        // Nếu có đổi mật khẩu thì mã hóa lại
        if (request.password() != null && !request.password().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.password()));
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponseDTO(updatedUser);
    }

    // Tìm kiếm người dùng theo keyword (Tên hoặc CCCD)
    public List<UserResponseDTO> searchUsers(String keyword) {
        List<User> users = userRepository.findByFullNameContainingIgnoreCaseOrIdentityCardContaining(keyword, keyword);
        return userMapper.toResponseDTOList(users);
    }


    // Hàm lấy Entity gốc (Dùng nội bộ cho BorrowService)
    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + id));
    }
}