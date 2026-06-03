<template>
  <div class="detail-page">
    <AppHeader />
    <main class="detail-container" v-if="diary">
      <button class="back-btn" @click="$router.push('/')">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="15 18 9 12 15 6"/>
        </svg>
        返回列表
      </button>

      <article class="detail-card">
        <!-- Meta Info Bar -->
        <div class="detail-meta">
          <span class="meta-item">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
              <line x1="16" y1="2" x2="16" y2="6"/>
              <line x1="8" y1="2" x2="8" y2="6"/>
              <line x1="3" y1="10" x2="21" y2="10"/>
            </svg>
            {{ formatDate(diary.createTime) }}
          </span>
          <span class="meta-item">
            {{ getMoodIcon(diary.mood) }} {{ diary.mood }}
          </span>
          <span class="meta-item">
            {{ getWeatherIcon(diary.weather) }} {{ diary.weather }}
          </span>
          <span class="meta-item tags" v-if="diary.tags">
            <span
              v-for="tag in parseTags(diary.tags)"
              :key="tag"
              class="detail-tag"
            >{{ tag }}</span>
          </span>
        </div>

        <h1 class="detail-title">{{ diary.title }}</h1>

        <div class="detail-content">{{ diary.content }}</div>
      </article>

      <div class="detail-actions">
        <button class="btn-edit" @click="$router.push(`/write/${diary.id}`)">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/>
          </svg>
          编辑
        </button>
        <button class="btn-delete" @click="handleDelete">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="3 6 5 6 21 6"/>
            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
          </svg>
          删除
        </button>
      </div>
    </main>

    <main class="detail-container" v-else-if="loading">
      <div class="loading-state">
        <div class="loading-spinner"></div>
        <p class="loading-text">加载中...</p>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getById, remove } from '@/api/diary'
import AppHeader from '@/components/AppHeader.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

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
  const weekDays = ['日', '一', '二', '三', '四', '五', '六']
  const w = weekDays[d.getDay()]
  return `${y}-${m}-${day} 星期${w}`
}

function getWeatherIcon(weather) {
  const map = { '晴': '☀️', '多云': '⛅', '阴': '☁️', '雨': '🌧️', '雪': '❄️', '风': '🌬️' }
  return map[weather] || '☀️'
}

function getMoodIcon(mood) {
  const map = { '开心': '😊', '伤心': '😢', '生气': '😡', '平静': '😌', '思考': '🤔', '幸福': '🥰' }
  return map[mood] || '😊'
}

function parseTags(tags) {
  if (!tags) return []
  return tags.split(',').map(t => t.trim()).filter(Boolean)
}

async function loadDiary() {
  loading.value = true
  try {
    const res = await getById(route.params.id)
    if (res.data.code === 200) {
      diary.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('加载日记失败')
    router.push('/')
  } finally {
    loading.value = false
  }
}

async function handleDelete() {
  try {
    await ElMessageBox.confirm('确定要删除这篇日记吗？删除后无法恢复。', '确认删除', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
      confirmButtonClass: 'el-button--danger'
    })
    await remove(route.params.id)
    ElMessage.success('删除成功')
    router.push('/')
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
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
  padding: 80px 24px 60px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
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

/* Detail Card */
.detail-card {
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 40px;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border-light);
}

/* Meta Info Bar */
.detail-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 28px;
  flex-wrap: wrap;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--border-light);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--text-secondary);
}

.meta-item svg {
  color: var(--text-muted);
}

.meta-item.tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.detail-tag {
  font-size: 12px;
  color: var(--accent);
  background: var(--accent-light);
  padding: 2px 10px;
  border-radius: 12px;
  font-weight: 500;
}

.detail-title {
  font-size: 32px;
  font-weight: 700;
  font-family: var(--font-heading);
  color: var(--text-primary);
  line-height: 1.3;
  margin-bottom: 32px;
  letter-spacing: 0.3px;
}

.detail-content {
  font-size: 17px;
  line-height: 2.0;
  color: var(--text-primary);
  white-space: pre-wrap;
  word-break: break-word;
  letter-spacing: 0.2px;
}

/* Actions */
.detail-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
  justify-content: flex-end;
}

.btn-edit {
  display: inline-flex;
  align-items: center;
  gap: 6px;
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
  display: inline-flex;
  align-items: center;
  gap: 6px;
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

/* Loading */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120px 0;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid var(--border-light);
  border-top-color: var(--accent);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  color: var(--text-muted);
  font-size: 14px;
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
    line-height: 1.9;
  }

  .detail-actions {
    padding: 0 18px;
  }

  .back-btn {
    margin-left: 18px;
  }
}
</style>
