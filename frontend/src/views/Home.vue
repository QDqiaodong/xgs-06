<template>
  <div class="home-page">
    <van-nav-bar title="手工皮具创作" fixed />

    <div class="page-content">
      <div class="matrix-section">
        <div class="matrix-group">
          <div class="matrix-group-header">
            <span class="group-label">品类</span>
            <span class="group-sub">按作品类型浏览</span>
          </div>
          <div class="matrix-grid">
            <div
              v-for="cat in categories"
              :key="'cat-' + cat.id"
              class="matrix-item"
              :class="{ active: selectedCategory === cat.id }"
              @click="toggleCategory(cat.id)"
            >
              <div class="matrix-icon">
                <van-icon :name="cat.icon || 'label-o'" size="24" />
              </div>
              <span class="matrix-name">{{ cat.name }}</span>
            </div>
          </div>
        </div>

        <div class="matrix-group">
          <div class="matrix-group-header">
            <span class="group-label">工艺</span>
            <span class="group-sub">按制作手法浏览</span>
          </div>
          <div class="matrix-grid">
            <div
              v-for="craft in craftTypes"
              :key="'craft-' + craft.id"
              class="matrix-item"
              :class="{ active: selectedCraft === craft.id }"
              @click="toggleCraft(craft.id)"
            >
              <div class="matrix-icon">
                <van-icon :name="craft.icon || 'label-o'" size="24" />
              </div>
              <span class="matrix-name">{{ craft.name }}</span>
            </div>
          </div>
        </div>

        <div v-if="selectedCategory || selectedCraft" class="filter-hint">
          <span>
            当前筛选：
            <template v-if="selectedCategory">
              <van-tag type="primary" closeable size="medium" @close="selectedCategory = null">
                {{ getCategoryName(selectedCategory) }}
              </van-tag>
            </template>
            <template v-if="selectedCraft">
              <span class="craft-badge-tag" :class="getCraftClass(getCraftName(selectedCraft))">
                {{ getCraftInfoByName(getCraftName(selectedCraft)).icon }} {{ getCraftName(selectedCraft) }}
              </span>
              <van-icon name="cross" class="filter-close" @click="selectedCraft = null" />
            </template>
          </span>
          <van-button size="mini" type="default" @click="clearFilter">清除筛选</van-button>
        </div>
      </div>

      <div class="density-bar">
        <span class="density-label">浏览密度</span>
        <div class="density-switch">
          <div
            class="density-btn"
            :class="{ active: viewDensity === 'standard' }"
            @click="viewDensity = 'standard'"
          >
            <van-icon name="grid-o" size="16" />
            <span>标准</span>
          </div>
          <div
            class="density-btn"
            :class="{ active: viewDensity === 'compact' }"
            @click="viewDensity = 'compact'"
          >
            <van-icon name="apps-o" size="16" />
            <span>紧凑</span>
          </div>
        </div>
      </div>

      <div class="filter-bar">
        <WorkList :category-id="selectedCategory" :craft-type-id="selectedCraft" :density="viewDensity" />
      </div>
    </div>

    <BottomNav />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCategories } from '@/api'
import { getCraftInfoByName, getCraftClass } from '@/utils/craftConfig'
import WorkList from '@/components/WorkList.vue'
import BottomNav from '@/components/BottomNav.vue'

const categories = ref([])
const craftTypes = ref([])
const selectedCategory = ref(null)
const selectedCraft = ref(null)
const viewDensity = ref('standard')

const getCategoryName = (id) => {
  const cat = categories.value.find(c => c.id === id)
  return cat ? cat.name : ''
}

const getCraftName = (id) => {
  const craft = craftTypes.value.find(c => c.id === id)
  return craft ? craft.name : ''
}

const toggleCategory = (id) => {
  selectedCategory.value = selectedCategory.value === id ? null : id
}

const toggleCraft = (id) => {
  selectedCraft.value = selectedCraft.value === id ? null : id
}

const clearFilter = () => {
  selectedCategory.value = null
  selectedCraft.value = null
}

onMounted(async () => {
  const [cats, crafts] = await Promise.all([
    getCategories('category'),
    getCategories('craft_type')
  ])
  categories.value = cats || []
  craftTypes.value = crafts || []
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  padding-bottom: 50px;
}

.page-content {
  padding-top: 46px;
}

.matrix-section {
  background: #fff;
  padding: 16px;
  margin-bottom: 8px;
}

.matrix-group {
  margin-bottom: 16px;
}

.matrix-group:last-child {
  margin-bottom: 0;
}

.matrix-group-header {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 12px;
}

.group-label {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.group-sub {
  font-size: 12px;
  color: #999;
}

.matrix-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.matrix-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  width: calc((100% - 24px) / 4);
  padding: 12px 4px;
  border-radius: 10px;
  background: #f9f6f2;
  cursor: pointer;
  transition: all 0.2s ease;
}

.matrix-item:active {
  transform: scale(0.95);
}

.matrix-item.active {
  background: #f5e6d3;
  border: 1px solid #c08457;
}

.matrix-item.active .matrix-icon {
  color: #8b6914;
}

.matrix-item.active .matrix-name {
  color: #8b6914;
  font-weight: 500;
}

.matrix-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #fff;
  color: #666;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.matrix-name {
  font-size: 12px;
  color: #666;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
}

.filter-hint {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
  margin-top: 12px;
  border-top: 1px solid #f0f0f0;
  font-size: 13px;
  color: #666;
}

.filter-hint span {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.density-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 16px;
  background: #fff;
  border-bottom: 1px solid #f5f5f5;
}

.density-label {
  font-size: 13px;
  color: #666;
  font-weight: 500;
}

.density-switch {
  display: flex;
  background: #f5f5f5;
  border-radius: 8px;
  padding: 2px;
}

.density-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  font-size: 12px;
  color: #666;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.density-btn.active {
  background: #fff;
  color: #c08457;
  font-weight: 500;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.filter-bar {
  background: #fff;
}

.craft-badge-tag {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  border: 1px solid transparent;
}

.filter-close {
  font-size: 14px;
  color: #999;
  margin-left: 2px;
  cursor: pointer;
  vertical-align: middle;
}
</style>
