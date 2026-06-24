/**
 * CampusConnect Market — Auth & JWT Utilities
 * Handles token storage, login, logout, and auth state
 */

const CCM_TOKEN_KEY = 'ccm_token';
const CCM_USER_KEY  = 'ccm_user';

const Auth = {

  /** Save token and user info after login */
  save(token, user) {
    localStorage.setItem(CCM_TOKEN_KEY, token);
    localStorage.setItem(CCM_USER_KEY, JSON.stringify(user));
  },

  /** Get stored JWT token */
  getToken() {
    return localStorage.getItem(CCM_TOKEN_KEY);
  },

  /** Get stored user object */
  getUser() {
    const raw = localStorage.getItem(CCM_USER_KEY);
    return raw ? JSON.parse(raw) : null;
  },

  /** Check if user is logged in */
  isLoggedIn() {
    return !!this.getToken() && !!this.getUser();
  },

  /** Get user role */
  getRole() {
    const user = this.getUser();
    return user ? user.role : null;
  },

  /** Clear auth data */
  logout() {
    localStorage.removeItem(CCM_TOKEN_KEY);
    localStorage.removeItem(CCM_USER_KEY);
    window.location.href = '/login.html';
  },

  /** Redirect to login if not authenticated */
  requireAuth(allowedRoles) {
    if (!this.isLoggedIn()) {
      window.location.href = '/login.html';
      return false;
    }
    if (allowedRoles && !allowedRoles.includes(this.getRole())) {
      window.location.href = '/index.html';
      return false;
    }
    return true;
  },

  /** Build Authorization header object */
  headers() {
    return {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + this.getToken()
    };
  }
};

/** Generic API call helper */
const API = {
  base: '',

  async get(path) {
    const res = await fetch(this.base + path, {
      headers: Auth.isLoggedIn() ? Auth.headers() : { 'Content-Type': 'application/json' }
    });
    return res.json();
  },

  async post(path, data) {
    const res = await fetch(this.base + path, {
      method: 'POST',
      headers: Auth.isLoggedIn() ? Auth.headers() : { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    return res.json();
  },

  async postPublic(path, data) {
    const res = await fetch(this.base + path, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    return res.json();
  },

  async put(path, data) {
    const res = await fetch(this.base + path, {
      method: 'PUT',
      headers: Auth.headers(),
      body: JSON.stringify(data)
    });
    return res.json();
  },

  async patch(path, data) {
    const res = await fetch(this.base + path, {
      method: 'PATCH',
      headers: Auth.headers(),
      body: JSON.stringify(data || {})
    });
    return res.json();
  },

  async delete(path) {
    const res = await fetch(this.base + path, {
      method: 'DELETE',
      headers: Auth.headers()
    });
    return res.json();
  }
};

/** Login form handler */
async function handleLogin(event) {
  event.preventDefault();
  const form = event.target;
  const email    = form.querySelector('#email').value.trim();
  const password = form.querySelector('#password').value;
  const btn      = form.querySelector('[type=submit]');
  const errEl    = document.getElementById('loginError');

  btn.disabled = true;
  btn.innerHTML = '<i class="fa fa-spinner fa-spin me-2"></i>Signing in...';

  try {
    const data = await API.postPublic('/api/auth/login', { email, password });

    if (data.token) {
      Auth.save(data.token, {
        id: data.id, name: data.name,
        email: data.email, role: data.role
      });

      // Redirect by role
      switch (data.role) {
        case 'ADMIN':    window.location.href = '/admin-dashboard.html'; break;
        case 'SELLER':   window.location.href = '/seller-dashboard.html'; break;
        case 'CUSTOMER': window.location.href = '/customer-dashboard.html'; break;
        default:         window.location.href = '/index.html';
      }
    } else {
      showError(errEl, data.message || 'Invalid email or password');
    }
  } catch (e) {
    showError(errEl, 'Connection error. Please try again.');
  } finally {
    btn.disabled = false;
    btn.innerHTML = '<i class="fa fa-sign-in-alt me-2"></i>Sign In';
  }
}

/** Register form handler */
async function handleRegister(event) {
  event.preventDefault();
  const form = event.target;
  const payload = {
    name:     form.querySelector('#name').value.trim(),
    email:    form.querySelector('#email').value.trim(),
    password: form.querySelector('#password').value,
    role:     form.querySelector('#role').value,
    phone:    form.querySelector('#phone')?.value.trim() || '',
    locality: form.querySelector('#locality')?.value.trim() || ''
  };

  const btn   = form.querySelector('[type=submit]');
  const errEl = document.getElementById('registerError');

  // Password confirm
  const confirm = form.querySelector('#confirmPassword')?.value;
  if (confirm && confirm !== payload.password) {
    showError(errEl, 'Passwords do not match');
    return;
  }

  btn.disabled = true;
  btn.innerHTML = '<i class="fa fa-spinner fa-spin me-2"></i>Creating account...';

  try {
    const data = await API.postPublic('/api/auth/register', payload);
    if (data.success) {
      Toast.show('Account created! Please sign in.', 'success');
      setTimeout(() => window.location.href = '/login.html', 1500);
    } else {
      showError(errEl, data.message || 'Registration failed');
    }
  } catch (e) {
    showError(errEl, 'Connection error. Please try again.');
  } finally {
    btn.disabled = false;
    btn.innerHTML = '<i class="fa fa-user-plus me-2"></i>Create Account';
  }
}

function showError(el, msg) {
  if (!el) return;
  el.textContent = msg;
  el.style.display = 'block';
  setTimeout(() => el.style.display = 'none', 5000);
}
