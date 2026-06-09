package org.example.thuvien.service;

import org.example.thuvien.model.BorrowRecord;
import org.example.thuvien.model.BorrowStatus;
import org.example.thuvien.repository.BorrowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificationService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private BorrowRepository borrowRepository;

    @Scheduled(cron = "0 0 8 * * *")
    public void sendDueSoonReminders() {
        if (mailSender == null) return;

        List<BorrowRecord> dueSoonRecords = borrowRepository
                .findByStatusAndReturnDateIsNullAndDueDateLessThanEqual(
                        BorrowStatus.BORROWING, LocalDate.now().plusDays(3));

        for (BorrowRecord record : dueSoonRecords) {
            sendReminder(record);
        }
    }

    @Transactional
    public String sendReminder(BorrowRecord record) {
        if (mailSender == null) return "Email chưa được cấu hình (spring.mail.username/password)";

        String email = record.getUser().getEmail();
        if (email == null || email.isBlank()) return "Người dùng không có email";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Nhắc nhở: Sách sắp đến hạn trả");
        message.setText(String.format("""
                Kính gửi %s,

                Sách "%s" của bạn sắp đến hạn trả vào ngày %s.
                Vui lòng mang sách đến thư viện để trả đúng hạn.

                Trân trọng,
                Thư viện trường học""",
                record.getUser().getFullName(),
                record.getBookCopy().getBook().getTitle(),
                record.getDueDate()));

        mailSender.send(message);
        record.setEmailSent(true);
        borrowRepository.save(record);
        return "Đã gửi email nhắc nhở đến " + email;
    }
}
