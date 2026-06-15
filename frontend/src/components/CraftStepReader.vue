<template>
  <div class="craft-step-reader" v-if="steps && steps.length">
    <div class="reader-header">
      <h3 class="reader-title">
        <van-icon name="wap-nav" />
        工序分镜
        <span class="step-count">共 {{ steps.length }} 步</span>
      </h3>
    </div>

    <div class="step-tabs-wrapper">
      <div class="step-tabs" ref="tabsRef">
        <div
          v-for="(step, idx) in steps"
          :key="idx"
          class="step-tab"
          :class="{ active: currentIndex === idx }"
          @click="selectStep(idx)"
        >
          <div class="tab-icon" :class="getStepTypeClass(step.stepType)">
            {{ getStepIcon(step.stepType) }}
          </div>
          <div class="tab-num">{{ idx + 1 }}</div>
          <div class="tab-name">{{ step.stepName }}</div>
        </div>
      </div>
    </div>

    <div class="step-progress">
      <div
        class="progress-bar"
        :style="{ width: ((currentIndex + 1) / steps.length * 100) + '%' }"
      ></div>
    </div>

    <div class="step-content-wrapper" @touchstart="onTouchStart" @touchmove="onTouchMove" @touchend="onTouchEnd">
      <div class="step-card">
        <div class="step-header">
          <div class="step-badge" :class="getStepTypeClass(currentStep.stepType)">
            <span class="badge-icon">{{ getStepIcon(currentStep.stepType) }}</span>
            第 {{ currentIndex + 1 }} 步
          </div>
          <h4 class="step-title">{{ currentStep.stepName }}</h4>
          <van-tag v-if="currentStep.stepType" :type="getStepTagType(currentStep.stepType)" plain>
            {{ getStepTypeName(currentStep.stepType) }}
          </van-tag>
        </div>

        <div v-if="currentStep.images && currentStep.images.length" class="step-images">
          <van-swipe :autoplay="0" indicator-color="#fff" :show-indicators="currentStep.images.length > 1">
            <van-swipe-item v-for="(img, imgIdx) in currentStep.images" :key="imgIdx">
              <img :src="img" alt="" @click="previewImage(imgIdx)" />
            </van-swipe-item>
          </van-swipe>
          <div v-if="currentStep.images.length > 1" class="image-counter">
            {{ previewIndex + 1 }} / {{ currentStep.images.length }}
          </div>
        </div>

        <div v-if="currentStep.materials" class="step-section">
          <div class="section-title">
            <van-icon name="gem-o" />
            本步材料
          </div>
          <div class="section-content materials">
            {{ currentStep.materials }}
          </div>
        </div>

        <div v-if="currentStep.description" class="step-section">
          <div class="section-title">
            <van-icon name="description" />
            操作说明
          </div>
          <div class="section-content description">
            {{ currentStep.description }}
          </div>
        </div>

        <div v-if="currentStep.tips" class="step-section tips-section">
          <div class="section-title">
            <van-icon name="warning-o" />
            注意要点
          </div>
          <div class="section-content tips">
            <van-icon name="bulb-o" class="bulb-icon" />
            {{ currentStep.tips }}
          </div>
        </div>
      </div>
    </div>

    <div class="step-nav">
      <van-button
        size="small"
        plain
        :disabled="currentIndex === 0"
        @click="prevStep"
        icon="arrow-left"
      >
        上一步
      </van-button>
      <div class="nav-info">
        <van-icon name="wap-nav" />
        {{ currentIndex + 1 }} / {{ steps.length }}
      </div>
      <van-button
        size="small"
        type="primary"
        :disabled="currentIndex === steps.length - 1"
        @click="nextStep"
      >
        下一步
        <van-icon name="arrow" />
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { showImagePreview } from 'vant'

const props = defineProps({
  steps: {
    type: Array,
    default: () => []
  }
})

const currentIndex = ref(0)
const previewIndex = ref(0)
const tabsRef = ref(null)
const touchStartX = ref(0)

const currentStep = computed(() => props.steps[currentIndex.value] || {})

const stepTypeMap = {
  cutting: { name: '裁切', icon: '✂️', class: 'type-cutting', tagType: 'danger' },
  sewing: { name: '缝制', icon: '🧵', class: 'type-sewing', tagType: 'primary' },
  edge: { name: '封边', icon: '🎨', class: 'type-edge', tagType: 'warning' },
  hardware: { name: '五金安装', icon: '🔩', class: 'type-hardware', tagType: 'success' },
  shaping: { name: '塑形', icon: '✨', class: 'type-shaping', tagType: '' },
  carving: { name: '皮雕', icon: '🗡️', class: 'type-carving', tagType: '' },
  other: { name: '其他', icon: '📝', class: 'type-other', tagType: '' }
}

const getStepTypeInfo = (type) => stepTypeMap[type] || stepTypeMap.other
const getStepTypeClass = (type) => getStepTypeInfo(type).class
const getStepIcon = (type) => getStepTypeInfo(type).icon
const getStepTypeName = (type) => getStepTypeInfo(type).name
const getStepTagType = (type) => getStepTypeInfo(type).tagType || 'default'

const selectStep = (idx) => {
  currentIndex.value = idx
  previewIndex.value = 0
  scrollTabIntoView(idx)
}

const scrollTabIntoView = (idx) => {
  nextTick(() => {
    if (tabsRef.value) {
      const tabs = tabsRef.value.querySelectorAll('.step-tab')
      if (tabs[idx]) {
        tabs[idx].scrollIntoView({ behavior: 'smooth', inline: 'center', block: 'nearest' })
      }
    }
  })
}

const prevStep = () => {
  if (currentIndex.value > 0) {
    selectStep(currentIndex.value - 1)
  }
}

const nextStep = () => {
  if (currentIndex.value < props.steps.length - 1) {
    selectStep(currentIndex.value + 1)
  }
}

const previewImage = (idx) => {
  previewIndex.value = idx
  if (currentStep.value.images) {
    showImagePreview({
      images: currentStep.value.images,
      startPosition: idx
    })
  }
}

const onTouchStart = (e) => {
  touchStartX.value = e.touches[0].clientX
}

const onTouchMove = (e) => {
  e.preventDefault()
}

const onTouchEnd = (e) => {
  const touchEndX = e.changedTouches[0].clientX
  const diff = touchStartX.value - touchEndX
  if (Math.abs(diff) > 50) {
    if (diff > 0) {
      nextStep()
    } else {
      prevStep()
    }
  }
}

watch(() => props.steps, () => {
  currentIndex.value = 0
  previewIndex.value = 0
})
</script>

<style scoped>
.craft-step-reader {
  background: linear-gradient(135deg, #faf5ef 0%, #f5ebe0 100%);
  border-radius: 16px;
  overflow: hidden;
  margin-top: 16px;
}

.reader-header {
  padding: 16px 16px 12px;
  background: linear-gradient(135deg, #8b5a2b 0%, #a0522d 100%);
}

.reader-title {
  margin: 0;
  font-size: 17px;
  font-weight: 600;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 6px;
}

.step-count {
  font-size: 13px;
  font-weight: 400;
  opacity: 0.85;
  margin-left: auto;
}

.step-tabs-wrapper {
  background: #fff;
  padding: 12px 0;
  border-bottom: 1px solid #f0e6d8;
}

.step-tabs {
  display: flex;
  gap: 10px;
  padding: 0 12px;
  overflow-x: auto;
  scrollbar-width: none;
}

.step-tabs::-webkit-scrollbar {
  display: none;
}

.step-tab {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px 12px;
  border-radius: 12px;
  background: #f8f4ee;
  cursor: pointer;
  transition: all 0.25s ease;
  min-width: 72px;
  border: 2px solid transparent;
}

.step-tab.active {
  background: #fff8ee;
  border-color: #8b5a2b;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(139, 90, 43, 0.2);
}

.tab-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  margin-bottom: 4px;
  background: #ede4d5;
}

.step-tab.active .tab-icon {
  transform: scale(1.1);
}

.tab-icon.type-cutting { background: #ffe4e1; }
.tab-icon.type-sewing { background: #e3f2fd; }
.tab-icon.type-edge { background: #fff3e0; }
.tab-icon.type-hardware { background: #e8f5e9; }
.tab-icon.type-shaping { background: #f3e5f5; }
.tab-icon.type-carving { background: #e0f7fa; }
.tab-icon.type-other { background: #f5f5f5; }

.tab-num {
  font-size: 11px;
  color: #999;
  margin-bottom: 2px;
}

.step-tab.active .tab-num {
  color: #8b5a2b;
  font-weight: 600;
}

.tab-name {
  font-size: 12px;
  color: #666;
  text-align: center;
  white-space: nowrap;
  max-width: 70px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.step-tab.active .tab-name {
  color: #8b5a2b;
  font-weight: 500;
}

.step-progress {
  height: 3px;
  background: #f0e6d8;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #8b5a2b, #cd853f);
  transition: width 0.3s ease;
  border-radius: 0 2px 2px 0;
}

.step-content-wrapper {
  padding: 16px;
  overflow: hidden;
}

.step-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(139, 90, 43, 0.08);
}

.step-header {
  margin-bottom: 16px;
}

.step-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  margin-bottom: 8px;
}

.badge-icon {
  font-size: 14px;
}

.step-badge.type-cutting { background: #ffe4e1; color: #c62828; }
.step-badge.type-sewing { background: #e3f2fd; color: #1565c0; }
.step-badge.type-edge { background: #fff3e0; color: #e65100; }
.step-badge.type-hardware { background: #e8f5e9; color: #2e7d32; }
.step-badge.type-shaping { background: #f3e5f5; color: #6a1b9a; }
.step-badge.type-carving { background: #e0f7fa; color: #00838f; }
.step-badge.type-other { background: #f5f5f5; color: #616161; }

.step-title {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.step-images {
  position: relative;
  margin: 0 -16px 16px;
}

.step-images :deep(.van-swipe) {
  width: 100%;
  aspect-ratio: 4/3;
}

.step-images img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-counter {
  position: absolute;
  bottom: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
}

.step-section {
  margin-bottom: 14px;
}

.step-section:last-child {
  margin-bottom: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #8b5a2b;
  margin-bottom: 8px;
}

.section-content {
  padding: 10px 12px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.7;
  color: #444;
}

.materials {
  background: #fdf6ec;
  border-left: 3px solid #cd853f;
}

.description {
  background: #f8f9fa;
  border-left: 3px solid #999;
  white-space: pre-wrap;
}

.tips-section .section-title {
  color: #e65100;
}

.tips {
  background: #fff8e1;
  border-left: 3px solid #ffa000;
  display: flex;
  gap: 6px;
}

.bulb-icon {
  flex-shrink: 0;
  color: #ffa000;
  font-size: 16px;
  margin-top: 2px;
}

.step-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px 16px;
  gap: 12px;
}

.nav-info {
  flex: 1;
  text-align: center;
  font-size: 14px;
  color: #8b5a2b;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.step-nav :deep(.van-button) {
  flex: 0 0 auto;
}
</style>
