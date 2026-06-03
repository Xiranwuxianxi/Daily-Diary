<template>
  <aside class="archive-sidebar" :class="{ open: mobileOpen }">
    <!-- Mobile toggle -->
    <button class="archive-toggle" @click="$emit('toggle')" v-if="isMobile">
      📂 归档 {{ mobileOpen ? '▲' : '▼' }}
    </button>

    <div class="archive-card" v-show="!isMobile || mobileOpen">
      <h3 class="archive-title">📅 时间归档</h3>

      <div v-if="loading" class="archive-loading">
        <div class="skeleton-line" v-for="i in 4" :key="i"></div>
      </div>

      <div v-else-if="archives.length === 0" class="archive-empty">
        暂无日记
      </div>

      <div v-else class="archive-list">
        <div
          v-for="item in archives"
          :key="item.yearMonth"
          :class="['archive-item', { active: selectedMonth === item.yearMonth }]"
          @click="$emit('select', item.yearMonth)"
        >
          <span class="archive-month">{{ formatMonth(item.yearMonth) }}</span>
          <span class="archive-count">{{ item.count }} 篇</span>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { getArchive } from '@/api/diary'

defineProps({
  selectedMonth: String,
  mobileOpen: Boolean
})

defineEmits(['select', 'toggle'])

const archives = ref([])
const loading = ref(false)
const isMobile = ref(false)

function checkMobile() {
  isMobile.value = window.innerWidth < 768
}

function formatMonth(ym) {
  const [y, m] = ym.split('-')
  return `${y}年${parseInt(m)}月`
}

async function fetchArchives() {
  loading.value = true
  try {
    const res = await getArchive()
    if (res.data.code === 200) {
      archives.value = res.data.data || []
    }
  } catch (e) {
    console.error('Failed to fetch archives', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
  fetchArchives()
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<style scoped>
.archive-sidebar {
  width: 240px;
  flex-shrink: 0;
  position: sticky;
  top: 80px;
  align-self: flex-start;
}

.archive-toggle {
  display: none;
  width: 100%;
  padding: 10px 16px;
  background: var(--bg-card);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-sm);
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
  margin-bottom: 12px;
  font-family: inherit;
  transition: var(--transition);
}

.archive-toggle:hover {
  border-color: var(--accent);
  color: var(--accent);
}

.archive-card {
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 20px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
}

.archive-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
  font-family: var(--font-heading);
}

.archive-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.archive-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: var(--transition);
  font-size: 13px;
}

.archive-item:hover {
  background: var(--accent-light);
  color: var(--accent);
}

.archive-item.active {
  background: var(--accent);
  color: #fff;
}

.archive-item.active .archive-count {
  color: rgba(255, 255, 255, 0.85);
}

.archive-month {
  font-weight: 500;
  color: var(--text-secondary);
}

.archive-item.active .archive-month {
  color: #fff;
}

.archive-count {
  font-size: 12px;
  color: var(--text-muted);
  background: var(--bg-main);
  padding: 2px 8px;
  border-radius: 10px;
}

.archive-item.active .archive-count {
  background: rgba(255, 255, 255, 0.2);
}

.archive-loading {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 8px 0;
}

.skeleton-line {
  height: 14px;
  background: linear-gradient(90deg, var(--border-light) 25%, var(--accent-light) 50%, var(--border-light) 75%);
  background-size: 200% 100%;
  border-radius: 6px;
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.archive-empty {
  font-size: 13px;
  color: var(--text-muted);
  text-align: center;
  padding: 20px 0;
}

@media (max-width: 768px) {
  .archive-sidebar {
    width: 100%;
    position: static;
  }

  .archive-toggle {
    display: block;
  }

  .archive-card {
    border-radius: var(--radius-sm);
  }
}
</style>
