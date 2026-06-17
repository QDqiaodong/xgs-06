<template>
  <div class="work-detail">
    <van-nav-bar
      title="作品详情" left-arrow @click-left="$router.back()" fixed>
      <template #right>
        <van-icon name="share-o" size="20" />
      </template>
    </van-nav-bar>

    <div class="detail-content" v-if="loading">
      <van-loading class="detail-loading" type="spinner" color="#8b5a2b">加载中...</van-loading>
    </div>

    <div class="detail-content detail-empty" v-else-if="loadError">
      <van-empty image="error" :description="loadError">
        <van-button round type="primary" size="small" @click="loadWorkDetail()">重新加载</van-button>
      </van-empty>
    </div>

    <div class="detail-content" v-else-if="work">
      <div class="offline-banner" v-if="work.status === 0">
        <van-icon name="warning-o" />
        <span>该作品已下架，仅作者可查看</span>
      </div>

      <div class="gallery-tabs-wrapper">
        <van-tabs v-model:active="galleryTab" class="gallery-tabs" sticky offset-top="46" line-width="40px">
          <van-tab title="成品图" name="finished">
            <div class="gallery-cabin finished-cabin">
              <div class="cabin-header">
                <van-icon name="star-o" class="cabin-icon" />
                <span class="cabin-title">作品成品展示</span>
                <span class="cabin-subtitle" v-if="validDisplayImages.length">共 {{ validDisplayImages.length }} 张</span>
              </div>
              <van-swipe
                v-if="validDisplayImages.length"
                class="finished-swiper"
                :autoplay="4000"
                indicator-color="#fff"
                :show-indicators="validDisplayImages.length > 1"
              >
                <van-swipe-item v-for="(img, idx) in validDisplayImages" :key="idx">
                  <img :src="img" alt="" @error="onImageError(idx, 'display')" @click="previewFinishedImage(idx)" />
                </van-swipe-item>
              </van-swipe>
              <div v-else class="image-placeholder finished-placeholder">
                <van-icon name="photo-o" />
                <span class="placeholder-text">暂无成品图</span>
              </div>
              <div v-if="validDisplayImages.length > 1" class="finished-thumbs">
                <div
                  v-for="(img, idx) in validDisplayImages"
                  :key="idx"
                  class="thumb-item"
                  :class="{ active: finishedIndex === idx }"
                  @click="selectFinishedImage(idx)"
                >
                  <img :src="img" alt="" @error="onImageError(idx, 'display')" />
                </div>
              </div>
            </div>
          </van-tab>

          <van-tab title="过程图" name="process">
            <div class="gallery-cabin process-cabin">
              <div class="cabin-header">
                <van-icon name="wap-nav" class="cabin-icon" />
                <span class="cabin-title">制作过程记录</span>
                <span class="cabin-subtitle" v-if="hasProcessContent">共 {{ processTotalCount }} 张</span>
              </div>

              <div v-if="work.steps && work.steps.length" class="process-timeline">
                <div v-if="totalTimeCost > 0" class="total-time-card">
                  <van-icon name="clock-o" class="clock-icon" />
                  <span class="total-time-label">预计总耗时</span>
                  <span class="total-time-value">{{ formatTotalTime(totalTimeCost) }}</span>
                </div>
                <div
                  v-for="(step, sIdx) in work.steps"
                  :key="sIdx"
                  class="timeline-item"
                >
                  <div class="timeline-node">
                    <div class="node-circle" :class="getStepTypeClass(step.stepType)">
                      {{ getStepIcon(step.stepType) }}
                    </div>
                    <div class="node-line" v-if="sIdx < work.steps.length - 1"></div>
                  </div>
                  <div class="timeline-content">
                    <div class="step-meta">
                      <span class="step-order">第 {{ sIdx + 1 }} 步</span>
                      <span class="step-name">{{ step.stepName }}</span>
                      <van-tag v-if="step.stepType" size="medium" :type="getStepTagType(step.stepType)" plain>
                        {{ getStepTypeName(step.stepType) }}
                      </van-tag>
                      <van-tag v-if="step.timeCost && step.timeCost > 0" size="medium" type="primary">
                        <van-icon name="clock-o" size="11" />
                        {{ step.timeCost }}分钟
                      </van-tag>
                    </div>
                    <div v-if="getValidStepImages(step, sIdx).length" class="step-image-grid">
                      <img
                        v-for="(img, iIdx) in getValidStepImages(step, sIdx)"
                        :key="iIdx"
                        :src="img"
                        alt=""
                        class="step-image"
                        :class="'step-image-' + getValidStepImages(step, sIdx).length"
                        @error="onStepImageError(sIdx, iIdx)"
                        @click="previewProcessImage(step, sIdx, iIdx)"
                      />
                    </div>
                    <div v-else class="step-no-image">
                      <van-icon name="photo-o" />
                      <span>本步暂无配图</span>
                    </div>
                    <p v-if="step.description" class="step-desc">{{ step.description }}</p>
                    <p v-if="step.tips" class="step-tips">
                      <van-icon name="bulb-o" class="bulb-icon" />
                      {{ step.tips }}
                    </p>
                  </div>
                </div>
              </div>

              <div v-else-if="validProcessImages.length" class="process-list">
                <div
                  v-for="(img, idx) in validProcessImages"
                  :key="idx"
                  class="process-list-item"
                  @click="previewProcessListImage(idx)"
                >
                  <div class="process-index">{{ idx + 1 }}</div>
                  <img :src="img" alt="" @error="onImageError(idx, 'process')" />
                </div>
              </div>

              <div v-else class="image-placeholder process-placeholder">
                <van-icon name="photo-o" />
                <span class="placeholder-text">暂无过程图</span>
              </div>
            </div>
          </van-tab>
        </van-tabs>
      </div>

      <div class="work-info">
        <h1 class="title">{{ work.title }}</h1>

        <div class="author-row">
          <van-image round size="40" :src="work.avatar || 'https://img.yzcdn.cn/vant/cat.jpeg'" />
          <div class="author-info">
            <div class="nickname">{{ work.nickname }}</div>
            <div class="time">{{ formatTime(work.createTime) }}</div>
          </div>
          <van-button
            size="small"
            :type="work.isFavorite ? 'warning' : 'default'"
            @click="handleFavorite"
          >
            {{ work.isFavorite ? '已收藏' : '收藏' }}
          </van-button>
        </div>

        <div class="tags">
          <van-tag plain type="primary">{{ work.categoryName }}</van-tag>
          <span v-if="work.craftTypeName" class="craft-tag" :class="getCraftTagClass(work.craftTypeName)">
            <span class="craft-icon">{{ getCraftIcon(work.craftTypeName) }}</span>
            <span>{{ work.craftTypeName }}</span>
          </span>
        </div>

        <div class="stats">
          <span><van-icon name="eye-o" /> {{ work.viewCount }} 浏览</span>
          <span><van-icon name="star-o" /> {{ work.favoriteCount }} 收藏</span>
        </div>

        <div class="craft-highlights" v-if="work.craftHighlights && work.craftHighlights.length">
          <div class="highlights-header">
            <van-icon name="fire-o" class="fire-icon" />
            <span class="highlights-title">工艺重点</span>
          </div>
          <div class="highlights-tags">
            <div
              v-for="(craft, idx) in work.craftHighlights"
              :key="idx"
              :class="getCraftTagClass(craft)"
            >
              <span class="craft-icon">{{ getCraftIcon(craft) }}</span>
              <span class="craft-name">{{ craft }}</span>
            </div>
          </div>
        </div>

        <van-divider>作品介绍</van-divider>
        <p class="content-text">{{ work.content }}</p>

        <van-divider v-if="work.materialSummary || work.materials">材料用量速览</van-divider>
        <MaterialSummary v-if="work.materialSummary" :material-summary="work.materialSummary" />
        <p class="content-text materials-text" v-if="work.materials && !work.materialSummary">{{ work.materials }}</p>
        <p class="content-text materials-text" v-if="work.materials && work.materialSummary">{{ work.materials }}</p>

        <CraftStepReader v-if="work.steps && work.steps.length" :steps="work.steps" />

        <van-divider v-if="!work.steps || !work.steps.length">制作流程</van-divider>
        <p class="content-text" v-if="(!work.steps || !work.steps.length) && work.craftSteps">{{ work.craftSteps }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getWorkDetail, toggleFavorite } from '@/api'
import { useUserStore } from '@/store/user'
import { showToast, showImagePreview } from 'vant'
import { getStepTypeInfo, getCraftInfoByName, getCraftClass, getStepClass } from '@/utils/craftConfig'
import CraftStepReader from '@/components/CraftStepReader.vue'
import MaterialSummary from '@/components/MaterialSummary.vue'

const route = useRoute()
const userStore = useUserStore()
const work = ref(null)
const loading = ref(false)
const loadError = ref('')
const failedImages = ref(new Set())
const stepFailedImages = ref(new Set())
const galleryTab = ref('finished')
const finishedIndex = ref(0)

const getStepTypeClass = (type) => getStepClass(type)
const getStepIcon = (type) => getStepTypeInfo(type).icon
const getStepTypeName = (type) => getStepTypeInfo(type).name
const getStepTagType = (type) => getStepTypeInfo(type).tagType || 'default'

const displayImages = computed(() => {
  if (!work.value) return []
  if (Array.isArray(work.value.images) && work.value.images.length) {
    return work.value.images
  }
  if (work.value.cover) {
    return [work.value.cover]
  }
  return []
})

const validDisplayImages = computed(() => {
  return displayImages.value.filter((_, idx) => !failedImages.value.has(`display-${idx}`))
})

const validProcessImages = computed(() => {
  if (!work.value || !work.value.processImages) return []
  return work.value.processImages.filter((_, idx) => !failedImages.value.has(`process-${idx}`))
})

const hasProcessContent = computed(() => {
  if (!work.value) return false
  if (work.value.steps && work.value.steps.length) {
    return work.value.steps.some((step, sIdx) => getValidStepImages(step, sIdx).length > 0)
  }
  return validProcessImages.value.length > 0
})

const processTotalCount = computed(() => {
  if (!work.value) return 0
  if (work.value.steps && work.value.steps.length) {
    return work.value.steps.reduce((sum, step, sIdx) => sum + getValidStepImages(step, sIdx).length, 0)
  }
  return validProcessImages.value.length
})

const totalTimeCost = computed(() => {
  if (!work.value || !work.value.steps || !work.value.steps.length) return 0
  return work.value.steps.reduce((sum, step) => sum + (step.timeCost || 0), 0)
})

const formatTotalTime = (minutes) => {
  if (!minutes) return '0分钟'
  if (minutes < 60) return `${minutes}分钟`
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return mins > 0 ? `${hours}小时${mins}分钟` : `${hours}小时`
}

const getValidStepImages = (step, sIdx) => {
  const images = step.images || []
  return images.filter((_, iIdx) => !stepFailedImages.value.has(`step-${sIdx}-${iIdx}`))
}

const onImageError = (idx, type) => {
  failedImages.value.add(`${type}-${idx}`)
  failedImages.value = new Set(failedImages.value)
}

const onStepImageError = (sIdx, iIdx) => {
  stepFailedImages.value.add(`step-${sIdx}-${iIdx}`)
  stepFailedImages.value = new Set(stepFailedImages.value)
}

const selectFinishedImage = (idx) => {
  finishedIndex.value = idx
}

const previewFinishedImage = (idx) => {
  if (validDisplayImages.value.length) {
    showImagePreview({
      images: validDisplayImages.value,
      startPosition: idx
    })
  }
}

const previewProcessImage = (step, sIdx, iIdx) => {
  const imgs = getValidStepImages(step, sIdx)
  if (imgs.length) {
    showImagePreview({
      images: imgs,
      startPosition: iIdx
    })
  }
}

const previewProcessListImage = (idx) => {
  if (validProcessImages.value.length) {
    showImagePreview({
      images: validProcessImages.value,
      startPosition: idx
    })
  }
}

watch(galleryTab, () => {
  finishedIndex.value = 0
})

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms))

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString()
}

const getCraftIcon = (name) => getCraftInfoByName(name).icon
const getCraftTagClass = (name) => `craft-tag ${getCraftClass(name)}`

const handleFavorite = async () => {
  if (!userStore.isLoggedIn()) {
    showToast('请先登录')
    return
  }
  await toggleFavorite(userStore.userInfo.id, route.params.id)
  work.value.isFavorite = !work.value.isFavorite
  work.value.favoriteCount += work.value.isFavorite ? 1 : -1
  showToast(work.value.isFavorite ? '收藏成功' : '已取消收藏')
}

const loadWorkDetail = async (retryCount = 8) => {
  loading.value = true
  loadError.value = ''
  failedImages.value = new Set()
  stepFailedImages.value = new Set()
  try {
    for (let attempt = 0; attempt <= retryCount; attempt++) {
      try {
        work.value = await getWorkDetail(route.params.id, userStore.userInfo?.id, { silentError: true })
        if (!work.value) {
          loadError.value = '作品不存在或已下架'
        }
        return
      } catch (error) {
        if (attempt === retryCount) {
          throw error
        }
        await sleep(2500)
      }
    }
  } catch (error) {
    loadError.value = '作品加载失败，请稍后重试'
    showToast(loadError.value)
  } finally {
    loading.value = false
  }
}

onMounted(() => loadWorkDetail())
</script>

<style scoped>
.work-detail {
  min-height: 100vh;
  background: #fff;
}

.detail-content {
  padding-top: 46px;
}

.detail-loading {
  display: flex;
  justify-content: center;
  padding: 120px 0;
}

.detail-empty {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.offline-banner {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  background: #fffbe8;
  color: #ed6a0c;
  font-size: 13px;
  border-bottom: 1px solid #ffe58f;
}

.gallery-tabs-wrapper {
  background: #fff;
}

.gallery-tabs :deep(.van-tabs__nav) {
  background: linear-gradient(180deg, #faf5ef 0%, #fff 100%);
}

.gallery-tabs :deep(.van-tab) {
  font-size: 15px;
  font-weight: 500;
  color: #888;
}

.gallery-tabs :deep(.van-tab--active) {
  color: #8b5a2b;
  font-weight: 600;
}

.gallery-tabs :deep(.van-tabs__line) {
  background: linear-gradient(90deg, #8b5a2b, #cd853f);
  border-radius: 2px;
}

.gallery-cabin {
  background: #fff;
}

.cabin-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px;
  border-bottom: 1px solid #f0e6d8;
}

.cabin-icon {
  font-size: 20px;
  color: #8b5a2b;
}

.cabin-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  flex: 1;
}

.cabin-subtitle {
  font-size: 13px;
  color: #999;
}

.finished-cabin {
  background: linear-gradient(180deg, #fdf9f5 0%, #fff 100%);
}

.finished-swiper {
  width: 100%;
  aspect-ratio: 4/3;
  background: #000;
}

.finished-swiper :deep(.van-swipe-item) {
  display: flex;
  align-items: center;
  justify-content: center;
}

.finished-swiper img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: #1a1a1a;
}

.finished-thumbs {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  overflow-x: auto;
  background: #fff;
}

.finished-thumbs::-webkit-scrollbar {
  display: none;
}

.thumb-item {
  flex-shrink: 0;
  width: 64px;
  height: 64px;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid transparent;
  transition: all 0.2s ease;
  background: #f5f5f5;
}

.thumb-item.active {
  border-color: #8b5a2b;
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(139, 90, 43, 0.3);
}

.thumb-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.process-cabin {
  background: linear-gradient(180deg, #f5faf5 0%, #fff 100%);
}

.process-timeline {
  padding: 16px;
}

.total-time-card {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 16px;
  background: linear-gradient(135deg, #fef7ed 0%, #fff5e6 100%);
  border-radius: 12px;
  margin-bottom: 20px;
  border: 1px solid #f5e6d3;
}

.clock-icon {
  font-size: 20px;
  color: #8b5a2b;
}

.total-time-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.total-time-value {
  margin-left: auto;
  font-size: 16px;
  font-weight: 700;
  color: #8b5a2b;
}

.timeline-item {
  display: flex;
  gap: 12px;
  padding-bottom: 24px;
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-node {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex-shrink: 0;
  width: 36px;
}

.node-circle {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  border: 2px solid #fff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  z-index: 1;
}

.node-line {
  flex: 1;
  width: 2px;
  margin-top: 4px;
  background: linear-gradient(180deg, #e0d0b8 0%, #f0e6d8 100%);
}

.timeline-content {
  flex: 1;
  min-width: 0;
}

.step-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 10px;
}

.step-order {
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  background: linear-gradient(135deg, #8b5a2b, #a0522d);
  padding: 2px 10px;
  border-radius: 10px;
}

.step-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.step-image-grid {
  display: grid;
  gap: 6px;
  margin-bottom: 10px;
}

.step-image-grid.step-image-1 {
  grid-template-columns: 1fr;
}

.step-image-grid.step-image-2 {
  grid-template-columns: 1fr 1fr;
}

.step-image-grid.step-image-3 {
  grid-template-columns: 1fr 1fr;
}

.step-image-grid.step-image-3 .step-image:first-child {
  grid-column: span 2;
}

.step-image-grid.step-image-4,
.step-image-grid.step-image-5 {
  grid-template-columns: 1fr 1fr;
}

.step-image {
  width: 100%;
  aspect-ratio: 4/3;
  object-fit: cover;
  border-radius: 8px;
  background: #f5f5f5;
}

.step-no-image {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  color: #aaa;
  font-size: 13px;
  margin-bottom: 10px;
}

.step-desc {
  font-size: 14px;
  line-height: 1.7;
  color: #555;
  margin: 0 0 8px;
  white-space: pre-wrap;
}

.step-tips {
  display: flex;
  gap: 6px;
  font-size: 13px;
  line-height: 1.6;
  color: #e65100;
  background: #fff8e1;
  padding: 10px 12px;
  border-radius: 8px;
  margin: 0;
  border-left: 3px solid #ffa000;
}

.bulb-icon {
  flex-shrink: 0;
  color: #ffa000;
  font-size: 15px;
  margin-top: 2px;
}

.process-list {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.process-list-item {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  background: #f5f5f5;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.process-list-item img {
  width: 100%;
  display: block;
}

.process-index {
  position: absolute;
  top: 10px;
  left: 10px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(139, 90, 43, 0.9);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}

.image-placeholder {
  width: 100%;
  aspect-ratio: 4/3;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: #f8f4ef;
  color: #c8aa84;
  font-size: 48px;
}

.placeholder-text {
  font-size: 14px;
  color: #b0a090;
}

.work-info {
  padding: 16px;
}

.title {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 16px;
}

.author-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.author-info {
  flex: 1;
}

.nickname {
  font-size: 15px;
  font-weight: 500;
}

.time {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.tags {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.stats {
  display: flex;
  gap: 20px;
  color: #999;
  font-size: 14px;
  margin-bottom: 16px;
}

.content-text {
  font-size: 15px;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
}

.materials-text {
  margin-top: 10px;
  font-size: 13px;
  color: #888;
  background: #faf7f3;
  padding: 10px 12px;
  border-radius: 6px;
  border-left: 3px solid #d4b896;
}

.craft-highlights {
  background: linear-gradient(135deg, #fef7ed 0%, #fff5e6 100%);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  border: 1px solid #f5e6d3;
}

.highlights-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 12px;
}

.fire-icon {
  color: #ff6b35;
  font-size: 18px;
}

.highlights-title {
  font-size: 15px;
  font-weight: 600;
  color: #8b5a2b;
}

.highlights-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.craft-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.craft-icon {
  font-size: 14px;
}
</style>
