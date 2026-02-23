/* Watch Store (Vanilla JS + LocalStorage) */

const KEYS = Object.freeze({
  users: "ws_users",
  session: "ws_session",
  products: "ws_products",
});

const money = new Intl.NumberFormat("en-IN", {
  style: "currency",
  currency: "INR",
  maximumFractionDigits: 0,
});

const qs = (sel, root = document) => root.querySelector(sel);
const qsa = (sel, root = document) => [...root.querySelectorAll(sel)];

function lsGet(key, fallback) {
  try {
    const raw = localStorage.getItem(key);
    if (raw == null) return fallback;
    return JSON.parse(raw);
  } catch {
    return fallback;
  }
}
function lsSet(key, value) {
  localStorage.setItem(key, JSON.stringify(value));
}

function getSessionEmail() {
  const s = lsGet(KEYS.session, null);
  return s?.email || null;
}
function setSession(email) {
  lsSet(KEYS.session, { email, at: new Date().toISOString() });
}
function clearSession() {
  localStorage.removeItem(KEYS.session);
}

function cartKey(email) {
  return `ws_cart:${email}`;
}
function ordersKey(email) {
  return `ws_orders:${email}`;
}
function lastOrderKey(email) {
  return `ws_lastOrder:${email}`;
}

function toast(title, message) {
  const el = qs("#toast");
  if (!el) return;
  qs(".t", el).textContent = title;
  qs(".m", el).textContent = message || "";
  el.classList.add("show");
  window.clearTimeout(toast._t);
  toast._t = window.setTimeout(() => el.classList.remove("show"), 2800);
}

function requireAuth() {
  if (getSessionEmail()) return;
  location.replace("./index.html");
}

function setActiveNav() {
  const here = (location.pathname.split("/").pop() || "").toLowerCase();
  qsa(".nav a.pill").forEach((a) => {
    const href = (a.getAttribute("href") || "").toLowerCase();
    if (!href) return;
    if (href === here) a.setAttribute("aria-current", "page");
    else a.removeAttribute("aria-current");
  });
}

function encodeSvg(svg) {
  return `data:image/svg+xml,${encodeURIComponent(svg)}`;
}
function watchSvg({ base = "#7C5CFF", accent = "#00E6FF", glow = "#35F29B" }) {
  return encodeSvg(`
<svg xmlns="http://www.w3.org/2000/svg" width="800" height="600" viewBox="0 0 800 600">
  <defs>
    <radialGradient id="g" cx="30%" cy="20%" r="80%">
      <stop offset="0%" stop-color="${accent}" stop-opacity="0.25"/>
      <stop offset="60%" stop-color="${base}" stop-opacity="0.10"/>
      <stop offset="100%" stop-color="#050712" stop-opacity="1"/>
    </radialGradient>
    <linearGradient id="s" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="${base}" stop-opacity="0.9"/>
      <stop offset="100%" stop-color="${accent}" stop-opacity="0.7"/>
    </linearGradient>
    <filter id="glow" x="-50%" y="-50%" width="200%" height="200%">
      <feGaussianBlur stdDeviation="10" result="b"/>
      <feMerge>
        <feMergeNode in="b"/>
        <feMergeNode in="SourceGraphic"/>
      </feMerge>
    </filter>
  </defs>
  <rect width="800" height="600" fill="url(#g)"/>
  <g transform="translate(0,10)">
    <rect x="340" y="40" width="120" height="130" rx="26" fill="#0a0f24" stroke="rgba(255,255,255,.16)" />
    <rect x="340" y="430" width="120" height="130" rx="26" fill="#0a0f24" stroke="rgba(255,255,255,.16)" />
    <rect x="360" y="75" width="80" height="55" rx="14" fill="url(#s)" opacity="0.55"/>
    <rect x="360" y="470" width="80" height="55" rx="14" fill="url(#s)" opacity="0.55"/>
    <circle cx="400" cy="300" r="150" fill="#071027" stroke="rgba(255,255,255,.18)" stroke-width="2"/>
    <circle cx="400" cy="300" r="120" fill="url(#s)" opacity="0.18" filter="url(#glow)"/>
    <circle cx="400" cy="300" r="92" fill="#060B18" stroke="rgba(255,255,255,.12)"/>
    <circle cx="400" cy="300" r="6" fill="${glow}" filter="url(#glow)"/>
    <g stroke="rgba(255,255,255,.45)" stroke-linecap="round">
      <line x1="400" y1="300" x2="400" y2="235" stroke-width="6"/>
      <line x1="400" y1="300" x2="460" y2="320" stroke-width="4"/>
      <line x1="400" y1="300" x2="385" y2="360" stroke-width="3"/>
    </g>
    <g fill="rgba(255,255,255,.30)">
      <circle cx="400" cy="214" r="4"/><circle cx="400" cy="386" r="4"/>
      <circle cx="314" cy="300" r="4"/><circle cx="486" cy="300" r="4"/>
    </g>
  </g>
</svg>`);
}

function seedProducts() {
  const existing = lsGet(KEYS.products, null);
  if (Array.isArray(existing) && existing.length) return;

  const products = [
    {
      id: "neo-chrono",
      title: "Neo Chrono X1",
      price: 7999,
      rating: 4.6,
      genre: "Cyber Sport",
      desc:
        "A neon-edged chronograph with a crisp dial layout and a lightweight strap built for long sessions and late-night runs.",
      image: watchSvg({ base: "#7C5CFF", accent: "#00E6FF", glow: "#35F29B" }),
    },
    {
      id: "aurora-stealth",
      title: "Aurora Stealth S",
      price: 9999,
      rating: 4.8,
      genre: "Stealth Ops",
      desc:
        "Minimal, dark, and razor-clean. Aurora Stealth pairs a deep matte dial with bright edge highlights for quick readability.",
      image: watchSvg({ base: "#1D2BFF", accent: "#00E6FF", glow: "#7C5CFF" }),
    },
    {
      id: "pulse-runner",
      title: "Pulse Runner R2",
      price: 6499,
      rating: 4.4,
      genre: "Speed Run",
      desc:
        "Designed for movement with a balanced face and bold markers. Clean ergonomics, fast legibility, smooth everyday wear.",
      image: watchSvg({ base: "#00E6FF", accent: "#35F29B", glow: "#00E6FF" }),
    },
    {
      id: "titan-arc",
      title: "Titan Arc T9",
      price: 11999,
      rating: 4.7,
      genre: "Boss Mode",
      desc:
        "A premium heavy-hitter with a strong silhouette and high-contrast hands. Built for those who like presence on the wrist.",
      image: watchSvg({ base: "#FFB703", accent: "#7C5CFF", glow: "#00E6FF" }),
    },
    {
      id: "spectra-lite",
      title: "Spectra Lite L3",
      price: 5499,
      rating: 4.2,
      genre: "Arcade",
      desc:
        "Bright, playful gradients and a compact dial. A daily driver that feels as quick as your reflexes.",
      image: watchSvg({ base: "#FF4D6D", accent: "#00E6FF", glow: "#35F29B" }),
    },
    {
      id: "nova-field",
      title: "Nova Field N5",
      price: 8999,
      rating: 4.5,
      genre: "Open World",
      desc:
        "Rugged lines with a futuristic finish. The Nova Field is made for exploring—clean dial, durable vibe, easy comfort.",
      image: watchSvg({ base: "#35F29B", accent: "#7C5CFF", glow: "#35F29B" }),
    },
    {
      id: "vortex-surge",
      title: "Vortex Surge V7",
      price: 10999,
      rating: 4.7,
      genre: "Ranked",
      desc:
        "High-energy styling with strong markers and sharp contrast. A statement watch that still reads instantly at a glance.",
      image: watchSvg({ base: "#00E6FF", accent: "#FF4D6D", glow: "#7C5CFF" }),
    },
    {
      id: "shadow-matrix",
      title: "Shadow Matrix M4",
      price: 7599,
      rating: 4.3,
      genre: "Night Raid",
      desc:
        "Dark dial, bright accents, and a sleek strap. Built for low-light clarity and all-day comfort.",
      image: watchSvg({ base: "#7C5CFF", accent: "#FF4D6D", glow: "#00E6FF" }),
    },
    {
      id: "pixel-pro",
      title: "Pixel Pro P8",
      price: 6999,
      rating: 4.4,
      genre: "Retro",
      desc:
        "A modern take on classic shapes—clean geometry and a friendly feel. The perfect blend of nostalgia and polish.",
      image: watchSvg({ base: "#35F29B", accent: "#00E6FF", glow: "#FFB703" }),
    },
    {
      id: "orbit-prime",
      title: "Orbit Prime O2",
      price: 13499,
      rating: 4.9,
      genre: "Legendary",
      desc:
        "Top-tier finish, crisp edges, and a premium face. Orbit Prime is for collectors who want a flagship piece.",
      image: watchSvg({ base: "#FFB703", accent: "#00E6FF", glow: "#35F29B" }),
    },
    {
      id: "drift-core",
      title: "Drift Core D6",
      price: 6299,
      rating: 4.1,
      genre: "Sandbox",
      desc:
        "Simple, smooth, and versatile. Drift Core stays clean and comfortable, from work to weekend sessions.",
      image: watchSvg({ base: "#00E6FF", accent: "#7C5CFF", glow: "#35F29B" }),
    },
    {
      id: "zenith-strike",
      title: "Zenith Strike Z3",
      price: 10499,
      rating: 4.6,
      genre: "FPS",
      desc:
        "Sharp angles and bold contrast for instant readability. A high-focus watch with a competitive edge.",
      image: watchSvg({ base: "#FF4D6D", accent: "#7C5CFF", glow: "#00E6FF" }),
    },
  ];

  lsSet(KEYS.products, products);
}

function getProducts() {
  return lsGet(KEYS.products, []);
}
function getProduct(id) {
  return getProducts().find((p) => p.id === id) || null;
}

function getCart(email) {
  return lsGet(cartKey(email), []);
}
function setCart(email, items) {
  lsSet(cartKey(email), items);
}
function cartCount(email) {
  return getCart(email).reduce((n, it) => n + (it.qty || 0), 0);
}

function cartLinesDetailed(email) {
  const items = getCart(email);
  const products = getProducts();
  const lines = items
    .map((it) => {
      const p = products.find((x) => x.id === it.id);
      if (!p) return null;
      const qty = Math.max(1, Number(it.qty || 1));
      return {
        id: p.id,
        title: p.title,
        price: p.price,
        qty,
        total: p.price * qty,
      };
    })
    .filter(Boolean);
  const grand = lines.reduce((s, l) => s + l.total, 0);
  return { lines, grand };
}

function updateNavBadges() {
  const email = getSessionEmail();
  const badge = qs('[data-cart-count]');
  if (!badge) return;
  if (!email) {
    badge.textContent = "0";
    return;
  }
  badge.textContent = String(cartCount(email));
}

async function sha256Hex(text) {
  // WebCrypto is not guaranteed on file:// in all browsers; fall back gracefully.
  if (globalThis.crypto?.subtle?.digest) {
    const data = new TextEncoder().encode(text);
    const hash = await crypto.subtle.digest("SHA-256", data);
    return [...new Uint8Array(hash)].map((b) => b.toString(16).padStart(2, "0")).join("");
  }
  return fnv1aHex(text);
}

function fnv1aHex(str) {
  let h1 = 0x811c9dc5;
  let h2 = 0x811c9dc5 ^ 0x9e3779b9;
  const s = String(str ?? "");
  for (let i = 0; i < s.length; i++) {
    const c = s.charCodeAt(i);
    h1 ^= c;
    h1 = Math.imul(h1, 0x01000193);
    h2 ^= c;
    h2 = Math.imul(h2, 0x01000193);
  }
  const a = (h1 >>> 0).toString(16).padStart(8, "0");
  const b = (h2 >>> 0).toString(16).padStart(8, "0");
  return a + b;
}

function normEmail(email) {
  return String(email || "").trim().toLowerCase();
}

async function authSignup(email, password) {
  const e = normEmail(email);
  if (!e || !e.includes("@")) throw new Error("Enter a valid email address.");
  if (!password || password.length < 6) throw new Error("Password must be at least 6 characters.");
  const users = lsGet(KEYS.users, {});
  if (users[e]) throw new Error("Account already exists. Please log in.");
  const pwHash = await sha256Hex(password);
  users[e] = { pwHash, createdAt: new Date().toISOString() };
  lsSet(KEYS.users, users);
  return e;
}

async function authLogin(email, password) {
  const e = normEmail(email);
  const users = lsGet(KEYS.users, {});
  const u = users[e];
  if (!u) throw new Error("No account found. Please sign up first.");
  const pwHash = await sha256Hex(password || "");
  if (pwHash !== u.pwHash) throw new Error("Invalid email or password.");
  return e;
}

function initAuthPage() {
  const loginForm = qs("#loginForm");
  const signupForm = qs("#signupForm");
  const toggleToSignup = qs("#toggleToSignup");
  const toggleToLogin = qs("#toggleToLogin");

  const show = (mode) => {
    const isSignup = mode === "signup";
    signupForm.hidden = !isSignup;
    loginForm.hidden = isSignup;
    toggleToSignup.hidden = isSignup;
    toggleToLogin.hidden = !isSignup;
    qs("#authTitle").textContent = isSignup ? "Create your account" : "Welcome back";
  };

  toggleToSignup?.addEventListener("click", () => show("signup"), { passive: true });
  toggleToLogin?.addEventListener("click", () => show("login"), { passive: true });

  loginForm?.addEventListener("submit", async (e) => {
    e.preventDefault();
    const err = qs("#loginErr");
    err.textContent = "";
    const btn = qs("#loginBtn");
    btn.disabled = true;
    try {
      const email = qs("#loginEmail").value;
      const pass = qs("#loginPassword").value;
      const okEmail = await authLogin(email, pass);
      setSession(okEmail);
      toast("Logged in", "Redirecting to the store…");
      setTimeout(() => location.replace("./home.html"), 350);
    } catch (ex) {
      err.textContent = ex.message || "Login failed.";
    } finally {
      btn.disabled = false;
    }
  });

  signupForm?.addEventListener("submit", async (e) => {
    e.preventDefault();
    const err = qs("#signupErr");
    err.textContent = "";
    const btn = qs("#signupBtn");
    btn.disabled = true;
    try {
      const email = qs("#signupEmail").value;
      const pass = qs("#signupPassword").value;
      const okEmail = await authSignup(email, pass);
      setSession(okEmail);
      toast("Account created", "Redirecting to the store…");
      setTimeout(() => location.replace("./home.html"), 350);
    } catch (ex) {
      err.textContent = ex.message || "Signup failed.";
    } finally {
      btn.disabled = false;
    }
  });

  if (getSessionEmail()) location.replace("./home.html");
}

function renderProductsGrid(list) {
  const grid = qs("#productsGrid");
  if (!grid) return;

  grid.innerHTML = list
    .map(
      (p) => `
<article class="card product-card col-3">
  <div class="product-img">
    <img src="${p.image}" alt="${p.title}" width="800" height="600" loading="lazy" decoding="async">
  </div>
  <div class="product-body stack">
    <div>
      <h3 class="title">${p.title}</h3>
      <div class="meta">
        <span class="chip">Genre: <strong>${p.genre}</strong></span>
        <span class="chip">Rating: <strong>${p.rating.toFixed(1)}</strong></span>
      </div>
      <div class="price">${money.format(p.price)}</div>
    </div>
    <div class="row">
      <a class="btn small" href="./product.html?id=${encodeURIComponent(p.id)}">View Details</a>
      <span class="spacer"></span>
    </div>
  </div>
</article>`
    )
    .join("");
}

function initHomePage() {
  const products = getProducts();
  renderProductsGrid(products);

  const input = qs("#searchInput");
  let raf = 0;
  input?.addEventListener("input", () => {
    window.cancelAnimationFrame(raf);
    raf = window.requestAnimationFrame(() => {
      const q = (input.value || "").trim().toLowerCase();
      const filtered = !q
        ? products
        : products.filter((p) => p.title.toLowerCase().includes(q));
      renderProductsGrid(filtered);
      qs("#resultsCount").textContent = `${filtered.length} watch${filtered.length === 1 ? "" : "es"}`;
    });
  });

  qs("#resultsCount").textContent = `${products.length} watches`;
}

function initProductPage() {
  const params = new URLSearchParams(location.search);
  const id = params.get("id");
  const p = id ? getProduct(id) : null;
  if (!p) {
    location.replace("./home.html");
    return;
  }

  qs("#pImg").src = p.image;
  qs("#pImg").alt = p.title;
  qs("#pTitle").textContent = p.title;
  qs("#pPrice").textContent = money.format(p.price);
  qs("#pGenre").textContent = p.genre;
  qs("#pRating").textContent = p.rating.toFixed(1);
  qs("#pDesc").textContent = p.desc;

  const btn = qs("#addToCartBtn");
  btn?.addEventListener("click", () => {
    const email = getSessionEmail();
    const cart = getCart(email);
    const found = cart.find((x) => x.id === p.id);
    if (found) found.qty = Math.min(99, (found.qty || 1) + 1);
    else cart.push({ id: p.id, qty: 1 });
    setCart(email, cart);
    updateNavBadges();
    toast("Added to cart", `${p.title} is ready in your cart.`);
  });
}

function initCartPage() {
  const email = getSessionEmail();
  const table = qs("#cartTable");
  const totalEl = qs("#grandTotal");
  const checkoutBtn = qs("#checkoutBtn");

  const render = () => {
    const { lines, grand } = cartLinesDetailed(email);
    totalEl.textContent = money.format(grand);
    checkoutBtn.disabled = grand <= 0;
    if (!lines.length) {
      table.innerHTML = `
<div class="card pad soft">
  <div class="stack">
    <div class="h1" style="font-size:18px;margin:0">Your cart is empty</div>
    <p class="sub" style="margin:0">Pick a watch you like and come back here to checkout.</p>
    <div><a class="btn small" href="./home.html">Browse watches</a></div>
  </div>
</div>`;
      return;
    }

    table.innerHTML = lines
      .map(
        (l) => `
<div class="line" data-id="${l.id}">
  <div class="stack" style="gap:2px;min-width:220px">
    <div class="name">${l.title}</div>
    <div class="muted">${money.format(l.price)} each</div>
  </div>
  <div class="qty">
    <button class="btn small ghost" data-act="dec" aria-label="Decrease quantity">−</button>
    <div class="num" aria-live="polite">${l.qty}</div>
    <button class="btn small ghost" data-act="inc" aria-label="Increase quantity">+</button>
  </div>
  <div class="spacer"></div>
  <div class="stack" style="gap:2px;align-items:flex-end">
    <div class="name">${money.format(l.total)}</div>
    <button class="btn small danger" data-act="rm">Remove</button>
  </div>
</div>`
      )
      .join("");
  };

  table?.addEventListener("click", (e) => {
    const btn = e.target.closest("button");
    if (!btn) return;
    const row = e.target.closest("[data-id]");
    if (!row) return;
    const id = row.getAttribute("data-id");
    const act = btn.getAttribute("data-act");
    const cart = getCart(email);
    const item = cart.find((x) => x.id === id);
    if (!item) return;

    if (act === "inc") item.qty = Math.min(99, (item.qty || 1) + 1);
    if (act === "dec") item.qty = Math.max(1, (item.qty || 1) - 1);
    if (act === "rm") {
      const next = cart.filter((x) => x.id !== id);
      setCart(email, next);
      updateNavBadges();
      render();
      return;
    }
    setCart(email, cart);
    updateNavBadges();
    render();
  });

  checkoutBtn?.addEventListener("click", () => location.assign("./payment.html"));
  render();
}

function luhnOk(num) {
  const s = String(num || "").replace(/\s+/g, "");
  if (!/^\d{12,19}$/.test(s)) return false;
  let sum = 0;
  let alt = false;
  for (let i = s.length - 1; i >= 0; i--) {
    let n = s.charCodeAt(i) - 48;
    if (alt) {
      n *= 2;
      if (n > 9) n -= 9;
    }
    sum += n;
    alt = !alt;
  }
  return sum % 10 === 0;
}

function parseExpiry(text) {
  const t = String(text || "").trim();
  const m = t.match(/^(\d{2})\s*\/\s*(\d{2})$/);
  if (!m) return null;
  const mm = Number(m[1]);
  const yy = Number(m[2]);
  if (!(mm >= 1 && mm <= 12)) return null;
  const fullYear = 2000 + yy;
  return { mm, yy: fullYear };
}

function initPaymentPage() {
  const email = getSessionEmail();
  const { lines, grand } = cartLinesDetailed(email);
  if (!lines.length) {
    location.replace("./cart.html");
    return;
  }

  qs("#payTotal").textContent = money.format(grand);
  qs("#orderSummary").innerHTML = lines
    .map((l) => `<div class="row"><span class="chip"><strong>${l.qty}×</strong> ${l.title}</span><span class="spacer"></span><span class="chip"><strong>${money.format(l.total)}</strong></span></div>`)
    .join("");

  const form = qs("#paymentForm");
  const err = qs("#payErr");
  const btn = qs("#payBtn");

  form?.addEventListener("submit", async (e) => {
    e.preventDefault();
    err.textContent = "";
    btn.disabled = true;
    btn.textContent = "Processing…";

    try {
      const name = qs("#cardName").value.trim();
      const card = qs("#cardNumber").value;
      const exp = qs("#expiry").value;
      const cvv = qs("#cvv").value.trim();
      const zip = qs("#zip").value.trim();

      if (name.length < 2) throw new Error("Enter the name on the card.");
      if (!luhnOk(card)) throw new Error("Enter a valid card number.");
      const ex = parseExpiry(exp);
      if (!ex) throw new Error("Expiry must be in MM/YY format.");
      const now = new Date();
      const expLast = new Date(ex.yy, ex.mm, 0, 23, 59, 59);
      if (expLast < now) throw new Error("Card is expired.");
      if (!/^\d{3,4}$/.test(cvv)) throw new Error("CVV must be 3–4 digits.");
      if (!/^[0-9]{4,10}$/.test(zip)) throw new Error("Enter a valid ZIP/Postal code.");

      // Simulated payment processing (fast, non-blocking)
      await new Promise((r) => setTimeout(r, 350));

      const orderId = (crypto.randomUUID?.() || `ORD-${Date.now()}-${Math.random().toString(16).slice(2, 8)}`).toUpperCase();
      const createdAt = new Date().toISOString();
      const order = { id: orderId, createdAt, total: grand, items: lines };

      const orders = lsGet(ordersKey(email), []);
      orders.unshift(order);
      lsSet(ordersKey(email), orders);
      localStorage.removeItem(cartKey(email));
      localStorage.setItem(lastOrderKey(email), orderId);
      updateNavBadges();

      toast("Payment successful", "Order placed. Opening your purchase history…");
      setTimeout(() => location.replace(`./orders.html?success=1&id=${encodeURIComponent(orderId)}`), 450);
    } catch (ex) {
      err.textContent = ex.message || "Payment failed.";
      btn.disabled = false;
      btn.textContent = "Pay & Place Order";
    }
  });
}

function initOrdersPage() {
  const email = getSessionEmail();
  const orders = lsGet(ordersKey(email), []);
  const wrap = qs("#ordersWrap");
  const params = new URLSearchParams(location.search);
  const success = params.get("success") === "1";
  const recentId = params.get("id") || localStorage.getItem(lastOrderKey(email)) || "";

  if (success && recentId) {
    toast("Order confirmed", `Order ${recentId} saved to your history.`);
    localStorage.removeItem(lastOrderKey(email));
  }

  if (!orders.length) {
    wrap.innerHTML = `
<div class="card pad soft">
  <div class="stack">
    <div class="h1" style="font-size:18px;margin:0">No orders yet</div>
    <p class="sub" style="margin:0">When you complete a purchase, it’ll show up here.</p>
    <div><a class="btn small" href="./home.html">Shop watches</a></div>
  </div>
</div>`;
    return;
  }

  wrap.innerHTML = orders
    .map((o) => {
      const dt = new Date(o.createdAt);
      const items = (o.items || [])
        .map((it) => `<div class="row"><span class="chip"><strong>${it.qty}×</strong> ${it.title}</span><span class="spacer"></span><span class="chip"><strong>${money.format(it.total)}</strong></span></div>`)
        .join("");
      return `
<section class="card pad">
  <div class="row">
    <div class="stack" style="gap:2px">
      <div class="h1" style="font-size:18px;margin:0">Order <span style="opacity:.9">${o.id}</span></div>
      <div class="sub" style="margin:0">${dt.toLocaleString()}</div>
    </div>
    <span class="spacer"></span>
    <div class="big">${money.format(o.total)}</div>
  </div>
  <div class="stack" style="margin-top:12px">${items}</div>
</section>`;
    })
    .join("");
}

function initCommonNav() {
  updateNavBadges();
  qs("#logoutBtn")?.addEventListener(
    "click",
    () => {
      clearSession();
      toast("Logged out", "See you next time.");
      setTimeout(() => location.replace("./index.html"), 250);
    },
    { passive: true }
  );
}

function boot() {
  seedProducts();
  setActiveNav();

  const page = document.body.dataset.page || "";
  if (page !== "auth") requireAuth();

  if (page !== "auth") initCommonNav();

  if (page === "auth") initAuthPage();
  if (page === "home") initHomePage();
  if (page === "product") initProductPage();
  if (page === "cart") initCartPage();
  if (page === "payment") initPaymentPage();
  if (page === "orders") initOrdersPage();
}

document.addEventListener("DOMContentLoaded", boot, { passive: true });

