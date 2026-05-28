<template>
  <div class="mood-picker">
    <span class="picker-label">心情</span>
    <div class="picker-options">
      <button
        v-for="m in moods"
        :key="m.value"
        :class="['option-btn', { active: modelValue === m.value }]"
        @click="$emit('update:modelValue', m.value)"
        :title="m.label"
      >
        <span class="mood-icon">{{ m.icon }}</span>
        <span class="mood-label">{{ m.label }}</span>
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  modelValue: {
    type: String,
    default: '开心'
  }
})

defineEmits(['update:modelValue'])

const moods = [
  { value: '开心', icon: '😊', label: '开心' },
  { value: '伤心', icon: '😢', label: '伤心' },
  { value: '生气', icon: '😡', label: '生气' },
  { value: '平静', icon: '😌', label: '平静' },
  { value: '思考', icon: '🤔', label: '思考' },
  { value: '幸福', icon: '🥰', label: '幸福' }
]
</script>

<style scoped>
.mood-picker {
  margin-bottom: 24px;
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
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  border: 1px solid var(--border);
  border-radius: 20px;
  background: var(--bg-card);
  cursor: pointer;
  transition: var(--transition);
  font-family: inherit;
}

.option-btn:hover {
  border-color: var(--accent);
  background: var(--accent-light);
}

.option-btn.active {
  background: var(--accent-light);
  border-color: var(--accent);
  font-weight: 500;
}

.mood-icon {
  font-size: 18px;
}

.mood-label {
  font-size: 13px;
  color: var(--text-secondary);
}

@media (max-width: 768px) {
  .picker-options {
    gap: 6px;
  }

  .option-btn {
    padding: 6px 12px;
  }
}
</style>