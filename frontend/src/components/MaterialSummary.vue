<template>
  <div class="material-summary" :class="{ compact: compact }">
    <template v-if="compact">
      <div class="compact-icons">
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
    </template>

    <template v-else>
      <div class="summary-section" v-if="summaryData.mainMaterials?.length">
        <div class="section-header">
          <span class="section-icon">🐂</span>
          <span class="section-title">主材</span>
          <span class="section-count">{{ summaryData.mainMaterials.length }} 项</span>
        </div>
        <div class="material-list">
          <div class="material-item" v-for="(item, idx) in summaryData.mainMaterials" :key="'m-'+idx">
            <div class="item-name">{{ item.name }}</div>
            <div class="item-detail">
              <span class="spec" v-if="item.spec">{{ item.spec }}</span>
              <span class="quantity" v-if="item.quantity">{{ item.quantity }}</span>
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
            <div class="item-name">{{ item.name }}</div>
            <div class="item-detail">
              <span class="spec" v-if="item.spec">{{ item.spec }}</span>
              <span class="quantity" v-if="item.quantity">{{ item.quantity }}</span>
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
            <div class="item-name">{{ item.name }}</div>
            <div class="item-detail">
              <span class="spec" v-if="item.spec">{{ item.spec }}</span>
              <span class="quantity" v-if="item.quantity">{{ item.quantity }}</span>
            </div>
          </div>
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
  compact: {
    type: Boolean,
    default: false
  }
})

const summaryData = computed(() => {
  if (!props.materialSummary) {
    return { mainMaterials: [], auxMaterials: [], tools: [] }
  }
  try {
    return JSON.parse(props.materialSummary)
  } catch (e) {
    return { mainMaterials: [], auxMaterials: [], tools: [] }
  }
})
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
</style>
