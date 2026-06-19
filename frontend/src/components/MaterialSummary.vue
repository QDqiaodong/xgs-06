<template>
  <div class="material-summary" :class="{ compact: compact }">
    <template v-if="compact">
      <div class="compact-icons" v-if="isValid && hasAnyMaterial">
        <div class="icon-item" v-if="summaryData.mainMaterials?.length">
          <span class="icon">🐂</span>
          <span class="count">{{ summaryData.mainMaterials.length }}</span>
        </div>
        <div class="icon-item" v-if="summaryData.auxMaterials?.length">
          <span class="icon">🔩</span>
          <span class="count">{{ summaryData.auxMaterials.length }}</span>
        </div>
        <div class="icon-item" v-if="summaryData.tools?.length">
          <span class="icon">🛠️</span>
          <span class="count">{{ summaryData.tools.length }}</span>
        </div>
      </div>
      <div class="compact-fallback" v-else-if="hasFallback">
        <van-icon name="description-o" size="12" />
        <span class="fallback-text">{{ truncateFallback }}</span>
      </div>
    </template>

    <template v-else>
      <template v-if="isValid && hasAnyMaterial">
        <div class="summary-section" v-if="summaryData.mainMaterials?.length">
          <div class="section-header">
            <span class="section-icon">🐂</span>
            <span class="section-title">主材</span>
            <span class="section-count">{{ summaryData.mainMaterials.length }} 项</span>
          </div>
          <div class="material-list">
            <div class="material-item" v-for="(item, idx) in summaryData.mainMaterials" :key="'m-'+idx">
              <div class="item-name">{{ safeName(item) }}</div>
              <div class="item-detail">
                <span class="spec" v-if="item?.spec">{{ item.spec }}</span>
                <span class="quantity" v-if="item?.quantity">{{ item.quantity }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="summary-section" v-if="summaryData.auxMaterials?.length">
          <div class="section-header">
            <span class="section-icon">🔩</span>
            <span class="section-title">辅材</span>
            <span class="section-count">{{ summaryData.auxMaterials.length }} 项</span>
          </div>
          <div class="material-list">
            <div class="material-item" v-for="(item, idx) in summaryData.auxMaterials" :key="'a-'+idx">
              <div class="item-name">{{ safeName(item) }}</div>
              <div class="item-detail">
                <span class="spec" v-if="item?.spec">{{ item.spec }}</span>
                <span class="quantity" v-if="item?.quantity">{{ item.quantity }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="summary-section" v-if="summaryData.tools?.length">
          <div class="section-header">
            <span class="section-icon">🛠️</span>
            <span class="section-title">工具</span>
            <span class="section-count">{{ summaryData.tools.length }} 项</span>
          </div>
          <div class="material-list">
            <div class="material-item" v-for="(item, idx) in summaryData.tools" :key="'t-'+idx">
              <div class="item-name">{{ safeName(item) }}</div>
              <div class="item-detail">
                <span class="spec" v-if="item?.spec">{{ item.spec }}</span>
                <span class="quantity" v-if="item?.quantity">{{ item.quantity }}</span>
              </div>
            </div>
          </div>
        </div>
      </template>

      <div class="fallback-section" v-else-if="hasFallback">
        <div class="fallback-header">
          <van-icon name="description-o" size="16" />
          <span class="fallback-title">材料用料（原始信息）</span>
          <van-tag size="medium" type="warning" plain>格式已兼容</van-tag>
        </div>
        <div class="fallback-content">
          {{ fallbackText }}
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  materialSummary: {
    type: String,
    default: ''
  },
  materialsText: {
    type: String,
    default: ''
  },
  compact: {
    type: Boolean,
    default: false
  }
})

const EMPTY_SUMMARY = { mainMaterials: [], auxMaterials: [], tools: [] }

const ensureArray = (arr) => Array.isArray(arr) ? arr.filter(i => i && typeof i === 'object') : []

const parsedResult = computed(() => {
  if (!props.materialSummary || !props.materialSummary.trim()) {
    return { data: null, valid: false, error: 'empty' }
  }
  try {
    const parsed = JSON.parse(props.materialSummary)
    if (parsed && typeof parsed === 'object') {
      const normalized = {
        mainMaterials: ensureArray(parsed.mainMaterials),
        auxMaterials: ensureArray(parsed.auxMaterials),
        tools: ensureArray(parsed.tools)
      }
      const hasAny = normalized.mainMaterials.length > 0 || normalized.auxMaterials.length > 0 || normalized.tools.length > 0
      return { data: normalized, valid: true, error: null, hasAny }
    }
    return { data: EMPTY_SUMMARY, valid: false, error: 'not_object' }
  } catch (e) {
    return { data: EMPTY_SUMMARY, valid: false, error: 'parse_error' }
  }
})

const isValid = computed(() => parsedResult.value.valid)
const summaryData = computed(() => parsedResult.value.data || EMPTY_SUMMARY)
const hasAnyMaterial = computed(() => parsedResult.value.hasAny)

const fallbackText = computed(() => {
  if (props.materialsText && props.materialsText.trim()) {
    return props.materialsText.trim()
  }
  if (props.materialSummary && props.materialSummary.trim()) {
    return props.materialSummary.trim()
  }
  return ''
})

const hasFallback = computed(() => !!fallbackText.value)

const truncateFallback = computed(() => {
  const text = fallbackText.value
  if (!text) return ''
  return text.length > 30 ? text.slice(0, 30) + '...' : text
})

const safeName = (item) => {
  if (!item) return '未命名'
  const name = item.name || item.material || item.title || item.label
  if (!name || !String(name).trim()) return '未命名'
  return String(name).trim()
}
</script>

<style scoped>
.material-summary.compact .compact-icons {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.material-summary.compact .icon-item {
  display: flex;
  align-items: center;
  gap: 3px;
  background: #f8f4ef;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 11px;
  color: #8b5a2b;
}

.material-summary.compact .icon {
  font-size: 12px;
}

.material-summary.compact .count {
  font-weight: 600;
}

.summary-section {
  background: #fff;
  border-radius: 10px;
  padding: 12px;
  margin-bottom: 10px;
  border: 1px solid #f0e8df;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #f0e8df;
}

.section-icon {
  font-size: 18px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #8b5a2b;
  flex: 1;
}

.section-count {
  font-size: 12px;
  color: #b08d6a;
  background: #f8f4ef;
  padding: 2px 8px;
  border-radius: 10px;
}

.material-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.material-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 8px;
  background: #faf7f3;
  border-radius: 6px;
}

.item-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.item-detail {
  display: flex;
  gap: 8px;
  align-items: center;
}

.spec {
  font-size: 12px;
  color: #999;
  background: #fff;
  padding: 2px 6px;
  border-radius: 4px;
  border: 1px solid #eee;
}

.quantity {
  font-size: 13px;
  color: #c08457;
  font-weight: 500;
}

.material-summary.compact .compact-fallback {
  display: flex;
  align-items: center;
  gap: 4px;
  background: #fff8e6;
  padding: 3px 8px;
  border-radius: 10px;
  border-left: 2px solid #faad14;
  font-size: 11px;
  color: #876800;
  max-width: 100%;
}

.material-summary.compact .fallback-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.fallback-section {
  background: #fffbe6;
  border: 1px solid #ffe58f;
  border-radius: 10px;
  padding: 12px;
  margin-bottom: 10px;
}

.fallback-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #ffe58f;
  color: #876800;
}

.fallback-title {
  font-size: 15px;
  font-weight: 600;
  flex: 1;
}

.fallback-content {
  font-size: 13px;
  line-height: 1.7;
  color: #5c4800;
  white-space: pre-wrap;
  word-break: break-all;
  background: #fff;
  padding: 10px 12px;
  border-radius: 6px;
  border: 1px solid #fff1b8;
}
</style>
