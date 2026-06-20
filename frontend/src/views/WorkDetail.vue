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
          <span
            v-if="work.workStatus && getWorkStatusInfo(work.workStatus)"
            class="work-status-tag"
            :class="'work-status-tag--' + work.workStatus"
          >
            <span class="work-status-icon">{{ getWorkStatusInfo(work.workStatus).icon }}</span>
            <span>{{ getWorkStatusInfo(work.workStatus).name }}</span>
            <span class="work-status-desc">{{ getWorkStatusInfo(work.workStatus).description }}</span>
          </span>
          <van-tag plain type="primary">{{ work.categoryName }}</van-tag>
          <span v-if="work.craftTypeName" class="craft-tag" :class="getCraftTagClass(work.craftTypeName)">
            <span class="craft-icon">{{ getCraftIcon(work.craftTypeName) }}</span>
            <span>{{ work.craftTypeName }}</span>
          </span>
          <span
            v-if="work.difficulty && getDifficultyInfo(work.difficulty)"
            class="difficulty-tag"
            :class="'difficulty-tag--' + work.difficulty"
          >
            <span class="difficulty-icon">{{ getDifficultyInfo(work.difficulty).icon }}</span>
            <span>{{ getDifficultyInfo(work.difficulty).name }}</span>
            <span class="difficulty-desc">{{ getDifficultyInfo(work.difficulty).description }}</span>
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
        <MaterialSummary v-if="work.materialSummary || work.materials" :material-summary="work.materialSummary" :materials-text="work.materials" />

        <CraftStepReader v-if="work.steps && work.steps.length" :steps="work.steps" />

        <van-divider v-if="!work.steps || !work.steps.length">制作流程</van-divider>
        <p class="content-text" v-if="(!work.steps || !work.steps.length) && work.craftSteps">{{ work.craftSteps }}</p>

        <div class="retrospective-card" v-if="retrospective || isAuthor">
          <div class="retro-header">
            <van-icon name="records-o" class="retro-icon" />
            <span class="retro-title">制作复盘记录</span>
            <span class="retro-edit-btn" v-if="isAuthor" @click="openRetroEditor">
              <van-icon :name="retrospective ? 'edit-o' : 'add-o'" />
              {{ retrospective ? '编辑' : '添加复盘' }}
            </span>
          </div>

          <div class="retro-body" v-if="retrospective">
            <div class="retro-item" v-if="retrospective.reworkPoints">
              <div class="retro-item-label"><van-icon name="warning-o" /> 返工点</div>
              <p class="retro-item-text">{{ retrospective.reworkPoints }}</p>
            </div>
            <div class="retro-item" v-if="retrospective.lossReasons">
              <div class="retro-item-label"><van-icon name="info-o" /> 损耗原因</div>
              <p class="retro-item-text">{{ retrospective.lossReasons }}</p>
            </div>
            <div class="retro-item" v-if="retrospective.improvements">
              <div class="retro-item-label retro-improve-label"><van-icon name="bulb-o" /> 下次改进方向</div>
              <p class="retro-item-text retro-improve-text">{{ retrospective.improvements }}</p>
            </div>
            <div class="retro-footer">
              <span>更新于 {{ formatTime(retrospective.updateTime) }}</span>
              <span class="retro-delete" v-if="isAuthor" @click="handleDeleteRetro">删除</span>
            </div>
          </div>

          <div class="retro-empty" v-else>
            <van-icon name="edit" size="28" color="#c8aa84" />
            <p>作品完成后可记录返工点、损耗原因与改进方向，沉淀制作经验</p>
          </div>
        </div>
      </div>
    </div>

    <van-popup
      v-model:show="showRetroPopup"
      round
      position="bottom"
      closeable
      close-icon-position="top-right"
      :style="{ height: '72%' }"
    >
      <div class="retro-popup">
        <div class="retro-popup-title">{{ retrospective ? '编辑复盘记录' : '添加复盘记录' }}</div>
        <div class="retro-popup-tip">记录本次制作的得失，沉淀可复用的经验</div>
        <van-cell-group inset class="retro-form">
          <van-field
            v-model="retroForm.reworkPoints"
            label="返工点"
            type="textarea"
            placeholder="记录需要返工的环节与问题"
            rows="2"
            autosize
            maxlength="500"
            show-word-limit
          />
          <van-field
            v-model="retroForm.lossReasons"
            label="损耗原因"
            type="textarea"
            placeholder="分析皮料、五金等损耗成因"
            rows="2"
            autosize
            maxlength="500"
            show-word-limit
          />
          <van-field
            v-model="retroForm.improvements"
            label="下次改进"
            type="textarea"
            placeholder="针对性的改进方向与建议"
            rows="2"
            autosize
            maxlength="500"
            show-word-limit
          />
        </van-cell-group>
        <div class="retro-popup-actions">
          <van-button round block type="primary" :loading="retroSaving" @click="saveRetro">
            保存复盘
          </van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getWorkDetail, toggleFavorite, getRetrospective, saveRetrospective, deleteRetrospective } from '@/api'
import { useUserStore } from '@/store/user'
import { showToast, showImagePreview, showConfirmDialog } from 'vant'
import { getStepTypeInfo, getCraftInfoByName, getCraftClass, getStepClass, getDifficultyInfo, getWorkStatusInfo, getWorkStatusClass } from '@/utils/craftConfig'
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

const retrospective = ref(null)
const showRetroPopup = ref(false)
const retroSaving = ref(false)
const retroForm = ref({ reworkPoints: '', lossReasons: '', improvements: '' })

const isAuthor = computed(() => {
  return !!(work.value && work.value.userId && userStore.userInfo && work.value.userId === userStore.userInfo.id)
})

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
  await toggleFavorite(route.params.id)
  work.value.isFavorite = !work.value.isFavorite
  work.value.favoriteCount += work.value.isFavorite ? 1 : -1
  showToast(work.value.isFavorite ? '收藏成功' : '已取消收藏')
}

const loadRetrospective = async () => {
  if (!route.params.id) {
    retrospective.value = null
    return
  }
  try {
    retrospective.value = await getRetrospective(route.params.id, { silentError: true })
  } catch {
    retrospective.value = null
  }
}

const openRetroEditor = () => {
  if (retrospective.value) {
    retroForm.value = {
      reworkPoints: retrospective.value.reworkPoints || '',
      lossReasons: retrospective.value.lossReasons || '',
      improvements: retrospective.value.improvements || ''
    }
  } else {
    retroForm.value = { reworkPoints: '', lossReasons: '', improvements: '' }
  }
  showRetroPopup.value = true
}

const saveRetro = async () => {
  if (!retroForm.value.reworkPoints.trim() && !retroForm.value.lossReasons.trim() && !retroForm.value.improvements.trim()) {
    showToast('请至少填写一项复盘内容')
    return
  }
  retroSaving.value = true
  try {
    await saveRetrospective({
      workId: Number(route.params.id),
      reworkPoints: retroForm.value.reworkPoints.trim(),
      lossReasons: retroForm.value.lossReasons.trim(),
      improvements: retroForm.value.improvements.trim()
    })
    showToast('复盘记录已保存')
    showRetroPopup.value = false
    await loadRetrospective()
  } catch {
    // 错误提示已由请求拦截器处理
  } finally {
    retroSaving.value = false
  }
}

const handleDeleteRetro = () => {
  showConfirmDialog({
    title: '删除复盘记录',
    message: '确定删除该作品的复盘记录吗？删除后可在个人页的复盘列表中重新添加。'
  })
    .then(async () => {
      try {
        await deleteRetrospective(route.params.id)
        showToast('已删除复盘记录')
        retrospective.value = null
      } catch {
        // 错误提示已由请求拦截器处理
      }
    })
    .catch(() => {})
}

const loadWorkDetail = async (retryCount = 8) => {
  loading.value = true
  loadError.value = ''
  failedImages.value = new Set()
  stepFailedImages.value = new Set()
  try {
    for (let attempt = 0; attempt <= retryCount; attempt++) {
      try {
        work.value = await getWorkDetail(route.params.id, { silentError: true })
        if (!work.value) {
          loadError.value = '作品不存在或已下架'
          return
        }
        loadRetrospective()
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

.difficulty-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.difficulty-icon {
  font-size: 14px;
}

.difficulty-desc {
  font-size: 11px;
  opacity: 0.75;
  margin-left: 2px;
  font-weight: 400;
}

.difficulty-tag--beginner {
  background: #f6ffed;
  color: #389e0d;
  border: 1px solid #b7eb8f;
}

.difficulty-tag--intermediate {
  background: #fff7e6;
  color: #d46b08;
  border: 1px solid #ffd591;
}

.difficulty-tag--advanced {
  background: #fff1f0;
  color: #cf1322;
  border: 1px solid #ffa39e;
}

.work-status-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.work-status-icon {
  font-size: 14px;
}

.work-status-desc {
  font-size: 11px;
  opacity: 0.75;
  margin-left: 2px;
  font-weight: 400;
}

.work-status-tag--practice {
  background: #fafafa;
  color: #8c8c8c;
  border: 1px solid #d9d9d9;
}

.work-status-tag--finished {
  background: #f6ffed;
  color: #389e0d;
  border: 1px solid #b7eb8f;
}

.work-status-tag--repair {
  background: #fff7e6;
  color: #d46b08;
  border: 1px solid #ffd591;
}

.work-status-tag--semi_finished {
  background: #e6f7ff;
  color: #096dd9;
  border: 1px solid #91d5ff;
}

.retrospective-card {
  margin-top: 20px;
  background: linear-gradient(135deg, #f7fafa 0%, #fff 100%);
  border: 1px solid #d9e6e6;
  border-radius: 12px;
  padding: 16px;
}

.retro-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 12px;
}

.retro-icon {
  font-size: 20px;
  color: #2e7d6f;
}

.retro-title {
  flex: 1;
  font-size: 15px;
  font-weight: 600;
  color: #2e7d6f;
}

.retro-edit-btn {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  font-size: 13px;
  color: #2e7d6f;
  background: rgba(46, 125, 111, 0.1);
  padding: 4px 10px;
  border-radius: 12px;
}

.retro-item {
  margin-bottom: 12px;
}

.retro-item:last-of-type {
  margin-bottom: 0;
}

.retro-item-label {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  font-weight: 600;
  color: #8b5a2b;
  margin-bottom: 5px;
}

.retro-item-text {
  font-size: 14px;
  line-height: 1.7;
  color: #555;
  margin: 0;
  white-space: pre-wrap;
  padding-left: 4px;
  border-left: 3px solid #e8d5bf;
  padding-left: 10px;
}

.retro-improve-label {
  color: #2e7d6f;
}

.retro-improve-text {
  border-left-color: #b3d9d2;
}

.retro-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px dashed #e0d8cf;
  font-size: 12px;
  color: #aaa;
}

.retro-delete {
  color: #ee0a24;
}

.retro-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 10px 0 4px;
}

.retro-empty p {
  font-size: 13px;
  color: #b0a090;
  margin: 0;
  text-align: center;
  line-height: 1.6;
}

.retro-popup {
  padding: 20px 0 24px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.retro-popup-title {
  font-size: 17px;
  font-weight: 600;
  text-align: center;
  color: #333;
}

.retro-popup-tip {
  font-size: 12px;
  color: #999;
  text-align: center;
  margin-top: 4px;
}

.retro-form {
  margin-top: 12px;
}

.retro-popup-actions {
  margin-top: auto;
  padding: 16px;
}

.retro-popup-actions :deep(.van-button--primary) {
  background: linear-gradient(135deg, #2e7d6f, #3a9d8c);
  border: none;
}
</style>
