<template>
  <div class="write-page">
    <AppHeader />
    <main class="editor-container">
      <div class="editor-card">
        <!-- Top Meta Row -->
        <div class="meta-row">
          <div class="meta-item date-display">
            <span class="meta-icon">&#x1F4C5;</span>
            <span>{{ today }}</span>
          </div>
        </div>

        <!-- Mood & Weather Pickers -->
        <MoodPicker v-model="form.mood" />

        <div class="weather-picker">
          <span class="picker-label">天气</span>
          <div class="picker-options">
            <button
              v-for="w in weathers"
              :key="w.value"
              :class="['option-btn', { active: form.weather === w.value }]"
              @click="form.weather = w.value"
              :title="w.label"
            >{{ w.icon }} {{ w.label }}</button>
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

        <!-- Actions -->
        <div class="actions">
          <button class="btn-cancel" @click="$router.back()">取消</button>
          <button class="btn-save" @click="handleSave" :disabled="saving">
            {{ saving ? '保存中...' : '保存日记' }}
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { create, update, getById } from '@/api/diary'
import AppHeader from '@/components/AppHeader.vue'
import MoodPicker from '@/components/MoodPicker.vue'

const router = useRouter()
const route = useRoute()
const editId = route.params.id
const saving = ref(false)

const today = new Date().toISOString().split('T')[0]

const weathers = [
  { value: '晴', icon: '☀️', label: '晴' },
  { value: '多云', icon: '⛅', label: '多云' },
  { value: '雨', icon: '🌧️', label: '雨' },
  { value: '雪', icon: '❄️', label: '雪' },
  { value: '风', icon: '🌬️', label: '风' }
]

const form = reactive({
  title: '',
  content: '',
  mood: '开心',
  weather: '晴',
  tags: '',
  isPublic: false
})

async function handleSave() {
  if (!form.title.trim()) {
    alert('请输入标题')
    return
  }
  if (!form.content.trim()) {
    alert('请输入内容')
    return
  }

  saving.value = true
  try {
    const data = {
      title: form.title.trim(),
      content: form.content.trim(),
      mood: form.mood,
      weather: form.weather,
      tags: form.tags,
      isPublic: form.isPublic
    }

    if (editId) {
      await update(editId, data)
    } else {
      await create(data)
    }
    router.push('/')
  } catch (e) {
    alert(e.response?.data?.message || '保存失败')
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
      form.title = d.title || ''
      form.content = d.content || ''
      form.mood = d.mood || '开心'
      form.weather = d.weather || '晴'
      form.tags = d.tags || ''
      form.isPublic = d.isPublic === 1
    }
  } catch (e) {
    alert('加载日记失败')
    router.push('/')
  }
}

onMounted(() => {
  loadDiary()
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
  padding: 80px 20px 60px;
}

.editor-card {
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 32px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
}

.meta-row {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

.date-display {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: var(--text-secondary);
  font-weight: 500;
}

.meta-icon {
  font-size: 18px;
}

.weather-picker {
  margin-bottom: 28px;
}

.picker-label {
  display: block;
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 10px;
  font-weight: 500;
}

.picker-options {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.option-btn {
  padding: 6px 14px;
  border: 1px solid var(--border);
  border-radius: 20px;
  background: var(--bg-card);
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: var(--transition);
  font-family: inherit;
}

.option-btn:hover {
  border-color: var(--accent);
  color: var(--accent);
}

.option-btn.active {
  background: var(--accent-light);
  border-color: var(--accent);
  color: var(--accent);
  font-weight: 500;
}

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

.content-wrap {
  margin-bottom: 28px;
}

.content-textarea {
  width: 100%;
  min-height: 360px;
  border: none;
  outline: none;
  resize: vertical;
  font-size: 16px;
  line-height: 1.9;
  color: var(--text-primary);
  font-family: inherit;
  background: transparent;
  padding: 8px 0;
}

.content-textarea::placeholder {
  color: var(--text-muted);
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid var(--border-light);
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

@media (max-width: 768px) {
  .editor-container {
    padding: 70px 0 40px;
  }

  .editor-card {
    border-radius: 0;
    padding: 24px 18px;
    box-shadow: none;
    border: none;
  }

  .title-input {
    font-size: 22px;
  }

  .content-textarea {
    min-height: 300px;
    font-size: 15px;
  }

  .actions {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background: var(--bg-card);
    padding: 12px 18px;
    border-top: 1px solid var(--border);
    z-index: 90;
  }
}
</style>