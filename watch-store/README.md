# Watch Store (Vanilla JS + LocalStorage)

## Pages
- `index.html`: Login / Signup (LocalStorage users + session)
- `home.html`: Watch grid + search
- `product.html`: Watch details + Add to Cart
- `cart.html`: Cart management + Checkout
- `payment.html`: Payment form (validation) + Place Order
- `orders.html`: Purchase history (persisted per user)

## How to run
This is a static site (no backend). You can:
- Open `watch-store/index.html` directly in a browser, **or**
- Run a tiny local static server (recommended for best Lighthouse results).

## LocalStorage keys (for reference)
- `ws_users`: user accounts (email → password hash)
- `ws_session`: current logged-in user
- `ws_products`: seeded watch catalog
- `ws_cart:<email>`: cart items per user
- `ws_orders:<email>`: orders per user

