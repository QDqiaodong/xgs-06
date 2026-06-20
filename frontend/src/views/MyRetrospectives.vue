<template>
  <div class="my-retro-page">
    <van-nav-bar title="我的复盘记录" left-arrow @click-left="$router.back()" fixed />

    <div class="page-content">
      <div v-if="!userStore.userInfo" class="empty-state">
        <van-empty description="请先登录查看" />
        <van-button type="primary" @click="$router.push('/login')">去登录</van-button>
      </div>

      <template v-else>
        <div class="summary-bar" v-if="!loading && list.length">
          <van-icon name="records-o" class="summary-icon" />
          <span class="summary-text">已沉淀 <b>{{ total }}</b> 篇复盘记录</span>
        </div>

        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
          :immediate-check="false"
        >
          <div
            v-for="item in list"
            :key="item.id"
            class="retro-card"
            @click="goDetail(item.workId)"
          >
            <div class="retro-card-head">
              <div class="retro-cover">
                <van-image
                  width="72"
                  height="72"
                  radius="8"
                  fit="cover"
                  :src="item.workCover || 'https://img.yzcdn.cn/vant/cat.jpeg'"
                />
                <span class="offline-badge" v-if="item.workStatus === 0">已下架</span>
              </div>
              <div class="retro-card-info">
                <div class="retro-card-title">{{ item.workTitle || '未命名作品' }}</div>
                <div class="retro-card-meta">
                  <van-tag plain type="primary" v-if="item.categoryName">{{ item.categoryName }}</van-tag>
                  <span class="retro-card-time">{{ formatTime(item.updateTime) }}</span>
                </div>
              </div>
              <van-icon name="arrow" class="retro-card-arrow" />
            </div>

            <div class="retro-card-body">
              <div class="retro-field" v-if="item.reworkPoints">
                <span class="retro-field-label rework"><van-icon name="warning-o" /> 返工点</span>
                <p class="retro-field-text">{{ item.reworkPoints }}</p>
              </div>
              <div class="retro-field" v-if="item.lossReasons">
                <span class="retro-field-label loss"><van-icon name="info-o" /> 损耗原因</span>
                <p class="retro-field-text">{{ item.lossReasons }}</p>
              </div>
              <div class="retro-field" v-if="item.improvements">
                <span class="retro-field-label improve"><van-icon name="bulb-o" /> 下次改进</span>
                <p class="retro-field-text">{{ item.improvements }}</p>
              </div>
            </div>
          </div>
        </van-list>

        <van-empty
          v-if="!loading && !list.length && loaded"
          image="search"
          description="还没有复盘记录"
        >
          <template #description>
            <p>在已完成作品详情页点击「添加复盘」</p>
            <p>记录返工点、损耗原因与改进方向</p>
          </template>
          <van-button round type="primary" size="small" @click="$router.push('/my-works')">查看我的作品</van-button>
        </van-empty>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getMyRetrospectives } from '@/api'

const router = useRouter()
const userStore = useUserStore()

const list = ref([])
const loading = ref(false)
const finished = ref(false)
const loaded = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = 10

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  if (isNaN(date.getTime())) return ''
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const goDetail = (workId) => {
  if (workId) {
    router.push(`/work/${workId}`)
  }
}

const onLoad = async () => {
  loading.value = true
  try {
    const res = await getMyRetrospectives({ page: page.value, size: pageSize })
    const records = res?.records || []
    list.value.push(...records)
    total.value = res?.total || 0
    finished.value = !res || list.value.length >= (res.total || 0)
    if (!finished.value) {
      page.value += 1
    }
  } catch {
    finished.value = true
  } finally {
    loading.value = false
    loaded.value = true
  }
}

if (userStore.userInfo) {
  onLoad()
} else {
  loaded.value = true
}
</script>

<style scoped>
.my-retro-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-content {
  padding-top: 46px;
  padding-bottom: 20px;
}

.empty-state {
  padding: 60px 20px;
  text-align: center;
}

.summary-bar {
  display: flex;
  align-items: center;
  gap: 6px;
  margin: 12px;
  padding: 10px 14px;
  background: linear-gradient(135deg, #fdf8f0 0%, #f5e6d3 100%);
  border-radius: 10px;
  border: 1px solid #e8d5bf;
  font-size: 13px;
  color: #8b6914;
}

.summary-icon {
  font-size: 16px;
  color: #c08457;
}

.summary-text b {
  color: #c08457;
  font-size: 15px;
}

.retro-card {
  margin: 0 12px 12px;
  background: #fff;
  border-radius: 12px;
  padding: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.retro-card-head {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.retro-cover {
  position: relative;
  flex-shrink: 0;
}

.offline-badge {
  position: absolute;
  bottom: 4px;
  left: 4px;
  font-size: 10px;
  color: #fff;
  background: rgba(0, 0, 0, 0.55);
  padding: 1px 5px;
  border-radius: 4px;
}

.retro-card-info {
  flex: 1;
  min-width: 0;
}

.retro-card-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.retro-card-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.retro-card-time {
  font-size: 12px;
  color: #aaa;
}

.retro-card-arrow {
  color: #c8c8c8;
  font-size: 16px;
}

.retro-card-body {
  padding-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.retro-field-label {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 4px;
}

.retro-field-label.rework {
  color: #d46b08;
}

.retro-field-label.loss {
  color: #cf1322;
}

.retro-field-label.improve {
  color: #2e7d6f;
}

.retro-field-text {
  font-size: 13px;
  line-height: 1.6;
  color: #666;
  margin: 0;
  padding-left: 8px;
  border-left: 2px solid #f0e6d8;
  white-space: pre-wrap;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
