<template>
  <div class="work-detail" v-if="work">
    <van-nav-bar
      title="作品详情" left-arrow @click-left="$router.back()" fixed>
      <template #right>
        <van-icon name="share-o" size="20" />
      </template>
    </van-nav-bar>

    <div class="detail-content">
      <van-swipe class="image-swiper" :autoplay="3000" indicator-color="#fff">
        <van-swipe-item v-for="(img, idx) in work.images" :key="idx">
          <img :src="img" alt="" />
        </van-swipe-item>
      </van-swipe>

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

        <van-divider>作品介绍</van-divider>
        <p class="content-text">{{ work.content }}</p>

        <van-divider v-if="work.materials">用料说明</van-divider>
        <p class="content-text" v-if="work.materials">{{ work.materials }}</p>

        <van-divider v-if="work.craftSteps">制作流程</van-divider>
        <p class="content-text" v-if="work.craftSteps">{{ work.craftSteps }}</p>

        <van-divider v-if="work.processImages && work.processImages.length">过程记录</van-divider>
        <div class="process-images" v-if="work.processImages && work.processImages.length">
          <img v-for="(img, idx) in work.processImages" :key="idx" :src="img" alt="" class="process-img" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getWorkDetail, toggleFavorite } from '@/api'
import { useUserStore } from '@/store/user'
import { showToast } from 'vant'

const route = useRoute()
const userStore = useUserStore()
const work = ref(null)

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString()
}

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

onMounted(async () => {
  work.value = await getWorkDetail(route.params.id, userStore.userInfo?.id)
})
</script>

<style scoped>
.work-detail {
  min-height: 100vh;
  background: #fff;
}

.detail-content {
  padding-top: 46px;
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

.process-images {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.process-img {
  width: 100%;
  border-radius: 8px;
}
</style>
