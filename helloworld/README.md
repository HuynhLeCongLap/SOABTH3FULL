# Product Management Service

## Mô tả
Dịch vụ quản lý sản phẩm (Product Management Service) - Bài thực hành số 3

## Kiến trúc SOA
- **Port**: 8081 (khác với Auth Service - 8080)
- **Database**: PRODUCT_DB (riêng biệt với SOA database)
- **Authentication**: Sử dụng JWT từ Auth Service

## API Endpoints

### 🔐 Yêu cầu Authentication
Tất cả endpoints đều yêu cầu JWT token trong header:
```
Authorization: Bearer <jwt-token>
```

### 📋 CRUD Operations

#### 1. GET /products
Lấy danh sách tất cả sản phẩm
```bash
curl -X GET http://localhost:8081/products \
  -H "Authorization: Bearer <token>"
```

#### 2. GET /products/{id}
Lấy thông tin chi tiết một sản phẩm
```bash
curl -X GET http://localhost:8081/products/1 \
  -H "Authorization: Bearer <token>"
```

#### 3. POST /products
Thêm một sản phẩm mới
```bash
curl -X POST http://localhost:8081/products \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Product",
    "description": "Product description",
    "price": 99.99,
    "quantity": 10
  }'
```

#### 4. PUT /products/{id}
Cập nhật thông tin sản phẩm
```bash
curl -X PUT http://localhost:8081/products/1 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Product",
    "price": 149.99,
    "quantity": 15
  }'
```

#### 5. DELETE /products/{id}
Xóa một sản phẩm
```bash
curl -X DELETE http://localhost:8081/products/1 \
  -H "Authorization: Bearer <token>"
```

### 🔍 Additional Endpoints

#### 6. GET /products/search?name=...
Tìm kiếm sản phẩm theo tên
```bash
curl -X GET "http://localhost:8081/products/search?name=iPhone" \
  -H "Authorization: Bearer <token>"
```

#### 7. GET /products/available
Lấy sản phẩm còn hàng (quantity > 0)
```bash
curl -X GET http://localhost:8081/products/available \
  -H "Authorization: Bearer <token>"
```

## Database Schema

### Bảng products
| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| id | INT (PRIMARY KEY) | ID duy nhất của sản phẩm |
| name | VARCHAR(255) | Tên sản phẩm |
| description | TEXT | Mô tả sản phẩm |
| price | DECIMAL(10,2) | Giá sản phẩm |
| quantity | INT | Số lượng sản phẩm trong kho |
| created_at | TIMESTAMP | Ngày sản phẩm được tạo |
| updated_at | TIMESTAMP | Ngày sản phẩm được cập nhật lần cuối |

## Cách chạy

### 1. Khởi động Auth Service (port 8080)
```bash
cd ../helloworld
./gradlew bootRun
```

### 2. Lấy JWT token
```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username": "lap", "password": "123"}'
```

### 3. Khởi động Product Service (port 8081)
```bash
cd ../product-service
./gradlew bootRun
```

### 4. Test Product APIs
```bash
# Lấy danh sách sản phẩm
curl -X GET http://localhost:8081/products \
  -H "Authorization: Bearer <token>"
```

## Dữ liệu mẫu
Service đã có sẵn 5 sản phẩm mẫu:
- iPhone 15 - $999.99
- Samsung Galaxy S24 - $899.99
- MacBook Pro - $1999.99
- Dell XPS 13 - $1299.99
- iPad Air - $599.99

.\gradlew clean build
.\gradlew bootRun
.\gradlew.bat bootRun