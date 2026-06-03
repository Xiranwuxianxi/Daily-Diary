<template>
  <div class="diary-list-page">
    <AppHeader />

    <main class="main-content">
      <!-- Greeting Header -->
      <div class="greeting-bar">
        <h2 class="greeting-text">{{ greeting }}，今天想记录什么？</h2>
        <p class="greeting-date">{{ todayStr }}</p>
      </div>

      <!-- Search & Filter Bar -->
      <div class="filter-bar">
        <div class="search-box">
          <el-input
            v-model="keyword"
            placeholder="搜索日记标题或内容..."
            clearable
            @input="handleSearch"
            @clear="handleSearch"
          >
            <template #prefix>
              <svg class="search-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8"/>
                <line x1="21" y1="21" x2="16.65" y2="16.65"/>
              </svg>
            </template>
          </el-input>
        </div>
        <div class="filter-options">
          <el-select
            v-model="mood"
            placeholder="全部心情"
            clearable
            @change="handleSearch"
            size="default"
          >
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

      <!-- Content Layout: Sidebar + List -->
      <div class="content-layout">
        <!-- Archive Sidebar (Desktop) -->
        <ArchiveSidebar
          :selectedMonth="selectedArchive"
          :mobileOpen="archiveOpen"
          @select="selectArchive"
          @toggle="archiveOpen = !archiveOpen"
        />

        <!-- Diary Grid -->
        <div class="diary-grid">
          <!-- Loading Skeleton -->
          <SkeletonCard v-if="loading" :count="4" />

          <!-- Empty State -->
          <div v-else-if="diaries.length === 0" class="empty-state">
            <div class="empty-illustration">
              <svg width="120" height="120" viewBox="0 0 120 120" fill="none">
                <rect x="25" y="15" width="70" height="90" rx="8" fill="#F5EDE4" stroke="#C8956C" stroke-width="1.5"/>
                <line x1="38" y1="38" x2="82" y2="38" stroke="#C8956C" stroke-width="2" stroke-linecap="round"/>
                <line x1="38" y1="50" x2="82" y2="50" stroke="#C8956C" stroke-width="2" stroke-linecap="round"/>
                <line x1="38" y1="62" x2="68" y2="62" stroke="#C8956C" stroke-width="2" stroke-linecap="round"/>
                <circle cx="85" cy="85" r="22" fill="#C8956C" opacity="0.12"/>
                <line x1="85" y1="78" x2="85" y2="92" stroke="#C8956C" stroke-width="2.5" stroke-linecap="round"/>
                <line x1="78" y1="85" x2="92" y2="85" stroke="#C8956C" stroke-width="2.5" stroke-linecap="round"/>
              </svg>
            </div>
            <p class="empty-title">还没有日记</p>
            <p class="empty-desc">点击右下角按钮，写下你的第一篇日记吧 ✍️</p>
          </div>

          <!-- Diary Cards -->
          <div v-else class="card-grid">
            <DiaryCard
              v-for="(diary, idx) in diaries"
              :key="diary.id"
              :diary="diary"
              :delay="idx"
              @click="goDetail(diary.id)"
              @edit="goEdit"
              @delete="handleDelete"
            />
          </div>

          <!-- Pagination -->
          <div class="pagination-wrap" v-if="total > pageSize && !loading">
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
      </div>

      <!-- FAB: Write Diary -->
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getList, getArchive, remove } from '@/api/diary'
import AppHeader from '@/components/AppHeader.vue'
import ArchiveSidebar from '@/components/ArchiveSidebar.vue'
import DiaryCard from '@/components/DiaryCard.vue'
import SkeletonCard from '@/components/SkeletonCard.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const diaries = ref([])
const archives = ref([])
const loading = ref(false)
const total = ref(0)
const pageSize = ref(10)
const currentPage = ref(1)
const keyword = ref('')
const mood = ref('')
const dateRange = ref([])
const selectedArchive = ref('')
const archiveOpen = ref(false)

const todayStr = computed(() => {
  const now = new Date()
  const y = now.getFullYear()
  const m = String(now.getMonth() + 1).padStart(2, '0')
  const d = String(now.getDate()).padStart(2, '0')
  const weekDays = ['日', '一', '二', '三', '四', '五', '六']
  return `${y}年${m}月${d}日 星期${weekDays[now.getDay()]}`
})

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '夜深了 🌙'
  if (h < 9) return '早上好 ☀️'
  if (h < 12) return '上午好 🌤️'
  if (h < 14) return '中午好 🌞'
  if (h < 18) return '下午好 🌅'
  if (h < 22) return '晚上好 🌙'
  return '夜深了 🌙'
})

function goDetail(id) {
  router.push(`/detail/${id}`)
}

function goEdit(id) {
  router.push(`/write/${id}`)
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定要删除这篇日记吗？删除后无法恢复。', '确认删除', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
      confirmButtonClass: 'el-button--danger'
    })
    await remove(id)
    ElMessage.success('删除成功')
    fetchDiaries()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
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
    ElMessage.error('加载日记失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  selectedArchive.value = ''
  fetchDiaries()
}

function handlePageChange(page) {
  currentPage.value = page
  fetchDiaries()
}

function selectArchive(yearMonth) {
  selectedArchive.value = yearMonth
  const [year, month] = yearMonth.split('-')
  const lastDay = new Date(year, month, 0).getDate()
  dateRange.value = [`${yearMonth}-01`, `${yearMonth}-${String(lastDay).padStart(2, '0')}`]
  keyword.value = ''
  mood.value = ''
  handleSearch()
}

onMounted(() => {
  fetchDiaries()
})
</script>

<style scoped>
.diary-list-page {
  min-height: 100vh;
  background: var(--bg-main);
}

.main-content {
  max-width: 1100px;
  margin: 0 auto;
  padding: 80px 24px 80px;
}

/* Greeting */
.greeting-bar {
  margin-bottom: 28px;
}

.greeting-text {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  font-family: var(--font-heading);
  margin-bottom: 4px;
}

.greeting-date {
  font-size: 14px;
  color: var(--text-muted);
}

/* Filter Bar */
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 24px;
  align-items: center;
}

.search-box {
  flex: 1;
  min-width: 220px;
  max-width: 400px;
}

.search-icon {
  color: var(--text-muted);
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

/* Content Layout */
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

/* Empty State */
.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-illustration {
  margin-bottom: 20px;
  opacity: 0.85;
}

.empty-title {
  font-size: 18px;
  color: var(--text-secondary);
  margin-bottom: 8px;
  font-weight: 600;
}

.empty-desc {
  font-size: 14px;
  color: var(--text-muted);
}

/* Pagination */
.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

/* FAB */
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

  .greeting-text {
    font-size: 18px;
  }

  .card-grid {
    grid-template-columns: 1fr;
  }

  .content-layout {
    flex-direction: column;
  }

  .filter-bar {
    flex-direction: column;
  }

  .filter-options {
    width: 100%;
  }

  .filter-options .el-select,
  .filter-options .el-date-editor {
    width: 100%;
  }

  .search-box {
    max-width: 100%;
  }

  .fab {
    right: 20px;
    bottom: 30px;
    width: 50px;
    height: 50px;
  }
}
</style>
