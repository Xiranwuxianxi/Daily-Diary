<template>
  <header class="app-header">
    <div class="header-inner">
      <!-- Logo -->
      <router-link to="/" class="header-brand">
        <svg class="brand-icon" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
          <rect x="6" y="4" width="28" height="33" rx="3" fill="#C8956C" opacity="0.12"/>
          <rect x="8" y="6" width="24" height="29" rx="2" fill="#C8956C"/>
          <line x1="14" y1="14" x2="26" y2="14" stroke="white" stroke-width="1.5" stroke-linecap="round"/>
          <line x1="14" y1="19" x2="26" y2="19" stroke="white" stroke-width="1.5" stroke-linecap="round"/>
          <line x1="14" y1="24" x2="22" y2="24" stroke="white" stroke-width="1.5" stroke-linecap="round"/>
        </svg>
        <span class="brand-text">Daily Diary</span>
      </router-link>

      <!-- Desktop Right -->
      <div class="header-right">
        <router-link to="/write" class="btn-write">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <line x1="12" y1="5" x2="12" y2="19"/>
            <line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          写日记
        </router-link>
        <span class="user-nickname">{{ nickname }}</span>
        <button class="logout-btn" @click="handleLogout">退出</button>
      </div>

      <!-- Mobile Menu Toggle -->
      <button class="mobile-menu-btn" @click="menuOpen = !menuOpen">
        <span></span><span></span><span></span>
      </button>
    </div>

    <!-- Mobile Drawer -->
    <div v-if="menuOpen" class="mobile-overlay" @click="menuOpen = false"></div>
    <div :class="['mobile-drawer', { open: menuOpen }]">
      <div class="drawer-header">
        <span class="drawer-title">菜单</span>
        <button class="drawer-close" @click="menuOpen = false">&times;</button>
      </div>
      <router-link to="/write" class="mobile-menu-item highlight" @click="menuOpen = false">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="12" y1="5" x2="12" y2="19"/>
          <line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        写日记
      </router-link>
      <router-link to="/" class="mobile-menu-item" @click="menuOpen = false">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="3" y="3" width="7" height="7"/>
          <rect x="14" y="3" width="7" height="7"/>
          <rect x="14" y="14" width="7" height="7"/>
          <rect x="3" y="14" width="7" height="7"/>
        </svg>
        日记列表
      </router-link>
      <div class="mobile-divider"></div>
      <span class="mobile-menu-item static">{{ nickname }}</span>
      <button class="mobile-menu-item logout-item" @click="handleLogout">退出登录</button>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const menuOpen = ref(false)
const nickname = ref(localStorage.getItem('nickname') || '用户')

function handleLogout() {
  localStorage.removeItem('token')
  localStorage.removeItem('nickname')
  localStorage.removeItem('userId')
  menuOpen.value = false
  router.push('/login')
}
</script>

<style scoped>
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: rgba(250, 250, 247, 0.92);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--border-light);
}

.header-inner {
  max-width: 1100px;
  margin: 0 auto;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
}

.header-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
}

.brand-icon {
  width: 32px;
  height: 32px;
}

.brand-text {
  font-family: var(--font-heading);
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: 0.3px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 14px;
}

/* Quick Write Button */
.btn-write {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 7px 18px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  text-decoration: none;
  transition: var(--transition);
  font-family: inherit;
  letter-spacing: 0.3px;
}

.btn-write:hover {
  background: var(--accent-hover);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(200, 149, 108, 0.3);
}

.user-nickname {
  font-size: 14px;
  color: var(--text-secondary);
}

.logout-btn {
  padding: 6px 16px;
  background: none;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: var(--transition);
  font-family: inherit;
}

.logout-btn:hover {
  border-color: var(--accent);
  color: var(--accent);
  background: var(--accent-light);
}

/* Mobile Menu Toggle */
.mobile-menu-btn {
  display: none;
  flex-direction: column;
  gap: 5px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
}

.mobile-menu-btn span {
  display: block;
  width: 22px;
  height: 2px;
  background: var(--text-primary);
  border-radius: 1px;
}

/* Mobile Overlay & Drawer */
.mobile-overlay {
  display: none;
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  z-index: 998;
}

.mobile-drawer {
  display: none;
  position: fixed;
  top: 0;
  right: -280px;
  width: 280px;
  height: 100vh;
  background: var(--bg-card);
  z-index: 999;
  padding: 0;
  transition: right 0.3s ease;
  box-shadow: -4px 0 20px rgba(0, 0, 0, 0.08);
}

.mobile-drawer.open {
  right: 0;
}

.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-light);
}

.drawer-title {
  font-weight: 600;
  font-size: 16px;
  color: var(--text-primary);
}

.drawer-close {
  background: none;
  border: none;
  font-size: 24px;
  color: var(--text-muted);
  cursor: pointer;
  padding: 0 4px;
  line-height: 1;
}

.mobile-menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 20px;
  font-size: 15px;
  color: var(--text-primary);
  text-decoration: none;
  border-bottom: 1px solid var(--border-light);
  font-family: inherit;
}

.mobile-menu-item:hover {
  background: var(--accent-light);
}

.mobile-menu-item.highlight {
  color: var(--accent);
  font-weight: 500;
}

.mobile-menu-item.static {
  color: var(--text-secondary);
  cursor: default;
}

.mobile-menu-item.static:hover {
  background: none;
}

.mobile-divider {
  height: 8px;
  background: var(--bg-main);
}

.logout-item {
  width: 100%;
  text-align: left;
  background: none;
  border: none;
  cursor: pointer;
  color: var(--danger);
  font-size: 15px;
  padding: 14px 20px;
  border-bottom: 1px solid var(--border-light);
}

@media (max-width: 768px) {
  .header-right {
    display: none;
  }

  .mobile-menu-btn {
    display: flex;
  }

  .mobile-overlay {
    display: block;
  }

  .mobile-drawer {
    display: block;
  }
}
</style>