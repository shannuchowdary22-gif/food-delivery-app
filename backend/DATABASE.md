# Database

Database name: `zomiggy` by default, configured with `DB_NAME`.

Tables:

- `users`: mobile identity, profile name/address, reward coins, role, timestamps.
- `otp_codes`: one-time mobile verification codes and expiry state.
- `restaurant`: seeded restaurant catalog.
- `dish`: seeded menu catalog; each dish belongs to one restaurant.
- `coupon`: coupon rules and descriptions.
- `orders`: user-owned orders, payment method, total, address, status, and timestamp.
- `order_item`: order lines referencing a dish, quantity, and price snapshot.

Relationships: `restaurant 1-to-many dish`, `user 1-to-many orders`, and      `order 1-to-many order_item`; each order item references one dish. Password hashes are not stored because this application authenticates by expiring OTP and issues JWT sessions.