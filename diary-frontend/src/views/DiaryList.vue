<template>
  <div class="diary-list-page">
    <AppHeader />

    <main class="main-content">
      <!-- Search & Filter -->
      <div class="filter-bar">
        <div class="search-box">
          <el-input
            v-model="keyword"
            placeholder="搜索日记标题..."
            clearable
            @input="handleSearch"
            @clear="handleSearch"
          >
            <template #prefix>
              <span class="search-icon">&#x1F50D;</span>
            </template>
          </el-input>
        </div>
        <div class="filter-options">
          <el-select v-model="mood" placeholder="全部心情" clearable @change="handleSearch" size="default">
            <el-option label="😊 开心" value="开心" />
            <el-option label="😢 伤心" value="伤心" />
            <el-option label="😡 生气" value="生气" />
            <el-option label="😌 平静" value="平静" />
            <el-option label="🤔 思考" value="思考" />
            <el-option label="🥰 幸福" value="幸福" />
          </el-select>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="handleSearch"
          />
        </div>
      </div>

      <!-- Archive Sidebar (Desktop only) -->
      <div class="content-layout">
        <div class="diary-grid">
          <div v-if="loading" class="loading-state">
            <p>加载中...</p>
          </div>
          <div v-else-if="diaries.length === 0" class="empty-state">
            <div class="empty-icon">&#x1F4D6;</div>
            <p class="empty-title">还没有日记</p>
            <p class="empty-desc">写下你的第一篇日记吧</p>
          </div>
          <div v-else class="card-grid">
            <div
              v-for="diary in diaries"
              :key="diary.id"
              class="diary-card"
              @click="goDetail(diary.id)"
            >
              <div class="card-header">
                <span class="card-date">{{ formatDate(diary.createTime) }}</span>
                <span class="card-weather">{{ getWeatherIcon(diary.weather) }}</span>
              </div>
              <h3 class="card-title">{{ diary.title }}</h3>
              <p class="card-content">{{ truncateContent(diary.content) }}</p>
              <div class="card-footer">
                <span class="card-mood">{{ getMoodIcon(diary.mood) }} {{ diary.mood }}</span>
              </div>
            </div>
          </div>

          <div class="pagination-wrap" v-if="total > pageSize">
            <el-pagination
              background
              layout="prev, pager, next"
              :total="total"
              :page-size="pageSize"
              :current-page="currentPage"
              @current-change="handlePageChange"
            />
          </div>
        </div>

        <!-- Archive Sidebar -->
        <aside class="archive-sidebar">
          <div class="archive-card">
            <h3 class="archive-title">归档</h3>
            <div v-if="archiveLoading" class="archive-loading">加载中...</div>
            <div v-else-if="archives.length === 0" class="archive-empty">暂无日记</div>
            <div v-else class="archive-list">
              <div
                v-for="item in archives"
                :key="item.yearMonth"
                class="archive-item"
                @click="selectArchive(item.yearMonth)"
              >
                <span class="archive-month">{{ item.yearMonth }}</span>
                <span class="archive-count">{{ item.count }} 篇</span>
              </div>
            </div>
          </div>
        </aside>
      </div>

      <!-- FAB -->
      <button class="fab" @click="$router.push('/write')" title="写日记">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
          <line x1="12" y1="5" x2="12" y2="19" stroke="white" stroke-width="2.5" stroke-linecap="round"/>
          <line x1="5" y1="12" x2="19" y2="12" stroke="white" stroke-width="2.5" stroke-linecap="round"/>
        </svg>
      </button>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getList, getArchive } from '@/api/diary'
import AppHeader from '@/components/AppHeader.vue'

const router = useRouter()
const diaries = ref([])
const archives = ref([])
const loading = ref(false)
const archiveLoading = ref(false)
const total = ref(0)
const pageSize = ref(10)
const currentPage = ref(1)
const keyword = ref('')
const mood = ref('')
const dateRange = ref([])

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

function truncateContent(content) {
  if (!content) return ''
  return content.length > 120 ? content.substring(0, 120) + '...' : content
}

function getWeatherIcon(weather) {
  const map = { '晴': '☀️', '多云': '⛅', '雨': '🌧️', '雪': '❄️', '风': '🌬️' }
  return map[weather] || '☀️'
}

function getMoodIcon(mood) {
  const map = { '开心': '😊', '伤心': '😢', '生气': '😡', '平静': '😌', '思考': '🤔', '幸福': '🥰' }
  return map[mood] || '😊'
}

function goDetail(id) {
  router.push(`/detail/${id}`)
}

async function fetchDiaries() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: keyword.value,
      mood: mood.value,
      startDate: dateRange.value?.[0] || '',
      endDate: dateRange.value?.[1] || ''
    }
    const res = await getList(params)
    if (res.data.code === 200) {
      const d = res.data.data
      diaries.value = d.records || []
      total.value = d.total || 0
    }
  } catch (e) {
    console.error('Failed to fetch diaries', e)
  } finally {
    loading.value = false
  }
}

async function fetchArchives() {
  archiveLoading.value = true
  try {
    const res = await getArchive()
    if (res.data.code === 200) {
      archives.value = res.data.data || []
    }
  } catch (e) {
    console.error('Failed to fetch archives', e)
  } finally {
    archiveLoading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  fetchDiaries()
}

function handlePageChange(page) {
  currentPage.value = page
  fetchDiaries()
}

function selectArchive(yearMonth) {
  const [year, month] = yearMonth.split('-')
  const lastDay = new Date(year, month, 0).getDate()
  dateRange.value = [`${yearMonth}-01`, `${yearMonth}-${String(lastDay).padStart(2, '0')}`]
  keyword.value = ''
  mood.value = ''
  handleSearch()
}

onMounted(() => {
  fetchDiaries()
  fetchArchives()
})
</script>

<style scoped>
.diary-list-page {
  min-height: 100vh;
  background: var(--bg-main);
}

.main-content {
  max-width: 960px;
  margin: 0 auto;
  padding: 80px 20px 80px;
}

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 24px;
  align-items: center;
}

.search-box {
  flex: 1;
  min-width: 200px;
}

.search-icon {
  font-size: 16px;
}

.filter-options {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.filter-options .el-select {
  width: 130px;
}

.filter-options .el-date-editor {
  width: 250px;
}

.content-layout {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.diary-grid {
  flex: 1;
  min-width: 0;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.diary-card {
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 20px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
  cursor: pointer;
  transition: var(--transition);
}

.diary-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.card-date {
  font-size: 13px;
  color: var(--text-muted);
}

.card-weather {
  font-size: 18px;
}

.card-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
  line-height: 1.4;
  font-family: var(--font-heading);
}

.card-content {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 12px;
}

.card-footer {
  display: flex;
  align-items: center;
}

.card-mood {
  font-size: 13px;
  color: var(--text-muted);
  background: var(--accent-light);
  padding: 3px 10px;
  border-radius: 20px;
}

/* Archive Sidebar */
.archive-sidebar {
  width: 200px;
  flex-shrink: 0;
  position: sticky;
  top: 80px;
}

.archive-card {
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 20px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
}

.archive-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
  font-family: var(--font-heading);
}

.archive-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.archive-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: var(--transition);
  font-size: 13px;
}

.archive-item:hover {
  background: var(--accent-light);
  color: var(--accent);
}

.archive-month {
  color: var(--text-secondary);
  font-weight: 500;
}

.archive-count {
  color: var(--text-muted);
  font-size: 12px;
}

.archive-loading,
.archive-empty {
  font-size: 13px;
  color: var(--text-muted);
  text-align: center;
  padding: 20px 0;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-title {
  font-size: 18px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.empty-desc {
  font-size: 14px;
  color: var(--text-muted);
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

.fab {
  position: fixed;
  right: 32px;
  bottom: 40px;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: var(--accent);
  border: none;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(200, 149, 108, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--transition);
  z-index: 100;
}

.fab:hover {
  background: var(--accent-hover);
  transform: scale(1.08);
  box-shadow: 0 6px 24px rgba(200, 149, 108, 0.45);
}

@media (max-width: 768px) {
  .main-content {
    padding: 70px 16px 80px;
  }

  .card-grid {
    grid-template-columns: 1fr;
  }

  .content-layout {
    flex-direction: column;
  }

  .archive-sidebar {
    display: none;
  }

  .filter-bar {
    flex-direction: column;
  }

  .filter-options {
    width: 100%;
  }

  .filter-options .el-select {
    width: 100%;
  }

  .filter-options .el-date-editor {
    width: 100%;
  }

  .fab {
    right: 20px;
    bottom: 30px;
    width: 50px;
    height: 50px;
  }
}
</style>