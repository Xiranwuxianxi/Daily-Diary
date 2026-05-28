<template>
  <header class="app-header">
    <div class="header-inner">
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

      <div class="header-right">
        <span class="user-nickname">{{ nickname }}</span>
        <button class="logout-btn" @click="handleLogout">退出</button>
      </div>

      <!-- Mobile menu toggle -->
      <button class="mobile-menu-btn" @click="menuOpen = !menuOpen">
        <span></span><span></span><span></span>
      </button>
    </div>

    <div v-if="menuOpen" class="mobile-menu">
      <router-link to="/" class="mobile-menu-item" @click="menuOpen = false">日记列表</router-link>
      <router-link to="/write" class="mobile-menu-item" @click="menuOpen = false">写日记</router-link>
      <div class="mobile-menu-divider"></div>
      <span class="mobile-menu-item user-info">{{ nickname }}</span>
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
  max-width: 960px;
  margin: 0 auto;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
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
  gap: 16px;
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

.mobile-menu {
  display: none;
  background: var(--bg-card);
  border-bottom: 1px solid var(--border);
  padding: 12px 20px;
}

.mobile-menu-item {
  display: block;
  padding: 12px 0;
  font-size: 15px;
  color: var(--text-primary);
  text-decoration: none;
  border-bottom: 1px solid var(--border-light);
}

.mobile-menu-divider {
  height: 8px;
}

.user-info {
  color: var(--text-secondary);
  border: none;
}

.logout-item {
  width: 100%;
  text-align: left;
  background: none;
  border: none;
  font-family: inherit;
  cursor: pointer;
  color: var(--danger);
  font-size: 15px;
  padding: 12px 0;
}

@media (max-width: 768px) {
  .header-right {
    display: none;
  }

  .mobile-menu-btn {
    display: flex;
  }

  .mobile-menu {
    display: block;
  }
}
</style>