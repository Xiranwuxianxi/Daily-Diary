<template>
  <div class="login-page">
    <div class="login-card">
      <!-- Header -->
      <div class="login-header">
        <div class="brand">
          <svg class="brand-icon" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="8" y="6" width="32" height="38" rx="4" fill="#C8956C" opacity="0.12"/>
            <rect x="10" y="8" width="28" height="34" rx="3" fill="#C8956C"/>
            <line x1="17" y1="18" x2="31" y2="18" stroke="white" stroke-width="2" stroke-linecap="round"/>
            <line x1="17" y1="24" x2="31" y2="24" stroke="white" stroke-width="2" stroke-linecap="round"/>
            <line x1="17" y1="30" x2="27" y2="30" stroke="white" stroke-width="2" stroke-linecap="round"/>
            <circle cx="37" cy="17" r="8" fill="#D4786C"/>
            <line x1="34" y1="17" x2="40" y2="17" stroke="white" stroke-width="1.8" stroke-linecap="round"/>
          </svg>
          <span class="brand-name">Daily Diary</span>
        </div>
        <p class="login-subtitle">记录每一天的美好</p>
      </div>

      <!-- Tabs -->
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

      <!-- Form -->
      <form @submit.prevent="handleSubmit" class="login-form">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <div class="input-wrap">
            <svg class="input-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
              <circle cx="12" cy="7" r="4"/>
            </svg>
            <input
              v-model="form.username"
              type="text"
              class="form-input"
              placeholder="请输入用户名"
              autocomplete="username"
              required
            />
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">密码</label>
          <div class="input-wrap">
            <svg class="input-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
            </svg>
            <input
              v-model="form.password"
              type="password"
              class="form-input"
              placeholder="请输入密码"
              autocomplete="current-password"
              required
            />
          </div>
        </div>

        <div v-if="activeTab === 'register'" class="form-group">
          <label class="form-label">昵称</label>
          <div class="input-wrap">
            <svg class="input-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
              <circle cx="12" cy="7" r="4"/>
            </svg>
            <input
              v-model="form.nickname"
              type="text"
              class="form-input"
              placeholder="给自己取个名字"
            />
          </div>
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
import { ElMessage } from 'element-plus'

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
        ElMessage.success('注册成功，请登录')
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
  max-width: 420px;
  background: var(--bg-card);
  border-radius: 16px;
  padding: 44px 40px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.06);
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 10px;
}

.brand-icon {
  width: 40px;
  height: 40px;
}

.brand-name {
  font-family: var(--font-heading);
  font-size: 26px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: 0.5px;
}

.login-subtitle {
  color: var(--text-secondary);
  font-size: 15px;
  margin-top: 4px;
}

/* Tabs */
.login-tabs {
  display: flex;
  background: var(--bg-main);
  border-radius: 10px;
  padding: 4px;
  margin-bottom: 32px;
}

.tab-btn {
  flex: 1;
  padding: 10px 0;
  background: none;
  border: none;
  font-size: 15px;
  color: var(--text-muted);
  cursor: pointer;
  border-radius: 8px;
  transition: var(--transition);
  font-family: inherit;
  font-weight: 500;
}

.tab-btn.active {
  background: var(--bg-card);
  color: var(--text-primary);
  font-weight: 600;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

/* Form */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 22px;
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

.input-wrap {
  display: flex;
  align-items: center;
  background: var(--bg-main);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  transition: var(--transition);
  overflow: hidden;
}

.input-wrap:focus-within {
  border-color: var(--accent);
  background: var(--bg-card);
  box-shadow: 0 0 0 3px rgba(200, 149, 108, 0.08);
}

.input-icon {
  flex-shrink: 0;
  margin-left: 14px;
  color: var(--text-muted);
}

.form-input {
  flex: 1;
  padding: 12px 14px 12px 10px;
  border: none;
  font-size: 15px;
  color: var(--text-primary);
  background: transparent;
  outline: none;
  font-family: inherit;
}

.form-input::placeholder {
  color: var(--text-muted);
}

.error-msg {
  color: var(--danger);
  font-size: 13px;
  text-align: center;
  padding: 10px 14px;
  background: #FEF2F2;
  border-radius: var(--radius-sm);
}

.submit-btn {
  margin-top: 6px;
  padding: 13px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: var(--radius-sm);
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: var(--transition);
  font-family: inherit;
  letter-spacing: 0.5px;
}

.submit-btn:hover {
  background: var(--accent-hover);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(200, 149, 108, 0.3);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

@media (max-width: 768px) {
  .login-card {
    padding: 32px 24px;
    border-radius: 12px;
    max-width: 380px;
  }

  .brand-name {
    font-size: 22px;
  }
}
</style>