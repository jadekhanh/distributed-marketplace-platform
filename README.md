# distributed-marketplace-platform
Inspired by Amazon, this is a backend e-commerce marketplace, where buyers browse products, sellers manage listings, and orders are processed through an event-driven architecture.

Developed by Phuong Khanh Tran (Jade Tran)

## Tech Stack
- Java: main programming language
- Spring Boot: backend framework for application services and dependency management
- Spring Security: JWT-based authentication, authorization, and role-based access control
- GraphQL: API layer for marketplace mutations and queries
- Kafka: event streaming for order, payment, inventory, and review workflows
- Redis: in-memory cache used to accelerate product and cart lookups
- MySQL: relational database for users, products, carts, cart items, orders, order items, payments, reviews, inventories, seller profiles, and notifications
- AWS S3: object storage for product images
- Docker: containerized local development environment
- Kubernetes: local deployment manifests for distributed services
- Flyway: database schema migrations
- JUnit/Spring Boot Test: integration and end-to-end testing framework

## Features
### Authentication
- User registration and login
- JWT-based authentication
- Role-based access: BUYER, SELLER, ADMIN
- Only ADMIN and SELLER have access to modifying products and categories

### Product
- Create, update, and delete products
- Query product by id, seller, category, and keyword
- Product images upload using AWS S3
- Product image URLs stored in MySQL
- Redis caching for product and cart lookups

### Seller Profile
- Create, update, and delete seller/storefront profiles
- Query seller profile by ID and current user
- Manage product listings
- Upload product images

### Buyer
- Browse products
- Add, update, remove cart items
- Place orders from cart
- View order history
- Leave, edit, and delete product reviews

### Category
- Create, update, delete product category
- Query all categories or category by ID

### Inventory
- Product inventory stores product available quantity and reserved quantity
- Prevent overselling during concurrent checkout
- Reserve inventory during order placement
- Release inventory reservation when payment fails

### Order
- Place and cancel an order
- Query an order by ID and view order history
- Order status: PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED

### Payment
- Simulated payment processing and refund for an order
- Query payment by ID or order ID

### Review
- Create, update, and delete product review
- Query a review by ID
- Query all reviews belonging to a product from most recent orders

### Notification
- Mark one notification as read
- Mark all unread notifications as read
- Query all notifications by user ID
- Query all unread notifications by user ID

### Kafka Events 
- ORDER_PLACED
- ORDER_CANCELLED
- PAYMENT_PROCESSED
- PAYMENT_FAILED
- INVENTORY_RESERVED
- INVENTORY_RESERVATION_FAILED
- REVIEW_CREATED

### AWS S3 Image Upload
- Product images are uploaded to AWS S3
- Product image URLs are stored in MySQL database

### Testing
#### Integration
- ConcurrentIntegrationCheckoutTest
- MarketplaceFlowIntegrationTest
- GraphQLIntegrationTest
- S3IntegrationTest
- AuthenticationIntegrationTest
- ImageUploadIntegrationTest
- KafkaIntegrationTest

#### End-to-end
- AuthenticationE2ETest
- MarketplaceE2ETest

## Run Instructions
### Create .env file at root
```
APP_NAME=jade-and-plushies-gang-marketplace
SERVER_PORT=8080

MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=jade-and-plushies-gang-marketplace
MYSQL_USER=root
MYSQL_PASSWORD=root
MYSQL_HOST=localhost
MYSQL_PORT=3306

# Redis
REDIS_HOST=localhost
REDIS_PORT=6380

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# JWT
JWT_SECRET=your_local_secret
JWT_EXPIRATION_MS=86400000

# AWS S3
AWS_ACCESS_KEY_ID=your_key
AWS_SECRET_ACCESS_KEY=your_secret
AWS_REGION=us-east-2
AWS_S3_BUCKET=your_bucket_name
```

### Configure AWS Credentials
```
aws configure
```

### Start dependencies: MySQL, Redis, Kafka
```
docker compose up -d
```

### Create MySQL database
```
docker compose exec mysql mysql -u root -p
```

Then inside MySQL:
```
CREATE DATABASE `jade-and-plushies-gang-marketplace`;
exit;
```

If resetting the local database:
```
DROP DATABASE `jade-and-plushies-gang-marketplace`; 
CREATE DATABASE `jade-and-plushies-gang-marketplace`; 
exit;
```

### Run Spring Boot app
```
mvn spring-boot:run
```
Flyway will create database tables from migration files

### Open GraphiQL playground in web browser
```
http://localhost:8080/graphiql
```

The actual GraphQL API endpoint is:
http://localhost:8080/graphql

### Health check on web browser
```
http://localhost:8080/health
```

### Run tests
```
mvn test
```

### Run S3 tests
S3 tests are disabled by default because they require real AWS credentials. To run them, temporarily remove @Disabled from S3IntegrationTest and ImageUploadIntegrationTest, then run:
```
mvn test -Dtest=S3IntegrationTest
mvn test -Dtest=ImageUploadIntegrationTest
```

### Stop local dependencies
```
docker compose down
```

### Deploy locally with Kubernetes
Make sure Kubernetes cluster is running first
```
kubectl config use-context docker-desktop 
kubectl get nodes
kubectl apply -f k8s/
```