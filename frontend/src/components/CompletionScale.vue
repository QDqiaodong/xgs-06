<template>
  <div class="completion-scale" :class="[size, { interactive }]">
    <div v-if="showLabel" class="scale-label">
      <span class="label-icon">{{ levelInfo?.icon || '📊' }}</span>
      <span class="label-text">{{ labelText }}</span>
      <span class="label-percent">{{ levelInfo?.percent || 0 }}%</span>
    </div>

    <div class="scale-track" @click="handleTrackClick">
      <div
        class="scale-fill"
        :style="{ width: fillWidth, background: fillGradient }"
      ></div>

      <div class="scale-ticks">
        <div
          v-for="tick in COMPLETION_ORDER"
          :key="tick"
          class="tick-item"
          :class="{
            active: tick <= currentLevel,
            current: tick === currentLevel,
            clickable: interactive
          }"
          @click.stop="handleTickClick(tick)"
        >
          <div
            class="tick-dot"
            :style="getTickStyle(tick)"
          >
            <span v-if="tick === currentLevel" class="tick-current-icon">
              {{ levelInfo?.icon || '•' }}
            </span>
          </div>
          <div v-if="showTickLabels" class="tick-label">
            <span class="tick-name">{{ COMPLETION_LEVELS[tick].shortName }}</span>
            <span class="tick-percent">{{ COMPLETION_LEVELS[tick].percent }}%</span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showNote && noteText" class="scale-note">
      <van-icon name="info-o" class="note-icon" />
      <span class="note-text">{{ noteText }}</span>
    </div>

    <div v-if="showHint && levelInfo" class="scale-hint">
      <span class="hint-icon">💡</span>
      <span class="hint-text">{{ levelInfo.hint }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { COMPLETION_LEVELS, COMPLETION_ORDER, getCompletionInfo } from '@/utils/craftConfig'

const props = defineProps({
  level: {
    type: [Number, String],
    default: 5
  },
  note: {
    type: String,
    default: ''
  },
  size: {
    type: String,
    default: 'medium'
  },
  interactive: {
    type: Boolean,
    default: false
  },
  showLabel: {
    type: Boolean,
    default: true
  },
  showTickLabels: {
    type: Boolean,
    default: true
  },
  showNote: {
    type: Boolean,
    default: false
  },
  showHint: {
    type: Boolean,
    default: false
  },
  customLabel: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:level'])

const currentLevel = computed(() => {
  const l = typeof props.level === 'string' ? parseInt(props.level, 10) : props.level
  if (!l || l < 1) return 1
  if (l > 5) return 5
  return l
})

const levelInfo = computed(() => getCompletionInfo(currentLevel.value))

const fillWidth = computed(() => {
  const percent = levelInfo.value?.percent || 0
  const baseOffset = 2
  return `calc(${baseOffset + percent * 0.96}%)`
})

const fillGradient = computed(() => {
  const colors = {
    1: ['#d9d9d9', '#8c8c8c'],
    2: ['#91d5ff', '#1890ff'],
    3: ['#ffd591', '#fa8c16'],
    4: ['#b7eb8f', '#52c41a'],
    5: ['#95de64', '#237804']
  }
  const pair = colors[currentLevel.value] || colors[5]
  return `linear-gradient(90deg, ${pair[0]}, ${pair[1]})`
})

const labelText = computed(() => {
  if (props.customLabel) return props.customLabel
  const info = levelInfo.value
  if (!info) return '工艺完成度'
  return `${info.name}状态`
})

const noteText = computed(() => props.note?.trim())

const getTickStyle = (tick) => {
  const info = COMPLETION_LEVELS[tick]
  const isActive = tick <= currentLevel.value
  const isCurrent = tick === currentLevel.value
  const base = {
    background: isActive ? info.bgColor : '#f5f5f5',
    borderColor: isActive ? info.borderColor : '#e0e0e0',
    color: isActive ? info.color : '#bfbfbf'
  }
  if (isCurrent) {
    base.boxShadow = `0 0 0 3px ${info.borderColor}33, 0 2px 8px rgba(0,0,0,0.1)`
    base.transform = 'scale(1.15)'
  }
  return base
}

const handleTickClick = (tick) => {
  if (props.interactive) {
    emit('update:level', tick)
  }
}

const handleTrackClick = (e) => {
  if (!props.interactive) return
  const track = e.currentTarget
  const rect = track.getBoundingClientRect()
  const x = e.clientX - rect.left
  const ratio = x / rect.width
  let tick = Math.round(ratio * 5 + 0.2)
  tick = Math.max(1, Math.min(5, tick))
  emit('update:level', tick)
}
</script>

<style scoped>
.completion-scale {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.completion-scale.small {
  gap: 6px;
}

.completion-scale.compact {
  gap: 4px;
}

.scale-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.completion-scale.small .scale-label {
  font-size: 12px;
}

.label-icon {
  font-size: 16px;
}

.completion-scale.small .label-icon {
  font-size: 14px;
}

.label-text {
  flex: 1;
}

.label-percent {
  font-weight: 600;
  color: #8b5a2b;
  font-size: 13px;
}

.completion-scale.small .label-percent {
  font-size: 11px;
}

.scale-track {
  position: relative;
  width: 100%;
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: visible;
  cursor: default;
}

.completion-scale.small .scale-track {
  height: 6px;
}

.completion-scale.interactive .scale-track {
  cursor: pointer;
}

.scale-fill {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  border-radius: 4px;
  transition: width 0.4s ease, background 0.4s ease;
}

.scale-ticks {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  transform: translateY(-50%);
  display: flex;
  justify-content: space-between;
  padding: 0 2%;
  pointer-events: none;
}

.tick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  pointer-events: auto;
}

.tick-item.clickable {
  cursor: pointer;
}

.tick-dot {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  border: 2px solid;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  font-size: 10px;
  z-index: 2;
  background: #fff;
}

.completion-scale.small .tick-dot {
  width: 14px;
  height: 14px;
  font-size: 8px;
}

.tick-current-icon {
  font-size: 10px;
}

.completion-scale.small .tick-current-icon {
  font-size: 8px;
}

.tick-label {
  margin-top: 6px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1px;
  white-space: nowrap;
}

.completion-scale.small .tick-label {
  margin-top: 4px;
}

.tick-name {
  font-size: 11px;
  color: #666;
  font-weight: 500;
}

.completion-scale.small .tick-name {
  font-size: 10px;
}

.tick-item.active .tick-name {
  color: #333;
  font-weight: 600;
}

.tick-item.current .tick-name {
  color: #8b5a2b;
}

.tick-percent {
  font-size: 10px;
  color: #aaa;
}

.completion-scale.small .tick-percent {
  font-size: 9px;
}

.scale-note {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  padding: 10px 12px;
  background: linear-gradient(135deg, #fef9f0 0%, #fff7ed 100%);
  border: 1px solid #ffe7ba;
  border-radius: 8px;
  font-size: 13px;
  line-height: 1.6;
  color: #613400;
}

.completion-scale.small .scale-note {
  padding: 8px 10px;
  font-size: 12px;
}

.note-icon {
  flex-shrink: 0;
  margin-top: 1px;
  color: #d46b08;
  font-size: 14px;
}

.note-text {
  flex: 1;
}

.scale-hint {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  padding: 8px 12px;
  background: #f6ffed;
  border: 1px solid #d9f7be;
  border-radius: 8px;
  font-size: 12px;
  line-height: 1.5;
  color: #389e0d;
}

.hint-icon {
  flex-shrink: 0;
  font-size: 14px;
}

.hint-text {
  flex: 1;
}
</style>
