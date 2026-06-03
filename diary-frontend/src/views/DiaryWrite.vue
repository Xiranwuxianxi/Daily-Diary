<template>
  <div class="write-page">
    <AppHeader />
    <main class="editor-container">
      <div class="editor-card">
        <!-- Date -->
        <div class="meta-row">
          <label class="meta-label">📅 日期</label>
          <input type="date" v-model="form.date" class="date-input" />
        </div>

        <!-- Mood Picker - Big Emoji -->
        <div class="mood-section">
          <label class="meta-label">心情</label>
          <div class="mood-picker">
            <button
              v-for="m in moods"
              :key="m.value"
              :class="['mood-btn', { active: form.mood === m.value }]"
              @click="form.mood = m.value"
            >
              <span class="mood-emoji">{{ m.emoji }}</span>
              <span class="mood-label">{{ m.label }}</span>
            </button>
          </div>
        </div>

        <!-- Weather Picker -->
        <div class="weather-section">
          <label class="meta-label">天气</label>
          <div class="weather-picker">
            <button
              v-for="w in weathers"
              :key="w.value"
              :class="['weather-btn', { active: form.weather === w.value }]"
              @click="form.weather = w.value"
            >
              <span class="weather-icon">{{ w.icon }}</span>
              <span class="weather-label">{{ w.label }}</span>
            </button>
          </div>
        </div>

        <!-- Tags -->
        <div class="tags-section">
          <label class="meta-label">🏷️ 标签</label>
          <div class="tags-input-wrap">
            <div class="tags-display">
              <span
                v-for="(tag, idx) in tagList"
                :key="idx"
                class="tag-item"
              >
                {{ tag }}
                <button class="tag-remove" @click="removeTag(idx)">&times;</button>
              </span>
              <input
                v-model="tagInput"
                type="text"
                class="tag-input"
                placeholder="输入标签后回车"
                @keydown.enter.prevent="addTag"
                @keydown.backspace="handleBackspace"
              />
            </div>
          </div>
        </div>

        <!-- Title -->
        <div class="title-wrap">
          <input
            v-model="form.title"
            type="text"
            class="title-input"
            placeholder="给今天起个标题..."
            maxlength="200"
          />
        </div>

        <!-- Content -->
        <div class="content-wrap">
          <textarea
            v-model="form.content"
            class="content-textarea"
            placeholder="今天发生了什么有趣的事..."
          ></textarea>
        </div>
      </div>
    </main>

    <!-- Floating Save Bar -->
    <div class="save-bar">
      <div class="save-bar-inner">
        <span class="save-status" v-if="savedHint">{{ savedHint }}</span>
        <div class="save-actions">
          <button class="btn-cancel" @click="goBack">取消</button>
          <button class="btn-save" @click="handleSave" :disabled="saving">
            {{ saving ? '保存中...' : (editId ? '更新日记' : '保存日记') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { create, update, getById } from '@/api/diary'
import AppHeader from '@/components/AppHeader.vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const editId = route.params.id
const saving = ref(false)
const savedHint = ref('')
const tagInput = ref('')
const AUTO_SAVE_KEY = 'diary_draft'

const moods = [
  { value: '开心', emoji: '😊', label: '开心' },
  { value: '幸福', emoji: '🥰', label: '幸福' },
  { value: '平静', emoji: '😌', label: '平静' },
  { value: '思考', emoji: '🤔', label: '思考' },
  { value: '伤心', emoji: '😢', label: '伤心' },
  { value: '生气', emoji: '😡', label: '生气' }
]

const weathers = [
  { value: '晴', icon: '☀️', label: '晴' },
  { value: '多云', icon: '⛅', label: '多云' },
  { value: '阴', icon: '☁️', label: '阴' },
  { value: '雨', icon: '🌧️', label: '雨' },
  { value: '雪', icon: '❄️', label: '雪' },
  { value: '风', icon: '🌬️', label: '风' }
]

const getTodayStr = () => new Date().toISOString().split('T')[0]

const form = reactive({
  date: getTodayStr(),
  title: '',
  content: '',
  mood: '开心',
  weather: '晴',
  tags: '',
  isPublic: false
})

const tagList = ref([])

function addTag() {
  const val = tagInput.value.trim()
  if (!val) return
  if (tagList.value.includes(val)) {
    tagInput.value = ''
    return
  }
  if (tagList.value.length >= 5) {
    ElMessage.warning('最多添加 5 个标签')
    return
  }
  tagList.value.push(val)
  tagInput.value = ''
}

function removeTag(idx) {
  tagList.value.splice(idx, 1)
}

function handleBackspace() {
  if (!tagInput.value && tagList.value.length > 0) {
    tagList.value.pop()
  }
}

// Load draft from localStorage (only for new diary)
function loadDraft() {
  if (editId) return
  const saved = localStorage.getItem(AUTO_SAVE_KEY)
  if (saved) {
    try {
      const draft = JSON.parse(saved)
      if (draft.date === getTodayStr()) {
        form.date = draft.date || getTodayStr()
        form.title = draft.title || ''
        form.content = draft.content || ''
        form.mood = draft.mood || '开心'
        form.weather = draft.weather || '晴'
        if (draft.tags) {
          tagList.value = draft.tags.split(',').filter(Boolean)
        }
      }
    } catch (e) {}
  }
}

// Auto-save
let autoSaveTimer = null

function autoSave() {
  if (editId) return
  const draft = {
    date: form.date,
    title: form.title,
    content: form.content,
    mood: form.mood,
    weather: form.weather,
    tags: tagList.value.join(',')
  }
  localStorage.setItem(AUTO_SAVE_KEY, JSON.stringify(draft))
}

function startAutoSave() {
  autoSaveTimer = setInterval(autoSave, 30000)
}

function stopAutoSave() {
  if (autoSaveTimer) clearInterval(autoSaveTimer)
}

// Watch for changes and autosave
watch(
  () => [form.title, form.content, form.mood, form.weather, tagList.value],
  () => {
    autoSave()
  },
  { deep: true }
)

function goBack() {
  router.back()
}

async function handleSave() {
  if (!form.title.trim()) {
    ElMessage.warning('请输入标题')
    return
  }
  if (!form.content.trim()) {
    ElMessage.warning('请输入内容')
    return
  }

  saving.value = true
  try {
    const data = {
      title: form.title.trim(),
      content: form.content.trim(),
      mood: form.mood,
      weather: form.weather,
      tags: tagList.value.join(','),
      isPublic: form.isPublic
    }

    if (editId) {
      await update(editId, data)
    } else {
      await create(data)
      localStorage.removeItem(AUTO_SAVE_KEY)
    }
    ElMessage.success('保存成功')
    router.push('/')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function loadDiary() {
  if (!editId) return
  try {
    const res = await getById(editId)
    if (res.data.code === 200) {
      const d = res.data.data
      form.date = d.createTime ? d.createTime.split('T')[0] : getTodayStr()
      form.title = d.title || ''
      form.content = d.content || ''
      form.mood = d.mood || '开心'
      form.weather = d.weather || '晴'
      if (d.tags) {
        tagList.value = d.tags.split(',').map(t => t.trim()).filter(Boolean)
      }
      form.isPublic = d.isPublic === 1
    }
  } catch (e) {
    ElMessage.error('加载日记失败')
    router.push('/')
  }
}

onMounted(() => {
  loadDraft()
  loadDiary()
  startAutoSave()
})

onUnmounted(() => {
  stopAutoSave()
})
</script>

<style scoped>
.write-page {
  min-height: 100vh;
  background: var(--bg-main);
}

.editor-container {
  max-width: 720px;
  margin: 0 auto;
  padding: 80px 24px 160px;
}

.editor-card {
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 36px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
}

/* Meta Row */
.meta-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.meta-label {
  font-size: 13px;
  color: var(--text-muted);
  font-weight: 500;
  width: 48px;
  flex-shrink: 0;
}

.date-input {
  padding: 8px 12px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  font-size: 15px;
  color: var(--text-primary);
  background: var(--bg-main);
  font-family: inherit;
  outline: none;
  transition: var(--transition);
}

.date-input:focus {
  border-color: var(--accent);
}

/* Mood Section */
.mood-section {
  margin-bottom: 24px;
}

.mood-picker {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 8px;
}

.mood-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 10px 14px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  background: var(--bg-card);
  cursor: pointer;
  transition: var(--transition);
  font-family: inherit;
  min-width: 64px;
}

.mood-btn:hover {
  border-color: var(--accent);
  background: var(--accent-light);
}

.mood-btn.active {
  border-color: var(--accent);
  background: var(--accent-light);
  transform: scale(1.05);
}

.mood-btn.active .mood-label {
  color: var(--accent);
  font-weight: 600;
}

.mood-emoji {
  font-size: 24px;
  transition: transform 0.2s ease;
}

.mood-btn.active .mood-emoji {
  transform: scale(1.2);
}

.mood-label {
  font-size: 11px;
  color: var(--text-muted);
}

/* Weather Section */
.weather-section {
  margin-bottom: 24px;
}

.weather-picker {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 8px;
}

.weather-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 8px 12px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  background: var(--bg-card);
  cursor: pointer;
  transition: var(--transition);
  font-family: inherit;
  min-width: 56px;
}

.weather-btn:hover {
  border-color: var(--accent);
  background: var(--accent-light);
}

.weather-btn.active {
  border-color: var(--accent);
  background: var(--accent-light);
}

.weather-btn.active .weather-label {
  color: var(--accent);
  font-weight: 600;
}

.weather-icon {
  font-size: 20px;
}

.weather-label {
  font-size: 11px;
  color: var(--text-muted);
}

/* Tags */
.tags-section {
  margin-bottom: 24px;
}

.tags-input-wrap {
  margin-top: 8px;
}

.tags-display {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  padding: 8px 12px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  background: var(--bg-main);
  min-height: 42px;
  transition: var(--transition);
}

.tags-display:focus-within {
  border-color: var(--accent);
}

.tag-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 3px 10px;
  background: var(--accent-light);
  color: var(--accent);
  border-radius: 14px;
  font-size: 13px;
  font-weight: 500;
}

.tag-remove {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: none;
  background: transparent;
  color: var(--accent);
  cursor: pointer;
  font-size: 14px;
  line-height: 1;
  padding: 0;
}

.tag-remove:hover {
  background: rgba(200, 149, 108, 0.2);
}

.tag-input {
  flex: 1;
  min-width: 100px;
  border: none;
  outline: none;
  font-size: 14px;
  color: var(--text-primary);
  background: transparent;
  font-family: inherit;
  padding: 4px 0;
}

.tag-input::placeholder {
  color: var(--text-muted);
}

/* Title */
.title-wrap {
  margin-bottom: 24px;
}

.title-input {
  width: 100%;
  font-size: 28px;
  font-weight: 600;
  font-family: var(--font-heading);
  border: none;
  border-bottom: 2px solid var(--border-light);
  padding: 8px 0 12px;
  outline: none;
  background: transparent;
  color: var(--text-primary);
  transition: var(--transition);
}

.title-input:focus {
  border-bottom-color: var(--accent);
}

.title-input::placeholder {
  color: var(--text-muted);
  font-weight: 400;
}

/* Content */
.content-wrap {
  margin-bottom: 8px;
}

.content-textarea {
  width: 100%;
  min-height: 380px;
  border: none;
  outline: none;
  resize: vertical;
  font-size: 16px;
  line-height: 1.8;
  color: var(--text-primary);
  font-family: inherit;
  background: transparent;
  padding: 8px 0;
}

.content-textarea::placeholder {
  color: var(--text-muted);
}

/* Floating Save Bar */
.save-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-top: 1px solid var(--border-light);
  z-index: 100;
  padding: 12px 24px;
}

.save-bar-inner {
  max-width: 720px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
}

.save-status {
  font-size: 12px;
  color: var(--text-muted);
  margin-right: auto;
}

.save-actions {
  display: flex;
  gap: 10px;
}

.btn-cancel {
  padding: 10px 24px;
  background: var(--bg-main);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  font-size: 15px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: var(--transition);
  font-family: inherit;
}

.btn-cancel:hover {
  background: var(--border);
}

.btn-save {
  padding: 10px 28px;
  background: var(--accent);
  border: none;
  border-radius: var(--radius-sm);
  font-size: 15px;
  color: #fff;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition);
  font-family: inherit;
}

.btn-save:hover {
  background: var(--accent-hover);
}

.btn-save:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Mobile */
@media (max-width: 768px) {
  .editor-container {
    padding: 70px 0 160px;
  }

  .editor-card {
    border-radius: 0;
    padding: 20px 16px;
    box-shadow: none;
    border: none;
  }

  .title-input {
    font-size: 22px;
  }

  .content-textarea {
    min-height: 320px;
    font-size: 15px;
  }

  .mood-picker {
    gap: 6px;
  }

  .mood-btn {
    padding: 8px 10px;
    min-width: 52px;
  }

  .mood-emoji {
    font-size: 20px;
  }

  .save-bar {
    padding: 10px 16px;
  }
}
</style>