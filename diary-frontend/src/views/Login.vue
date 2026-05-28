<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="brand">
          <svg class="brand-icon" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="6" y="4" width="28" height="33" rx="3" fill="currentColor" opacity="0.15"/>
            <rect x="8" y="6" width="24" height="29" rx="2" fill="#C8956C"/>
            <line x1="14" y1="14" x2="26" y2="14" stroke="white" stroke-width="1.5" stroke-linecap="round"/>
            <line x1="14" y1="19" x2="26" y2="19" stroke="white" stroke-width="1.5" stroke-linecap="round"/>
            <line x1="14" y1="24" x2="22" y2="24" stroke="white" stroke-width="1.5" stroke-linecap="round"/>
          </svg>
          <span class="brand-name">Daily Diary</span>
        </div>
        <p class="login-subtitle">记录每一天的美好</p>
      </div>

      <div class="login-tabs">
        <button
          :class="['tab-btn', { active: activeTab === 'login' }]"
          @click="activeTab = 'login'"
        >登录</button>
        <button
          :class="['tab-btn', { active: activeTab === 'register' }]"
          @click="activeTab = 'register'"
        >注册</button>
      </div>

      <form @submit.prevent="handleSubmit" class="login-form">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <input
            v-model="form.username"
            type="text"
            class="form-input"
            placeholder="请输入用户名"
            autocomplete="username"
            required
          />
        </div>
        <div class="form-group">
          <label class="form-label">密码</label>
          <input
            v-model="form.password"
            type="password"
            class="form-input"
            placeholder="请输入密码"
            autocomplete="current-password"
            required
          />
        </div>
        <div v-if="activeTab === 'register'" class="form-group">
          <label class="form-label">昵称</label>
          <input
            v-model="form.nickname"
            type="text"
            class="form-input"
            placeholder="给自己取个名字"
          />
        </div>

        <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>

        <button type="submit" class="submit-btn" :disabled="loading">
          {{ loading ? '处理中...' : (activeTab === 'login' ? '登录' : '注册') }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login, register } from '@/api/auth'

const router = useRouter()
const activeTab = ref('login')
const loading = ref(false)
const errorMsg = ref('')

const form = reactive({
  username: '',
  password: '',
  nickname: ''
})

async function handleSubmit() {
  errorMsg.value = ''
  loading.value = true
  try {
    if (activeTab.value === 'login') {
      const res = await login({ username: form.username, password: form.password })
      if (res.data.code === 200) {
        const d = res.data.data
        localStorage.setItem('token', d.token)
        localStorage.setItem('userId', d.userId)
        localStorage.setItem('nickname', d.nickname || form.username)
        router.push('/')
      } else {
        errorMsg.value = res.data.message
      }
    } else {
      const res = await register({
        username: form.username,
        password: form.password,
        nickname: form.nickname || form.username
      })
      if (res.data.code === 200) {
        activeTab.value = 'login'
        errorMsg.value = ''
        form.password = ''
        alert('注册成功，请登录')
      } else {
        errorMsg.value = res.data.message
      }
    }
  } catch (e) {
    errorMsg.value = e.response?.data?.message || '请求失败，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(160deg, #FAF7F2 0%, #F5EDE4 40%, #F8F5F0 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 40px 36px;
  box-shadow: var(--shadow-lg);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 8px;
}

.brand-icon {
  width: 36px;
  height: 36px;
  color: #C8956C;
}

.brand-name {
  font-family: var(--font-heading);
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: 0.5px;
}

.login-subtitle {
  color: var(--text-secondary);
  font-size: 14px;
}

.login-tabs {
  display: flex;
  border-bottom: 2px solid var(--border-light);
  margin-bottom: 28px;
}

.tab-btn {
  flex: 1;
  padding: 10px 0;
  background: none;
  border: none;
  font-size: 15px;
  color: var(--text-muted);
  cursor: pointer;
  position: relative;
  transition: var(--transition);
  font-family: inherit;
}

.tab-btn.active {
  color: var(--accent);
  font-weight: 600;
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 20%;
  right: 20%;
  height: 2px;
  background: var(--accent);
  border-radius: 1px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  letter-spacing: 0.3px;
}

.form-input {
  padding: 12px 14px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  font-size: 15px;
  color: var(--text-primary);
  background: var(--bg-main);
  outline: none;
  transition: var(--transition);
  font-family: inherit;
}

.form-input:focus {
  border-color: var(--accent);
  background: var(--bg-card);
  box-shadow: 0 0 0 3px rgba(200, 149, 108, 0.08);
}

.form-input::placeholder {
  color: var(--text-muted);
}

.error-msg {
  color: var(--danger);
  font-size: 13px;
  text-align: center;
  padding: 8px 12px;
  background: #FEF2F2;
  border-radius: var(--radius-sm);
}

.submit-btn {
  margin-top: 4px;
  padding: 13px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: var(--radius-sm);
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition);
  font-family: inherit;
  letter-spacing: 0.5px;
}

.submit-btn:hover {
  background: var(--accent-hover);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .login-card {
    padding: 32px 24px;
    border-radius: var(--radius);
  }
}
</style>