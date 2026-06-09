package org.example.thuvien.service;

import org.example.thuvien.dto.UserRequestDTO;
import org.example.thuvien.dto.UserResponseDTO;
import org.example.thuvien.exception.ResourceNotFoundException;
import org.example.thuvien.mapper.UserMapper;
import org.example.thuvien.model.BorrowRecord;
import org.example.thuvien.model.User;
import org.example.thuvien.repository.BorrowRepository;
import org.example.thuvien.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private UserMapper userMapper;

    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toResponseDTOList(users);
    }

    public Page<UserResponseDTO> getUsersPaged(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size)).map(userMapper::toResponseDTO);
    }

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng ID: " + id));
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public UserResponseDTO registerUser(UserRequestDTO request) {
        User user = userMapper.toEntity(request);

        if (request.password() != null && !request.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng ID: " + id));

        existingUser.setFullName(request.fullName());
        existingUser.setEmail(request.email());
        existingUser.setPhoneNumber(request.phoneNumber());
        existingUser.setIdentityCard(request.identityCard());

        if (request.password() != null && !request.password().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.password()));
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponseDTO(updatedUser);
    }

    public List<UserResponseDTO> searchUsers(String keyword) {
        List<User> users = userRepository.findByFullNameContainingIgnoreCaseOrIdentityCardContaining(keyword, keyword);
        return userMapper.toResponseDTOList(users);
    }

    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng ID: " + id));
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng hiện tại"));
    }

    public UserResponseDTO getCurrentUserDTO() {
        return userMapper.toResponseDTO(getCurrentUser());
    }

    public List<BorrowRecord> getCurrentUserBorrowHistory() {
        User user = getCurrentUser();
        return borrowRepository.findByUserId(user.getId());
    }

    public List<BorrowRecord> getCurrentUserCurrentBorrows() {
        User user = getCurrentUser();
        return borrowRepository.findByUserIdAndReturnDateIsNull(user.getId());
    }

    public UserResponseDTO getUserByIdDTO(Long id) {
        User user = getUserById(id);
        return userMapper.toResponseDTO(user);
    }
}
