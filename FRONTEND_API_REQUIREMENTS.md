# Zomiggy Frontend API Requirements

The current frontend uses local mock data and browser storage. These endpoints are the future Spring Boot contract needed to replace the mock services.

## Authentication

### POST `/api/auth/request-otp`
Purpose: Request a login OTP for a mobile number.
Authentication: Public.
Request: `{ "mobile": "1234567890" }`
Response: `{ "message": "OTP sent", "expiresIn": 60 }`
Possible Errors: `400` invalid mobile, `429` too many requests, `503` SMS provider unavailable.

### POST `/api/auth/verify-otp`
Purpose: Verify an OTP and create a JWT session.
Authentication: Public.
Request: `{ "mobile": "1234567890", "otp": "123456" }`
Response: `{ "token": "jwt", "user": { "id": "user-1", "name": "Foodie User", "mobile": "1234567890" } }`
Possible Errors: `400` invalid or expired OTP, `429` too many attempts.

### POST `/api/auth/logout`
Purpose: Invalidate the current refresh/session token.
Authentication: Bearer JWT.
Request: `{}`
Response: `{ "message": "Logged out" }`
Possible Errors: `401` unauthorized.

## Discovery

### GET `/api/restaurants`
Purpose: Return restaurants available near the user's location.
Authentication: Bearer JWT.
Request: Query parameters `latitude`, `longitude`, optional `category`, `instantOnly`.
Response: `{ "restaurants": [{ "id": 101, "name": "Domino's Pizza", "rating": 4.5, "time": "15-25 min", "cuisine": "Pizzas, Fast Food, Italian", "imageUrl": "...", "offer": "50% OFF up to ₹100" }] }`
Possible Errors: `401` unauthorized, `422` invalid coordinates, `503` discovery unavailable.

### GET `/api/dishes`
Purpose: Search and filter dishes.
Authentication: Bearer JWT.
Request: Query parameters `q`, `category`, `restaurantId`, `vegOnly`, `instantOnly`, `sort`.
Response: `{ "dishes": [{ "id": 1, "restaurantId": 101, "name": "Margherita Veg Pizza", "price": 299, "rating": 4.5, "time": "15-25 min", "veg": true, "description": "...", "imageUrl": "..." }] }`
Possible Errors: `401` unauthorized, `422` invalid filters.

### GET `/api/locations/reverse`
Purpose: Convert GPS coordinates into a readable delivery location.
Authentication: Bearer JWT.
Request: Query parameters `latitude`, `longitude`.
Response: `{ "label": "Jubilee Hills, Hyderabad", "latitude": 17.4399, "longitude": 78.4983 }`
Possible Errors: `422` invalid coordinates, `503` geocoding unavailable.

### GET `/api/locations/search`
Purpose: Resolve a manually entered delivery address.
Authentication: Bearer JWT.
Request: Query parameter `q`.
Response: `{ "label": "Jubilee Hills, Hyderabad", "latitude": 17.4399, "longitude": 78.4983 }`
Possible Errors: `404` address not found, `503` geocoding unavailable.

## Orders

### POST `/api/orders`
Purpose: Place an order after payment verification.
Authentication: Bearer JWT.
Request: `{ "items": [{ "dishId": 1, "quantity": 2 }], "couponCode": "ZOMNEW", "paymentMethod": "UPI", "splitCount": 1, "deliveryAddress": { "label": "...", "latitude": 17.4399, "longitude": 78.4983 } }`
Response: `{ "order": { "id": "ODR123456", "status": "Processing", "total": 523, "createdAt": "2026-08-19T12:00:00Z" } }`
Possible Errors: `400` invalid cart/payment, `401` unauthorized, `409` item unavailable, `422` coupon or address invalid.

### GET `/api/orders`
Purpose: Return the current user's order and payment history.
Authentication: Bearer JWT.
Request: Optional query parameter `status`.
Response: `{ "orders": [{ "id": "ODR123456", "items": "...", "method": "UPI", "total": 523, "status": "Processing", "createdAt": "..." }] }`
Possible Errors: `401` unauthorized, `503` order history unavailable.

### GET `/api/orders/{orderId}/tracking`
Purpose: Return live delivery status, ETA, driver, and route coordinates.
Authentication: Bearer JWT.
Request: Path parameter `orderId`.
Response: `{ "orderId": "ODR123456", "status": "On The Way", "etaMinutes": 12, "distanceKm": 0.8, "driver": { "name": "Ramesh Kumar", "phone": "...", "vehicle": "AP09 BX 1234", "rating": 4.8 }, "origin": { "latitude": 17.44, "longitude": 78.48 }, "destination": { "latitude": 17.4399, "longitude": 78.4983 } }`
Possible Errors: `401` unauthorized, `404` order not found, `409` tracking unavailable.

## User, coupons, notifications, and assistant

### GET/PATCH `/api/users/me`
Purpose: Load or save the user's name and delivery address.
Authentication: Bearer JWT.
Request: PATCH body `{ "name": "Foodie User", "address": "Jubilee Hills, Hyderabad" }`.
Response: `{ "user": { "name": "Foodie User", "mobile": "1234567890", "address": "Jubilee Hills, Hyderabad", "coins": 50 } }`.
Possible Errors: `400` invalid profile, `401` unauthorized.

### GET `/api/coupons`
Purpose: Return coupons currently available to the user.
Authentication: Bearer JWT.
Request: Optional query parameter `subtotal`.
Response: `{ "coupons": [{ "code": "ZOMNEW", "description": "₹75 OFF on orders above ₹199" }] }`
Possible Errors: `401` unauthorized, `503` coupon service unavailable.

### GET/PATCH `/api/notifications`
Purpose: List notifications and mark them read.
Authentication: Bearer JWT.
Request: PATCH body `{ "read": true }`.
Response: `{ "notifications": [{ "id": "n1", "title": "New Coupon Added!", "message": "...", "read": false }] }`.
Possible Errors: `401` unauthorized.

### POST `/api/assistant/messages`
Purpose: Send a food discovery, order status, coupon, or support question to the assistant.
Authentication: Bearer JWT.
Request: `{ "message": "Show healthy food", "orderId": "ODR123456" }`
Response: `{ "message": "Recommended dishes...", "dishes": [], "actions": [] }`
Possible Errors: `401` unauthorized, `429` rate limit, `503` assistant unavailable.
