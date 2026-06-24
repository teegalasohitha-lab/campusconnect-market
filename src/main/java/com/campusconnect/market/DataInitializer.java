package com.campusconnect.market;

import com.campusconnect.market.model.*;
import com.campusconnect.market.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Seeds the database with sample categories, users, and products on first run.
 * Skips seeding if data already exists.
 */
@Component
public class DataInitializer implements CommandLineRunner {

        private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

        @Autowired
        private UserRepository userRepository;
        @Autowired
        private CategoryRepository categoryRepository;
        @Autowired
        private ProductRepository productRepository;
        @Autowired
        private OrderRepository orderRepository;
        @Autowired
        private OrderItemRepository orderItemRepository;
        @Autowired
        private WishlistRepository wishlistRepository;
        @Autowired
        private PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) {
                if (productRepository.findByTitleContainingIgnoreCase("Dress").size() > 0) {
                        log.info("✔ CampusConnect test data already exists. Skipping initialization.");
                        return;
                }

                log.info("🌱 Cleaning up and seeding database with sample data...");

                // Cleanup existing duplicates of sample data if any
                productRepository.findAll().forEach(p -> {
                        if (p.getTitle().equals("AITS Official Lab Coat / Apron") ||
                                        p.getTitle().equals("Cotton Track Pants - Sports") ||
                                        p.getTitle().equals("Campus Connect Hoodie (Blue)")) {
                                productRepository.delete(p);
                        }
                });

                // ── Categories ──────────────────────────────────────────────
                Category electronics = save(Category.builder()
                                .name("Electronics").description("Gadgets and electronic devices")
                                .icon("fa-laptop").color("#1A3C6E").build());

                Category books = save(Category.builder()
                                .name("Books & Stationery").description("Textbooks, notebooks, pens and more")
                                .icon("fa-book").color("#FF6B35").build());

                Category clothing = save(Category.builder()
                                .name("Clothing").description("Shirts, jeans, uniforms and accessories")
                                .icon("fa-tshirt").color("#6C63FF").build());

                Category food = save(Category.builder()
                                .name("Food & Snacks").description("Homemade snacks, tiffins and beverages")
                                .icon("fa-utensils").color("#28A745").build());

                Category crafts = save(Category.builder()
                                .name("Handmade & Crafts").description("Handcrafted items by local creators")
                                .icon("fa-paint-brush").color("#DC3545").build());

                Category sports = save(Category.builder()
                                .name("Sports & Fitness").description("Sports equipment and gym accessories")
                                .icon("fa-dumbbell").color("#17A2B8").build());

                // Student Exchange Hub (parent category)
                Category studentHub = save(Category.builder()
                                .name("Student Exchange Hub").description("Buy and sell student essentials on campus")
                                .icon("fa-graduation-cap").color("#FFC107").build());

                // Student Exchange subcategories
                save(Category.builder().name("Textbooks").icon("fa-book-open")
                                .color("#FFC107").parent(studentHub).build());
                save(Category.builder().name("Lab Records").icon("fa-flask")
                                .color("#FFC107").parent(studentHub).build());
                save(Category.builder().name("Project Components").icon("fa-microchip")
                                .color("#FFC107").parent(studentHub).build());
                save(Category.builder().name("Calculators").icon("fa-calculator")
                                .color("#FFC107").parent(studentHub).build());

                // ── Users ───────────────────────────────────────────────────
                String encoded = passwordEncoder.encode("Password@123");

                saveUser(User.builder()
                                .name("Admin User").email("admin@campusconnect.com")
                                .password(encoded).role(Role.ADMIN)
                                .phone("9000000001").locality("Admin HQ").isActive(true).build());

                User seller1 = saveUser(User.builder()
                                .name("Priya Electronics").email("priya@seller.com")
                                .password(encoded).role(Role.SELLER)
                                .phone("9000000002").locality("Anna Nagar, Chennai").isActive(true).build());

                User seller2 = saveUser(User.builder()
                                .name("Ravi Homemade Foods").email("ravi@seller.com")
                                .password(encoded).role(Role.SELLER)
                                .phone("9000000003").locality("T. Nagar, Chennai").isActive(true).build());

                User seller3 = saveUser(User.builder()
                                .name("Campus Books Corner").email("books@seller.com")
                                .password(encoded).role(Role.SELLER)
                                .phone("9000000004").locality("Velachery, Chennai").isActive(true).build());

                User customer1 = saveUser(User.builder()
                                .name("Arjun Kumar").email("arjun@customer.com")
                                .password(encoded).role(Role.CUSTOMER)
                                .phone("9000000005").locality("Adyar, Chennai").isActive(true).build());

                User customer2 = saveUser(User.builder()
                                .name("Sneha Patel").email("sneha@customer.com")
                                .password(encoded).role(Role.CUSTOMER)
                                .phone("9000000006").locality("Anna Nagar, Chennai").isActive(true).build());

                saveUser(User.builder()
                                .name("Kiran M").email("kiran@customer.com")
                                .password(encoded).role(Role.CUSTOMER)
                                .phone("9000000007").locality("Velachery, Chennai").isActive(true).build());

                // ── Products ────────────────────────────────────────────────
                // Electronics
                saveProduct(Product.builder()
                                .title("Wireless Bluetooth Earphones")
                                .description("High-quality wireless earphones with 20hr battery life and noise cancellation")
                                .price(new BigDecimal("899")).stock(50)
                                .seller(seller1).category(electronics).location("Anna Nagar, Chennai")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400")
                                .build());

                saveProduct(Product.builder()
                                .title("USB-C Laptop Charger 65W")
                                .description("Universal laptop charger compatible with most laptops")
                                .price(new BigDecimal("549")).stock(30)
                                .seller(seller1).category(electronics).location("Anna Nagar, Chennai")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=400")
                                .build());

                saveProduct(Product.builder()
                                .title("Scientific Calculator (Casio FX-991)")
                                .description("Used Casio scientific calculator in excellent condition")
                                .price(new BigDecimal("350")).stock(5)
                                .seller(seller3).category(studentHub).location("Velachery, Chennai")
                                .type(ProductType.EXCHANGE).condition(ProductCondition.LIKE_NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1611532736597-de2d4265fba3?w=400")
                                .build());

                saveProduct(Product.builder()
                                .title("Power Bank 10000mAh")
                                .description("Compact power bank with fast charging support")
                                .price(new BigDecimal("699")).stock(25)
                                .seller(seller1).category(electronics).location("Anna Nagar, Chennai")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=400")
                                .build());

                // Books / Student Exchange
                saveProduct(Product.builder()
                                .title("Engineering Mathematics Vol 1 & 2")
                                .description("Complete set by Dr. R.D. Sharma — barely used, clear notes inside")
                                .price(new BigDecimal("280")).stock(3)
                                .seller(seller3).category(books).location("Velachery, Chennai")
                                .type(ProductType.EXCHANGE).condition(ProductCondition.GOOD).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400")
                                .build());

                saveProduct(Product.builder()
                                .title("Operating Systems by Silberschatz")
                                .description("Classic OS textbook — 10th edition, minor highlights")
                                .price(new BigDecimal("350")).stock(2)
                                .seller(seller3).category(books).location("Velachery, Chennai")
                                .type(ProductType.EXCHANGE).condition(ProductCondition.GOOD).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400")
                                .build());

                saveProduct(Product.builder()
                                .title("Blank Lab Record (A4, 200 pages)")
                                .description("High quality lab record book — ruled and plain alternate pages")
                                .price(new BigDecimal("65")).stock(100)
                                .seller(seller3).category(studentHub).location("Velachery, Chennai")
                                .type(ProductType.EXCHANGE).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1554415707-6e8cfc93fe23?w=400").build());

                saveProduct(Product.builder()
                                .title("Homemade Samosa Box (10 pcs)")
                                .description("Freshly made crispy samosas — order by 9 AM for same-day delivery")
                                .price(new BigDecimal("120")).stock(20)
                                .seller(seller2).category(food).location("T. Nagar, Chennai")
                                .type(ProductType.PREORDER).condition(ProductCondition.NEW).isActive(true)
                                .preOrderDeadline(LocalDate.now().plusDays(7)).minPreOrderQty(1)
                                .imageUrl("https://images.unsplash.com/photo-1601050690597-df0568f70950?w=400")
                                .build());

                saveProduct(Product.builder()
                                .title("Tiffin Box — South Indian Lunch")
                                .description("Homemade fresh lunch box: rice, dal, sabzi, chapati & pickle")
                                .price(new BigDecimal("80")).stock(15)
                                .seller(seller2).category(food).location("T. Nagar, Chennai")
                                .type(ProductType.PREORDER).condition(ProductCondition.NEW).isActive(true)
                                .preOrderDeadline(LocalDate.now().plusDays(3)).minPreOrderQty(1)
                                .imageUrl("https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400").build());

                saveProduct(Product.builder()
                                .title("Classic Juicy Chicken Burger")
                                .description("Homemade chicken patty, fresh lettuce, and special campus sauce. Served with fries.")
                                .price(new BigDecimal("150")).stock(20)
                                .seller(seller2).category(food).location("T. Nagar, Chennai")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400")
                                .build());

                saveProduct(Product.builder()
                                .title("Fresh Watermelon Juice")
                                .description("100% natural, freshly squeezed watermelon juice. No added sugar.")
                                .price(new BigDecimal("60")).stock(40)
                                .seller(seller2).category(food).location("T. Nagar, Chennai")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1543158071-ca426b919630?w=400")
                                .build());

                saveProduct(Product.builder()
                                .title("Handmade Macramé Wall Hanging")
                                .description("Beautiful boho-style handcrafted wall art — 60cm x 40cm")
                                .price(new BigDecimal("350")).stock(8)
                                .seller(seller2).category(crafts).location("T. Nagar, Chennai")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1563293958-c22dc4c2c8bd?w=400").build());

                saveProduct(Product.builder()
                                .title("Handmade Scented Candles Set")
                                .description("Set of 3 soy wax candles — lavender, vanilla, and rose fragrances")
                                .price(new BigDecimal("299")).stock(15)
                                .seller(seller2).category(crafts).location("T. Nagar, Chennai")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=400")
                                .build());

                // Sports
                saveProduct(Product.builder()
                                .title("Cricket Bat (Kashmir Willow)")
                                .description("Full-size Kashmir willow cricket bat — great for practice")
                                .price(new BigDecimal("1200")).stock(10)
                                .seller(seller1).category(sports).location("Anna Nagar, Chennai")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1540747913346-19e32dc3e97e?w=400")
                                .build());

                saveProduct(Product.builder()
                                .title("Yoga Mat (6mm thick)")
                                .description("Anti-slip premium yoga mat with carry strap")
                                .price(new BigDecimal("399")).stock(20)
                                .seller(seller1).category(sports).location("Anna Nagar, Chennai")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1545389336-cf090694435e?w=400").build());

                saveProduct(Product.builder()
                                .title("Campus Event Formal Blazer")
                                .description("Professional navy blue blazer for college presentations and events.")
                                .price(new BigDecimal("1800")).stock(15)
                                .seller(seller1).category(clothing).location("aits tirupati")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1594932224036-9c2049ba3f39?w=400")
                                .build());

                saveProduct(Product.builder()
                                .title("Engineering Mini-Drafter (Technical Drawing)")
                                .description("High precision mini-drafter for engineering drawing classes. Used for one semester, excellent condition.")
                                .price(new BigDecimal("750")).stock(3)
                                .seller(seller1).category(electronics).location("aits tirupati")
                                .type(ProductType.EXCHANGE).condition(ProductCondition.GOOD).isActive(true)
                                .imageUrl("/images/seed/drafter.png").build());

                saveProduct(Product.builder()
                                .title("Engineering Textbook Bundle (1st Year)")
                                .description("Complete set of textbooks for 1st year engineering: Physics, Calculus, and Data Structures.")
                                .price(new BigDecimal("1200")).stock(2)
                                .seller(seller1).category(books).location("aits tirupati")
                                .type(ProductType.EXCHANGE).condition(ProductCondition.FAIR).isActive(true)
                                .imageUrl("/images/seed/books.png").build());

                // Project Components (Student Exchange)
                saveProduct(Product.builder()
                                .title("Arduino Uno R3 + Starter Kit")
                                .description("Complete Arduino starter kit with sensors, LEDs, jumper wires")
                                .price(new BigDecimal("650")).stock(12)
                                .seller(seller1).category(studentHub).location("Anna Nagar, Chennai")
                                .type(ProductType.EXCHANGE).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1608564697071-ddf911d81370?w=400")
                                .build());

                Product p1 = saveProduct(Product.builder()
                                .title("Raspberry Pi 4 (4GB) — Used")
                                .description("Working Raspberry Pi 4B with 4GB RAM, power adapter included")
                                .price(new BigDecimal("3500")).stock(2)
                                .seller(seller3).category(studentHub).location("Velachery, Chennai")
                                .type(ProductType.EXCHANGE).condition(ProductCondition.LIKE_NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1518770660439-4636190af475?w=400")
                                .build());

                // ── Additional Sample Products ──────────────────────────────
                // Electronics
                saveProduct(Product.builder()
                                .title("Logitech Wireless Mouse M235")
                                .description("Compact, reliable wireless mouse with long battery life. Color: Grey.")
                                .price(new BigDecimal("699")).stock(15)
                                .seller(seller1).category(electronics).location("Anna Nagar, Chennai")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400")
                                .build());

                saveProduct(Product.builder()
                                .title("Ambrane 10000mAh Power Bank")
                                .description("Fast charging power bank with dual output. Pocket-sized design.")
                                .price(new BigDecimal("899")).stock(25)
                                .seller(seller1).category(electronics).location("Anna Nagar, Chennai")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1609592424109-dd9892f1b177?w=400")
                                .build());

                // Textbooks
                saveProduct(Product.builder()
                                .title("Java: The Complete Reference (12th Edition)")
                                .description("Herbert Schildt's masterpiece. Essential for computer science students.")
                                .price(new BigDecimal("550")).stock(10)
                                .seller(seller1).category(books).location("aits tirupati")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1587620962725-abab7fe55159?w=400")
                                .build());

                saveProduct(Product.builder()
                                .title("Discrete Mathematics — Kenneth Rosen")
                                .description("Used textbook for CSE/IT students. Minimal highlighting, perfect condition.")
                                .price(new BigDecimal("450")).stock(5)
                                .seller(seller3).category(books).location("Velachery, Chennai")
                                .type(ProductType.EXCHANGE).condition(ProductCondition.GOOD).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1543003919-a995d51555c9?w=400").build());

                // Food
                saveProduct(Product.builder()
                                .title("Fresh Homemade Samosas (Plate of 2)")
                                .description("Crispy, spicy potato samosas served with green chutney.")
                                .price(new BigDecimal("40")).stock(30)
                                .seller(seller2).category(food).location("T. Nagar, Chennai")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1601050633647-81a35d37c331?w=400")
                                .build());

                saveProduct(Product.builder()
                                .title("Veg Hyderabadi Biryani (Full)")
                                .description("Aromatic biryani with garden fresh vegetables and spices. Pre-order only.")
                                .price(new BigDecimal("220")).stock(10)
                                .seller(seller2).category(food).location("T. Nagar, Chennai")
                                .type(ProductType.PREORDER).condition(ProductCondition.NEW).isActive(true)
                                .preOrderDeadline(LocalDate.now().plusDays(1)).minPreOrderQty(1)
                                .imageUrl("https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=400")
                                .build());

                saveProduct(Product.builder()
                                .title("Summer Floral Print Dress")
                                .description("Lightweight and comfortable floral dress for summer campus outings.")
                                .price(new BigDecimal("850")).stock(20)
                                .seller(seller3).category(clothing).location("Anna Nagar, Chennai")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1572804013307-a9a7dfd68254?w=400")
                                .build());

                saveProduct(Product.builder()
                                .title("Campus Spirit Graphic T-Shirt")
                                .description("Casual cotton t-shirt with unique campus connect design.")
                                .price(new BigDecimal("450")).stock(50)
                                .seller(seller3).category(clothing).location("T. Nagar, Chennai")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400")
                                .build());

                // Stationery
                saveProduct(Product.builder()
                                .title("Mechanical Pencil Set (0.5mm)")
                                .description("Set of 2 professional mechanical pencils with lead refills.")
                                .price(new BigDecimal("150")).stock(50)
                                .seller(seller1).category(books).location("aits tirupati")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1513519245088-0e12902e5a38?w=400")
                                .build());

                saveProduct(Product.builder()
                                .title("Premium Journal / Notebook")
                                .description("Hardbound A5 notebook, 200 pages, grid paper. Ideal for notes.")
                                .price(new BigDecimal("250")).stock(20)
                                .seller(seller1).category(books).location("aits tirupati")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1531346878377-a5be20888e57?w=400")
                                .build());

                // ── Diversified Locations ───────────────────────────────────
                // Renigunta
                saveProduct(Product.builder()
                                .title("Scientific Calculator (FX-82MS)")
                                .description("Reliable Casio calculator for engineering students.")
                                .price(new BigDecimal("450")).stock(10)
                                .seller(seller1).category(electronics).location("renigunta")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1611532736597-de2d4265fba3?w=400")
                                .build());

                // MR Palli
                saveProduct(Product.builder()
                                .title("Inductive Proximity Sensor")
                                .description("Industrial grade sensor, used for college project once.")
                                .price(new BigDecimal("150")).stock(2)
                                .seller(seller3).category(studentHub).location("mr palli")
                                .type(ProductType.EXCHANGE).condition(ProductCondition.GOOD).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1518770660439-4636190af475?w=400")
                                .build());

                // Annamayya Circle
                saveProduct(Product.builder()
                                .title("Freshly Baked Cookies (Pack of 6)")
                                .description("Wholesome chocolate chip cookies, baked daily.")
                                .price(new BigDecimal("180")).stock(10)
                                .seller(seller2).category(food).location("annamayya cercle")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=400")
                                .build());

                // Out of State - Bangalore
                saveProduct(Product.builder()
                                .title("Original Apple Earpods (3.5mm)")
                                .description("Wired earphones with excellent sound quality. Shipping available from Bangalore.")
                                .price(new BigDecimal("1500")).stock(5)
                                .seller(seller1).category(electronics).location("Bangalore, KA")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1546435770-a3e426ff472b?w=400").build());

                // Out of State - Hyderabad
                saveProduct(Product.builder()
                                .title("Premium Laptop Stand - Adjustable")
                                .description("Ergonomic laptop stand for better posture. Shipping from Hyderabad.")
                                .price(new BigDecimal("1299")).stock(8)
                                .seller(seller1).category(electronics).location("Hyderabad, TS")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400")
                                .build());

                // Additional Clothing for diverse locations
                saveProduct(Product.builder()
                                .title("Designer Kurti & Dresses Set")
                                .description("Beautiful ethnic wear dresses, perfect for campus events. Cotton material.")
                                .price(new BigDecimal("1500")).stock(12)
                                .seller(seller3).category(clothing).location("Bangalore, KA")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1585487000160-6ebcfceb0d03?w=400")
                                .build());

                saveProduct(Product.builder()
                                .title("Casual Denim Jacket (Unisex)")
                                .description("Rugged denim jacket, stylish and durable. Available in all sizes.")
                                .price(new BigDecimal("999")).stock(10)
                                .seller(seller3).category(clothing).location("Hyderabad, TS")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1544642899-f0d6e5f6ed6f?w=400").build());

                saveProduct(Product.builder()
                                .title("Traditional Pattu Saree / Dress")
                                .description("Elegant traditional wear for festivals and special occasions.")
                                .price(new BigDecimal("2500")).stock(5)
                                .seller(seller3).category(clothing).location("aits tirupati")
                                .type(ProductType.NORMAL).condition(ProductCondition.NEW).isActive(true)
                                .imageUrl("https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=400")
                                .build());

                // ── Orders ──────────────────────────────────────────────────
                Order order1 = orderRepository.save(Order.builder()
                                .orderNumber("CCM-20240115001").user(customer1)
                                .totalAmount(p1.getPrice()).status(OrderStatus.DELIVERED)
                                .deliveryAddress("Adyar, Chennai").paymentMethod("UPI").build());

                orderItemRepository.save(OrderItem.builder()
                                .order(order1).product(p1).quantity(1).price(p1.getPrice()).build());

                orderRepository.save(Order.builder()
                                .orderNumber("CCM-20240115002").user(customer2)
                                .totalAmount(new BigDecimal("120")).status(OrderStatus.PENDING)
                                .deliveryAddress("Anna Nagar, Chennai").paymentMethod("CASH").build());

                // ── Wishlist ────────────────────────────────────────────────
                wishlistRepository.save(Wishlist.builder().user(customer1).product(p1).build());

                log.info("✅ Sample data seeded successfully!");
                log.info("📋 Test Accounts:");
                log.info("   Admin:    admin@campusconnect.com / Password@123");
                log.info("   Seller 1: priya@seller.com / Password@123");
                log.info("   Seller 2: ravi@seller.com / Password@123");
                log.info("   Customer: arjun@customer.com / Password@123");
        }

        private Category save(Category c) {
                return categoryRepository.findByName(c.getName())
                                .orElseGet(() -> categoryRepository.save(c));
        }

        private User saveUser(User u) {
                return userRepository.findByEmail(u.getEmail())
                                .orElseGet(() -> userRepository.save(u));
        }

        private Product saveProduct(Product p) {
                // Check if product with same title and seller already exists
                boolean exists = productRepository.findBySeller(p.getSeller()).stream()
                                .anyMatch(existing -> existing.getTitle().equalsIgnoreCase(p.getTitle()));
                if (exists) {
                        return productRepository.findByTitleContainingIgnoreCase(p.getTitle()).get(0);
                }
                return productRepository.save(p);
        }
}
