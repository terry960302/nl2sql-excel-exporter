# Microservice 코드 컨벤션

## 0. 아키텍처 가이드라인

### ✅ Gateway 기반 인증/인가 처리

#### 1. Gateway에서 처리되는 부분
- JWT 토큰 검증
- 사용자 인증
- 권한 검사
- 요청 헤더 검증
- API 키 검증

#### 2. 마이크로서비스에서 처리하지 않아도 되는 부분
```java
// ❌ 마이크로서비스에서 불필요한 인증 처리
@RestController
public class SchemaController {
    @PostMapping("/schemas")
    public ResponseEntity<SchemaResponse> createSchema(
        @RequestHeader("Authorization") String token,  // 불필요
        @RequestBody SchemaCreateRequest request
    ) {
        // 토큰 검증 로직 불필요
        // 사용자 인증 로직 불필요
        return ResponseEntity.ok(schemaService.createSchema(request));
    }
}

// ✅ Gateway에서 인증 처리 후 전달되는 요청
@RestController
public class SchemaController {
    @PostMapping("/schemas")
    public ResponseEntity<SchemaResponse> createSchema(
        @RequestBody SchemaCreateRequest request
    ) {
        // 순수하게 비즈니스 로직만 처리
        return ResponseEntity.ok(schemaService.createSchema(request));
    }
}
```

#### 3. Gateway에서 전달되는 정보 활용
```java
@RestController
public class SchemaController {
    @PostMapping("/schemas")
    public ResponseEntity<SchemaResponse> createSchema(
        @RequestHeader("X-User-Id") UUID userId,  // Gateway에서 검증된 사용자 ID
        @RequestHeader("X-Organization-Id") UUID orgId,  // Gateway에서 검증된 조직 ID
        @RequestBody SchemaCreateRequest request
    ) {
        // Gateway에서 검증된 정보를 신뢰하고 사용
        return ResponseEntity.ok(schemaService.createSchema(userId, orgId, request));
    }
}
```

### ✅ 실무 적용 체크리스트

#### 기본 체크리스트
- [ ] 마이크로서비스에서 직접적인 인증/인가 로직 제거
- [ ] Gateway에서 전달되는 헤더 정보 활용
- [ ] 순수 비즈니스 로직에만 집중
- [ ] 불필요한 보안 관련 의존성 제거

#### 주의사항
- Gateway에서 전달되는 헤더 정보는 이미 검증된 것으로 간주
- 마이크로서비스는 순수하게 비즈니스 로직에만 집중
- 보안 관련 로직은 Gateway에 위임


## 1. 패키지 구조

```
com.pandaterry.{service_name}
├── application
│   ├── dto
│   └── service
├── domain
│   ├── entity
│   ├── enums
│   ├── exception
│   └── repository
├── infrastructure
│   ├── repository
│   └── util
└── presentation
    ├── controller
    ├── dto
    └── exception
```

## 2. 네이밍 컨벤션

### 2.1 클래스 네이밍

- Entity: PascalCase

  ```java
  public class UserProfile { }
  public class OrderItem { }
  ```

- DTO: PascalCase + 목적

  ```java
  public class UserProfileResponse { }
  public class OrderItemCreateRequest { }
  ```

- Controller: PascalCase + Controller

  ```java
  public class UserController { }
  public class OrderController { }
  ```

- Service: PascalCase + Service

  ```java
  public class UserService { }
  public class OrderService { }
  ```

- Repository: PascalCase + Repository

  ```java
  public interface UserRepository { }
  public interface OrderRepository { }
  ```

- Exception: PascalCase + Exception
  ```java
  public class UserException extends RuntimeException { }
  public class OrderException extends RuntimeException { }
  ```

### 2.2 메서드 네이밍

- 생성: create + EntityName

  ```java
  public UserResponse createUser(UserCreateRequest request) { }
  public OrderResponse createOrder(OrderCreateRequest request) { }
  ```

- 조회: get + EntityName

  ```java
  public UserResponse getUser(UUID userId) { }
  public List<OrderResponse> getOrdersByUser(UUID userId) { }
  ```

- 수정: update + EntityName

  ```java
  public UserResponse updateUser(UUID userId, UserUpdateRequest request) { }
  public OrderResponse updateOrder(UUID orderId, OrderUpdateRequest request) { }
  ```

- 삭제: delete + EntityName

  ```java
  public void deleteUser(UUID userId) { }
  public void deleteOrder(UUID orderId) { }
  ```

- 비활성화: deactivate + EntityName (상태 변경이 필요한 경우)
  ```java
  public void deactivateUser(UUID userId) { }
  public void deactivateOrder(UUID orderId) { }
  ```

### 2.3 변수 네이밍

- ID: camelCase + Id

  ```java
  private UUID userId;
  private UUID orderId;
  ```

- 상태: is + PascalCase

  ```java
  private boolean isEnabled;
  private boolean isDeleted;
  ```

- 시간: camelCase + At
  ```java
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  ```

## 3. 코드 스타일

### 3.1 어노테이션

- Entity

  ```java
  @Entity
  @Table(name = "users")
  @Getter
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  public class User {
      @Id
      @GeneratedValue(strategy = GenerationType.UUID)
      private UUID id;
  }
  ```

- Controller

  ```java
  @RestController
  @RequestMapping("/api/v1/users")
  @RequiredArgsConstructor
  public class UserController { }
  ```

- Service

  ```java
  @Service
  @Transactional
  @RequiredArgsConstructor
  public class UserService {
      private final UserRepository userRepository;

      @Transactional(readOnly = true)
      public UserResponse getUser(UUID userId) {
          return userRepository.findById(userId)
              .map(UserResponse::of)
              .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
      }

      @Transactional(readOnly = true)
      public List<UserResponse> getUsers() {
          return userRepository.findAll().stream()
              .map(UserResponse::of)
              .toList();
      }

      public UserResponse createUser(UserCreateRequest request) {
          User user = User.create(request.getName());
          return UserResponse.of(userRepository.save(user));
      }
  }
  ```

- Repository

  ```java
  @Repository
  public interface UserRepository extends JpaRepository<User, UUID> { }
  ```

- DTO

  ```java
  @Getter
  @Builder
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public class UserResponse {
      private final UUID id;
      private final String name;
      private final String email;

      public static UserResponse of(User user) {
          return UserResponse.builder()
              .id(user.getId())
              .name(user.getName())
              .email(user.getEmail())
              .build();
      }
  }

  @Getter
  @Builder
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public class UserCreateRequest {
      private final String name;
      private final String email;

      public User toEntity() {
          return User.create(name, email);
      }
  }
  ```

### 3.2 메서드 구조

- DTO는 빌더 패턴을 사용하여 생성

  ```java
  // 잘못된 예시 - 생성자 사용
  UserResponse response = new UserResponse(user.getId(), user.getName()); // X

  // 올바른 예시 - 빌더 패턴 사용
  UserResponse response = UserResponse.builder()
      .id(user.getId())
      .name(user.getName())
      .build(); // O
  ```

- DTO와 엔티티 간 변환은 DTO에서 담당

  ```java
  // 잘못된 예시 - 서비스에서 변환
  public UserResponse createUser(UserCreateRequest request) {
      User user = new User(request.getName(), request.getEmail()); // X
      return new UserResponse(user.getId(), user.getName()); // X
  }

  // 올바른 예시 - DTO에서 변환
  public UserResponse createUser(UserCreateRequest request) {
      User user = request.toEntity();
      return UserResponse.of(userRepository.save(user)); // O
  }
  ```

### 3.3 예외 처리

- 서비스별 예외 클래스 정의

  ```java
  public class UserException extends RuntimeException {
      private final ErrorCode errorCode;

      public UserException(ErrorCode errorCode) {
          super(errorCode.getMessage());
          this.errorCode = errorCode;
      }
  }
  ```

- ErrorCode는 enum으로 관리

  ```java
  public enum ErrorCode {
      USER_NOT_FOUND(404, "U001", "User not found"),
      INVALID_INPUT(400, "U002", "Invalid input");

      private final int status;
      private final String code;
      private final String message;
  }
  ```

- GlobalExceptionHandler에서 처리
  ```java
  @RestControllerAdvice
  public class GlobalExceptionHandler {
      @ExceptionHandler(UserException.class)
      public ResponseEntity<ErrorResponse> handleUserException(UserException ex) {
          ErrorCode errorCode = ex.getErrorCode();
          return new ResponseEntity<>(
              ErrorResponse.of(errorCode.getCode(), errorCode.getMessage()),
              HttpStatus.valueOf(errorCode.getStatus())
          );
      }
  }
  ```

## 4. 테스트 코드 컨벤션

### 4.1 단위 테스트

- 서비스 로직 테스트는 Mockito 사용

  ```java
  @ExtendWith(MockitoExtension.class)
  class UserServiceTest {
      @Mock
      private UserRepository userRepository;

      @InjectMocks
      private UserService userService;

      @Test
      void createUser_성공() {
          // given
          UserCreateRequest request = new UserCreateRequest("test");
          User user = User.create("test");
          when(userRepository.save(any())).thenReturn(user);

          // when
          UserResponse response = userService.createUser(request);

          // then
          assertThat(response.getName()).isEqualTo("test");
      }
  }
  ```

### 4.2 통합 테스트

- 실제 데이터베이스 사용
- TestDataFactory로 테스트 데이터 생성
- MockitoBean 사용 (MockBean은 deprecated)

  ```java
  @SpringBootTest
  class UserIntegrationTest {
      @MockitoBean  // @MockBean 대신 @MockitoBean 사용
      private ExternalService externalService;

      @Autowired
      private UserRepository userRepository;

      @Autowired
      private UserTestDataFactory userTestDataFactory;

      @Test
      void createUser_성공() {
          // given
          UserCreateRequest request = new UserCreateRequest("test");
          when(externalService.validate(any())).thenReturn(true);

          // when
          UserResponse response = userService.createUser(request);

          // then
          assertThat(response.getName()).isEqualTo("test");
      }
  }
  ```

## 5. API 엔드포인트 컨벤션

### 5.1 URL 패턴

- 버전 관리: `/api/v1/{resource}`
- 리소스는 복수형 사용

  ```java
  @RestController
  @RequestMapping("/api/v1/users")
  public class UserController {
      @PostMapping
      public ResponseEntity<UserResponse> createUser(...) { }

      @GetMapping("/{userId}")
      public ResponseEntity<UserResponse> getUser(...) { }
  }
  ```

### 5.2 응답 형식

- ResponseEntity로 감싸서 반환
- HTTP 상태 코드 적절히 사용
  ```java
  @GetMapping("/{userId}")
  public ResponseEntity<UserResponse> getUser(@PathVariable UUID userId) {
      return ResponseEntity.ok(userService.getUser(userId));
  }
  ```

## 6. 보안 컨벤션

### 6.1 헤더

- 커스텀 헤더는 X- 접두사 사용
  ```java
  @RequestHeader("X-Organization-Id") UUID orgId
  @RequestHeader("X-User-Id") UUID userId
  ```

### 6.2 암호화

- 민감 정보는 암호화하여 저장
  ```java
  @Component
  public class EncryptionUtil {
      public String encrypt(String value) { }
      public String decrypt(String value) { }
  }
  ```

## 7. 데이터베이스 컨벤션

### 7.1 컬럼 네이밍

- 스네이크 케이스 사용
  ```sql
  CREATE TABLE users (
      id UUID PRIMARY KEY,
      user_name VARCHAR(255),
      is_enabled BOOLEAN,
      created_at TIMESTAMP,
      updated_at TIMESTAMP
  );
  ```

### 7.2 인덱스

- ID: Primary Key
- 외래키: Index
- 상태: Index
  ```sql
  CREATE INDEX idx_users_org_id ON users(org_id);
  CREATE INDEX idx_users_is_enabled ON users(is_enabled);
  ```
 

## 8. 객체지향설(OOP) 가이드라인

# 대규모 기업 실무를 위한 자바/스프링 OOP 설계 가이드라인

## **적용 전략 및 우선순위**

대규모 기업에서는 점진적 도입과 팀 역량을 고려한 단계별 접근이 중요합니다[1].

**1단계: 기본 OOP 원칙 적용 (모든 프로젝트)**
- Static Factory Method 도입
- 기본적인 도메인 로직 캡슐화
- 명확한 레이어 분리

**2단계: Rich Domain Model (비즈니스 복잡도 증가시)**
- 도메인 객체에 행위 부여
- Command/Query 객체 도입
- 도메인 서비스 분리

**3단계: 이벤트 기반 설계 (마이크로서비스/대규모 시스템)**
- 도메인 이벤트 발행
- 비동기 처리
- CQRS 패턴 적용

## **핵심 설계 원칙**

### **SOLID 원칙 적용**

**단일 책임 원칙 (SRP)**[2][9]
```java
// ❌ 나쁜 예: 여러 책임을 가진 클래스
public class UserService {
    public void createUser(User user) { }
    public void sendEmail(String email) { }
    public void generateReport() { }
}

// ✅ 좋은 예: 책임 분리
@Service
public class UserService {
    public void createUser(User user) { }
}

@Service
public class EmailService {
    public void sendEmail(String email) { }
}

@Service
public class ReportService {
    public void generateReport() { }
}
```

**개방-폐쇄 원칙 (OCP)**[2][9]
```java
// ✅ 확장에는 열려있고 변경에는 닫혀있는 설계
public interface PaymentProcessor {
    void processPayment(Payment payment);
}

@Component
public class CreditCardProcessor implements PaymentProcessor {
    @Override
    public void processPayment(Payment payment) { }
}

@Component
public class PayPalProcessor implements PaymentProcessor {
    @Override
    public void processPayment(Payment payment) { }
}
```

### **의존성 주입 활용**[2][3]

**생성자 주입 선호**[3]
```java
@Service
@RequiredArgsConstructor
public class OrderService {
    private final PaymentProcessor paymentProcessor;
    private final InventoryService inventoryService;
    private final NotificationService notificationService;
}
```

## **계층별 설계 가이드라인**

### **1. 도메인 계층 (Domain Layer)**

**Rich Domain Model 적용 기준**[1]
- 비즈니스 규칙이 3개 이상인 경우
- 상태 변경 로직이 복잡한 경우
- 도메인 전문가와의 소통이 중요한 경우

```java
public class Order {
    private OrderId id;
    private CustomerId customerId;
    private List items;
    private OrderStatus status;
    private Money totalAmount;

    public static Order create(CustomerId customerId, List items) {
        validateItems(items);
        Order order = new Order(customerId, items);
        order.calculateTotal();
        order.registerEvent(new OrderCreatedEvent(order.getId()));
        return order;
    }

    public void cancel(String reason) {
        if (!canBeCancelled()) {
            throw new OrderException("주문을 취소할 수 없습니다");
        }
        this.status = OrderStatus.CANCELLED;
        registerEvent(new OrderCancelledEvent(this.id, reason));
    }

    private boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }
}
```

**도메인 서비스 적용 기준**[1]
- 여러 Aggregate 간 협력이 필요한 경우
- 외부 시스템과의 연동이 필요한 경우
- 복잡한 계산 로직이 포함된 경우

```java
@DomainService
public class OrderPricingService {
    
    public Money calculateDiscount(Order order, Customer customer) {
        // 여러 도메인 객체를 활용한 복잡한 할인 계산
        DiscountPolicy policy = customer.getDiscountPolicy();
        return policy.calculateDiscount(order.getTotalAmount());
    }
}
```

### **2. 애플리케이션 계층 (Application Layer)**

**Facade 패턴 적용**[1][7]
```java
@Component
@RequiredArgsConstructor
public class OrderProcessingFacade {
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    private final NotificationService notificationService;

    @Transactional
    public OrderResponse processOrder(CreateOrderCommand command) {
        // 1. 재고 확인
        inventoryService.checkAvailability(command.getItems());
        
        // 2. 주문 생성
        Order order = orderService.createOrder(command);
        
        // 3. 결제 처리
        Payment payment = paymentService.processPayment(
            PaymentRequest.from(order)
        );
        
        // 4. 주문 확정
        order.confirm(payment);
        
        return OrderResponse.from(order);
    }
}
```

**Command/Query 객체 활용**[1]
```java
// Command 객체
public record CreateOrderCommand(
    UUID customerId,
    List items,
    ShippingAddress shippingAddress,
    PaymentMethod paymentMethod
) {
    public CreateOrderCommand {
        validate(customerId, items, shippingAddress);
    }
}

// Query 객체
public record OrderSearchQuery(
    UUID customerId,
    OrderStatus status,
    LocalDate startDate,
    LocalDate endDate,
    Pageable pageable
) { }
```

### **3. 인프라스트럭처 계층**

**Repository 인터페이스 분리**[4][7]
```java
// 도메인 계층에 정의
public interface OrderRepository {
    Order save(Order order);
    Optional findById(OrderId id);
    List findByCustomerId(CustomerId customerId);
}

// 인프라스트럭처 계층에 구현
@Repository
@RequiredArgsConstructor
public class JpaOrderRepository implements OrderRepository {
    private final OrderJpaRepository jpaRepository;
    private final OrderMapper mapper;

    @Override
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
```

## **이벤트 기반 설계**

### **도메인 이벤트 적용 기준**[1]

- 상태 변경 후 2개 이상의 독립적인 후속 작업이 필요한 경우
- 다른 Bounded Context와의 연동이 필요한 경우
- 감사 로그나 알림 등 부가 기능이 필요한 경우

```java
@Entity
public class User {
    @DomainEvents
    public List domainEvents() {
        return this.events;
    }

    @AfterDomainEventPublication
    public void clearEvents() {
        this.events.clear();
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
        this.events.add(new UserActivatedEvent(this.id));
    }
}

@EventHandler
@Component
@RequiredArgsConstructor
public class UserEventHandler {
    private final EmailService emailService;
    private final AuditService auditService;

    @Async
    @EventListener
    public void handle(UserActivatedEvent event) {
        emailService.sendWelcomeEmail(event.getUserId());
        auditService.logUserActivation(event);
    }
}
```

## **성능 고려사항**

### **N+1 문제 해결**[3]
```java
@Repository
public class OrderRepositoryImpl implements OrderRepository {
    
    @Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.customerId = :customerId")
    List findByCustomerIdWithItems(@Param("customerId") UUID customerId);
}
```

### **비동기 처리**[3]
```java
@Service
public class NotificationService {
    
    @Async("taskExecutor")
    public CompletableFuture sendOrderConfirmation(Order order) {
        // 비동기 알림 처리
        return CompletableFuture.completedFuture(null);
    }
}
```

## **테스트 전략**

### **계층별 테스트**[6][10]
```java
// 도메인 로직 테스트
@Test
void 주문_취소_시_상태가_변경된다() {
    // Given
    Order order = Order.create(customerId, items);
    
    // When
    order.cancel("고객 요청");
    
    // Then
    assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
}

// 애플리케이션 서비스 테스트
@SpringBootTest
@Transactional
class OrderServiceTest {
    
    @Test
    void 주문_생성_시_이벤트가_발행된다() {
        // Given & When
        Order order = orderService.createOrder(command);
        
        // Then
        verify(eventPublisher).publishEvent(any(OrderCreatedEvent.class));
    }
}
```

## **실무 적용 체크리스트**

### **기본 체크리스트**
- [ ] 각 클래스가 단일 책임을 가지는가?[2][4]
- [ ] 인터페이스를 통한 의존성 역전이 적용되었는가?[2][3]
- [ ] 도메인 로직이 적절한 계층에 위치하는가?[1][7]
- [ ] 예외 처리가 계층별로 적절히 분리되었는가?[6][10]

### **고급 체크리스트**
- [ ] 도메인 이벤트가 필요한 곳에 적용되었는가?[1]
- [ ] 비동기 처리가 적절히 활용되었는가?[3]
- [ ] 성능 이슈(N+1, 메모리 사용량 등)가 고려되었는가?[3]
- [ ] 테스트 커버리지가 충분한가?[6][10]

## **팀 적용 가이드**

### **점진적 도입 전략**
1. **기존 코드 리팩토링**: Anemic Model → Rich Model
2. **새 기능 개발**: OOP 원칙 적용
3. **복잡한 도메인**: Domain Event 도입
4. **마이크로서비스**: CQRS 패턴 적용

### **코드 리뷰 체크포인트**[6][10]
- 도메인 로직이 Service가 아닌 Domain 객체에 있는지
- 의존성 방향이 올바른지 (외부 → 내부)
- 네이밍이 도메인 용어를 반영하는지
- 테스트 코드가 비즈니스 시나리오를 잘 표현하는지

