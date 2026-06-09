package org.example.thuvien.seed;

import org.example.thuvien.model.*;
import org.example.thuvien.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private BookCopyRepository bookCopyRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) {
        long bookCount = bookRepository.count();
        if (bookCount > 0) {
            log.info("Dữ liệu đã tồn tại, bỏ qua seed.");
            return;
        }

        log.warn("Dọn dẹp dữ liệu cũ (kể cả soft-deleted)...");
        hardDeleteAll();

        log.info("Bắt đầu seed dữ liệu...");
        seedCategories();
        seedUsers();
        seedBooks();
        long copyCount = bookCopyRepository.count();
        log.info("Seed dữ liệu hoàn tất: admin/admin, 99 users, {} categories, {} books, {} bản sao.",
                categoryRepository.count(), bookRepository.count(), copyCount);
    }

    private void hardDeleteAll() {
        jdbcTemplate.execute("DELETE FROM borrow_records");
        jdbcTemplate.execute("DELETE FROM book_copies");
        jdbcTemplate.execute("DELETE FROM books");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM categories");
        log.info("Đã xóa toàn bộ dữ liệu cũ.");
    }

    private void seedCategories() {
        List<String> names = List.of(
                "Công nghệ thông tin", "Văn học", "Kinh tế",
                "Khoa học tự nhiên", "Khoa học xã hội", "Ngoại ngữ",
                "Toán học", "Lịch sử", "Y học", "Kỹ thuật"
        );
        for (String name : names) {
            Category c = new Category();
            c.setName(name);
            categoryRepository.save(c);
        }
    }

    private void seedUsers() {
        String[] lastNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng",
                "Huỳnh", "Võ", "Đặng", "Bùi", "Đỗ"};
        String[] firstNames = {"Văn Anh", "Thị Bích", "Văn Cường", "Thị Dung", "Văn Em",
                "Thị Phương", "Văn Giang", "Thị Hoa", "Văn Hưng", "Minh Khang"};
        String[] userTypes = {"STUDENT", "TEACHER", "GUEST"};
        Random rand = new Random();

        for (int i = 0; i < 100; i++) {
            User user = new User();
            String ln = lastNames[i / 10];
            String fn = firstNames[i % 10];
            user.setFullName(ln + " " + fn);
            user.setEmail(("user" + (i + 1) + "@school.edu.vn").toLowerCase());
            user.setPhoneNumber(String.format("09%08d", rand.nextInt(100_000_000)));
            user.setIdentityCard(String.format("%012d", i + 1));
            user.setUsername("user" + (i + 1));
            user.setPassword(passwordEncoder.encode("123456"));
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());

            if (i == 0) {
                user.setFullName("ADMIN");
                user.setEmail("admin@library.edu.vn");
                user.setUsername("admin");
                user.setPassword(passwordEncoder.encode("admin"));
                user.setRole("ADMIN");
                user.setUserType(null);
            } else {
                user.setRole("PATRON");
                user.setUserType(userTypes[rand.nextInt(userTypes.length)]);
            }
            userRepository.save(user);
        }
    }

    private void seedBooks() {
        List<Category> categories = categoryRepository.findAll();
        Random rand = new Random();
        Set<String> usedIsbns = new HashSet<>();

        String[] subjects = {
                "Lập trình Java", "Python cơ bản", "Cấu trúc dữ liệu", "Giải thuật", "Mạng máy tính",
                "Cơ sở dữ liệu", "Hệ điều hành", "Trí tuệ nhân tạo", "An ninh mạng", "Phát triển Web",
                "Truyện Kiều", "Văn học Việt Nam", "Văn học nước ngoài", "Thơ ca", "Kịch bản văn học",
                "Phê bình văn học", "Ngôn ngữ học", "Văn hóa dân gian", "Văn học đương đại", "Lý luận văn học",
                "Kinh tế vi mô", "Kinh tế vĩ mô", "Tài chính ngân hàng", "Kế toán tài chính", "Quản trị kinh doanh",
                "Marketing căn bản", "Thương mại điện tử", "Kinh tế lượng", "Đầu tư tài chính", "Luật kinh tế",
                "Vật lý đại cương", "Hóa học hữu cơ", "Sinh học phân tử", "Hóa học vô cơ", "Vật lý lượng tử",
                "Địa chất học", "Thiên văn học", "Khoa học môi trường", "Hóa phân tích", "Vật lý thiên văn",
                "Triết học Mác", "Tâm lý học đại cương", "Xã hội học", "Nhân học văn hóa", "Logic học",
                "Giáo dục học", "Tôn giáo học", "Dân tộc học", "Tội phạm học", "Chính trị học",
                "Tiếng Anh giao tiếp", "Tiếng Trung cơ bản", "Tiếng Nhật sơ cấp", "Tiếng Hàn nhập môn", "Ngữ pháp tiếng Anh",
                "IELTS Reading", "TOEIC Listening", "Tiếng Pháp du lịch", "Tiếng Đức căn bản", "Biên dịch Anh-Việt",
                "Đại số tuyến tính", "Giải tích hàm", "Phương trình vi phân", "Xác suất thống kê", "Hình học không gian",
                "Số học", "Tô pô học", "Toán rời rạc", "Lý thuyết đồ thị", "Toán ứng dụng",
                "Lịch sử Việt Nam", "Lịch sử thế giới", "Lịch sử cổ đại", "Lịch sử trung đại", "Lịch sử hiện đại",
                "Khảo cổ học", "Sử học", "Lịch sử văn minh", "Lịch sử Đông Nam Á", "Lịch sử chiến tranh",
                "Giải phẫu người", "Dược lý học", "Bệnh học nội khoa", "Vi sinh y học", "Dinh dưỡng học",
                "Y học cổ truyền", "Nhi khoa", "Sản phụ khoa", "Thần kinh học", "Da liễu",
                "Cơ học kỹ thuật", "Điện tử căn bản", "Kỹ thuật xây dựng", "Cơ khí chế tạo", "Tự động hóa",
                "Kỹ thuật nhiệt", "Kỹ thuật môi trường", "Kỹ thuật giao thông", "Vật liệu học", "Robot công nghiệp"
        };

        String[] authors = {
                "Nguyễn Văn An", "Trần Thị Bình", "Lê Văn Cảnh", "Phạm Thị Dung", "Hoàng Văn Em",
                "Huỳnh Thị Phượng", "Võ Văn Giỏi", "Đặng Thị Hạnh", "Bùi Văn Ý", "Đỗ Minh Khang",
                "Nguyễn Thị Lan", "Trần Văn Minh", "Lê Thị Ngọc", "Phạm Văn Phú", "Hoàng Thị Quyên",
                "Huỳnh Văn Sang", "Võ Thị Thúy", "Đặng Văn Trọng", "Bùi Thị Vân", "Đỗ Văn Xuân"
        };

        String[] titlePatterns = {
                "Giáo trình %s", "Bài tập %s", "%s căn bản", "%s nâng cao",
                "%s tập 1", "%s tập 2", "Thực hành %s", "Giới thiệu %s",
                "%s", "Chuyên đề %s"
        };

        String[] shelfRows = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        int count = 0;

        for (int si = 0; si < subjects.length && count < 1000; si++) {
            for (int p = 0; p < titlePatterns.length && count < 1000; p++) {
                Book book = new Book();
                String subject = subjects[si];
                book.setTitle(titlePatterns[p].formatted(subject));
                book.setAuthor(authors[rand.nextInt(authors.length)]);
                book.setPublishedYear(1980 + rand.nextInt(46));
                book.setLocation(shelfRows[rand.nextInt(shelfRows.length)] + "-"
                        + (rand.nextInt(50) + 1));
                book.setCategory(categories.get(rand.nextInt(categories.size())));
                bookRepository.save(book);

                int qty = rand.nextInt(5) + 1;
                for (int c = 1; c <= qty; c++) {
                    BookCopy copy = new BookCopy();
                    copy.setBook(book);
                    copy.setCopyNumber(String.valueOf(c));
                    String isbn;
                    do { isbn = generateIsbn(rand); } while (!usedIsbns.add(isbn));
                    copy.setIsbn(isbn);
                    copy.setStatus(BookCopyStatus.AVAILABLE);
                    bookCopyRepository.save(copy);
                }
                count++;
            }
        }
    }

    private String generateIsbn(Random rand) {
        int len = rand.nextBoolean() ? 10 : 13;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }
}
