<template>
  <div class="detail-page">
    <AppHeader />
    <main class="detail-container" v-if="diary">
      <button class="back-btn" @click="$router.push('/')">
        &larr; 返回列表
      </button>

      <article class="detail-card">
        <div class="detail-meta">
          <span class="detail-date">
            <span class="meta-icon">&#x1F4C5;</span>
            {{ formatDate(diary.createTime) }}
          </span>
          <span class="detail-mood">
            {{ getMoodIcon(diary.mood) }} {{ diary.mood }}
          </span>
          <span class="detail-weather">
            {{ getWeatherIcon(diary.weather) }} {{ diary.weather }}
          </span>
        </div>

        <h1 class="detail-title">{{ diary.title }}</h1>

        <div class="detail-content">{{ diary.content }}</div>
      </article>

      <div class="detail-actions">
        <button class="btn-edit" @click="$router.push(`/write/${diary.id}`)">
          &#x270F; 编辑
        </button>
        <button class="btn-delete" @click="handleDelete">
          &#x1F5D1; 删除
        </button>
      </div>
    </main>

    <main class="detail-container" v-else-if="loading">
      <p class="loading-text">加载中...</p>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getById, remove } from '@/api/diary'
import AppHeader from '@/components/AppHeader.vue'

const route = useRoute()
const router = useRouter()
const diary = ref(null)
const loading = ref(true)

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

function getWeatherIcon(weather) {
  const map = { '晴': '☀️', '多云': '⛅', '雨': '🌧️', '雪': '❄️', '风': '🌬️' }
  return map[weather] || '☀️'
}

function getMoodIcon(mood) {
  const map = { '开心': '😊', '伤心': '😢', '生气': '😡', '平静': '😌', '思考': '🤔', '幸福': '🥰' }
  return map[mood] || '😊'
}

async function loadDiary() {
  loading.value = true
  try {
    const res = await getById(route.params.id)
    if (res.data.code === 200) {
      diary.value = res.data.data
    }
  } catch (e) {
    alert('加载日记失败')
    router.push('/')
  } finally {
    loading.value = false
  }
}

async function handleDelete() {
  if (!confirm('确定要删除这篇日记吗？')) return
  try {
    await remove(route.params.id)
    router.push('/')
  } catch (e) {
    alert(e.response?.data?.message || '删除失败')
  }
}

onMounted(() => {
  loadDiary()
})
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: var(--bg-main);
}

.detail-container {
  max-width: 720px;
  margin: 0 auto;
  padding: 80px 20px 60px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  background: none;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: var(--transition);
  font-family: inherit;
  margin-bottom: 24px;
}

.back-btn:hover {
  border-color: var(--accent);
  color: var(--accent);
}

.detail-card {
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 36px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.detail-date,
.detail-mood,
.detail-weather {
  font-size: 14px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 6px;
}

.meta-icon {
  font-size: 16px;
}

.detail-title {
  font-size: 32px;
  font-weight: 700;
  font-family: var(--font-heading);
  color: var(--text-primary);
  line-height: 1.3;
  margin-bottom: 28px;
}

.detail-content {
  font-size: 16px;
  line-height: 2;
  color: var(--text-primary);
  white-space: pre-wrap;
  word-break: break-word;
}

.detail-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
  justify-content: flex-end;
}

.btn-edit {
  padding: 10px 24px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: var(--transition);
  font-family: inherit;
}

.btn-edit:hover {
  border-color: var(--accent);
  color: var(--accent);
  background: var(--accent-light);
}

.btn-delete {
  padding: 10px 24px;
  background: none;
  border: 1px solid var(--danger);
  border-radius: var(--radius-sm);
  font-size: 14px;
  color: var(--danger);
  cursor: pointer;
  transition: var(--transition);
  font-family: inherit;
}

.btn-delete:hover {
  background: #FEF2F2;
}

.loading-text {
  text-align: center;
  color: var(--text-muted);
  padding: 80px 0;
}

@media (max-width: 768px) {
  .detail-container {
    padding: 70px 0 40px;
  }

  .detail-card {
    border-radius: 0;
    padding: 24px 18px;
    box-shadow: none;
    border: none;
  }

  .detail-title {
    font-size: 24px;
  }

  .detail-content {
    font-size: 15px;
    line-height: 1.8;
  }

  .detail-actions {
    padding: 0 18px;
  }

  .back-btn {
    margin-left: 18px;
  }
}
</style>