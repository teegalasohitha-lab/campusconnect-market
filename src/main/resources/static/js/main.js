/**
 * CampusConnect Market — Main JavaScript Utilities
 */

// ── Toast Notifications ─────────────────────────────────
const Toast = {
  container: null,

  init() {
    if (!this.container) {
      this.container = document.createElement('div');
      this.container.className = 'toast-container-ccm';
      document.body.appendChild(this.container);
    }
  },

  show(message, type = 'success', duration = 3500) {
    this.init();
    const icons = {
      success: 'fa-check-circle', error: 'fa-times-circle',
      warning: 'fa-exclamation-triangle', info: 'fa-info-circle'
    };
    const colors = { success: '#22C55E', error: '#EF4444', warning: '#F59E0B', info: '#3B82F6' };

    const toast = document.createElement('div');
    toast.className = `toast-ccm ${type}`;
    toast.innerHTML = `
      <i class="fas ${icons[type] || icons.info}" style="color:${colors[type]};flex-shrink:0"></i>
      <span>${message}</span>
      <button onclick="this.parentElement.remove()" style="margin-left:auto;background:none;border:none;cursor:pointer;font-size:1rem;color:#9AAAC4;line-height:1">&times;</button>
    `;
    this.container.appendChild(toast);
    setTimeout(() => toast.remove(), duration);
  }
};

// ── Navbar cart/wishlist badge update ───────────────────
async function updateNavBadges() {
  if (!Auth.isLoggedIn()) return;
  try {
    const [cartRes, wishRes] = await Promise.all([
      API.get('/api/cart'),
      API.get('/api/wishlist')
    ]);
    const cartCount = cartRes.data?.length || 0;
    const wishCount = wishRes.data?.length || 0;
    document.querySelectorAll('[data-cart-badge]').forEach(el => el.textContent = cartCount || '');
    document.querySelectorAll('[data-wish-badge]').forEach(el => el.textContent = wishCount || '');
  } catch { }
}

// ── Render user info in navbar ───────────────────────────
function renderNavUser() {
  const user = Auth.getUser();
  const container = document.getElementById('navUserArea');
  if (!container) return;

  if (!user) {
    container.innerHTML = `
      <a href="/login.html" class="btn-primary-ccm" style="font-size:.85rem;padding:.5rem 1.2rem">
        <i class="fas fa-sign-in-alt"></i> Sign In
      </a>
      <a href="/register.html" class="btn-outline-ccm ms-2" style="font-size:.85rem;padding:.5rem 1.2rem">
        Sign Up
      </a>`;
  } else {
    container.innerHTML = `
      <div class="d-flex align-items-center gap-3">
        <a href="/cart.html" class="nav-link-ccm position-relative" style="font-size:1.1rem">
          <i class="fas fa-shopping-cart"></i>
          <span class="nav-badge" data-cart-badge></span>
        </a>
        <a href="/wishlist.html" class="nav-link-ccm position-relative" style="font-size:1.1rem">
          <i class="fas fa-heart"></i>
          <span class="nav-badge" data-wish-badge></span>
        </a>
        <div class="dropdown">
          <button class="btn-ghost-ccm dropdown-toggle" type="button" data-bs-toggle="dropdown">
            <i class="fas fa-user-circle me-1"></i> ${user.name.split(' ')[0]}
          </button>
          <ul class="dropdown-menu dropdown-menu-end shadow border-0" style="border-radius:12px;min-width:200px">
            <li><h6 class="dropdown-header" style="font-size:.7rem;text-transform:uppercase;letter-spacing:.05em">${user.role}</h6></li>
            <li><a class="dropdown-item" href="${getDashboardUrl(user.role)}"><i class="fas fa-tachometer-alt me-2"></i>Dashboard</a></li>
            <li><a class="dropdown-item" href="/profile.html"><i class="fas fa-user me-2"></i>Profile</a></li>
            <li><a class="dropdown-item" href="/orders.html"><i class="fas fa-box me-2"></i>My Orders</a></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item text-danger" href="#" onclick="Auth.logout()"><i class="fas fa-sign-out-alt me-2"></i>Logout</a></li>
          </ul>
        </div>
      </div>`;
    setTimeout(updateNavBadges, 300);
  }
}

function getDashboardUrl(role) {
  if (role === 'ADMIN') return '/admin-dashboard.html';
  if (role === 'SELLER') return '/seller-dashboard.html';
  return '/customer-dashboard.html';
}

// ── Format currency ──────────────────────────────────────
function formatCurrency(amount) {
  return '₹' + parseFloat(amount).toLocaleString('en-IN', { minimumFractionDigits: 2 });
}

// ── Format date ──────────────────────────────────────────
function formatDate(dateStr) {
  if (!dateStr) return '—';
  return new Date(dateStr).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
}

// ── Build star rating HTML ───────────────────────────────
function renderStars(rating, max = 5) {
  let html = '<span class="stars">';
  for (let i = 1; i <= max; i++) {
    html += `<i class="fas fa-star star ${i <= Math.round(rating) ? 'filled' : ''}"></i>`;
  }
  html += '</span>';
  return html;
}

// ── Product card component ───────────────────────────────
function buildProductCard(p) {
  const img = p.imageUrl || 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400';
  const conditionMap = {
    NEW: 'condition-new', LIKE_NEW: 'condition-like-new',
    GOOD: 'condition-good', FAIR: 'condition-fair'
  };
  const condClass = conditionMap[p.condition] || 'condition-new';
  const condLabel = p.condition ? p.condition.replace('_', ' ') : 'New';

  let badges = '';
  if (p.type === 'PREORDER') badges += `<span class="badge-ccm badge-accent">⚡ Pre-Order</span> `;
  if (p.type === 'EXCHANGE') badges += `<span class="badge-ccm badge-primary">🎓 Student</span>`;

  return `
    <div class="col-6 col-md-4 col-lg-3 mb-4">
      <div class="product-card h-100 fade-in" onclick="location.href='/product-detail.html?id=${p.id}'" style="cursor:pointer">
        <div style="overflow:hidden;border-radius:12px 12px 0 0;position:relative">
          <img src="${img}" alt="${p.title}" class="product-card-img"
               onerror="this.src='https://via.placeholder.com/400x200/f4f7fc/1a3c6e?text=No+Image'">
          <div style="position:absolute;top:.6rem;left:.6rem;display:flex;flex-wrap:wrap;gap:.25rem">${badges}</div>
          <button class="wishlist-btn" onclick="event.stopPropagation();toggleWishlist(${p.id},this)"
                  data-product-id="${p.id}"
                  style="position:absolute;top:.5rem;right:.5rem;background:white;border:none;
                         width:32px;height:32px;border-radius:50%;cursor:pointer;
                         box-shadow:0 2px 8px rgba(0,0,0,.15);font-size:1rem;
                         display:flex;align-items:center;justify-content:center;transition:all .2s">
            <i class="far fa-heart" style="color:#EF4444"></i>
          </button>
        </div>
        <div class="product-card-body">
          <div class="product-card-title">${p.title}</div>
          <div class="mb-1">
            <span class="badge-ccm ${condClass}" style="font-size:.65rem">${condLabel}</span>
          </div>
          <div class="d-flex align-items-center gap-1 mb-1">
            ${renderStars(0)}
            <span style="font-size:.75rem;color:var(--text-muted)">(0)</span>
          </div>
          <div style="display:flex;align-items:baseline;gap:.5rem;margin-top:auto">
            <span class="product-price">${formatCurrency(p.price)}</span>
          </div>
          <div style="font-size:.78rem;color:var(--text-muted);margin-top:.25rem">
            <i class="fas fa-map-marker-alt me-1" style="color:var(--accent)"></i>${p.location || 'Unknown'}
          </div>
        </div>
        <div class="product-card-footer">
          <button class="btn-primary-ccm w-100" style="justify-content:center;font-size:.85rem;padding:.5rem"
                  onclick="event.stopPropagation();addToCart(${p.id})">
            <i class="fas fa-cart-plus"></i> Add to Cart
          </button>
        </div>
      </div>
    </div>`;
}

// ── Cart actions ─────────────────────────────────────────
async function addToCart(productId) {
  if (!Auth.isLoggedIn()) {
    Toast.show('Please sign in to add items to cart', 'info');
    setTimeout(() => window.location.href = '/login.html', 1500);
    return;
  }
  try {
    const res = await API.post('/api/cart/add', { productId, quantity: 1 });
    if (res.success) {
      Toast.show('Added to cart! 🛒', 'success');
      updateNavBadges();
    } else {
      Toast.show(res.message || 'Could not add to cart', 'error');
    }
  } catch { Toast.show('Error adding to cart', 'error'); }
}

// ── Wishlist toggle ──────────────────────────────────────
async function toggleWishlist(productId, btn) {
  if (!Auth.isLoggedIn()) {
    Toast.show('Please sign in to use wishlist', 'info');
    return;
  }
  try {
    const res = await API.post('/api/wishlist/toggle', { productId });
    if (res.success) {
      const icon = btn.querySelector('i');
      if (res.data === 'added') {
        icon.className = 'fas fa-heart';
        icon.style.color = '#EF4444';
        Toast.show('Added to wishlist ❤️', 'success');
      } else {
        icon.className = 'far fa-heart';
        Toast.show('Removed from wishlist', 'info');
      }
      updateNavBadges();
    }
  } catch { Toast.show('Error updating wishlist', 'error'); }
}

// ── Debounce ──────────────────────────────────────────────
function debounce(fn, delay) {
  let timer;
  return (...args) => { clearTimeout(timer); timer = setTimeout(() => fn(...args), delay); };
}

// ── Scroll-based navbar shadow ───────────────────────────
window.addEventListener('scroll', () => {
  const navbar = document.querySelector('.navbar-ccm');
  if (navbar) navbar.classList.toggle('scrolled', window.scrollY > 10);
});

// ── Init on page load ────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  renderNavUser();
});
