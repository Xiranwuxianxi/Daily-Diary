<template>
  <div class="skeleton-card" v-for="i in count" :key="i">
    <div class="skeleton-header">
      <div class="skeleton-line short"></div>
      <div class="skeleton-line tiny"></div>
    </div>
    <div class="skeleton-line medium" style="margin-top: 12px;"></div>
    <div class="skeleton-line long" style="margin-top: 8px;"></div>
    <div class="skeleton-line long" style="margin-top: 6px;"></div>
    <div class="skeleton-footer">
      <div class="skeleton-line tiny"></div>
      <div class="skeleton-line tiny"></div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  count: { type: Number, default: 4 }
})
</script>

<style scoped>
.skeleton-card {
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 20px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
  animation: pulse 1.5s infinite;
}

.skeleton-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.skeleton-line {
  background: linear-gradient(90deg, var(--border-light) 25%, var(--accent-light) 50%, var(--border-light) 75%);
  background-size: 200% 100%;
  border-radius: 6px;
  animation: shimmer 1.5s infinite;
}

.skeleton-line.short {
  width: 30%;
  height: 12px;
}

.skeleton-line.medium {
  width: 70%;
  height: 18px;
}

.skeleton-line.long {
  width: 100%;
  height: 14px;
}

.skeleton-line.tiny {
  width: 40px;
  height: 10px;
}

.skeleton-footer {
  display: flex;
  gap: 8px;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid var(--border-light);
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}
</style>
