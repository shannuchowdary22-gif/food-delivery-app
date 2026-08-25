# API Documentation

Base URL: `http://localhost:8080/api`. All endpoints except authentication require `Authorization: Bearer <jwt>`.

## Authentication

- `POST /auth/request-otp` accepts `{ "mobile": "1234567890" }`; returns `{ "message": "OTP sent successfully", "expiresIn": 30, "otp" }` in development. The `otp` field is omitted when `EXPOSE_OTP=false`.
- `POST /auth/verify-otp` accepts `{ "mobile", "otp" }`; returns `{ "token", "user": { "id", "name", "mobile", "address", "coins" } }`.
- `POST /auth/logout` returns `{ "message": "Logged out" }`.

## Catalog and profile

- `GET /restaurants?category=&instantOnly=` returns `{ "restaurants": [...] }`.
- `GET /dishes?q=&category=&restaurantId=&vegOnly=&instantOnly=&sort=` returns `{ "dishes": [...] }`.
- `GET /coupons` returns `{ "coupons": [{ "code", "description" }] }`.
- `GET /users/me` returns `{ "user": ... }`.
- `PATCH /users/me` accepts `{ "name", "address" }` and returns the updated user.
- `GET /locations/reverse?latitude=&longitude=` and `GET /locations/search?q=` return a delivery address object.

## Orders and assistant

- `POST /orders` accepts `{ "items": [{ "dishId", "quantity" }], "couponCode", "paymentMethod", "splitCount", "deliveryAddress": { "label", "latitude", "longitude" } }` and returns `{ "order": ... }`.
- `GET /orders` returns `{ "orders": [...] }` for the authenticated user.
- `GET /orders/{orderId}/tracking` returns status, ETA, driver, origin, and destination.
- `POST /assistant/messages` accepts `{ "message", "orderId" }` and returns `{ "message", "dishes", "actions" }`.

Validation failures return `{ "success": false, "message": "Validation failed", "errors": { ... } }`; other client failures return the same success/message shape without internal details.