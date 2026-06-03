<template>
  <div
    class="diary-card"
    :style="{ animationDelay: `${delay * 0.05}s` }"
    @click="$emit('click')"
  >
    <div class="card-header">
      <span class="card-date">{{ formatDate(diary.createTime) }}</span>
      <div class="card-badges">
        <span class="card-weather" :title="diary.weather">{{ getWeatherIcon(diary.weather) }}</span>
        <span class="card-mood-icon" :title="diary.mood">{{ getMoodIcon(diary.mood) }}</span>
      </div>
    </div>

    <h3 class="card-title">{{ diary.title }}</h3>

    <p class="card-content">{{ truncateContent(diary.content) }}</p>

    <div class="card-tags" v-if="diary.tags">
      <span
        v-for="tag in parseTags(diary.tags)"
        :key="tag"
        class="tag-chip"
      >{{ tag }}</span>
    </div>

    <div class="card-actions" @click.stop>
      <button class="action-btn edit-btn" @click="$emit('edit', diary.id)" title="编辑">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/>
        </svg>
      </button>
      <button class="action-btn delete-btn" @click="$emit('delete', diary.id)" title="删除">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="3 6 5 6 21 6"/>
          <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  diary: { type: Object, required: true },
  delay: { type: Number, default: 0 }
})

defineEmits(['click', 'edit', 'delete'])

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const weekDays = ['日', '一', '二', '三', '四', '五', '六']
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const w = weekDays[d.getDay()]
  return `${y}-${m}-${day} 星期${w}`
}

function truncateContent(content) {
  if (!content) return ''
  return content.length > 100 ? content.substring(0, 100) + '...' : content
}

function getWeatherIcon(weather) {
  const map = { '晴': '☀️', '多云': '⛅', '雨': '🌧️', '雪': '❄️', '风': '🌬️' }
  return map[weather] || '☀️'
}

function getMoodIcon(mood) {
  const map = { '开心': '😊', '伤心': '😢', '生气': '😡', '平静': '😌', '思考': '🤔', '幸福': '🥰' }
  return map[mood] || '😊'
}

function parseTags(tags) {
  if (!tags) return []
  if (Array.isArray(tags)) return tags.slice(0, 3)
  const arr = tags.split(',').map(t => t.trim()).filter(Boolean)
  return arr.slice(0, 3)
}
</script>

<style scoped>
.diary-card {
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 20px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
  cursor: pointer;
  transition: var(--transition);
  animation: fadeInUp 0.45s ease backwards;
  position: relative;
}

.diary-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-3px);
  border-color: var(--accent-light);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.card-date {
  font-size: 12px;
  color: var(--text-muted);
  font-weight: 500;
}

.card-badges {
  display: flex;
  gap: 6px;
  font-size: 16px;
}

.card-weather,
.card-mood-icon {
  line-height: 1;
}

.card-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
  line-height: 1.4;
  font-family: var(--font-heading);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
  min-height: 42px;
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}

.tag-chip {
  font-size: 11px;
  color: var(--accent);
  background: var(--accent-light);
  padding: 2px 10px;
  border-radius: 12px;
  font-weight: 500;
}

.card-actions {
  display: flex;
  gap: 4px;
  padding-top: 12px;
  border-top: 1px solid var(--border-light);
  opacity: 0;
  transition: opacity 0.2s ease;
}

.diary-card:hover .card-actions {
  opacity: 1;
}

.action-btn {
  padding: 6px 8px;
  background: none;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  color: var(--text-muted);
  transition: var(--transition);
  display: flex;
  align-items: center;
  justify-content: center;
}

.edit-btn:hover {
  color: var(--accent);
  background: var(--accent-light);
}

.delete-btn:hover {
  color: var(--danger);
  background: #FEF2F2;
}
</style>