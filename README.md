# distributed-marketplace-platform
Inspired by Amazon, this is a backend e-commerce marketplace, where buyers browse products, sellers manage listings, and orders are processed through an event-driven architecture.

Developed by Phuong Khanh Tran (Jade Tran)

## Tech Stacks
- Java: primary programming language
- Spring Boot: backend framework to build application, manage dependencies, and system components
- Spring Security: provides JWT-based authentication, authorization, and role-based access control
- GraphQL: API layer to allow clients query and mutate data
- Kafka: event streaming platform for asynchronous order processing, inventory updates, and notifications
- Redis: in-memory cache used to accelerate product lookups, dashboards, and frequently accessed data
- MySQL: relational database for marketplace data
- AWS S3: object storage service to store and serve produc images and other uploaded files
- Docker: containerization platform to package and run application services
- Kubernetes: containerization platform to deploy, scale, and manage distributed services
- Mockito: testing framework

## Features
### Authentication
- User registration and login
- JWT-based authentication
- Role based access: buyer, seller, admin

### Product Catalog
- Create/edit/delete products
- Product categorires
- Product search/filetring
- Product detail page
- Product images stored in AWS S3
- Redis caching for product detail pages

### Seller Feaures
- Seller storefront/profile
- Manage product listings
- Manage inventory quantity
- View seller orders

### Buyer Features
- Browse products
- Add/remove cart items
- Checkout
- View order history
- Leave product reviews

### Inventory & Order Processing
- Prevent overselling during concurrent checkout
- Reverse inventory when order is placed
- Track order status: PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED

### Kafka Events 
- Order placement
- Order confirmation
- Order cancellation
- Inventory reversal
- Failed inventory reversal
- Payment process
- Product update
- Review addition

### Notification Workflow
- Kafka consumer creates order notifications
- Buyer receives order updates
- Seller receives new order alerts

### GraphQL API
#### Queries
- Product detail
- Product search
- Buyer cart
- Order history
- Seller dashboard

#### Mutations
- Register/login
- Create product
- Upload product image
- Add to cart
- Place order
- Update inventory
- Add review

### Testing
- OrderServiceTest
- InventoryServiceTest
- ConcurrentCheckoutTest
- ProductServiceTest
- AuthServiceTest
- OrderFlowIntegrationTest
- KafkaIntegrationTest
- GraphQLIntegrationTest

## Run Instructions
### Create .env file at root
```
MYSQL_DATABASE=marketplace
MYSQL_USER=marketplace_user
MYSQL_PASSWORD=marketplace_password

JWT_SECRET=your_local_secret

AWS_ACCESS_KEY_ID=your_key
AWS_SECRET_ACCESS_KEY=your_secret
AWS_REGION=us-east-2
AWS_S3_BUCKET=your_bucket_name
```

### Start dependencies: MySQL, Redis, Kafka, Zookeeper/Raft
```
docker compose up -d
```

### Run Spring Boot app
```
./mvnw spring-boot:run
```

### Open GraphQL playground
```
http://localhost:8080/graphql
```

### Run test
```
./mvnw test
```

### Run with Docker
```
docker compose up --build
```

### Deploy locally with Kubernetes
```
kubectl apply -f k8s/
```