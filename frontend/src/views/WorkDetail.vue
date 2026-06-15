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

      <van-swipe v-if="displayImages.length" class="image-swiper" :autoplay="3000" indicator-color="#fff">
        <van-swipe-item v-for="(img, idx) in displayImages" :key="idx">
          <img :src="img" alt="" @error="onImageError(idx, 'display')" />
        </van-swipe-item>
      </van-swipe>
      <div v-else class="image-placeholder">
        <van-icon name="photo-o" />
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
          <van-tag plain type="success" v-if="work.craftTypeName">{{ work.craftTypeName }}</van-tag>
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
              class="craft-tag"
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

        <van-divider v-if="validProcessImages.length">过程记录</van-divider>
        <div class="process-images" v-if="validProcessImages.length">
          <img v-for="(img, idx) in validProcessImages" :key="idx" :src="img" alt="" class="process-img" @error="onImageError(idx, 'process')" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getWorkDetail, toggleFavorite } from '@/api'
import { useUserStore } from '@/store/user'
import { showToast } from 'vant'
import CraftStepReader from '@/components/CraftStepReader.vue'
import MaterialSummary from '@/components/MaterialSummary.vue'

const route = useRoute()
const userStore = useUserStore()
const work = ref(null)
const loading = ref(false)
const loadError = ref('')
const failedImages = ref(new Set())

const displayImages = computed(() => {
  if (!work.value) return []
  if (Array.isArray(work.value.images) && work.value.images.length) {
    return work.value.images.filter((img, idx) => !failedImages.value.has(`display-${idx}`))
  }
  if (work.value.cover && !failedImages.value.has('display-0')) {
    return [work.value.cover]
  }
  return []
})

const validProcessImages = computed(() => {
  if (!work.value || !work.value.processImages) return []
  return work.value.processImages.filter((img, idx) => !failedImages.value.has(`process-${idx}`))
})

const onImageError = (idx, type) => {
  failedImages.value.add(`${type}-${idx}`)
  failedImages.value = new Set(failedImages.value)
}

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms))

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString()
}

const craftStyleMap = {
  '封边': { icon: '🎨', class: 'craft-edge' },
  '打孔': { icon: '⚡', class: 'craft-punch' },
  '缝线': { icon: '🧵', class: 'craft-sewing' },
  '上色': { icon: '🖌️', class: 'craft-color' },
  '皮雕': { icon: '🗡️', class: 'craft-carving' },
  '塑形': { icon: '✨', class: 'craft-shaping' },
  '五金安装': { icon: '🔩', class: 'craft-hardware' },
  '裁切': { icon: '✂️', class: 'craft-cutting' },
  '编织': { icon: '🧶', class: 'craft-weaving' },
  '削薄': { icon: '📏', class: 'craft-thinning' }
}

const getCraftInfo = (name) => craftStyleMap[name] || { icon: '📌', class: 'craft-default' }
const getCraftIcon = (name) => getCraftInfo(name).icon
const getCraftTagClass = (name) => getCraftInfo(name).class

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

.image-swiper {
  width: 100%;
  aspect-ratio: 1/1;
}

.image-swiper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-placeholder {
  width: 100%;
  aspect-ratio: 1/1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f4ef;
  color: #c8aa84;
  font-size: 48px;
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

.process-images {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.process-img {
  width: 100%;
  border-radius: 8px;
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

.craft-edge {
  background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
  color: #e65100;
  border: 1px solid #ffcc80;
}

.craft-punch {
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  color: #2e7d32;
  border: 1px solid #a5d6a7;
}

.craft-sewing {
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  color: #1565c0;
  border: 1px solid #90caf9;
}

.craft-color {
  background: linear-gradient(135deg, #fce4ec 0%, #f8bbd0 100%);
  color: #c2185b;
  border: 1px solid #f48fb1;
}

.craft-carving {
  background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
  color: #00838f;
  border: 1px solid #80deea;
}

.craft-shaping {
  background: linear-gradient(135deg, #f3e5f5 0%, #e1bee7 100%);
  color: #6a1b9a;
  border: 1px solid #ce93d8;
}

.craft-hardware {
  background: linear-gradient(135deg, #fbe9e7 0%, #ffccbc 100%);
  color: #d84315;
  border: 1px solid #ffab91;
}

.craft-cutting {
  background: linear-gradient(135deg, #ffebee 0%, #ffcdd2 100%);
  color: #c62828;
  border: 1px solid #ef9a9a;
}

.craft-weaving {
  background: linear-gradient(135deg, #fffde7 0%, #fff9c4 100%);
  color: #f57f17;
  border: 1px solid #fff59d;
}

.craft-thinning {
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  color: #33691e;
  border: 1px solid #aed581;
}

.craft-default {
  background: linear-gradient(135deg, #f5f5f5 0%, #eeeeee 100%);
  color: #616161;
  border: 1px solid #e0e0e0;
}
</style>
